package com.example.manger.service;

import com.example.manger.dto.CodeExecutionRequest;
import com.example.manger.dto.CodeExecutionResponse;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 代码执行服务（使用 Docker）
 */
@Service
@Slf4j
public class CodeExecutionService {

    @Value("${docker.execution.timeout:5}")
    private int defaultTimeoutSeconds;

    @Value("${docker.execution.memory:256m}")
    private String memoryLimit;

    @Value("${docker.execution.cpus:0.5}")
    private double cpuLimit;

    @Value("${docker.execution.host:unix:///var/run/docker.sock}")
    private String dockerHost;

    /**
     * Docker 客户端
     */
    private DockerClient dockerClient;

    /**
     * 初始化 Docker 客户端（延迟初始化）
     */
    private synchronized DockerClient getDockerClient() {
        if (dockerClient == null) {
            try {
                DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                        .withDockerHost(dockerHost)
                        .build();

                ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                        .dockerHost(config.getDockerHost())
                        .maxConnections(100)
                        .connectionTimeout(Duration.ofSeconds(30))
                        .responseTimeout(Duration.ofSeconds(45))
                        .build();

                dockerClient = DockerClientImpl.getInstance(config, httpClient);
                
                log.info("Docker client initialized successfully, host: {}", dockerHost);
            } catch (Exception e) {
                log.error("Docker client initialization failed, code execution will not work: {}", e.getMessage(), e);
            }
        }
        return dockerClient;
    }

    /**
     * 执行代码
     */
    public CodeExecutionResponse executeCode(CodeExecutionRequest request) {
        DockerClient client = getDockerClient();
        if (client == null) {
            return CodeExecutionResponse.builder()
                    .success(false)
                    .error("Docker 未配置，无法执行代码。请确保 Docker 已安装并运行，并检查配置中的 docker.execution.host")
                    .build();
        }

        long startTime = System.currentTimeMillis();
        
        try {
            String language = request.getLanguage().toUpperCase();
            String code = request.getCode();
            String input = request.getInput() != null ? request.getInput() : "";
            int timeout = request.getTimeoutSeconds() != null ? request.getTimeoutSeconds() : defaultTimeoutSeconds;

            // 根据语言选择合适的 Docker 镜像和执行命令
            String image = getImageForLanguage(language);
            List<String> commands = buildExecutionCommands(language, code, input);

            log.info("Executing {} code in Docker container, timeout: {}s", language, timeout);

            // 创建临时容器
            String containerId = createContainer(client, image, commands, timeout);
            
            try {
                // 启动容器
                client.startContainerCmd(containerId).exec();

                // 等待容器执行完成
                CompletableFuture<ContainerExitCode> exitCodeFuture = waitForContainer(client, containerId, timeout);
                ContainerExitCode exitCode = exitCodeFuture.get(timeout + 2, TimeUnit.SECONDS);

                // 立即获取容器输出（容器设置了 autoRemove，必须在删除前获取）
                // 不延迟，容器停止后立即获取日志
                String output = "";
                String error = null;
                try {
                    output = getAllContainerLogs(client, containerId);
                    if (exitCode.getCode() != 0) {
                        error = getContainerErrors(client, containerId);
                    }
                } catch (Exception e) {
                    log.warn("Failed to get container logs (container may have been auto-removed): {}", e.getMessage());
                    // 如果容器已被删除，尝试从错误信息中获取
                    if (exitCode.getCode() != 0) {
                        error = "执行失败，退出码: " + exitCode.getCode();
                    }
                }
                
                // 记录调试信息
                log.debug("Container {} exit code: {}, output length: {}", containerId, exitCode.getCode(), output.length());

                long executionTime = System.currentTimeMillis() - startTime;

                return CodeExecutionResponse.builder()
                        .success(exitCode.getCode() == 0)
                        .output(output)
                        .error(error)
                        .executionTimeMs(executionTime)
                        .exitCode(exitCode.getCode())
                        .build();

            } finally {
                // 删除容器
                try {
                    client.removeContainerCmd(containerId)
                            .withForce(true)
                            .exec();
                } catch (Exception e) {
                    log.warn("Failed to remove container {}: {}", containerId, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Code execution failed", e);
            long executionTime = System.currentTimeMillis() - startTime;
            
            return CodeExecutionResponse.builder()
                    .success(false)
                    .error("执行失败: " + e.getMessage())
                    .executionTimeMs(executionTime)
                    .exitCode(-1)
                    .build();
        }
    }

    /**
     * 根据语言获取 Docker 镜像
     */
    private String getImageForLanguage(String language) {
        return switch (language) {
            case "JAVA" -> "openjdk:17-jdk-slim";  // Java 17
            case "PYTHON" -> "python:3.11-slim";   // Python 3.11
            case "CPP", "C++" -> "gcc:latest";     // GCC for C++
            default -> throw new IllegalArgumentException("不支持的语言: " + language);
        };
    }

    /**
     * 构建执行命令
     */
    private List<String> buildExecutionCommands(String language, String code, String input) {
        List<String> commands = new ArrayList<>();
        
        switch (language.toUpperCase()) {
            case "JAVA":
                // Java: 提取类名，创建临时文件、编译、运行
                String className = extractJavaClassName(code);
                commands.add("/bin/sh");
                commands.add("-c");
                String javaScript;
                if (input != null && !input.trim().isEmpty()) {
                    javaScript = String.format(
                        "echo '%s' > /tmp/%s.java && " +
                        "javac /tmp/%s.java && " +
                        "cd /tmp && " +
                        "echo '%s' | timeout %d java %s",
                        escapeCode(code),
                        className,
                        className,
                        escapeInput(input),
                        defaultTimeoutSeconds,
                        className
                    );
                } else {
                    javaScript = String.format(
                        "echo '%s' > /tmp/%s.java && " +
                        "javac /tmp/%s.java && " +
                        "cd /tmp && " +
                        "timeout %d java %s",
                        escapeCode(code),
                        className,
                        className,
                        defaultTimeoutSeconds,
                        className
                    );
                }
                commands.add(javaScript);
                break;
                
            case "PYTHON":
                // Python: 直接执行代码（使用 -u 参数禁用缓冲，确保输出实时）
                // 使用 python3 -u 直接执行，不使用 timeout 命令（超时由 Docker 控制）
                commands.add("/bin/sh");
                commands.add("-c");
                String pythonScript;
                if (input != null && !input.trim().isEmpty()) {
                    pythonScript = String.format(
                        "cd /tmp && " +
                        "echo '%s' > solution.py && " +
                        "echo '%s' | python3 -u solution.py",
                        escapeCode(code),
                        escapeInput(input)
                    );
                } else {
                    pythonScript = String.format(
                        "cd /tmp && " +
                        "echo '%s' > solution.py && " +
                        "python3 -u solution.py",
                        escapeCode(code)
                    );
                }
                commands.add(pythonScript);
                break;
                
            case "CPP", "C++":
                // C++: 编译并运行
                commands.add("/bin/sh");
                commands.add("-c");
                String cppScript = String.format(
                    "echo '%s' > /tmp/solution.cpp && " +
                    "g++ -o /tmp/solution /tmp/solution.cpp && " +
                    "cd /tmp && " +
                    "echo '%s' | timeout %d ./solution",
                    escapeCode(code),
                    escapeInput(input),
                    defaultTimeoutSeconds
                );
                commands.add(cppScript);
                break;
                
            default:
                throw new IllegalArgumentException("不支持的语言: " + language);
        }
        
        return commands;
    }

    /**
     * 从 Java 代码中提取类名
     */
    private String extractJavaClassName(String code) {
        // 匹配 "public class ClassName" 或 "class ClassName"
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "(?:public\\s+)?class\\s+([A-Za-z_][A-Za-z0-9_]*)"
        );
        java.util.regex.Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // 如果没有找到，默认使用 Solution
        return "Solution";
    }

    /**
     * 转义代码中的特殊字符
     */
    private String escapeCode(String code) {
        return code.replace("'", "'\"'\"'")
                   .replace("\\", "\\\\")
                   .replace("$", "\\$")
                   .replace("`", "\\`");
    }

    /**
     * 转义输入中的特殊字符
     */
    private String escapeInput(String input) {
        return input.replace("'", "'\"'\"'")
                   .replace("\\", "\\\\")
                   .replace("$", "\\$")
                   .replace("`", "\\`");
    }

    /**
     * 创建 Docker 容器（带资源限制）
     */
    private String createContainer(DockerClient client, String image, List<String> commands, int timeout) {
        // CPU 限制（单位：微秒）
        // CPU period: 100000 微秒（100 毫秒）
        // CPU quota: period * cpuLimit（例如：0.5 核 = 50000 微秒）
        Long cpuPeriod = 100000L;  // 100 毫秒（微秒单位）
        Long cpuQuota = (long) (cpuPeriod * cpuLimit);  // 0.5 核 = 50000 微秒
        
        // 内存限制（256MB = 268435456 字节）
        Long memoryBytes = parseMemoryLimit(memoryLimit);

        CreateContainerResponse container = client.createContainerCmd(image)
                .withCmd(commands)
                .withTty(false)
                .withStdinOpen(false)
                .withNetworkDisabled(true)  // 禁用网络访问
                .withHostConfig(HostConfig.newHostConfig()
                        .withMemory(memoryBytes)
                        .withCpuQuota(cpuQuota)      // CPU quota（微秒）
                        .withCpuPeriod(cpuPeriod)    // CPU period（微秒）
                        .withNetworkMode("none")      // 无网络
                        .withReadonlyRootfs(false)    // 允许写入临时文件
                        .withAutoRemove(false)        // 不自动删除，手动控制删除（确保能获取日志）
                        .withRestartPolicy(RestartPolicy.noRestart())  // 不使用重启策略
                )
                .exec();

        return container.getId();
    }

    /**
     * 等待容器执行完成并获取日志
     */
    private CompletableFuture<ContainerExitCode> waitForContainer(DockerClient client, String containerId, int timeout) {
        CompletableFuture<ContainerExitCode> future = new CompletableFuture<>();
        
        // 启动线程等待容器完成
        Thread waitThread = new Thread(() -> {
            try {
                WaitResponseCallback callback = new WaitResponseCallback();
                client.waitContainerCmd(containerId).exec(callback);
                Integer exitCode = callback.awaitStatusCode();
                // 容器停止后立即获取日志（在 autoRemove 删除容器之前）
                future.complete(new ContainerExitCode(exitCode != null ? exitCode : -1));
            } catch (Exception e) {
                log.error("Error waiting for container: {}", e.getMessage());
                future.complete(new ContainerExitCode(-1));
            }
        });
        waitThread.start();
        
        // 设置超时任务
        new Thread(() -> {
            try {
                Thread.sleep((timeout + 2) * 1000L);
                if (!future.isDone()) {
                    // 强制停止容器
                    try {
                        client.stopContainerCmd(containerId)
                                .withTimeout(1)
                                .exec();
                    } catch (Exception e) {
                        log.warn("Failed to stop container: {}", e.getMessage());
                    }
                    future.complete(new ContainerExitCode(124)); // timeout exit code
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return future;
    }

    /**
     * 获取容器日志（输出）
     */
    private String getContainerLogs(DockerClient client, String containerId) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            client.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(false)
                    .exec(new LogContainerResultCallback(outputStream))
                    .awaitCompletion();
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to get container logs: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 获取容器错误日志
     */
    private String getContainerErrors(DockerClient client, String containerId) {
        try {
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            client.logContainerCmd(containerId)
                    .withStdOut(false)
                    .withStdErr(true)
                    .exec(new LogContainerResultCallback(errorStream))
                    .awaitCompletion();
            return errorStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to get container errors: {}", e.getMessage());
            return "执行错误";
        }
    }

    /**
     * 获取容器所有日志（stdout + stderr）
     */
    private String getAllContainerLogs(DockerClient client, String containerId) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            client.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new LogContainerResultCallback(outputStream))
                    .awaitCompletion();
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to get all container logs: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 解析内存限制字符串（如 "256m" -> 字节数）
     */
    private Long parseMemoryLimit(String memoryLimit) {
        memoryLimit = memoryLimit.toLowerCase().trim();
        long multiplier = 1;
        
        if (memoryLimit.endsWith("k")) {
            multiplier = 1024;
            memoryLimit = memoryLimit.substring(0, memoryLimit.length() - 1);
        } else if (memoryLimit.endsWith("m")) {
            multiplier = 1024 * 1024;
            memoryLimit = memoryLimit.substring(0, memoryLimit.length() - 1);
        } else if (memoryLimit.endsWith("g")) {
            multiplier = 1024 * 1024 * 1024;
            memoryLimit = memoryLimit.substring(0, memoryLimit.length() - 1);
        }
        
        return Long.parseLong(memoryLimit) * multiplier;
    }

    /**
     * 容器退出码包装类
     */
    private static class ContainerExitCode {
        private final int code;
        
        public ContainerExitCode(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
    }

    /**
     * 日志容器结果回调
     */
    private static class LogContainerResultCallback extends ResultCallback.Adapter<Frame> {
        private final ByteArrayOutputStream outputStream;
        
        public LogContainerResultCallback(ByteArrayOutputStream outputStream) {
            this.outputStream = outputStream;
        }
        
        @Override
        public void onNext(Frame frame) {
            try {
                if (frame.getPayload() != null) {
                    outputStream.write(frame.getPayload());
                }
            } catch (Exception e) {
                // 忽略写入错误
            }
        }
    }

    /**
     * 等待容器响应回调
     */
    private static class WaitResponseCallback extends ResultCallback.Adapter<WaitResponse> {
        private Integer exitCode;
        private final Object lock = new Object();
        
        @Override
        public void onNext(WaitResponse waitResponse) {
            synchronized (lock) {
                this.exitCode = waitResponse.getStatusCode();
                lock.notifyAll();
            }
        }
        
        @Override
        public void onError(Throwable throwable) {
            synchronized (lock) {
                this.exitCode = -1;
                lock.notifyAll();
            }
        }
        
        public Integer awaitStatusCode() {
            synchronized (lock) {
                if (exitCode == null) {
                    try {
                        lock.wait(30000); // 最多等待 30 秒
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return -1;
                    }
                }
            }
            return exitCode != null ? exitCode : -1;
        }
    }
}


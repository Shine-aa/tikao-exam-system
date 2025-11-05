package com.example.manger.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 代码执行请求
 */
@Data
public class CodeExecutionRequest {
    /**
     * 编程语言：JAVA, PYTHON, CPP, C
     */
    @NotBlank(message = "编程语言不能为空")
    private String language;
    
    /**
     * 代码内容
     */
    @NotBlank(message = "代码不能为空")
    private String code;
    
    /**
     * 输入数据（可选）
     */
    private String input;
    
    /**
     * 执行超时时间（秒），默认 5 秒
     */
    private Integer timeoutSeconds = 5;
}


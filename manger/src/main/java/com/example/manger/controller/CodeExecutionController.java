package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.CodeExecutionRequest;
import com.example.manger.dto.CodeExecutionResponse;
import com.example.manger.service.CodeExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 代码执行控制器
 */
@RestController
@RequestMapping("/api/code")
@RequiredArgsConstructor
@Tag(name = "代码执行", description = "代码执行相关接口（使用 Docker）")
@CrossOrigin
public class CodeExecutionController {

    private final CodeExecutionService codeExecutionService;

    @PostMapping("/execute")
    @Operation(summary = "执行代码", description = "在 Docker 容器中安全执行代码")
    // 暂时允许匿名访问以便测试，生产环境需要添加 @PreAuthorize("hasRole('USER')")
    public ApiResponse<CodeExecutionResponse> executeCode(
            @Valid @RequestBody CodeExecutionRequest request) {
        try {
            // 验证语言支持
            String language = request.getLanguage().toUpperCase();
            if (!isLanguageSupported(language)) {
                return ApiResponse.error("不支持的语言: " + language + "，支持的语言: JAVA, PYTHON, CPP, C");
            }
            
            // 执行代码
            CodeExecutionResponse response = codeExecutionService.executeCode(request);
            return ApiResponse.success("执行完成", response);
        } catch (Exception e) {
            return ApiResponse.error("执行失败: " + e.getMessage());
        }
    }

    /**
     * 检查语言是否支持
     */
    private boolean isLanguageSupported(String language) {
        return "JAVA".equals(language) || 
               "PYTHON".equals(language) || 
               "CPP".equals(language) || 
               "C++".equals(language) ||
               "C".equals(language);
    }
}


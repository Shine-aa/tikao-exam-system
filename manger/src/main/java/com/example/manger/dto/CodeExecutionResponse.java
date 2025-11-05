package com.example.manger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代码执行响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResponse {
    /**
     * 是否执行成功
     */
    private Boolean success;
    
    /**
     * 执行输出
     */
    private String output;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 执行时间（毫秒）
     */
    private Long executionTimeMs;
    
    /**
     * 退出码
     */
    private Integer exitCode;
}


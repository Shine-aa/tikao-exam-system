package com.example.manger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 专业请求DTO
 */
@Data
public class MajorRequest {
    
    @NotBlank(message = "专业代码不能为空")
    @Size(max = 20, message = "专业代码长度不能超过20个字符")
    private String majorCode;
    
    @NotBlank(message = "专业名称不能为空")
    @Size(max = 100, message = "专业名称长度不能超过100个字符")
    private String majorName;
    
    @Size(max = 500, message = "专业描述长度不能超过500个字符")
    private String description;
}

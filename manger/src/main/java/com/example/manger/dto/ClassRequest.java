package com.example.manger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 班级请求DTO
 */
@Data
public class ClassRequest {
    
    @NotBlank(message = "班级代码不能为空")
    @Size(max = 50, message = "班级代码长度不能超过50个字符")
    private String classCode;
    
    @NotBlank(message = "班级名称不能为空")
    @Size(max = 100, message = "班级名称长度不能超过100个字符")
    private String className;
    
    @NotBlank(message = "年级不能为空")
    @Size(max = 10, message = "年级长度不能超过10个字符")
    private String grade;
    
    @NotNull(message = "专业ID不能为空")
    private Long majorId;
    
    @NotNull(message = "班级序号不能为空")
    @Min(value = 1, message = "班级序号必须大于0")
    private Integer classNumber;
    
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;
    
    private String description;
    
    @Min(value = 1, message = "最大学生数必须大于0")
    private Integer maxStudents = 50;
    
    private Boolean isActive = true;
}

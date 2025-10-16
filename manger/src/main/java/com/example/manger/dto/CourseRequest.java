package com.example.manger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程请求DTO
 */
@Data
public class CourseRequest {
    
    @NotBlank(message = "课程代码不能为空")
    @Size(max = 50, message = "课程代码长度不能超过50个字符")
    private String courseCode;
    
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    private String courseName;
    
    private String description;
    
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;
    
    @Size(max = 20, message = "学期长度不能超过20个字符")
    private String semester;
    
    @Size(max = 10, message = "学年长度不能超过10个字符")
    private String academicYear;
    
    private Boolean isActive = true;
}

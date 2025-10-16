package com.example.manger.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 班级课程关联请求DTO
 */
@Data
public class ClassCourseRequest {
    
    @NotNull(message = "班级ID不能为空")
    private Long classId;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @Size(max = 20, message = "学期长度不能超过20个字符")
    private String semester;
    
    @Size(max = 20, message = "学年长度不能超过20个字符")
    private String academicYear;
}

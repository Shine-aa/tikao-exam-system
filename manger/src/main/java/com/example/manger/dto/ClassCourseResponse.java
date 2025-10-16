package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级课程关联响应DTO
 */
@Data
public class ClassCourseResponse {
    
    private Long id;
    
    private Long classId;
    
    private Long courseId;
    
    private String semester;
    
    private String academicYear;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    // 扩展字段，用于显示班级和课程信息
    private String className;
    
    private String classCode;
    
    private String courseName;
    
    private String courseCode;
    
    private Integer credits;
}

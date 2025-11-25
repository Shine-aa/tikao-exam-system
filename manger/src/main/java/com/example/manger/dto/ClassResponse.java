package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级响应DTO
 */
@Data
public class ClassResponse {
    
    private Long id;
    private String classCode;
    private String className;
    private String grade;
    private Long majorId;
    private String majorName; // 专业名称
    private String majorCode; // 专业代码
    private Integer classNumber;
    private Long teacherId;
    private String teacherName; // 教师姓名
    private String description;
    private Integer maxStudents;
    private Integer currentStudents;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

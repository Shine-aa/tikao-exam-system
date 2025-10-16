package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程响应DTO
 */
@Data
public class CourseResponse {
    
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private Long teacherId;
    private String teacherName; // 教师姓名
    private String semester;
    private String academicYear;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long classCount; // 班级数量
}

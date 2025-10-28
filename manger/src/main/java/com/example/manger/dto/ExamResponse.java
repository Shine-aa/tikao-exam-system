package com.example.manger.dto;

import lombok.Data;
import com.example.manger.entity.Exam;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试响应DTO
 */
@Data
public class ExamResponse {
    private Long id;
    private String examCode;
    private String examName;
    private String description;
    private Long paperId;
    private String paperName;
    private String paperCode;
    private Long classId;
    private String className;
    private String classCode;
    private Long teacherId;
    private String teacherName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private Boolean isRandomOrder;
    private Boolean isRandomOptions;
    private Boolean allowReview;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 试卷信息
    private Integer totalQuestions;
    private Integer totalPoints;
    
    // 班级学生数量
    private Integer studentCount;
    
    // 已参加考试的学生数量
    private Integer participatedCount;
    
    // 未交卷的学生数量
    private Integer unsubmittedCount;
    
    // 已判卷的学生数量
    private Integer gradedCount;
    
    // 判卷进度（百分比）
    private Integer gradingProgress;
    
    // 考试时长（分钟）
    private Integer duration;
    
    // 学生考试分数
    private Double totalScore;
    
    // 学生考试状态
    private String studentExamStatus;
}

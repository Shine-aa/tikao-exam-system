package com.example.manger.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 判卷结果实体
 */
@Entity
@Table(name = "grading_results")
public class GradingResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "student_exam_id", nullable = false)
    private Long studentExamId;
    
    @Column(name = "exam_name", nullable = false, length = 255)
    private String examName;
    
    @Column(name = "student_name", nullable = false, length = 100)
    private String studentName;
    
    @Column(name = "exam_start_time", nullable = false)
    private LocalDateTime examStartTime;
    
    @Column(name = "grading_data", columnDefinition = "JSON", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> gradingData;
    
    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore = BigDecimal.ZERO;
    
    @Column(name = "is_grading_completed")
    private Boolean isGradingCompleted = false;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    @Column(name = "graded_by")
    private Long gradedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getStudentExamId() { return studentExamId; }
    public void setStudentExamId(Long studentExamId) { this.studentExamId = studentExamId; }
    
    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public LocalDateTime getExamStartTime() { return examStartTime; }
    public void setExamStartTime(LocalDateTime examStartTime) { this.examStartTime = examStartTime; }
    
    public Map<String, Object> getGradingData() { return gradingData; }
    public void setGradingData(Map<String, Object> gradingData) { this.gradingData = gradingData; }
    
    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    
    public Boolean getIsGradingCompleted() { return isGradingCompleted; }
    public void setIsGradingCompleted(Boolean isGradingCompleted) { this.isGradingCompleted = isGradingCompleted; }
    
    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }
    
    public Long getGradedBy() { return gradedBy; }
    public void setGradedBy(Long gradedBy) { this.gradedBy = gradedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
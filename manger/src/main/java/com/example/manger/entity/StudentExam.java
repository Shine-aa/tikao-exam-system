package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生考试记录实体类
 */
@Entity
@Table(name = "student_exams")
@Data
public class StudentExam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "submit_time")
    private LocalDateTime submitTime;
    
    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExamStatus status = ExamStatus.NOT_STARTED;
    
    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @OneToMany(mappedBy = "studentExamId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentAnswer> studentAnswers;
    
    public enum ExamStatus {
        NOT_STARTED("未开始"),
        IN_PROGRESS("进行中"),
        SUBMITTED("已提交"),
        GRADED("已评分");
        
        private final String description;
        
        ExamStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

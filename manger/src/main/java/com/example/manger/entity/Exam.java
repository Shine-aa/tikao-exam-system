package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试实体类
 */
@Entity
@Table(name = "exams")
@Data
public class Exam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_code", unique = true, nullable = false, length = 50)
    private String examCode;
    
    @Column(name = "exam_name", nullable = false, length = 200)
    private String examName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "paper_id", nullable = false)
    private Long paperId;
    
    @Column(name = "class_id", nullable = false)
    private Long classId;
    
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(name = "max_attempts")
    private Integer maxAttempts = 1;
    
    @Column(name = "is_random_order")
    private Boolean isRandomOrder = true;
    
    @Column(name = "is_random_options")
    private Boolean isRandomOptions = true;
    
    @Column(name = "allow_review")
    private Boolean allowReview = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExamStatus status = ExamStatus.DRAFT;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @OneToMany(mappedBy = "examId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentExam> studentExams;
    
    public enum ExamStatus {
        DRAFT("草稿"),
        SCHEDULED("已安排"),
        ONGOING("进行中"),
        COMPLETED("已完成"),
        CANCELLED("已取消");
        
        private final String description;
        
        ExamStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

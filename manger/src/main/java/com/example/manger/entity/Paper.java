package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷实体类
 */
@Entity
@Table(name = "papers")
@Data
public class Paper {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "paper_code", unique = true, nullable = false, length = 50)
    private String paperCode;
    
    @Column(name = "paper_name", nullable = false, length = 200)
    private String paperName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "class_id", nullable = false)
    private Long classId;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;
    
    @Column(name = "total_questions")
    private Integer totalQuestions = 0;
    
    @Column(name = "total_points")
    private Integer totalPoints = 0;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes = 120;
    
    @Column(name = "difficulty_distribution", columnDefinition = "JSON")
    private String difficultyDistribution;
    
    @Column(name = "question_type_distribution", columnDefinition = "JSON")
    private String questionTypeDistribution;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @OneToMany(mappedBy = "paperId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaperQuestion> paperQuestions;
}

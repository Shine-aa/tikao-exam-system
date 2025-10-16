package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生答题记录实体类
 */
@Entity
@Table(name = "student_answers")
@Data
public class StudentAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_exam_id", nullable = false)
    private Long studentExamId;
    
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @Column(name = "answer_content", columnDefinition = "TEXT")
    private String answerContent;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;
    
    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_exam_id", insertable = false, updatable = false)
    private StudentExam studentExam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
}

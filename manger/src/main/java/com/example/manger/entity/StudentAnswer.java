package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 学生答题记录实体类（整张试卷）
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
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_content", columnDefinition = "JSON")
    private Map<String, Object> answerContent;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "paper_content", columnDefinition = "JSON")
    private Map<String, Object> paperContent;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;
    
    @Column(name = "is_graded")
    private Boolean isGraded = false;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    @Column(name = "graded_by")
    private Long gradedBy;
    
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
}

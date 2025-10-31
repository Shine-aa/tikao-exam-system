package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 题目知识点关联实体类
 * 实现题目和知识点的多对多关系
 */
@Entity
@Table(name = "question_knowledge_points")
@Data
public class QuestionKnowledgePoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @Column(name = "knowledge_point_id", nullable = false)
    private Long knowledgePointId;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_point_id", insertable = false, updatable = false)
    private KnowledgePoint knowledgePoint;
}


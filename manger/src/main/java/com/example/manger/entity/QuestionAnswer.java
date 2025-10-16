package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_answers")
@Data
public class QuestionAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @Column(name = "answer_content", columnDefinition = "TEXT", nullable = false)
    private String answerContent;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "answer_type")
    private AnswerType answerType = AnswerType.TEXT;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = true;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum AnswerType {
        TEXT("文本"),
        NUMBER("数字"),
        FORMULA("公式");
        
        private final String description;
        
        AnswerType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "questions")
@Data
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    // 题目图片（TEXT格式，多个路径用分号(;)分隔）
    @Column(name = "images", columnDefinition = "TEXT")
    private String images;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private QuestionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private DifficultyLevel difficulty = DifficultyLevel.MEDIUM;
    
    @Column(name = "points")
    private Integer points = 1;
    
    // 题目选项（JSON格式存储）
    @Column(name = "options", columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Map<String, Object>> options;
    
    // 正确答案
    @Column(name = "correct_answer", length = 1000)
    private String correctAnswer;
    
    // 编程语言（仅程序题使用）：JAVA, PYTHON, CPP, C
    @Column(name = "programming_language", length = 20)
    private String programmingLanguage;
    
    // 测试用例（仅程序题使用，JSON格式存储）
    // 格式：[{"input": "1\n2", "output": "3"}, {"input": "5\n7", "output": "12"}]
    @Column(name = "test_cases", columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Map<String, Object>> testCases;
    
    // knowledge_point_id 字段已删除，改用 question_knowledge_points 关联表
    // @Column(name = "knowledge_point_id")
    // private Long knowledgePointId;
    
    @Column(name = "tags", length = 500)
    private String tags;
    
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum QuestionType {
        SINGLE_CHOICE("单选题"),
        MULTIPLE_CHOICE("多选题"),
        TRUE_FALSE("判断题"),
        FILL_BLANK("填空题"),
        SUBJECTIVE("主观题"),
        PROGRAMMING("程序题");
        
        private final String description;
        
        QuestionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum DifficultyLevel {
        EASY("简单"),
        MEDIUM("中等"),
        HARD("困难");
        
        private final String description;
        
        DifficultyLevel(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

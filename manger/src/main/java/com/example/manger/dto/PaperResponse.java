package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 试卷响应DTO
 */
@Data
public class PaperResponse {
    
    private Long id;
    private String paperCode;
    private String paperName;
    private String description;
    private Long classId;
    private String className;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long teacherId;
    private String teacherName;
    private Integer totalQuestions;
    private Integer totalPoints;
    private Integer durationMinutes;
    private Map<String, Integer> difficultyDistribution;
    private Map<String, Integer> questionTypeDistribution;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 试卷题目列表
    private List<PaperQuestionResponse> questions;
    
    @Data
    public static class PaperQuestionResponse {
        private Long questionId;
        private String questionTitle;
        private String questionContent;
        private String questionType;
        private String difficulty;
        private Integer points;
        private Integer questionOrder;
        private String tags;
        private String explanation;
        
        // 题目选项
        private List<QuestionOptionResponse> options;
        
        // 题目答案
        private List<QuestionAnswerResponse> answers;
        
        @Data
        public static class QuestionOptionResponse {
            private Long id;
            private String optionKey;
            private String optionContent;
            private Boolean isCorrect;
            private Integer sortOrder;
        }
        
        @Data
        public static class QuestionAnswerResponse {
            private Long id;
            private String answerContent;
            private String answerType;
            private Boolean isPrimary;
            private Integer sortOrder;
        }
    }
}

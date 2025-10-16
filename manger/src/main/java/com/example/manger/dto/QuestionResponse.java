package com.example.manger.dto;

import com.example.manger.entity.Question;
import com.example.manger.entity.QuestionAnswer;
import com.example.manger.entity.QuestionOption;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionResponse {
    
    private Long id;
    private String title;
    private String content;
    private Question.QuestionType type;
    private String typeDescription;
    private Question.DifficultyLevel difficulty;
    private String difficultyDescription;
    private Integer points;
    private Long knowledgePointId;
    private String knowledgePointName;
    private String tags;
    private String explanation;
    private Boolean isActive;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 选择题选项
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
        private QuestionAnswer.AnswerType answerType;
        private String answerTypeDescription;
        private Boolean isPrimary;
        private Integer sortOrder;
    }
}

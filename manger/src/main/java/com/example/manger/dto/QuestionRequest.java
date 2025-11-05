package com.example.manger.dto;

import com.example.manger.entity.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    
    @NotBlank(message = "题目内容不能为空")
    private String content;
    
    private String title;
    
    private String images; // 题目图片路径，多个路径用分号(;)分隔
    
    @NotNull(message = "题目类型不能为空")
    private Question.QuestionType type;
    
    private Question.DifficultyLevel difficulty = Question.DifficultyLevel.MEDIUM;
    
    @Positive(message = "题目分值必须大于0")
    private Integer points = 1;
    
    private Long knowledgePointId;
    
    private String tags;
    
    private String explanation;
    
    // 编程语言（仅程序题使用）：JAVA, PYTHON, CPP, C
    private String programmingLanguage;
    
    // 选择题选项
    private List<QuestionOptionRequest> options;
    
    // 题目答案
    private List<QuestionAnswerRequest> answers;
    
    @Data
    public static class QuestionOptionRequest {
        @NotBlank(message = "选项标识不能为空")
        private String optionKey;
        
        @NotBlank(message = "选项内容不能为空")
        private String optionContent;
        
        private Boolean isCorrect = false;
        
        private Integer sortOrder = 0;
    }
    
    @Data
    public static class QuestionAnswerRequest {
        @NotBlank(message = "答案内容不能为空")
        private String answerContent;
    }
}

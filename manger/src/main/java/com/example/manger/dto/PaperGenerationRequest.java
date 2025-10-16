package com.example.manger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.util.Map;

/**
 * 试卷生成请求DTO
 */
@Data
public class PaperGenerationRequest {
    
    @NotBlank(message = "试卷名称不能为空")
    private String paperName;
    
    private String description;
    
    @NotNull(message = "班级ID不能为空")
    private Long classId;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotNull(message = "考试时长不能为空")
    @Min(value = 30, message = "考试时长不能少于30分钟")
    @Max(value = 300, message = "考试时长不能超过300分钟")
    private Integer durationMinutes = 120;
    
    @NotNull(message = "总题目数不能为空")
    @Min(value = 1, message = "总题目数不能少于1题")
    @Max(value = 100, message = "总题目数不能超过100题")
    private Integer totalQuestions;
    
    @NotNull(message = "总分不能为空")
    @Min(value = 1, message = "总分不能少于1分")
    @Max(value = 1000, message = "总分不能超过1000分")
    private Integer totalPoints;
    
    // 难度分布：{"EASY": 5, "MEDIUM": 10, "HARD": 5}
    private Map<String, Integer> difficultyDistribution;
    
    // 题型分布：{"SINGLE_CHOICE": 10, "MULTIPLE_CHOICE": 5, "TRUE_FALSE": 5, "FILL_BLANK": 3, "SUBJECTIVE": 2}
    private Map<String, Integer> questionTypeDistribution;
    
    // 知识点分布（可选）
    private Map<Long, Integer> knowledgePointDistribution;
    
    // 是否随机题目顺序
    private Boolean isRandomOrder = true;
    
    // 是否随机选项顺序
    private Boolean isRandomOptions = true;
}

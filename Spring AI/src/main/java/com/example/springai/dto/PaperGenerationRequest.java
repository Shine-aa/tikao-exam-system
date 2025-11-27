package com.example.springai.dto;

import lombok.Data;
import java.util.Map;

/**
 * 试卷生成请求 DTO
 */
@Data
public class PaperGenerationRequest {

    private String paperName;
    private String description;
    private Long classId;
    private Long courseId;
    private Integer durationMinutes = 120;
    private Integer totalQuestions;
    private Integer totalPoints;
    private Map<String, Integer> difficultyDistribution;
    private Map<String, Integer> questionTypeDistribution;
    private Boolean isRandomOrder = true;
    private Boolean isRandomOptions = true;
}

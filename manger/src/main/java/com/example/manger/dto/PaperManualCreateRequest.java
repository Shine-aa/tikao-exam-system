package com.example.manger.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 手动组卷请求DTO
 */
@Data
public class PaperManualCreateRequest {
    @NotBlank(message = "试卷名称不能为空")
    private String paperName;

    private String description;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    private Long classId; // 可选，试卷可绑定单个班级或通用

    @NotNull(message = "考试时长不能为空")
    @Min(value = 30, message = "考试时长不能少于30分钟")
    private Integer durationMinutes;

    @NotNull(message = "总分不能为空")
    @Min(value = 1, message = "总分不能小于1分")
    private Integer totalPoints;

    @NotNull(message = "题目列表不能为空")
    private List<PaperManualQuestion> questions; // 手动选择的题目列表

    // 内部类：手动组卷的题目信息
    @Data
    public static class PaperManualQuestion {
        @NotNull(message = "题目ID不能为空")
        private Long questionId;

        @NotNull(message = "题目顺序不能为空")
        private Integer questionOrder;

        @NotNull(message = "题目分值不能为空")
        @Min(value = 0, message = "题目分值不能小于0分")
        private Integer points;
    }
}
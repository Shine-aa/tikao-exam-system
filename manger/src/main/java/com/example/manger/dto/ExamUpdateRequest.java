package com.example.manger.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamUpdateRequest {
    @NotBlank(message = "考试名称不能为空")
    private String examName;

    private String description;

    @NotNull(message = "试卷ID不能为空")
    private Long paperId;

    @NotNull(message = "班级ID不能为空")
    private Long classId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "考试时长不能为空")
    @Min(value = 1, message = "考试时长至少1分钟")
    @Max(value = 480, message = "考试时长不能超过8小时")
    private Integer durationMinutes;

    @Min(value = 1, message = "最大尝试次数至少1次")
    @Max(value = 10, message = "最大尝试次数不能超过10次")
    private Integer maxAttempts = 1;

    private Boolean isRandomOrder = true;

    private Boolean isRandomOptions = true;

    private Boolean allowReview = true;
}

package com.example.manger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "授权教师请求参数")
public class AuthorizeTeacherRequest {

    @NotEmpty(message = "教师ID列表不能为空")
    @Schema(description = "教师ID列表", example = "[1,2,3]")
    private List<Long> teacherIds;
}
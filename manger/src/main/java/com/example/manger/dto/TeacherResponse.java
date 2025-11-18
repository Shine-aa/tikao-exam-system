package com.example.manger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "教师信息响应DTO")
public class TeacherResponse {

    @Schema(description = "教师ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "邮箱")
    private String email;

    // 可根据前端需要补充其他字段（如工号、部门等）
}
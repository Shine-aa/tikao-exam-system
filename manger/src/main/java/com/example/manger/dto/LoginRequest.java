package com.example.manger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginRequest {
    
    @Schema(description = "用户名或手机号", example = "admin", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Schema(description = "密码（密码登录时必填）", example = "123456")
    private String password;
    
    @Schema(description = "图形验证码（密码登录时必填）", example = "1234")
    private String captcha;
    
    @Schema(description = "验证码ID（密码登录时必填）", example = "captcha-123")
    private String captchaId;
    
    @Schema(description = "记住我", example = "false")
    private Boolean rememberMe = false;
}

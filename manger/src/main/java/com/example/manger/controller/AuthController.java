package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.AuthResponse;
import com.example.manger.dto.LoginRequest;
import com.example.manger.dto.RegisterRequest;
import com.example.manger.service.CaptchaService;
import com.example.manger.service.SmsService;
import com.example.manger.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册、验证码等认证相关接口")
public class AuthController {
    
    private final UserService userService;
    private final CaptchaService captchaService;
    private final SmsService smsService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册，支持用户名、邮箱、手机号注册")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或用户已存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.success("注册成功", null);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过用户名密码或短信验证码登录")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或验证码错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        AuthResponse response = userService.login(request, ipAddress, userAgent);
        return ApiResponse.success("登录成功", response);
    }
    
    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    @Operation(summary = "获取验证码", description = "获取图形验证码，用于注册和登录验证")
    public ApiResponse<CaptchaService.CaptchaResult> getCaptcha() {
        try {
            CaptchaService.CaptchaResult captcha = captchaService.generateCaptcha();
            return ApiResponse.success(captcha);
        } catch (Exception e) {
            return ApiResponse.error("生成验证码失败");
        }
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "用户退出登录，使Token失效")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            userService.logout(token);
            return ApiResponse.success("退出登录成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public ApiResponse<String> refreshToken(@RequestParam String refreshToken) {
        try {
            String newToken = userService.refreshToken(refreshToken);
            return ApiResponse.success("令牌刷新成功", newToken);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 发送短信验证码
     */
    @PostMapping("/sms/send")
    @Operation(summary = "发送短信验证码", description = "发送短信验证码到指定手机号，支持注册和登录两种类型")
    public ApiResponse<Map<String, Object>> sendSmsCode(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            String phone = request.get("phone");
            String type = request.get("type"); // register 或 login
            
            if (phone == null || phone.trim().isEmpty()) {
                return ApiResponse.error("手机号不能为空");
            }
            
            if (type == null || (!type.equals("register") && !type.equals("login"))) {
                return ApiResponse.error("验证码类型无效");
            }
            
            // 验证手机号格式
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return ApiResponse.error("手机号格式不正确");
            }
            
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            Map<String, Object> result = smsService.sendSmsCode(phone, type, ipAddress, userAgent);
            
            if ((Boolean) result.get("success")) {
                return ApiResponse.success("验证码发送成功", result);
            } else {
                return ApiResponse.error((String) result.get("message"));
            }
            
        } catch (Exception e) {
            return ApiResponse.error("发送验证码失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证短信验证码
     */
    @PostMapping("/sms/verify")
    @Operation(summary = "验证短信验证码", description = "验证短信验证码是否正确且未过期")
    public ApiResponse<Boolean> verifySmsCode(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String code = request.get("code");
            String type = request.get("type");
            
            if (phone == null || code == null || type == null) {
                return ApiResponse.error("参数不完整");
            }
            
            boolean isValid = smsService.verifySmsCode(phone, code, type);
            
            if (isValid) {
                return ApiResponse.success("验证码验证成功", true);
            } else {
                return ApiResponse.error("验证码错误或已过期");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("验证码验证失败: " + e.getMessage());
        }
    }
}

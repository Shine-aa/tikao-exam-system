package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.AuthResponse;
import com.example.manger.entity.User;
import com.example.manger.repository.UserRepository;
import com.example.manger.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户工具", description = "用户名和邮箱可用性检查等工具接口")
public class UserController {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    @Operation(summary = "检查用户名可用性", description = "检查指定用户名是否已被使用")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "检查成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<Map<String, Boolean>> checkUsername(
            @Parameter(description = "要检查的用户名") @RequestParam String username) {
        try {
            boolean exists = userRepository.existsByUsername(username);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", !exists);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱可用性", description = "检查指定邮箱是否已被使用")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "检查成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<Map<String, Boolean>> checkEmail(
            @Parameter(description = "要检查的邮箱") @RequestParam String email) {
        try {
            boolean exists = userRepository.existsByEmail(email);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", !exists);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

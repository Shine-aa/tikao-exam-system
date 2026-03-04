package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.AuthResponse;
import com.example.manger.entity.Role;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户工具", description = "用户名和邮箱可用性检查等工具接口")
public class UserController {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * Author：李正阳，李子政
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
     * Author：李正阳，李子政
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
    
    /**
     * Author：李正阳，李子政
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 创建简化的用户信息，避免循环引用
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("name",user.getName());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("facePhoto", user.getFacePhoto());
            userInfo.put("isActive", user.getIsActive());
            userInfo.put("classId", user.getClassId());
            userInfo.put("createTime", user.getCreateTime());
            userInfo.put("updateTime", user.getUpdateTime());
            userInfo.put("lastLoginTime", user.getLastLoginTime());
            
            // 只包含角色名称，不包含完整的角色对象
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            userInfo.put("roleNames", roleNames);
            
            return ApiResponse.success(userInfo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

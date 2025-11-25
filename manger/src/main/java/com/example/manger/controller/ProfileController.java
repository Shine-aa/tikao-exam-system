package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.ChangePasswordRequest;
import com.example.manger.dto.UpdateProfileRequest;
import com.example.manger.dto.UserResponse;
import com.example.manger.service.ProfileService;
import com.example.manger.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理、个人资料等接口")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {
    
    private final ProfileService profileService;
    private final JwtUtil jwtUtil;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @PreAuthorize("hasRole('USER') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<UserResponse> getUserInfo(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserResponse userInfo = profileService.getUserInfo(userId);
        return ApiResponse.success(userInfo);
    }
    
    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "更新个人资料", description = "更新当前登录用户的个人信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request, 
                                                  HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        UserResponse updatedUser = profileService.updateProfile(userId, request);
        return ApiResponse.success("资料更新成功", updatedUser);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或当前密码错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                           HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        profileService.changePassword(userId, request);
        return ApiResponse.success("密码修改成功", null);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("无法获取用户信息");
    }
}

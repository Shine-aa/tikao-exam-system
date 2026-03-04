package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.context.BaseContext;
import com.example.manger.dto.ChangePasswordRequest;
import com.example.manger.dto.UpdateProfileRequest;
import com.example.manger.dto.UserResponse;
import com.example.manger.service.AliOssService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理、个人资料等接口")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {
    
    private final ProfileService profileService;
    private final AliOssService aliOssService;
    
    /**
     * Author：李子政，郭依林
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<UserResponse> getUserInfo() {
        Long userId = getCurrentUserId();
        UserResponse userInfo = profileService.getUserInfo(userId);
        return ApiResponse.success(userInfo);
    }

    /**
     * 上传人脸照片
     */
    @PostMapping("/upload-face")
    @Operation(summary = "上传人脸照片", description = "上传考生的照片到阿里云 OSS 并保存到数据库")
    public ApiResponse<String> uploadFace(@RequestPart("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ApiResponse.error("上传文件不能为空");
            }
            
            // 1. 获取当前用户 ID
            Long userId = getCurrentUserId();
            
            // 2. 上传到阿里云 OSS
            String facePhotoUrl = aliOssService.uploadFile(file);
            
            // 3. 更新数据库
            profileService.updateFacePhoto(userId, facePhotoUrl);
            
            return ApiResponse.success("人脸照片上传成功", facePhotoUrl);
        } catch (Exception e) {
            return ApiResponse.error("上传人脸照片失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李子政，郭依林
     * 更新个人资料
     */
    @PutMapping("/profile")
    @Operation(summary = "更新个人资料", description = "更新当前登录用户的个人信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = getCurrentUserId();
        UserResponse updatedUser = profileService.updateProfile(userId, request);
        return ApiResponse.success("资料更新成功", updatedUser);
    }
    
    /**
     * Author：李子政，郭依林
     * 修改密码
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或当前密码错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = getCurrentUserId();
        profileService.changePassword(userId, request);
        return ApiResponse.success("密码修改成功", null);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return BaseContext.getCurrentId();
    }
}

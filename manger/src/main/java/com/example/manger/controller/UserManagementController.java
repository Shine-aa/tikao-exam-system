package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.*;
import com.example.manger.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class UserManagementController {
    
    private final UserManagementService userManagementService;
    
    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserResponse user = userManagementService.createUser(request);
            return ApiResponse.success("用户创建成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId, 
                                               @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserResponse user = userManagementService.updateUser(userId, request);
            return ApiResponse.success("用户更新成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        try {
            userManagementService.deleteUser(userId);
            return ApiResponse.success("用户删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "批量删除用户", description = "根据用户ID列表批量删除用户")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ApiResponse<Void> batchDeleteUsers(@RequestBody List<Long> userIds) {
        try {
            userManagementService.batchDeleteUsers(userIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long userId) {
        try {
            UserResponse user = userManagementService.getUserById(userId);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有用户
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isActive) {
        try {
            List<UserResponse> users = userManagementService.getAllUsers(username, email, isActive);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "分页查询用户", description = "支持按用户名、邮箱、状态等条件分页查询用户列表")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ApiResponse<PageResponse<UserResponse>> getUsersWithPagination(
            @Parameter(description = "用户名（模糊查询）") @RequestParam(required = false) String username,
            @Parameter(description = "邮箱（模糊查询）") @RequestParam(required = false) String email,
            @Parameter(description = "用户状态") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序顺序，asc或desc") @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            PageResponse<UserResponse> users = userManagementService.getUsersWithPagination(
                    username, email, isActive, page, size, sortBy, sortOrder);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取启用的用户
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<List<UserResponse>> getActiveUsers() {
        try {
            List<UserResponse> users = userManagementService.getActiveUsers();
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用用户
     */
    @PutMapping("/{userId}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<UserResponse> toggleUserStatus(@PathVariable Long userId) {
        try {
            UserResponse user = userManagementService.toggleUserStatus(userId);
            return ApiResponse.success("用户状态更新成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 为用户分配角色
     */
    @PutMapping("/{userId}/assign-roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<UserResponse> assignRolesToUser(@PathVariable Long userId,
                                                      @Valid @RequestBody AssignRolesRequest request) {
        try {
            UserResponse user = userManagementService.assignRolesToUser(userId, request);
            return ApiResponse.success("角色分配成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 从用户移除角色
     */
    @DeleteMapping("/{userId}/remove-roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<UserResponse> removeRolesFromUser(@PathVariable Long userId,
                                                        @RequestBody Set<Long> roleIds) {
        try {
            UserResponse user = userManagementService.removeRolesFromUser(userId, roleIds);
            return ApiResponse.success("角色移除成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 重置用户密码
     */
    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Void> resetUserPassword(@PathVariable Long userId,
                                              @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("newPassword");
            userManagementService.resetUserPassword(userId, newPassword);
            return ApiResponse.success("密码重置成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.*;
import com.example.manger.entity.User;
import com.example.manger.service.AliOssService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class UserManagementController {
    
    private final UserManagementService userManagementService;
    private final AliOssService aliOssService;
    
    /**
     * Author：李正阳
     * 管理员为任意用户上传/更新人脸照片
     */
    @PostMapping("/{userId}/face-photo")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "管理员更新人脸照片", description = "管理员上传或更新任意用户的人脸照片")
    public ApiResponse<String> updateUserFacePhoto(@PathVariable Long userId,
                                                  @RequestPart("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ApiResponse.error("上传文件不能为空");
            }
            
            // 1. 上传到阿里云 OSS
            String facePhotoUrl = aliOssService.uploadFile(file);
            
            // 2. 更新数据库
            userManagementService.updateUserFacePhoto(userId, facePhotoUrl);
            
            return ApiResponse.success("人脸照片更新成功", facePhotoUrl);
        } catch (Exception e) {
            return ApiResponse.error("更新人脸照片失败: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserResponse user = userManagementService.createUser(request);
            return ApiResponse.success("用户创建成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * Author：郭依林，李子政
     * 一键导入创建用户
     */
    @PostMapping("/import")
    public ApiResponse<Map<String, Object>> importUsers(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success("导入完成", userManagementService.importUsersFromExcel(file));
    }

    /**
     * Author：李正阳，李子政
     * 更新用户
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
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
     * Author：李子政，郭依林
     * 更新用户密码
     */
    @PutMapping("/{userId}/change-password")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    public ApiResponse<String> updatePassword(@PathVariable Long userId,
                                              @Valid @RequestBody User user) {
        try {
            userManagementService.resetUserPassword(userId, user.getPassword());
            return ApiResponse.success("用户更新成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        try {
            userManagementService.deleteUser(userId);
            return ApiResponse.success("用户删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
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
     * Author：李正阳，郭依林
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long userId) {
        try {
            UserResponse user = userManagementService.getUserById(userId);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 获取所有用户
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    public ApiResponse<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String role) {
        try {
            List<UserResponse> users = userManagementService.getAllUsers(username, email, isActive, role);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 分页查询用户
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "分页查询用户", description = "支持按用户名、邮箱、状态等条件分页查询用户列表")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ApiResponse<PageResponse<UserResponse>> getUsersWithPagination(
            @Parameter(description = "用户姓名（模糊查询）") @RequestParam(required = false) String name,
            @Parameter(description = "用户名（模糊查询）") @RequestParam(required = false) String username,
            @Parameter(description = "邮箱（模糊查询）") @RequestParam(required = false) String email,
            @Parameter(description = "用户状态") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序顺序，asc或desc") @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            PageResponse<UserResponse> users = userManagementService.getUsersWithPagination(
                    name,username, email, isActive, page, size, sortBy, sortOrder);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 获取启用的用户
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    public ApiResponse<List<UserResponse>> getActiveUsers() {
        try {
            List<UserResponse> users = userManagementService.getActiveUsers();
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 启用/禁用用户
     */
    @PutMapping("/{userId}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    public ApiResponse<UserResponse> toggleUserStatus(@PathVariable Long userId) {
        try {
            UserResponse user = userManagementService.toggleUserStatus(userId);
            return ApiResponse.success("用户状态更新成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，李子政
     * 为用户分配角色
     */
    @PutMapping("/{userId}/assign-roles")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
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
     * Author：李正阳，李子政
     * 从用户移除角色
     */
    @DeleteMapping("/{userId}/remove-roles")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
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
     * Author：李正阳，李子政
     * 重置用户密码
     */
    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
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
    
    /**
     * Author：李正阳，李子政
     * 获取所有学生（普通用户）
     */
    @GetMapping("/students")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "获取学生列表", description = "获取所有普通用户（学生）列表")
    public ApiResponse<List<UserResponse>> getStudents() {
        try {
            List<UserResponse> students = userManagementService.getStudents();
            return ApiResponse.success("获取学生列表成功", students);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，李子政
     * 分页获取学生列表（带班级信息）
     */
    @GetMapping("/students/page")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "分页获取学生列表", description = "分页获取学生列表，支持按班级和状态筛选")
    public ApiResponse<PageResponse<UserResponse>> getStudentsWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String status) {
        try {
            PageResponse<UserResponse> response = userManagementService.getStudentsWithPagination(
                    page, size, keyword, classId, status);
            return ApiResponse.success("获取学生列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，李子政
     * 分配学生到班级
     */
    @PostMapping("/students/assign")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "分配学生到班级", description = "将学生分配到指定班级")
    public ApiResponse<Void> assignStudentToClass(@RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            Long classId = Long.valueOf(request.get("classId").toString());
            
            userManagementService.assignStudentToClass(studentId, classId);
            return ApiResponse.success("分配成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，李子政
     * 批量分配学生到班级
     */
    @PostMapping("/students/batch-assign")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "批量分配学生到班级", description = "批量将学生分配到指定班级")
    public ApiResponse<Void> batchAssignStudentsToClass(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> studentIdsObj = (List<Object>) request.get("studentIds");
            List<Long> studentIds = studentIdsObj.stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .collect(Collectors.toList());
            Long classId = Long.valueOf(request.get("classId").toString());
            
            userManagementService.batchAssignStudentsToClass(studentIds, classId);
            return ApiResponse.success("批量分配成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 将学生移出班级
     */
    @DeleteMapping("/students/{studentId}/remove-from-class")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "将学生移出班级", description = "将学生从当前班级中移除")
    public ApiResponse<Void> removeStudentFromClass(@PathVariable Long studentId) {
        try {
            userManagementService.removeStudentFromClass(studentId);
            return ApiResponse.success("移出成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 批量将学生移出班级
     */
    @DeleteMapping("/students/batch-remove-from-class")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "批量将学生移出班级", description = "批量将学生从当前班级中移除")
    public ApiResponse<Void> batchRemoveStudentsFromClass(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> studentIdsObj = (List<Object>) request.get("studentIds");
            List<Long> studentIds = studentIdsObj.stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .collect(Collectors.toList());
            
            userManagementService.batchRemoveStudentsFromClass(studentIds);
            return ApiResponse.success("批量移出成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

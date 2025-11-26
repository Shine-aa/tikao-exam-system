package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.dto.PermissionResponse;
import com.example.manger.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限信息管理相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class PermissionController {
    
    private final PermissionService permissionService;
    
    /**
     * Author：李正阳，郭依林
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<List<PermissionResponse>> getAllPermissions(
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String permissionCode,
            @RequestParam(required = false) String resourceType) {
        try {
            List<PermissionResponse> permissions = permissionService.getAllPermissions(permissionName, permissionCode, resourceType);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 分页查询权限
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "分页查询权限", description = "支持按权限名称、权限代码、资源类型、状态等条件分页查询权限列表")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ApiResponse<PageResponse<PermissionResponse>> getPermissionsWithPagination(
            @Parameter(description = "权限名称（模糊查询）") @RequestParam(required = false) String permissionName,
            @Parameter(description = "权限代码（模糊查询）") @RequestParam(required = false) String permissionCode,
            @Parameter(description = "资源类型") @RequestParam(required = false) String resourceType,
            @Parameter(description = "权限状态") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序顺序，asc或desc") @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            PageResponse<PermissionResponse> permissions = permissionService.getPermissionsWithPagination(
                    permissionName, permissionCode, resourceType, isActive, page, size, sortBy, sortOrder);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 批量删除权限
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "批量删除权限", description = "根据权限ID列表批量删除权限")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ApiResponse<Void> batchDeletePermissions(@RequestBody List<Long> permissionIds) {
        try {
            permissionService.batchDeletePermissions(permissionIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 根据资源类型获取权限
     */
    @GetMapping("/resource-type/{resourceType}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<List<PermissionResponse>> getPermissionsByResourceType(@PathVariable String resourceType) {
        try {
            List<PermissionResponse> permissions = permissionService.getPermissionsByResourceType(resourceType);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 根据角色ID获取权限
     */
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<List<PermissionResponse>> getPermissionsByRoleId(@PathVariable Long roleId) {
        try {
            List<PermissionResponse> permissions = permissionService.getPermissionsByRoleId(roleId);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 根据用户ID获取权限
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<List<PermissionResponse>> getPermissionsByUserId(@PathVariable Long userId) {
        try {
            List<PermissionResponse> permissions = permissionService.getPermissionsByUserId(userId);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 检查用户是否有特定权限
     */
    @GetMapping("/check/{userId}/{permissionCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Boolean> hasPermission(@PathVariable Long userId, @PathVariable String permissionCode) {
        try {
            boolean hasPermission = permissionService.hasPermission(userId, permissionCode);
            return ApiResponse.success(hasPermission);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 获取用户所有权限代码
     */
    @GetMapping("/codes/user/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Set<String>> getUserPermissions(@PathVariable Long userId) {
        try {
            Set<String> permissions = permissionService.getUserPermissions(userId);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 检查用户是否有角色
     */
    @GetMapping("/check-role/{userId}/{roleCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Boolean> hasRole(@PathVariable Long userId, @PathVariable String roleCode) {
        try {
            boolean hasRole = permissionService.hasRole(userId, roleCode);
            return ApiResponse.success(hasRole);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 获取用户所有角色代码
     */
    @GetMapping("/role-codes/user/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Set<String>> getUserRoles(@PathVariable Long userId) {
        try {
            Set<String> roles = permissionService.getUserRoles(userId);
            return ApiResponse.success(roles);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

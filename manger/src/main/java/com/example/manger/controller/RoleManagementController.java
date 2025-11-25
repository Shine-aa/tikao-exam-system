package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.*;
import com.example.manger.service.RoleService;
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
import java.util.Set;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色信息管理相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class RoleManagementController {
    
    private final RoleService roleService;
    
    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        try {
            RoleResponse role = roleService.createRole(request);
            return ApiResponse.success("角色创建成功", role);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新角色
     */
    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<RoleResponse> updateRole(@PathVariable Long roleId,
                                               @Valid @RequestBody UpdateRoleRequest request) {
        try {
            RoleResponse role = roleService.updateRole(roleId, request);
            return ApiResponse.success("角色更新成功", role);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Void> deleteRole(@PathVariable Long roleId) {
        try {
            roleService.deleteRole(roleId);
            return ApiResponse.success("角色删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取角色详情
     */
    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<RoleResponse> getRoleById(@PathVariable Long roleId) {
        try {
            RoleResponse role = roleService.getRoleById(roleId);
            return ApiResponse.success(role);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有角色
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<List<RoleResponse>> getAllRoles(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) Boolean isActive) {
        try {
            List<RoleResponse> roles = roleService.getAllRoles(roleName, roleCode, isActive);
            return ApiResponse.success(roles);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询角色
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "分页查询角色", description = "支持按角色名称、角色代码、状态等条件分页查询角色列表")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ApiResponse<PageResponse<RoleResponse>> getRolesWithPagination(
            @Parameter(description = "角色名称（模糊查询）") @RequestParam(required = false) String roleName,
            @Parameter(description = "角色代码（模糊查询）") @RequestParam(required = false) String roleCode,
            @Parameter(description = "角色状态") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序顺序，asc或desc") @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            PageResponse<RoleResponse> roles = roleService.getRolesWithPagination(
                    roleName, roleCode, isActive, page, size, sortBy, sortOrder);
            return ApiResponse.success(roles);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "批量删除角色", description = "根据角色ID列表批量删除角色")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ApiResponse<Void> batchDeleteRoles(@RequestBody List<Long> roleIds) {
        try {
            roleService.batchDeleteRoles(roleIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取启用的角色
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<List<RoleResponse>> getActiveRoles() {
        try {
            List<RoleResponse> roles = roleService.getActiveRoles();
            return ApiResponse.success(roles);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用角色
     */
    @PutMapping("/{roleId}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<RoleResponse> toggleRoleStatus(@PathVariable Long roleId) {
        try {
            RoleResponse role = roleService.toggleRoleStatus(roleId);
            return ApiResponse.success("角色状态更新成功", role);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 为角色分配权限
     */
    @PutMapping("/{roleId}/assign-permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<RoleResponse> assignPermissionsToRole(@PathVariable Long roleId,
                                                            @RequestBody Set<Long> permissionIds) {
        try {
            RoleResponse role = roleService.assignPermissionsToRole(roleId, permissionIds);
            return ApiResponse.success("权限分配成功", role);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 从角色移除权限
     */
    @DeleteMapping("/{roleId}/remove-permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<RoleResponse> removePermissionsFromRole(@PathVariable Long roleId,
                                                              @RequestBody Set<Long> permissionIds) {
        try {
            RoleResponse role = roleService.removePermissionsFromRole(roleId, permissionIds);
            return ApiResponse.success("权限移除成功", role);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

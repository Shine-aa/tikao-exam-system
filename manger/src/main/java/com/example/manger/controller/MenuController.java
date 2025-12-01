package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.AssignMenusRequest;
import com.example.manger.dto.MenuRequest;
import com.example.manger.dto.MenuResponse;
import com.example.manger.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/menus")
@Tag(name = "菜单管理", description = "菜单管理相关接口")
public class MenuController {
    
    @Autowired
    private MenuService menuService;
    
    /**
     * Author：李正阳
     * 创建菜单
     */
    @PostMapping
    @Operation(summary = "创建菜单", description = "创建新的菜单项")
    @PreAuthorize("hasAuthority('menu:create')")
    public ApiResponse<MenuResponse> createMenu(@Valid @RequestBody MenuRequest request) {
        MenuResponse menu = menuService.createMenu(request);
        return ApiResponse.success("菜单创建成功", menu);
    }
    
    /**
     * Author：李正阳
     * 更新菜单
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新菜单", description = "更新指定ID的菜单信息")
    @PreAuthorize("hasAuthority('menu:edit')")
    public ApiResponse<MenuResponse> updateMenu(
            @Parameter(description = "菜单ID") @PathVariable Long id,
            @Valid @RequestBody MenuRequest request) {
        MenuResponse menu = menuService.updateMenu(id, request);
        return ApiResponse.success("菜单更新成功", menu);
    }
    
    /**
     * Author：李正阳
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单", description = "删除指定ID的菜单")
    @PreAuthorize("hasAuthority('menu:delete')")
    public ApiResponse<Void> deleteMenu(@Parameter(description = "菜单ID") @PathVariable Long id) {
        menuService.deleteMenu(id);
        return ApiResponse.success("菜单删除成功", null);
    }
    
    /**
     * Author：李正阳
     * 根据ID获取菜单
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取菜单详情", description = "根据ID获取菜单详细信息")
    @PreAuthorize("hasAuthority('menu:read')")
    public ApiResponse<MenuResponse> getMenuById(@Parameter(description = "菜单ID") @PathVariable Long id) {
        MenuResponse menu = menuService.getMenuById(id);
        return ApiResponse.success(menu);
    }
    
    /**
     * Author：李正阳
     * 获取所有菜单（树形结构）
     */
    @GetMapping
    @Operation(summary = "获取菜单列表", description = "获取所有菜单的树形结构")
    @PreAuthorize("hasAuthority('menu:read')")
    public ApiResponse<List<MenuResponse>> getAllMenus() {
        List<MenuResponse> menus = menuService.getAllMenus();
        return ApiResponse.success(menus);
    }
    
    /**
     * Author：李正阳
     * 获取启用的菜单（树形结构）
     */
    @GetMapping("/active")
    @Operation(summary = "获取启用菜单", description = "获取所有启用状态的菜单树形结构")
    public ApiResponse<List<MenuResponse>> getActiveMenus() {
        List<MenuResponse> menus = menuService.getActiveMenus();
        return ApiResponse.success(menus);
    }
    
    /**
     * Author：李正阳
     * 根据角色ID获取菜单
     */
    @GetMapping("/role/{roleId}")
    @Operation(summary = "获取角色菜单", description = "根据角色ID获取菜单列表")
    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<List<MenuResponse>> getMenusByRoleId(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<MenuResponse> menus = menuService.getMenusByRoleId(roleId);
        return ApiResponse.success(menus);
    }
    
    /**
     * Author：李正阳
     * 根据用户ID获取菜单
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户菜单", description = "根据用户ID获取菜单权限")
    @PreAuthorize("hasAuthority('user:read')")
    public ApiResponse<List<MenuResponse>> getMenusByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<MenuResponse> menus = menuService.getMenusByUserId(userId);
        return ApiResponse.success(menus);
    }
    
    /**
     * Author：李正阳
     * 为角色分配菜单
     */
    @PostMapping("/assign")
    @Operation(summary = "分配菜单", description = "为角色分配菜单权限")
    @PreAuthorize("hasAuthority('role:permission:assign')")
    public ApiResponse<Void> assignMenusToRole(@Valid @RequestBody AssignMenusRequest request) {
        menuService.assignMenusToRole(request);
        return ApiResponse.success("菜单分配成功", null);
    }
    
    /**
     * Author：李正阳
     * 获取角色的菜单ID列表
     */
    @GetMapping("/role/{roleId}/ids")
    @Operation(summary = "获取角色菜单ID", description = "获取角色拥有的菜单ID列表")
    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<List<Long>> getMenuIdsByRoleId(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<Long> menuIds = menuService.getMenuIdsByRoleId(roleId);
        return ApiResponse.success(menuIds);
    }
}

package com.example.manger.service;

import com.example.manger.dto.*;
import com.example.manger.entity.Permission;
import com.example.manger.entity.Role;
import com.example.manger.repository.PermissionRepository;
import com.example.manger.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    /**
     * 创建角色
     */
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        // 检查角色代码是否已存在
        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new RuntimeException("角色代码已存在");
        }
        
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        
        // 分配权限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(permissions.stream().collect(Collectors.toSet()));
        }
        
        Role savedRole = roleRepository.save(role);
        return convertToResponse(savedRole);
    }
    
    /**
     * 更新角色
     */
    @Transactional
    public RoleResponse updateRole(Long roleId, UpdateRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        // 检查角色代码是否已被其他角色使用
        if (request.getRoleCode() != null && 
            !request.getRoleCode().equals(role.getRoleCode()) &&
            roleRepository.existsByRoleCodeAndIdNot(request.getRoleCode(), roleId)) {
            throw new RuntimeException("角色代码已被其他角色使用");
        }
        
        // 更新基本信息
        if (request.getRoleName() != null) {
            role.setRoleName(request.getRoleName());
        }
        if (request.getRoleCode() != null) {
            role.setRoleCode(request.getRoleCode());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            role.setIsActive(request.getIsActive());
        }
        
        // 更新权限
        if (request.getPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(permissions.stream().collect(Collectors.toSet()));
        }
        
        Role savedRole = roleRepository.save(role);
        return convertToResponse(savedRole);
    }
    
    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("角色不存在");
        }
        roleRepository.deleteById(roleId);
    }
    
    /**
     * 获取角色详情
     */
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        return convertToResponse(role);
    }
    
    /**
     * 获取所有角色
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAllWithPermissions();
        return roles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据条件搜索角色
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles(String roleName, String roleCode, Boolean isActive) {
        List<Role> roles = roleRepository.findAllWithPermissions();
        
        // 应用搜索过滤条件
        return roles.stream()
                .filter(role -> roleName == null || role.getRoleName().contains(roleName))
                .filter(role -> roleCode == null || role.getRoleCode().contains(roleCode))
                .filter(role -> isActive == null || role.getIsActive().equals(isActive))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取启用的角色
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getActiveRoles() {
        List<Role> roles = roleRepository.findByIsActiveTrue();
        return roles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 启用/禁用角色
     */
    @Transactional
    public RoleResponse toggleRoleStatus(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        role.setIsActive(!role.getIsActive());
        Role savedRole = roleRepository.save(role);
        return convertToResponse(savedRole);
    }
    
    /**
     * 为角色分配权限
     */
    @Transactional
    public RoleResponse assignPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.setPermissions(permissions.stream().collect(Collectors.toSet()));
        
        Role savedRole = roleRepository.save(role);
        return convertToResponse(savedRole);
    }
    
    /**
     * 从角色移除权限
     */
    @Transactional
    public RoleResponse removePermissionsFromRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        Set<Permission> permissions = role.getPermissions();
        permissions.removeIf(permission -> permissionIds.contains(permission.getId()));
        role.setPermissions(permissions);
        
        Role savedRole = roleRepository.save(role);
        return convertToResponse(savedRole);
    }
    
    /**
     * 转换为响应DTO
     */
    private RoleResponse convertToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setRoleName(role.getRoleName());
        response.setRoleCode(role.getRoleCode());
        response.setDescription(role.getDescription());
        response.setIsActive(role.getIsActive());
        response.setCreateTime(role.getCreateTime());
        response.setUpdateTime(role.getUpdateTime());
        
        // 转换权限信息
        if (role.getPermissions() != null) {
            Set<PermissionResponse> permissionResponses = role.getPermissions().stream()
                    .map(this::convertPermissionToResponse)
                    .collect(Collectors.toSet());
            response.setPermissions(permissionResponses);
        }
        
        return response;
    }
    
    /**
     * 分页查询角色
     */
    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> getRolesWithPagination(String roleName, String roleCode, Boolean isActive,
                                                             Integer page, Integer size, String sortBy, String sortOrder) {
        // 构建查询条件
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // 如果有排序字段，添加排序
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        }
        
        // 执行分页查询
        Page<Role> rolePage = roleRepository.findAllWithPermissions(pageable);
        
        // 应用搜索过滤条件
        List<RoleResponse> filteredRoles = rolePage.getContent().stream()
                .filter(role -> roleName == null || role.getRoleName().contains(roleName))
                .filter(role -> roleCode == null || role.getRoleCode().contains(roleCode))
                .filter(role -> isActive == null || role.getIsActive().equals(isActive))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(filteredRoles, page, size, rolePage.getTotalElements());
    }
    
    /**
     * 批量删除角色
     */
    @Transactional
    public void batchDeleteRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new RuntimeException("角色ID列表不能为空");
        }
        
        // 检查所有角色是否存在
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new RuntimeException("部分角色不存在");
        }
        
        roleRepository.deleteAllById(roleIds);
    }
    
    /**
     * 转换权限为响应DTO
     */
    private PermissionResponse convertPermissionToResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId());
        response.setPermissionName(permission.getPermissionName());
        response.setPermissionCode(permission.getPermissionCode());
        response.setResourceType(permission.getResourceType());
        response.setAction(permission.getAction());
        response.setDescription(permission.getDescription());
        response.setIsActive(permission.getIsActive());
        response.setCreateTime(permission.getCreateTime());
        return response;
    }
}

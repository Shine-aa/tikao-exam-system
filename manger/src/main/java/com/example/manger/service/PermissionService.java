package com.example.manger.service;

import com.example.manger.dto.PageResponse;
import com.example.manger.dto.PermissionResponse;
import com.example.manger.entity.Permission;
import com.example.manger.entity.Role;
import com.example.manger.entity.User;
import com.example.manger.repository.PermissionRepository;
import com.example.manger.repository.RoleRepository;
import com.example.manger.repository.UserRepository;
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
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    
    /**
     * 检查用户是否有特定权限
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String permissionCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return user.getRoles().stream()
                .filter(Role::getIsActive)
                .flatMap(role -> role.getPermissions().stream())
                .filter(Permission::getIsActive)
                .anyMatch(permission -> permission.getPermissionCode().equals(permissionCode));
    }
    
    /**
     * 获取用户所有权限代码
     */
    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(Long userId) {
        List<Permission> permissions = permissionRepository.findActivePermissionsByUserId(userId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }
    
    /**
     * 检查用户是否有角色
     */
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, String roleCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return user.getRoles().stream()
                .filter(Role::getIsActive)
                .anyMatch(role -> role.getRoleCode().equals(roleCode));
    }
    
    /**
     * 获取用户所有角色代码
     */
    @Transactional(readOnly = true)
    public Set<String> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return user.getRoles().stream()
                .filter(Role::getIsActive)
                .map(Role::getRoleCode)
                .collect(Collectors.toSet());
    }
    
    /**
     * 获取所有权限
     */
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据条件搜索权限
     */
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions(String permissionName, String permissionCode, String resourceType) {
        List<Permission> permissions = permissionRepository.findAll();
        
        // 应用搜索过滤条件
        return permissions.stream()
                .filter(permission -> permissionName == null || permission.getPermissionName().contains(permissionName))
                .filter(permission -> permissionCode == null || permission.getPermissionCode().contains(permissionCode))
                .filter(permission -> resourceType == null || (permission.getResourceType() != null && permission.getResourceType().contains(resourceType)))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据资源类型获取权限
     */
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByResourceType(String resourceType) {
        List<Permission> permissions = permissionRepository.findByResourceTypeAndIsActiveTrue(resourceType);
        return permissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据角色ID获取权限
     */
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByRoleId(Long roleId) {
        List<Permission> permissions = permissionRepository.findActivePermissionsByRoleId(roleId);
        return permissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据用户ID获取权限
     */
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByUserId(Long userId) {
        List<Permission> permissions = permissionRepository.findActivePermissionsByUserId(userId);
        return permissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页查询权限
     */
    @Transactional(readOnly = true)
    public PageResponse<PermissionResponse> getPermissionsWithPagination(String permissionName, String permissionCode, 
                                                                         String resourceType, Boolean isActive,
                                                                         Integer page, Integer size, String sortBy, String sortOrder) {
        // 构建查询条件
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // 如果有排序字段，添加排序
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        }
        
        // 执行分页查询
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        
        // 应用搜索过滤条件
        List<PermissionResponse> filteredPermissions = permissionPage.getContent().stream()
                .filter(permission -> permissionName == null || permission.getPermissionName().contains(permissionName))
                .filter(permission -> permissionCode == null || permission.getPermissionCode().contains(permissionCode))
                .filter(permission -> resourceType == null || permission.getResourceType().contains(resourceType))
                .filter(permission -> isActive == null || permission.getIsActive().equals(isActive))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(filteredPermissions, page, size, permissionPage.getTotalElements());
    }
    
    /**
     * 批量删除权限
     */
    @Transactional
    public void batchDeletePermissions(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new RuntimeException("权限ID列表不能为空");
        }
        
        // 检查所有权限是否存在
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new RuntimeException("部分权限不存在");
        }
        
        permissionRepository.deleteAllById(permissionIds);
    }
    
    /**
     * 转换为响应DTO
     */
    private PermissionResponse convertToResponse(Permission permission) {
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

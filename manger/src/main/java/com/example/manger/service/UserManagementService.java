package com.example.manger.service;

import com.example.manger.dto.*;
import com.example.manger.entity.Role;
import com.example.manger.entity.User;
import com.example.manger.repository.RoleRepository;
import com.example.manger.repository.UserRepository;
import com.example.manger.util.PasswordUtil;
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
public class UserManagementService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordUtil passwordUtil;
    
    /**
     * 创建用户
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        // 加密密码
        String salt = passwordUtil.generateSalt();
        String hashedPassword = passwordUtil.hashPassword(request.getPassword(), salt);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        
        // 分配角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(roles.stream().collect(Collectors.toSet()));
        }
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 更新用户
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户名是否已被其他用户使用
        if (request.getUsername() != null && 
            !request.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
            throw new RuntimeException("用户名已被其他用户使用");
        }
        
        // 检查邮箱是否已被其他用户使用
        if (request.getEmail() != null && 
            !request.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new RuntimeException("邮箱已被其他用户使用");
        }
        
        // 更新基本信息
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        
        // 更新密码
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            String salt = passwordUtil.generateSalt();
            String hashedPassword = passwordUtil.hashPassword(request.getPassword(), salt);
            user.setPassword(hashedPassword);
            user.setSalt(salt);
        }
        
        // 更新角色
        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(roles.stream().collect(Collectors.toSet()));
        }
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
    }
    
    /**
     * 批量删除用户
     */
    @Transactional
    public void batchDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("用户ID列表不能为空");
        }
        
        // 检查所有用户是否存在
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new RuntimeException("部分用户不存在");
        }
        
        userRepository.deleteAllById(userIds);
    }
    
    /**
     * 获取用户详情
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToResponse(user);
    }
    
    /**
     * 获取所有用户
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllWithRoles();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据条件搜索用户
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(String username, String email, Boolean isActive) {
        List<User> users = userRepository.findAllWithRoles();
        
        // 应用搜索过滤条件
        return users.stream()
                .filter(user -> username == null || user.getUsername().contains(username))
                .filter(user -> email == null || user.getEmail().contains(email))
                .filter(user -> isActive == null || user.getIsActive().equals(isActive))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页查询用户
     */
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsersWithPagination(String username, String email, Boolean isActive, 
                                                           Integer page, Integer size, String sortBy, String sortOrder) {
        // 构建查询条件
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // 如果有排序字段，添加排序
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        }
        
        // 执行分页查询
        Page<User> userPage = userRepository.findAllWithRoles(pageable);
        
        // 应用搜索过滤条件
        List<UserResponse> filteredUsers = userPage.getContent().stream()
                .filter(user -> username == null || user.getUsername().contains(username))
                .filter(user -> email == null || user.getEmail().contains(email))
                .filter(user -> isActive == null || user.getIsActive().equals(isActive))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(filteredUsers, page, size, userPage.getTotalElements());
    }
    
    /**
     * 获取启用的用户
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 启用/禁用用户
     */
    @Transactional
    public UserResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setIsActive(!user.getIsActive());
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 为用户分配角色
     */
    @Transactional
    public UserResponse assignRolesToUser(Long userId, AssignRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        user.setRoles(roles.stream().collect(Collectors.toSet()));
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 从用户移除角色
     */
    @Transactional
    public UserResponse removeRolesFromUser(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Set<Role> roles = user.getRoles();
        roles.removeIf(role -> roleIds.contains(role.getId()));
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 重置用户密码
     */
    @Transactional
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        String salt = passwordUtil.generateSalt();
        String hashedPassword = passwordUtil.hashPassword(newPassword, salt);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        
        userRepository.save(user);
    }
    
    /**
     * 转换为响应DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setIsActive(user.getIsActive());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        
        // 转换角色信息
        if (user.getRoles() != null) {
            Set<RoleResponse> roleResponses = user.getRoles().stream()
                    .map(this::convertRoleToResponse)
                    .collect(Collectors.toSet());
            response.setRoles(roleResponses);
        }
        
        return response;
    }
    
    /**
     * 转换角色为响应DTO
     */
    private RoleResponse convertRoleToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setRoleName(role.getRoleName());
        response.setRoleCode(role.getRoleCode());
        response.setDescription(role.getDescription());
        response.setIsActive(role.getIsActive());
        response.setCreateTime(role.getCreateTime());
        response.setUpdateTime(role.getUpdateTime());
        return response;
    }
}

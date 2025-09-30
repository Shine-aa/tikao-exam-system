package com.example.manger.service;

import com.example.manger.dto.DashboardStatsResponse;
import com.example.manger.dto.UserResponse;
import com.example.manger.entity.User;
import com.example.manger.repository.PermissionRepository;
import com.example.manger.repository.RoleRepository;
import com.example.manger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    /**
     * 获取仪表盘统计数据
     */
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();
        
        // 统计用户数量
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        
        // 统计角色数量
        long totalRoles = roleRepository.count();
        
        // 统计权限数量
        long totalPermissions = permissionRepository.count();
        
        // 获取最近创建的用户（最多5个）
        List<User> recentUsers = userRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createTime"))
        ).getContent();
        
        response.setTotalUsers(totalUsers);
        response.setActiveUsers(activeUsers);
        response.setTotalRoles(totalRoles);
        response.setTotalPermissions(totalPermissions);
        response.setRecentUsers(convertToUserResponses(recentUsers));
        
        return response;
    }
    
    /**
     * 转换用户实体为响应DTO
     */
    private List<UserResponse> convertToUserResponses(List<User> users) {
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换单个用户实体为响应DTO
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setIsActive(user.getIsActive());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        // 注意：这里不包含角色信息，避免循环依赖
        return response;
    }
}

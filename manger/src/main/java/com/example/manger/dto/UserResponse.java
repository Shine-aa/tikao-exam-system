package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String facePhoto;
    private Boolean isActive;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Set<RoleResponse> roles;
    
    // 角色相关字段（用于简化显示）
    private String role; // 用户角色代码
    private String roleName; // 角色显示名称
    
    // 班级相关字段
    private Long classId; // 班级ID
    private String className; // 班级名称
    private Long majorId; // 专业ID
    private String majorName; // 专业名称
}

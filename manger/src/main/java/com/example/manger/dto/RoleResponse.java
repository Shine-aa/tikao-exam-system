package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RoleResponse {
    
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Set<PermissionResponse> permissions;
}

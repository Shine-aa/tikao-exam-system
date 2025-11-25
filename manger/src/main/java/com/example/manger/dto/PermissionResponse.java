package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissionResponse {
    
    private Long id;
    private String permissionName;
    private String permissionCode;
    private String resourceType;
    private String action;
    private String description;
    private Boolean isActive;
    private LocalDateTime createTime;
}

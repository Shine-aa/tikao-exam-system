package com.example.manger.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateRoleRequest {
    
    @Size(min = 2, max = 50, message = "角色名称长度必须在2-50个字符之间")
    private String roleName;
    
    @Size(min = 2, max = 50, message = "角色代码长度必须在2-50个字符之间")
    private String roleCode;
    
    private String description;
    
    private Boolean isActive;
    
    private Set<Long> permissionIds;
}

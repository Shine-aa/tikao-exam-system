package com.example.manger.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 菜单请求DTO
 */
@Data
public class MenuRequest {
    
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;
    
    @NotBlank(message = "菜单代码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_:]+$", message = "菜单代码只能包含字母、数字、下划线和冒号")
    private String menuCode;
    
    private Long parentId;
    
    @NotBlank(message = "菜单类型不能为空")
    @Pattern(regexp = "^(menu|button)$", message = "菜单类型只能是menu或button")
    private String menuType = "menu";
    
    private String path;
    
    private String component;
    
    private String icon;
    
    private Integer sortOrder = 0;
    
    @NotNull(message = "是否启用不能为空")
    private Boolean isActive = true;
    
    private String description;
}

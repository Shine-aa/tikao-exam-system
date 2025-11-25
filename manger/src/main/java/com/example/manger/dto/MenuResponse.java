package com.example.manger.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单响应DTO
 */
@Data
public class MenuResponse {
    
    private Long id;
    
    private String menuName;
    
    private String menuCode;
    
    private Long parentId;
    
    private String parentName;
    
    private String menuType;
    
    private String path;
    
    private String component;
    
    private String icon;
    
    private Integer sortOrder;
    
    private Boolean isActive;
    
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 子菜单列表
    private List<MenuResponse> children;
}

package com.example.manger.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "menus")
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;
    
    @Column(name = "menu_code", nullable = false, length = 100, unique = true)
    private String menuCode;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "menu_type", nullable = false, length = 20)
    private String menuType = "menu"; // menu-菜单，button-按钮
    
    @Column(name = "path", length = 200)
    private String path;
    
    @Column(name = "component", length = 200)
    private String component;
    
    @Column(name = "icon", length = 100)
    private String icon;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    
    // 子菜单列表（非数据库字段）
    @Transient
    private List<Menu> children;
    
    // 父菜单名称（非数据库字段）
    @Transient
    private String parentName;
    
}

package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(exclude = {"roles"})
@ToString(exclude = {"roles"})
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;
    
    @Column(name = "permission_code", nullable = false, length = 100, unique = true)
    private String permissionCode;
    
    @Column(name = "resource_type", length = 50)
    private String resourceType;
    
    @Column(name = "action", length = 50)
    private String action;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    // 权限与角色的多对多关系
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
}

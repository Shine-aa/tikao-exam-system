package com.example.manger.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(exclude = {"roles"})
@ToString(exclude = {"roles"})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "salt", nullable = false, length = 64)
    private String salt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 直接在字段上配置序列化格式，无需依赖全局 ObjectMapper
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    @Column(name = "class_id")
    private Long classId;

    // 直接在字段上配置序列化格式，无需依赖全局 ObjectMapper
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    // 直接在字段上配置序列化格式，无需依赖全局 ObjectMapper
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    
    // 用户与角色的多对多关系
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}

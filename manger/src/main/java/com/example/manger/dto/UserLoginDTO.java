package com.example.manger.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户登录后存储到 Redis 的数据传输对象（无任何关联关系）
 */
@Data
public class UserLoginDTO {
    private Long id;                  // 用户ID
    private String name;              // 姓名
    private String username;          // 登录账号
    private String email;             // 邮箱
    private String phone;             // 手机号
    private Boolean isActive;         // 是否激活
    private LocalDateTime lastLoginTime; // 最后登录时间
    private Long classId;             // 班级ID（如果需要）
    private Set<String> roleCodes;    // 角色编码集合（如 "ROLE_TEACHER"）
    private Set<String> permissionCodes; // 权限编码集合（如 "user:query"）
}
package com.example.manger.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String name;
        private String username;
        private String email;
        private String createTime;
        private String lastLoginTime;
        private String role; // 用户角色
        private String roleName; // 角色名称
    }
}

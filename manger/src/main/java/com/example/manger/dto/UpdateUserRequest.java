package com.example.manger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {
    @Size(min = 2, max = 10, message = "用户姓名长度必须在2-10个字符之间")
    private String name;
    
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private Boolean isActive;
    
    private Set<Long> roleIds;
}
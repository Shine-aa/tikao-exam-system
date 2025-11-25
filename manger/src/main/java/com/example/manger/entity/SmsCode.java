package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 短信验证码实体
 */
@Entity
@Table(name = "sms_codes")
@Data
public class SmsCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 手机号
     */
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    
    /**
     * 验证码
     */
    @Column(name = "code", nullable = false, length = 10)
    private String code;
    
    /**
     * 验证码类型：register-注册, login-登录
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    
    /**
     * 是否已使用
     */
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;
    
    /**
     * 过期时间
     */
    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 使用时间
     */
    @Column(name = "used_time")
    private LocalDateTime usedTime;
    
    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    /**
     * 用户代理
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;
}

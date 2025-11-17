package com.example.manger.service;

import com.example.manger.dto.*;
import com.example.manger.entity.Role;
import com.example.manger.entity.User;
import com.example.manger.entity.UserSession;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.RoleRepository;
import com.example.manger.repository.UserRepository;
import com.example.manger.repository.UserSessionRepository;
import com.example.manger.util.JwtUtil;
import com.example.manger.util.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final RoleRepository roleRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    // 注入你配置的 ObjectMapper（支持 LocalDateTime）
    private final ObjectMapper redisObjectMapper; // 对应 RedisConfig 中的 redisObjectMapper 方法
    private final UserDtoConverter dtoConverter; // 注入转换工具
    
    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户姓名是否为空
        if (request.getName()==null||request.getName().isBlank()) {
            throw new BusinessException(ErrorCode.PASSWORD_BLANK, "姓名不能为空");
        }
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(1001, "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(1002, "邮箱已被注册");
        }
        
        // 检查手机号是否已存在
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException(1003, "手机号已被注册");
            }
        }
        
        // 生成盐值和加密密码
        String salt = passwordUtil.generateSalt();
        String hashedPassword = passwordUtil.hashPassword(request.getPassword(), salt);
        
        // 创建用户
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPassword(hashedPassword);
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setSalt(salt);
        user.setIsActive(true);
        
        // 为新用户分配普通用户角色
        Optional<Role> userRole = roleRepository.findByRoleCode("USER");
        if (userRole.isPresent()) {
            Set<Role> roles = new HashSet<>();
            roles.add(userRole.get());
            user.setRoles(roles);
        } else {
            throw new BusinessException(1007, "系统配置错误：未找到普通用户角色");
        }
        
        userRepository.save(user);
    }
    
    /**
     * 用户登录
     */
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        // 选择过期时间
        long ttlSeconds = Boolean.TRUE.equals(request.getRememberMe()) ? jwtUtil.getRememberExpiration() : jwtUtil.getExpiration();

        // 验证验证码（只有在提供验证码时才验证）
        if (request.getCaptcha() != null && !request.getCaptcha().isEmpty()) {
            String captchaKey = "captcha:" + request.getCaptchaId();
            String storedCaptcha = redisTemplate.opsForValue().get(captchaKey);
            
            if (storedCaptcha == null || !storedCaptcha.equalsIgnoreCase(request.getCaptcha())) {
                throw new BusinessException(1004, "验证码错误或已过期");
            }
            
            // 删除验证码
            redisTemplate.delete(captchaKey);
        }
        
        // 查找用户（支持用户名和手机号登录）
        User user = null;
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            // 先尝试按用户名查找
            user = userRepository.findByUsername(request.getUsername()).orElse(null);
            
            // 如果按用户名找不到，且用户名看起来像手机号，则按手机号查找
            if (user == null && request.getUsername().matches("^1[3-9]\\d{9}$")) {
                user = userRepository.findByPhone(request.getUsername()).orElse(null);
            }
        }
        
        if (user == null) {
            throw new BusinessException(1005, "用户名或密码错误");
        }
        
        // 验证密码（只有在提供密码时才验证）
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!passwordUtil.verifyPassword(request.getPassword(), user.getSalt(), user.getPassword())) {
                throw new BusinessException(1005, "用户名或密码错误");
            }
        }
        
        // 检查用户是否被禁用
        if (!user.getIsActive()) {
            throw new BusinessException(1006, "用户已被禁用");
        }
        
        // 并发会话控制
        if (request.getRememberMe()) {
            // 记住我模式：允许多个会话
            handleConcurrentSessions(user.getId(), false);
        } else {
            // 普通模式：限制会话数量
            handleConcurrentSessions(user.getId(), true);
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 生成令牌
        // 生成 JWT Token（携带自定义过期时间）
        String token = jwtUtil.generateToken((long) user.getId(), ttlSeconds);
        // 转换为 DTO（剥离关联对象）
        UserLoginDTO userDTO = dtoConverter.convertToLoginDTO(user);

        // 转换 DTO 为 Map（无循环关联，不会栈溢出）
        Map<String, Object> userStore = redisObjectMapper.convertValue(userDTO, Map.class);

        // 存储会话至 Redis（使用同样的 TTL）
        jwtUtil.storeSessionInRedis(token, userStore, ttlSeconds);
        // 保存会话信息
        saveUserSession(user.getId(), token, token, ipAddress, userAgent, request.getRememberMe());

        // 构建响应
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setCreateTime(user.getCreateTime().toString());
        userInfo.setLastLoginTime(user.getLastLoginTime() != null ? user.getLastLoginTime().toString() : "");
        
        // 设置用户角色信息
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            var role = user.getRoles().iterator().next();
            userInfo.setRole(role.getRoleCode());
            userInfo.setRoleName(role.getRoleName());
        }
        
        response.setUserInfo(userInfo);
        
        return response;
    }
    
    /**
     * 处理并发会话
     */
    private void handleConcurrentSessions(Long userId, boolean limitSessions) {
        if (limitSessions) {
            // 限制会话数量为1
            userSessionRepository.deactivateAllSessionsByUserId(userId);
        } else {
            // 记住我模式：限制会话数量为3
            LocalDateTime now = LocalDateTime.now();
            var activeSessions = userSessionRepository.findActiveSessionsByUserId(userId, now);
            
            if (activeSessions.size() >= 3) {
                // 删除最旧的会话
                UserSession oldestSession = activeSessions.stream()
                        .min((s1, s2) -> s1.getCreateTime().compareTo(s2.getCreateTime()))
                        .orElse(null);
                
                if (oldestSession != null) {
                    oldestSession.setIsActive(false);
                    userSessionRepository.save(oldestSession);
                }
            }
        }
    }
    
    /**
     * 保存用户会话
     */
    private void saveUserSession(Long userId, String token, String refreshToken, String ipAddress, String userAgent, boolean rememberMe) {
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setSessionToken(token);
        session.setRefreshToken(refreshToken);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setIsActive(true);
        
        // 设置过期时间
        if (rememberMe) {
            session.setExpiresAt(LocalDateTime.now().plusDays(30));
        } else {
            session.setExpiresAt(LocalDateTime.now().plusHours(24));
        }
        
        userSessionRepository.save(session);
    }
    
    /**
     * 退出登录
     */
    @Transactional
    public void logout(String token) {
        jwtUtil.inValidate(token);
        UserSession session = userSessionRepository.findBySessionToken(token)
                .orElse(null);
        
        if (session != null) {
            session.setIsActive(false);
            userSessionRepository.save(session);
        }
    }
}

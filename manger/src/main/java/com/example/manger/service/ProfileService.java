package com.example.manger.service;

import com.example.manger.dto.ChangePasswordRequest;
import com.example.manger.dto.UpdateProfileRequest;
import com.example.manger.dto.UserResponse;
import com.example.manger.entity.User;
import com.example.manger.exception.BusinessException;
import com.example.manger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.manger.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil passwordUtil;
    
    /**
     * 获取用户信息
     */
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new BusinessException(1007, "用户不存在"));
        
        return convertToUserResponse(user);
    }
    
    /**
     * 更新个人资料
     */
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(1007, "用户不存在"));
        
        // 检查用户名是否已被其他用户使用
        if (!user.getUsername().equals(request.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
                throw new BusinessException(1008, "用户名已被使用");
            }
        }
        
        // 检查邮箱是否已被其他用户使用
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new BusinessException(1009, "邮箱已被使用");
            }
        }
        
        // 检查手机号是否已被其他用户使用
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (!request.getPhone().equals(user.getPhone()) && 
                userRepository.existsByPhoneAndIdNot(request.getPhone(), userId)) {
                throw new BusinessException(1010, "手机号已被使用");
            }
        }
        
        // 更新用户信息
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }
    
    /**
     * 更新用户人脸照片
     */
    @Transactional
    public void updateFacePhoto(Long userId, String facePhotoUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(1007, "用户不存在"));
        user.setFacePhoto(facePhotoUrl);
        userRepository.save(user);
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(1007, "用户不存在"));
        
        // 验证当前密码 - 使用自定义的密码验证方法
        if (!passwordUtil.verifyPassword(request.getCurrentPassword(), user.getSalt(), user.getPassword())) {
            throw new BusinessException(1011, "当前密码错误");
        }
        
        // 检查新密码是否与当前密码相同
        if (passwordUtil.verifyPassword(request.getNewPassword(), user.getSalt(), user.getPassword())) {
            throw new BusinessException(1012, "新密码不能与当前密码相同");
        }
        
        // 更新密码 - 使用自定义的密码编码方法
        String newHashedPassword = passwordUtil.hashPassword(request.getNewPassword(), user.getSalt());
        user.setPassword(newHashedPassword);
        userRepository.save(user);
    }
    
    /**
     * 转换为用户响应对象
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setFacePhoto(user.getFacePhoto());
        response.setIsActive(user.getIsActive());
        response.setCreateTime(user.getCreateTime());
        response.setLastLoginTime(user.getLastLoginTime());
        
        // 设置角色信息
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            String roleCode = user.getRoles().iterator().next().getRoleCode();
            response.setRole(roleCode);
            response.setRoleName(getRoleDisplayName(roleCode));
        }
        
        return response;
    }
    
    /**
     * 获取角色显示名称
     */
    private String getRoleDisplayName(String roleCode) {
        switch (roleCode) {
            case "SUPER_ADMIN":
                return "超级管理员";
            case "USER":
                return "普通用户";
            default:
                return roleCode;
        }
    }
}

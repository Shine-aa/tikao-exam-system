package com.example.manger.dto;

import com.example.manger.entity.Permission;
import com.example.manger.entity.Role;
import com.example.manger.entity.User;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDtoConverter {

    /**
     * 将 User 实体转换为 UserLoginDTO（剥离所有关联对象，只保留编码）
     */
    public UserLoginDTO convertToLoginDTO(User user) {
        UserLoginDTO dto = new UserLoginDTO();
        
        // 基本信息映射
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIsActive(user.getIsActive());
        dto.setLastLoginTime(user.getLastLoginTime());
        dto.setClassId(user.getClassId());
        
        // 提取角色编码（只保留有效角色）
        Set<String> roleCodes = user.getRoles().stream()
                .filter(Role::getIsActive) // 只保留激活的角色
                .map(Role::getRoleCode)    // 取角色编码（如 "ROLE_ADMIN"）
                .collect(Collectors.toSet());
        dto.setRoleCodes(roleCodes);
        
        // 提取权限编码（去重，只保留有效权限）
        Set<String> permissionCodes = user.getRoles().stream()
                .filter(Role::getIsActive) // 过滤有效角色
                .flatMap(role -> role.getPermissions().stream()) // 展开权限集合
                .filter(Permission::getIsActive) // 过滤有效权限
                .map(Permission::getPermissionCode) // 取权限编码（如 "user:edit"）
                .collect(Collectors.toSet());
        dto.setPermissionCodes(permissionCodes);
        
        return dto;
    }
}
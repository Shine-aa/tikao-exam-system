package com.example.manger.repository;

import com.example.manger.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * 根据权限代码查找权限
     */
    Optional<Permission> findByPermissionCode(String permissionCode);
    
    /**
     * 检查权限代码是否存在
     */
    boolean existsByPermissionCode(String permissionCode);
    
    /**
     * 检查权限代码是否存在（排除指定ID）
     */
    boolean existsByPermissionCodeAndIdNot(String permissionCode, Long id);
    
    /**
     * 查找所有启用的权限
     */
    List<Permission> findByIsActiveTrue();
    
    /**
     * 根据资源类型查找启用的权限
     */
    List<Permission> findByResourceTypeAndIsActiveTrue(String resourceType);
    
    /**
     * 根据角色ID查找角色的所有权限
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId AND p.isActive = true")
    List<Permission> findActivePermissionsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID查找用户的所有权限
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.id = :userId AND p.isActive = true")
    List<Permission> findActivePermissionsByUserId(@Param("userId") Long userId);
    
    /**
     * 分页查找所有权限
     */
    Page<Permission> findAll(Pageable pageable);
}

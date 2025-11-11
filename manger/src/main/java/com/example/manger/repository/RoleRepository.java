package com.example.manger.repository;

import com.example.manger.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色代码查找角色
     */
    Optional<Role> findByRoleCode(String roleCode);

    /**
     * 根据角色代码查找角色
     */
    Optional<Role> findByRoleName(String roleName);
    
    /**
     * 检查角色代码是否存在
     */
    boolean existsByRoleCode(String roleCode);
    
    /**
     * 检查角色代码是否存在（排除指定ID）
     */
    boolean existsByRoleCodeAndIdNot(String roleCode, Long id);
    
    /**
     * 查找所有启用的角色
     */
    List<Role> findByIsActiveTrue();
    
    /**
     * 根据角色代码查找启用的角色
     */
    Optional<Role> findByRoleCodeAndIsActiveTrue(String roleCode);
    
    /**
     * 根据用户ID查找用户的所有角色
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId AND r.isActive = true")
    List<Role> findActiveRolesByUserId(@Param("userId") Long userId);
    
    /**
     * 查找所有角色及其权限（解决懒加载问题）
     */
    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions")
    List<Role> findAllWithPermissions();
    
    /**
     * 分页查找所有角色及其权限（解决懒加载问题）
     */
    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions")
    Page<Role> findAllWithPermissions(Pageable pageable);
}

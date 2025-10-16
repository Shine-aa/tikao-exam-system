package com.example.manger.repository;

import com.example.manger.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhone(String phone);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    /**
     * 检查用户名是否存在（排除指定ID）
     */
    boolean existsByUsernameAndIdNot(String username, Long id);
    
    /**
     * 检查邮箱是否存在（排除指定ID）
     */
    boolean existsByEmailAndIdNot(String email, Long id);
    
    /**
     * 检查手机号是否存在（排除指定ID）
     */
    boolean existsByPhoneAndIdNot(String phone, Long id);
    
    /**
     * 查找所有启用的用户
     */
    List<User> findByIsActiveTrue();
    
    /**
     * 统计启用的用户数量
     */
    long countByIsActiveTrue();
    
    /**
     * 根据ID查找用户及其角色和权限（解决懒加载问题）
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);
    
    /**
     * 查找所有用户及其角色（解决懒加载问题）
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles();
    
    /**
     * 分页查找所有用户及其角色（解决懒加载问题）
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    Page<User> findAllWithRoles(Pageable pageable);
    
    /**
     * 根据角色代码查找用户
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.roleCode = :roleCode AND u.isActive = true")
    List<User> findByRolesRoleCodeAndIsActiveTrue(@Param("roleCode") String roleCode);
}

package com.example.manger.repository;

import com.example.manger.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 菜单数据访问层
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    /**
     * 根据菜单代码查找菜单
     */
    Optional<Menu> findByMenuCode(String menuCode);
    
    /**
     * 检查菜单代码是否存在
     */
    boolean existsByMenuCode(String menuCode);
    
    /**
     * 根据父菜单ID查找子菜单
     */
    List<Menu> findByParentIdOrderBySortOrder(Long parentId);
    
    /**
     * 查找所有顶级菜单（父菜单ID为null）
     */
    List<Menu> findByParentIdIsNullOrderBySortOrder();
    
    /**
     * 根据是否启用查找菜单
     */
    List<Menu> findByIsActiveOrderBySortOrder(Boolean isActive);
    
    /**
     * 根据菜单类型查找菜单
     */
    List<Menu> findByMenuTypeOrderBySortOrder(String menuType);
    
    /**
     * 检查菜单代码是否存在（排除指定ID）
     */
    boolean existsByMenuCodeAndIdNot(String menuCode, Long id);
    
    /**
     * 检查是否存在子菜单
     */
    boolean existsByParentId(Long parentId);
    
    /**
     * 根据角色ID查找菜单
     */
    @Query("SELECT m FROM Menu m " +
           "JOIN RoleMenus rm ON m.id = rm.menuId " +
           "WHERE rm.roleId = :roleId AND m.isActive = true " +
           "ORDER BY m.sortOrder")
    List<Menu> findByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 查找用户的所有菜单权限
     */
    @Query("SELECT DISTINCT m FROM Menu m " +
           "JOIN RoleMenus rm ON m.id = rm.menuId " +
           "JOIN UserRoles ur ON rm.roleId = ur.roleId " +
           "WHERE ur.userId = :userId AND m.isActive = true " +
           "ORDER BY m.sortOrder")
    List<Menu> findByUserId(@Param("userId") Long userId);
}

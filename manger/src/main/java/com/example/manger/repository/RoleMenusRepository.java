package com.example.manger.repository;

import com.example.manger.entity.RoleMenus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色菜单关联数据访问层
 */
@Repository
public interface RoleMenusRepository extends JpaRepository<RoleMenus, Long> {
    
    /**
     * 根据角色ID查找所有关联的菜单ID
     */
    @Query("SELECT rm.menuId FROM RoleMenus rm WHERE rm.roleId = :roleId")
    List<Long> findMenuIdsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据角色ID删除所有关联
     */
    @Modifying
    @Query("DELETE FROM RoleMenus rm WHERE rm.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据菜单ID删除所有关联
     */
    @Modifying
    @Query("DELETE FROM RoleMenus rm WHERE rm.menuId = :menuId")
    void deleteByMenuId(@Param("menuId") Long menuId);
    
    /**
     * 检查角色菜单关联是否存在
     */
    boolean existsByRoleIdAndMenuId(Long roleId, Long menuId);
    
    /**
     * 根据角色ID和菜单ID删除关联
     */
    @Modifying
    @Query("DELETE FROM RoleMenus rm WHERE rm.roleId = :roleId AND rm.menuId = :menuId")
    void deleteByRoleIdAndMenuId(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
}

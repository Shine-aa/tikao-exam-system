package com.example.manger.service;

import com.example.manger.dto.AssignMenusRequest;
import com.example.manger.dto.MenuRequest;
import com.example.manger.dto.MenuResponse;
import com.example.manger.entity.Menu;
import com.example.manger.entity.RoleMenus;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.MenuRepository;
import com.example.manger.repository.RoleMenusRepository;
import com.example.manger.repository.RoleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 菜单服务类
 */
@Service
@Transactional
public class MenuService {
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private RoleMenusRepository roleMenusRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    /**
     * 创建菜单
     */
    public MenuResponse createMenu(MenuRequest request) {
        // 检查菜单代码是否已存在
        if (menuRepository.existsByMenuCode(request.getMenuCode())) {
            throw new BusinessException(ErrorCode.MENU_CODE_EXISTS, "菜单代码已存在");
        }
        
        // 检查父菜单是否存在
        if (request.getParentId() != null) {
            Optional<Menu> parentMenu = menuRepository.findById(request.getParentId());
            if (!parentMenu.isPresent()) {
                throw new BusinessException(ErrorCode.PARENT_MENU_NOT_FOUND, "父菜单不存在");
            }
        }
        
        Menu menu = new Menu();
        BeanUtils.copyProperties(request, menu);
        menu = menuRepository.save(menu);
        
        return convertToResponse(menu);
    }
    
    /**
     * 更新菜单
     */
    public MenuResponse updateMenu(Long id, MenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在"));
        
        // 检查菜单代码是否已被其他菜单使用
        if (!menu.getMenuCode().equals(request.getMenuCode()) && 
            menuRepository.existsByMenuCodeAndIdNot(request.getMenuCode(), id)) {
            throw new BusinessException(ErrorCode.MENU_CODE_EXISTS, "菜单代码已存在");
        }
        
        // 检查父菜单是否存在且不能是自己
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException(ErrorCode.MENU_CANNOT_BE_PARENT_OF_ITSELF, "菜单不能成为自己的父菜单");
            }
            Optional<Menu> parentMenu = menuRepository.findById(request.getParentId());
            if (!parentMenu.isPresent()) {
                throw new BusinessException(ErrorCode.PARENT_MENU_NOT_FOUND, "父菜单不存在");
            }
        }
        
        BeanUtils.copyProperties(request, menu);
        menu = menuRepository.save(menu);
        
        return convertToResponse(menu);
    }
    
    /**
     * 删除菜单
     */
    public void deleteMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在"));
        
        // 检查是否有子菜单
        if (menuRepository.existsByParentId(id)) {
            throw new BusinessException(ErrorCode.MENU_HAS_CHILDREN, "菜单存在子菜单，无法删除");
        }
        
        // 删除角色菜单关联
        roleMenusRepository.deleteByMenuId(id);
        
        menuRepository.deleteById(id);
    }
    
    /**
     * 根据ID获取菜单
     */
    public MenuResponse getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在"));
        return convertToResponse(menu);
    }
    
    /**
     * 获取所有菜单（树形结构）
     */
    public List<MenuResponse> getAllMenus() {
        List<Menu> menus = menuRepository.findAll();
        return buildMenuTree(menus, null);
    }
    
    /**
     * 获取启用的菜单（树形结构）
     */
    public List<MenuResponse> getActiveMenus() {
        List<Menu> menus = menuRepository.findByIsActiveOrderBySortOrder(true);
        return buildMenuTree(menus, null);
    }
    
    /**
     * 根据角色ID获取菜单
     */
    public List<MenuResponse> getMenusByRoleId(Long roleId) {
        List<Menu> menus = menuRepository.findByRoleId(roleId);
        return buildMenuTree(menus, null);
    }
    
    /**
     * 根据用户ID获取菜单
     */
    public List<MenuResponse> getMenusByUserId(Long userId) {
        List<Menu> menus = menuRepository.findByUserId(userId);
        return buildMenuTree(menus, null);
    }
    
    /**
     * 为角色分配菜单
     */
    public void assignMenusToRole(AssignMenusRequest request) {
        // 检查角色是否存在
        if (!roleRepository.existsById(request.getRoleId())) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND, "角色不存在");
        }
        
        // 删除原有关联
        roleMenusRepository.deleteByRoleId(request.getRoleId());
        
        // 添加新关联
        if (request.getMenuIds() != null && !request.getMenuIds().isEmpty()) {
            List<RoleMenus> roleMenus = request.getMenuIds().stream()
                    .map(menuId -> {
                        RoleMenus roleMenu = new RoleMenus();
                        roleMenu.setRoleId(request.getRoleId());
                        roleMenu.setMenuId(menuId);
                        return roleMenu;
                    })
                    .collect(Collectors.toList());
            roleMenusRepository.saveAll(roleMenus);
        }
    }
    
    /**
     * 获取角色的菜单ID列表
     */
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return roleMenusRepository.findMenuIdsByRoleId(roleId);
    }
    
    /**
     * 构建菜单树
     */
    private List<MenuResponse> buildMenuTree(List<Menu> menus, Long parentId) {
        List<MenuResponse> result = new ArrayList<>();
        
        for (Menu menu : menus) {
            if ((parentId == null && menu.getParentId() == null) || 
                (parentId != null && parentId.equals(menu.getParentId()))) {
                
                MenuResponse response = convertToResponse(menu);
                List<MenuResponse> children = buildMenuTree(menus, menu.getId());
                response.setChildren(children);
                result.add(response);
            }
        }
        
        return result;
    }
    
    /**
     * 转换为响应DTO
     */
    private MenuResponse convertToResponse(Menu menu) {
        MenuResponse response = new MenuResponse();
        BeanUtils.copyProperties(menu, response);
        
        // 设置父菜单名称
        if (menu.getParentId() != null) {
            Optional<Menu> parentMenu = menuRepository.findById(menu.getParentId());
            if (parentMenu.isPresent()) {
                response.setParentName(parentMenu.get().getMenuName());
            }
        }
        
        return response;
    }
}
import request from '../utils/request'

// ==================== 用户管理 API ====================

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  })
}

// 分页获取用户列表
export function getUserListWithPagination(params) {
  return request({
    url: '/api/admin/users/page',
    method: 'get',
    params
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/api/admin/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'delete'
  })
}

// 批量删除用户
export function batchDeleteUsers(userIds) {
  return request({
    url: '/api/admin/users/batch',
    method: 'delete',
    data: userIds
  })
}

// 切换用户状态
export function toggleUserStatus(id) {
  return request({
    url: `/api/admin/users/${id}/toggle-status`,
    method: 'put'
  })
}

// 重置用户密码
export function resetUserPassword(id, data) {
  return request({
    url: `/api/admin/users/${id}/reset-password`,
    method: 'put',
    data
  })
}

// 为用户分配角色
export function assignUserRoles(id, data) {
  return request({
    url: `/api/admin/users/${id}/assign-roles`,
    method: 'put',
    data
  })
}

// ==================== 角色管理 API ====================

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/api/admin/roles',
    method: 'get',
    params
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/api/admin/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/api/admin/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/api/admin/roles/${id}`,
    method: 'delete'
  })
}

// 切换角色状态
export function toggleRoleStatus(id) {
  return request({
    url: `/api/admin/roles/${id}/toggle-status`,
    method: 'put'
  })
}

// 为角色分配权限
export function assignRolePermissions(id, data) {
  return request({
    url: `/api/admin/roles/${id}/assign-permissions`,
    method: 'put',
    data
  })
}

// ==================== 权限管理 API ====================

// 获取权限列表
export function getPermissionList(params) {
  return request({
    url: '/api/admin/permissions',
    method: 'get',
    params
  })
}

// 创建权限
export function createPermission(data) {
  return request({
    url: '/api/admin/permissions',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/api/admin/permissions/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/api/admin/permissions/${id}`,
    method: 'delete'
  })
}

// 切换权限状态
export function togglePermissionStatus(id) {
  return request({
    url: `/api/admin/permissions/${id}/toggle-status`,
    method: 'put'
  })
}

// ==================== 菜单管理 API ====================

// 获取菜单列表
export function getMenuList(params) {
  return request({
    url: '/api/menus',
    method: 'get',
    params
  })
}

// 获取启用菜单列表
export function getActiveMenuList() {
  return request({
    url: '/api/menus/active',
    method: 'get'
  })
}

// 创建菜单
export function createMenu(data) {
  return request({
    url: '/api/menus',
    method: 'post',
    data
  })
}

// 更新菜单
export function updateMenu(id, data) {
  return request({
    url: `/api/menus/${id}`,
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `/api/menus/${id}`,
    method: 'delete'
  })
}

// 获取菜单详情
export function getMenuById(id) {
  return request({
    url: `/api/menus/${id}`,
    method: 'get'
  })
}

// 根据角色ID获取菜单
export function getMenusByRoleId(roleId) {
  return request({
    url: `/api/menus/role/${roleId}`,
    method: 'get'
  })
}

// 根据用户ID获取菜单
export function getMenusByUserId(userId) {
  return request({
    url: `/api/menus/user/${userId}`,
    method: 'get'
  })
}

// 为角色分配菜单
export function assignMenusToRole(data) {
  return request({
    url: '/api/menus/assign',
    method: 'post',
    data
  })
}

// 获取角色的菜单ID列表
export function getMenuIdsByRoleId(roleId) {
  return request({
    url: `/api/menus/role/${roleId}/ids`,
    method: 'get'
  })
}

// 角色管理分页和批处理API
// 分页获取角色列表
export function getRoleListWithPagination(params) {
  return request({
    url: '/api/admin/roles/page',
    method: 'get',
    params
  })
}

// 批量删除角色
export function batchDeleteRoles(roleIds) {
  return request({
    url: '/api/admin/roles/batch',
    method: 'delete',
    data: roleIds
  })
}

// 权限管理分页和批处理API
// 分页获取权限列表
export function getPermissionListWithPagination(params) {
  return request({
    url: '/api/admin/permissions/page',
    method: 'get',
    params
  })
}

// 批量删除权限
export function batchDeletePermissions(permissionIds) {
  return request({
    url: '/api/admin/permissions/batch',
    method: 'delete',
    data: permissionIds
  })
}
import request from '../utils/request'

// 用户注册
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/api/auth/captcha',
    method: 'get'
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/api/user/info',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 更新个人资料
export function updateProfile(data) {
  return request({
    url: '/api/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/api/user/change-password',
    method: 'post',
    data
  })
}

// 刷新token
export function refreshToken(refreshToken) {
  return request({
    url: '/api/auth/refresh',
    method: 'post',
    params: { refreshToken }
  })
}

// 获取仪表盘统计数据
export function getDashboardStats() {
  return request({
    url: '/api/admin/dashboard/stats',
    method: 'get'
  })
}

// 执行代码（Docker）
export function executeCode(data) {
  return request({
    url: '/api/code/execute',
    method: 'post',
    data
  })
}
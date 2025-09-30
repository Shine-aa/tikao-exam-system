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

// 获取图形验证码
export function getCaptcha() {
  return request({
    url: '/api/captcha/generate',
    method: 'get'
  })
}

// 发送短信验证码
export function sendSmsCode(phone) {
  return request({
    url: '/api/sms/send',
    method: 'post',
    data: { phone }
  })
}

// 短信登录
export function smsLogin(data) {
  return request({
    url: '/api/auth/sms-login',
    method: 'post',
    data
  })
}

// 刷新Token
export function refreshToken(refreshToken) {
  return request({
    url: '/api/auth/refresh',
    method: 'post',
    data: { refreshToken }
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

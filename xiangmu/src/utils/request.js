import axios from 'axios'
import { ElMessage } from 'element-plus'
import { refreshToken } from '../api/user'

// 创建axios实例
const request = axios.create({
  baseURL: '',
  timeout: 10000
})

// 是否正在刷新token
let isRefreshing = false
// 等待刷新token的请求队列
let requests = []

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { data } = response
    if (data.code === 200) {
      return data
    } else {
      // 显示具体的错误信息
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  async error => {
    // 处理400错误（业务异常）
    if (error.response?.status === 400) {
      const errorData = error.response.data
      if (errorData && errorData.message) {
        ElMessage.error(errorData.message)
        return Promise.reject(new Error(errorData.message))
      }
    }
    
    if (error.response?.status === 401) {
      // token过期，尝试使用refreshToken刷新
      const refreshTokenValue = localStorage.getItem('remember_token')
      
      if (refreshTokenValue && !isRefreshing) {
        isRefreshing = true
        
        try {
          // 调用刷新token接口
          const refreshResponse = await refreshToken(refreshTokenValue)
          
          if (refreshResponse.code === 200) {
            // 更新token
            const newToken = refreshResponse.data
            localStorage.setItem('token', newToken)
            
            // 重试之前失败的请求
            requests.forEach(cb => cb(newToken))
            requests = []
            
            // 重试当前请求
            const config = error.config
            config.headers.Authorization = `Bearer ${newToken}`
            return request(config)
          }
        } catch (refreshError) {
          // 刷新失败，清除所有存储并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          localStorage.removeItem('remember_token')
          localStorage.removeItem('remember_me')
          
          // 使用原生方式跳转，避免router依赖问题
          window.location.href = '/login'
          return Promise.reject(refreshError)
        } finally {
          isRefreshing = false
        }
      } else if (isRefreshing) {
        // 如果正在刷新，将请求加入队列
        return new Promise(resolve => {
          requests.push((token) => {
            error.config.headers.Authorization = `Bearer ${token}`
            resolve(request(error.config))
          })
        })
      } else {
        // 没有refreshToken，清除存储并跳转
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('remember_token')
        localStorage.removeItem('remember_me')
        
        window.location.href = '/login'
      }
    }
    
    // 处理其他错误
    const errorMessage = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(errorMessage)
    return Promise.reject(new Error(errorMessage))
  }
)

export default request

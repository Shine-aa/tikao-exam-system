<template>
  <div class="success-container">
    <div class="success-box">
      <div class="success-header">
        <el-icon class="success-icon" color="#67c23a" size="64">
          <CircleCheckFilled />
        </el-icon>
        <h2>登录成功！</h2>
        <p>欢迎回来，{{ userInfo.username }}</p>
      </div>
      
      <div class="user-info">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>用户信息</span>
            </div>
          </template>
          
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
          
          <div class="info-item">
            <span class="label">注册时间：</span>
            <span class="value">{{ formatDate(userInfo.createTime) }}</span>
          </div>
          
          <div class="info-item">
            <span class="label">最后登录：</span>
            <span class="value">{{ formatDate(userInfo.lastLoginTime) }}</span>
          </div>
          
          <div class="info-item">
            <span class="label">用户角色：</span>
            <span class="value">{{ userInfo.roleName || getRoleDisplayName(userInfo.role) }}</span>
          </div>
        </el-card>
      </div>
      
      <div class="action-buttons">
        <el-button type="primary" size="large" @click="goToHome">
          {{ getEnterButtonText() }}
        </el-button>
        <el-button type="danger" size="large" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheckFilled } from '@element-plus/icons-vue'
import { logout } from '../api/user'

const router = useRouter()
const userInfo = ref({
  username: '',
  email: '',
  createTime: '',
  lastLoginTime: '',
  role: '',
  roleName: ''
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '暂无'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 获取角色显示名称
const getRoleDisplayName = (role) => {
  const roleMap = {
    'SUPER_ADMIN': '超级管理员',
    'TEACHER': '老师',
    'USER': '普通用户'
  }
  return roleMap[role] || '未知角色'
}

// 获取进入系统按钮文本
const getEnterButtonText = () => {
  const userRole = userInfo.value.role
  if (userRole === 'SUPER_ADMIN') {
    return '进入管理后台'
  } else if (userRole === 'TEACHER') {
    return '进入教师界面'
  } else {
    return '进入考试界面'
  }
}

// 获取用户信息
const getUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo)
  }
}

// 进入系统
const goToHome = () => {
  // 根据用户角色跳转不同界面
  const userRole = userInfo.value.role
  if (userRole === 'SUPER_ADMIN') {
    router.push('/admin/dashboard')
  } else if (userRole === 'TEACHER') {
    router.push('/teacher/dashboard')
  } else {
    router.push('/user/exam')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用退出登录接口
    await logout()
    
    // 清除所有本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('captchaId')
    localStorage.removeItem('remember_token')
    localStorage.removeItem('remember_me')
    
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
      ElMessage.error('退出登录失败')
    }
  }
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.success-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.success-box {
  width: 600px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.success-header {
  text-align: center;
  padding: 40px;
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
}

.success-icon {
  margin-bottom: 20px;
}

.success-header h2 {
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 600;
}

.success-header p {
  margin: 0;
  font-size: 18px;
  opacity: 0.9;
}

.user-info {
  padding: 30px;
}

.info-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.info-item {
  display: flex;
  margin-bottom: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.label {
  width: 100px;
  font-weight: 600;
  color: #666;
}

.value {
  flex: 1;
  color: #333;
}

.action-buttons {
  padding: 30px;
  text-align: center;
  background: #f8f9fa;
}

.action-buttons .el-button {
  margin: 0 10px;
  min-width: 120px;
}
</style>

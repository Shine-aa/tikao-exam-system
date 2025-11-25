<template>
  <div class="teacher-layout">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="logo">
        <div class="logo-icon">钛</div>
        <div class="logo-text">钛考</div>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        @select="handleMenuSelect"
      >
        <el-menu-item index="dashboard">
          <el-icon><House /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="question-bank">
          <el-icon><Collection /></el-icon>
          <span>题库管理</span>
        </el-menu-item>
        <el-menu-item index="course-management">
          <el-icon><Reading /></el-icon>
          <span>课程管理</span>
        </el-menu-item>
        <!-- 专业管理暂时隐藏，保留代码 -->
        <!-- <el-menu-item index="major-management">
          <el-icon><Reading /></el-icon>
          <span>专业管理</span>
        </el-menu-item> -->
        <el-menu-item index="class-management">
          <el-icon><School /></el-icon>
          <span>班级管理</span>
        </el-menu-item>
        <el-menu-item index="exam-grouping">
          <el-icon><Document /></el-icon>
          <span>组卷管理</span>
        </el-menu-item>
        <el-menu-item index="exam-management">
          <el-icon><List /></el-icon>
          <span>考试管理</span>
        </el-menu-item>
        <el-menu-item index="student-management">
          <el-icon><User /></el-icon>
          <span>学生管理</span>
        </el-menu-item>
        <el-menu-item index="score-analysis">
          <el-icon><TrendCharts /></el-icon>
          <span>成绩分析</span>
        </el-menu-item>
        <el-menu-item index="monitoring">
          <el-icon><View /></el-icon>
          <span>监考中心</span>
        </el-menu-item>
        <el-menu-item index="settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <div class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-icon><User /></el-icon>
              <span>{{ userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="main-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House, 
  Collection, 
  Reading,
  School,
  Document, 
  List, 
  User, 
  TrendCharts, 
  View, 
  Setting,
  ArrowDown
} from '@element-plus/icons-vue'
import { logout } from '../api/user'

const router = useRouter()
const route = useRoute()

const userInfo = ref({
  username: '',
  email: '',
  role: ''
})

// 当前激活的菜单
const activeMenu = ref('dashboard')

// 当前页面标题
const currentPageTitle = computed(() => {
  const titleMap = {
    'dashboard': '仪表盘',
    'question-bank': '题库管理',
    'course-management': '课程管理',
    'major-management': '专业管理', // 保留代码，暂时隐藏
    'class-management': '班级管理',
    'exam-grouping': '组卷管理',
    'exam-management': '考试管理',
    'student-management': '学生管理',
    'score-analysis': '成绩分析',
    'monitoring': '监考中心',
    'settings': '系统设置'
  }
  return titleMap[activeMenu.value] || '仪表盘'
})

// 获取用户信息
const getUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo)
  }
}

// 根据当前路由设置激活菜单
const setActiveMenuFromRoute = () => {
  const currentPath = route.path
  if (currentPath.includes('/teacher/course-management')) {
    activeMenu.value = 'course-management'
  } else if (currentPath.includes('/teacher/major-management')) {
    activeMenu.value = 'major-management'
  } else if (currentPath.includes('/teacher/class-management')) {
    activeMenu.value = 'class-management'
  } else if (currentPath.includes('/teacher/question-bank')) {
    activeMenu.value = 'question-bank'
  } else if (currentPath.includes('/teacher/exam-grouping')) {
    activeMenu.value = 'exam-grouping'
  } else if (currentPath.includes('/teacher/exam-management')) {
    activeMenu.value = 'exam-management'
  } else if (currentPath.includes('/teacher/student-management')) {
    activeMenu.value = 'student-management'
  } else if (currentPath.includes('/teacher/score-analysis')) {
    activeMenu.value = 'score-analysis'
  } else if (currentPath.includes('/teacher/dashboard')) {
    activeMenu.value = 'dashboard'
  }
  // 可以根据需要添加更多路由判断
}

// 菜单选择处理
const handleMenuSelect = (index) => {
  activeMenu.value = index
  
  // 根据菜单项跳转到对应页面
  const routeMap = {
    'dashboard': '/teacher/dashboard',
    'question-bank': '/teacher/question-bank',
    'course-management': '/teacher/course-management',
    'major-management': '/teacher/major-management',
    'class-management': '/teacher/class-management',
    'exam-grouping': '/teacher/exam-grouping',
    'exam-management': '/teacher/exam-management',
    'student-management': '/teacher/student-management',
    'score-analysis': '/teacher/score-analysis',
    'monitoring': '/teacher/monitoring',
    'settings': '/teacher/settings'
  }
  
  const targetRoute = routeMap[index]
  if (targetRoute) {
    router.push(targetRoute)
  }
}

// 用户下拉菜单处理
const handleUserCommand = async (command) => {
  if (command === 'profile') {
    ElMessage.info('个人设置功能开发中...')
  } else if (command === 'logout') {
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
}

onMounted(() => {
  getUserInfo()
  setActiveMenuFromRoute()
})

// 监听路由变化
watch(() => route.path, () => {
  setActiveMenuFromRoute()
})
</script>

<style scoped>
.teacher-layout {
  height: 100vh;
  display: flex;
}

/* 侧边栏样式 */
.sidebar {
  width: 200px;
  background-color: #304156;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
  color: white;
  gap: 8px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
}

.sidebar-menu {
  flex: 1;
  border: none;
  padding: 0;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  padding: 0 20px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #263445 !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #263445 !important;
  border-right: 3px solid #409EFF;
}

/* 主内容区样式 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
  gap: 5px;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.main-content {
  flex: 1;
  background-color: #f5f5f5;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 60px;
  }
  
  .logo-text {
    display: none;
  }
  
  .sidebar-menu .el-menu-item span {
    display: none;
  }
}
</style>

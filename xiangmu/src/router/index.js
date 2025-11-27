import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Success from '../views/Success.vue'
import AdminLayout from '../views/AdminLayout.vue'
import Dashboard from '../views/admin/Dashboard.vue'
import UserManagement from '../views/admin/UserManagement.vue'
import RoleManagement from '../views/admin/RoleManagement.vue'
import PermissionManagement from '../views/admin/PermissionManagement.vue'
import MenuManagement from '../views/admin/MenuManagement.vue'
import UserLayout from '../views/UserLayout.vue'
import Profile from '../views/user/Profile.vue'
import StudentDashboard from '../views/StudentDashboard.vue'
import TeacherLayout from '../views/TeacherLayout.vue'
import TeacherDashboard from '../views/TeacherDashboard.vue'
import QuestionBankManagement from '../views/teacher/QuestionBankManagement.vue'
import GradingInterface from '../views/teacher/GradingInterface.vue'
import SwaggerTest from '../views/SwaggerTest.vue'
import { getUserInfo } from '../api/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/success',
    name: 'Success',
    component: Success,
    meta: { requiresAuth: true }
  },
  {
    path: '/swagger',
    name: 'SwaggerTest',
    component: SwaggerTest
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, role: 'SUPER_ADMIN' },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { requiresAuth: true, role: 'SUPER_ADMIN' }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: UserManagement,
        meta: { requiresAuth: true, role: 'SUPER_ADMIN' }
      },
      {
        path: 'class-management',
        name: 'AdminClassManagement',
        // 修正后（相对路径，和路由文件位置对应）
        component: () => import('../views/teacher/ClassManagement.vue'),
        meta: { requiresAuth: true, role: 'SUPER_ADMIN'}
      },
      {
        path: 'roles',
        name: 'RoleManagement',
        component: RoleManagement,
        meta: { requiresAuth: true, role: 'SUPER_ADMIN' }
      },
      {
        path: 'permissions',
        name: 'PermissionManagement',
        component: PermissionManagement,
        meta: { requiresAuth: true, role: 'SUPER_ADMIN' }
      },
      {
        path: 'menus',
        name: 'MenuManagement',
        component: MenuManagement,
        meta: { requiresAuth: true, role: 'SUPER_ADMIN' }
      },
    ]
  },
  {
    path: '/user',
    component: UserLayout,
    meta: { requiresAuth: true, role: 'USER' },
    children: [
      {
        path: '',
        redirect: '/user/exam'
      },
      {
        path: 'exam',
        name: 'StudentDashboard',
        component: StudentDashboard,
        meta: { requiresAuth: true, role: 'USER' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { requiresAuth: true, role: 'USER' }
      },
      {
        path: 'exam/:id/info',
        name: 'ExamInfo',
        component: () => import('@/views/student/ExamInfo.vue'),
        meta: { requiresAuth: true, role: 'USER' }
      },
      {
        path: 'exam/:id/preview',
        name: 'ExamPreview',
        component: () => import('@/views/student/ExamPreview.vue'),
        meta: { requiresAuth: true, role: 'USER' }
      },
      {
        path: 'exam/:id/result',
        name: 'ExamResult',
        component: () => import('@/views/student/ExamResult.vue'),
        meta: { requiresAuth: true, role: 'USER' }
      },
      {
        path: 'exam/:examId/review',
        name: 'ExamReview',
        component: () => import('@/views/student/ExamReview.vue'),
        meta: { requiresAuth: true, role: 'USER' }
      }
    ]
  },
  {
    path: '/teacher',
    component: TeacherLayout,
    meta: { requiresAuth: true, role: ['TEACHER'] },
    children: [
      {
        path: '',
        redirect: '/teacher/dashboard'
      },
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: TeacherDashboard,
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'question-bank',
        name: 'QuestionBankManagement',
        component: QuestionBankManagement,
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'course-management',
        name: 'CourseManagement',
        component: () => import('@/views/teacher/CourseManagement.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'course-question-bank',
        name: 'CourseQuestionBank',
        component: () => import('@/views/teacher/CourseQuestionBank.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'major-management',
        name: 'MajorManagement',
        component: () => import('@/views/teacher/MajorManagement.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'class-management',
        name: 'ClassManagement',
        component: () => import('@/views/teacher/ClassManagement.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'student-management',
        name: 'StudentManagement',
        component: () => import('@/views/teacher/StudentManagement.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'exam-grouping',
        name: 'ExamGrouping',
        component: () => import('@/views/teacher/ExamGrouping.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'manual-paper-grouping',
        name: 'ManualPaperGrouping',
        component: () => import('@/views/teacher/ManualPaperGrouping.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'exam-management',
        name: 'ExamManagement',
        component: () => import('@/views/teacher/ExamManagement.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'score-analysis',
        name: 'GradingAnalysis',
        component: () => import('@/views/teacher/GradingAnalysis.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'score-analysis/students/:examId',
        name: 'StudentSelection',
        component: () => import('@/views/teacher/StudentSelection.vue'),
        meta: { requiresAuth: true, role: 'TEACHER' }
      },
      {
        path: 'score-analysis/:examId/student/:studentId',
        name: 'GradingInterface',
        component: GradingInterface,
        meta: { requiresAuth: true, role: 'TEACHER' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 检查登录状态的函数
const checkAuthStatus = async () => {
  const token = localStorage.getItem('token')
  const rememberMe = localStorage.getItem('remember_me')
  const rememberToken = localStorage.getItem('remember_token')
  
  // 如果有普通token，直接返回true
  if (token) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    return { isAuthenticated: true, tokenType: 'access', role: userInfo.role }
  }
  
  // 如果选择了记住我且有refreshToken，尝试自动登录
  if (rememberMe === 'true' && rememberToken) {
    try {
      const response = await getUserInfo()
      if (response.code === 200) {
        // 自动登录成功，返回true
        return { isAuthenticated: true, tokenType: 'refresh', role: response.data.role }
      }
    } catch (error) {
      // 自动登录失败，清除记住我相关存储
      localStorage.removeItem('remember_token')
      localStorage.removeItem('remember_me')
    }
  }
  
  return { isAuthenticated: false, tokenType: null, role: null }
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 如果访问登录页面，检查是否已经登录
  if (to.path === '/login') {
    const authStatus = await checkAuthStatus()
    if (authStatus.isAuthenticated) {
      // 已登录，根据角色跳转
      if (authStatus.role === 'SUPER_ADMIN') {
        next('/admin/dashboard')
      } else if (authStatus.role === 'TEACHER') {
        next('/teacher/dashboard')
      } else {
        next('/user/exam')
      }
      return
    }
    next()
    return
  }
  
  // 如果访问需要认证的页面
  if (to.meta.requiresAuth) {
    const authStatus = await checkAuthStatus()
    if (authStatus.isAuthenticated) {
      // 检查角色权限
      if (to.meta.role && authStatus.role !== to.meta.role) {
        // 角色不匹配，跳转到对应角色的首页
        if (authStatus.role === 'SUPER_ADMIN') {
          next('/admin/dashboard')
        } else if (authStatus.role === 'TEACHER') {
          next('/teacher/dashboard')
        } else {
          next('/user/exam')
        }
        return
      }
      // 已认证且角色匹配，允许访问
      next()
    } else {
      // 未认证，跳转到登录页
      next('/login')
    }
  } else {
    next()
  }
})

export default router

<template>
  <div class="student-dashboard">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-card">
          <div class="welcome-content">
            <h1>欢迎回来，{{ userInfo.username }}！</h1>
            <p>准备好开始今天的学习和考试了吗？</p>
          </div>
          <div class="welcome-avatar">
            <div class="avatar-icon">👨‍🎓</div>
          </div>
        </div>
      </div>

      <!-- 考试状态卡片 -->
      <div class="exam-cards">
        <div class="exam-card">
          <div class="card-title">
            <span class="card-icon">📝</span>
            <span>我的考试</span>
          </div>
          <div class="exam-tabs">
            <div class="tab-item" :class="{ active: currentTab === 'ongoing' }" @click="switchTab('ongoing')">
              <span class="tab-count">{{ ongoingExams.length }}</span>
              <span class="tab-label">进行中</span>
            </div>
            <div class="tab-item" :class="{ active: currentTab === 'upcoming' }" @click="switchTab('upcoming')">
              <span class="tab-count">{{ upcomingExams.length }}</span>
              <span class="tab-label">即将开始</span>
            </div>
            <div class="tab-item" :class="{ active: currentTab === 'completed' }" @click="switchTab('completed')">
              <span class="tab-count">{{ completedExams.length }}</span>
              <span class="tab-label">已完成</span>
            </div>
          </div>
        </div>

        <div class="exam-card">
          <div class="card-title">
            <span class="card-icon">⚡</span>
            <span>快速操作</span>
          </div>
          <div class="quick-actions">
            <div class="action-btn" @click="startExam">
              <div class="action-icon">🚀</div>
              <div class="action-text">开始考试</div>
            </div>
            <div class="action-btn" @click="viewResults">
              <div class="action-icon">📊</div>
              <div class="action-text">查看成绩</div>
            </div>
            <div class="action-btn" @click="viewMaterials">
              <div class="action-icon">📚</div>
              <div class="action-text">学习资料</div>
            </div>
            <div class="action-btn" @click="viewHistory">
              <div class="action-icon">📋</div>
              <div class="action-text">考试历史</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 考试列表 -->
      <div class="exam-list-section">
        <div class="exam-list-card">
          <div class="card-title">
            <span class="card-icon">📋</span>
            <span>{{ getCurrentTabTitle() }}</span>
          </div>
          <div class="exam-list" v-loading="loading">
            <div 
              v-for="exam in getCurrentExams()" 
              :key="exam.id" 
              class="exam-item"
              @click="handleExamClick(exam)"
            >
              <div class="exam-header">
                <div class="exam-title">{{ exam.examName }}</div>
                <div class="exam-status" :class="getStatusClass(exam.status)">
                  {{ getStatusText(exam.status) }}
                </div>
              </div>
              <div class="exam-info">
                <span>📅 {{ formatExamTime(exam.startTime) }}</span>
                <span>⏱️ {{ exam.durationMinutes }}分钟</span>
                <span>📊 {{ exam.totalScore || '未评分' }}</span>
              </div>
              <div class="exam-description">{{ exam.description }}</div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="getCurrentExams().length === 0" class="empty-state">
              <div class="empty-icon">📝</div>
              <div class="empty-text">暂无{{ getCurrentTabTitle() }}的考试</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习统计 -->
      <div class="stats-section">
        <div class="stats-card">
          <div class="card-title">
            <span class="card-icon">📈</span>
            <span>学习统计</span>
          </div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ stats.totalExams }}</div>
              <div class="stat-label">总考试数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.avgScore }}%</div>
              <div class="stat-label">平均分</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.studyDays }}</div>
              <div class="stat-label">学习天数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.rank }}</div>
              <div class="stat-label">班级排名</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最新通知 -->
      <div class="notifications-section">
        <div class="notifications-card">
          <div class="card-title">
            <span class="card-icon">🔔</span>
            <span>最新通知</span>
          </div>
          <div class="notifications-list">
            <div 
              v-for="notification in notifications" 
              :key="notification.id" 
              class="notification-item"
            >
              <div class="notification-icon">{{ notification.icon }}</div>
              <div class="notification-content">
                <div class="notification-title">{{ notification.title }}</div>
                <div class="notification-time">{{ notification.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习建议 -->
      <div class="suggestions-section">
        <div class="suggestions-card">
          <div class="card-title">
            <span class="card-icon">💡</span>
            <span>学习建议</span>
          </div>
          <div class="suggestions-list">
            <div class="suggestion-item">
              <div class="suggestion-icon">📚</div>
              <div class="suggestion-text">建议每天至少学习2小时，保持学习节奏</div>
            </div>
            <div class="suggestion-item">
              <div class="suggestion-icon">🎯</div>
              <div class="suggestion-text">重点关注错题，及时复习巩固</div>
            </div>
            <div class="suggestion-item">
              <div class="suggestion-icon">⏰</div>
              <div class="suggestion-text">合理安排考试时间，避免临时抱佛脚</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { studentExamApi } from '@/api/admin'

const router = useRouter()

// 用户信息
const userInfo = ref({
  username: '同学们'
})

// 当前选中的标签页
const currentTab = ref('ongoing')

// 加载状态
const loading = ref(false)

// 考试数据
const ongoingExams = ref([])
const upcomingExams = ref([])
const completedExams = ref([])

// 统计数据
const stats = ref({
  totalExams: 0,
  avgScore: 0,
  studyDays: 0,
  rank: 0
})

// 通知数据
const notifications = ref([])

// 加载考试数据
const loadExamData = async () => {
  try {
    loading.value = true
    const response = await studentExamApi.getStudentExams()
    
    if (response.code === 200) {
      const data = response.data
      ongoingExams.value = data.ongoing || []
      upcomingExams.value = data.upcoming || []
      completedExams.value = data.completed || []
    } else {
      ElMessage.error(response.message || '加载考试数据失败')
    }
  } catch (error) {
    console.error('Load exam data error:', error)
    ElMessage.error('加载考试数据失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await studentExamApi.getStudentExamStats()
    
    if (response.code === 200) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('Load stats error:', error)
  }
}

// 切换标签页
const switchTab = (tab) => {
  currentTab.value = tab
}

// 获取当前标签页的标题
const getCurrentTabTitle = () => {
  const titles = {
    ongoing: '进行中的考试',
    upcoming: '即将开始的考试',
    completed: '已完成的考试'
  }
  return titles[currentTab.value] || '考试列表'
}

// 获取当前标签页的考试列表
const getCurrentExams = () => {
  const examMap = {
    ongoing: ongoingExams.value,
    upcoming: upcomingExams.value,
    completed: completedExams.value
  }
  return examMap[currentTab.value] || []
}

// 获取状态样式类
const getStatusClass = (status) => {
  const statusClassMap = {
    'ONGOING': 'status-ongoing',
    'SCHEDULED': 'status-upcoming',
    'COMPLETED': 'status-completed',
    'CANCELLED': 'status-cancelled'
  }
  return statusClassMap[status] || 'status-default'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'ONGOING': '进行中',
    'SCHEDULED': '即将开始',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化考试时间（简洁的日期时间格式）
const formatExamTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 处理考试点击
const handleExamClick = async (exam) => {
  if (exam.status === 'COMPLETED') {
    // 已完成的考试直接进入成绩页面
    router.push(`/user/exam/${exam.id}/result`)
  } else if (exam.status === 'ONGOING' || exam.status === 'SCHEDULED') {
    // 进行中和已安排的考试可以进入考试信息页面
    router.push(`/user/exam/${exam.id}/info`)
  } else if (exam.status === 'CANCELLED') {
    // 已取消的考试
    ElMessage.warning('该考试已取消')
  } else {
    // 其他状态，也进入考试信息页面
    router.push(`/user/exam/${exam.id}/info`)
  }
}

// 快速操作
const startExam = () => {
  ElMessage.info('开始考试功能')
}

const viewResults = () => {
  ElMessage.info('查看成绩功能')
}

const viewMaterials = () => {
  ElMessage.info('学习资料功能')
}

const viewHistory = () => {
  ElMessage.info('考试历史功能')
}

// 组件挂载
onMounted(() => {
  // 获取用户信息
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo)
  }
  
  // 加载数据
  loadExamData()
  loadStats()
})
</script>

<style scoped>
.student-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 20px;
}

.welcome-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-content h1 {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.welcome-content p {
  font-size: 16px;
  color: #606266;
}

.welcome-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
}

/* 考试卡片 */
.exam-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.exam-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-icon {
  font-size: 20px;
}

/* 考试标签页 */
.exam-tabs {
  display: flex;
  gap: 12px;
}

.tab-item {
  flex: 1;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-item:hover {
  background: #e3f2fd;
  border-color: #409EFF;
}

.tab-item.active {
  background: #409EFF;
  border-color: #409EFF;
  color: white;
}

.tab-count {
  display: block;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.tab-label {
  font-size: 14px;
  font-weight: 500;
}

/* 快速操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.action-icon {
  font-size: 24px;
}

.action-text {
  font-size: 14px;
}

/* 考试列表 */
.exam-list-section {
  grid-column: 1 / -1;
}

.exam-list-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.exam-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.exam-item {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
  cursor: pointer;
}

.exam-item:hover {
  background: #e3f2fd;
  border-color: #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.exam-title {
  font-weight: bold;
  color: #303133;
  font-size: 16px;
}

.exam-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.status-ongoing {
  background: #e8f5e8;
  color: #67c23a;
}

.status-upcoming {
  background: #fff3cd;
  color: #e6a23c;
}

.status-completed {
  background: #f0f0f0;
  color: #909399;
}

.status-cancelled {
  background: #fef0f0;
  color: #f56c6c;
}

.status-default {
  background: #f4f4f5;
  color: #909399;
}

.exam-info {
  font-size: 14px;
  color: #606266;
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.exam-description {
  font-size: 14px;
  color: #909399;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
}

/* 统计和学习建议 */
.stats-section,
.notifications-section,
.suggestions-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.stats-card,
.notifications-card,
.suggestions-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

/* 通知列表 */
.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.notification-icon {
  font-size: 20px;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

/* 学习建议 */
.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.suggestion-icon {
  font-size: 20px;
}

.suggestion-text {
  color: #606266;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-cards {
    grid-template-columns: 1fr;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .welcome-card {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
}
</style>

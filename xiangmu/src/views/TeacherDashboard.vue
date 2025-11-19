<template>
  <div class="teacher-dashboard">
    <div class="welcome-card">
      <h1 class="welcome-title">欢迎使用钛考在线考试系统</h1>
      <p class="welcome-subtitle">重塑在线考试的信任基石，打造坚不可摧的智能评测平台</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-number">{{ stats.questionBankCount }}</div>
        <div class="stat-label">我管理的题库总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.ongoingExams }}</div>
        <div class="stat-label">进行中考试</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.studentCount }}</div>
        <div class="stat-label">学生总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.completedExams }}</div>
        <div class="stat-label">已完成考试</div>
      </div>
    </div>

    <div class="quick-actions">
      <h3>快速操作</h3>
      <div class="action-buttons">
        <el-button type="success" @click="handleQuestionBank">题库管理</el-button>
        <el-button type="warning" @click="handleSmartGrouping">智能组卷</el-button>
        <el-button type="success" @click="handleScoreAnalysis">成绩分析</el-button>
        <el-button type="warning" @click="handleStudentManagement">学生管理</el-button>
      </div>
    </div>

    <div class="recent-activities">
      <h3>最近活动</h3>
      <el-card>
        <el-timeline>
          <el-timeline-item
            v-for="activity in recentActivities"
            :key="activity.id"
            :timestamp="activity.time"
            :type="activity.type"
          >
            {{ activity.content }}
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { examApi } from '@/api/admin'

const router = useRouter()

// 统计数据
const stats = ref({
  questionBankCount: 0,
  ongoingExams: 0,
  studentCount: 0,
  completedExams: 0
})

// 最近活动
const recentActivities = ref([])

// 加载状态
const loading = ref(false)

// 快速操作处理函数
const handleQuestionBank = () => {
  router.push('/teacher/question-bank')
}

const handleSmartGrouping = () => {
  router.push('/teacher/exam-grouping')
}

const handleScoreAnalysis = () => {
  router.push('/teacher/score-analysis')
}

const handleStudentManagement = () => {
  router.push('/teacher/student-management')
}

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    loading.value = true
    
    // 并行加载统计数据和最近活动
    const [statsResponse, activitiesResponse] = await Promise.all([
      examApi.getDashboardStats(),
      examApi.getRecentActivities()
    ])
    
    if (statsResponse.code === 200) {
      stats.value = statsResponse.data
    }
    
    if (activitiesResponse.code === 200) {
      recentActivities.value = activitiesResponse.data
    }
    
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载仪表盘数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.teacher-dashboard {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.welcome-card {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.welcome-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.welcome-subtitle {
  color: #606266;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.quick-actions {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.quick-actions h3 {
  margin-bottom: 15px;
  color: #303133;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.action-buttons .el-button {
  height: 40px;
}

.recent-activities {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.recent-activities h3 {
  margin-bottom: 15px;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .action-buttons {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    grid-template-columns: 1fr;
  }
}
</style>

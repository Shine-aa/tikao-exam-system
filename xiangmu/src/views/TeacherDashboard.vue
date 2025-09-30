<template>
  <div class="teacher-dashboard">
    <div class="welcome-card">
      <h1 class="welcome-title">欢迎使用钛考在线考试系统</h1>
      <p class="welcome-subtitle">重塑在线考试的信任基石，打造坚不可摧的智能评测平台</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-number">{{ stats.questionBankCount }}</div>
        <div class="stat-label">题库总数</div>
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
        <el-button type="primary" @click="handleCreateExam">创建考试</el-button>
        <el-button type="success" @click="handleQuestionBank">题库管理</el-button>
        <el-button type="warning" @click="handleSmartGrouping">智能组卷</el-button>
        <el-button type="primary" @click="handleMonitoring">实时监考</el-button>
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

// 统计数据
const stats = ref({
  questionBankCount: 156,
  ongoingExams: 23,
  studentCount: 1234,
  completedExams: 89
})

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    content: '创建了新的数学考试',
    time: '2025-01-15 14:30',
    type: 'primary'
  },
  {
    id: 2,
    content: '批改了英语考试试卷',
    time: '2025-01-15 10:20',
    type: 'success'
  },
  {
    id: 3,
    content: '添加了新的物理题库',
    time: '2025-01-14 16:45',
    type: 'warning'
  },
  {
    id: 4,
    content: '监考了化学考试',
    time: '2025-01-14 09:15',
    type: 'info'
  }
])

// 快速操作处理函数
const handleCreateExam = () => {
  ElMessage.info('创建考试功能开发中...')
}

const handleQuestionBank = () => {
  ElMessage.info('题库管理功能开发中...')
}

const handleSmartGrouping = () => {
  ElMessage.info('智能组卷功能开发中...')
}

const handleMonitoring = () => {
  ElMessage.info('实时监考功能开发中...')
}

const handleScoreAnalysis = () => {
  ElMessage.info('成绩分析功能开发中...')
}

const handleStudentManagement = () => {
  ElMessage.info('学生管理功能开发中...')
}

onMounted(() => {
  // 可以在这里加载实际的数据
  loadDashboardData()
})

const loadDashboardData = () => {
  // 模拟加载数据
  console.log('加载老师端仪表盘数据...')
}
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

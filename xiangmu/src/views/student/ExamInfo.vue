<template>
  <div class="exam-info-page">
    <div class="exam-info-container">
      <!-- 考试信息卡片 -->
      <div class="exam-info-card">
        <div class="exam-header">
          <h1 class="exam-title">{{ examInfo.examName }}</h1>
        </div>
        
        <div class="exam-details">
          <div class="detail-row">
            <span class="label">考试时长：</span>
            <span class="value">{{ examInfo.durationMinutes }} 分钟</span>
          </div>
          <div class="detail-row">
            <span class="label">开始时间：</span>
            <span class="value">{{ formatDateTime(examInfo.startTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">结束时间：</span>
            <span class="value">{{ formatDateTime(examInfo.endTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">试卷名称：</span>
            <span class="value">{{ examInfo.paperName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">题目数量：</span>
            <span class="value">{{ examInfo.totalQuestions }} 题</span>
          </div>
          <div class="detail-row">
            <span class="label">总分：</span>
            <span class="value">{{ examInfo.totalPoints }} 分</span>
          </div>
        </div>

        <div v-if="examInfo.description" class="exam-description">
          <h3>考试说明</h3>
          <p>{{ examInfo.description }}</p>
        </div>
      </div>

      <!-- 考试注意事项 -->
      <div class="exam-notices-card">
        <h2>考试注意事项</h2>
        <div class="notices-list">
          <div class="notice-item">
            <el-icon><Warning /></el-icon>
            <span>考试期间请保持网络连接稳定，避免中途断网导致考试中断</span>
          </div>
          <div class="notice-item">
            <el-icon><Warning /></el-icon>
            <span>考试过程中请勿刷新页面或关闭浏览器，否则可能导致考试数据丢失</span>
          </div>
          <div class="notice-item">
            <el-icon><Warning /></el-icon>
            <span>考试时间结束后系统将自动提交试卷，请合理安排答题时间</span>
          </div>
          <div class="notice-item">
            <el-icon><Warning /></el-icon>
            <span>考试过程中如遇到技术问题，请及时联系监考老师</span>
          </div>
          <div class="notice-item">
            <el-icon><Warning /></el-icon>
            <span>请确保在安静的环境中参加考试，避免外界干扰</span>
          </div>
        </div>

        <!-- 确认选项 -->
        <div class="confirmation-section">
          <el-checkbox v-model="agreedToTerms" size="large">
            我已阅读并同意上述考试注意事项
          </el-checkbox>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button size="large" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-button 
          v-if="canStartExam || examInfo.status === 'COMPLETED'"
          :type="canStartExam ? 'primary' : 'success'"
          size="large" 
          :disabled="canStartExam && !agreedToTerms"
          :loading="loading"
          @click="handleButtonClick"
        >
          <el-icon v-if="canStartExam"><VideoPlay /></el-icon>
          <el-icon v-else><View /></el-icon>
          {{ getButtonText() }}
        </el-button>
        <el-button 
          v-else
          type="warning" 
          size="large" 
          disabled
        >
          <el-icon><Clock /></el-icon>
          {{ getButtonText() }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Warning, ArrowLeft, VideoPlay, View, Clock } from '@element-plus/icons-vue'
import { studentExamApi } from '@/api/admin'
import serverTimeSync from '@/utils/serverTime'
import { enterFullscreen } from '@/utils/fullscreenMonitor'

const route = useRoute()
const router = useRouter()

const examInfo = ref({})
const agreedToTerms = ref(false)
const loading = ref(false)

// 计算是否可以开始考试（使用服务器时间）
const canStartExam = computed(() => {
  if (!examInfo.value) return false
  
  const status = examInfo.value.status
  const studentStatus = examInfo.value.studentExamStatus
  const now = serverTimeSync.isSynced() ? serverTimeSync.getServerTime() : new Date()
  const startTime = new Date(examInfo.value.startTime)
  const endTime = new Date(examInfo.value.endTime)
  
  // 如果学生考试已提交或已评分，不能开始考试
  if (studentStatus === 'SUBMITTED' || studentStatus === 'GRADED') {
    return false
  }
  
  // 考试状态必须是SCHEDULED或ONGOING，且在考试时间范围内
  return (status === 'SCHEDULED' || status === 'ONGOING') &&
         now >= startTime && now <= endTime
})

// 格式化日期时间（简洁版）
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  const date = new Date(dateTimeString)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 加载考试信息
const loadExamInfo = async () => {
  try {
    const examId = route.params.id
    const response = await studentExamApi.getStudentExamDetail(examId)
    
    if (response.code === 200) {
      examInfo.value = response.data
    } else {
      ElMessage.error(response.message || '获取考试信息失败')
      goBack()
    }
  } catch (error) {
    console.error('Load exam info error:', error)
    ElMessage.error('获取考试信息失败')
    goBack()
  }
}

// 处理按钮点击
const handleButtonClick = async () => {
  if (!examInfo.value) return
  
  const status = examInfo.value.status
  const studentStatus = examInfo.value.studentExamStatus
  const now = serverTimeSync.isSynced() ? serverTimeSync.getServerTime() : new Date()
  const startTime = new Date(examInfo.value.startTime)
  const endTime = new Date(examInfo.value.endTime)
  
  // 根据学生考试状态优先判断
  if (studentStatus === 'IN_PROGRESS') {
    await startExam()
  } else if (studentStatus === 'SUBMITTED' || studentStatus === 'GRADED') {
    viewResults()
  } else if (canStartExam.value) {
    await startExam()
  } else if (status === 'COMPLETED') {
    viewResults()
  } else {
    // 其他状态不执行任何操作
    ElMessage.info(getButtonText())
  }
}

// 开始考试
const startExam = async () => {
  if (!agreedToTerms.value) {
    ElMessage.warning('请先同意考试注意事项')
    return
  }

  try {
    loading.value = true
    const examId = route.params.id
    
    // 尝试进入全屏
    const fullscreenSuccess = await enterFullscreen()
    if (!fullscreenSuccess) {
      ElMessage.warning('无法进入全屏模式，请手动按F11进入全屏，否则可能影响考试')
    }
    
    await studentExamApi.startStudentExam(examId)
    
    ElMessage.success('考试已开始')
    // 跳转到整卷预览模式
    router.push(`/user/exam/${examId}/preview`)
  } catch (error) {
    console.error('Start exam error:', error)
    ElMessage.error('开始考试失败')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 获取按钮文本
const getButtonText = () => {
  if (!examInfo.value) return '加载中...'
  
  const status = examInfo.value.status
  const studentStatus = examInfo.value.studentExamStatus
  const now = serverTimeSync.isSynced() ? serverTimeSync.getServerTime() : new Date()
  const startTime = new Date(examInfo.value.startTime)
  const endTime = new Date(examInfo.value.endTime)
  
  // 根据学生考试状态优先判断
  if (studentStatus === 'IN_PROGRESS') {
    return '继续考试'
  } else if (studentStatus === 'SUBMITTED') {
    return '查看成绩'
  } else if (studentStatus === 'GRADED') {
    return '查看成绩'
  }
  
  // 根据考试状态判断
  switch (status) {
    case 'DRAFT':
      return '考试未发布'
    case 'SCHEDULED':
      if (now < startTime) {
        return '考试未开始'
      } else if (now >= startTime && now <= endTime) {
        return '开始考试'
      } else {
        return '考试已结束'
      }
    case 'ONGOING':
      if (now > endTime) {
        return '考试已结束'
      } else {
        return '开始考试'
      }
    case 'COMPLETED':
      return '查看成绩'
    case 'CANCELLED':
      return '考试已取消'
    default:
      return '考试状态异常'
  }
}

// 查看成绩
const viewResults = () => {
  const examId = route.params.id
  router.push(`/user/exam/${examId}/result`)
}

onMounted(async () => {
  // 初始化服务器时间同步
  await serverTimeSync.init().catch(error => {
    console.error('服务器时间同步初始化失败:', error)
  })
  loadExamInfo()
})
</script>

<style scoped>
.exam-info-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.exam-info-container {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.exam-info-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.exam-header {
  text-align: center;
  margin-bottom: 32px;
}

.exam-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}


.exam-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.detail-row {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.label {
  font-weight: 500;
  color: #606266;
  margin-right: 8px;
  min-width: 80px;
}

.value {
  color: #303133;
  font-weight: 500;
}

.exam-description {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}

.exam-description h3 {
  color: #303133;
  margin-bottom: 12px;
}

.exam-description p {
  color: #606266;
  line-height: 1.6;
}

.exam-notices-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.exam-notices-card h2 {
  color: #303133;
  margin-bottom: 24px;
  text-align: center;
}

.notices-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 8px;
}

.notice-item .el-icon {
  color: #fa8c16;
  font-size: 18px;
  margin-top: 2px;
  flex-shrink: 0;
}

.notice-item span {
  color: #d46b08;
  line-height: 1.5;
}

.confirmation-section {
  text-align: center;
  padding: 24px;
  background: #f8f9fa;
  border-radius: 8px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.action-buttons .el-button {
  min-width: 120px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-details {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>

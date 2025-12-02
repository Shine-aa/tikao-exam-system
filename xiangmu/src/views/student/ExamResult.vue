<template>
  <div class="exam-result-page">
    <div class="exam-result-container">
      <!-- 考试信息卡片 -->
      <div class="exam-info-card">
        <div class="exam-header">
          <h1 class="exam-title">{{ examInfo.examName }}</h1>
          <p class="exam-subtitle">{{ examInfo.className }} - {{ examInfo.paperName }}</p>
        </div>
        
        <div class="exam-details">
          <div class="detail-row">
            <span class="label">考试时间：</span>
            <span class="value">{{ formatDateTime(examInfo.startTime) }} - {{ formatDateTime(examInfo.endTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">交卷时间：</span>
            <span class="value">{{ formatDateTime(studentExam.submitTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">用时：</span>
            <span class="value">{{ studentExam.timeSpentMinutes || 0 }} 分钟</span>
          </div>
        </div>
      </div>

      <!-- 成绩统计卡片 -->
      <div class="score-card">
        <div class="score-header">
          <h2>考试成绩</h2>
          <div class="score-badge" :class="getScoreLevelClass()">
            {{ getScoreLevel() }}
          </div>
        </div>
        
        <div class="score-content">
          <div class="main-score">
            <div class="score-number">{{ studentExam.totalScore || 0 }}</div>
            <div class="score-total">/ {{ examInfo.totalPoints }} 分</div>
          </div>
          
          <div class="score-percentage">
            <div class="percentage-number">{{ getScorePercentage() }}%</div>
            <div class="percentage-label">得分率</div>
          </div>
        </div>
        
        <div class="score-breakdown">
          <div class="breakdown-item">
            <span class="breakdown-label">总题数：</span>
            <span class="breakdown-value">{{ examInfo.totalQuestions }} 题</span>
          </div>
          <div class="breakdown-item">
            <span class="breakdown-label">客观题：</span>
            <span class="breakdown-value">{{ objectiveStats.correct }}/{{ objectiveStats.total }} 题</span>
          </div>
          <div class="breakdown-item">
            <span class="breakdown-label">主观题：</span>
            <span class="breakdown-value">{{ subjectiveStats.graded }}/{{ subjectiveStats.total }} 题已判</span>
          </div>
        </div>
        
        <div class="action-buttons-score-card">
          <el-button 
            type="primary" 
            size="large"
            @click="viewExamDetails"
          >
            <el-icon><View /></el-icon>
            查看考试详情
          </el-button>
        </div>
      </div>

      <!-- 题目得分详情 -->
      <div class="question-scores-card">
        <h3>题目得分详情</h3>
        <div class="question-scores-list">
          <div 
            v-for="(question, index) in questionScores" 
            :key="question.id"
            class="question-score-item"
            :class="{ 'correct': question.givenScore === question.points, 'partial': question.givenScore > 0 && question.givenScore < question.points, 'wrong': question.givenScore === 0 }"
          >
            <div class="question-info">
              <span class="question-number">第 {{ index + 1 }} 题</span>
              <span class="question-type">{{ getQuestionTypeLabel(question.questionType) }}</span>
            </div>
            <div class="question-score">
              <span class="score">{{ question.givenScore || 0 }}</span>
              <span class="total">/ {{ question.points }} 分</span>
            </div>
            <div class="question-status">
              <el-tag 
                :type="getQuestionStatusType(question)" 
                size="small"
              >
                {{ getQuestionStatusLabel(question) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button size="large" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <!-- <el-button 
          type="primary" 
          size="large" 
          @click="downloadResult"
          :disabled="!studentExam.totalScore"
        >
          <el-icon><Download /></el-icon>
          下载成绩单
        </el-button> -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Download, View } from '@element-plus/icons-vue'
import { studentExamApi } from '@/api/admin'

const route = useRoute()
const router = useRouter()

const examInfo = ref({})
const studentExam = ref({})
const questionScores = ref([])
const loading = ref(false)

// 计算属性
const objectiveStats = computed(() => {
  const objectiveQuestions = questionScores.value.filter(q => 
    ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE'].includes(q.questionType)
  )
  const correct = objectiveQuestions.filter(q => q.givenScore === q.points).length
  return {
    total: objectiveQuestions.length,
    correct: correct
  }
})

const subjectiveStats = computed(() => {
  const subjectiveQuestions = questionScores.value.filter(q => 
    ['FILL_BLANK', 'SUBJECTIVE'].includes(q.questionType)
  )
  const graded = subjectiveQuestions.filter(q => q.givenScore !== null).length
  return {
    total: subjectiveQuestions.length,
    graded: graded
  }
})

// 格式化日期时间
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return '-'
  const date = new Date(dateTimeString)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 获取得分百分比
const getScorePercentage = () => {
  if (!examInfo.value.totalPoints || examInfo.value.totalPoints === 0) return 0
  const score = studentExam.value.totalScore || 0
  return Math.round((score / examInfo.value.totalPoints) * 100)
}

// 获取成绩等级
const getScoreLevel = () => {
  const percentage = getScorePercentage()
  if (percentage >= 90) return '优秀'
  if (percentage >= 80) return '良好'
  if (percentage >= 70) return '中等'
  if (percentage >= 60) return '及格'
  return '不及格'
}

// 获取成绩等级样式类
const getScoreLevelClass = () => {
  const percentage = getScorePercentage()
  if (percentage >= 90) return 'excellent'
  if (percentage >= 80) return 'good'
  if (percentage >= 70) return 'medium'
  if (percentage >= 60) return 'pass'
  return 'fail'
}

// 获取题目类型标签
const getQuestionTypeLabel = (type) => {
  const typeMap = {
    'SINGLE_CHOICE': '单选题',
    'MULTIPLE_CHOICE': '多选题',
    'TRUE_FALSE': '判断题',
    'FILL_BLANK': '填空题',
    'SUBJECTIVE': '主观题'
  }
  return typeMap[type] || type
}

// 获取题目状态类型
const getQuestionStatusType = (question) => {
  if (question.givenScore === null) return 'info'
  if (question.givenScore === question.points) return 'success'
  if (question.givenScore > 0) return 'warning'
  return 'danger'
}

// 获取题目状态标签
const getQuestionStatusLabel = (question) => {
  if (question.givenScore === null) return '未判分'
  if (question.givenScore === question.points) return '满分'
  if (question.givenScore > 0) return '部分得分'
  return '不得分'
}

// 加载考试结果
const loadExamResult = async () => {
  try {
    loading.value = true
    const examId = route.params.id
    
    // 加载考试信息
    const examResponse = await studentExamApi.getStudentExamDetail(examId)
    if (examResponse.code === 200) {
      examInfo.value = examResponse.data
    }
    
    // 加载学生考试结果
    const resultResponse = await studentExamApi.getStudentExamResult(examId)
    if (resultResponse.code === 200) {
      studentExam.value = resultResponse.data.studentExam || {}
      questionScores.value = resultResponse.data.questionScores || []
    }
    
  } catch (error) {
    console.error('Load exam result error:', error)
    ElMessage.error('获取考试结果失败')
    goBack()
  } finally {
    loading.value = false
  }
}

// 下载成绩单
const downloadResult = () => {
  ElMessage.info('成绩单下载功能开发中')
}

// 查看考试详情
const viewExamDetails = () => {
  const examId = route.params.id
  router.push(`/user/exam/${examId}/review`)
}

// 返回上一页
const goBack = () => {
  router.push(`/user/exam/`)
}

onMounted(() => {
  loadExamResult()
})
</script>

<style scoped>
.exam-result-page {
  --action-buttons-score-card-margin: 24px 0 0;
  --action-buttons-score-card-display: flex;
  --action-buttons-score-card-justify: center;
  --action-buttons-score-card-gap: 12px;
}

.action-buttons-score-card {
  margin: var(--action-buttons-score-card-margin);
  display: var(--action-buttons-score-card-display);
  justify-content: var(--action-buttons-score-card-justify);
  gap: var(--action-buttons-score-card-gap);
}

.exam-result-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.exam-result-container {
  max-width: 900px;
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

.exam-subtitle {
  color: #606266;
  font-size: 16px;
  margin: 0;
}

.exam-details {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.detail-row {
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.label {
  font-weight: 500;
  color: #606266;
  margin-bottom: 4px;
  font-size: 14px;
}

.value {
  color: #303133;
  font-weight: 500;
  font-size: 16px;
}

.score-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.score-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.score-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
}

.score-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 14px;
}

.score-badge.excellent {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.score-badge.good {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.score-badge.medium {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.score-badge.pass {
  background: #f9f0ff;
  color: #722ed1;
  border: 1px solid #d3adf7;
}

.score-badge.fail {
  background: #fff2f0;
  color: #f5222d;
  border: 1px solid #ffccc7;
}

.score-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.main-score {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.score-number {
  font-size: 48px;
  font-weight: bold;
  color: #303133;
}

.score-total {
  font-size: 24px;
  color: #606266;
}

.score-percentage {
  text-align: center;
}

.percentage-number {
  font-size: 36px;
  font-weight: bold;
  color: #1890ff;
}

.percentage-label {
  font-size: 14px;
  color: #606266;
}

.score-breakdown {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.breakdown-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.breakdown-label {
  display: block;
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.breakdown-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.question-scores-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.question-scores-card h3 {
  margin: 0 0 24px 0;
  color: #303133;
  font-size: 20px;
}

.question-scores-list {
  display: grid;
  gap: 12px;
}

.question-score-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  background: #fafafa;
}

.question-score-item.correct {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.question-score-item.partial {
  background: #fff7e6;
  border-color: #ffd591;
}

.question-score-item.wrong {
  background: #fff2f0;
  border-color: #ffccc7;
}

.question-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.question-number {
  font-weight: bold;
  color: #303133;
}

.question-type {
  font-size: 12px;
  color: #909399;
}

.question-score {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.question-score .score {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.question-score .total {
  font-size: 14px;
  color: #606266;
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
  
  .score-content {
    flex-direction: column;
    gap: 24px;
  }
  
  .score-breakdown {
    grid-template-columns: 1fr;
  }
  
  .question-score-item {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>

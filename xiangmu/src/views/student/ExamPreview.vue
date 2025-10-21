<template>
  <div class="exam-preview-page">
    <!-- 考试头部信息 -->
    <div class="exam-header">
      <div class="exam-info">
        <h1 class="exam-title">{{ examInfo.examName }}</h1>
        <div class="exam-meta">
          <span class="paper-name">{{ examInfo.paperName }}</span>
          <span class="question-count">共 {{ questions.length }} 题 | 总分 {{ examInfo.totalPoints }} 分</span>
          <span v-if="lastSaveTime" class="save-status">
            <el-icon><Check /></el-icon>
            已保存 {{ formatSaveTime(lastSaveTime) }}
          </span>
        </div>
      </div>
      <div class="exam-timer">
        <div class="timer-display" :class="{ warning: timeLeft < 300 }">
          {{ formatTime(timeLeft) }}
        </div>
        <div class="timer-label">剩余时间</div>
        <el-button 
          type="primary" 
          size="small" 
          @click="refreshPage"
          style="margin-top: 8px;"
        >
          <el-icon><Refresh /></el-icon>
          刷新页面
        </el-button>
      </div>
    </div>

    <!-- 整卷内容 -->
    <div class="exam-content">
      <div 
        v-for="(question, index) in questions" 
        :key="question.questionId"
        class="question-section"
      >
        <!-- 题目头部 -->
        <div class="question-header">
          <div class="question-number">{{ index + 1 }}</div>
          <div class="question-info">
            <div class="question-type">{{ getQuestionTypeLabel(question.questionType) }}</div>
            <div class="question-points">{{ question.points }} 分</div>
          </div>
        </div>

        <!-- 题目内容 -->
        <div class="question-content">
          <div class="question-text">
            {{ question.questionText }}
          </div>

          <!-- 选择题选项 -->
          <div v-if="isChoiceQuestion(question.questionType)" class="question-options">
            <div 
              v-for="(option, optIndex) in getDisplayOptions(question)" 
              :key="optIndex"
              class="option-item"
              :class="{ selected: isAnswerSelected(index, option.optionKey) }"
              @click="handleAnswerSelect(index, option.optionKey)"
            >
              <div class="option-label">{{ String.fromCharCode(65 + optIndex) }}</div>
              <div class="option-text">{{ option.optionContent }}</div>
              <div v-if="isAnswerSelected(index, option.optionKey)" class="option-check">
                <el-icon><Check /></el-icon>
              </div>
            </div>
          </div>

          <!-- 填空题 -->
          <div v-else-if="question.questionType === 'FILL_BLANK'" class="fill-blank-answer">
            <el-input
              v-model="fillBlankAnswers[index]"
              type="textarea"
              :rows="3"
              placeholder="请在此处填写答案"
              @input="handleFillBlankAnswer(index, $event)"
            />
            <div class="save-button-container">
              <el-button 
                type="primary" 
                size="small" 
                @click="manualSave(index)"
                :disabled="!fillBlankAnswers[index] || fillBlankAnswers[index].trim() === ''"
              >
                <el-icon><Check /></el-icon>
                保存答案
              </el-button>
            </div>
          </div>

          <!-- 主观题 -->
          <div v-else-if="question.questionType === 'SUBJECTIVE'" class="essay-answer">
            <el-input
              v-model="essayAnswers[index]"
              type="textarea"
              :rows="6"
              placeholder="请在此处详细回答"
              @input="handleEssayAnswer(index, $event)"
            />
            <div class="save-button-container">
              <el-button 
                type="primary" 
                size="small" 
                @click="manualSave(index)"
                :disabled="!essayAnswers[index] || essayAnswers[index].trim() === ''"
              >
                <el-icon><Check /></el-icon>
                保存答案
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="exam-footer">
      <div class="footer-info">
        <span>共 {{ questions.length }} 题 | 已答 {{ answeredCount }} 题 | 总分 {{ examInfo.totalPoints }} 分 | 考试时长 {{ examInfo.durationMinutes }} 分钟</span>
      </div>
      <div class="footer-actions">
        <el-button size="large" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-button 
          type="success" 
          size="large" 
          :loading="submitting"
          @click="submitExam"
        >
          <el-icon><Check /></el-icon>
          提交试卷
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Edit, View, Check, Refresh } from '@element-plus/icons-vue'
import { studentExamApi } from '@/api/admin'

const route = useRoute()
const router = useRouter()

// 响应式数据
const examInfo = ref({})
const questions = ref([])
const timeLeft = ref(0)
const selectedAnswers = ref({})
const submitting = ref(false)
const fillBlankAnswers = ref({})
const essayAnswers = ref({})
const autoSaveTimer = ref(null)
const lastSaveTime = ref(null)

let timer = null

// 计算属性
const answeredCount = computed(() => {
  let count = 0
  for (let i = 0; i < questions.value.length; i++) {
    if (isQuestionAnswered(i)) {
      count++
    }
  }
  return count
})

// 格式化时间
const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  } else {
    return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
}

// 格式化保存时间
const formatSaveTime = (saveTime) => {
  if (!saveTime) return ''
  
  const now = new Date()
  const diff = now - saveTime
  const minutes = Math.floor(diff / 60000)
  
  if (minutes < 1) {
    return '刚刚'
  } else if (minutes < 60) {
    return `${minutes}分钟前`
  } else {
    const hours = Math.floor(minutes / 60)
    return `${hours}小时前`
  }
}

// 判断是否为选择题
const isChoiceQuestion = (questionType) => {
  return ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE'].includes(questionType)
}

// 获取显示的选项（判断题只显示A、B选项）
const getDisplayOptions = (question) => {
  if (!question || !question.options) return []
  
  if (question.questionType === 'TRUE_FALSE') {
    // 判断题只显示前两个选项（A、B），确保按sortOrder排序
    const sortedOptions = [...question.options].sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
    return sortedOptions.slice(0, 2)
  }
  
  // 其他题型显示所有选项，按sortOrder排序
  return [...question.options].sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
}

// 获取题目类型标签
const getQuestionTypeLabel = (questionType) => {
  const typeMap = {
    'SINGLE_CHOICE': '单选题',
    'MULTIPLE_CHOICE': '多选题',
    'TRUE_FALSE': '判断题',
    'FILL_BLANK': '填空题',
    'SUBJECTIVE': '主观题'
  }
  return typeMap[questionType] || '未知题型'
}

// 开始计时器
const startTimer = () => {
  timer = setInterval(() => {
    // 重新计算剩余时间（基于考试结束时间）
    const now = new Date()
    const endTime = new Date(examInfo.value.endTime)
    const remainingSeconds = Math.max(0, Math.floor((endTime - now) / 1000))
    
    timeLeft.value = remainingSeconds
    
    if (remainingSeconds <= 0) {
      // 时间到，跳转到考试界面
      clearInterval(timer)
      ElMessage.warning('考试时间已到，将跳转到答题界面')
      goToExamTaking()
    }
  }, 1000)
}

// 加载考试数据
const loadExamData = async () => {
  try {
    const examId = route.params.id
    
    // 获取考试试卷题目
    const response = await studentExamApi.getStudentExamPaper(examId)
    
    if (response.code === 200) {
      const paperData = response.data
      
      // 设置考试信息
      examInfo.value = {
        examId: paperData.examId,
        examName: paperData.examName,
        paperName: paperData.paperName,
        totalQuestions: paperData.totalQuestions,
        totalPoints: paperData.totalPoints,
        durationMinutes: paperData.durationMinutes,
        startTime: paperData.startTime,
        endTime: paperData.endTime,
        isRandomOrder: paperData.isRandomOrder,
        isRandomOptions: paperData.isRandomOptions
      }
      
      // 检查考试是否已结束
      const now = new Date()
      const endTime = new Date(examInfo.value.endTime)
      if (now > endTime) {
        ElMessage.error('考试已结束')
        router.push('/user/exam')
        return
      }
      
      // 设置题目数据
      questions.value = paperData.questions.map(q => ({
        questionId: q.questionId,
        questionOrder: q.questionOrder,
        questionType: q.questionType,
        questionText: q.questionContent,
        points: q.points,
        difficulty: q.difficulty,
        options: q.options || [],
        answers: q.answers || []
      }))
      
      // 计算剩余时间（基于考试结束时间）
      const remainingSeconds = Math.max(0, Math.floor((endTime - now) / 1000))
      timeLeft.value = remainingSeconds
      
      // 开始计时
      startTimer()
    } else {
      ElMessage.error(response.message || '获取试卷失败')
      router.push('/user/exam')
    }
  } catch (error) {
    console.error('Load exam data error:', error)
    ElMessage.error('获取试卷失败')
    router.push('/user/exam')
  }
}

// 处理选择题答案选择
const handleAnswerSelect = (questionIndex, answer) => {
  const question = questions.value[questionIndex]
  
  // 找到对应的选项内容
  const option = question.options.find(opt => opt.optionKey === answer)
  const optionContent = option ? option.optionContent : answer
  
  if (question.questionType === 'SINGLE_CHOICE' || question.questionType === 'TRUE_FALSE') {
    // 单选题和判断题，直接设置答案内容
    selectedAnswers.value[questionIndex] = [optionContent]
  } else if (question.questionType === 'MULTIPLE_CHOICE') {
    // 多选题，切换答案内容
    if (!selectedAnswers.value[questionIndex]) {
      selectedAnswers.value[questionIndex] = []
    }
    
    const currentAnswers = selectedAnswers.value[questionIndex]
    const index = currentAnswers.indexOf(optionContent)
    
    if (index > -1) {
      // 如果已选中，则取消选择
      currentAnswers.splice(index, 1)
    } else {
      // 如果未选中，则添加选择
      currentAnswers.push(optionContent)
    }
  }
  
  // 选择题自动保存
  autoSave()
}

// 处理填空题答案输入
const handleFillBlankAnswer = (questionIndex, answer) => {
  fillBlankAnswers.value[questionIndex] = answer
  if (!selectedAnswers.value[questionIndex]) {
    selectedAnswers.value[questionIndex] = []
  }
  selectedAnswers.value[questionIndex] = [answer]
}

// 处理主观题答案输入
const handleEssayAnswer = (questionIndex, answer) => {
  essayAnswers.value[questionIndex] = answer
  if (!selectedAnswers.value[questionIndex]) {
    selectedAnswers.value[questionIndex] = []
  }
  selectedAnswers.value[questionIndex] = [answer]
}

// 检查答案是否已选择
const isAnswerSelected = (questionIndex, optionKey) => {
  const question = questions.value[questionIndex]
  const option = question.options.find(opt => opt.optionKey === optionKey)
  const optionContent = option ? option.optionContent : optionKey
  
  const answers = selectedAnswers.value[questionIndex]
  return answers && answers.includes(optionContent)
}

// 获取填空题答案
const getFillBlankAnswer = (questionIndex) => {
  return fillBlankAnswers.value[questionIndex] || ''
}

// 获取主观题答案
const getEssayAnswer = (questionIndex) => {
  return essayAnswers.value[questionIndex] || ''
}

// 判断题目是否已答
const isQuestionAnswered = (questionIndex) => {
  const question = questions.value[questionIndex]
  if (!question) return false
  
  if (question.questionType === 'FILL_BLANK') {
    return fillBlankAnswers.value[questionIndex] && fillBlankAnswers.value[questionIndex].trim() !== ''
  } else if (question.questionType === 'SUBJECTIVE') {
    return essayAnswers.value[questionIndex] && essayAnswers.value[questionIndex].trim() !== ''
  } else {
    return selectedAnswers.value[questionIndex] && selectedAnswers.value[questionIndex].length > 0
  }
}

// 自动保存（选择题）
const autoSave = () => {
  // 清除之前的定时器
  if (autoSaveTimer.value) {
    clearTimeout(autoSaveTimer.value)
  }
  
  // 设置新的定时器，延迟1秒后保存
  autoSaveTimer.value = setTimeout(() => {
    saveAnswersToLocal()
  }, 1000)
}

// 手动保存（填空题和主观题）
const manualSave = (questionIndex) => {
  saveAnswersToLocal()
  ElMessage.success('答案已保存')
}

// 保存答案到本地存储
const saveAnswersToLocal = () => {
  try {
    const examId = route.params.id
    const saveData = {
      selectedAnswers: selectedAnswers.value,
      fillBlankAnswers: fillBlankAnswers.value,
      essayAnswers: essayAnswers.value,
      saveTime: new Date().toISOString()
    }
    
    console.log('ExamPreview - Saving to local storage:', saveData)
    
    localStorage.setItem(`exam_${examId}_answers`, JSON.stringify(saveData))
    lastSaveTime.value = new Date()
  } catch (error) {
    console.error('Save to local storage error:', error)
  }
}

// 从本地存储加载答案
const loadAnswersFromLocal = () => {
  try {
    const examId = route.params.id
    const savedData = localStorage.getItem(`exam_${examId}_answers`)
    
    console.log('ExamPreview - Loading from local storage:', savedData)
    
    if (savedData) {
      const data = JSON.parse(savedData)
      
      // 清理旧格式的数据
      const cleanedData = cleanOldAnswerData(data)
      
      // 清理和重新分配答案数据
      selectedAnswers.value = {}
      fillBlankAnswers.value = {}
      essayAnswers.value = {}
      
      // 根据题目类型正确分配答案
      if (questions.value && questions.value.length > 0) {
        questions.value.forEach((question, index) => {
          if (question.questionType === 'FILL_BLANK') {
            // 填空题答案
            if (cleanedData.fillBlankAnswers && cleanedData.fillBlankAnswers[index]) {
              fillBlankAnswers.value[index] = cleanedData.fillBlankAnswers[index]
            }
          } else if (question.questionType === 'SUBJECTIVE') {
            // 主观题答案
            if (cleanedData.essayAnswers && cleanedData.essayAnswers[index]) {
              essayAnswers.value[index] = cleanedData.essayAnswers[index]
            }
          } else {
            // 选择题答案（单选、多选、判断）
            if (cleanedData.selectedAnswers && cleanedData.selectedAnswers[index]) {
              selectedAnswers.value[index] = cleanedData.selectedAnswers[index]
            }
          }
        })
      }
      
      lastSaveTime.value = data.saveTime ? new Date(data.saveTime) : null
      
      console.log('ExamPreview - Loaded answers:', {
        selectedAnswers: selectedAnswers.value,
        fillBlankAnswers: fillBlankAnswers.value,
        essayAnswers: essayAnswers.value
      })
      
      ElMessage.success('已恢复之前的答题进度')
    }
  } catch (error) {
    console.error('Load from local storage error:', error)
  }
}

// 清除本地保存的答案
const clearLocalAnswers = () => {
  try {
    const examId = route.params.id
    localStorage.removeItem(`exam_${examId}_answers`)
    lastSaveTime.value = null
  } catch (error) {
    console.error('Clear local storage error:', error)
  }
}

// 清理旧格式的答案数据
const cleanOldAnswerData = (data) => {
  const cleanedData = {
    selectedAnswers: {},
    fillBlankAnswers: {},
    essayAnswers: {}
  }
  
  // 清理选择题答案，只保留有效的选项内容
  if (data.selectedAnswers) {
    Object.keys(data.selectedAnswers).forEach(key => {
      const answers = data.selectedAnswers[key]
      if (Array.isArray(answers)) {
        // 过滤掉数字和无效的选项键，只保留字符串内容
        const validAnswers = answers.filter(answer => 
          typeof answer === 'string' && 
          answer.length > 0 && 
          !/^[A-E]$/.test(answer) && // 不是单个字母
          !/^\d+$/.test(answer) // 不是纯数字
        )
        if (validAnswers.length > 0) {
          cleanedData.selectedAnswers[key] = validAnswers
        }
      }
    })
  }
  
  // 清理填空题答案
  if (data.fillBlankAnswers) {
    Object.keys(data.fillBlankAnswers).forEach(key => {
      const answer = data.fillBlankAnswers[key]
      if (typeof answer === 'string' && answer.trim().length > 0) {
        cleanedData.fillBlankAnswers[key] = answer
      }
    })
  }
  
  // 清理主观题答案
  if (data.essayAnswers) {
    Object.keys(data.essayAnswers).forEach(key => {
      const answer = data.essayAnswers[key]
      if (typeof answer === 'string' && answer.trim().length > 0) {
        cleanedData.essayAnswers[key] = answer
      }
    })
  }
  
  return cleanedData
}

// 提交试卷
const submitExam = async () => {
  try {
    submitting.value = true
    
    const examId = route.params.id
    
    // 合并所有答案数据
    const allAnswers = {}
    
    // 添加选择题答案
    Object.keys(selectedAnswers.value).forEach(key => {
      allAnswers[key] = selectedAnswers.value[key]
    })
    
    // 添加填空题答案
    Object.keys(fillBlankAnswers.value).forEach(key => {
      allAnswers[key] = fillBlankAnswers.value[key]
    })
    
    // 添加主观题答案
    Object.keys(essayAnswers.value).forEach(key => {
      allAnswers[key] = essayAnswers.value[key]
    })
    
    console.log('Submitting answers:', allAnswers)
    
    const response = await studentExamApi.submitStudentExam(examId, {
      answers: allAnswers
    })
    
    if (response.code === 200) {
      ElMessage.success('试卷提交成功')
      clearLocalAnswers() // 清除本地保存的答案
      router.push('/user/exam')
    } else {
      ElMessage.error(response.message || '提交失败')
    }
  } catch (error) {
    console.error('Submit exam error:', error)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}


// 刷新页面
const refreshPage = () => {
  window.location.reload()
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

onMounted(async () => {
  await loadExamData()
  // 确保题目数据加载完成后再加载答案
  nextTick(() => {
    loadAnswersFromLocal() // 加载本地保存的答案
  })
  
  // 监听页面可见性变化，当页面重新可见时重新加载答案
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

const handleVisibilityChange = () => {
  if (!document.hidden) {
    // 页面重新可见时，重新加载答案
    loadAnswersFromLocal()
  }
}

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
  if (autoSaveTimer.value) {
    clearTimeout(autoSaveTimer.value)
  }
  // 移除页面可见性监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped>
.exam-preview-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.exam-header {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-info {
  flex: 1;
}

.exam-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
}

.exam-meta {
  display: flex;
  gap: 16px;
  color: #606266;
  font-size: 14px;
  align-items: center;
}

.save-status {
  color: #67c23a;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.exam-timer {
  text-align: center;
}

.timer-display {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
}

.timer-display.warning {
  color: #f56c6c;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.timer-label {
  font-size: 12px;
  color: #909399;
  text-align: center;
  margin-top: 4px;
}

.exam-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.question-section {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #ebeef5;
}

.question-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.question-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  margin-right: 20px;
}

.question-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.question-type {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
  background: #ecf5ff;
  padding: 6px 16px;
  border-radius: 20px;
}

.question-points {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.question-content {
  margin-left: 60px;
}

.question-text {
  font-size: 18px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 20px;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.option-item:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.option-item.selected {
  border-color: #409eff;
  background: #409eff;
  color: white;
}

.option-label {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 12px;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;
}

.option-item.selected .option-label {
  background: white;
  color: #409eff;
}

.option-text {
  flex: 1;
  font-size: 16px;
  color: #303133;
  transition: all 0.3s;
}

.option-item.selected .option-text {
  color: white;
}

.option-check {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: white;
  font-size: 18px;
}

.fill-blank-answer,
.essay-answer {
  margin-top: 20px;
}

.fill-blank-answer .el-textarea,
.essay-answer .el-textarea {
  width: 100%;
}

.fill-blank-answer .el-textarea__inner,
.essay-answer .el-textarea__inner {
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  padding: 12px;
  font-size: 16px;
  line-height: 1.5;
  resize: vertical;
}

.fill-blank-answer .el-textarea__inner:focus,
.essay-answer .el-textarea__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.save-button-container {
  margin-top: 12px;
  text-align: right;
}

.exam-footer {
  background: white;
  border-radius: 8px;
  padding: 20px 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-info {
  color: #606266;
  font-size: 14px;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 768px) {
  .exam-preview-page {
    padding: 10px;
  }
  
  .exam-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .exam-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .question-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .question-content {
    margin-left: 0;
  }
  
  .question-info {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .exam-footer {
    flex-direction: column;
    gap: 15px;
  }
  
  .footer-actions {
    width: 100%;
    justify-content: center;
  }
}
</style>

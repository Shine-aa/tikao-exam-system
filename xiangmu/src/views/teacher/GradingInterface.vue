<template>
  <div class="grading-interface">
    <!-- 头部信息 -->
    <div class="grading-header">
      <div class="header-left">
        <el-button @click="goBack" type="info" plain>
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <div class="exam-info">
          <h2>{{ examInfo.examName }}</h2>
          <p>{{ studentInfo.realName }} ({{ studentInfo.username }}) - {{ examInfo.className }}</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="success" @click="handleSaveGrading" :loading="saving">
          <el-icon><Check /></el-icon>
          保存判卷
        </el-button>
        <el-button type="primary" @click="handleSubmitGrading" :loading="submitting">
          <el-icon><Upload /></el-icon>
          提交判卷
        </el-button>
      </div>
    </div>

    <!-- 考试统计信息 -->
    <el-card class="exam-stats" v-if="examInfo.id">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ examInfo.totalQuestions }}</div>
            <div class="stat-label">总题数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ examInfo.totalPoints }}</div>
            <div class="stat-label">总分</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ studentInfo.timeSpentMinutes || 0 }}</div>
            <div class="stat-label">用时(分钟)</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ currentTotalScore.toFixed(1) }}</div>
            <div class="stat-label">当前得分</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 题目列表 -->
    <div class="questions-container">
      <div class="questions-nav">
        <h3>题目导航</h3>
        
        <!-- 客观题导航 -->
        <div class="nav-section">
          <div class="nav-section-header">
            <h4>客观题（自动判分）</h4>
            <el-button
              type="primary"
              size="small"
              @click="autoGradeObjectiveQuestions"
              :disabled="!hasObjectiveQuestions"
            >
              一键判卷
            </el-button>
          </div>
          <div class="question-tabs">
            <el-button
              v-for="(question, index) in objectiveQuestions"
              :key="question.id"
              :type="currentQuestionIndex === question.originalIndex ? 'primary' : ''"
              :class="{ 'graded': question.isGraded }"
              size="small"
              @click="goToQuestion(question.originalIndex)"
            >
              {{ question.originalIndex + 1 }}
              <el-icon v-if="question.isGraded" class="graded-icon">
                <Check />
              </el-icon>
            </el-button>
          </div>
        </div>
        
        <!-- 主观题导航 -->
        <div class="nav-section">
          <div class="nav-section-header">
            <h4>主观题（手动判分）</h4>
            <el-button
              type="warning"
              size="small"
              @click="resetSubjectiveQuestions"
            >
              重置主观题
            </el-button>
          </div>
          <div class="question-tabs">
            <el-button
              v-for="(question, index) in subjectiveQuestions"
              :key="question.id"
              :type="currentQuestionIndex === question.originalIndex ? 'primary' : ''"
              :class="{ 'graded': question.isGraded }"
              size="small"
              @click="goToQuestion(question.originalIndex)"
            >
              {{ question.originalIndex + 1 }}
              <el-icon v-if="question.isGraded" class="graded-icon">
                <Check />
              </el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 当前题目 -->
      <div class="current-question" v-if="currentQuestion">
        <div class="question-header">
          <div class="question-info">
            <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
            <el-tag :type="getQuestionTypeTag(currentQuestion.questionType)">
              {{ getQuestionTypeLabel(currentQuestion.questionType) }}
            </el-tag>
            <span class="question-score">{{ currentQuestion.points }}分</span>
          </div>
          <div class="grading-status">
            <el-tag v-if="currentQuestion.isGraded" type="success">已判卷</el-tag>
            <el-tag v-else type="warning">未判卷</el-tag>
          </div>
        </div>

        <div class="question-content">
          <div class="question-text" v-html="currentQuestion.questionText"></div>
          
          <!-- 客观题选项和答案 -->
          <div v-if="isObjectiveQuestion(currentQuestion.questionType)" class="options-answers">
            <h4>选项与答案：</h4>
            <div class="options-grid">
              <div
                v-for="(option, index) in currentQuestion.options"
                :key="index"
                class="option-answer-item"
                :class="{
                  'correct-option': option.isCorrect,
                  'student-correct': isStudentSelected(option) && option.isCorrect,
                  'student-wrong': isStudentSelected(option) && !option.isCorrect,
                  'student-missed': isMissedOption(option),
                  'not-selected': !isStudentSelected(option) && !option.isCorrect
                }"
              >
                <div class="option-info">
                  <span class="option-label">{{ option.optionKey }}</span>
                  <span class="option-content">{{ option.optionContent }}</span>
                </div>
                <div class="answer-status">
                  <span v-if="option.isCorrect" class="correct-mark">✓ 正确答案</span>
                  <span v-if="isStudentSelected(option)" class="student-mark">学生选择</span>
                </div>
                <div class="option-feedback">
                  <span v-if="isStudentSelected(option) && option.isCorrect" class="feedback correct-feedback">选对了</span>
                  <span v-if="isWrongOption(option)" class="feedback wrong-feedback">错选</span>
                  <span v-if="isMissedOption(option)" class="feedback missed-feedback">漏选</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 主观题学生答案区域 -->
          <div v-if="isSubjectiveQuestion(currentQuestion.questionType)" class="student-answer">
            <h4>学生答案：</h4>
            <div v-if="currentQuestion.questionType === 'FILL_BLANK'" class="fill-blank-answer">
              <div class="fill-blank-comparison">
                <div class="answer-item">
                  <span class="answer-label">学生答案：</span>
                  <el-tag
                    v-if="getStudentFillBlankAnswers()"
                    :type="isFillBlankCorrect(0, getStudentFillBlankAnswers()) ? 'success' : 'danger'"
                    class="answer-tag"
                  >
                    {{ getStudentFillBlankAnswers() }}
                  </el-tag>
                  <span v-else class="no-answer">未作答</span>
                </div>
                <div class="answer-item">
                  <span class="answer-label">正确答案：</span>
                  <el-tag
                    v-if="currentQuestion.answers && currentQuestion.answers[0]"
                    type="info"
                    class="answer-tag correct-answer-tag"
                  >
                    {{ currentQuestion.answers[0].answerContent }}
                  </el-tag>
                  <span v-else class="no-answer">无标准答案</span>
                </div>
              </div>
            </div>
            <div v-else-if="currentQuestion.questionType === 'SUBJECTIVE'" class="subjective-answer">
              <div class="subjective-comparison">
                <div class="answer-section">
                  <h5 class="answer-title">学生答案：</h5>
                  <div v-if="getStudentEssayAnswer()" class="answer-content student-answer">
                    {{ getStudentEssayAnswer() }}
                  </div>
                  <div v-else class="no-answer">未作答</div>
                </div>
                <div class="answer-section">
                  <h5 class="answer-title">参考答案：</h5>
                  <div v-if="currentQuestion.answers && currentQuestion.answers[0]" class="answer-content correct-answer">
                    {{ currentQuestion.answers[0].answerContent }}
                  </div>
                  <div v-else class="no-answer">无标准答案</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 判分区域 -->
          <div class="grading-section">
            <h4>判分：</h4>
            <div class="grading-controls">
              <el-input-number
                v-model="currentQuestion.givenScore"
                :min="0"
                :max="currentQuestion.points"
                :precision="1"
                size="small"
                style="width: 120px"
              />
              <span class="score-label">/ {{ currentQuestion.points }} 分</span>
              <el-button
                v-if="isObjectiveQuestion(currentQuestion.questionType)"
                type="primary"
                size="small"
                @click="autoGradeQuestion"
                :disabled="currentQuestion.isGraded"
              >
                重新判分
              </el-button>
              <el-button
                type="success"
                size="small"
                @click="markAsGraded"
                :disabled="currentQuestion.givenScore === null"
              >
                标记已判
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 保存提示 -->
    <el-dialog
      v-model="saveDialogVisible"
      title="保存判卷结果"
      width="400px"
      :close-on-click-modal="false"
    >
      <p>确定要保存当前的判卷结果吗？</p>
      <template #footer>
        <el-button @click="saveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSave">确定保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Check, Upload
} from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { examApi } from '@/api/admin'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const submitting = ref(false)
const examInfo = ref({})
const studentInfo = ref({})
const questions = ref([])
const studentAnswers = ref({})
const currentQuestionIndex = ref(0)
const fillBlankAnswers = ref({})
const subjectiveAnswer = ref('')
const saveDialogVisible = ref(false)

// 计算属性
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value] || null
})

const currentTotalScore = computed(() => {
  return questions.value.reduce((total, q) => {
    return total + (q.givenScore || 0)
  }, 0)
})

// 客观题列表
const objectiveQuestions = computed(() => {
  return questions.value
    .map((question, index) => ({ ...question, originalIndex: index }))
    .filter(question => isObjectiveQuestion(question.questionType))
})

// 主观题列表
const subjectiveQuestions = computed(() => {
  return questions.value
    .map((question, index) => ({ ...question, originalIndex: index }))
    .filter(question => isSubjectiveQuestion(question.questionType))
})

// 是否有客观题
const hasObjectiveQuestions = computed(() => {
  return objectiveQuestions.value.length > 0
})

// 获取考试ID和学生ID
const examId = computed(() => route.params.examId)
const studentId = computed(() => route.params.studentId)

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    
    // 加载考试信息
    const examResponse = await examApi.getExamById(examId.value)
    if (examResponse.code === 200) {
      examInfo.value = examResponse.data
    }
    
    // 加载学生信息
    const studentResponse = await examApi.getStudentInfo(studentId.value)
    if (studentResponse.code === 200) {
      studentInfo.value = studentResponse.data
    }
    
    // 加载学生答案和题目
    const answerResponse = await examApi.getStudentAnswers(examId.value, studentId.value)
    if (answerResponse.code === 200) {
      questions.value = answerResponse.data.questions || []
      studentAnswers.value = answerResponse.data
      
      initializeGradingData()
      
      // 自动判分客观题
      autoGradeObjectiveQuestions()
      
      // 自动保存客观题判分结果
      await saveObjectiveGradingResults()
    }
    
  } catch (error) {
    console.error('Load data error:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化判卷数据
const initializeGradingData = () => {
  questions.value.forEach((question, index) => {
    // 初始化给定分数
    if (question.givenScore === undefined) {
      question.givenScore = null
    }
    
    // 初始化判卷状态 - 有分数就标记为已判（包括0分），null表示未判分
    question.isGraded = question.givenScore !== null && question.givenScore !== undefined
    
    // 初始化填空题答案
    if (question.questionType === 'FILL_BLANK') {
      const studentAnswer = getStudentFillBlankAnswers()
      fillBlankAnswers.value[`${index}_0`] = studentAnswer || ''
    }
  })
}

// 跳转到指定题目
const goToQuestion = (index) => {
  // 如果当前题目有分数，自动标记为已判
  if (currentQuestion.value && currentQuestion.value.givenScore !== null && currentQuestion.value.givenScore !== undefined) {
    currentQuestion.value.isGraded = true
  }
  
  currentQuestionIndex.value = index
}

// 判断是否为选择题
const isChoiceQuestion = (questionType) => {
  return ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE'].includes(questionType)
}

// 判断是否为客观题（自动判分）
const isObjectiveQuestion = (questionType) => {
  return ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE'].includes(questionType)
}

// 判断是否为主观题（手动判分）
const isSubjectiveQuestion = (questionType) => {
  return ['FILL_BLANK', 'SUBJECTIVE'].includes(questionType)
}

// 获取题目类型标签
const getQuestionTypeTag = (questionType) => {
  const typeMap = {
    'SINGLE_CHOICE': 'primary',
    'MULTIPLE_CHOICE': 'success',
    'TRUE_FALSE': 'warning',
    'FILL_BLANK': 'info',
    'SUBJECTIVE': 'danger'
  }
  return typeMap[questionType] || 'info'
}

// 获取题目类型标签文本
const getQuestionTypeLabel = (questionType) => {
  const typeMap = {
    'SINGLE_CHOICE': '单选题',
    'MULTIPLE_CHOICE': '多选题',
    'TRUE_FALSE': '判断题',
    'FILL_BLANK': '填空题',
    'SUBJECTIVE': '主观题'
  }
  return typeMap[questionType] || questionType
}

// 获取选项的CSS类
const getOptionClass = (option) => {
  const classes = []
  
  if (option.isCorrect) {
    classes.push('correct-option')
  }
  
  if (isStudentSelected(option)) {
    classes.push('student-selected')
    if (!option.isCorrect) {
      classes.push('wrong-selected')
    }
  }
  
  return classes
}

// 判断是否为正确答案选项
const isCorrectOption = (option) => {
  return option.isCorrect
}

// 判断是否为错误选择
const isWrongOption = (option) => {
  return isStudentSelected(option) && !option.isCorrect
}

// 判断是否为漏选（正确答案但学生未选择）- 仅适用于多选题
const isMissedOption = (option) => {
  return currentQuestion.value?.questionType === 'MULTIPLE_CHOICE' && 
         option.isCorrect && 
         !isStudentSelected(option)
}

// 判断学生是否选择了该选项
const isStudentSelected = (option) => {
  const studentAnswers = getStudentChoiceAnswers()
  const isSelected = studentAnswers.includes(option.optionContent)
  // 学生答案存储的是选项内容，所以比较 optionContent
  return isSelected
}

// 获取学生选择题答案
const getStudentChoiceAnswers = () => {
  if (!currentQuestion.value || !currentQuestion.value.studentAnswers) {
    return []
  }
  
  const answer = currentQuestion.value.studentAnswers
  
  if (Array.isArray(answer)) {
    return answer
  } else if (answer) {
    return [answer]
  }
  
  return []
}

// 判断答案是否正确
const isAnswerCorrect = (answer) => {
  return currentQuestion.value.options.some(option => 
    option.optionContent === answer && option.isCorrect
  )
}

// 获取学生填空题答案
const getStudentFillBlankAnswers = () => {
  if (!currentQuestion.value || !currentQuestion.value.studentAnswers) {
    return ''
  }
  
  const answer = currentQuestion.value.studentAnswers
  
  if (Array.isArray(answer)) {
    return answer[0] || ''
  } else if (answer) {
    return answer
  }
  
  return ''
}

// 判断填空题答案是否正确
const isFillBlankCorrect = (index, answer) => {
  const correctAnswers = currentQuestion.value.answers
  if (!correctAnswers || !correctAnswers[index]) {
    return false
  }
  
  const correctAnswer = correctAnswers[index].answerContent
  return answer === correctAnswer
}

// 获取学生主观题答案
const getStudentEssayAnswer = () => {
  if (!currentQuestion.value || !currentQuestion.value.studentAnswers) {
    return ''
  }
  
  const answer = currentQuestion.value.studentAnswers
  
  return answer || ''
}


// 判断选项是否被标记为正确答案
const isOptionSelected = (option) => {
  return option.isCorrect
}



// 获取学生主观题答案
const getStudentSubjectiveAnswer = () => {
  if (!currentQuestion.value) return ''
  const studentAnswers = currentQuestion.value.studentAnswers || []
  return Array.isArray(studentAnswers) ? studentAnswers.join('') : studentAnswers
}



// 自动判分所有客观题
const autoGradeObjectiveQuestions = () => {
  questions.value.forEach((question, index) => {
    if (isObjectiveQuestion(question.questionType)) {
      const score = calculateObjectiveScore(question)
      question.givenScore = score
      // 只有真正有分数才标记为已判
      question.isGraded = score !== null && score >= 0
    }
  })
  ElMessage.success('客观题自动判分完成')
}

// 保存客观题判分结果
const saveObjectiveGradingResults = async () => {
  try {
    const gradingData = {
      examId: examId.value,
      studentId: studentId.value,
      questions: questions.value.map(question => ({
        questionId: question.id,
        givenScore: question.givenScore,
        isGraded: question.isGraded
      }))
    }
    
    await examApi.saveGradingResult(gradingData)
    // 客观题判分结果已自动保存
  } catch (error) {
    console.error('保存客观题判分结果失败:', error)
  }
}

// 重置所有主观题为未判状态
const resetSubjectiveQuestions = () => {
  questions.value.forEach((question, index) => {
    if (isSubjectiveQuestion(question.questionType)) {
      question.givenScore = null
      question.isGraded = false
    }
  })
  ElMessage.success('主观题已重置为未判状态')
}

// 计算客观题分数
const calculateObjectiveScore = (question) => {
  const questionType = question.questionType
  const studentAnswers = getStudentAnswersForQuestion(question)
  
  if (questionType === 'SINGLE_CHOICE' || questionType === 'TRUE_FALSE') {
    // 单选题和判断题：选对得满分，选错得0分
    if (studentAnswers.length === 0) return null // 没有作答返回null
    
    const isCorrect = studentAnswers.some(answer => 
      question.options.some(opt => opt.optionContent === answer && opt.isCorrect)
    )
    return isCorrect ? question.points : 0
  } else if (questionType === 'MULTIPLE_CHOICE') {
    // 多选题评分规则
    if (studentAnswers.length === 0) return null // 没有作答返回null
    
    const correctOptions = question.options.filter(opt => opt.isCorrect)
    const studentCorrectAnswers = studentAnswers.filter(answer => 
      question.options.some(opt => opt.optionContent === answer && opt.isCorrect)
    )
    
    // 检查是否有错选
    const hasWrongAnswer = studentAnswers.some(answer => 
      !question.options.some(opt => opt.optionContent === answer && opt.isCorrect)
    )
    
    if (hasWrongAnswer) {
      return 0 // 有错选得0分
    } else if (studentCorrectAnswers.length === correctOptions.length) {
      return question.points // 全选对得满分
    } else if (studentCorrectAnswers.length > 0) {
      return Math.floor(question.points * 0.6) // 部分选对得60%分
    }
    
    return 0
  }
  
  return null // 其他类型题目返回null
}

// 获取指定题目的学生答案
const getStudentAnswersForQuestion = (question) => {
  if (!question.studentAnswers) return []
  
  const answer = question.studentAnswers
  if (Array.isArray(answer)) {
    return answer
  } else if (answer) {
    return [answer]
  }
  
  return []
}

// 自动判分当前题目
const autoGradeQuestion = () => {
  if (!currentQuestion.value) return
  
  const questionType = currentQuestion.value.questionType
  
  // 只对客观题进行自动判分
  if (isObjectiveQuestion(questionType)) {
    const score = calculateObjectiveScore(currentQuestion.value)
    currentQuestion.value.givenScore = score
    currentQuestion.value.isGraded = true
    
    ElMessage.success(`自动判分完成，得分：${score}/${currentQuestion.value.points}`)
  } else {
    ElMessage.warning('填空题和主观题需要手动判分')
  }
}

// 标记为已判卷
const markAsGraded = () => {
  if (currentQuestion.value) {
    currentQuestion.value.isGraded = true
  }
}

// 保存判卷结果
const handleSaveGrading = () => {
  saveDialogVisible.value = true
}

// 确认保存
const confirmSave = async () => {
  try {
    saving.value = true
    saveDialogVisible.value = false
    
    // 确保主观题有分数时自动标记为已判
    questions.value.forEach(question => {
      if (isSubjectiveQuestion(question.questionType)) {
        // 有分数就标记为已判（包括0分），null表示未判分
        question.isGraded = question.givenScore !== null && question.givenScore !== undefined
      }
    })
    
    const gradingData = {
      examId: examId.value,
      studentId: studentId.value,
      questions: questions.value.map(q => ({
        questionId: q.id,
        givenScore: q.givenScore,
        isGraded: q.isGraded
      })),
      totalScore: currentTotalScore.value,
      gradingComment: subjectiveAnswer.value
    }
    
    await examApi.saveGradingResult(gradingData)
    ElMessage.success('判卷结果已保存')
    
  } catch (error) {
    console.error('Save grading error:', error)
    ElMessage.error('保存判卷结果失败')
  } finally {
    saving.value = false
  }
}

// 提交判卷结果
const handleSubmitGrading = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要提交判卷结果吗？提交后将无法修改。',
      '提交判卷确认',
      {
        confirmButtonText: '确定提交',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    submitting.value = true
    
    // 确保主观题有分数时自动标记为已判
    questions.value.forEach(question => {
      if (isSubjectiveQuestion(question.questionType)) {
        // 有分数就标记为已判（包括0分），null表示未判分
        question.isGraded = question.givenScore !== null && question.givenScore !== undefined
      }
    })
    
    const gradingData = {
      examId: examId.value,
      studentId: studentId.value,
      questions: questions.value.map(q => ({
        questionId: q.id,
        givenScore: q.givenScore,
        isGraded: q.isGraded
      })),
      totalScore: currentTotalScore.value,
      gradingComment: subjectiveAnswer.value
    }
    
    await examApi.submitGradingResult(gradingData)
    ElMessage.success('判卷结果已提交')
    
    // 返回学生选择页面
    router.push(`/teacher/score-analysis/students/${examId.value}`)
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Submit grading error:', error)
      ElMessage.error('提交判卷结果失败')
    }
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.push(`/teacher/score-analysis/students/${examId.value}`)
}

// 生命周期
onMounted(() => {
  if (examId.value && studentId.value) {
    loadData()
  }
})
</script>

<style scoped>
.grading-interface {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.grading-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.exam-info h2 {
  margin: 0 0 5px 0;
  color: #303133;
}

.exam-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.header-right {
  display: flex;
  gap: 10px;
}

.exam-stats {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.questions-container {
  display: flex;
  gap: 20px;
}

.questions-nav {
  width: 200px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  height: fit-content;
}

.questions-nav h3 {
  margin: 0 0 15px 0;
  color: #303133;
}

.nav-section {
  margin-bottom: 20px;
}

.nav-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.nav-section-header h4 {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.question-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: space-between;
}

.question-tabs .el-button {
  position: relative;
  width: 30%;
  min-width: 40px;
  justify-content: center;
  margin: 0;
}

.graded {
  background: #67c23a !important;
  border-color: #67c23a !important;
  color: white !important;
}

.graded-icon {
  position: absolute;
  top: -5px;
  right: -5px;
  font-size: 12px;
}

.current-question {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.question-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.question-number {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.question-score {
  color: #f56c6c;
  font-weight: bold;
}

.question-content {
  margin-bottom: 20px;
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 20px;
  color: #303133;
}

/* 新的选项和答案布局 */
.options-answers {
  margin-bottom: 20px;
}

.options-grid {
  display: grid;
  gap: 12px;
}

.option-answer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s ease;
}

.option-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.option-label {
  display: inline-block;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  background: #409eff;
  color: white;
  border-radius: 50%;
  font-weight: bold;
  font-size: 14px;
}

.option-content {
  font-size: 15px;
  color: #303133;
  flex: 1;
}

.answer-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.correct-mark {
  color: #67c23a;
  font-weight: bold;
  font-size: 14px;
}

.student-mark {
  color: #409eff;
  font-weight: bold;
  font-size: 14px;
}

/* 选项状态样式 */
.option-answer-item.correct-option {
  background: #f0f9ff;
  border-color: #67c23a;
}

.option-answer-item.student-correct {
  background: #f6ffed;
  border-color: #52c41a;
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.2);
}

.option-answer-item.student-wrong {
  background: #fff2f0;
  border-color: #ff4d4f;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.2);
}

.option-answer-item.student-missed {
  background: #fff7e6;
  border-color: #fa8c16;
  box-shadow: 0 2px 8px rgba(250, 140, 22, 0.2);
}

.option-answer-item.not-selected {
  background: #fafafa;
  border-color: #d9d9d9;
}

/* 反馈提示样式 */
.option-feedback {
  margin-top: 8px;
  text-align: center;
}

.feedback {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.correct-feedback {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.wrong-feedback {
  background-color: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

.missed-feedback {
  background-color: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

/* 填空题答案对比样式 */
.fill-blank-comparison {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.answer-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.answer-label {
  font-weight: bold;
  min-width: 80px;
  color: #606266;
}

.answer-tag {
  flex: 1;
  max-width: 300px;
}

.correct-answer-tag {
  background-color: #f0f9ff !important;
  border-color: #409eff !important;
  color: #409eff !important;
}

/* 主观题答案对比样式 */
.subjective-comparison {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.answer-section {
  flex: 1;
  min-width: 0; /* 防止内容溢出 */
}

.answer-title {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: bold;
  color: #606266;
  border-bottom: 2px solid #e4e7ed;
  padding-bottom: 5px;
}

.answer-content {
  min-height: 120px;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background-color: #fafafa;
  white-space: pre-wrap;
  word-wrap: break-word;
  line-height: 1.6;
}

.student-answer {
  background-color: #f6ffed;
  border-color: #b7eb8f;
}

.correct-answer {
  background-color: #f0f9ff;
  border-color: #91d5ff;
}

/* 旧的选项样式（保留兼容性） */
.options {
  margin-bottom: 20px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fafafa;
  position: relative;
}

.option-item.correct {
  background: #f0f9ff;
  border-color: #67c23a;
}

.option-item.selected {
  background: #e1f3d8;
  border-color: #67c23a;
}

.option-item.student-selected {
  background: #fef0f0;
  border-color: #f56c6c;
}

.option-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 20px;
}

.option-content {
  flex: 1;
}

.correct-icon {
  color: #67c23a;
  margin-left: 10px;
}

.student-answer {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
}

.student-answer h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.answer-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.no-answer {
  color: #909399;
  font-style: italic;
}

.fill-blank-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.blank-label {
  font-weight: 500;
  min-width: 60px;
}

.original-answer {
  margin-left: 10px;
}

.subjective-answer {
  margin-bottom: 15px;
}

.grading-comment {
  margin-bottom: 15px;
}

.answer-content {
  margin-top: 10px;
  padding: 10px;
  background: white;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  white-space: pre-wrap;
}

.grading-section {
  padding: 15px;
  background: #f0f9ff;
  border-radius: 4px;
}

.grading-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.grading-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score-label {
  color: #606266;
  font-size: 14px;
}

/* 答案对比样式 */
.correct-option {
  background-color: #f6ffed !important;
  border-color: #52c41a !important;
  color: #52c41a;
}

.student-selected {
  background-color: #fff2e8 !important;
  border-color: #fa8c16 !important;
}

.wrong-selected {
  background-color: #fff1f0 !important;
  border-color: #ff4d4f !important;
  color: #ff4d4f;
}

.correct-icon {
  color: #52c41a;
  font-size: 16px;
  margin-left: 8px;
}

.wrong-icon {
  color: #ff4d4f;
  font-size: 16px;
  margin-left: 8px;
}

.answer-section {
  margin-top: 20px;
}

.student-answer-section,
.grading-section {
  margin-bottom: 15px;
}

.student-answer-section h4,
.grading-section h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
}

.answer-content {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
  min-height: 40px;
  white-space: pre-wrap;
}

.blank-answers {
  margin-top: 10px;
}

.student-answer,
.correct-answer {
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 4px;
}

.student-answer.correct {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
}

.student-answer.wrong {
  background-color: #fff1f0;
  border: 1px solid #ffccc7;
  color: #ff4d4f;
}

.correct-answer {
  background-color: #f0f9ff;
  border: 1px solid #91d5ff;
  color: #1890ff;
}

.answer-label {
  font-weight: bold;
  margin-right: 8px;
}
</style>

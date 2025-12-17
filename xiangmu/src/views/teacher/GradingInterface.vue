<template>
  <div class="grading-interface">
    <!-- 查看模式提示 -->
    <el-alert
      v-if="isViewMode"
      title="当前为批阅情况查看模式，不允许修改判卷结果"
      type="info"
      show-icon
      closable
      style="margin-bottom: 20px;"
    />

    <!-- 头部信息 -->
    <div class="grading-header">
      <div class="header-left">
        <el-button @click="goBack" type="info" plain>
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <div class="exam-info">
          <h2>{{ examInfo.examName }}</h2>
          <p>{{ studentInfo.username }} - {{ examInfo.className }}</p>
        </div>
      </div>
      <div class="header-right">
        <el-button 
          type="success" 
          @click="handleSaveGrading" 
          :loading="saving"
          :disabled="isViewMode"
        >
          <el-icon><Check /></el-icon>
          保存判卷
        </el-button>
        <el-button 
          type="primary" 
          @click="handleSubmitGrading" 
          :loading="submitting" 
          :disabled="!isAllGraded || isViewMode"
        >
          <el-icon><Upload /></el-icon>
          {{ isAllGraded ? '提交判卷' : '请完成所有题目判分' }}
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
              :disabled="!hasObjectiveQuestions || isViewMode"
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
              :disabled="isViewMode"
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
                    <div v-html="formatReferenceAnswer(currentQuestion.answers[0].answerContent)"></div>
                  </div>
                  <div v-else class="no-answer">无标准答案</div>
                </div>
              </div>
            </div>
            
            <!-- 程序题显示 -->
            <div v-else-if="currentQuestion.questionType === 'PROGRAMMING'" class="programming-answer">
              <div class="programming-comparison">
                <!-- 学生代码 -->
                <div class="code-section">
                  <div class="code-header">
                    <h5 class="answer-title">学生代码：</h5>
                    <el-tag v-if="getStudentProgrammingLanguage()" type="info" size="small">
                      {{ getLanguageLabel(getStudentProgrammingLanguage()) }}
                    </el-tag>
                  </div>
                  <div v-if="getStudentProgrammingCode()" class="code-content">
                    <pre class="code-block"><code>{{ getStudentProgrammingCode() }}</code></pre>
                  </div>
                  <div v-else class="no-answer">未作答</div>
                  
                  <!-- 运行学生代码 -->
                  <div v-if="getStudentProgrammingCode()" class="code-actions" style="margin-top: 12px;">
                    <el-input
                      v-model="studentTestInput"
                      type="textarea"
                      :rows="2"
                      placeholder="测试输入（可选，每行一个输入）"
                      style="margin-bottom: 8px; font-family: 'Consolas', 'Monaco', 'Courier New', monospace;"
                    />
                    <el-button
                      type="success"
                      size="small"
                      @click="runStudentCode"
                      :loading="studentCodeRunning"
                      :disabled="isViewMode"
                    >
                      <el-icon><Loading v-if="studentCodeRunning" /><CaretRight v-else /></el-icon>
                      运行学生代码
                    </el-button>
                  </div>
                </div>
                
                <!-- 参考答案 -->
                <div class="code-section">
                  <div class="code-header">
                    <h5 class="answer-title">参考答案：</h5>
                    <el-tag v-if="getReferenceProgrammingLanguage()" type="info" size="small">
                      {{ getLanguageLabel(getReferenceProgrammingLanguage()) }}
                    </el-tag>
                  </div>
                  <div v-if="getReferenceProgrammingCode()" class="code-content">
                    <pre class="code-block"><code>{{ getReferenceProgrammingCode() }}</code></pre>
                  </div>
                  <div v-else class="no-answer">无标准答案</div>
                  
                  <!-- 运行参考答案 -->
                  <div v-if="getReferenceProgrammingCode()" class="code-actions" style="margin-top: 12px;">
                    <el-input
                      v-model="referenceTestInput"
                      type="textarea"
                      :rows="2"
                      placeholder="测试输入（可选，每行一个输入）"
                      style="margin-bottom: 8px; font-family: 'Consolas', 'Monaco', 'Courier New', monospace;"
                    />
                    <el-button
                      type="primary"
                      size="small"
                      @click="runReferenceCode"
                      :loading="referenceCodeRunning"
                      :disabled="isViewMode"
                    >
                      <el-icon><Loading v-if="referenceCodeRunning" /><CaretRight v-else /></el-icon>
                      运行参考答案
                    </el-button>
                  </div>
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
                :readonly="isViewMode"
                :disabled="isViewMode"
              />
              <span class="score-label">/ {{ currentQuestion.points }} 分</span>
              <el-button
                v-if="isObjectiveQuestion(currentQuestion.questionType)"
                type="primary"
                size="small"
                @click="autoGradeQuestion"
                :disabled="currentQuestion.isGraded || isViewMode"
              >
                重新判分
              </el-button>
              <el-button
                type="success"
                size="small"
                @click="markAsGraded"
                :disabled="currentQuestion.givenScore === null || isViewMode"
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
    
    <!-- 代码执行结果对话框 -->
    <el-dialog
      v-model="codeResultDialog.visible"
      title="代码执行结果"
      width="60%"
      :close-on-click-modal="false"
    >
      <div class="code-result-content">
        <div class="result-info">
          <el-tag :type="codeResultDialog.result.success ? 'success' : 'danger'">
            {{ codeResultDialog.result.success ? '执行成功' : '执行失败' }}
          </el-tag>
          <span style="margin-left: 12px; color: #909399;">
            执行时间: {{ codeResultDialog.result.executionTimeMs }}ms
          </span>
          <span v-if="codeResultDialog.result.exitCode !== undefined" style="margin-left: 12px; color: #909399;">
            退出码: {{ codeResultDialog.result.exitCode }}
          </span>
        </div>
        
        <div v-if="codeResultDialog.result.output" class="result-output">
          <div class="result-label">输出：</div>
          <el-input
            type="textarea"
            :rows="8"
            :value="codeResultDialog.result.output"
            readonly
            class="result-textarea"
          />
        </div>
        
        <div v-if="codeResultDialog.result.error" class="result-error">
          <div class="result-label">错误：</div>
          <el-input
            type="textarea"
            :rows="6"
            :value="codeResultDialog.result.error"
            readonly
            class="result-textarea error-textarea"
          />
        </div>
      </div>
      
      <template #footer>
        <el-button @click="codeResultDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Check, Upload, Loading, CaretRight
} from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { examApi } from '@/api/admin'
import { executeCode } from '@/api/user'

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
// 程序题相关
const studentTestInput = ref('')
const referenceTestInput = ref('')
const studentCodeRunning = ref(false)
const referenceCodeRunning = ref(false)
const codeResultDialog = ref({
  visible: false,
  result: {
    success: false,
    output: '',
    error: null,
    executionTimeMs: 0,
    exitCode: 0
  }
})

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

// 是否所有题目都已判完
const isAllGraded = computed(() => {
  return questions.value.every(q => q.isGraded)
})

// 是否为查看模式（根据路由参数判断）
const isViewMode = computed(() => {
  // 根据路由参数 mode 区分模式
  return route.query.mode === 'view'
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
      
      // 自动判分客观题（仅在非查看模式下）
      if (!isViewMode.value) {
        autoGradeObjectiveQuestions()
        
        // 自动保存客观题判分结果
        await saveObjectiveGradingResults()
      }
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
  return ['FILL_BLANK', 'SUBJECTIVE', 'PROGRAMMING'].includes(questionType)
}

// 获取题目类型标签
const getQuestionTypeTag = (questionType) => {
  const typeMap = {
    'SINGLE_CHOICE': 'primary',
    'MULTIPLE_CHOICE': 'success',
    'TRUE_FALSE': 'warning',
    'FILL_BLANK': 'info',
    'SUBJECTIVE': 'danger',
    'PROGRAMMING': 'warning'
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
    'SUBJECTIVE': '主观题',
    'PROGRAMMING': '程序题'
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

// 解析并格式化参考答案（支持 JSON 格式）
const formatReferenceAnswer = (answerContent) => {
  if (!answerContent) {
    return '无标准答案'
  }
  
  // 尝试解析 JSON 格式的参考答案
  try {
    // 检查是否是 JSON 字符串格式
    if (typeof answerContent === 'string' && answerContent.trim().startsWith('[')) {
      const parsed = JSON.parse(answerContent)
      if (Array.isArray(parsed) && parsed.length > 0) {
        // 合并所有内容块
        return parsed.map(item => {
          if (item.type === 'TEXT') {
            return item.content || ''
          } else if (item.type === 'CODE') {
            return `<pre><code>${item.content || ''}</code></pre>`
          } else {
            return item.content || ''
          }
        }).join('\n').replace(/\n/g, '<br>')
      }
    }
  } catch (e) {
    // 如果不是 JSON 格式，直接返回原内容
    console.warn('Failed to parse reference answer as JSON:', e)
  }
  
  // 如果不是 JSON 格式，直接返回原内容（进行基本的 HTML 转义）
  return answerContent.replace(/\n/g, '<br>')
}

// 获取学生程序代码
const getStudentProgrammingCode = () => {
  if (!currentQuestion.value || !currentQuestion.value.studentAnswers) {
    return ''
  }
  
  const answer = currentQuestion.value.studentAnswers
  // 学生答案格式可能是 "LANGUAGE:code" 或直接是代码
  if (typeof answer === 'string') {
    // 检查是否是 "LANGUAGE:code" 格式
    const colonIndex = answer.indexOf(':')
    if (colonIndex > 0) {
      return answer.substring(colonIndex + 1)
    }
    return answer
  } else if (Array.isArray(answer) && answer.length > 0) {
    const firstAnswer = answer[0]
    if (typeof firstAnswer === 'string') {
      const colonIndex = firstAnswer.indexOf(':')
      if (colonIndex > 0) {
        return firstAnswer.substring(colonIndex + 1)
      }
      return firstAnswer
    }
  }
  
  return ''
}

// 获取学生选择的编程语言
const getStudentProgrammingLanguage = () => {
  if (!currentQuestion.value || !currentQuestion.value.studentAnswers) {
    return currentQuestion.value?.programmingLanguage || 'JAVA'
  }
  
  const answer = currentQuestion.value.studentAnswers
  // 学生答案格式可能是 "LANGUAGE:code"
  if (typeof answer === 'string') {
    const colonIndex = answer.indexOf(':')
    if (colonIndex > 0) {
      return answer.substring(0, colonIndex)
    }
  } else if (Array.isArray(answer) && answer.length > 0) {
    const firstAnswer = answer[0]
    if (typeof firstAnswer === 'string') {
      const colonIndex = firstAnswer.indexOf(':')
      if (colonIndex > 0) {
        return firstAnswer.substring(0, colonIndex)
      }
    }
  }
  
  return currentQuestion.value?.programmingLanguage || 'JAVA'
}

// 获取参考答案代码
const getReferenceProgrammingCode = () => {
  if (!currentQuestion.value) {
    return ''
  }
  
  // 优先从 answers 数组获取
  if (currentQuestion.value.answers && currentQuestion.value.answers.length > 0) {
    const answerContent = currentQuestion.value.answers[0].answerContent
    if (answerContent) {
      return answerContent
    }
  }
  
  // 如果没有，尝试从 correctAnswer 字段获取
  if (currentQuestion.value.correctAnswer) {
    return currentQuestion.value.correctAnswer
  }
  
  return ''
}

// 获取参考答案的编程语言
const getReferenceProgrammingLanguage = () => {
  return currentQuestion.value?.programmingLanguage || 'JAVA'
}

// 获取语言标签
const getLanguageLabel = (language) => {
  const langMap = {
    'JAVA': 'Java',
    'PYTHON': 'Python',
    'CPP': 'C++',
    'C++': 'C++',  // 兼容性：后端支持两种格式，保留以防万一
    'C': 'C'
  }
  return langMap[language] || language || 'Java'
}

// 运行学生代码
const runStudentCode = async () => {
  if (isViewMode.value) return
  
  const code = getStudentProgrammingCode()
  if (!code || code.trim() === '') {
    ElMessage.warning('学生代码为空')
    return
  }
  
  const language = getStudentProgrammingLanguage()
  const testInput = studentTestInput.value || ''
  
  studentCodeRunning.value = true
  
  try {
    const response = await executeCode({
      code: code,
      language: language,
      input: testInput.trim(),
      timeoutSeconds: 10
    })
    
    if (response.code === 200 && response.data) {
      codeResultDialog.value.result = {
        success: response.data.success || false,
        output: response.data.output || '',
        error: response.data.error || null,
        executionTimeMs: response.data.executionTimeMs || 0,
        exitCode: response.data.exitCode || 0
      }
      codeResultDialog.value.visible = true
    } else {
      ElMessage.error('执行失败: ' + (response.message || '未知错误'))
    }
  } catch (error) {
    console.error('Code execution error:', error)
    ElMessage.error('执行失败: ' + (error.message || '网络错误，请稍后重试'))
  } finally {
    studentCodeRunning.value = false
  }
}

// 运行参考答案
const runReferenceCode = async () => {
  if (isViewMode.value) return
  
  const code = getReferenceProgrammingCode()
  if (!code || code.trim() === '') {
    ElMessage.warning('参考答案为空')
    return
  }
  
  const language = getReferenceProgrammingLanguage()
  const testInput = referenceTestInput.value || ''
  
  referenceCodeRunning.value = true
  
  try {
    const response = await executeCode({
      code: code,
      language: language,
      input: testInput.trim(),
      timeoutSeconds: 10
    })
    
    if (response.code === 200 && response.data) {
      codeResultDialog.value.result = {
        success: response.data.success || false,
        output: response.data.output || '',
        error: response.data.error || null,
        executionTimeMs: response.data.executionTimeMs || 0,
        exitCode: response.data.exitCode || 0
      }
      codeResultDialog.value.visible = true
    } else {
      ElMessage.error('执行失败: ' + (response.message || '未知错误'))
    }
  } catch (error) {
    console.error('Code execution error:', error)
    ElMessage.error('执行失败: ' + (error.message || '网络错误，请稍后重试'))
  } finally {
    referenceCodeRunning.value = false
  }
}


// 保存客观题判分结果
const saveObjectiveGradingResults = async () => {
  if (isViewMode.value) return
  
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
  if (isViewMode.value) return
  
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
    // 单选题和判断题：选对得满分，选错/未作答得0分（不再返回null）
    const isCorrect = studentAnswers.some(answer => 
      question.options.some(opt => opt.optionContent === answer && opt.isCorrect)
    )
    return isCorrect ? question.points : 0
  } else if (questionType === 'MULTIPLE_CHOICE') {
    // 多选题评分规则：未作答得0分，有错选得0分，全对得满分，部分选对得60%分
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
    } else {
      return 0 // 未作答得0分（不再返回null）
    }
  }
  
  return 0 // 其他客观题类型默认得0分
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

// 自动判分所有客观题（辅助优化：确保标记为已判）
const autoGradeObjectiveQuestions = () => {
  if (isViewMode.value) return
  
  questions.value.forEach((question, index) => {
    if (isObjectiveQuestion(question.questionType)) {
      const score = calculateObjectiveScore(question)
      question.givenScore = score
      question.isGraded = true // 客观题只要判分就标记为已判（包括0分）
    }
  })
  ElMessage.success('客观题自动判分完成（未作答题目已判0分）')
}

// 标记为已判卷
const markAsGraded = () => {
  if (isViewMode.value) return
  
  if (currentQuestion.value) {
    currentQuestion.value.isGraded = true
  }
}

// 保存判卷结果
const handleSaveGrading = () => {
  if (isViewMode.value) return
  
  saveDialogVisible.value = true
}

// 确认保存
const confirmSave = async () => {
  if (isViewMode.value) return
  
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
  if (isViewMode.value) return
  
  // 显示详细的判卷摘要信息
  const gradingSummary = generateGradingSummary()
  
  // 检查是否所有题目都已判完
  if (gradingSummary.ungradedCount > 0) {
    ElMessageBox.alert(
      `还有 ${gradingSummary.ungradedCount} 道题目未判分，请完成所有题目的判分后再提交！`,
      '判卷未完成',
      {
        confirmButtonText: '我知道了',
        type: 'warning',
        showClose: true
      }
    )
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `<div style="text-align: left; line-height: 1.8; font-size: 16px;">
        <p style="margin: 0 0 15px 0; font-weight: bold; color: #e6a23c;">请仔细核对以下判卷信息：</p>
        <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 10px 0;">
          <p style="margin: 8px 0;"><strong>学生姓名：</strong>${studentInfo.value.name}</p>
          <p style="margin: 8px 0;"><strong>学生学号：</strong>${studentInfo.value.username}</p>
          <p style="margin: 8px 0;"><strong>考试名称：</strong>${examInfo.value.examName}</p>
          <p style="margin: 8px 0;"><strong>班级名称：</strong>${examInfo.value.className}</p>
          <p style="margin: 8px 0;"><strong>得分情况：</strong>${currentTotalScore.value.toFixed(1)}/${examInfo.value.totalPoints}分</p>
          <p style="margin: 8px 0;"><strong>判卷进度：</strong>${gradingSummary.gradedCount}/${gradingSummary.totalCount}题</p>
          <p style="margin: 8px 0;"><strong>未判题目：</strong>${gradingSummary.ungradedCount}题</p>
        </div>
        <p style="margin: 15px 0 0 0; color: #f56c6c; font-weight: bold;">⚠️ 提交后将无法修改，请确认无误后再提交！</p>
      </div>`,
      '提交判卷确认',
      {
        confirmButtonText: '确认提交',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        customClass: 'grading-confirm-dialog',
        showClose: true,
        closeOnClickModal: false,
        closeOnPressEscape: false
      }
    )
    
    // 二次确认
    await ElMessageBox.confirm(
      `最后确认：\n\n` +
      `学生：${studentInfo.value.username}\n` +
      `得分：${currentTotalScore.value.toFixed(1)}分\n\n` +
      `确定要提交此判卷结果吗？`,
      '最终确认',
      {
        confirmButtonText: '确定提交',
        cancelButtonText: '取消',
        type: 'error',
        showClose: true,
        closeOnClickModal: false,
        closeOnPressEscape: false
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

// 生成判卷摘要信息
const generateGradingSummary = () => {
  const totalCount = questions.value.length
  const gradedCount = questions.value.filter(q => q.isGraded).length
  const ungradedCount = totalCount - gradedCount
  
  return {
    totalCount,
    gradedCount,
    ungradedCount
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
  white-space: pre-line; /* 保留换行符，合并多余空格 */
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

/* 判卷确认对话框样式 */
:deep(.grading-confirm-dialog) {
  width: 650px !important;
  max-width: 90vw !important;
  
  .el-message-box__content {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    padding: 30px 35px;
  }
  
  .el-message-box__title {
    color: #e6a23c;
    font-weight: bold;
    font-size: 22px;
    margin-bottom: 25px;
  }
  
  .el-message-box__message {
    text-align: left;
    color: #303133;
    line-height: 1.8;
    font-size: 16px;
  }
  
  .el-message-box__btns {
    padding: 25px 35px 35px;
    text-align: center;
  }
  
  .el-button {
    min-width: 130px;
    height: 50px;
    font-size: 16px;
    font-weight: bold;
    margin: 0 15px;
  }
  
  .el-button--primary {
    background-color: #f56c6c;
    border-color: #f56c6c;
  }
  
  .el-button--primary:hover {
    background-color: #f78989;
    border-color: #f78989;
  }
}

/* 程序题样式 */
.programming-comparison {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.code-section {
  flex: 1;
  min-width: 0;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 16px;
  background-color: #fafafa;
}

.code-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.code-content {
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 12px;
  max-height: 400px;
  overflow-y: auto;
}

.code-block {
  margin: 0;
  padding: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: #303133;
}

.code-actions {
  margin-top: 12px;
}

/* 代码执行结果对话框样式 */
.code-result-content {
  padding: 10px 0;
}

.result-info {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.result-label {
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  font-size: 14px;
}

.result-output,
.result-error {
  margin-bottom: 20px;
}

.result-textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
}

.result-textarea.error-textarea :deep(.el-textarea__inner) {
  background-color: #fef0f0;
  border-color: #f56c6c;
  color: #f56c6c;
}

.result-textarea :deep(.el-textarea__inner) {
  background-color: #f5f7fa;
  color: #303133;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}
</style>
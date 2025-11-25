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
            <!-- 题目图片 -->
            <div v-if="question.images && getImageUrls(question.images).length > 0" class="question-images">
              <div class="images-grid">
                <div v-for="(imageUrl, imgIndex) in getImageUrls(question.images)" :key="imgIndex" class="image-item">
                  <el-image
                    :src="imageUrl"
                    :preview-src-list="getImageUrls(question.images)"
                    :initial-index="imgIndex"
                    fit="contain"
                    style="width: 100%; max-height: 400px; cursor: pointer; margin-bottom: 16px;"
                  >
                    <template #error>
                      <div class="image-error">图片加载失败</div>
                    </template>
                  </el-image>
                </div>
              </div>
            </div>
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

          <!-- 程序题 -->
          <div v-else-if="question.questionType === 'PROGRAMMING'" class="programming-answer">
            <!-- 题目图片 -->
            <div v-if="question.images && getImageUrls(question.images).length > 0" class="question-images">
              <div class="images-grid">
                <div v-for="(imageUrl, imgIndex) in getImageUrls(question.images)" :key="imgIndex" class="image-item">
                  <el-image
                    :src="imageUrl"
                    :preview-src-list="getImageUrls(question.images)"
                    :initial-index="imgIndex"
                    fit="contain"
                    style="width: 100%; max-height: 400px; cursor: pointer; margin-bottom: 16px;"
                  >
                    <template #error>
                      <div class="image-error">图片加载失败</div>
                    </template>
                  </el-image>
                </div>
              </div>
            </div>
            
            <!-- 编程语言选择器 -->
            <div class="language-selector" style="margin-bottom: 12px;">
              <el-select
                v-model="selectedLanguages[index]"
                placeholder="选择编程语言"
                @change="onLanguageChange(index, $event)"
                style="width: 200px;"
              >
                <el-option label="Java" value="JAVA" />
                <el-option label="Python" value="PYTHON" />
                <el-option label="C++" value="CPP" />
                <el-option label="C" value="C" />
              </el-select>
              <span style="margin-left: 12px; color: #606266; font-size: 14px;">
                当前语言：{{ getLanguageLabel(selectedLanguages[index] || question.programmingLanguage || 'JAVA') }}
              </span>
            </div>
            
            <!-- 代码编辑器 -->
            <div class="code-editor-container">
              <div :id="'code-editor-' + index" class="code-editor"></div>
            </div>
            
            <!-- 测试用例列表 -->
            <div v-if="questions[index].testCases && questions[index].testCases.length > 0" class="test-cases-container" style="margin-top: 12px; margin-bottom: 12px;">
              <div style="margin-bottom: 8px; font-size: 14px; color: #606266; font-weight: 500;">
                测试用例（{{ questions[index].testCases.length }} 个）：
              </div>
              <div v-for="(testCase, testCaseIndex) in questions[index].testCases" :key="testCaseIndex" 
                   class="test-case-item" 
                   style="margin-bottom: 12px; padding: 12px; background: #f5f7fa; border-radius: 4px; border: 1px solid #e4e7ed;">
                <div style="display: flex; align-items: center; margin-bottom: 8px;">
                  <span style="font-weight: 500; color: #303133;">用例 {{ testCaseIndex + 1 }}：</span>
                  <el-tag 
                    v-if="testCaseResults[index] && testCaseResults[index][testCaseIndex]"
                    :type="testCaseResults[index][testCaseIndex].passed ? 'success' : 'danger'"
                    size="small"
                    style="margin-left: 8px;"
                  >
                    {{ testCaseResults[index][testCaseIndex].passed ? '通过' : '失败' }}
                  </el-tag>
                </div>
                <div style="margin-bottom: 6px;">
                  <span style="color: #909399; font-size: 12px;">输入：</span>
                  <pre style="display: inline-block; margin: 0; padding: 4px 8px; background: #fff; border-radius: 2px; font-family: 'Consolas', 'Monaco', 'Courier New', monospace; font-size: 12px;">{{ testCase.input || '(无)' }}</pre>
                </div>
                <div>
                  <span style="color: #909399; font-size: 12px;">期望输出：</span>
                  <pre style="display: inline-block; margin: 0; padding: 4px 8px; background: #fff; border-radius: 2px; font-family: 'Consolas', 'Monaco', 'Courier New', monospace; font-size: 12px;">{{ testCase.output || '(无)' }}</pre>
                </div>
                <div v-if="testCaseResults[index] && testCaseResults[index][testCaseIndex] && !testCaseResults[index][testCaseIndex].passed" 
                     style="margin-top: 6px; padding-top: 6px; border-top: 1px solid #e4e7ed;">
                  <span style="color: #f56c6c; font-size: 12px;">实际输出：</span>
                  <pre style="display: inline-block; margin: 0; padding: 4px 8px; background: #fef0f0; border-radius: 2px; font-family: 'Consolas', 'Monaco', 'Courier New', monospace; font-size: 12px; color: #f56c6c;">{{ testCaseResults[index][testCaseIndex].actualOutput || '(无)' }}</pre>
                </div>
              </div>
            </div>
            
            <div class="save-button-container">
              <el-button 
                v-if="questions[index].testCases && questions[index].testCases.length > 0"
                type="success" 
                size="small" 
                @click="runAllTestCases(index)"
                :loading="codeRunning[index]"
                :disabled="!programmingAnswers[index] || programmingAnswers[index].trim() === ''"
                style="margin-right: 12px;"
              >
                <el-icon><Loading v-if="codeRunning[index]" /><CaretRight v-else /></el-icon>
                运行所有测试用例
              </el-button>
              <el-button 
                type="primary" 
                size="small" 
                @click="saveProgrammingCode(index)"
                :disabled="!programmingAnswers[index] || programmingAnswers[index].trim() === ''"
              >
                <el-icon><Check /></el-icon>
                保存代码
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
          type="warning" 
          size="large" 
          :loading="submitting"
          @click="saveDraft"
        >
          <el-icon><Check /></el-icon>
          保存答卷
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
import { ref, computed, onMounted, onUnmounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Edit, View, Check, Refresh, Loading, CaretRight } from '@element-plus/icons-vue'
import { studentExamApi } from '@/api/admin'
import { executeCode } from '@/api/user'
import serverTimeSync from '@/utils/serverTime'

// 导入 CodeMirror 6（轻量级、易配置、性能好，无需 worker 配置）
import { EditorView, lineNumbers, keymap } from '@codemirror/view'
import { EditorState } from '@codemirror/state'
import { defaultKeymap, indentWithTab } from '@codemirror/commands'
import { java } from '@codemirror/lang-java'
import { python } from '@codemirror/lang-python'
import { cpp } from '@codemirror/lang-cpp'

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
const programmingAnswers = ref({}) // 程序题答案
const selectedLanguages = ref({}) // 每个程序题选择的编程语言
const codeEditors = ref({}) // CodeMirror 6 编辑器实例
const autoSaveTimer = ref(null)
const lastSaveTime = ref(null)
const isAutoSaving = ref(false)
const lastAutoSaveAt = ref(null)
const codeRunning = ref({}) // 每个程序题的运行状态
const testCaseResults = ref({}) // 每个程序题的测试用例执行结果 { [questionIndex]: { [testCaseIndex]: { passed, actualOutput } } }
const codeResultDialog = ref({ // 代码执行结果对话框
  visible: false,
  result: {
    success: false,
    output: '',
    error: null,
    executionTimeMs: 0,
    exitCode: 0
  }
})

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

// 计算未答题目数量
const unansweredCount = computed(() => {
  return questions.value.length - answeredCount.value
})

// 计算是否所有题目都已作答
const isAllAnswered = computed(() => {
  return answeredCount.value === questions.value.length && questions.value.length > 0
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
  
  const now = serverTimeSync.getServerTime()
  const diff = now - (saveTime instanceof Date ? saveTime : new Date(saveTime))
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

// 获取图片URL列表
const getImageUrls = (images) => {
  if (!images) return []
  
  // 如果是数组，直接返回
  if (Array.isArray(images)) {
    return images
  }
  
  // 如果是字符串，按分号或逗号分割，并添加后端地址前缀
  if (typeof images === 'string') {
    const urls = images.split(/[;,]/).filter(url => url.trim()).map(url => url.trim())
    // 添加后端地址前缀 (http://localhost:8080)
    return urls.map(url => url.startsWith('http') ? url : `http://localhost:8080${url}`)
  }
  
  return []
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
    'SUBJECTIVE': '主观题',
    'PROGRAMMING': '程序题'
  }
  return typeMap[questionType] || '未知题型'
}

// 开始计时器
const startTimer = () => {
  timer = setInterval(() => {
    // 重新计算剩余时间（基于个人有效截止时间，使用服务器时间）
    const now = serverTimeSync.getServerTime()
    const windowEnd = new Date(examInfo.value.endTime)
    let effectiveEnd = windowEnd
    if (examInfo.value.allowedEndTime) {
      const allowedEnd = new Date(examInfo.value.allowedEndTime)
      effectiveEnd = allowedEnd < windowEnd ? allowedEnd : windowEnd
    }
    const remainingSeconds = Math.max(0, Math.floor((effectiveEnd - now) / 1000))
    
    timeLeft.value = remainingSeconds
    
    if (remainingSeconds <= 0) {
      // 时间到，自动提交答案
      clearInterval(timer)
      ElMessage.warning('考试时间已到，系统将自动提交您的答案')
      // 自动提交答案
      autoSubmitOnTimeUp()
    }
  }, 1000)
}

// 加载考试数据
const loadExamData = async () => {
  try {
    const examId = route.params.id
    
    // 清空之前考试的答案数据，防止不同考试之间的数据混淆
    selectedAnswers.value = {}
    fillBlankAnswers.value = {}
    essayAnswers.value = {}
    programmingAnswers.value = {}
    selectedLanguages.value = {}
    codeEditors.value = {}
    
    // 停止之前的定时器
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    if (autoSaveTimer.value) {
      clearTimeout(autoSaveTimer.value)
      autoSaveTimer.value = null
    }
    
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
        studentStartTime: paperData.studentStartTime,
        allowedEndTime: paperData.allowedEndTime,
        isRandomOrder: paperData.isRandomOrder,
        isRandomOptions: paperData.isRandomOptions
      }
      
      // 检查考试是否已结束（使用服务器时间）
      const now = serverTimeSync.getServerTime()
      const endTime = new Date(examInfo.value.endTime)
      if (now > endTime) {
        ElMessage.error('考试已结束')
        router.push('/user/exam')
        return
      }
      
      // 设置题目数据
      questions.value = paperData.questions.map((q, idx) => {
        // 初始化程序题的语言选择（如果没有保存过，使用默认值）
        if (q.questionType === 'PROGRAMMING') {
          const defaultLanguage = q.programmingLanguage || 'JAVA'
          if (!selectedLanguages.value[idx]) {
            selectedLanguages.value[idx] = defaultLanguage
          }
        }
        return {
          questionId: q.questionId,
          questionOrder: q.questionOrder,
          questionType: q.questionType,
          questionText: q.questionContent,
          points: q.points,
          difficulty: q.difficulty,
          options: q.options || [],
          answers: q.answers || [],
          images: q.images || null,
          programmingLanguage: q.programmingLanguage || 'JAVA',
          testCases: q.testCases || [] // 添加测试用例
        }
      })
      
      // 初始化测试用例结果
      questions.value.forEach((q, idx) => {
        if (q.questionType === 'PROGRAMMING' && q.testCases && q.testCases.length > 0) {
          testCaseResults.value[idx] = {}
        }
      })
      
      // 计算剩余时间（基于个人有效截止时间：窗口截止与个人时长截止的较早者，使用服务器时间）
      const allowedEnd = paperData.allowedEndTime ? new Date(paperData.allowedEndTime) : endTime
      const remainingSeconds = Math.max(0, Math.floor((allowedEnd - now) / 1000))
      timeLeft.value = remainingSeconds
      
      // 开始计时
      startTimer()
      
      // 异步初始化程序题代码编辑器（不阻塞界面）
      // 完全异步，使用 setTimeout 延迟加载，确保页面先完全渲染出来
      // 不等待 nextTick，直接异步执行
      // 大幅延迟编辑器初始化，确保页面完全可交互后再加载
      setTimeout(() => {
        const programmingQuestions = questions.value
          .map((q, index) => ({ q, index }))
          .filter(({ q }) => q.questionType === 'PROGRAMMING')
        
        // 如果没有程序题，直接返回
        if (programmingQuestions.length === 0) {
          return
        }
        
        // 逐个初始化编辑器，间隔更长，避免一次性加载
        programmingQuestions.forEach(({ q, index }, idx) => {
          // 每个编辑器间隔更长（500ms），给浏览器更多时间渲染和响应
          setTimeout(() => {
            const language = selectedLanguages.value[index] || q.programmingLanguage || 'JAVA'
            // 完全异步初始化，不阻塞，不等待结果
            initCodeEditor(index, language).catch(err => {
              console.error(`Failed to init code editor for question ${index}:`, err)
            })
          }, idx * 500) // 每个编辑器间隔500ms加载，确保不阻塞
        })
      }, 1500) // 页面渲染完成后再加载编辑器（延迟更长，确保页面完全可交互）
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

// 获取程序题答案
const getProgrammingAnswer = (questionIndex) => {
  return programmingAnswers.value[questionIndex] || ''
}

// 判断题目是否已答
const isQuestionAnswered = (questionIndex) => {
  const question = questions.value[questionIndex]
  if (!question) return false
  
  if (question.questionType === 'FILL_BLANK') {
    return fillBlankAnswers.value[questionIndex] && fillBlankAnswers.value[questionIndex].trim() !== ''
  } else if (question.questionType === 'SUBJECTIVE') {
    return essayAnswers.value[questionIndex] && essayAnswers.value[questionIndex].trim() !== ''
  } else if (question.questionType === 'PROGRAMMING') {
    return programmingAnswers.value[questionIndex] && programmingAnswers.value[questionIndex].trim() !== ''
  } else {
    return selectedAnswers.value[questionIndex] && selectedAnswers.value[questionIndex].length > 0
  }
}

// 获取编程语言标签
const getLanguageLabel = (language) => {
  const languageMap = {
    'JAVA': 'Java',
    'PYTHON': 'Python',
    'CPP': 'C++',
    'C': 'C',
    'JAVASCRIPT': 'JavaScript'
  }
  return languageMap[language] || language
}

// 获取语言扩展（CodeMirror 6）
const getLanguageExtension = (language) => {
  const langMap = {
    'JAVA': java(),
    'java': java(),
    'PYTHON': python(),
    'python': python(),
    'CPP': cpp(),
    'cpp': cpp(),
    'c++': cpp(),
    'C': cpp(),  // C 语言使用 cpp 支持（语法相似）
    'c': cpp()
  }
  return langMap[language] || java() // 默认 Java
}

// 编程语言改变时的处理（CodeMirror 6 切换语言很简单）
const onLanguageChange = (questionIndex, newLanguage) => {
  const question = questions.value[questionIndex]
  if (!question || question.questionType !== 'PROGRAMMING') {
    return
  }
  
  // 更新选择的语言
  selectedLanguages.value[questionIndex] = newLanguage
  
  // 清除该题目的测试用例结果（切换语言后，之前的测试结果不准确）
  if (testCaseResults.value[questionIndex]) {
    testCaseResults.value[questionIndex] = {}
  }
  
  // CodeMirror 6 切换语言：保存代码、销毁旧编辑器、重新初始化
  const editor = codeEditors.value[questionIndex]
  if (editor && editor instanceof EditorView) {
    // 获取当前代码并保存（确保代码已保存）
    const currentCode = editor.state.doc.toString()
    programmingAnswers.value[questionIndex] = currentCode
    
    // 销毁旧编辑器
    try {
      editor.destroy()
    } catch (e) {
      console.warn('Failed to destroy old editor:', e)
    }
    codeEditors.value[questionIndex] = null
    
    // 重新初始化编辑器（使用新语言）
    // 延迟执行，确保旧编辑器完全销毁
    setTimeout(() => {
      // 初始化编辑器时会自动使用 programmingAnswers 中保存的代码
      initCodeEditor(questionIndex, newLanguage).catch(err => {
        console.error('Failed to reinit editor with new language:', err)
      })
    }, 50)
  } else {
    // 如果编辑器不存在，初始化
    initCodeEditor(questionIndex, newLanguage)
  }
}

// 初始化代码编辑器（完全异步，不阻塞界面）
// 返回 Promise，以便调用者可以等待初始化完成
const initCodeEditor = (questionIndex, language = 'java') => {
  // 检查是否已经初始化，避免重复初始化
  if (codeEditors.value[questionIndex]) {
    // 如果编辑器已存在，直接返回 resolved Promise
    return Promise.resolve(codeEditors.value[questionIndex])
  }
  
  return new Promise((resolve, reject) => {
    // 使用 requestIdleCallback 或 setTimeout，确保在主线程空闲时执行
    // 避免阻塞 UI 渲染
    const scheduleInit = () => {
      try {
        // 检查元素是否存在
        const editorId = `code-editor-${questionIndex}`
        let editorElement = document.getElementById(editorId)
        
        // 如果元素不存在，延迟重试
        if (!editorElement) {
          setTimeout(scheduleInit, 500)
          return
        }
        
        // 元素存在，异步初始化（不阻塞）
        // 使用 setTimeout 确保不阻塞主线程
        setTimeout(() => {
          continueEditorInit(questionIndex, language, editorElement, editorId)
            .then(editor => {
              resolve(editor)
            })
            .catch(err => {
              console.error(`Failed to init editor for question ${questionIndex}:`, err)
              // 如果初始化失败，显示错误提示但不清空已有的代码
              reject(err)
            })
        }, 100)
      } catch (error) {
        console.error(`Error initializing code editor for question ${questionIndex}:`, error)
        reject(error)
      }
    }
    
    // 延迟执行，确保页面已渲染
    if (window.requestIdleCallback) {
      requestIdleCallback(scheduleInit, { timeout: 2000 })
    } else {
      setTimeout(scheduleInit, 200)
    }
  })
}

// 继续编辑器初始化（CodeMirror 6 实现）
const continueEditorInit = async (questionIndex, language, editorElement, editorId) => {
  // 再次检查编辑器是否已经存在（防止重复初始化）
  if (codeEditors.value[questionIndex]) {
    return codeEditors.value[questionIndex]
  }
  
  try {
    // 再次检查元素（可能在等待期间被删除）
    const element = document.getElementById(editorId)
    if (!element) {
      const error = new Error(`Code editor element not found: ${editorId}`)
      console.warn(error.message)
      throw error
    }
    
    // 再次检查是否已有编辑器实例（防止并发初始化）
    if (codeEditors.value[questionIndex]) {
      return codeEditors.value[questionIndex]
    }
    
    // 获取当前代码内容或使用默认模板
    const currentCode = programmingAnswers.value[questionIndex] || getDefaultCodeTemplate(language.toLowerCase())
    
    // 创建 CodeMirror 6 编辑器状态
    const state = EditorState.create({
      doc: currentCode,
      extensions: [
        lineNumbers(),
        keymap.of([...defaultKeymap, indentWithTab]),
        getLanguageExtension(language),
        EditorView.updateListener.of((update) => {
          if (update.docChanged) {
            const code = update.state.doc.toString()
            programmingAnswers.value[questionIndex] = code
            // 同步到selectedAnswers用于提交
            if (!selectedAnswers.value[questionIndex]) {
              selectedAnswers.value[questionIndex] = []
            }
            selectedAnswers.value[questionIndex] = [code]
            
            // 防抖：延迟自动保存，避免频繁保存
            if (element.saveTimer) {
              clearTimeout(element.saveTimer)
            }
            element.saveTimer = setTimeout(() => {
              autoSave()
            }, 2000) // 2秒后才保存
          }
        }),
        // 编辑器样式配置
        EditorView.theme({
          '&': {
            fontSize: '14px',
            minHeight: '300px',
            height: '400px'
          },
          '.cm-content': {
            fontFamily: 'Consolas, Monaco, "Courier New", monospace',
            padding: '12px'
          },
          '.cm-editor': {
            border: '1px solid #dcdfe6',
            borderRadius: '4px'
          }
        })
      ]
    })
    
    // 创建 CodeMirror 6 编辑器实例（非常简单，不需要 worker 配置）
    let editor
    try {
      editor = new EditorView({
        state: state,
        parent: element
      })
    } catch (createError) {
      console.error('Failed to create CodeMirror editor:', createError)
      ElMessage.error('代码编辑器初始化失败，请刷新页面重试')
      throw createError
    }
    
    codeEditors.value[questionIndex] = editor
    return editor
  } catch (error) {
    console.error(`Error in continueEditorInit for question ${questionIndex}:`, error)
    throw error // 重新抛出错误，让调用者处理
  }
}

// 获取默认代码模板
const getDefaultCodeTemplate = (language) => {
  const templates = {
    'java': `public class Solution {\n    public static void main(String[] args) {\n        // 在此处编写你的代码\n    }\n}`,
    'python': `# 在此处编写你的代码\ndef solution():\n    pass\n\nif __name__ == '__main__':\n    solution()`,
    'cpp': `#include <iostream>\nusing namespace std;\n\nint main() {\n    // 在此处编写你的代码\n    return 0;\n}`,
    'c': `#include <stdio.h>\n\nint main() {\n    // 在此处编写你的代码\n    return 0;\n}`,
    'javascript': `// 在此处编写你的代码\nfunction solution() {\n    \n}\n\nsolution();`
  }
  return templates[language] || templates['java']
}

// 保存程序题代码
const saveProgrammingCode = (questionIndex) => {
  const code = programmingAnswers.value[questionIndex]
  if (!code || code.trim() === '') {
    ElMessage.warning('代码不能为空')
    return
  }
  
  // 保存到selectedAnswers用于提交
  if (!selectedAnswers.value[questionIndex]) {
    selectedAnswers.value[questionIndex] = []
  }
  selectedAnswers.value[questionIndex] = [code]
  
  // 保存到本地
  saveAnswersToLocal()
  
  // 保存到服务器
  saveDraftToServer(false)
  
  ElMessage.success('代码已保存')
}

// 比较输出（去除末尾空白字符和换行符）
const normalizeOutput = (output) => {
  if (!output) return ''
  return output.trimEnd().replace(/\r\n/g, '\n')
}

// 运行所有测试用例
const runAllTestCases = async (questionIndex) => {
  const question = questions.value[questionIndex]
  if (!question || question.questionType !== 'PROGRAMMING') {
    ElMessage.warning('该题目不是程序题')
    return
  }
  
  if (!question.testCases || question.testCases.length === 0) {
    ElMessage.warning('该题目没有测试用例')
    return
  }
  
  const code = programmingAnswers.value[questionIndex]
  if (!code || code.trim() === '') {
    ElMessage.warning('代码不能为空')
    return
  }
  
  const language = selectedLanguages.value[questionIndex] || question.programmingLanguage || 'JAVA'
  
  // 设置运行状态
  codeRunning.value[questionIndex] = true
  
  // 初始化测试用例结果
  if (!testCaseResults.value[questionIndex]) {
    testCaseResults.value[questionIndex] = {}
  }
  
  try {
    // 逐个执行测试用例
    for (let i = 0; i < question.testCases.length; i++) {
      const testCase = question.testCases[i]
      const testInput = testCase.input || ''
      
      // 调用代码执行 API
      const response = await executeCode({
        code: code,
        language: language,
        input: testInput.trim(),
        timeoutSeconds: 10
      })
      
      // 比较实际输出和期望输出
      if (response.code === 200 && response.data) {
        const actualOutput = normalizeOutput(response.data.output || '')
        const expectedOutput = normalizeOutput(testCase.output || '')
        const passed = actualOutput === expectedOutput && response.data.success
        
        testCaseResults.value[questionIndex][i] = {
          passed: passed,
          actualOutput: response.data.output || '',
          error: response.data.error || null,
          executionTimeMs: response.data.executionTimeMs || 0
        }
      } else {
        testCaseResults.value[questionIndex][i] = {
          passed: false,
          actualOutput: '',
          error: response.message || '执行失败',
          executionTimeMs: 0
        }
      }
    }
    
    // 统计通过数量
    const passedCount = Object.values(testCaseResults.value[questionIndex]).filter(r => r.passed).length
    const totalCount = question.testCases.length
    
    if (passedCount === totalCount) {
      ElMessage.success(`所有测试用例通过！(${passedCount}/${totalCount})`)
    } else {
      ElMessage.warning(`部分测试用例未通过：${passedCount}/${totalCount}`)
    }
  } catch (error) {
    console.error('Code execution error:', error)
    ElMessage.error('执行失败: ' + (error.message || '网络错误，请稍后重试'))
  } finally {
    // 清除运行状态
    codeRunning.value[questionIndex] = false
  }
}

// 组装提交/保存数据
const buildSubmitPayload = () => {
  const allAnswers = {}
  Object.keys(selectedAnswers.value).forEach(key => { allAnswers[key] = selectedAnswers.value[key] })
  Object.keys(fillBlankAnswers.value).forEach(key => { allAnswers[key] = fillBlankAnswers.value[key] })
  Object.keys(essayAnswers.value).forEach(key => { allAnswers[key] = essayAnswers.value[key] })
  
  // 处理程序题答案（包含代码和选择的语言）
  Object.keys(programmingAnswers.value).forEach(key => {
    const questionIndex = parseInt(key)
    const question = questions.value[questionIndex]
    if (question && question.questionType === 'PROGRAMMING') {
      const code = programmingAnswers.value[questionIndex]
      const language = selectedLanguages.value[questionIndex] || question.programmingLanguage || 'JAVA'
      // 将代码和语言信息一起保存（格式：LANGUAGE:code）
      allAnswers[key] = [`${language}:${code}`]
    }
  })

  const paperContent = {
    questions: questions.value.map((q, index) => {
      const questionData = {
        questionId: q.questionId,
        questionOrder: q.questionOrder,
        questionType: q.questionType,
        questionContent: q.questionText || q.questionContent,
        points: q.points,
        options: q.options || []
      }
      // 如果是程序题，保存选择的语言
      if (q.questionType === 'PROGRAMMING') {
        questionData.selectedLanguage = selectedLanguages.value[index] || q.programmingLanguage || 'JAVA'
      }
      return questionData
    })
  }

  return { answers: allAnswers, paperContent }
}

// 将当前答题保存到服务器（草稿）
const saveDraftToServer = async (isAuto = false) => {
  try {
    // 仅在自动保存时进行并发拦截，手动保存不拦截
    if (isAuto && (submitting.value || isAutoSaving.value)) return
    isAutoSaving.value = true
    const examId = route.params.id
    const payload = buildSubmitPayload()
    const resp = await studentExamApi.saveStudentExamDraft(examId, payload)
    if (resp.code === 200) {
      saveAnswersToLocal()
      lastAutoSaveAt.value = serverTimeSync.getServerTime()
      if (!isAuto) {
        ElMessage.success('保存成功')
      }
    }
  } catch (e) {
    if (!isAuto) {
      ElMessage.error('保存失败')
    }
    console.error('saveDraftToServer error:', e)
  } finally {
    isAutoSaving.value = false
  }
}

// 自动保存（选择题）→ 调用后端草稿保存
const autoSave = () => {
  if (autoSaveTimer.value) {
    clearTimeout(autoSaveTimer.value)
  }
  autoSaveTimer.value = setTimeout(() => {
    // 静默自动保存到服务器
    saveDraftToServer(true)
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
    
    // 确保examId存在
    if (!examId) {
      console.warn('Exam ID not found in route params, cannot save')
      return
    }
    
    const saveData = {
      examId: examId, // 添加examId用于验证
      selectedAnswers: selectedAnswers.value,
      fillBlankAnswers: fillBlankAnswers.value,
      essayAnswers: essayAnswers.value,
      programmingAnswers: programmingAnswers.value, // 包含程序题答案
      selectedLanguages: selectedLanguages.value, // 包含选择的编程语言
      saveTime: serverTimeSync.getServerTime().toISOString()
    }
    
    console.log('ExamPreview - Saving to local storage for exam:', examId, 'Data:', saveData)
    
    localStorage.setItem(`exam_${examId}_answers`, JSON.stringify(saveData))
    lastSaveTime.value = serverTimeSync.getServerTime()
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
             programmingAnswers.value = {}
             selectedLanguages.value = {}
             
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
                 } else if (question.questionType === 'PROGRAMMING') {
                   // 程序题答案
                   if (cleanedData.programmingAnswers && cleanedData.programmingAnswers[index]) {
                     programmingAnswers.value[index] = cleanedData.programmingAnswers[index]
                     // 恢复选择的语言
                     if (cleanedData.selectedLanguages && cleanedData.selectedLanguages[index]) {
                       selectedLanguages.value[index] = cleanedData.selectedLanguages[index]
                     } else {
                       // 如果没有保存的语言，使用默认值
                       selectedLanguages.value[index] = question.programmingLanguage || 'JAVA'
                     }
                     // 同时同步到selectedAnswers
                     if (!selectedAnswers.value[index]) {
                       selectedAnswers.value[index] = []
                     }
                     selectedAnswers.value[index] = [cleanedData.programmingAnswers[index]]
                     // 恢复代码编辑器内容和语言（异步，不阻塞）
                     // 延迟执行，让界面先可交互
                     setTimeout(() => {
                       const language = selectedLanguages.value[index] || question.programmingLanguage || 'JAVA'
                       const savedCode = cleanedData.programmingAnswers[index]
                       
                       // 检查编辑器是否存在且有效
                       const editor = codeEditors.value[index]
                       if (editor && editor instanceof EditorView) {
                         try {
                           // 检查编辑器的 DOM 元素是否仍然存在（验证编辑器是否有效）
                           const editorElement = editor.dom
                           if (!editorElement || !editorElement.parentElement) {
                             // 编辑器已被销毁，需要重新初始化
                             codeEditors.value[index] = null
                             initCodeEditor(index, language).catch(err => {
                               console.error('Failed to reinit editor:', err)
                             })
                             return
                           }
                           
                           // 编辑器有效，更新内容
                           const currentCode = editor.state.doc.toString()
                           if (savedCode && currentCode !== savedCode) {
                             // 使用当前状态创建更新
                             editor.dispatch({
                               changes: {
                                 from: 0,
                                 to: currentCode.length,
                                 insert: savedCode
                               }
                             })
                           }
                         } catch (e) {
                           // 更新失败，可能是状态不一致，重新初始化编辑器
                           console.warn('Failed to update editor, reinitializing:', e)
                           try {
                             editor.destroy()
                           } catch (destroyError) {
                             // 忽略销毁错误
                           }
                           codeEditors.value[index] = null
                           initCodeEditor(index, language).catch(err => {
                             console.error('Failed to reinit editor after update error:', err)
                           })
                         }
                       } else {
                         // 编辑器不存在，异步初始化
                         initCodeEditor(index, language).catch(err => {
                           console.error('Failed to init editor:', err)
                         })
                       }
                     }, 500) // 延迟执行，确保页面可交互
                   } else {
                     // 即使没有答案，也要初始化语言选择
                     selectedLanguages.value[index] = question.programmingLanguage || 'JAVA'
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
    essayAnswers: {},
    programmingAnswers: {},
    selectedLanguages: {}
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
  
  // 清理程序题答案
  if (data.programmingAnswers) {
    Object.keys(data.programmingAnswers).forEach(key => {
      const answer = data.programmingAnswers[key]
      if (typeof answer === 'string' && answer.trim().length > 0) {
        cleanedData.programmingAnswers[key] = answer
      }
    })
  }
  
  // 清理选择的编程语言
  if (data.selectedLanguages) {
    Object.keys(data.selectedLanguages).forEach(key => {
      const language = data.selectedLanguages[key]
      if (typeof language === 'string' && ['JAVA', 'PYTHON', 'CPP', 'C'].includes(language)) {
        cleanedData.selectedLanguages[key] = language
      }
    })
  }
  
  return cleanedData
}

// 提交试卷
const submitExam = async () => {
  try {
    // 检查是否所有题目都已作答，显示不同的提示信息
    const confirmMessage = isAllAnswered.value 
      ? '你已完成所有题目，确认交卷吗？' 
      : `你还有 ${unansweredCount.value} 道未做完的题目，确认提交吗？`
    
    // 显示确认对话框
    try {
      await ElMessageBox.confirm(
        confirmMessage,
        '确认提交',
        {
          confirmButtonText: '确认提交',
          cancelButtonText: '取消',
          type: isAllAnswered.value ? 'success' : 'warning',
          distinguishCancelAndClose: true
        }
      )
    } catch (error) {
      // 用户点击了取消或关闭
      if (error === 'cancel' || error === 'close') {
        return
      }
      throw error
    }
    
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
    
    // 程序题答案已在selectedAnswers中（通过编辑器onDidChangeModelContent同步），无需单独处理
    // 但确保从programmingAnswers中也获取答案，以防万一
    Object.keys(programmingAnswers.value).forEach(key => {
      if (!allAnswers[key] && programmingAnswers.value[key]) {
        allAnswers[key] = [programmingAnswers.value[key]]
      }
    })
    
    console.log('Submitting answers:', allAnswers)
    
    // 准备试卷内容（包含题目和选项顺序）
    const paperContent = {
      questions: questions.value.map(q => ({
        questionId: q.questionId,
        questionOrder: q.questionOrder,
        questionType: q.questionType,
        questionContent: q.questionText || q.questionContent,  // 题目内容
        points: q.points,
        options: q.options || []
        // 不存储答案（answers），避免重复和格式问题
      }))
    }
    
    const submitData = {
      answers: allAnswers,
      paperContent: paperContent
    }
    
    console.log('Submitting data:', submitData)
    console.log('Answers count:', Object.keys(allAnswers).length)
    console.log('Paper content questions count:', paperContent.questions.length)
    
    const response = await studentExamApi.submitStudentExam(examId, submitData)
    
    if (response.code === 200) {
      ElMessage.success('试卷提交成功')
      clearLocalAnswers() // 清除本地保存的答案
      router.push('/user/exam')
    } else {
      ElMessage.error(response.message || '提交失败')
    }
  } catch (error) {
    // 如果错误不是用户取消，才显示错误信息
    if (error !== 'cancel' && error !== 'close') {
      console.error('Submit exam error:', error)
      ElMessage.error('提交失败')
    }
  } finally {
    submitting.value = false
  }
}


// 刷新页面
const refreshPage = () => {
  window.location.reload()
}

// 保存答卷（草稿，不提交）
const saveDraft = async () => {
  try {
    submitting.value = true
    await saveDraftToServer(false)
  } finally {
    submitting.value = false
  }
}

// 时间到了自动提交（不显示确认对话框）
const autoSubmitOnTimeUp = async () => {
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
    
    // 准备试卷内容（包含题目和选项顺序）
    const paperContent = {
      questions: questions.value.map(q => ({
        questionId: q.questionId,
        questionOrder: q.questionOrder,
        questionType: q.questionType,
        questionContent: q.questionText || q.questionContent,
        points: q.points,
        options: q.options || []
      }))
    }
    
    const submitData = {
      answers: allAnswers,
      paperContent: paperContent
    }
    
    const response = await studentExamApi.submitStudentExam(examId, submitData)
    
    if (response.code === 200) {
      ElMessage.success('考试时间已到，答案已自动提交')
      clearLocalAnswers()
      router.push('/user/exam')
    } else {
      ElMessage.error(response.message || '自动提交失败')
      // 即使提交失败，也跳转回去，因为后端定时任务会处理状态
      setTimeout(() => {
        router.push('/user/exam')
      }, 2000)
    }
  } catch (error) {
    console.error('Auto submit on time up error:', error)
    ElMessage.error('自动提交失败，请稍后查看考试结果')
    // 即使出错也跳转回去
    setTimeout(() => {
      router.push('/user/exam')
    }, 2000)
  } finally {
    submitting.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 监听路由变化，确保切换考试时清空状态
watch(() => route.params.id, async (newExamId, oldExamId) => {
  if (newExamId && oldExamId && newExamId !== oldExamId) {
    console.log('Exam ID changed from', oldExamId, 'to', newExamId, '- Clearing previous exam data')
    
    // 清空之前考试的答案数据
    selectedAnswers.value = {}
    fillBlankAnswers.value = {}
    essayAnswers.value = {}
    programmingAnswers.value = {}
    selectedLanguages.value = {}
    
    // 清理代码编辑器实例（CodeMirror 6 使用 destroy 方法）
    Object.entries(codeEditors.value).forEach(([key, editor]) => {
      if (editor && editor instanceof EditorView) {
        try {
          editor.destroy()
        } catch (e) {
          // 忽略销毁错误
        }
      }
      delete codeEditors.value[key]
    })
    codeEditors.value = {}
    
    // 停止之前的定时器
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    if (autoSaveTimer.value) {
      clearTimeout(autoSaveTimer.value)
      autoSaveTimer.value = null
    }
    
    // 重新加载新考试数据
    await loadExamData()
    nextTick(() => {
      loadAnswersFromLocal()
    })
  }
}, { immediate: false })

// 监听路由变化，当离开考试页面时清理资源
watch(() => route.path, (newPath, oldPath) => {
  // 如果离开考试预览页面，清理编辑器
  if (oldPath && oldPath.includes('/exam/') && !newPath.includes('/exam/')) {
    console.log('Leaving exam page, cleaning up editors')
    Object.entries(codeEditors.value).forEach(([key, editor]) => {
      if (editor && editor instanceof EditorView) {
        try {
          editor.destroy()
        } catch (e) {
          // 忽略销毁错误
        }
      }
      delete codeEditors.value[key]
    })
    codeEditors.value = {}
  }
}, { immediate: false })

onMounted(async () => {
  try {
    // 初始化服务器时间同步（在加载考试数据之前）
    await serverTimeSync.init()
    
    // 先加载考试数据
    await loadExamData()
    
    // 异步加载本地保存的答案（不阻塞界面）
    // 不使用 await，直接异步执行
    setTimeout(() => {
      loadAnswersFromLocal() // 加载本地保存的答案
    }, 200) // 延迟更久，确保页面已完全渲染和可交互
  } catch (error) {
    console.error('Failed to load exam data:', error)
    ElMessage.error('加载考试数据失败，请刷新页面重试')
  }
  
  // 监听页面可见性变化，当页面重新可见时重新加载答案
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

const handleVisibilityChange = () => {
  if (!document.hidden) {
    // 页面重新可见时，重新加载答案（确保加载的是当前考试的答案）
    loadAnswersFromLocal()
  }
}

// 组件销毁前清理资源
onBeforeUnmount(() => {
  // 清理代码编辑器实例
  Object.values(codeEditors.value).forEach(editor => {
    if (editor && editor instanceof EditorView) {
      try {
        editor.destroy()
      } catch (e) {
        // 忽略销毁错误
      }
    }
  })
  codeEditors.value = {}
  
  // 停止定时器
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  if (autoSaveTimer.value) {
    clearTimeout(autoSaveTimer.value)
    autoSaveTimer.value = null
  }
  
  // 移除事件监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  
  // 停止服务器时间同步
  serverTimeSync.stop()
})

// onUnmounted 已经移到 onBeforeUnmount，这里保留作为备份
onUnmounted(() => {
  // 清理逻辑已在 onBeforeUnmount 中处理
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

/* 题目图片样式 */
.question-images {
  margin-bottom: 16px;
}

.images-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.image-item {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
  background: #f5f7fa;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  font-size: 14px;
}

/* 程序题样式 */
.programming-answer {
  margin-top: 20px;
}

.language-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.code-editor-container {
  width: 100%;
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
}

.code-editor {
  width: 100%;
  height: 400px;
  min-height: 300px;
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

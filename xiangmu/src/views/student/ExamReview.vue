<template>
  <div class="exam-review-page">
    <!-- 考试头部信息 -->
    <div class="exam-header">
      <div class="exam-info">
        <h1 class="exam-title">{{ examInfo.examName }} - 考试详情</h1>
        <div class="exam-meta">
          <span class="paper-name">{{ examInfo.paperName }}</span>
          <span class="question-count">共 {{ questions.length }} 题 | 总分 {{ examInfo.totalPoints }} 分</span>
        </div>
      </div>
      <div class="exam-actions">
        <el-button 
          v-if="allowViewCorrect"
          type="success" 
          size="large" 
          @click="toggleShowAnswers"
        >
          {{ showAnswers ? '隐藏答案' : '显示答案' }}
        </el-button>
        <el-button 
          type="primary" 
          size="large" 
          @click="backToExamResult"
        >
          <el-icon><ArrowLeft /></el-icon>
          返回成绩页面
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
            <!-- 显示得分 -->
            <div v-if="questionScore[index] !== undefined" class="question-score" :class="{ 'score-full': questionScore[index] === question.points, 'score-zero': questionScore[index] === 0 }">
              得分：{{ questionScore[index] }}
            </div>
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
              :class="{
                'selected': isAnswerSelected(index, option.optionKey),
                'correct': isCorrectAnswer(question, option.optionKey),
                'wrong': isAnswerSelected(index, option.optionKey) && !isCorrectAnswer(question, option.optionKey)
              }"
            >
              <div class="option-label">{{ String.fromCharCode(65 + optIndex) }}</div>
              <div class="option-text">{{ option.optionContent }}</div>
              <div v-if="isAnswerSelected(index, option.optionKey)" class="option-check">
                <el-icon><Check /></el-icon>
              </div>
              <div v-if="isCorrectAnswer(question, option.optionKey) && !isAnswerSelected(index, option.optionKey)" class="option-correct">
                <el-icon><Check /></el-icon>
              </div>
            </div>
            <!-- 选择题学生答案文本提示（可选，增强可读性） -->
            <div class="student-answer-hint" style="margin-top: 12px; padding: 8px; background: #f5f7fa; border-radius: 4px;">
              <span style="font-weight: 500; color: #409eff;">你的答案：</span>
              <span>{{ getChoiceStudentAnswerText(index) || '未作答' }}</span>
            </div>
          </div>

          <!-- 填空题 -->
          <div v-else-if="question.questionType === 'FILL_BLANK'" class="fill-blank-answer">
            <div style="margin-bottom: 8px; font-weight: 500; color: #409eff;">你的答案：</div>
            <!-- 先判断是否为数组，否则用空数组兜底 -->
            <el-input
              :value="Array.isArray(question.studentAnswers) ? question.studentAnswers.join('；') : '未作答'"
              type="textarea"
              :rows="3"
              readonly
              class="readonly-input"
            />
            <!-- 正确答案：点击显示后才显示 -->
            <div v-if="(question.correctAnswer || question.answers) && showAnswers" class="answer-hint">
              <span class="hint-label">正确答案：</span>
              <span class="correct-answer">
                {{ 
                  question.correctAnswer 
                    ? question.correctAnswer 
                    : Array.isArray(question.answers) 
                      ? question.answers.map(item => item.answerContent).join('；') 
                      : question.answers 
                }}
              </span>
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
            <!-- 学生答案：明确标签 -->
            <div style="margin-bottom: 8px; font-weight: 500; color: #409eff;">你的答案：</div>
            <el-input
              :value="
                Array.isArray(question.studentAnswers) && question.studentAnswers[0] 
                  ? question.studentAnswers[0] 
                  : typeof question.studentAnswers === 'string' 
                    ? question.studentAnswers 
                    : '未作答'
              "
              type="textarea"
              :rows="6"
              readonly
              class="readonly-input"
            />
            <!-- 参考答案：修复显示逻辑，兼容不同字段 -->
            <div v-if="(question.correctAnswer || question.referenceAnswer || question.answers) && showAnswers" class="answer-hint">
              <span class="hint-label">参考答案：</span>
              <span class="correct-answer">
                {{ question.answers[0].answerContent || question.referenceAnswer || (Array.isArray(question.answers) ? question.answers.join('；') : question.answers) || '无' }}
              </span>
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
            
            <!-- 编程语言信息 -->
            <div class="language-info" style="margin-bottom: 12px;">
              <span style="color: #606266; font-size: 14px;">
                编程语言：{{ getLanguageLabel(selectedLanguages[index] || question.programmingLanguage || 'JAVA') }}
              </span>
            </div>
            
            <!-- 学生代码（明确标签） -->
            <div style="margin-bottom: 8px; font-weight: 500; color: #409eff;">你的代码：</div>
            <!-- 代码展示区域 -->
            <div class="code-display-container">
              <div :id="'code-editor-' + index" class="code-editor"></div>
            </div>
            
            <!-- 程序题参考答案（点击显示后才显示） -->
            <div v-if="(question.correctAnswer || question.referenceAnswer) && showAnswers" class="answer-hint" style="margin-top: 12px;">
              <span class="hint-label">参考代码：</span>
              <pre class="correct-answer" style="margin: 8px 0 0 0; padding: 12px; background: #f0f9eb; border-radius: 4px; font-family: 'Consolas', 'Monaco', 'Courier New', monospace; font-size: 14px; white-space: pre-wrap; word-break: break-all;">
                {{ question.correctAnswer || question.referenceAnswer || '无' }}
              </pre>
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Check, Refresh, View } from '@element-plus/icons-vue'
import { studentExamApi, getOwnAnswers } from '@/api/admin'

// 导入 CodeMirror 6（轻量级、易配置、性能好，无需 worker 配置）
import { EditorView, lineNumbers } from '@codemirror/view'
import { EditorState } from '@codemirror/state'
import { java } from '@codemirror/lang-java'
import { python } from '@codemirror/lang-python'
import { cpp } from '@codemirror/lang-cpp'

const route = useRoute()
const router = useRouter()

// 响应式数据
const examInfo = ref({})
const questions = ref([])
const selectedAnswers = ref({}) // 学生选择的答案
const fillBlankAnswers = ref({}) // 学生的填空题答案
const essayAnswers = ref({}) // 学生的主观题答案
const programmingAnswers = ref({}) // 学生的程序题答案
const selectedLanguages = ref({}) // 每个程序题选择的编程语言
const codeEditors = ref({}) // CodeMirror 6 编辑器实例
const testCaseResults = ref({}) // 每个程序题的测试用例执行结果
const questionScore = ref({}) // 题目得分
const allowViewCorrect = ref(false) // 是否允许查看正确答案
const showAnswers = ref(false) // 是否显示答案

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

// 检查答案是否已选择（学生的答案）
const isAnswerSelected = (questionIndex, optionKey) => {
  const question = questions.value[questionIndex]
  // 补充：判断 studentAnswers 是数组且非空
  if (!question || !Array.isArray(question.studentAnswers) || question.studentAnswers.length === 0) {
    return false
  }
  
  const option = question.options.find(opt => opt.optionKey === optionKey)
  // 优先匹配 optionContent（接口返回的 studentAnswers 存的是选项内容，如 "StringBuilder"）
  return question.studentAnswers.includes(option?.optionContent) || question.studentAnswers.includes(optionKey)
}

// 检查是否为正确答案
const isCorrectAnswer = (question, optionKey) => {
  // 必须满足：允许查看答案 + 已点击显示答案 + 题目有选项 + 选项存在且正确
  if (!allowViewCorrect.value || !showAnswers.value) return false
  if (!question || !Array.isArray(question.options)) return false

  const option = question.options.find(opt => opt.optionKey === optionKey)
  // 确保 option 存在且 isCorrect 为 true（避免 undefined 干扰）
  return !!option && option.isCorrect === true
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

// 初始化代码编辑器（只读模式）
const initCodeEditor = (questionIndex, language = 'java') => {
  // 检查是否已经初始化，避免重复初始化
  if (codeEditors.value[questionIndex]) {
    return Promise.resolve(codeEditors.value[questionIndex])
  }
  
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        const editorId = `code-editor-${questionIndex}`
        let editorElement = document.getElementById(editorId)
        
        if (!editorElement) {
          reject(new Error(`Editor element not found: ${editorId}`))
          return
        }
        
        // 获取当前代码内容或使用默认模板
        const currentCode = programmingAnswers.value[questionIndex] || ''
        
        // 创建 CodeMirror 6 编辑器状态（只读模式）
        const state = EditorState.create({
          doc: currentCode,
          extensions: [
            lineNumbers(),
            getLanguageExtension(language),
            EditorView.editable.of(false), // 设置为只读模式
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
                borderRadius: '4px',
                backgroundColor: '#f5f7fa' // 只读模式的背景色
              }
            })
          ]
        })
        
        // 创建 CodeMirror 6 编辑器实例
        const editor = new EditorView({
          state: state,
          parent: editorElement
        })
        
        codeEditors.value[questionIndex] = editor
        resolve(editor)
      } catch (error) {
        console.error(`Error initializing code editor for question ${questionIndex}:`, error)
        reject(error)
      }
    }, 100)
  })
}

// 加载考试数据
const loadExamData = async () => {
  try {
    const examId = route.params.examId
    
    // 清空之前考试的答案数据
    selectedAnswers.value = {}
    fillBlankAnswers.value = {}
    essayAnswers.value = {}
    programmingAnswers.value = {}
    selectedLanguages.value = {}
    codeEditors.value = {}
    questionScore.value = {}
    allowViewCorrect.value = false
    
    // 1. 获取考试详情（包含allowReview信息）
    const examDetailResponse = await studentExamApi.getStudentExamDetail(examId)
    if (examDetailResponse.code !== 200) {
      ElMessage.error(examDetailResponse.message || '获取考试详情失败')
      router.push(`/user/exam/${examId}/result`)
      return
    }
    
    const examDetail = examDetailResponse.data
    allowViewCorrect.value = examDetail.allowReview || false
    
    // 设置考试信息
    examInfo.value = {
      examId: examDetail.id,
      examName: examDetail.examName,
      paperName: examDetail.paperName,
      totalQuestions: examDetail.totalQuestions,
      totalPoints: examDetail.totalPoints,
      durationMinutes: examDetail.durationMinutes
    }
    
    // 2. 获取学生答案信息
    const studentAnswersResponse = await getOwnAnswers(examId)
    if (studentAnswersResponse.code !== 200) {
      ElMessage.error(studentAnswersResponse.message || '获取考试题目失败')
      router.push(`/user/exam/${examId}/result`)
      return
    }
    
    const paperData = studentAnswersResponse.data

    
    // 设置题目数据
    questions.value = paperData.questions.map((q, idx) => {
      // 关键修复：处理字符串类型的 studentAnswers（填空题/主观题）
      let studentAnswers;
      if (Array.isArray(q.studentAnswers)) {
        studentAnswers = q.studentAnswers; // 数组类型直接使用
      } else if (typeof q.studentAnswers === 'string' && q.studentAnswers.trim() !== '') {
        studentAnswers = [q.studentAnswers.trim()]; // 字符串转数组（单个元素）
      } else {
        studentAnswers = []; // 其他情况（undefined/null/空字符串）设为空数组
      }
      
      // 保存题目得分（补充：接口返回的是 givenScore，不是 score）
      if (q.givenScore !== undefined) {
        questionScore.value[idx] = q.givenScore
      }
      
      // 初始化程序题的语言选择（修改：使用格式化后的 studentAnswers）
      if (q.questionType === 'PROGRAMMING') {
        const defaultLanguage = q.programmingLanguage || 'JAVA'
        selectedLanguages.value[idx] = defaultLanguage
        
        if (studentAnswers.length > 0) {
          const answerStr = studentAnswers[0] // 数组第一个元素是答案
          if (answerStr.includes(':')) {
            const [lang, code] = answerStr.split(':', 2)
            selectedLanguages.value[idx] = lang
            programmingAnswers.value[idx] = code
          } else {
            programmingAnswers.value[idx] = answerStr
          }
        }
      }
      
      // 保存学生答案（修改：使用格式化后的 studentAnswers）
      switch (q.questionType) {
        case 'FILL_BLANK':
          // 填空题答案：数组转字符串（多个空用分号分隔）
          fillBlankAnswers.value[idx] = studentAnswers.join('；') || ''
          break
        case 'SUBJECTIVE':
          // 主观题答案：数组第一个元素（通常主观题答案是单个字符串）
          essayAnswers.value[idx] = studentAnswers[0] || ''
          break
        case 'SINGLE_CHOICE':
        case 'MULTIPLE_CHOICE':
        case 'TRUE_FALSE':
          // 选择题答案：直接保存数组（支持多选）
          selectedAnswers.value[idx] = studentAnswers
          break
      }
      
      return {
        questionId: q.questionId,
        questionOrder: q.questionOrder,
        questionType: q.questionType,
        questionText: q.questionContent || q.questionText,
        points: q.points,
        difficulty: q.difficulty,
        options: q.options || [],
        answers: q.correctAnswers || q.answers || [],
        correctAnswer: q.correctAnswer,
        referenceAnswer: q.referenceAnswer,
        images: q.images || null,
        programmingLanguage: q.programmingLanguage || 'JAVA',
        testCases: q.testCases || [],
        testCaseResults: q.testCaseResults || {},
        studentAnswers: studentAnswers, // 返回格式化后的数组（数组/字符串都转为数组）
        givenScore: q.givenScore // 保存得分（接口返回的是 givenScore）
      }
    })
    
    // 初始化测试用例结果
    questions.value.forEach((q, idx) => {
      if (q.questionType === 'PROGRAMMING' && q.testCaseResults) {
        testCaseResults.value[idx] = q.testCaseResults
      }
    })
      
      // 异步初始化程序题代码编辑器（只读模式）
      setTimeout(() => {
        const programmingQuestions = questions.value
          .map((q, index) => ({ q, index }))
          .filter(({ q }) => q.questionType === 'PROGRAMMING')
        
        programmingQuestions.forEach(({ q, index }, idx) => {
          setTimeout(() => {
            const language = selectedLanguages.value[index] || q.programmingLanguage || 'JAVA'
            initCodeEditor(index, language).catch(err => {
              console.error(`Failed to init code editor for question ${index}:`, err)
            })
          }, idx * 300)
        })
      }, 500)
    } catch (error) {
      console.error('Load exam data error:', error)
      ElMessage.error('获取考试详情失败')
      router.push(`/user/exam/${examId}/result`)
    }
  }
  
  // 返回成绩页面
const backToExamResult = () => {
  const examId = route.params.examId
  router.push(`/user/exam/${examId}/result`)
}

// 切换显示答案
const toggleShowAnswers = () => {
  showAnswers.value = !showAnswers.value
}

// 监听路由变化，确保切换考试时清空状态
watch(() => route.params.examId, async (newExamId, oldExamId) => {
  if (newExamId && oldExamId && newExamId !== oldExamId) {
    // 清理代码编辑器实例
    Object.entries(codeEditors.value).forEach(([key, editor]) => {
      if (editor && editor instanceof EditorView) {
        try {
          editor.destroy()
        } catch (e) {
          // �忽律销毁错误
        }
      }
      delete codeEditors.value[key]
    })
    
    // 重新加载新考试数据
    await loadExamData()
  }
}, { immediate: false })

onMounted(async () => {
  try {
    // 加载考试数据
    await loadExamData()
  } catch (error) {
    console.error('Failed to load exam data:', error)
    ElMessage.error('加载考试数据失败，请刷新页面重试')
  }
})

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
})

// 获取选择题学生答案的文本描述（如：A. 选项1；B. 选项2）
const getChoiceStudentAnswerText = (questionIndex) => {
  const question = questions.value[questionIndex]
  // 先判断 question 和 studentAnswers 是否为数组
  if (!question || !Array.isArray(question.studentAnswers) || question.studentAnswers.length === 0) {
    return ''
  }
  
  // 遍历学生答案，匹配对应的选项文本
  const studentAnswerTexts = question.studentAnswers.map(answerKey => {
    const option = question.options.find(opt => opt.optionKey === answerKey || opt.optionContent === answerKey)
    return option ? `${String.fromCharCode(65 + question.options.findIndex(opt => opt.optionKey === option.optionKey))}. ${option.optionContent}` : answerKey
  })
  
  return studentAnswerTexts.join('；')
}

</script>

<style scoped>
.exam-review-page {
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

.exam-actions {
  display: flex;
  gap: 12px;
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

.question-score {
  font-size: 16px;
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 12px;
  background: #f0f9eb;
  color: #67c23a;
}

.question-score.score-full {
  background: #67c23a;
  color: white;
}

.question-score.score-zero {
  background: #fef0f0;
  color: #f56c6c;
}

.question-content {
  margin-left: 60px;
}

.question-text {
  font-size: 18px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 20px;
  white-space: pre-line; /* 保留换行符，合并多余空格 */
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
  position: relative;
  cursor: default;
  transition: all 0.3s;
}

.option-item.selected {
  border-color: #409eff;
  background: #409eff;
  color: white;
}

.option-item.correct {
  border-color: #67c23a;
  background: #f0f9eb;
  color: #67c23a;
}

.option-item.wrong {
  border-color: #f56c6c;
  background: #fef0f0;
  color: #f56c6c;
}

.option-item.selected.correct {
  border-color: #67c23a;
  background: #67c23a;
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

.option-item.correct .option-label {
  background: #67c23a;
  color: white;
}

.option-item.wrong .option-label {
  background: #f56c6c;
  color: white;
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

.option-check,
.option-correct {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: white;
  font-size: 18px;
}

.option-correct {
  color: #67c23a;
}

.fill-blank-answer,
.essay-answer {
  margin-top: 20px;
}

.readonly-input {
  width: 100%;
  background: #f5f7fa;
  cursor: default;
}

.readonly-input .el-textarea__inner {
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  padding: 12px;
  font-size: 16px;
  line-height: 1.5;
  resize: vertical;
  background: #f5f7fa;
  color: #606266;
}

.answer-hint {
  margin-top: 12px;
  padding: 12px;
  background: #ecf5ff;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.hint-label {
  font-weight: 600;
  color: #409eff;
  margin-right: 8px;
}

.correct-answer {
  color: #67c23a;
  font-weight: 500;
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

.code-display-container {
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

@media (max-width: 768px) {
  .exam-review-page {
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
}
</style>
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
          </div>

          <!-- 填空题 -->
          <div v-else-if="question.questionType === 'FILL_BLANK'" class="fill-blank-answer">
            <el-input
              :value="fillBlankAnswers[index] || '未作答'"
              type="textarea"
              :rows="3"
              readonly
              class="readonly-input"
            />
            <div v-if="question.correctAnswer" class="answer-hint">
              <span class="hint-label">正确答案：</span>
              <span class="correct-answer">{{ question.correctAnswer }}</span>
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
              :value="essayAnswers[index] || '未作答'"
              type="textarea"
              :rows="6"
              readonly
              class="readonly-input"
            />
            <div v-if="question.correctAnswer" class="answer-hint">
              <span class="hint-label">参考答案：</span>
              <span class="correct-answer">{{ question.correctAnswer }}</span>
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
            
            <!-- 代码展示区域 -->
            <div class="code-display-container">
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
import { studentExamApi } from '@/api/admin'

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
const selectedAnswers = ref({})
const fillBlankAnswers = ref({})
const essayAnswers = ref({})
const programmingAnswers = ref({}) // 程序题答案
const selectedLanguages = ref({}) // 每个程序题选择的编程语言
const codeEditors = ref({}) // CodeMirror 6 编辑器实例
const testCaseResults = ref({}) // 每个程序题的测试用例执行结果
const questionScore = ref({}) // 题目得分

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

// 检查答案是否已选择
const isAnswerSelected = (questionIndex, optionKey) => {
  const question = questions.value[questionIndex]
  const option = question.options.find(opt => opt.optionKey === optionKey)
  const optionContent = option ? option.optionContent : optionKey
  
  const answers = selectedAnswers.value[questionIndex]
  return answers && answers.includes(optionContent)
}

// 检查是否为正确答案
const isCorrectAnswer = (question, optionKey) => {
  // 这里假设后端返回的答案结构是正确的，需要根据实际情况调整
  if (!question || !question.answers || question.answers.length === 0) return false
  
  const correctAnswer = question.answers[0]
  const option = question.options.find(opt => opt.optionKey === optionKey)
  const optionContent = option ? option.optionContent : optionKey
  
  return correctAnswer === optionContent || correctAnswer === optionKey
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
    
    // 获取考试试卷题目和答案
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
        durationMinutes: paperData.durationMinutes
      }
      
      // 设置题目数据
      questions.value = paperData.questions.map((q, idx) => {
        // 保存题目得分
        if (q.score !== undefined) {
          questionScore.value[idx] = q.score
        }
        
        // 初始化程序题的语言选择
        if (q.questionType === 'PROGRAMMING') {
          const defaultLanguage = q.programmingLanguage || 'JAVA'
          selectedLanguages.value[idx] = defaultLanguage
          
          // 如果有答案，保存答案
          if (q.answer) {
            // 解析答案格式（可能是 "LANGUAGE:code" 格式）
            if (q.answer.includes(':')) {
              const [lang, code] = q.answer.split(':', 2)
              selectedLanguages.value[idx] = lang
              programmingAnswers.value[idx] = code
            } else {
              programmingAnswers.value[idx] = q.answer
            }
          }
        }
        
        // 保存其他题型的答案
        if (q.answer) {
          switch (q.questionType) {
            case 'FILL_BLANK':
              fillBlankAnswers.value[idx] = q.answer
              break
            case 'SUBJECTIVE':
              essayAnswers.value[idx] = q.answer
              break
            case 'SINGLE_CHOICE':
            case 'MULTIPLE_CHOICE':
            case 'TRUE_FALSE':
              if (!selectedAnswers.value[idx]) {
                selectedAnswers.value[idx] = []
              }
              // 多选题可能有多个答案，用分号分隔
              if (q.answer.includes(';')) {
                selectedAnswers.value[idx] = q.answer.split(';')
              } else {
                selectedAnswers.value[idx] = [q.answer]
              }
              break
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
          answers: q.correctAnswers || [], // 正确答案
          correctAnswer: q.correctAnswer, // 针对填空题和主观题的正确答案
          images: q.images || null,
          programmingLanguage: q.programmingLanguage || 'JAVA',
          testCases: q.testCases || [],
          testCaseResults: q.testCaseResults || {} // 测试用例执行结果
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
    } else {
      ElMessage.error(response.message || '获取考试详情失败')
      router.push('/user/exam-result')
    }
  } catch (error) {
    console.error('Load exam data error:', error)
    ElMessage.error('获取考试详情失败')
    router.push('/user/exam-result')
  }
}

// 返回成绩页面
const backToExamResult = () => {
  const examId = route.params.examId
  router.push(`/student/exam-result/${examId}`)
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
          // 忽略销毁错误
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
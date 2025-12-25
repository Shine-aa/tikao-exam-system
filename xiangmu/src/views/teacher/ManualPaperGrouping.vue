<template>
  <div class="manual-paper-grouping">
    <div class="page-header">
      <h2>手动组卷</h2>
      <p>手动选择题目，自定义组卷</p>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button @click="handleBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <!-- 手动组卷表单 -->
    <div class="manual-create-container">
      <el-form
        ref="manualCreateFormRef"
        :model="createFormManual"
        :rules="manualCreateFormRules"
        label-width="120px"
      >
        <!-- 基本信息区域 -->
        <div class="form-section">
          <h3>基本信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="试卷名称" prop="paperName">
                <el-input v-model="createFormManual.paperName" placeholder="请输入试卷名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="课程" prop="courseId">
                <el-select v-model="createFormManual.courseId" placeholder="请选择课程" @change="handleCourseChange">
                  <el-option
                    v-for="course in courseList"
                    :key="course.id"
                    :label="course.courseName"
                    :value="course.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="考试时长" prop="durationMinutes">
                <el-input-number
                  v-model="createFormManual.durationMinutes"
                  :min="30"
                  placeholder="分钟"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="总分" prop="totalPoints">
                <el-input-number
                  v-model="createFormManual.totalPoints"
                  :min="1"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="试卷描述">
            <el-input
              v-model="createFormManual.description"
              type="textarea"
              :rows="3"
              placeholder="请输入试卷描述（可选）"
            />
          </el-form-item>
        </div>

        <!-- 题目筛选与选择区域 -->
        <div class="form-section">
          <h3>题目筛选与选择</h3>
          <div class="manual-grouping-container">
            <!-- 左侧筛选栏 -->
            <div class="filter-panel">
              <div class="filter-section">
                <div class="filter-header">
                  <el-checkbox 
                    v-model="checkAllTypes" 
                    @change="handleTypeCheckAll"
                  />
                  <h4>题型</h4>
                </div>
                <el-checkbox-group v-model="filterForm.batchTypes" @change="handleTypeChange">
                  <el-checkbox 
                    v-for="(label, key) in questionTypes" 
                    :key="key" 
                    :label="key"
                  >
                    {{ label.label }}
                  </el-checkbox>
                </el-checkbox-group>
              </div>
              
              <div class="filter-section">
                <div class="filter-header">
                  <el-checkbox 
                    v-model="checkAllDifficulties" 
                    @change="handleDifficultyCheckAll"
                  />
                  <h4>难度</h4>
                </div>
                <el-checkbox-group v-model="filterForm.batchDifficulties" @change="handleDifficultyChange">
                  <el-checkbox 
                    v-for="(label, key) in difficulties" 
                    :key="key" 
                    :label="key"
                  >
                    {{ label.label }}
                  </el-checkbox>
                </el-checkbox-group>
              </div>
              
              <div class="filter-section">
                <h4>关键词搜索</h4>
                <el-input
                  v-model="filterForm.keyword"
                  placeholder="搜索题目标题/内容"
                  @keyup.enter="handleSearchQuestions"
                />
              </div>
              
              <div class="filter-actions">
                <el-button type="primary" @click="handleSearchQuestions">查询</el-button>
                <el-button @click="handleResetFilter">重置</el-button>
              </div>
            </div>
            
            <!-- 右侧题目列表 -->
            <div class="questions-panel">
              <!-- 可选题目列表 -->
              <div class="optional-questions">
                <h4>可选题目</h4>
                <el-table
                  v-loading="optionalLoading"
                  :data="optionalQuestions"
                  border
                  stripe
                  height="300"
                  @selection-change="handleSelectionChange"
                >
                  <el-table-column prop="id" label="题目ID" width="80" align="center" />
                  <el-table-column prop="questionType" label="题型" width="100" align="center">
                    <template #default="{ row }">
                      {{ getQuestionTypeLabel(row.questionType) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="difficulty" label="难度" width="80" align="center">
                    <template #default="{ row }">
                      {{ getDifficultyLabel(row.difficulty) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="title" label="题目标题" min-width="300" show-overflow-tooltip />
                  <el-table-column label="操作" width="80" align="center">
                    <template #default="{ row }">
                      <el-button 
                        type="primary" 
                        size="small" 
                        @click="addToSelected(row)"
                        :disabled="isQuestionSelected(row.id)"
                      >
                        {{ isQuestionSelected(row.id) ? '已添加' : '添加' }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                
                <div class="pagination-container">
                  <el-pagination
                    v-model:current-page="optionalPagination.page"
                    v-model:page-size="optionalPagination.size"
                    :total="optionalPagination.total"
                    :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleOptionalSizeChange"
                    @current-change="handleOptionalPageChange"
                  />
                </div>
              </div>
              
              <!-- 已选题目列表 -->
              <div class="selected-questions">
                <h4>已选题目</h4>
                <el-table
                  :data="selectedQuestions"
                  border
                  stripe
                  height="200"
                  :row-class-name="tableRowClassName"
                >
                  <el-table-column type="index" label="序号" width="60" align="center" />
                  <el-table-column prop="title" label="题目标题" min-width="200" show-overflow-tooltip />
                  <el-table-column prop="questionType" label="题型" width="100" align="center">
                    <template #default="{ row }">
                      {{ getQuestionTypeLabel(row.questionType) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="分值" width="100" align="center">
                    <template #default="{ row }">
                      <el-input-number 
                        v-model="row.points" 
                        :min="0" 
                        size="small" 
                        @change="updateSelectedTotalPoints"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="顺序" width="100" align="center">
                    <template #default="{ row }">
                      <el-input-number 
                        v-model="row.order" 
                        :min="1" 
                        size="small"
                        @change="checkOrderDuplicate"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="80" align="center">
                    <template #default="{ row }">
                      <el-button 
                        type="text" 
                        size="small" 
                        class="remove-btn"
                        @click="removeFromSelected(row.id)"
                      >
                        移除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                
                <div class="selected-summary">
                  已选题目：{{ selectedCount }}道 | 
                  已选总分：<span :class="{ 'error-text': selectedTotalPoints !== createFormManual.totalPoints }">{{ selectedTotalPoints }}</span>分
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-form>
      
      <!-- 提交确认区域 -->
      <div class="submit-section">
        <div v-if="manualCreateError" class="error-message">
          {{ manualCreateError }}
        </div>
        <div class="submit-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitManualCreate" 
            :loading="manualCreateLoading"
          >
            创建试卷
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { paperApi, classApi, courseApi } from '@/api/admin'
import { getQuestionsForManual } from '@/api/admin'

const router = useRouter()

// 响应式数据
const courseList = ref([])

// 手动组卷加载状态
const manualCreateLoading = ref(false)

// 手动组卷表单
const createFormManual = reactive({
  paperName: '',
  description: '',
  courseId: null,
  classId: null,
  durationMinutes: 120,
  totalPoints: 100
})

// 筛选条件
const filterForm = reactive({
  batchTypes: ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'FILL_BLANK', 'SUBJECTIVE', 'PROGRAMMING'],
  batchDifficulties: ['EASY', 'MEDIUM', 'HARD'],
  keyword: ''
})

// 全选状态
const checkAllTypes = ref(true)
const checkAllDifficulties = ref(true)

// 可选题目列表相关
const optionalQuestions = ref([])
const optionalPagination = reactive({ page: 1, size: 10, total: 0 })
const optionalLoading = ref(false)

// 已选题目列表相关
const selectedQuestions = ref([])
const selectedCount = computed(() => selectedQuestions.value.length)
const selectedTotalPoints = computed(() => selectedQuestions.value.reduce((sum, item) => sum + item.points, 0))

// 防抖定时器
let searchTimer = null

// 手动组卷表单验证规则
const manualCreateFormRules = {
  paperName: [
    { required: true, message: '请输入试卷名称', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  durationMinutes: [
    { required: true, message: '请输入考试时长', trigger: 'blur' }
  ],
  totalPoints: [
    { required: true, message: '请输入总分', trigger: 'blur' }
  ]
}

// 手动组卷表单引用
const manualCreateFormRef = ref(null)

// 错误信息
const manualCreateError = ref('')

// 题型配置
const questionTypes = {
  'SINGLE_CHOICE': { label: '单选题' },
  'MULTIPLE_CHOICE': { label: '多选题' },
  'TRUE_FALSE': { label: '判断题' },
  'FILL_BLANK': { label: '填空题' },
  'SUBJECTIVE': { label: '主观题' },
  'PROGRAMMING': { label: '程序题' }
}

// 题型顺序（与后端保持一致）
const questionTypeOrder = [
  'SINGLE_CHOICE',    // 单选题
  'MULTIPLE_CHOICE',  // 多选题
  'TRUE_FALSE',       // 判断题
  'FILL_BLANK',       // 填空题
  'SUBJECTIVE',       // 主观题
  'PROGRAMMING'       // 程序题
]

// 难度配置
const difficulties = {
  'EASY': { label: '简单' },
  'MEDIUM': { label: '中等' },
  'HARD': { label: '困难' }
}

// 方法
// 不再需要加载班级列表，班级选择已移除

const loadCourseList = async () => {
  try {
    const response = await courseApi.getCoursesWithPagination(1, 1000, '')
    if (response.code === 200) {
      courseList.value = response.data.content || []
    }
  } catch (error) {
    console.error('Load course list error:', error)
  }
}

// 返回组卷管理页面
const handleBack = () => {
  router.push('/teacher/exam-grouping')
}

// 取消并返回
const handleCancel = () => {
  router.push('/teacher/exam-grouping')
}

// 手动组卷相关方法
const resetManualCreateForm = () => {
  createFormManual.paperName = ''
  createFormManual.description = ''
  createFormManual.courseId = null
  createFormManual.classId = null
  createFormManual.durationMinutes = 120
  createFormManual.totalPoints = 100
  manualCreateError.value = ''
  selectedQuestions.value = []
  optionalQuestions.value = []
  optionalPagination.page = 1
  optionalPagination.size = 10
  optionalPagination.total = 0
  // 重置筛选条件
  filterForm.batchTypes = ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'FILL_BLANK', 'SUBJECTIVE', 'PROGRAMMING']
  filterForm.batchDifficulties = ['EASY', 'MEDIUM', 'HARD']
  filterForm.keyword = ''
}

// 获取题型标签
const getQuestionTypeLabel = (type) => {
  return questionTypes[type]?.label || type
}

// 获取难度标签
const getDifficultyLabel = (difficulty) => {
  return difficulties[difficulty]?.label || difficulty
}

const handleCourseChange = async (courseId) => {
  // 课程切换时清空已选题目
  selectedQuestions.value = []
  // 如果选择了课程，加载可选题目列表
  if (courseId) {
    await loadOptionalQuestions()
  }
}

const loadOptionalQuestions = async () => {
  if (!createFormManual.courseId) return
  
  optionalLoading.value = true
  try {
    const params = {
      courseId: createFormManual.courseId,
      type: null,
      difficulty: null,
      batchTypes: filterForm.batchTypes.join(','),
      batchDifficulties: filterForm.batchDifficulties.join(','),
      keyword: filterForm.keyword,
      page: optionalPagination.page,
      size: optionalPagination.size,
      sortBy: 'createdAt',
      sortDir: 'DESC'
    }
    
    const response = await getQuestionsForManual(params)
    
    if (response.code === 200) {
      // 将后端返回的type字段转换为前端需要的questionType字段
      optionalQuestions.value = (response.data.content || []).map(question => ({
        ...question,
        questionType: question.type  // 映射字段名
      }))
      optionalPagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取题目列表失败')
    }
  } catch (error) {
    console.error('Load questions error:', error)
    ElMessage.error('获取题目列表失败')
  } finally {
    optionalLoading.value = false
  }
}

const handleSearchQuestions = async () => {
  // 防抖处理
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  
  searchTimer = setTimeout(async () => {
    optionalPagination.page = 1
    await loadOptionalQuestions()
  }, 300)
}

const handleResetFilter = () => {
  filterForm.batchTypes = ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'FILL_BLANK', 'SUBJECTIVE', 'PROGRAMMING']
  filterForm.batchDifficulties = ['EASY', 'MEDIUM', 'HARD']
  filterForm.keyword = ''
  // 重置全选状态
  checkAllTypes.value = true
  checkAllDifficulties.value = true
  optionalPagination.page = 1
  loadOptionalQuestions()
}

// 题型全选处理
const handleTypeCheckAll = (val) => {
  filterForm.batchTypes = val ? Object.keys(questionTypes) : []
}

// 题型变化时检查是否全选
const handleTypeChange = (values) => {
  checkAllTypes.value = values.length === Object.keys(questionTypes).length
}

// 难度全选处理
const handleDifficultyCheckAll = (val) => {
  filterForm.batchDifficulties = val ? Object.keys(difficulties) : []
}

// 难度变化时检查是否全选
const handleDifficultyChange = (values) => {
  checkAllDifficulties.value = values.length === Object.keys(difficulties).length
}

const handleOptionalSizeChange = (size) => {
  optionalPagination.size = size
  optionalPagination.page = 1
  loadOptionalQuestions()
}

const handleOptionalPageChange = (page) => {
  optionalPagination.page = page
  loadOptionalQuestions()
}

const isQuestionSelected = (questionId) => {
  return selectedQuestions.value.some(q => q.id === questionId)
}

const addToSelected = (question) => {
  if (isQuestionSelected(question.id)) return
  
  const newQuestion = {
    id: question.id,
    title: question.title,
    questionType: question.questionType || question.type, // 兼容后端返回的type字段
    difficulty: question.difficulty,
    points: 5, // 默认分值
    order: selectedQuestions.value.length + 1 // 默认顺序
  }
  
  selectedQuestions.value.push(newQuestion)
  updateSelectedTotalPoints()
}

const removeFromSelected = (questionId) => {
  const index = selectedQuestions.value.findIndex(q => q.id === questionId)
  if (index !== -1) {
    selectedQuestions.value.splice(index, 1)
    // 不重新计算顺序，保持用户设置的顺序
    updateSelectedTotalPoints()
  }
}

const updateSelectedTotalPoints = () => {
  // 触发计算属性更新
  selectedTotalPoints.value
  // 检查总分是否一致
  if (selectedTotalPoints.value !== createFormManual.totalPoints) {
    manualCreateError.value = `已选题目总分（${selectedTotalPoints.value}）与试卷总分（${createFormManual.totalPoints}）不一致`
  } else {
    manualCreateError.value = ''
  }
  
  // 触发表格重新渲染以更新行样式
  selectedQuestions.value = [...selectedQuestions.value]
}

const checkOrderDuplicate = () => {
  // 检查顺序是否重复
  const orders = selectedQuestions.value.map(q => q.order)
  const uniqueOrders = [...new Set(orders)]
  
  if (orders.length !== uniqueOrders.length) {
    manualCreateError.value = '存在重复顺序，请修改'
  } else {
    manualCreateError.value = ''
  }
  
  // 触发表格重新渲染以更新行样式
  selectedQuestions.value = [...selectedQuestions.value]
}

const validateSelectedQuestions = () => {
  // 校验已选题目
  if (selectedQuestions.value.length === 0) {
    manualCreateError.value = '请至少选择一道题目'
    return false
  }
  
  // 检查顺序是否重复
  const orders = selectedQuestions.value.map(q => q.order)
  const uniqueOrders = [...new Set(orders)]
  
  if (orders.length !== uniqueOrders.length) {
    manualCreateError.value = '存在重复顺序，请修改'
    return false
  }
  
  // 检查总分是否一致
  const totalPoints = selectedQuestions.value.reduce((sum, item) => sum + item.points, 0)
  if (totalPoints !== createFormManual.totalPoints) {
    manualCreateError.value = `已选题目总分（${totalPoints}）与试卷总分（${createFormManual.totalPoints}）不一致`
    return false
  }
  
  // 检查分值是否合法
  const invalidPoints = selectedQuestions.value.some(q => q.points < 0)
  if (invalidPoints) {
    manualCreateError.value = '题目分值不能小于 0'
    return false
  }
  
  manualCreateError.value = ''
  return true
}

const submitManualCreate = async () => {
  if (!manualCreateFormRef.value) return
  
  try {
    // 验证表单
    await manualCreateFormRef.value.validate()
    
    // 验证已选题目
    if (!validateSelectedQuestions()) {
      return
    }
    
    manualCreateLoading.value = true
    
    // 构造请求数据
    const requestData = {
      paperName: createFormManual.paperName,
      description: createFormManual.description,
      courseId: createFormManual.courseId,
      classId: createFormManual.classId,
      durationMinutes: createFormManual.durationMinutes,
      totalPoints: createFormManual.totalPoints,
      questions: selectedQuestions.value.map(item => ({
        questionId: item.id,
        questionOrder: item.order,
        points: item.points
      }))
    }
    
    const response = await paperApi.createManualPaper(requestData)
    
    if (response.code === 200) {
      ElMessage.success('手动组卷成功')
      router.push('/teacher/exam-grouping')
    } else {
      ElMessage.error(response.message || '手动组卷失败')
    }
  } catch (error) {
    console.error('Manual create paper error:', error)
    ElMessage.error('手动组卷失败')
  } finally {
    manualCreateLoading.value = false
  }
}

// 表格行类名处理函数，用于高亮显示顺序重复的行
const tableRowClassName = ({ row }) => {
  // 检查当前行的顺序是否与其他行重复
  const currentOrder = row.order
  const duplicateOrders = selectedQuestions.value.filter(q => q.order === currentOrder)
  
  // 如果有重复的顺序，则返回错误行类名
  if (duplicateOrders.length > 1) {
    return 'error-row'
  }
  
  return ''
}

// 生命周期
onMounted(() => {
  loadCourseList()
  resetManualCreateForm()
})
</script>

<style scoped>
.manual-paper-grouping {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.action-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.manual-create-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
}

/* 手动组卷样式 */
.form-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
}

.form-section h3 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.manual-grouping-container {
  display: flex;
  gap: 20px;
}

.filter-panel {
  width: 220px;
  flex-shrink: 0;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #ffffff;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.filter-header h4 {
  margin: 0;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.filter-section .el-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-actions {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.questions-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.optional-questions, .selected-questions {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #ffffff;
}

.optional-questions h4, .selected-questions h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.selected-summary {
  margin-top: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
  font-weight: 500;
}

.error-text {
  color: #f56c6c;
  font-weight: bold;
}

.remove-btn {
  color: #f56c6c;
}

.submit-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.error-message {
  color: #f56c6c;
  margin-bottom: 15px;
  font-weight: 500;
}

.submit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.selected-question-row.error-row {
  background-color: #fef0f0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.question-options h5, .question-answers h5, .question-explanation h5, .question-tags h5 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.options-list, .answers-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item, .answer-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: white;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

.correct-option {
  border-color: #67c23a !important;
  background: #f0f9ff !important;
}

.option-key, .answer-type {
  font-weight: bold;
  color: #409eff;
  min-width: 20px;
}

.option-content, .answer-content {
  flex: 1;
  color: #606266;
}

.question-explanation p {
  margin: 0;
  padding: 10px;
  background: white;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  line-height: 1.6;
  color: #606266;
}

.question-tags .el-tag {
  margin-bottom: 5px;
}

.no-questions {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 40px 0;
}
</style>

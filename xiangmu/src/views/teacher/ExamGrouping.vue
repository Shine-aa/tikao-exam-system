<template>
  <div class="exam-grouping">
    <div class="page-header">
      <h2>组卷管理</h2>
      <p>智能组卷，快速生成高质量试卷</p>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleCreatePaper">
        <el-icon><Plus /></el-icon>
        智能组卷
      </el-button>
      <el-button type="primary" @click="handleManualCreatePaper">
        <el-icon><List /></el-icon>
        手动组卷
      </el-button>
      <el-button @click="loadPaperList">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索试卷名称或代码"
        style="width: 300px"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 试卷列表 -->
    <div class="paper-list">
      <el-table
        v-loading="loading"
        :data="paperList"
        border
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column prop="paperCode" label="试卷代码" width="140" show-overflow-tooltip />
        <el-table-column prop="paperName" label="试卷名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="className" label="班级" width="100" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.className">{{ row.className }}</span>
            <el-tag v-else type="info" size="small">通用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程" width="120" show-overflow-tooltip />
        <el-table-column prop="totalQuestions" label="题目数" width="70" align="center" />
        <el-table-column prop="totalPoints" label="总分" width="60" align="center" />
        <el-table-column prop="durationMinutes" label="时长(分钟)" width="90" align="center" />
        <el-table-column prop="teacherName" label="创建教师" width="100" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="160" sortable>
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleViewPaper(row)">
                查看
              </el-button>
              <el-button type="info" size="small" @click="handleViewQuestions(row)">
                题目详情
              </el-button>
              <el-dropdown @command="(command) => handleActionCommand(command, row)" size="small">
                <el-button size="small">
                  更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="createExam">
                      <el-icon><plus /></el-icon>
                      创建考试
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 新建试卷对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="新建试卷"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createFormRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="试卷名称" prop="paperName">
              <el-input v-model="createForm.paperName" placeholder="请输入试卷名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程" prop="courseId">
              <el-select v-model="createForm.courseId" placeholder="请选择课程">
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
                v-model="createForm.durationMinutes"
                :min="30"
                :max="300"
                placeholder="分钟"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总题目数" prop="totalQuestions">
              <el-input-number
                v-model="createForm.totalQuestions"
                :min="1"
                :max="100"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总分" prop="totalPoints">
              <el-input-number
                v-model="createForm.totalPoints"
                :min="1"
                :max="1000"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="试卷描述">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入试卷描述"
          />
        </el-form-item>

        <!-- 题型分布配置 -->
        <el-form-item label="题型分布">
          <div class="question-type-config">
            <div class="type-section">
              <h4>按题型顺序配置题目数量（试卷将按此顺序组织）</h4>
              <div class="type-order-info">
                <el-tag type="info" size="small">单选题 → 多选题 → 判断题 → 填空题 → 主观题 → 程序题</el-tag>
              </div>
            </div>
            <div
              v-for="typeKey in questionTypeOrder"
              :key="typeKey"
              class="type-item"
            >
              <span class="type-label">{{ questionTypes[typeKey].label }}：</span>
              <el-input-number
                v-model="createForm.questionTypeDistribution[typeKey]"
                :min="0"
                :max="50"
                size="small"
                style="width: 100px"
              />
              <span class="type-order">{{ getQuestionTypeOrder(typeKey) }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 难度分布配置 -->
        <el-form-item label="难度分布">
          <div class="difficulty-config">
            <div
              v-for="(difficulty, key) in difficulties"
              :key="key"
              class="difficulty-item"
            >
              <span class="difficulty-label">{{ difficulty.label }}：</span>
              <el-input-number
                v-model="createForm.difficultyDistribution[key]"
                :min="0"
                :max="50"
                size="small"
                style="width: 100px"
              />
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmCreate" :loading="submitting">
          生成试卷
        </el-button>
      </template>
    </el-dialog>

    <!-- 试卷详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="试卷详情"
      width="1000px"
    >
      <div v-if="currentPaper" class="paper-detail">
        <div class="paper-header">
          <h3>{{ currentPaper.paperName }}</h3>
          <p>{{ currentPaper.description }}</p>
        </div>
        
        <div class="paper-info">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="试卷代码">{{ currentPaper.paperCode }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ currentPaper.className }}</el-descriptions-item>
            <el-descriptions-item label="课程">{{ currentPaper.courseName }}</el-descriptions-item>
            <el-descriptions-item label="题目数">{{ currentPaper.totalQuestions }}</el-descriptions-item>
            <el-descriptions-item label="总分">{{ currentPaper.totalPoints }}</el-descriptions-item>
            <el-descriptions-item label="时长">{{ currentPaper.durationMinutes }}分钟</el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-if="currentPaper.questions && currentPaper.questions.length > 0" class="questions-list">
          <h4>题目列表</h4>
          <div
            v-for="(question, index) in currentPaper.questions"
            :key="question.questionId"
            class="question-item"
          >
            <div class="question-header">
              <span class="question-number">{{ index + 1 }}.</span>
              <span class="question-type">{{ getQuestionTypeLabel(question.questionType) }}</span>
              <span class="question-points">{{ question.points }}分</span>
            </div>
            <div class="question-content">{{ question.questionContent }}</div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 创建考试对话框 -->
    <el-dialog
      v-model="createExamDialogVisible"
      title="创建考试"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="createExamFormRef"
        :model="createExamForm"
        :rules="createExamFormRules"
        label-width="120px"
      >
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="createExamForm.examName" placeholder="请输入考试名称" />
        </el-form-item>
        
        <el-form-item label="考试描述" prop="description">
          <el-input
            v-model="createExamForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入考试描述"
          />
        </el-form-item>
        
        <el-form-item label="班级" prop="classIds">
          <el-select 
            v-model="createExamForm.classIds" 
            placeholder="请选择班级" 
            style="width: 100%"
            multiple
            collapse-tags
          >
            <el-option
              v-for="classItem in classList"
              :key="classItem.id"
              :label="classItem.className"
              :value="classItem.id"
            />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="createExamForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="createExamForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试时长" prop="durationMinutes">
              <el-input-number
                v-model="createExamForm.durationMinutes"
                :min="1"
                :max="480"
                placeholder="分钟"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="12">
            <el-form-item label="最大尝试次数" prop="maxAttempts">
              <el-input-number
                v-model="createExamForm.maxAttempts"
                :min="1"
                :max="10"
                placeholder="次数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col> -->
        </el-row>
        
        <el-form-item label="乱序设置">
          <el-checkbox v-model="createExamForm.isRandomOrder">题目乱序</el-checkbox>
          <el-checkbox v-model="createExamForm.isRandomOptions">选项乱序</el-checkbox>
        </el-form-item>
        
        <el-form-item label="其他设置">
          <el-checkbox v-model="createExamForm.allowReview">允许查看答案</el-checkbox>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createExamDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmCreateExam" :loading="createExamLoading">
            创建考试
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 题目详情对话框 -->
    <el-dialog
      v-model="questionsDialogVisible"
      title="试卷题目详情"
      width="90%"
      :close-on-click-modal="false"
      class="questions-dialog"
    >
      <div v-if="currentPaper" class="questions-detail">
        <div class="paper-header">
          <h3>{{ currentPaper.paperName }}</h3>
          <p>试卷代码：{{ currentPaper.paperCode }} | 班级：{{ currentPaper.className }} | 课程：{{ currentPaper.courseName }}</p>
          <p>题目数量：{{ currentPaper.totalQuestions }} | 总分：{{ currentPaper.totalPoints }} | 时长：{{ currentPaper.durationMinutes }}分钟</p>
        </div>
        
        <div v-if="currentPaper.questions && currentPaper.questions.length > 0" class="questions-list">
          <!-- 按题型分组显示题目 -->
          <div v-for="typeKey in questionTypeOrder" :key="typeKey" class="question-type-section">
            <div v-if="getQuestionsByType(typeKey).length > 0" class="type-section-header">
              <h3>{{ questionTypes[typeKey].label }}（{{ getQuestionsByType(typeKey).length }}题）</h3>
            </div>
            <div v-for="(question, index) in getQuestionsByType(typeKey)" :key="question.questionId" class="question-detail-item">
              <div class="question-header">
                <div class="question-info">
                  <span class="question-number">{{ getGlobalQuestionNumber(question)}}.</span>
                  <span class="question-type">{{ getQuestionTypeLabel(question.questionType) }}</span>
                  <span class="question-difficulty">{{ getDifficultyLabel(question.difficulty) }}</span>
                  <span class="question-points">{{ question.points }}分</span>
                </div>
              </div>
              
              <div class="question-content">
                <h4>{{ question.questionTitle }}</h4>
                <p>{{ question.questionContent }}</p>
              </div>
              
              <!-- 选择题选项 -->
              <div v-if="question.options && question.options.length > 0" class="question-options">
                <h5>选项：</h5>
                <div class="options-list">
                  <div v-for="option in question.options" :key="option.id" 
                       :class="['option-item', { 'correct-option': option.isCorrect }]">
                    <span class="option-key">{{ option.optionKey }}.</span>
                    <span class="option-content">{{ option.optionContent }}</span>
                    <el-tag v-if="option.isCorrect" type="success" size="small">正确答案</el-tag>
                  </div>
                </div>
              </div>
              
              <!-- 题目答案 -->
              <div v-if="question.answers && question.answers.length > 0" class="question-answers">
                <h5>答案：</h5>
                <div class="answers-list">
                  <div v-for="answer in question.answers" :key="answer.id" class="answer-item">
                    <span class="answer-type">{{ answer.answerType }}：</span>
                    <span class="answer-content">{{ answer.answerContent }}</span>
                    <el-tag v-if="answer.isPrimary" type="primary" size="small">主要答案</el-tag>
                  </div>
                </div>
              </div>
              
              <!-- 题目解析 -->
              <div v-if="question.explanation" class="question-explanation">
                <h5>解析：</h5>
                <p>{{ question.explanation }}</p>
              </div>
              
              <!-- 题目标签 -->
              <div v-if="question.tags" class="question-tags">
                <h5>标签：</h5>
                <el-tag v-for="tag in question.tags.split(',')" :key="tag" size="small" style="margin-right: 5px;">
                  {{ tag.trim() }}
                </el-tag>
              </div>
              
              <el-divider />
            </div>
          </div>
        </div>
        <div v-else class="no-questions">
          暂无题目
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, ArrowDown, Delete, List } from '@element-plus/icons-vue'
import { paperApi, classApi, courseApi, examApi, userApi } from '@/api/admin'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const paperList = ref([])
const classList = ref([])
const courseList = ref([])
const createDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const questionsDialogVisible = ref(false)
const currentPaper = ref(null)

// 创建考试对话框
const createExamDialogVisible = ref(false)
const createExamLoading = ref(false)
const createExamFormRef = ref(null)
const currentPaperForExam = ref(null)

// 创建考试表单
const createExamForm = reactive({
  examName: '',
  description: '',
  paperId: null,
  classIds: [],
  startTime: '',
  endTime: '',
  durationMinutes: 120,
  maxAttempts: 1,//模板数据，现没有真实意义。
  isRandomOrder: true,
  isRandomOptions: true,
  allowReview: true
})

// 创建考试表单验证规则
const createExamFormRules = {
  examName: [
    { required: true, message: '请输入考试名称', trigger: 'blur' }
  ],
  classIds: [
    { required: true, message: '请选择班级', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一个班级', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  durationMinutes: [
    { required: true, message: '请输入考试时长', trigger: 'blur' }
  ]
}

// 手动组卷表单验证规则已移除（移动到独立组件）

// 搜索表单
const searchForm = reactive({
  keyword: ''
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 创建表单
const createForm = reactive({
  paperName: '',
  description: '',
  courseId: null,
  durationMinutes: 120,
  totalQuestions: 20,
  totalPoints: 100,
  questionTypeDistribution: {
    'SINGLE_CHOICE': 10,
    'MULTIPLE_CHOICE': 5,
    'TRUE_FALSE': 3,
    'FILL_BLANK': 2,
    'SUBJECTIVE': 0,
    'PROGRAMMING': 0
  },
  difficultyDistribution: {
    'EASY': 5,
    'MEDIUM': 10,
    'HARD': 5
  }
})

// 表单验证规则
const createFormRules = {
  paperName: [
    { required: true, message: '请输入试卷名称', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  durationMinutes: [
    { required: true, message: '请输入考试时长', trigger: 'blur' }
  ],
  totalQuestions: [
    { required: true, message: '请输入总题目数', trigger: 'blur' }
  ],
  totalPoints: [
    { required: true, message: '请输入总分', trigger: 'blur' }
  ]
}

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
const loadPaperList = async () => {
  try {
    loading.value = true
    const response = await paperApi.getPapersWithPagination(
      pagination.page,
      pagination.size,
      searchForm.keyword
    )
    
    console.log('Paper list response:', response)
    
    if (response.code === 200) {
      paperList.value = response.data.content || []
      pagination.total = response.data.total || 0
      console.log('Paper list loaded:', paperList.value.length, 'papers')
    } else {
      ElMessage.error(response.message || '获取试卷列表失败')
    }
  } catch (error) {
    console.error('Load paper list error:', error)
    ElMessage.error('获取试卷列表失败')
  } finally {
    loading.value = false
  }
}

const loadClassList = async () => {
  try {
    const response = await classApi.getAllClasses()
    if (response.code === 200) {
      classList.value = response.data || []
    }
  } catch (error) {
    console.error('Load class list error:', error)
  }
}

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

const handleCreatePaper = () => {
  createDialogVisible.value = true
  resetCreateForm()
}


const handleConfirmCreate = async () => {
  try {
    submitting.value = true
    const response = await paperApi.generatePaper(createForm)
    
    if (response.code === 200) {
      ElMessage.success('试卷生成成功')
      createDialogVisible.value = false
      loadPaperList()
    }
  } catch (error) {
    console.error('Create paper error:', error)
    ElMessage.error('试卷生成失败')
  } finally {
    submitting.value = false
  }
}

const handleViewPaper = async (row) => {
  try {
    const response = await paperApi.getPaperById(row.id)
    if (response.code === 200) {
      currentPaper.value = response.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    console.error('View paper error:', error)
    ElMessage.error('获取试卷详情失败')
  }
}

const handleViewQuestions = async (row) => {
  try {
    const response = await paperApi.getPaperById(row.id)
    if (response.code === 200) {
      currentPaper.value = response.data
      questionsDialogVisible.value = true
    }
  } catch (error) {
    console.error('View questions error:', error)
    ElMessage.error('获取题目详情失败')
  }
}

const handleCreateExam = (row) => {
  currentPaperForExam.value = row
  createExamForm.paperId = row.id
  createExamForm.classIds = [] // 不再从试卷继承，需要手动选择班级
  createExamForm.examName = `${row.paperName} - 考试`
  createExamForm.durationMinutes = row.durationMinutes
  createExamDialogVisible.value = true
}

const handleActionCommand = (command, row) => {
  switch (command) {
    case 'createExam':
      handleCreateExam(row)
      break
    case 'delete':
      handleDeletePaper(row)
      break
  }
}

const handleDeletePaper = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除试卷"${row.paperName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await paperApi.deletePaper(row.id)
    ElMessage.success('删除成功')
    loadPaperList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete paper error:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadPaperList()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadPaperList()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadPaperList()
}

const resetCreateForm = () => {
  Object.assign(createForm, {
    paperName: '',
    description: '',
    courseId: null,
    durationMinutes: 120,
    totalQuestions: 20,
    totalPoints: 100,
    questionTypeDistribution: {
      'SINGLE_CHOICE': 10,
      'MULTIPLE_CHOICE': 5,
      'TRUE_FALSE': 3,
      'FILL_BLANK': 2,
      'SUBJECTIVE': 0
    },
    difficultyDistribution: {
      'EASY': 5,
      'MEDIUM': 10,
      'HARD': 5
    }
  })
}

const formatDate = (dateStr) => {
  if (!dateStr) return '暂无'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const getQuestionTypeLabel = (type) => {
  return questionTypes[type]?.label || type
}

const getDifficultyLabel = (difficulty) => {
  return difficulties[difficulty]?.label || difficulty
}

const getQuestionTypeOrder = (typeKey) => {
  const index = questionTypeOrder.indexOf(typeKey)
  return index !== -1 ? `第${index + 1}部分` : ''
}

// 按题型分组题目
const getQuestionsByType = (typeKey) => {
  if (!currentPaper.value || !currentPaper.value.questions) {
    return []
  }
  return currentPaper.value.questions.filter(question => question.questionType === typeKey)
}

// 获取题目的全局题号
const getGlobalQuestionNumber = (question) => {
  if (!currentPaper.value || !currentPaper.value.questions) {
    return 1
  }
  const index = currentPaper.value.questions.findIndex(q => q.questionId === question.questionId)
  return index + 1
}

// 创建考试相关方法
const handleConfirmCreateExam = async () => {
  if (!createExamFormRef.value) return
  
  try {
    await createExamFormRef.value.validate()
    
    createExamLoading.value = true
    
    // 转换时间格式 - 转换为ISO格式
    const examData = {
      ...createExamForm,
      startTime: createExamForm.startTime ? createExamForm.startTime.replace(' ', 'T') : null,
      endTime: createExamForm.endTime ? createExamForm.endTime.replace(' ', 'T') : null
    }
    
    const response = await examApi.createExam(examData)
    
    if (response.code === 200) {
      ElMessage.success('考试创建成功')
      createExamDialogVisible.value = false
      resetCreateExamForm()
      // 可以跳转到考试管理页面或刷新当前页面
      loadPaperList()
    } else {
      ElMessage.error(response.message || '创建考试失败')
    }
  } catch (error) {
    console.error('Create exam error:', error)
    ElMessage.error('创建考试失败')
  } finally {
    createExamLoading.value = false
  }
}

const resetCreateExamForm = () => {
  createExamForm.examName = ''
  createExamForm.description = ''
  createExamForm.paperId = null
  createExamForm.classIds = []
  createExamForm.startTime = ''
  createExamForm.endTime = ''
  createExamForm.durationMinutes = 120
  createExamForm.maxAttempts = 1
  createExamForm.isRandomOrder = true
  createExamForm.isRandomOptions = true
  createExamForm.allowReview = true
  currentPaperForExam.value = null
}

// 手动组卷方法（跳转到独立页面）
const handleManualCreatePaper = () => {
  router.push('/teacher/manual-paper-grouping')
}

// 生命周期
onMounted(() => {
  loadPaperList()
  loadClassList()
  loadCourseList()
})
</script>

<style scoped>
.exam-grouping {
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

.search-bar {
  margin-bottom: 20px;
}

.paper-list {
  background: white;
  border-radius: 4px;
  overflow: hidden;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.action-buttons {
  display: flex;
  gap: 6px;
  align-items: center;
  justify-content: flex-start;
}

.action-buttons .el-button {
  margin: 0;
  padding: 4px 8px;
  font-size: 12px;
  min-width: 50px;
}

.action-buttons .el-dropdown {
  margin: 0;
}

.question-type-config,
.difficulty-config {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.type-section {
  margin-bottom: 10px;
}

.type-section h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 14px;
}

.type-order-info {
  margin-bottom: 10px;
}

.type-item,
.difficulty-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.type-label,
.difficulty-label {
  min-width: 80px;
  font-size: 14px;
  font-weight: 500;
}

.type-order {
  font-size: 12px;
  color: #909399;
  font-style: italic;
}

.paper-detail {
  max-height: 600px;
  overflow-y: auto;
}

.paper-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.paper-header h3 {
  margin: 0 0 8px 0;
  color: #303133;
}

.paper-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

/* 题目详情对话框样式 */
.questions-dialog .el-dialog__body {
  max-height: 80vh;
  overflow-y: auto;
}

.questions-detail {
  padding: 0;
}

.question-detail-item {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
}

.question-detail-item .question-header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.question-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.question-number {
  font-weight: bold;
  font-size: 16px;
  color: #409eff;
}

.question-type, .question-difficulty {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.question-type {
  background: #e1f3d8;
  color: #67c23a;
}

.question-difficulty {
  background: #fdf6ec;
  color: #e6a23c;
}

.question-points {
  font-weight: bold;
  color: #f56c6c;
}

.question-content h4 {
  margin: 10px 0 8px 0;
  color: #303133;
  font-size: 16px;
}

.question-content p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.question-options, .question-answers, .question-explanation, .question-tags {
  margin-top: 15px;
}
</style>

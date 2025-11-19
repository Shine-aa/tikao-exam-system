<template>
  <div class="question-bank-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>题库管理<template v-if="currentCourseName"> - {{ currentCourseName }}</template></h2>
      <p>管理题目、知识点和标签</p>
    </div>

    <!-- 统计信息 -->
    <el-card class="stats-card" shadow="hover" style="margin-bottom: 20px;">
      <div class="stats-header">
        <h3 style="margin: 0; font-size: 16px;">题库概览</h3>
      </div>
      <div class="stats-content">
        <!-- 统一网格布局展示所有统计项 -->
        <div class="stats-grid">
          <!-- 题目总数（仍然突出显示，但放在网格中） -->
          <div class="stat-item total-stats">
            <div class="stat-number">{{ statistics.totalQuestions || 0 }}</div>
            <div class="stat-label">题目总数</div>
          </div>
          <!-- 各题型统计 -->
          <div class="stat-item">
            <div class="stat-number">{{ statistics.singleChoiceCount || 0 }}</div>
            <div class="stat-label">单选题</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ statistics.multipleChoiceCount || 0 }}</div>
            <div class="stat-label">多选题</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ statistics.trueFalseCount || 0 }}</div>
            <div class="stat-label">判断题</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ statistics.fillBlankCount || 0 }}</div>
            <div class="stat-label">填空题</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ statistics.subjectiveCount || 0 }}</div>
            <div class="stat-label">主观题</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ statistics.programmingCount || 0 }}</div>
            <div class="stat-label">程序题</div>
          </div>
        </div>
      </div>
      

    </el-card>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="题目类型">
          <el-select v-model="searchForm.type" placeholder="题目类型" clearable style="width: 140px">
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="判断题" value="TRUE_FALSE" />
            <el-option label="填空题" value="FILL_BLANK" />
            <el-option label="主观题" value="SUBJECTIVE" />
            <el-option label="程序题" value="PROGRAMMING" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="难度等级" clearable style="width: 120px">
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属课程">
          <el-select 
            v-model="searchForm.courseId" 
            placeholder="选择课程" 
            clearable 
            style="width: 180px"
            :loading="coursesLoading"
            @change="handleCourseChange"
          >
            <el-option 
              v-for="course in courseList" 
              :key="course.id" 
              :label="course.courseName || course.name" 
              :value="course.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入关键词"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="action-card">
      <div class="action-bar">
        <div class="action-left">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增题目
          </el-button>
          <el-button type="success" @click="handleImport">
            <el-icon><Upload /></el-icon>
            导入题库
          </el-button>
          <el-button 
            type="danger" 
            :disabled="selectedQuestions.length === 0"
            @click="handleBatchDelete"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
        <div class="action-right">
          <el-button @click="loadQuestionList">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 题目列表 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="questionList"
        @selection-change="handleSelectionChange"
        row-key="id"
        stripe
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="题目标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="题目类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeDescription(row.type) }}
            </el-tag>

          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="getDifficultyTagType(row.difficulty)">
              {{ getDifficultyDescription(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="分值" width="80" />
        <el-table-column prop="tags" label="标签" min-width="150">
          <template #default="{ row }">
            <el-tag
              v-for="tag in getTagList(row.tags)"
              :key="tag"
              size="small"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 题目详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="题目详情"
      width="80%"
      :close-on-click-modal="false"
    >
      <div v-if="currentQuestion" class="question-detail">
        <div class="question-header">
          <h3>{{ currentQuestion.title || '无标题' }}</h3>
          <div class="question-meta">
            <el-tag :type="getTypeTagType(currentQuestion.type)">
              {{ getTypeDescription(currentQuestion.type) }}
            </el-tag>
            <el-tag :type="getDifficultyTagType(currentQuestion.difficulty)">
              {{ getDifficultyDescription(currentQuestion.difficulty) }}
            </el-tag>
            <span class="points">分值: {{ currentQuestion.points }}</span>
          </div>
        </div>
        
        <div class="question-content">
          <h4>题目内容:</h4>
          <div class="content-text" v-html="formatContent(currentQuestion.content)"></div>
        </div>

        <!-- 题目图片 -->
        <div v-if="currentQuestion.images && getImageUrls(currentQuestion.images).length > 0" class="question-images">
          <h4>题目图片:</h4>
          <div class="images-grid">
            <div v-for="(imageUrl, index) in getImageUrls(currentQuestion.images)" :key="index" class="image-item">
              <el-image
                :src="imageUrl"
                :preview-src-list="getImageUrls(currentQuestion.images)"
                :initial-index="index"
                fit="contain"
                style="width: 100%; max-height: 400px; cursor: pointer;"
              >
                <template #error>
                  <div class="image-error">图片加载失败</div>
                </template>
              </el-image>
            </div>
          </div>
        </div>

        <!-- 选择题选项 -->
        <div v-if="currentQuestion.options && currentQuestion.options.length > 0" class="question-options">
          <h4>选项:</h4>
          <div class="options-list">
            <div
              v-for="option in currentQuestion.options"
              :key="option.id"
              class="option-item"
              :class="{ 'correct-option': option.isCorrect }"
            >
              <span class="option-key">{{ option.optionKey }}.</span>
              <span class="option-content">{{ option.optionContent }}</span>
              <el-tag v-if="option.isCorrect" type="success" size="small">正确答案</el-tag>
            </div>
          </div>
        </div>

        <!-- 题目答案 -->
        <div v-if="currentQuestion.answers && currentQuestion.answers.length > 0" class="question-answers">
          <h4>答案:</h4>
          <div class="answers-list">
            <div
              v-for="answer in currentQuestion.answers"
              :key="answer.id"
              class="answer-item"
            >
              <span class="answer-content">{{ answer.answerContent }}</span>
              <el-tag size="small">{{ answer.answerTypeDescription }}</el-tag>
            </div>
          </div>
        </div>

        <!-- 程序题答案（代码） -->
        <div v-if="currentQuestion.type === 'PROGRAMMING' && currentQuestion.correctAnswer" class="question-answers">
          <h4>参考答案:</h4>
          <div class="answer-item code-answer">
            <pre class="code-block"><code>{{ currentQuestion.correctAnswer }}</code></pre>
          </div>
        </div>

        <!-- 题目解析 -->
        <div v-if="currentQuestion.explanation" class="question-explanation">
          <h4>解析:</h4>
          <div class="explanation-text" v-html="formatContent(currentQuestion.explanation)"></div>
        </div>
      </div>
    </el-dialog>

    <!-- 导入题库对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入题库"
      width="600px"
      :close-on-click-modal="false"
      @open="handleImportDialogOpen"
    >
      <div class="import-content">
        <!-- 课程选择 -->
        <el-form-item label="选择课程" style="margin-bottom: 20px;">
          <el-select
            v-model="selectedCourseId"
            placeholder="请选择要导入的课程"
            style="width: 100%;"
            :loading="coursesLoading"
          >
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="fileList"
        >
          <el-button type="primary">选择Excel文件</el-button>
          <template #tip>
            <div class="el-upload__tip">
              只能上传 .xlsx 或 .xls 格式的文件，请参考格式说明文档
            </div>
          </template>
        </el-upload>

        <el-alert
          v-if="importResult"
          :title="`导入完成: 成功 ${importResult.successCount} 条，失败 ${importResult.errorCount} 条`"
          :type="importResult.errorCount > 0 ? 'warning' : 'success'"
          :closable="false"
          style="margin-top: 20px"
        >
          <template #default>
            <div v-if="importResult.errors && importResult.errors.length > 0">
              <div v-for="(error, index) in importResult.errors" :key="index" style="margin-top: 8px">
                {{ error }}
              </div>
            </div>
          </template>
        </el-alert>

        <div v-if="fileList.length > 0" class="upload-actions" style="margin-top: 20px">
          <el-button type="primary" :loading="uploading" @click="submitUpload">
            开始导入
          </el-button>
          <el-button @click="handleImportCancel">取消</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 新增/编辑题目对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="editDialogTitle"
      width="70%"
      :close-on-click-modal="false"
    >
      <el-form
        v-if="editingQuestion"
        :model="editingQuestion"
        :rules="rules"
        ref="editFormRef"
        label-width="100px"
      >
        <el-form-item label="题目标题" prop="title">
          <el-input v-model="editingQuestion.title" placeholder="请输入题目标题" />
        </el-form-item>

        <el-form-item label="题目内容" prop="content">
          <el-input
            v-model="editingQuestion.content"
            type="textarea"
            rows="4"
            placeholder="请输入题目内容"
          />
        </el-form-item>

        <el-form-item label="题目类型" prop="type">
          <el-select v-model="editingQuestion.type" placeholder="请选择题目类型">
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="判断题" value="TRUE_FALSE" />
            <el-option label="填空题" value="FILL_BLANK" />
            <el-option label="主观题" value="SUBJECTIVE" />
            <el-option label="程序题" value="PROGRAMMING" />
          </el-select>
        </el-form-item>

        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="editingQuestion.difficulty" placeholder="请选择难度">
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
        </el-form-item>

        <el-form-item label="分值" prop="points">
          <el-input-number v-model="editingQuestion.points" :min="1" :max="100" />
        </el-form-item>

        <el-form-item label="所属课程" prop="courseId" required>
          <el-select v-model="editingQuestion.courseId" placeholder="请选择课程" :loading="coursesLoading">
            <el-option 
              v-for="course in courseList" 
              :key="course.id" 
              :label="course.courseName" 
              :value="course.id" 
            />
          </el-select>
        </el-form-item>

        <!-- 选择题选项 -->
        <template v-if="editingQuestion.type === 'SINGLE_CHOICE' || editingQuestion.type === 'MULTIPLE_CHOICE'">
          <el-form-item label="选项">
            <div v-for="(option, index) in editingQuestion.options" :key="index" class="option-input-group">
              <el-checkbox
                v-model="option.isCorrect"
                @change="(val) => handleOptionChange(option, index, val)"
                style="margin-right: 10px"
              >{{ option.optionKey }}.</el-checkbox>
              <el-input
                v-model="option.optionContent"
                placeholder="请输入选项内容"
                style="width: calc(100% - 50px)"
              />
            </div>
            <el-button type="primary" plain @click="addOption" size="small" style="margin-top: 10px">
              添加选项
            </el-button>
          </el-form-item>
        </template>

        <!-- 判断题选项 -->
        <template v-else-if="editingQuestion.type === 'TRUE_FALSE'">
          <el-form-item label="选项">
            <div class="option-input-group">
              <el-checkbox
                v-model="editingQuestion.options[0].isCorrect"
                @change="(val) => handleOptionChange(editingQuestion.options[0], 0, val)"
                style="margin-right: 10px"
              >正确</el-checkbox>
            </div>
            <div class="option-input-group">
              <el-checkbox
                v-model="editingQuestion.options[1].isCorrect"
                @change="(val) => handleOptionChange(editingQuestion.options[1], 1, val)"
                style="margin-right: 10px"
              >错误</el-checkbox>
            </div>
          </el-form-item>
        </template>

        <!-- 程序题特有字段 -->
        <template v-if="editingQuestion.type === 'PROGRAMMING'">
          <el-form-item label="编程语言">
            <el-select v-model="editingQuestion.programmingLanguage" placeholder="请选择编程语言">
              <el-option label="Java" value="JAVA" />
              <el-option label="Python" value="PYTHON" />
              <el-option label="C++" value="CPP" />
              <el-option label="C" value="C" />
            </el-select>
          </el-form-item>
        </template>

        <!-- 正确答案 - 仅在填空题、主观题和程序题时显示并必填 -->
        <template v-if="editingQuestion.type === 'FILL_BLANK' || editingQuestion.type === 'SUBJECTIVE' || editingQuestion.type === 'PROGRAMMING'">
          <el-form-item label="正确答案" :prop="'correctAnswer'">
            <el-input
              v-model="editingQuestion.correctAnswer"
              type="textarea"
              rows="2"
              placeholder="请输入正确答案"
            />
          </el-form-item>
        </template>

        <el-form-item label="标签">
          <el-input v-model="editingQuestion.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>

        <el-form-item label="答案解析">
          <el-input
            v-model="editingQuestion.explanation"
            type="textarea"
            rows="3"
            placeholder="请输入答案解析"
          />
        </el-form-item>

        <el-form-item label="图片路径">
          <el-input v-model="editingQuestion.images" placeholder="多个图片路径用分号分隔" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitQuestion">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Refresh, Upload, Check } from '@element-plus/icons-vue'
import { getQuestions, deleteQuestion, batchDeleteQuestions, getQuestionStatistics, importQuestions, createQuestion, updateQuestion, getCourses } from '../../api/admin'

// 响应式数据
const route = useRoute()
const loading = ref(false)
const questionList = ref([])
const selectedQuestions = ref([])
const statistics = ref({})
const detailDialogVisible = ref(false)
const currentQuestion = ref(null)
const editingQuestion = ref(null)
// 课程相关
const courseList = ref([]) // 课程列表
const coursesLoading = ref(false) // 课程加载状态
// 路由参数相关
const currentCourseName = ref('') // 当前选中的课程名称

// 加载课程列表
const loadCourseList = async () => {
  try {
    coursesLoading.value = true
    const response = await getCourses()
    if (response.code === 200 && response.data) {
      courseList.value = response.data
    } else {
      ElMessage.error('获取课程列表失败')
      courseList.value = []
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
    courseList.value = []
  } finally {
    coursesLoading.value = false
  }
}


// 导入相关
const importDialogVisible = ref(false)
const fileList = ref([])
const uploading = ref(false)
const importResult = ref(null)
const uploadRef = ref(null)
const selectedCourseId = ref('') // 选中的课程ID

// 搜索表单
const searchForm = reactive({
  type: '',
  difficulty: '',
  keyword: '',
  courseId: route.query.courseId || ''
})

// 防抖函数
const debounce = (func, wait) => {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

// 防抖搜索函数
const debouncedSearch = debounce(() => {
  pagination.page = 1
  loadQuestionList()
}, 500)

// 监听搜索表单变化
watch(
  () => [searchForm.type, searchForm.difficulty, searchForm.keyword, searchForm.courseId],
  () => {
    debouncedSearch()
  },
  { deep: true }
)

// 监听题目类型变化，自动更新选项
watch(
  () => editingQuestion.value?.type,
  (newType, oldType) => {
    // 确保editingQuestion.value存在再进行操作
    if (!editingQuestion.value) return
    
    // 当类型从非判断题变为判断题时，更新选项
    if (newType === 'TRUE_FALSE' && newType !== oldType) {
      editingQuestion.value.options = [
        { optionKey: 'A', optionContent: '正确', isCorrect: false },
        { optionKey: 'B', optionContent: '错误', isCorrect: false }
      ]
    } 
    // 当类型从判断题变为其他选择题类型时，重置为默认选项
    else if (newType !== 'TRUE_FALSE' && oldType === 'TRUE_FALSE' && ['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(newType)) {
      editingQuestion.value.options = [
        { optionKey: 'A', optionContent: '', isCorrect: false },
        { optionKey: 'B', optionContent: '', isCorrect: false },
        { optionKey: 'C', optionContent: '', isCorrect: false },
        { optionKey: 'D', optionContent: '', isCorrect: false }
      ]
    }
  }
)

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 监听路由参数变化
watch(() => route.query, (newQuery) => {
  if (newQuery.courseId) {
    // 1. 统一类型：将路由参数的courseId转为与课程列表id一致的类型（假设课程id是数字）
    const courseId = Number(newQuery.courseId); // 若课程id是字符串则用String()
    searchForm.courseId = courseId;

    // 2. 优先用路由中的courseName，否则从课程列表查找
    if (newQuery.courseName) {
      currentCourseName.value = newQuery.courseName;
    } else if (courseList.value.length > 0) {
      const selectedCourse = courseList.value.find(course => course.id === courseId);
      currentCourseName.value = selectedCourse ? (selectedCourse.courseName || selectedCourse.name || '') : '';
    } else {
      currentCourseName.value = '';
    }

    pagination.page = 1;
    loadQuestionList();
  }
});

// 处理课程选择变化
const handleCourseChange = (courseId) => {
  if (courseId) {
    // 从课程列表中查找对应的课程名称
    const selectedCourse = courseList.value.find(course => course.id === courseId)
    if (selectedCourse) {
      currentCourseName.value = selectedCourse.courseName || selectedCourse.name || ''
    }
  } else {
    // 清除选择时，清空课程名称
    currentCourseName.value = ''
  }
  // 重置页码并重新加载数据
  pagination.page = 1
  loadQuestionList()
}

// 计算属性
const getTypeTagType = (type) => {
  const typeMap = {
    'SINGLE_CHOICE': 'primary',
    'MULTIPLE_CHOICE': 'success',
    'TRUE_FALSE': 'warning',
    'FILL_BLANK': 'info',
    'SUBJECTIVE': 'danger',
    'PROGRAMMING': 'warning'
  }
  return typeMap[type] || 'default'
}

const getDifficultyTagType = (difficulty) => {
  const difficultyMap = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return difficultyMap[difficulty] || 'default'
}

const getTypeDescription = (type) => {
  const typeMap = {
    'SINGLE_CHOICE': '单选题',
    'MULTIPLE_CHOICE': '多选题',
    'TRUE_FALSE': '判断题',
    'FILL_BLANK': '填空题',
    'SUBJECTIVE': '主观题',
    'PROGRAMMING': '程序题'
  }
  return typeMap[type] || type
}

const getDifficultyDescription = (difficulty) => {
  const difficultyMap = {
    'EASY': '简单',
    'MEDIUM': '中等',
    'HARD': '困难'
  }
  return difficultyMap[difficulty] || difficulty
}

// 提交新增/编辑题目
const submitQuestion = async () => {
  try {
    let response
    // 准备提交数据
    const submitData = {
      title: editingQuestion.value.title,
      content: editingQuestion.value.content,
      type: editingQuestion.value.type,
      difficulty: editingQuestion.value.difficulty,
      points: editingQuestion.value.points,
      tags: editingQuestion.value.tags,
      explanation: editingQuestion.value.explanation,
      programmingLanguage: editingQuestion.value.programmingLanguage,
      images: editingQuestion.value.images,
      courseId: editingQuestion.value.courseId // 添加课程ID字段
    }
    
    // 根据题型处理不同的数据格式
    if (['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE'].includes(editingQuestion.value.type)) {
      // 处理选项
      if (editingQuestion.value.options) {
        // 对于判断题，确保有正确的选项内容
        if (editingQuestion.value.type === 'TRUE_FALSE') {
          // 确保判断题有两个选项，并且内容正确
          if (editingQuestion.value.options.length < 2) {
            editingQuestion.value.options = [
              { optionKey: 'A', optionContent: '正确', isCorrect: false },
              { optionKey: 'B', optionContent: '错误', isCorrect: false }
            ]
          } else {
            // 确保选项内容正确
            editingQuestion.value.options[0].optionKey = 'A'
            editingQuestion.value.options[0].optionContent = '正确'
            editingQuestion.value.options[1].optionKey = 'B'
            editingQuestion.value.options[1].optionContent = '错误'
          }
        }
        
        submitData.options = editingQuestion.value.options
          .filter(opt => opt.optionContent && opt.optionContent.trim())
          .map((opt) => ({
            optionKey: opt.optionKey,
            optionContent: opt.optionContent.trim(),
            isCorrect: opt.isCorrect ? 1 : 0
          }))
      }
      
      // 构建答案列表 - 确保至少有一个正确答案
      const correctOptions = editingQuestion.value.options
        .filter(option => option.isCorrect)
        .map(option => ({
          answerContent: option.optionKey
        }))
        
      if (correctOptions.length === 0) {
        ElMessage.warning('请至少选择一个正确答案')
        return
      }
      
      submitData.answers = correctOptions
      // 清空非选择题的correctAnswer字段
      delete submitData.correctAnswer
    } else if (editingQuestion.value.type === 'PROGRAMMING') {
      // 程序题处理
      // 构建答案列表
      if (!editingQuestion.value.correctAnswer || editingQuestion.value.correctAnswer.trim() === '') {
        ElMessage.warning('请输入正确答案')
        return
      }
      submitData.answers = [{ answerContent: editingQuestion.value.correctAnswer.trim() }]
      
      // 如果有测试用例
      if (editingQuestion.value.testCases && editingQuestion.value.testCases.length > 0) {
        submitData.testCases = editingQuestion.value.testCases
          .filter(testCase => testCase.input && testCase.input.trim() && testCase.output && testCase.output.trim())
          .map(testCase => ({
            input: testCase.input.trim(),
            output: testCase.output.trim()
          }))
      }
      // 清空非选择题的correctAnswer字段
      delete submitData.correctAnswer
    } else {
      // 填空题和主观题
      if (!editingQuestion.value.correctAnswer || editingQuestion.value.correctAnswer.trim() === '') {
        ElMessage.warning('请输入正确答案')
        return
      }
      // 构建答案列表
      submitData.answers = [{ answerContent: editingQuestion.value.correctAnswer.trim() }]
      // 清空非选择题的correctAnswer字段
      delete submitData.correctAnswer
    }
    
    console.log('提交的数据:', submitData)
    
    if (isEditMode.value) {
      // 编辑模式
      response = await updateQuestion(editingQuestion.value.id, submitData)
    } else {
      // 新增模式
      response = await createQuestion(submitData)
    }
    
    if (response.code === 200) {
      ElMessage.success(isEditMode.value ? '题目更新成功' : '题目创建成功')
      editDialogVisible.value = false
      loadQuestionList()
      loadStatistics()
      // 重置表单
      resetQuestionForm()
    } else {
      ElMessage.error(response.message || (isEditMode.value ? '题目更新失败' : '题目创建失败'))
    }
  } catch (error) {
    console.error('Submit question error:', error)
    ElMessage.error('操作失败: ' + (error.message || '未知错误'))
  }
}

// 重置题目表单
const resetQuestionForm = () => {
  editingQuestion.value = {
    title: '',
    content: '',
    type: 'SINGLE_CHOICE',
    difficulty: 'MEDIUM',
    points: 5,
    tags: '',
    explanation: '',
    images: '',
    programmingLanguage: 'JAVA',
    correctAnswer: '',
    options: [
      { optionKey: 'A', optionContent: '', isCorrect: false },
      { optionKey: 'B', optionContent: '', isCorrect: false },
      { optionKey: 'C', optionContent: '', isCorrect: false },
      { optionKey: 'D', optionContent: '', isCorrect: false }
    ],
    testCases: []
  }
}

// 处理选项变化
const handleOptionChange = (option, index, checked) => {
  // 单选题只能有一个正确选项
  if (editingQuestion.value.type === 'SINGLE_CHOICE') {
    // 如果当前选项被选中
    if (checked) {
      // 先重置所有选项
      editingQuestion.value.options.forEach(opt => {
        opt.isCorrect = false
      })
      // 再将当前点击的选项设为正确
      option.isCorrect = true
    } else {
      // 如果取消选中，直接设置为false
      option.isCorrect = false
    }
  } else {
    // 多选题和判断题可以有多个正确选项，直接使用v-model的值
    option.isCorrect = checked
  }
}

// 添加选项
const addOption = () => {
  const lastOption = editingQuestion.value.options[editingQuestion.value.options.length - 1]
  const nextKey = String.fromCharCode(lastOption.optionKey.charCodeAt(0) + 1)
  editingQuestion.value.options.push({
    optionKey: nextKey,
    optionContent: '',
    isCorrect: false
  })
}

// 方法
const loadQuestionList = async () => {
  try {
    loading.value = true
    // 创建搜索参数，只包含有值的字段
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    // 只有当搜索条件有值时才添加到参数中
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.difficulty) params.difficulty = searchForm.difficulty
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.courseId) params.courseId = searchForm.courseId
    
    const response = await getQuestions(params)
    if (response.code === 200) {
      questionList.value = response.data.content || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取题目列表失败')
    }
  } catch (error) {
    console.error('Load question list error:', error)
    ElMessage.error('获取题目列表失败')
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  try {
    const response = await getQuestionStatistics()
    if (response.code === 200) {
      statistics.value = response.data || {}
    }
  } catch (error) {
    console.error('Load statistics error:', error)
    // 设置默认值
      statistics.value = {
        totalQuestions: 0,
        singleChoiceCount: 0,
        multipleChoiceCount: 0,
        trueFalseCount: 0,
        fillBlankCount: 0,
        subjectiveCount: 0,
        programmingCount: 0
      }
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadQuestionList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    type: '',
    difficulty: '',
    keyword: '',
    courseId: ''
  })
  pagination.page = 1
  loadQuestionList()
}

// 新增/编辑题目对话框相关
const editDialogVisible = ref(false)
const editDialogTitle = ref('新增题目')
const isEditMode = ref(false)

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入题目标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入题目内容', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择题目类型', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ],
  points: [
    { required: true, message: '请输入分值', trigger: 'blur' },
    { type: 'number', min: 1, message: '分值必须大于0', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ],
  correctAnswer: [
    { 
      required: (rule, value, callback) => {
        const questionType = editingQuestion.value?.type;
        // 只有在填空题、主观题和程序题时才必填
        if (['FILL_BLANK', 'SUBJECTIVE', 'PROGRAMMING'].includes(questionType)) {
          return value && value.trim() ? callback() : callback(new Error('请输入正确答案'));
        }
        return callback();
      }, 
      trigger: 'blur' 
    }
  ]
}

const handleAdd = async () => {
  isEditMode.value = false
  editDialogTitle.value = '新增题目'
  const defaultQuestion = {
    title: '',
    content: '',
    type: 'SINGLE_CHOICE',
    difficulty: 'MEDIUM',
    points: 3,
    correctAnswer: '',
    tags: '',
    explanation: '',
    programmingLanguage: '',
    images: '',
    courseId: '', // 添加课程ID字段
    options: [
      { optionKey: 'A', optionContent: '', isCorrect: false },
      { optionKey: 'B', optionContent: '', isCorrect: false },
      { optionKey: 'C', optionContent: '', isCorrect: false },
      { optionKey: 'D', optionContent: '', isCorrect: false }
    ]
  }
  
  editingQuestion.value = defaultQuestion
  await loadCourseList() // 加载课程列表
  editDialogVisible.value = true
}

// 导入相关函数
// 处理导入对话框打开
const handleImportDialogOpen = async () => {
  selectedCourseId.value = '' // 重置选中的课程
  await loadCourseList() // 加载课程列表
}

const handleImport = () => {
  importDialogVisible.value = true
}

const handleFileChange = (file) => {
  // 文件选择后的处理
  if (file.raw) {
    const isExcel = file.raw.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                    file.raw.type === 'application/vnd.ms-excel' ||
                    file.name.endsWith('.xlsx') || 
                    file.name.endsWith('.xls')
    
    if (!isExcel) {
      ElMessage.error('只能上传Excel文件(.xlsx, .xls)')
      fileList.value = []
      return
    }
    
    // 添加文件到列表
    fileList.value = [file]
  }
}

const handleFileRemove = () => {
  fileList.value = []
  importResult.value = null
}

const submitUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }

  if (!selectedCourseId.value) {
    ElMessage.warning('请选择要导入的课程')
    return
  }

  const formData = new FormData()
  formData.append('file', fileList.value[0].raw)
  formData.append('courseId', selectedCourseId.value)

  uploading.value = true
  try {
    const response = await importQuestions(formData, selectedCourseId.value)
    
    if (response.code === 200) {
      importResult.value = response.data
      ElMessage.success(`导入完成: 成功 ${response.data.successCount} 条`)
      
      // 如果有失败，显示警告
      if (response.data.errorCount > 0) {
        ElMessage.warning(`失败 ${response.data.errorCount} 条，请查看详情`)
      }
      
      // 刷新列表
      if (response.data.successCount > 0) {
        loadQuestionList()
      }
    } else {
      ElMessage.error(response.message || '导入失败')
    }
  } catch (error) {
    console.error('Import error:', error)
    ElMessage.error('导入失败: ' + (error.message || '未知错误'))
  } finally {
    uploading.value = false
  }
}

const handleImportCancel = () => {
  importDialogVisible.value = false
  fileList.value = []
  importResult.value = null
  selectedCourseId.value = ''
}

const handleEdit = async (row) => {
  isEditMode.value = true
  editDialogTitle.value = '编辑题目'
  // 深拷贝题目数据
  editingQuestion.value = JSON.parse(JSON.stringify(row))
  
  // 转换选项格式：从数据库格式（key/content/correct）转换为前端格式（optionKey/optionContent/isCorrect）
  if (editingQuestion.value.options && editingQuestion.value.options.length > 0) {
    editingQuestion.value.options = editingQuestion.value.options.map(opt => {
      // 如果已经是前端格式（有optionKey），直接使用，但需要处理 isCorrect 的值
      if (opt.optionKey !== undefined) {
        return {
          optionKey: opt.optionKey,
          optionContent: opt.optionContent || opt.content || '',
          isCorrect: opt.isCorrect === 1 || opt.isCorrect === true || opt.isCorrect === '1'
        }
      }
      // 如果是数据库格式（有key），转换为前端格式
      return {
        optionKey: opt.key || '',
        optionContent: opt.content || '',
        isCorrect: opt.correct === 1 || opt.correct === true || opt.correct === '1'
      }
    })
  }
  
  // 确保选项格式正确
  if (!editingQuestion.value.options || editingQuestion.value.options.length === 0) {
    if (editingQuestion.value.type === 'TRUE_FALSE') {
      // 判断题特殊处理
      editingQuestion.value.options = [
        { optionKey: 'A', optionContent: '正确', isCorrect: false },
        { optionKey: 'B', optionContent: '错误', isCorrect: false }
      ]
    } else {
      // 其他选择题类型
      editingQuestion.value.options = [
        { optionKey: 'A', optionContent: '', isCorrect: false },
        { optionKey: 'B', optionContent: '', isCorrect: false },
        { optionKey: 'C', optionContent: '', isCorrect: false },
        { optionKey: 'D', optionContent: '', isCorrect: false }
      ]
    }
  } else if (editingQuestion.value.type === 'TRUE_FALSE') {
    // 如果是判断题且已有选项，确保选项内容正确
    if (editingQuestion.value.options.length < 2) {
      editingQuestion.value.options = [
        { optionKey: 'A', optionContent: '正确', isCorrect: editingQuestion.value.options[0]?.isCorrect || false },
        { optionKey: 'B', optionContent: '错误', isCorrect: false }
      ]
    } else {
      editingQuestion.value.options[0].optionKey = 'A'
      editingQuestion.value.options[0].optionContent = '正确'
      editingQuestion.value.options[1].optionKey = 'B'
      editingQuestion.value.options[1].optionContent = '错误'
    }
  }
  
  // 恢复选择题和判断题的正确答案回显
  if (['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE'].includes(editingQuestion.value.type) && editingQuestion.value.options) {
    // 先检查后端是否已经设置了isCorrect字段（在重置之前检查）
    const hasBackendIsCorrect = editingQuestion.value.options.some(option => option.isCorrect === true)
    
    if (!hasBackendIsCorrect) {
      // 如果后端没有设置isCorrect，才需要从answers数组中提取
      // 先重置所有选项的isCorrect状态
      editingQuestion.value.options.forEach(option => {
        option.isCorrect = false
      })
      
      // 从answers数组中提取正确答案
      if (editingQuestion.value.answers && editingQuestion.value.answers.length > 0) {
        // 判断题和选择题统一处理：通过匹配选项键来设置isCorrect
        editingQuestion.value.answers.forEach(answer => {
          if (!answer.answerContent) return
          
          const answerContent = answer.answerContent.trim()
          const answerUpper = answerContent.toUpperCase()
          
          if (editingQuestion.value.type === 'TRUE_FALSE') {
            // 判断题特殊处理：支持多种答案格式
            // 匹配 "A"、"正确"、"TRUE"、"T" 等 -> 选项A（正确）
            if (answerUpper === 'A' || 
                answerContent.includes('正确') || 
                answerUpper === 'TRUE' || 
                answerUpper === 'T' ||
                answerContent === '正确') {
              const option = editingQuestion.value.options.find(opt => opt.optionKey.toUpperCase() === 'A')
              if (option) {
                option.isCorrect = true
              }
            }
            // 匹配 "B"、"错误"、"FALSE"、"F" 等 -> 选项B（错误）
            else if (answerUpper === 'B' || 
                     answerContent.includes('错误') || 
                     answerUpper === 'FALSE' || 
                     answerUpper === 'F' ||
                     answerContent === '错误') {
              const option = editingQuestion.value.options.find(opt => opt.optionKey.toUpperCase() === 'B')
              if (option) {
                option.isCorrect = true
              }
            }
          } else {
            // 单选题和多选题：直接匹配选项键（如 "A", "B", "A,B" 等）
            // 处理多个答案用逗号分隔的情况（如 "A,B"）
            const keys = answerUpper.split(/[,，\s]+/).map(k => k.trim())
            
            keys.forEach(key => {
              const option = editingQuestion.value.options.find(opt => opt.optionKey.toUpperCase() === key)
              if (option) {
                option.isCorrect = true
              }
            })
          }
        })
      }
    }
    // 如果后端已经设置了isCorrect，直接使用，不需要做任何处理
  }
  
  await loadCourseList() // 加载课程列表
  editDialogVisible.value = true
}

const handleView = (row) => {
  // 深拷贝题目数据
  currentQuestion.value = JSON.parse(JSON.stringify(row))
  
  // 转换选项格式：从数据库格式（key/content/correct）转换为前端格式（optionKey/optionContent/isCorrect）
  if (currentQuestion.value.options && currentQuestion.value.options.length > 0) {
    currentQuestion.value.options = currentQuestion.value.options.map(opt => {
      // 如果已经是前端格式（有optionKey），直接使用，但需要处理 isCorrect 的值
      if (opt.optionKey !== undefined) {
        return {
          ...opt,
          isCorrect: opt.isCorrect === 1 || opt.isCorrect === true || opt.isCorrect === '1'
        }
      }
      // 如果是数据库格式（有key），转换为前端格式
      return {
        optionKey: opt.key || '',
        optionContent: opt.content || '',
        isCorrect: opt.correct === 1 || opt.correct === true || opt.correct === '1',
        id: opt.id
      }
    })
  }
  
  detailDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这道题目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await deleteQuestion(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadQuestionList()
      loadStatistics()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete question error:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请选择要删除的题目')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedQuestions.value.length} 道题目吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const ids = selectedQuestions.value.map(item => item.id)
    const response = await batchDeleteQuestions(ids)
    // response.data 直接就是操作结果
    ElMessage.success('批量删除成功')
    loadQuestionList()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Batch delete questions error:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

const handleSelectionChange = (selection) => {
  selectedQuestions.value = selection
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadQuestionList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadQuestionList()
}

const getTagList = (tags) => {
  if (!tags) return []
  return tags.split(',').filter(tag => tag.trim())
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

const formatContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
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

// 生命周期
onMounted(async () => {
  await loadStatistics()
  await loadCourseList()
  
  if (route.query.courseId) {
    // 统一转为数字类型（关键修改）
    searchForm.courseId = Number(route.query.courseId)
    
    if (route.query.courseName) {
      currentCourseName.value = route.query.courseName
    } else if (courseList.value.length > 0) {
      // 直接用数字比较（因searchForm.courseId已转为数字）
      const selectedCourse = courseList.value.find(course => course.id === searchForm.courseId)
      if (selectedCourse) {
        currentCourseName.value = selectedCourse.courseName || selectedCourse.name || ''
      }
    } else {
      currentCourseName.value = ''
    }
    
    pagination.page = 1
  }
  
  loadQuestionList()
})
</script>

<style scoped>
.question-bank-container {
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
  color: #606266;
  font-size: 14px;
}

.stats-container {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-row:last-child {
  margin-bottom: 0;
}

.stat-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.stat-card-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card-primary .stat-number {
  color: #ffffff;
  font-size: 32px;
}

.stat-card-primary .stat-label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  font-weight: 500;
}

.stat-content {
  text-align: center;
  padding: 20px 10px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.search-card, .action-card, .table-card {
  margin-bottom: 20px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.search-form {
  margin-bottom: 0;
}

.search-form .el-form-item {
  margin-bottom: 0;
  margin-right: 20px;
}

.search-form .el-form-item:last-child {
  margin-right: 0;
}

.search-form .el-form-item__label {
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}

/* 响应式样式 */
@media (max-width: 1200px) {
  .search-form .el-form-item {
    margin-right: 15px;
  }
  
  .search-form .el-select {
    width: 120px !important;
  }
  
  .search-form .el-input {
    width: 180px !important;
  }
}

@media (max-width: 768px) {
  .search-form {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .action-bar {
    flex-direction: column;
    gap: 10px;
  }
  
  .action-left, .action-right {
    width: 100%;
    justify-content: center;
  }
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-left, .action-right {
  display: flex;
  gap: 10px;
}

.tag-item {
  margin-right: 5px;
  margin-bottom: 5px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.question-detail {
  max-height: 600px;
  overflow-y: auto;
}

.question-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e6e6e6;
}

.question-header h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.points {
  color: #606266;
  font-size: 14px;
}

  .stats-header {
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 15px;
  }
  .stats-content {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }
  .total-stats {
    text-align: center;
    padding: 10px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 8px;
    margin-bottom: 5px;
  }
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 10px;
  }
  .stat-item {
    text-align: center;
    padding: 10px;
    background-color: #f5f7fa;
    border-radius: 6px;
    transition: all 0.3s ease;
  }
  .stat-item:hover {
    background-color: #e4e7ed;
    transform: translateY(-2px);
  }
  .stat-number {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 4px;
  }
  .stat-number.large {
    font-size: 24px;
  }
  .stat-label {
    font-size: 12px;
    color: #606266;
  }
  .total-stats .stat-label {
    color: rgba(255, 255, 255, 0.9);
  }

.question-content, .question-options, .question-answers, .question-explanation {
  margin-bottom: 20px;
}

.question-content h4, .question-options h4, .question-answers h4, .question-explanation h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.content-text, .explanation-text {
  line-height: 1.6;
  color: #606266;
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
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.correct-option {
  background-color: #f0f9ff;
  border: 1px solid #409EFF;
}

.option-key {
  font-weight: bold;
  color: #409EFF;
  min-width: 20px;
}

.option-content, .answer-content {
  flex: 1;
  color: #606266;
}

/* 题目图片样式 */
.question-images {
  margin-bottom: 20px;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  margin-top: 12px;
}

.image-item {
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fafafa;
  transition: all 0.3s;
}

.image-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  font-size: 14px;
}

/* 选项输入组样式 */
.option-input-group {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.option-input-group .el-checkbox {
  font-weight: bold;
}

.option-input-group .el-input {
  flex: 1;
}

/* 编辑对话框样式 */
.el-dialog__body {
  max-height: 600px;
  overflow-y: auto;
  padding-bottom: 0;
}
    .stat-label {
      font-size: 12px;
      color: #606266;
    }
    .total-stats .stat-label {
      color: rgba(255, 255, 255, 0.9);
    }

@media (max-width: 768px) {
  .question-bank-container {
    padding: 10px;
  }
  
  .action-bar {
    flex-direction: column;
    gap: 10px;
  }
  
  .action-left, .action-right {
    width: 100%;
    justify-content: center;
  }
}

/* 统计卡片样式 */
.stats-card {
  border-radius: 8px;
}
.stats-header {
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 15px;
}
.stats-content {
  display: flex;
  flex-direction: column;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 10px;
}
.stat-item {
  text-align: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 6px;
  transition: all 0.3s ease;
}
.stat-item:hover {
  background-color: #e4e7ed;
  transform: translateY(-2px);
}
/* 题目总数特殊样式 */
.stat-item.total-stats {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
.stat-number {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}
.stat-label {
  font-size: 12px;
  color: #606266;
}
.total-stats .stat-label {
  color: rgba(255, 255, 255, 0.9);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
    gap: 8px;
  }
  .stat-item {
    padding: 8px;
  }
  .stat-number {
    font-size: 16px;
  }
  .stat-label {
    font-size: 11px;
  }
}
</style>

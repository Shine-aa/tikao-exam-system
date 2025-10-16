<template>
  <div class="question-bank-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>题库管理</h2>
      <p>管理题目、知识点和标签</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalQuestions || 0 }}</div>
            <div class="stat-label">题目总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.singleChoiceCount || 0 }}</div>
            <div class="stat-label">单选题</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.multipleChoiceCount || 0 }}</div>
            <div class="stat-label">多选题</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.trueFalseCount || 0 }}</div>
            <div class="stat-label">判断题</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.fillBlankCount || 0 }}</div>
            <div class="stat-label">填空题</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.subjectiveCount || 0 }}</div>
            <div class="stat-label">主观题</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

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
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="难度等级" clearable style="width: 120px">
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
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

        <!-- 题目解析 -->
        <div v-if="currentQuestion.explanation" class="question-explanation">
          <h4>解析:</h4>
          <div class="explanation-text" v-html="formatContent(currentQuestion.explanation)"></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Refresh } from '@element-plus/icons-vue'
import { getQuestions, deleteQuestion, batchDeleteQuestions, getQuestionStatistics } from '../../api/admin'

// 响应式数据
const loading = ref(false)
const questionList = ref([])
const selectedQuestions = ref([])
const statistics = ref({})
const detailDialogVisible = ref(false)
const currentQuestion = ref(null)

// 搜索表单
const searchForm = reactive({
  type: '',
  difficulty: '',
  keyword: ''
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 计算属性
const getTypeTagType = (type) => {
  const typeMap = {
    'SINGLE_CHOICE': 'primary',
    'MULTIPLE_CHOICE': 'success',
    'TRUE_FALSE': 'warning',
    'FILL_BLANK': 'info',
    'SUBJECTIVE': 'danger'
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
    'SUBJECTIVE': '主观题'
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

// 方法
const loadQuestionList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
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
      subjectiveCount: 0
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
    keyword: ''
  })
  pagination.page = 1
  loadQuestionList()
}

const handleAdd = () => {
  ElMessage.info('新增题目功能开发中...')
  // TODO: 实现新增题目功能
}

const handleEdit = (row) => {
  ElMessage.info('编辑题目功能开发中...')
  // TODO: 实现编辑题目功能
}

const handleView = (row) => {
  currentQuestion.value = row
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

// 生命周期
onMounted(() => {
  loadQuestionList()
  loadStatistics()
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

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  text-align: center;
  padding: 10px;
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
</style>

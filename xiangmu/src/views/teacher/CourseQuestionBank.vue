<template>
  <div class="course-question-bank-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ courseName ? `课程题库 - ${courseName}` : '课程题库管理' }}</h2>
      <p>管理课程相关题目</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ questionList.length }}</div>
            <div class="stat-label">题目总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ getQuestionTypeCount('SINGLE_CHOICE') }}</div>
            <div class="stat-label">单选题</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ getQuestionTypeCount('MULTIPLE_CHOICE') }}</div>
            <div class="stat-label">多选题</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ getQuestionTypeCount('SUBJECTIVE') }}</div>
            <div class="stat-label">主观题</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <el-card class="action-card">
      <div class="action-bar">
        <div class="action-left">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加题目
          </el-button>
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回课程管理
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
        :data="questionList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="title" label="题目标题" min-width="200" />
        <el-table-column prop="type" label="题型" width="120">
          <template #default="{ row }">
            <el-tag :type="getQuestionTypeTagType(row.type)" size="small">
              {{ getQuestionTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyTagType(row.difficulty)" size="small">
              {{ getDifficultyLabel(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="分值" width="80" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" v-if="questionList.length > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadQuestionList"
          @current-change="loadQuestionList"
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
            <el-tag :type="getQuestionTypeTagType(currentQuestion.type)">
              {{ getQuestionTypeLabel(currentQuestion.type) }}
            </el-tag>
            <el-tag :type="getDifficultyTagType(currentQuestion.difficulty)">
              {{ getDifficultyLabel(currentQuestion.difficulty) }}
            </el-tag>
            <span class="points">分值: {{ currentQuestion.points }}</span>
          </div>
        </div>
        
        <div class="question-content">
          <h4>题目内容:</h4>
          <div class="content-text" v-html="formatQuestionContent(currentQuestion.content)"></div>
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
                lazy
              >
                <template #error>
                  <div class="image-error">
                    <p>图片加载失败</p>
                    <p>路径: {{ imageUrl }}</p>
                  </div>
                </template>
              </el-image>
            </div>
          </div>
        </div>

        <!-- 选择题选项 -->
        <div v-if="currentQuestion.options && Object.keys(currentQuestion.options).length > 0" class="question-options">
          <h4>选项:</h4>
          <div class="options-list">
            <div
              v-for="(value, key) in currentQuestion.options"
              :key="key"
              class="option-item"
            >
              <span class="option-key">{{ key }}.</span>
              <span class="option-content">{{ value }}</span>
            </div>
          </div>
        </div>

        <!-- 题目答案 -->
        <div v-if="currentQuestion.correctAnswer" class="question-answers">
          <h4>正确答案:</h4>
          <div class="answers-list">
            <div class="answer-item">
              <span class="answer-content">{{ currentQuestion.correctAnswer }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, View, ArrowLeft } from '@element-plus/icons-vue'
import { questionApi } from '@/api/admin'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const detailDialogVisible = ref(false)
const questionList = ref([])
const currentQuestion = ref(null)
const courseName = ref('')
const courseId = ref(null)

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 获取课程ID和名称
onMounted(() => {
  const id = route.query.courseId
  const name = route.query.courseName
  
  if (id) {
    courseId.value = id
  }
  if (name) {
    courseName.value = name
  }
  
  loadQuestionList()
})

// 加载题目列表
const loadQuestionList = async () => {
  if (!courseId.value) return
  
  loading.value = true
  try {
    const response = await questionApi.getQuestionsByCourse(courseId.value)
    if (response.code === 200) {
      questionList.value = response.data || []
      pagination.total = questionList.value.length
    } else {
      ElMessage.error(response.message || '加载失败')
    }
  } catch (error) {
    console.error('Load questions error:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 统计
const getQuestionTypeCount = (type) => {
  return questionList.value.filter(q => q.type === type).length
}

// 查看题目详情
const handleView = (row) => {
  currentQuestion.value = row
  detailDialogVisible.value = true
}

// 编辑题目
const handleEdit = (row) => {
  window.location.href = `/teacher/question-bank/${row.id}?courseId=${courseId.value}`
}

// 删除题目
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除题目"${row.title}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await questionApi.deleteQuestion(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadQuestionList()
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

// 添加题目
const handleAdd = () => {
  window.location.href = `/teacher/question-bank/create?courseId=${courseId.value}`
}

// 返回
const goBack = () => {
  router.push('/teacher/course-management')
}

// 获取图片URL列表
const getImageUrls = (images) => {
  if (!images) return []
  
  if (Array.isArray(images)) {
    return images
  }
  
  if (typeof images === 'string') {
    const urls = images.split(/[;,]/).filter(url => url.trim()).map(url => url.trim())
    return urls.map(url => url.startsWith('http') ? url : `http://localhost:8080${url}`)
  }
  
  return []
}

// 格式化题目内容
const formatQuestionContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}

// 题目类型标签
const getQuestionTypeLabel = (type) => {
  const typeMap = {
    'SINGLE_CHOICE': '单选题',
    'MULTIPLE_CHOICE': '多选题',
    'TRUE_FALSE': '判断题',
    'FILL_BLANK': '填空题',
    'SUBJECTIVE': '主观题'
  }
  return typeMap[type] || type
}

const getQuestionTypeTagType = (type) => {
  const typeMap = {
    'SINGLE_CHOICE': 'primary',
    'MULTIPLE_CHOICE': 'success',
    'TRUE_FALSE': 'warning',
    'FILL_BLANK': 'info',
    'SUBJECTIVE': 'danger'
  }
  return typeMap[type] || 'default'
}

// 难度标签
const getDifficultyLabel = (difficulty) => {
  const difficultyMap = {
    'EASY': '简单',
    'MEDIUM': '中等',
    'HARD': '困难'
  }
  return difficultyMap[difficulty] || difficulty
}

const getDifficultyTagType = (difficulty) => {
  const typeMap = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return typeMap[difficulty] || 'info'
}
</script>

<style scoped>
.course-question-bank-container {
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
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.action-card {
  margin-bottom: 20px;
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

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 题目详情样式 */
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

.question-content, .question-options, .question-answers, .question-images {
  margin-bottom: 20px;
}

.question-content h4, .question-options h4, .question-answers h4, .question-images h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.content-text {
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

.option-key {
  font-weight: bold;
  color: #409EFF;
  min-width: 20px;
}

.option-content, .answer-content {
  flex: 1;
  color: #606266;
}

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

@media (max-width: 768px) {
  .course-question-bank-container {
    padding: 10px;
  }
  
  .action-bar {
    flex-direction: column;
    gap: 10px;
  }
  
  .action-left, .action-right {
    width: 100%;
  }
}
</style>


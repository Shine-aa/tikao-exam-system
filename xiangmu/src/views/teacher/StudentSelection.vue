<template>
  <div class="student-selection">
    <div class="page-header">
      <el-button @click="goBack" type="info" plain>
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2>选择学生进行判卷</h2>
      <p>{{ examInfo.examName }} - {{ examInfo.className }}</p>
    </div>

    <!-- 考试信息卡片 -->
    <el-card class="exam-info-card" v-if="examInfo.id">
      <template #header>
        <div class="card-header">
          <span>考试信息</span>
          <el-tag :type="getStatusType(examInfo.status)">
            {{ getStatusLabel(examInfo.status) }}
          </el-tag>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item">
            <span class="label">试卷名称：</span>
            <span class="value">{{ examInfo.paperName }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <span class="label">应考人数：</span>
            <span class="value">{{ examInfo.studentCount }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <span class="label">已交卷：</span>
            <span class="value">{{ submittedCount }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <span class="label">未交卷：</span>
            <span class="value">{{ unsubmittedCount }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="7">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学生姓名、邮箱或班级"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="5">
          <el-select
            v-model="selectedStatus"
            placeholder="交卷状态"
            clearable
            @change="handleStatusFilter"
            style="width: 100%"
          >
            <el-option label="全部" value="" />
            <el-option label="已交卷" value="SUBMITTED" />
            <el-option label="未交卷" value="NOT_SUBMITTED" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select
            v-model="selectedGradingStatus"
            placeholder="判卷状态"
            clearable
            @change="handleGradingFilter"
            style="width: 100%"
          >
            <el-option label="全部" value="" />
            <el-option label="未判卷" value="NOT_GRADED" />
            <el-option label="已判卷" value="GRADED" />
          </el-select>
        </el-col>
        <el-col :span="7">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="loadStudentList" style="margin-left: 10px;">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </el-col>
      </el-row>
    </div>


    <!-- 学生列表 -->
    <div class="student-list">
      <div class="table-container">
        <el-table
          :data="studentList"
          :loading="loading"
          border
          stripe
          style="width: 100%"
        >
        <el-table-column prop="id" label="学生ID" width="100" />
        <el-table-column prop="username" label="学生姓名" width="180" />
        <el-table-column prop="submitTime" label="交卷时间" width="220">
          <template #default="{ row }">
            {{ row.submitTime ? formatDateTime(row.submitTime) : '未交卷' }}
          </template>
        </el-table-column>
        <el-table-column prop="timeSpentMinutes" label="用时(分钟)" width="130" align="center">
          <template #default="{ row }">
            {{ row.timeSpentMinutes || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.totalScore !== null && row.totalScore !== undefined">
              {{ row.totalScore }}
            </span>
            <span v-else class="no-score">未判卷</span>
          </template>
        </el-table-column>
        <el-table-column prop="gradingStatus" label="判卷状态" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="getGradingStatusType(row.gradingStatus)">
              {{ getGradingStatusLabel(row.gradingStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :disabled="!row.submitTime || row.gradingStatus === 'GRADED'"
              @click="handleStartGrading(row)"
            >
              <el-icon><EditPen /></el-icon>
              {{ row.gradingStatus === 'GRADED' ? '已判卷' : '判卷' }}
            </el-button>
          </template>
        </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Search, Refresh, EditPen, Close
} from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { examApi } from '@/api/admin'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const examInfo = ref({})
const studentList = ref([])
const searchKeyword = ref('')
const selectedStatus = ref('')
const selectedGradingStatus = ref('')

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 计算属性
const submittedCount = computed(() => {
  return studentList.value.filter(student => student.submitTime).length
})

const unsubmittedCount = computed(() => {
  return studentList.value.filter(student => !student.submitTime).length
})

// 获取考试ID
const examId = computed(() => {
  return route.params.examId
})

// 加载考试信息
const loadExamInfo = async () => {
  try {
    const response = await examApi.getExamById(examId.value)
    if (response.code === 200) {
      examInfo.value = response.data
    }
  } catch (error) {
    console.error('Load exam info error:', error)
    ElMessage.error('获取考试信息失败')
  }
}

// 加载学生列表
const loadStudentList = async () => {
  try {
    loading.value = true
    const response = await examApi.getExamStudentsForGrading(
      examId.value,
      pagination.page,
      pagination.size,
      searchKeyword.value,
      selectedStatus.value,
      selectedGradingStatus.value
    )
    
    if (response.code === 200) {
      studentList.value = response.data.content || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('Load student list error:', error)
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.page = 1
  loadStudentList()
}

// 状态筛选
const handleStatusFilter = () => {
  pagination.page = 1
  loadStudentList()
}

// 判卷状态筛选
const handleGradingFilter = () => {
  pagination.page = 1
  loadStudentList()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadStudentList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadStudentList()
}


// 开始判卷（单个）
const handleStartGrading = (row) => {
  if (!row.submitTime) {
    ElMessage.warning('该学生尚未交卷')
    return
  }
  
  if (row.gradingStatus === 'GRADED') {
    ElMessage.warning('该学生试卷已判卷，无法再次判卷')
    return
  }
  
  // 跳转到判卷界面
  router.push(`/teacher/score-analysis/${examId.value}/student/${row.id}`)
}




// 返回
const goBack = () => {
  router.push('/teacher/score-analysis')
}

// 状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    'DRAFT': 'info',
    'SCHEDULED': 'warning',
    'ONGOING': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 状态标签文本
const getStatusLabel = (status) => {
  const statusMap = {
    'DRAFT': '草稿',
    'SCHEDULED': '已安排',
    'ONGOING': '进行中',
    'COMPLETED': '已结束',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 判卷状态标签类型
const getGradingStatusType = (status) => {
  const statusMap = {
    'NOT_GRADED': 'warning',
    'GRADED': 'success'
  }
  return statusMap[status] || 'info'
}

// 判卷状态标签文本
const getGradingStatusLabel = (status) => {
  const statusMap = {
    'NOT_GRADED': '未判卷',
    'GRADED': '已判卷'
  }
  return statusMap[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  if (examId.value) {
    loadExamInfo()
    loadStudentList()
  }
})
</script>

<style scoped>
.student-selection {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.exam-info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-item .label {
  font-weight: 500;
  color: #606266;
  margin-right: 8px;
}

.info-item .value {
  color: #303133;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.batch-actions {
  margin-bottom: 20px;
  padding: 15px;
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.student-list {
  margin-bottom: 20px;
}

.table-container {
  width: 100%;
  max-width: 980px;
  margin: 0 auto;
}

.pagination {
  display: flex;
  justify-content: center;
}

.no-score {
  color: #909399;
  font-style: italic;
}

/* 响应式设计 - 仅适配桌面端和平板端 */
@media (max-width: 1200px) {
  .student-selection {
    padding: 15px;
  }
  
  .search-section {
    padding: 15px;
  }
  
  .exam-info-card {
    margin-bottom: 15px;
  }
}
</style>

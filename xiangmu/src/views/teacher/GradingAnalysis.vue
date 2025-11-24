<template>
  <div class="grading-analysis">
    <div class="page-header">
      <h2>判卷管理</h2>
      <p>对已结束的考试进行判卷和成绩分析</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索考试名称"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="5">
          <el-select
            v-model="selectedClass"
            placeholder="选择班级"
            clearable
            @change="handleClassFilter"
            style="width: 100%"
          >
            <el-option
              v-for="clazz in classOptions"
              :key="clazz.id"
              :label="clazz.className"
              :value="clazz.id"
            />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="5">
          <el-select
            v-model="selectedStatus"
            placeholder="考试状态"
            clearable
            @change="handleStatusFilter"
            style="width: 100%"
          >
            <el-option label="全部" value="" />
            <el-option label="已结束" value="COMPLETED" />
            <el-option label="进行中" value="ONGOING" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="24" :md="4" :lg="4">
          <el-button type="primary" @click="loadExamList" style="width: 100%">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 考试列表 -->
    <div class="exam-list">
      <el-table
        :data="examList"
        :loading="loading"
        border
        stripe
        style="width: 100%"
        :scroll-x="true"
      >
        <el-table-column prop="examName" label="考试名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="paperName" label="试卷名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="className" label="班级" width="120" show-overflow-tooltip />
        <el-table-column prop="studentCount" label="应考人数" width="90" align="center" />
        <el-table-column prop="participatedCount" label="实考人数" width="90" align="center" />
        <el-table-column prop="unsubmittedCount" label="未交卷" width="90" align="center">
          <template #default="{ row }">
            <span :class="{ 'unsubmitted-highlight': row.unsubmittedCount > 0 }">
              {{ row.unsubmittedCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="gradedCount" label="已判卷" width="90" align="center">
          <template #default="{ row }">
            <span class="graded-count">{{ row.gradedCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="140" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="80" align="center">
          <template #default="{ row }">
            {{ row.duration }}分钟
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gradingProgress" label="判卷进度" width="100" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="row.gradingProgress"
              :status="row.gradingProgress === 100 ? 'success' : ''"
              :stroke-width="6"
              :show-text="false"
            />
            <div style="font-size: 12px; color: #666; margin-top: 2px;">{{ row.gradingProgress }}%</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="default"
                :disabled="row.status !== 'COMPLETED' || row.gradingProgress === 100"
                @click="handleStartGrading(row)"
              >
                <el-icon><EditPen /></el-icon>
                {{ row.gradingProgress === 100 ? '已判卷' : '开始判卷' }}
              </el-button>
              <el-button
                type="info"
                size="default"
                @click="handleViewDetails(row)"
              >
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button
                type="info"
                size="default"
                :disabled="row.status !== 'COMPLETED' || row.gradingProgress < 100"
                @click="handleViewGradingStatus(row)"
              >
                <el-icon><View /></el-icon>
                批阅情况
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
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

    <!-- 考试详情对话框 -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="考试详情"
      width="60%"
      :close-on-click-modal="false"
    >
      <div v-if="currentExam" class="exam-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="考试名称">{{ currentExam.examName }}</el-descriptions-item>
          <el-descriptions-item label="试卷名称">{{ currentExam.paperName }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ currentExam.className }}</el-descriptions-item>
          <el-descriptions-item label="应考人数">{{ currentExam.studentCount }}</el-descriptions-item>
          <el-descriptions-item label="实考人数">{{ currentExam.participatedCount }}</el-descriptions-item>
          <el-descriptions-item label="未交卷人数">{{ currentExam.unsubmittedCount }}</el-descriptions-item>
          <el-descriptions-item label="已判卷人数">{{ currentExam.gradedCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(currentExam.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(currentExam.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ currentExam.duration }}分钟</el-descriptions-item>
          <el-descriptions-item label="判卷进度">
            <el-progress :percentage="currentExam.gradingProgress" />
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="dialog-actions">
          <el-button type="info" @click="detailsDialogVisible = false">
            <el-icon><Close /></el-icon>
            关闭
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, Refresh, EditPen, View, Close
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { examApi, classApi } from '@/api/admin'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const examList = ref([])
const classOptions = ref([])
const searchKeyword = ref('')
const selectedClass = ref('')
const selectedStatus = ref('')
const detailsDialogVisible = ref(false)
const currentExam = ref(null)

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 加载考试列表
const loadExamList = async () => {
  try {
    loading.value = true
    const response = await examApi.getGradingExams(
      pagination.page,
      pagination.size,
      searchKeyword.value,
      selectedClass.value,
      selectedStatus.value
    )
    
    if (response.code === 200) {
      examList.value = response.data.content || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('Load exam list error:', error)
    ElMessage.error('获取考试列表失败')
  } finally {
    loading.value = false
  }
}

// 加载班级选项
const loadClassOptions = async () => {
  try {
    const response = await classApi.getClasses()
    if (response.code === 200) {
      classOptions.value = response.data || []
    }
  } catch (error) {
    console.error('Load class options error:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.page = 1
  loadExamList()
}

// 班级筛选
const handleClassFilter = () => {
  pagination.page = 1
  loadExamList()
}

// 状态筛选
const handleStatusFilter = () => {
  pagination.page = 1
  loadExamList()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadExamList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadExamList()
}

// 开始判卷
const handleStartGrading = (row) => {
  if (row.status !== 'COMPLETED') {
    ElMessage.warning('只有已结束的考试才能进行判卷')
    return
  }
  
  if (row.gradingProgress === 100) {
    ElMessage.warning('该考试已全部判卷完成，无法再次判卷')
    return
  }
  
  // 跳转到学生选择页面，传递 mode=grading 参数
  router.push(`/teacher/score-analysis/students/${row.id}?mode=grading`)
}

// 查看详情
const handleViewDetails = async (row) => {
  try {
    const response = await examApi.getExamById(row.id)
    if (response.code === 200) {
      currentExam.value = response.data
      detailsDialogVisible.value = true
    }
  } catch (error) {
    console.error('View exam details error:', error)
    ElMessage.error('获取考试详情失败')
  }
}

// 查看批阅情况
const handleViewGradingStatus = (row) => {
  if (row.status !== 'COMPLETED' || row.gradingProgress < 100) {
    ElMessage.warning('只有已结束且判卷进度100%的考试才能查看批阅情况')
    return
  }
  
  // 跳转到学生选择页面，传递 mode=view 参数
  router.push(`/teacher/score-analysis/students/${row.id}?mode=view`)
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

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadExamList()
  loadClassOptions()
})
</script>

<style scoped>
.grading-analysis {
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

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.exam-list {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}

.unsubmitted-highlight {
  color: #f56c6c;
  font-weight: bold;
}

.graded-count {
  color: #67c23a;
  font-weight: bold;
}

.exam-details {
  padding: 10px 0;
}

.dialog-actions {
  margin-top: 20px;
  text-align: center;
}

.dialog-actions .el-button {
  margin: 0 10px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  align-items: center;
}

.action-buttons .el-button {
  flex: 1;
  min-width: 85px;
  height: 32px;
  font-size: 13px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .grading-analysis {
    padding: 15px;
  }
  
  .search-section {
    padding: 15px;
  }
}

@media (max-width: 768px) {
  .grading-analysis {
    padding: 10px;
  }
  
  .search-section {
    padding: 10px;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 8px;
  }
  
  .action-buttons .el-button {
    width: 100%;
    min-width: auto;
    height: 36px;
  }
}

@media (max-width: 480px) {
  .grading-analysis {
    padding: 5px;
  }
  
  .search-section {
    padding: 5px;
  }
  
  .page-header h2 {
    font-size: 18px;
  }
  
  .page-header p {
    font-size: 12px;
  }
}
</style>
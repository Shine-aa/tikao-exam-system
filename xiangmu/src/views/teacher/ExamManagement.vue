<template>
  <div class="exam-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <el-icon class="title-icon"><Document /></el-icon>
            考试管理
          </h1>
          <p class="page-subtitle">管理已创建的考试，监控考试状态和进度</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="loadExamList">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalExams }}</div>
          <div class="stat-label">总考试数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon ongoing">
          <el-icon><VideoPlay /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.ongoingExams }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon completed">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.completedExams }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon scheduled">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.scheduledExams }}</div>
          <div class="stat-label">已安排</div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-section">
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索考试名称、代码或班级"
          style="width: 350px"
          @keyup.enter="handleSearch"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.status"
          placeholder="考试状态"
          style="width: 150px"
          clearable
        >
          <el-option label="全部状态" value="" />
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已安排" value="SCHEDULED" />
          <el-option label="进行中" value="ONGOING" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">
          <el-icon><RefreshLeft /></el-icon>
          重置
        </el-button>
      </div>
    </div>

    <!-- 考试列表 -->
    <div class="exam-list">
      <el-table
        v-loading="loading"
        :data="examList"
        border
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
        table-layout="auto"
        :scroll-x="true"
      >
        <el-table-column prop="examCode" label="考试代码" width="120" show-overflow-tooltip />
        <el-table-column prop="examName" label="考试名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="paperName" label="试卷名称" width="100" show-overflow-tooltip />
        <el-table-column prop="className" label="班级" width="80" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="140" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="140" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="durationMinutes" label="时长" width="60" align="center">
          <template #default="{ row }">
            {{ row.durationMinutes }}分钟
          </template>
        </el-table-column>
        <el-table-column prop="studentCount" label="应考" width="60" align="center" />
        <el-table-column prop="participatedCount" label="实考" width="60" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="120" sortable>
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleViewExam(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button 
                v-if="row.status === 'SCHEDULED' || row.status === 'DRAFT' || row.status === null" 
                type="warning" 
                size="small" 
                @click="handleEditExam(row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-dropdown @command="(command) => handleActionCommand(command, row)" size="small" trigger="click">
                <el-button size="small" type="info">
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item 
                      v-if="row.status === 'SCHEDULED'" 
                      command="start"
                    >
                      <el-icon><VideoPlay /></el-icon>
                      开始考试
                    </el-dropdown-item>
                    <el-dropdown-item 
                      v-if="row.status === 'ONGOING'" 
                      command="end"
                    >
                      <el-icon><VideoPause /></el-icon>
                      结束考试
                    </el-dropdown-item>
                    <el-dropdown-item command="students">
                      <el-icon><User /></el-icon>
                      查看学生
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
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

    <!-- 考试详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="考试详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentExam" class="exam-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="考试代码">{{ currentExam.examCode }}</el-descriptions-item>
          <el-descriptions-item label="考试名称">{{ currentExam.examName }}</el-descriptions-item>
          <el-descriptions-item label="试卷名称">{{ currentExam.paperName }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ currentExam.className }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(currentExam.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(currentExam.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ currentExam.durationMinutes }}分钟</el-descriptions-item>
          <el-descriptions-item label="最大尝试次数">{{ currentExam.maxAttempts }}次</el-descriptions-item>
          <el-descriptions-item label="题目乱序">
            <el-tag :type="currentExam.isRandomOrder ? 'success' : 'info'">
              {{ currentExam.isRandomOrder ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="选项乱序">
            <el-tag :type="currentExam.isRandomOptions ? 'success' : 'info'">
              {{ currentExam.isRandomOptions ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="允许查看答案">
            <el-tag :type="currentExam.allowReview ? 'success' : 'info'">
              {{ currentExam.allowReview ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="考试状态">
            <el-tag :type="getStatusType(currentExam.status)">
              {{ getStatusLabel(currentExam.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="应考人数">{{ currentExam.studentCount }}人</el-descriptions-item>
          <el-descriptions-item label="实考人数">{{ currentExam.participatedCount }}人</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentExam.createdAt) }}</el-descriptions-item>
        </el-descriptions>
        
        <div v-if="currentExam.description" class="exam-description">
          <h4>考试描述</h4>
          <p>{{ currentExam.description }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 编辑考试对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑考试"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="120px"
      >
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="editForm.examName" placeholder="请输入考试名称" />
        </el-form-item>
        
        <el-form-item label="考试描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入考试描述"
          />
        </el-form-item>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="editForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="editForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="考试时长" prop="durationMinutes">
          <el-input-number
            v-model="editForm.durationMinutes"
            :min="1"
            :max="480"
            placeholder="分钟"
            style="width: 100%"
          />
          <span class="form-tip">考试时长（1-480分钟）</span>
        </el-form-item>
        
        <el-form-item label="最大尝试次数" prop="maxAttempts">
          <el-input-number
            v-model="editForm.maxAttempts"
            :min="1"
            :max="10"
            placeholder="次数"
            style="width: 100%"
          />
          <span class="form-tip">学生最多可尝试的次数（1-10次）</span>
        </el-form-item>
        
        <el-form-item label="题目乱序" prop="isRandomOrder">
          <el-switch
            v-model="editForm.isRandomOrder"
            active-text="是"
            inactive-text="否"
          />
          <span class="form-tip">是否随机打乱题目顺序</span>
        </el-form-item>
        
        <el-form-item label="选项乱序" prop="isRandomOptions">
          <el-switch
            v-model="editForm.isRandomOptions"
            active-text="是"
            inactive-text="否"
          />
          <span class="form-tip">是否随机打乱选项顺序</span>
        </el-form-item>
        
        <el-form-item label="允许查看答案" prop="allowReview">
          <el-switch
            v-model="editForm.allowReview"
            active-text="是"
            inactive-text="否"
          />
          <span class="form-tip">考试结束后是否允许学生查看答案</span>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleSaveEdit">
          保存修改
        </el-button>
      </template>
    </el-dialog>

    <!-- 学生列表对话框 -->
    <el-dialog
      v-model="studentsDialogVisible"
      title="参与考试的学生"
      width="70%"
      :close-on-click-modal="false"
    >
      <div v-if="currentExam" class="students-list">
        <el-table :data="studentList" border stripe>
          <el-table-column prop="id" label="学生ID" width="80" />
          <el-table-column prop="realName" label="学生姓名" width="120" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column prop="attemptNumber" label="尝试次数" width="90" align="center" />
          <el-table-column prop="examStatus" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStudentStatusType(row.examStatus)">
                {{ getStudentStatusLabel(row.examStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="160">
            <template #default="{ row }">
              {{ row.startTime ? formatDateTime(row.startTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" width="160">
            <template #default="{ row }">
              {{ row.submitTime ? formatDateTime(row.submitTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="totalScore" label="总分" width="80" align="center">
            <template #default="{ row }">
              {{ row.totalScore || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="timeSpentMinutes" label="用时(分钟)" width="100" align="center">
            <template #default="{ row }">
              {{ row.timeSpentMinutes || '-' }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Refresh, ArrowDown, Delete, VideoPlay, VideoPause, User, 
  Document, Search, RefreshLeft, View, Edit, Check, Clock, MoreFilled 
} from '@element-plus/icons-vue'
import { examApi } from '@/api/admin'

// 响应式数据
const loading = ref(false)
const examList = ref([])
const viewDialogVisible = ref(false)
const studentsDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editLoading = ref(false)
const currentExam = ref(null)
const studentList = ref([])
const editFormRef = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: ''
})

// 编辑表单
const editForm = reactive({
  id: null,
  examName: '',
  description: '',
  startTime: '',
  endTime: '',
  durationMinutes: 60,
  maxAttempts: 1,
  isRandomOrder: false,
  isRandomOptions: false,
  allowReview: false,
  paperId: null,
  classId: null
})

// 编辑表单验证规则
const editRules = {
  examName: [
    { required: true, message: '请输入考试名称', trigger: 'blur' },
    { min: 2, max: 50, message: '考试名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  durationMinutes: [
    { required: true, message: '请输入考试时长', trigger: 'blur' },
    { type: 'number', min: 1, max: 480, message: '考试时长必须在 1-480 分钟之间', trigger: 'blur' }
  ],
  maxAttempts: [
    { required: true, message: '请输入最大尝试次数', trigger: 'blur' },
    { type: 'number', min: 1, max: 10, message: '最大尝试次数必须在 1-10 次之间', trigger: 'blur' }
  ]
}

// 统计数据
const stats = computed(() => {
  const totalExams = examList.value.length
  const ongoingExams = examList.value.filter(exam => exam.status === 'ONGOING').length
  const completedExams = examList.value.filter(exam => exam.status === 'COMPLETED').length
  const scheduledExams = examList.value.filter(exam => exam.status === 'SCHEDULED').length
  
  return {
    totalExams,
    ongoingExams,
    completedExams,
    scheduledExams
  }
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 方法
const loadExamList = async () => {
  try {
    loading.value = true
    const response = await examApi.getExamsWithPagination(
      pagination.page,
      pagination.size,
      searchForm.keyword
    )
    
    if (response.code === 200) {
      examList.value = response.data.content || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取考试列表失败')
      // 如果API失败，添加一些测试数据
      examList.value = [
        {
          id: 1,
          examCode: 'EXAM001',
          examName: '数学期末考试',
          paperName: '高等数学试卷A',
          className: '计算机1班',
          startTime: '2024-12-20T09:00:00',
          endTime: '2024-12-20T11:00:00',
          durationMinutes: 120,
          studentCount: 30,
          participatedCount: 0,
          status: 'SCHEDULED',
          createdAt: '2024-12-15T10:00:00',
          description: '期末考试'
        },
        {
          id: 2,
          examCode: 'EXAM002',
          examName: '英语期中考试',
          paperName: '大学英语试卷B',
          className: '计算机2班',
          startTime: '2024-12-18T14:00:00',
          endTime: '2024-12-18T16:00:00',
          durationMinutes: 120,
          studentCount: 28,
          participatedCount: 5,
          status: 'ONGOING',
          createdAt: '2024-12-10T10:00:00',
          description: '期中考试'
        }
      ]
      pagination.total = 2
    }
  } catch (error) {
    console.error('Load exam list error:', error)
    ElMessage.error('获取考试列表失败')
    // 如果API出错，也添加测试数据
    examList.value = [
      {
        id: 1,
        examCode: 'EXAM001',
        examName: '数学期末考试',
        paperName: '高等数学试卷A',
        className: '计算机1班',
        startTime: '2024-12-20T09:00:00',
        endTime: '2024-12-20T11:00:00',
        durationMinutes: 120,
        studentCount: 30,
        participatedCount: 0,
        status: 'SCHEDULED',
        createdAt: '2024-12-15T10:00:00',
        description: '期末考试'
      }
    ]
    pagination.total = 1
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadExamList()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  pagination.page = 1
  loadExamList()
}

// 编辑考试
const handleEditExam = (row) => {
  // 填充编辑表单
  editForm.id = row.id
  editForm.examName = row.examName
  editForm.description = row.description || ''
  editForm.startTime = row.startTime
  editForm.endTime = row.endTime
  editForm.durationMinutes = row.durationMinutes
  editForm.maxAttempts = row.maxAttempts
  editForm.isRandomOrder = row.isRandomOrder
  editForm.isRandomOptions = row.isRandomOptions
  editForm.allowReview = row.allowReview
  editForm.paperId = row.paperId
  editForm.classId = row.classId
  
  editDialogVisible.value = true
}

// 保存编辑
const handleSaveEdit = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    editLoading.value = true
    
    await examApi.updateExam(editForm.id, editForm)
    
    ElMessage.success('考试信息更新成功')
    editDialogVisible.value = false
    loadExamList()
  } catch (error) {
    if (error !== false) {
      console.error('Update exam error:', error)
      ElMessage.error('更新考试信息失败')
    }
  } finally {
    editLoading.value = false
  }
}

const handleViewExam = async (row) => {
  try {
    const response = await examApi.getExamById(row.id)
    if (response.code === 200) {
      currentExam.value = response.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    console.error('View exam error:', error)
    ElMessage.error('获取考试详情失败')
  }
}

const handleActionCommand = async (command, row) => {
  switch (command) {
    case 'start':
      await handleStartExam(row)
      break
    case 'end':
      await handleEndExam(row)
      break
    case 'students':
      await handleViewStudents(row)
      break
    case 'delete':
      await handleDeleteExam(row)
      break
  }
}

const handleStartExam = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要开始考试"${row.examName}"吗？`,
      '确认开始',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await examApi.startExam(row.id)
    ElMessage.success('考试已开始')
    loadExamList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Start exam error:', error)
      ElMessage.error('开始考试失败')
    }
  }
}

const handleEndExam = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要结束考试"${row.examName}"吗？`,
      '确认结束',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await examApi.endExam(row.id)
    ElMessage.success('考试已结束')
    loadExamList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('End exam error:', error)
      ElMessage.error('结束考试失败')
    }
  }
}

const handleViewStudents = async (row) => {
  try {
    currentExam.value = row
    loading.value = true
    
    // 调用获取学生列表的API
    const response = await examApi.getExamStudents(row.id)
    studentList.value = response.data || []
    studentsDialogVisible.value = true
  } catch (error) {
    console.error('View students error:', error)
    ElMessage.error('获取学生列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const handleDeleteExam = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除考试"${row.examName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await examApi.deleteExam(row.id)
    ElMessage.success('删除成功')
    loadExamList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete exam error:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadExamList()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadExamList()
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const formatDate = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

const getStatusType = (status) => {
  const statusMap = {
    'DRAFT': 'info',
    'SCHEDULED': 'warning',
    'ONGOING': 'success',
    'COMPLETED': 'primary',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusLabel = (status) => {
  const statusMap = {
    'DRAFT': '草稿',
    'SCHEDULED': '已安排',
    'ONGOING': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const getStudentStatusType = (status) => {
  const statusMap = {
    'NOT_STARTED': 'info',
    'IN_PROGRESS': 'warning',
    'SUBMITTED': 'success',
    'GRADED': 'primary'
  }
  return statusMap[status] || 'info'
}

const getStudentStatusLabel = (status) => {
  const statusMap = {
    'NOT_STARTED': '未开始',
    'IN_PROGRESS': '进行中',
    'SUBMITTED': '已提交',
    'GRADED': '已评分'
  }
  return statusMap[status] || status
}

// 生命周期
onMounted(() => {
  loadExamList()
})
</script>

<style scoped>
.exam-management {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: bold;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 36px;
}

.page-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-icon.ongoing {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-icon.scheduled {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 搜索区域 */
.search-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

/* 考试列表 */
.exam-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-radius: 0 0 12px 12px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 4px;
  align-items: center;
  justify-content: flex-start;
  flex-wrap: nowrap;
  min-width: 160px;
}

.action-buttons .el-button {
  margin: 0;
  padding: 4px 8px;
  font-size: 12px;
  min-width: 50px;
  border-radius: 4px;
  flex-shrink: 0;
}

.action-buttons .el-button--small {
  padding: 4px 6px;
  min-width: 32px;
}

/* 对话框样式 */
.exam-detail {
  padding: 20px 0;
}

.exam-description {
  margin-top: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.exam-description h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.exam-description p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.students-list {
  max-height: 500px;
  overflow-y: auto;
}

/* 编辑表单样式 */
.form-tip {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .exam-list {
    overflow-x: auto;
  }
  
  .action-buttons {
    min-width: 140px;
  }
  
  .action-buttons .el-button {
    padding: 3px 6px;
    font-size: 11px;
    min-width: 45px;
  }
}

@media (max-width: 768px) {
  .exam-management {
    padding: 16px;
  }
  
  .page-header {
    padding: 20px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-bar .el-input,
  .search-bar .el-select {
    width: 100% !important;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 2px;
    min-width: 120px;
  }
  
  .action-buttons .el-button {
    width: 100%;
    padding: 2px 4px;
    font-size: 10px;
    min-width: 40px;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}
</style>

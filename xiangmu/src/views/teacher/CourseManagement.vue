<template>
  <div class="course-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>课程管理</h2>
      <p>管理课程信息，创建和编辑课程</p>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入课程名称或代码"
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
            新增课程
          </el-button>
        </div>
        <div class="action-right">
          <el-button @click="loadCourseList">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 课程列表 -->
    <el-card class="table-card">
      <el-table
        :data="courseList"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="description" label="课程描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="semester" label="学期" width="100" />
        <el-table-column prop="academicYear" label="学年" width="100" />
        <el-table-column prop="classCount" label="班级数量" width="100" />
        <el-table-column prop="isActive" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleManageQuestions(row)">
              <el-icon><Collection /></el-icon>
              题库
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程代码" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="form.semester" placeholder="请输入学期" />
        </el-form-item>
        <el-form-item label="学年" prop="academicYear">
          <el-input v-model="form.academicYear" placeholder="请输入学年" />
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-switch v-model="form.isActive" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { courseApi } from '@/api/admin'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const courseList = ref([])
const selectedCourses = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()


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

// 表单数据
const form = reactive({
  id: null,
  courseCode: '',
  courseName: '',
  description: '',
  teacherId: 1, // 当前教师ID
  semester: '',
  academicYear: '',
  isActive: true
})

// 表单验证规则
const formRules = {
  courseCode: [
    { required: true, message: '请输入课程代码', trigger: 'blur' },
    { max: 50, message: '课程代码长度不能超过50个字符', trigger: 'blur' }
  ],
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { max: 100, message: '课程名称长度不能超过100个字符', trigger: 'blur' }
  ],
  semester: [
    { max: 20, message: '学期长度不能超过20个字符', trigger: 'blur' }
  ],
  academicYear: [
    { max: 10, message: '学年长度不能超过10个字符', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑课程' : '新增课程')

// 方法
const loadCourseList = async () => {
  try {
    loading.value = true
    const response = await courseApi.getCoursesWithPagination(
      pagination.page,
      pagination.size,
      searchForm.keyword
    )
    
    if (response.code === 200) {
      courseList.value = response.data.content || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('Load course list error:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadCourseList()
}

const handleReset = () => {
  searchForm.keyword = ''
  pagination.page = 1
  loadCourseList()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    courseCode: row.courseCode,
    courseName: row.courseName,
    description: row.description || '',
    teacherId: row.teacherId,
    semester: row.semester || '',
    academicYear: row.academicYear || '',
    isActive: row.isActive
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${row.courseName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await courseApi.deleteCourse(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadCourseList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete course error:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const response = isEdit.value
      ? await courseApi.updateCourse(form.id, form)
      : await courseApi.createCourse(form)
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadCourseList()
    } else {
      ElMessage.error(response.message || (isEdit.value ? '更新失败' : '创建失败'))
    }
  } catch (error) {
    console.error('Submit course error:', error)
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  resetForm()
  formRef.value?.resetFields()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    courseCode: '',
    courseName: '',
    description: '',
    teacherId: 1,
    semester: '',
    academicYear: '',
    isActive: true
  })
}

const handleSelectionChange = (selection) => {
  selectedCourses.value = selection
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadCourseList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadCourseList()
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadCourseList()
})

// 题目管理相关方法
const handleManageQuestions = (row) => {
  // 跳转到课程题库管理页面
  router.push({
    name: 'CourseQuestionBank',
    query: {
      courseId: row.id,
      courseName: row.courseName
    }
  })
}

</script>

<style scoped>
.course-management-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.search-card,
.action-card,
.table-card {
  margin-bottom: 20px;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-left,
.action-right {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-management-container {
    padding: 10px;
  }
  
  .action-bar {
    flex-direction: column;
    gap: 10px;
  }
  
  .action-left,
  .action-right {
    width: 100%;
    justify-content: center;
  }
  
  .search-form .el-form-item {
    margin-bottom: 10px;
  }
}

.question-stats {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.question-stats .el-tag {
  font-size: 14px;
  padding: 5px 10px;
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

.question-content, .question-options, .question-answers, .question-explanation, .question-images {
  margin-bottom: 20px;
}

.question-content h4, .question-options h4, .question-answers h4, .question-explanation h4, .question-images h4 {
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
</style>

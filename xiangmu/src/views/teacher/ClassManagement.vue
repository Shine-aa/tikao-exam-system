<template>
  <div class="class-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>班级管理</h2>
      <p>管理班级信息，按专业和年级组织班级</p>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入班级名称或代码"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="专业">
          <el-select
            v-model="searchForm.majorId"
            placeholder="选择专业"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="major in majorOptions"
              :key="major.id"
              :label="major.majorName"
              :value="major.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-select
            v-model="searchForm.grade"
            placeholder="选择年级"
            clearable
            style="width: 150px"
          >
            <el-option label="2022级" value="2022" />
            <el-option label="2023级" value="2023" />
            <el-option label="2024级" value="2024" />
          </el-select>
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
            新增班级
          </el-button>
          <el-button 
            type="danger" 
            :disabled="selectedClasses.length === 0"
            @click="handleBatchDelete"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
        <div class="action-right">
          <el-button @click="loadClassList">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 班级列表 -->
    <el-card class="table-card">
      <el-table
        :data="classList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="classCode" label="班级代码" width="120" />
        <el-table-column prop="className" label="班级名称" min-width="150" />
        <el-table-column prop="grade" label="年级" width="80" />
        <el-table-column prop="majorName" label="专业" min-width="120" />
        <el-table-column prop="classNumber" label="班级序号" width="100" />
        <el-table-column prop="currentStudents" label="当前人数" width="100" />
        <el-table-column prop="maxStudents" label="最大人数" width="100" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="success" size="small" @click="handleManageStudents(row)">
                学生管理
              </el-button>
              <el-button type="warning" size="small" @click="handleManageCourses(row)">
                课程管理
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                删除
              </el-button>
            </div>
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
          @size-change="loadClassList"
          @current-change="loadClassList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="班级代码" prop="classCode">
          <el-input v-model="form.classCode" placeholder="请输入班级代码" />
        </el-form-item>
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
            <el-option label="2022级" value="2022" />
            <el-option label="2023级" value="2023" />
            <el-option label="2024级" value="2024" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="majorId">
          <el-select v-model="form.majorId" placeholder="请选择专业" style="width: 100%">
            <el-option
              v-for="major in majorOptions"
              :key="major.id"
              :label="major.majorName"
              :value="major.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级序号" prop="classNumber">
          <el-input-number v-model="form.classNumber" :min="1" :max="99" style="width: 100%" />
        </el-form-item>
        <el-form-item label="班主任" prop="teacherId">
          <el-select v-model="form.teacherId" placeholder="请选择班主任" style="width: 100%">
            <el-option
              v-for="teacher in teacherOptions"
              :key="teacher.id"
              :label="teacher.username"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="form.maxStudents" :min="1" :max="200" style="width: 100%" />
        </el-form-item>
        <el-form-item label="班级描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入班级描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 学生管理对话框 -->
    <el-dialog
      v-model="studentDialogVisible"
      title="学生管理"
      width="800px"
    >
      <div class="student-management">
        <div class="student-header">
          <h3>{{ currentClass?.className }} - 学生管理</h3>
          <el-button type="primary" @click="handleAddStudent">
            <el-icon><Plus /></el-icon>
            添加学生
          </el-button>
        </div>
        
        <el-table :data="classStudents" v-loading="studentLoading" stripe>
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="email" label="邮箱" min-width="150" />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column prop="createdAt" label="加入时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="handleRemoveStudent(row)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 添加学生对话框 -->
    <el-dialog
      v-model="addStudentDialogVisible"
      title="添加学生"
      width="600px"
    >
      <el-form :model="addStudentForm" label-width="100px">
        <el-form-item label="选择学生">
          <el-select
            v-model="addStudentForm.studentId"
            placeholder="请选择要添加的学生"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="student in availableStudents"
              :key="student.id"
              :label="`${student.username} (${student.email})`"
              :value="student.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addStudentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAddStudent">确定</el-button>
      </template>
    </el-dialog>

    <!-- 课程管理对话框 -->
    <el-dialog
      v-model="courseDialogVisible"
      title="课程管理"
      width="900px"
    >
      <div class="course-management">
        <div class="course-header">
          <h3>{{ currentClass?.className }} - 课程管理</h3>
          <el-button type="primary" @click="handleAddCourse">
            <el-icon><Plus /></el-icon>
            添加课程
          </el-button>
        </div>
        
        <el-table :data="classCourses" v-loading="courseLoading" stripe>
          <el-table-column prop="courseName" label="课程名称" width="200" />
          <el-table-column prop="courseCode" label="课程代码" width="120" />
          <el-table-column prop="credits" label="学分" width="80" />
          <el-table-column prop="semester" label="学期" width="100" />
          <el-table-column prop="academicYear" label="学年" width="120" />
          <el-table-column prop="createdAt" label="添加时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="handleRemoveCourse(row)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 添加课程对话框 -->
    <el-dialog
      v-model="addCourseDialogVisible"
      title="添加课程"
      width="600px"
    >
      <el-form :model="addCourseForm" label-width="100px">
        <el-form-item label="选择课程">
          <el-select
            v-model="addCourseForm.courseId"
            placeholder="请选择要添加的课程"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="course in availableCourses"
              :key="course.courseId"
              :label="`${course.courseName} (${course.courseCode})`"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="addCourseForm.semester" placeholder="请选择学期" style="width: 100%">
            <el-option label="春季学期" value="春季" />
            <el-option label="秋季学期" value="秋季" />
            <el-option label="夏季学期" value="夏季" />
            <el-option label="冬季学期" value="冬季" />
          </el-select>
        </el-form-item>
        <el-form-item label="学年">
          <el-input v-model="addCourseForm.academicYear" placeholder="请输入学年，如：2024-2025" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addCourseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAddCourse">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Refresh } from '@element-plus/icons-vue'
import { classApi, majorApi, userApi, classCourseApi } from '@/api/admin'

// 响应式数据
const loading = ref(false)
const studentLoading = ref(false)
const courseLoading = ref(false)
const submitting = ref(false)
const classList = ref([])
const selectedClasses = ref([])
const majorOptions = ref([])
const teacherOptions = ref([])
const classStudents = ref([])
const availableStudents = ref([])
const classCourses = ref([])
const availableCourses = ref([])
const currentClass = ref(null)
const formRef = ref(null)

// 对话框状态
const dialogVisible = ref(false)
const studentDialogVisible = ref(false)
const addStudentDialogVisible = ref(false)
const courseDialogVisible = ref(false)
const addCourseDialogVisible = ref(false)
const dialogTitle = ref('')

// 搜索表单
const searchForm = reactive({
  keyword: '',
  majorId: null,
  grade: null
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
  classCode: '',
  className: '',
  grade: '',
  majorId: null,
  classNumber: 1,
  teacherId: null,
  maxStudents: 50,
  description: ''
})

// 添加学生表单
const addStudentForm = reactive({
  studentId: null
})

// 添加课程表单
const addCourseForm = reactive({
  courseId: null,
  semester: '',
  academicYear: ''
})

// 表单验证规则
const formRules = {
  classCode: [
    { required: true, message: '请输入班级代码', trigger: 'blur' },
    { max: 50, message: '班级代码长度不能超过50个字符', trigger: 'blur' }
  ],
  className: [
    { required: true, message: '请输入班级名称', trigger: 'blur' },
    { max: 100, message: '班级名称长度不能超过100个字符', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请选择年级', trigger: 'change' }
  ],
  majorId: [
    { required: true, message: '请选择专业', trigger: 'change' }
  ],
  classNumber: [
    { required: true, message: '请输入班级序号', trigger: 'blur' }
  ],
  teacherId: [
    { required: true, message: '请选择班主任', trigger: 'change' }
  ],
  maxStudents: [
    { required: true, message: '请输入最大人数', trigger: 'blur' }
  ]
}

// 加载班级列表
const loadClassList = async () => {
  try {
    loading.value = true
    const response = await classApi.getClassesWithPagination(
      pagination.page,
      pagination.size,
      searchForm.keyword,
      searchForm.majorId,
      searchForm.grade
    )
    
    // response.data 直接就是 PageResponse 对象
    classList.value = response.data.content || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('Load class list error:', error)
    ElMessage.error('获取班级列表失败')
  } finally {
    loading.value = false
  }
}

// 加载专业选项
const loadMajorOptions = async () => {
  try {
    const response = await majorApi.getMajorList()
    // response.data 直接就是专业列表数组
    majorOptions.value = response.data || []
  } catch (error) {
    console.error('Load major options error:', error)
  }
}

// 加载教师选项
const loadTeacherOptions = async () => {
  try {
    const response = await userApi.getUserList(null, null, null, null, 'TEACHER')
    // response.data 直接就是用户列表数组
    teacherOptions.value = response.data || []
  } catch (error) {
    console.error('Load teacher options error:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadClassList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    majorId: null,
    grade: null
  })
  pagination.page = 1
  loadClassList()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedClasses.value = selection
}

// 新增班级
const handleAdd = () => {
  dialogTitle.value = '新增班级'
  resetForm()
  dialogVisible.value = true
}

// 编辑班级
const handleEdit = (row) => {
  dialogTitle.value = '编辑班级'
  Object.assign(form, {
    id: row.id,
    classCode: row.classCode,
    className: row.className,
    grade: row.grade,
    majorId: row.majorId,
    classNumber: row.classNumber,
    teacherId: row.teacherId,
    maxStudents: row.maxStudents,
    description: row.description
  })
  dialogVisible.value = true
}

// 删除班级
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除班级"${row.className}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await classApi.deleteClass(row.id)
    // response.data 直接就是操作结果
    ElMessage.success('删除成功')
    loadClassList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete class error:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedClasses.value.length} 个班级吗？`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedClasses.value.map(item => item.id)
    const response = await classApi.batchDeleteClasses(ids)
    // response.data 直接就是操作结果
    ElMessage.success('批量删除成功')
    loadClassList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Batch delete classes error:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 管理学生
const handleManageStudents = async (row) => {
  currentClass.value = row
  studentDialogVisible.value = true
  await loadClassStudents(row.id)
}

// 管理课程
const handleManageCourses = async (row) => {
  currentClass.value = row
  courseDialogVisible.value = true
  await loadClassCourses(row.id)
}

// 加载班级学生
const loadClassStudents = async (classId) => {
  try {
    studentLoading.value = true
    // 使用用户管理API获取指定班级的学生
    const response = await userApi.getStudentsWithPagination(
      1, // 页码
      1000, // 每页数量，获取所有学生
      '', // 关键词
      classId, // 班级ID
      'assigned' // 只获取已分配班级的学生
    )
    
    if (response.code === 200) {
      classStudents.value = response.data.content || []
    }
  } catch (error) {
    console.error('Load class students error:', error)
    ElMessage.error('获取班级学生失败')
  } finally {
    studentLoading.value = false
  }
}

// 加载班级课程
const loadClassCourses = async (classId) => {
  try {
    courseLoading.value = true
    const response = await classCourseApi.getClassCoursesByClassId(classId)
    if (response.code === 200) {
      classCourses.value = response.data || []
    }
  } catch (error) {
    console.error('Load class courses error:', error)
    ElMessage.error('获取班级课程失败')
  } finally {
    courseLoading.value = false
  }
}

// 添加学生
const handleAddStudent = async () => {
  try {
    // 获取未分配班级的学生
    const response = await userApi.getStudentsWithPagination(
      1, // 页码
      1000, // 每页数量
      '', // 关键词
      null, // 班级ID为null，获取未分配的学生
      'unassigned' // 只获取未分配班级的学生
    )
    
    if (response.code === 200) {
      availableStudents.value = response.data.content || []
      addStudentDialogVisible.value = true
    }
  } catch (error) {
    console.error('Load available students error:', error)
    ElMessage.error('获取可用学生失败')
  }
}

// 确认添加学生
const handleConfirmAddStudent = async () => {
  if (!addStudentForm.studentId) {
    ElMessage.warning('请选择学生')
    return
  }
  
  try {
    // 使用用户管理API分配学生到班级
    await userApi.assignStudentToClass(Number(addStudentForm.studentId), Number(currentClass.value.id))
    ElMessage.success('添加学生成功')
    addStudentDialogVisible.value = false
    addStudentForm.studentId = null
    loadClassStudents(currentClass.value.id)
  } catch (error) {
    console.error('Add student to class error:', error)
    ElMessage.error('添加学生失败')
  }
}

// 移除学生
const handleRemoveStudent = async (student) => {
  try {
    await ElMessageBox.confirm(
      `确定要从班级中移除学生"${student.username}"吗？`,
      '确认移除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 使用用户管理API移除学生
    await userApi.removeStudentFromClass(Number(student.id))
    ElMessage.success('移除学生成功')
    loadClassStudents(currentClass.value.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Remove student from class error:', error)
      ElMessage.error('移除学生失败')
    }
  }
}

// 添加课程
const handleAddCourse = async () => {
  try {
    // 获取所有课程
    const response = await classCourseApi.getAllCourses()
    if (response.code === 200) {
      availableCourses.value = response.data || []
      addCourseDialogVisible.value = true
    }
  } catch (error) {
    console.error('Load available courses error:', error)
    ElMessage.error('获取可用课程失败')
  }
}

// 确认添加课程
const handleConfirmAddCourse = async () => {
  if (!addCourseForm.courseId) {
    ElMessage.warning('请选择课程')
    return
  }
  
  if (!addCourseForm.semester) {
    ElMessage.warning('请选择学期')
    return
  }
  
  if (!addCourseForm.academicYear) {
    ElMessage.warning('请输入学年')
    return
  }
  
  try {
    // 创建班级课程关联
    await classCourseApi.createClassCourse({
      classId: currentClass.value.id,
      courseId: addCourseForm.courseId,
      semester: addCourseForm.semester,
      academicYear: addCourseForm.academicYear
    })
    
    ElMessage.success('添加课程成功')
    addCourseDialogVisible.value = false
    addCourseForm.courseId = null
    addCourseForm.semester = ''
    addCourseForm.academicYear = ''
    loadClassCourses(currentClass.value.id)
  } catch (error) {
    console.error('Add course to class error:', error)
    ElMessage.error('添加课程失败')
  }
}

// 移除课程
const handleRemoveCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要从班级中移除课程"${course.courseName}"吗？`,
      '确认移除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 移除班级课程关联
    await classCourseApi.deleteClassCourse(course.id)
    ElMessage.success('移除课程成功')
    loadClassCourses(currentClass.value.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Remove course from class error:', error)
      ElMessage.error('移除课程失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    let response
    if (form.id) {
      // 更新
      response = await classApi.updateClass(form.id, form)
    } else {
      // 新增
      response = await classApi.createClass(form)
    }
    
    // response.data 直接就是操作结果
    ElMessage.success(form.id ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadClassList()
  } catch (error) {
    console.error('Submit form error:', error)
    ElMessage.error(form.id ? '更新失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    classCode: '',
    className: '',
    grade: '',
    majorId: null,
    classNumber: 1,
    teacherId: null,
    maxStudents: 50,
    description: ''
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 组件挂载时加载数据
onMounted(() => {
  loadClassList()
  loadMajorOptions()
  loadTeacherOptions()
})
</script>

<style scoped>
.class-management-container {
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
  gap: 12px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.student-management {
  padding: 20px 0;
}

.student-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.student-header h3 {
  margin: 0;
  color: #303133;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: nowrap;
  align-items: center;
}

.action-buttons .el-button {
  margin: 0;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .search-form .el-form {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .action-left,
  .action-right {
    justify-content: center;
  }
}
</style>
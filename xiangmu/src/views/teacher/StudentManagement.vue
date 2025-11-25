<template>
  <div class="student-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入用户名或邮箱"
          style="width: 300px; margin-right: 10px;"
          clearable
          @keyup.enter="loadStudentList"
        />
        <el-select
          v-model="searchForm.classId"
          placeholder="选择班级"
          style="width: 200px; margin-right: 10px;"
          clearable
        >
          <el-option
            v-for="classItem in classOptions"
            :key="classItem.id"
            :label="`${classItem.className} (${classItem.majorName})`"
            :value="classItem.id"
          />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          style="width: 120px; margin-right: 10px;"
          clearable
        >
          <el-option label="已分配" value="assigned" />
          <el-option label="未分配" value="unassigned" />
        </el-select>
        <el-button type="primary" @click="loadStudentList">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
      
      <!-- 批量操作栏 -->
      <div class="batch-actions" v-if="selectedStudents.length > 0">
        <el-button type="primary" @click="handleBatchAssignToClass">
          <el-icon><Plus /></el-icon>
          批量分配到班级 ({{ selectedStudents.length }})
        </el-button>
        <el-button type="danger" @click="handleBatchRemoveFromClass">
          <el-icon><Delete /></el-icon>
          批量移出班级 ({{ selectedStudents.length }})
        </el-button>
      </div>
      
      <!-- 学生表格 -->
      <el-table 
        :data="studentList" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="className" label="所属班级" width="150">
          <template #default="{ row }">
            <el-tag v-if="row.className" type="success">{{ row.className }}</el-tag>
            <el-tag v-else type="info">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="majorName" label="专业" width="120" />
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button 
                v-if="!row.className" 
                type="primary" 
                size="small" 
                @click="handleAssignToClass({...row})"
              >
                分配班级
              </el-button>
              <el-button 
                v-else 
                type="warning" 
                size="small" 
                @click="handleChangeClass({...row})"
              >
                更换班级
              </el-button>
              <el-button 
                v-if="row.className" 
                type="danger" 
                size="small" 
                @click="handleRemoveFromClass({...row})"
              >
                移出班级
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
          @size-change="loadStudentList"
          @current-change="loadStudentList"
        />
      </div>
    </el-card>

    <!-- 分配班级对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      :title="assignDialogTitle"
      width="500px"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="选择班级" required>
          <el-select
            v-model="assignForm.classId"
            placeholder="请选择班级"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="classItem in classOptions"
              :key="classItem.id"
              :label="`${classItem.className} (${classItem.majorName})`"
              :value="classItem.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="assignForm.studentIds.length > 1" label="学生数量">
          <span>{{ assignForm.studentIds.length }} 名学生</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAssign" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Delete } from '@element-plus/icons-vue'
import { userApi, classApi } from '@/api/admin'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const studentList = ref([])
const selectedStudents = ref([])
const classOptions = ref([])
const assignDialogVisible = ref(false)
const assignDialogTitle = ref('')

// 搜索表单
const searchForm = reactive({
  keyword: '',
  classId: null,
  status: null
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 分配表单
const assignForm = reactive({
  classId: null,
  studentIds: []
})

// 页面加载时获取数据
onMounted(() => {
  loadStudentList()
  loadClassOptions()
})

// 加载学生列表
const loadStudentList = async () => {
  try {
    loading.value = true
    const response = await userApi.getStudentsWithPagination(
      pagination.page,
      pagination.size,
      searchForm.keyword,
      searchForm.classId,
      searchForm.status
    )
    
    studentList.value = response.data.content || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('Load student list error:', error)
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 加载班级选项
const loadClassOptions = async () => {
  try {
    const response = await classApi.getClassesWithPagination(1, 1000)
    classOptions.value = response.data.content || []
  } catch (error) {
    console.error('Load class options error:', error)
    ElMessage.error('获取班级列表失败')
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.classId = null
  searchForm.status = null
  loadStudentList()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedStudents.value = selection
}

// 分配班级
const handleAssignToClass = (student = null) => {
  if (student) {
    assignForm.studentIds = [student.id]
    assignDialogTitle.value = `分配学生 ${student.username} 到班级`
  } else {
    assignForm.studentIds = selectedStudents.value.map(s => s.id)
    assignDialogTitle.value = `批量分配 ${assignForm.studentIds.length} 名学生到班级`
  }
  
  assignForm.classId = null
  assignDialogVisible.value = true
}

// 批量分配班级
const handleBatchAssignToClass = () => {
  if (selectedStudents.value.length === 0) {
    ElMessage.warning('请选择要分配的学生')
    return
  }
  handleAssignToClass()
}

// 确认分配
const handleConfirmAssign = async () => {
  if (!assignForm.classId) {
    ElMessage.warning('请选择班级')
    return
  }
  
  if (!assignForm.studentIds || assignForm.studentIds.length === 0) {
    ElMessage.warning('没有选择学生')
    return
  }
  
  try {
    submitting.value = true
    
    // 确保类型转换正确
    const studentIds = assignForm.studentIds.map(id => Number(id))
    const classId = Number(assignForm.classId)
    
    if (assignForm.studentIds.length === 1) {
      // 单个分配
      await userApi.assignStudentToClass(studentIds[0], classId)
    } else {
      // 批量分配
      await userApi.batchAssignStudentsToClass(studentIds, classId)
    }
    
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    loadStudentList()
  } catch (error) {
    console.error('Assign student to class error:', error)
    ElMessage.error('分配失败')
  } finally {
    submitting.value = false
  }
}

// 更换班级
const handleChangeClass = (student) => {
  if (!student.id) {
    ElMessage.error('学生ID不存在')
    return
  }
  
  assignForm.studentIds = [student.id]
  assignDialogTitle.value = `更换学生 ${student.username} 的班级`
  assignForm.classId = null
  assignDialogVisible.value = true
}

// 移出班级
const handleRemoveFromClass = async (student) => {
  try {
    await ElMessageBox.confirm(
      `确定要将学生 ${student.username} 移出班级吗？`,
      '确认移出',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await userApi.removeStudentFromClass(Number(student.id))
    ElMessage.success('移出成功')
    loadStudentList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Remove student from class error:', error)
      ElMessage.error('移出失败')
    }
  }
}

// 批量移出班级
const handleBatchRemoveFromClass = async () => {
  if (selectedStudents.value.length === 0) {
    ElMessage.warning('请选择要移出的学生')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要将选中的 ${selectedStudents.value.length} 名学生移出班级吗？`,
      '确认批量移出',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const studentIds = selectedStudents.value.map(s => Number(s.id))
    await userApi.batchRemoveStudentsFromClass(studentIds)
    ElMessage.success('批量移出成功')
    loadStudentList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Batch remove students from class error:', error)
      ElMessage.error('批量移出失败')
    }
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}
</script>

<style scoped>
.student-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.batch-actions {
  margin-bottom: 20px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  gap: 10px;
}

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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-bar .el-input,
  .search-bar .el-select {
    width: 100% !important;
    margin-right: 0 !important;
    margin-bottom: 10px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
}
</style>

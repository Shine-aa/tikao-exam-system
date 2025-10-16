<template>
  <div class="major-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>专业管理</h2>
      <p>管理学校专业信息，包括专业代码、名称和描述</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <div class="search-form">
        <el-form :inline="true">
          <el-form-item label="关键词">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入专业名称或代码"
              clearable
              style="width: 200px"
              @keyup.enter="loadMajorList"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadMajorList">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增专业
        </el-button>
        <el-button 
          type="danger" 
          :disabled="selectedMajors.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 专业列表 -->
    <div class="table-container">
      <el-table
        :data="majorList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="majorCode" label="专业代码" width="120" />
        <el-table-column prop="majorName" label="专业名称" min-width="150" />
        <el-table-column prop="description" label="专业描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180">
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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadMajorList"
        @current-change="loadMajorList"
      />
    </div>

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
        <el-form-item label="专业代码" prop="majorCode">
          <el-input v-model="form.majorCode" placeholder="请输入专业代码" />
        </el-form-item>
        <el-form-item label="专业名称" prop="majorName">
          <el-input v-model="form.majorName" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="专业描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入专业描述"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { majorApi } from '@/api/admin'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const majorList = ref([])
const selectedMajors = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
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
  majorCode: '',
  majorName: '',
  description: ''
})

// 表单验证规则
const formRules = {
  majorCode: [
    { required: true, message: '请输入专业代码', trigger: 'blur' },
    { max: 20, message: '专业代码长度不能超过20个字符', trigger: 'blur' }
  ],
  majorName: [
    { required: true, message: '请输入专业名称', trigger: 'blur' },
    { max: 100, message: '专业名称长度不能超过100个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '专业描述长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 加载专业列表
const loadMajorList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword
    }
    const response = await majorApi.getMajorsWithPagination(params)
    
    // response.data 直接就是 PageResponse 对象
    majorList.value = response.data.content || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('Load major list error:', error)
    ElMessage.error('获取专业列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  pagination.page = 1
  loadMajorList()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedMajors.value = selection
}

// 新增专业
const handleAdd = () => {
  dialogTitle.value = '新增专业'
  resetForm()
  dialogVisible.value = true
}

// 编辑专业
const handleEdit = (row) => {
  dialogTitle.value = '编辑专业'
  Object.assign(form, {
    id: row.id,
    majorCode: row.majorCode,
    majorName: row.majorName,
    description: row.description
  })
  dialogVisible.value = true
}

// 删除专业
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除专业"${row.majorName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await majorApi.deleteMajor(row.id)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadMajorList()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete major error:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedMajors.value.length} 个专业吗？`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedMajors.value.map(item => item.id)
    const response = await majorApi.batchDeleteMajors(ids)
    if (response.data.code === 200) {
      ElMessage.success('批量删除成功')
      loadMajorList()
    } else {
      ElMessage.error(response.data.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Batch delete majors error:', error)
      ElMessage.error('批量删除失败')
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
      response = await majorApi.updateMajor(form.id, form)
    } else {
      // 新增
      response = await majorApi.createMajor(form)
    }
    
    if (response.data.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadMajorList()
    } else {
      ElMessage.error(response.data.message || (form.id ? '更新失败' : '创建失败'))
    }
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
    majorCode: '',
    majorName: '',
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
  loadMajorList()
})
</script>

<style scoped>
.major-management {
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

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.table-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
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
  
  .action-buttons {
    justify-content: center;
  }
}
</style>

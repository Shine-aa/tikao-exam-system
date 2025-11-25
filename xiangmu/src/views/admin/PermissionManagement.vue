<template>
  <div class="permission-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.permissionName"
          placeholder="请输入权限名称"
          style="width: 200px; margin-right: 10px;"
          clearable
        />
        <el-input
          v-model="searchForm.permissionCode"
          placeholder="请输入权限代码"
          style="width: 200px; margin-right: 10px;"
          clearable
        />
        <el-select
          v-model="searchForm.resourceType"
          placeholder="资源类型"
          style="width: 150px; margin-right: 10px;"
          clearable
        >
          <el-option label="系统" value="system" />
          <el-option label="用户" value="user" />
          <el-option label="角色" value="role" />
          <el-option label="数据" value="data" />
        </el-select>
        <el-select
          v-model="searchForm.isActive"
          placeholder="状态"
          style="width: 120px; margin-right: 10px;"
          clearable
        >
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
        <el-button type="primary" @click="searchPermissions">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
      
      <!-- 批量操作栏 -->
      <div class="batch-actions" v-if="selectedPermissions.length > 0">
        <el-button type="danger" @click="batchDeletePermissions">
          <el-icon><Delete /></el-icon>
          批量删除 ({{ selectedPermissions.length }})
        </el-button>
      </div>
      
      <!-- 权限表格 -->
      <el-table 
        :data="permissions" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="permissionName" label="权限名称" />
        <el-table-column prop="permissionCode" label="权限代码" />
        <el-table-column prop="resourceType" label="资源类型" width="120">
          <template #default="scope">
            <el-tag :type="getResourceTypeTagType(scope.row.resourceType)">
              {{ scope.row.resourceType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作类型" width="120">
          <template #default="scope">
            <el-tag :type="getActionTagType(scope.row.action)">
              {{ scope.row.action }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
              {{ scope.row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
      
      <!-- 分页组件 -->
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { getPermissionList, getPermissionListWithPagination, batchDeletePermissions as batchDeletePermissionsAPI } from '@/api/admin'

const loading = ref(false)
const permissions = ref([])
const selectedPermissions = ref([])

const searchForm = reactive({
  permissionName: '',
  permissionCode: '',
  resourceType: null,
  isActive: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 获取资源类型标签类型
const getResourceTypeTagType = (resourceType) => {
  const typeMap = {
    'system': 'danger',
    'user': 'primary',
    'role': 'success',
    'data': 'warning'
  }
  return typeMap[resourceType] || 'info'
}

// 获取操作类型标签类型
const getActionTagType = (action) => {
  const typeMap = {
    'read': 'info',
    'create': 'success',
    'update': 'warning',
    'delete': 'danger'
  }
  return typeMap[action] || 'info'
}

// 加载权限列表
const loadPermissions = async (searchParams = {}) => {
  loading.value = true
  try {
    const params = { 
      ...searchForm, 
      ...searchParams,
      page: pagination.page,
      size: pagination.size
    }
    const response = await getPermissionListWithPagination(params)
    permissions.value = response.data.content || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载权限列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 搜索权限
const searchPermissions = () => {
  loadPermissions()
}

// 重置搜索
const resetSearch = () => {
  searchForm.permissionName = ''
  searchForm.permissionCode = ''
  searchForm.resourceType = null
  searchForm.isActive = null
  loadPermissions()
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedPermissions.value = selection
}

// 批量删除权限
const batchDeletePermissions = async () => {
  if (selectedPermissions.value.length === 0) {
    ElMessage.warning('请选择要删除的权限')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedPermissions.value.length} 个权限吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const permissionIds = selectedPermissions.value.map(permission => permission.id)
    await batchDeletePermissionsAPI(permissionIds)
    ElMessage.success('批量删除成功')
    selectedPermissions.value = []
    loadPermissions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadPermissions()
}

// 页码变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadPermissions()
}

onMounted(() => {
  loadPermissions()
})
</script>

<style scoped>
.permission-management {
  padding: 0;
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
}

.batch-actions {
  margin-bottom: 16px;
  padding: 8px 0;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>

<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.roleName"
          placeholder="请输入角色名称"
          style="width: 200px; margin-right: 10px;"
          clearable
        />
        <el-input
          v-model="searchForm.roleCode"
          placeholder="请输入角色代码"
          style="width: 200px; margin-right: 10px;"
          clearable
        />
        <el-select
          v-model="searchForm.isActive"
          placeholder="角色状态"
          style="width: 120px; margin-right: 10px;"
          clearable
        >
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
        <el-button type="primary" @click="searchRoles">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
      
      <!-- 批量操作栏 -->
      <div class="batch-actions" v-if="selectedRoles.length > 0">
        <el-button type="danger" @click="batchDeleteRoles">
          <el-icon><Delete /></el-icon>
          批量删除 ({{ selectedRoles.length }})
        </el-button>
      </div>
      
      <!-- 角色表格 -->
      <el-table 
        :data="roles" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色代码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
              {{ scope.row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissions" label="权限" width="200">
          <template #default="scope">
            <el-tag
              v-for="permission in scope.row.permissions"
              :key="permission.id"
              size="small"
              style="margin-right: 5px;"
            >
              {{ permission.permissionName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="editRole(scope.row)">编辑</el-button>
            <el-button
              size="small"
              :type="scope.row.isActive ? 'warning' : 'success'"
              @click="toggleRoleStatus(scope.row)"
            >
              {{ scope.row.isActive ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteRole(scope.row)">删除</el-button>
          </template>
        </el-table-column>
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
    
    <!-- 创建/编辑角色对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingRole ? '编辑角色' : '新增角色'"
      width="600px"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleFormRules"
        label-width="80px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色代码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色代码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            placeholder="请输入角色描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="权限" prop="permissionIds">
          <el-select
            v-model="roleForm.permissionIds"
            multiple
            placeholder="请选择权限"
            style="width: 100%;"
          >
            <el-option
              v-for="permission in permissions"
              :key="permission.id"
              :label="permission.permissionName"
              :value="permission.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Delete } from '@element-plus/icons-vue'
import { 
  getRoleList, 
  createRole, 
  updateRole, 
  deleteRole as deleteRoleAPI, 
  toggleRoleStatus as toggleRoleStatusAPI,
  assignRolePermissions,
  getRoleListWithPagination,
  batchDeleteRoles as batchDeleteRolesAPI
} from '@/api/admin'
import { getPermissionList } from '@/api/admin'

const loading = ref(false)
const roles = ref([])
const permissions = ref([])
const selectedRoles = ref([])
const showCreateDialog = ref(false)
const editingRole = ref(null)
const roleFormRef = ref()

const searchForm = reactive({
  roleName: '',
  roleCode: '',
  isActive: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const roleForm = reactive({
  roleName: '',
  roleCode: '',
  description: '',
  permissionIds: []
})

const roleFormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色代码', trigger: 'blur' },
    { min: 2, max: 50, message: '角色代码长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 加载角色列表
const loadRoles = async (searchParams = {}) => {
  loading.value = true
  try {
    const params = { 
      ...searchForm, 
      ...searchParams,
      page: pagination.page,
      size: pagination.size
    }
    const response = await getRoleListWithPagination(params)
    roles.value = response.data.content || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载角色列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 加载权限列表
const loadPermissions = async () => {
  try {
    const response = await getPermissionList()
    permissions.value = response.data || []
  } catch (error) {
    ElMessage.error('加载权限列表失败: ' + (error.response?.data?.message || error.message))
  }
}

// 搜索角色
const searchRoles = () => {
  loadRoles()
}

// 重置搜索
const resetSearch = () => {
  searchForm.roleName = ''
  searchForm.roleCode = ''
  searchForm.isActive = null
  loadRoles()
}

// 编辑角色
const editRole = (role) => {
  editingRole.value = role
  roleForm.roleName = role.roleName
  roleForm.roleCode = role.roleCode
  roleForm.description = role.description
  roleForm.permissionIds = role.permissions.map(permission => permission.id)
  showCreateDialog.value = true
}

// 保存角色
const saveRole = async () => {
  try {
    await roleFormRef.value.validate()
    
    if (editingRole.value) {
      // 更新角色
      await updateRole(editingRole.value.id, roleForm)
      ElMessage.success('角色更新成功')
    } else {
      // 创建角色
      await createRole(roleForm)
      ElMessage.success('角色创建成功')
    }
    
    showCreateDialog.value = false
    resetForm()
    loadRoles()
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.response?.data?.message || error.message))
  }
}

// 切换角色状态
const toggleRoleStatus = async (role) => {
  try {
    await ElMessageBox.confirm(
      `确定要${role.isActive ? '禁用' : '启用'}角色 ${role.roleName} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await toggleRoleStatusAPI(role.id)
    role.isActive = !role.isActive
    ElMessage.success(`角色${role.isActive ? '启用' : '禁用'}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 删除角色
const deleteRole = async (role) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色 ${role.roleName} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteRoleAPI(role.id)
    ElMessage.success('角色删除成功')
    loadRoles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 重置表单
const resetForm = () => {
  editingRole.value = null
  roleForm.roleName = ''
  roleForm.roleCode = ''
  roleForm.description = ''
  roleForm.permissionIds = []
  roleFormRef.value?.resetFields()
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedRoles.value = selection
}

// 批量删除角色
const batchDeleteRoles = async () => {
  if (selectedRoles.value.length === 0) {
    ElMessage.warning('请选择要删除的角色')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRoles.value.length} 个角色吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const roleIds = selectedRoles.value.map(role => role.id)
    await batchDeleteRolesAPI(roleIds)
    ElMessage.success('批量删除成功')
    selectedRoles.value = []
    loadRoles()
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
  loadRoles()
}

// 页码变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadRoles()
}

onMounted(() => {
  loadRoles()
  loadPermissions()
})
</script>

<style scoped>
.role-management {
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

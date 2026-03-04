<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div>
            <el-button type="primary" @click="handleShowCreateDialog">
              <el-icon>
                <Plus />
              </el-icon>
              新增用户
            </el-button>
            <el-button type="primary" @click="showImportDialog = true" style="margin-left: 10px;">
              <el-icon>
                <Upload />
              </el-icon>
              导入用户
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="searchForm.name" placeholder="请输入用户姓名" style="width: 200px; margin-right: 10px;" clearable />
        <el-input v-model="searchForm.username" placeholder="请输入用户名" style="width: 200px; margin-right: 10px;"
                  clearable />
        <el-input v-model="searchForm.email" placeholder="请输入邮箱" style="width: 200px; margin-right: 10px;" clearable />
        <el-select v-model="searchForm.isActive" placeholder="用户状态" style="width: 120px; margin-right: 10px;" clearable>
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
        <el-button type="primary" @click="searchUsers">
          <el-icon>
            <Search />
          </el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">
          <el-icon>
            <Refresh />
          </el-icon>
          重置
        </el-button>
      </div>

      <!-- 批量操作栏 -->
      <div class="batch-actions" v-if="selectedUsers.length > 0">
        <el-button type="danger" @click="batchDeleteUsers">
          <el-icon>
            <Delete />
          </el-icon>
          批量删除 ({{ selectedUsers.length }})
        </el-button>
      </div>

      <!-- 用户表格 -->
      <el-table :data="users" style="width: 100%" v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="人脸照片" width="100">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.facePhoto">
              <el-icon><UserIcon /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="用户姓名" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
              {{ scope.row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roles" label="角色" width="200">
          <template #default="scope">
            <el-tag v-for="role in scope.row.roles" :key="role.id" size="small" style="margin-right: 5px;">
              {{ role.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="350" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
            <el-button size="small" type="primary" @click="showFaceDialog(scope.row)">人脸管理</el-button>
            <el-button size="small" type="info" @click="showChangePassword(scope.row)">密码</el-button>
            <el-button size="small" :type="scope.row.isActive ? 'warning' : 'success'"
                       @click="toggleUserStatus(scope.row)">
              {{ scope.row.isActive ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
                       :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper"
                       @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 创建/编辑用户对话框 -->
    <el-dialog v-model="showCreateDialog" :title="editingUser ? '编辑用户' : '新增用户'" width="600px">
      <el-form ref="userFormRef" :model="userForm" :rules="userFormRules" label-width="80px">
        <el-form-item label="用户姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入用户姓名" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!editingUser" label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="userForm.roleIds" multiple placeholder="请选择角色" style="width: 100%;">
            <el-option v-for="role in roles" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入用户对话框（手动上传模式） -->
    <el-dialog v-model="showImportDialog" title="导入用户" width="500px">
      <div style="display: flex; flex-direction: column; gap: 16px;">
        <el-upload ref="uploadRef" drag :before-upload="beforeUpload" :show-file-list="true" :auto-upload="false"
                 accept=".csv,.xlsx" :on-change="handleFileChange">
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">拖拽文件到这里，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">只支持 CSV 或 Excel 文件</div>
        </el-upload>
        <el-button type="info" @click="handleDownloadTemplate" style="align-self: flex-start;">
          下载导入模板
        </el-button>
      </div>

      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedFile" @click="submitUpload">导入</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="showErrorDialog" title="导入失败详情" width="700px">
      <el-table :data="importErrors" style="width: 100%">
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="classId" label="班级" min-width="80" />
        <el-table-column prop="roleName" label="角色" min-width="80" />
        <el-table-column prop="message" label="错误信息" min-width="200" />
      </el-table>

      <template #footer>
        <el-button @click="showErrorDialog = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="showChangePasswordDialog" title="修改密码" width="400px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordFormRules" label-width="80px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showChangePasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="submitChangePassword">确定</el-button>
      </template>
    </el-dialog>

    <!-- 管理员管理用户人脸照片对话框 -->
    <el-dialog v-model="showFaceManageDialog" title="人脸照片管理" width="450px" center>
      <div class="admin-face-manage">
        <div class="current-face">
          <p>当前人脸照片：</p>
          <div class="face-preview-box">
            <el-image v-if="selectedUserForFace?.facePhoto" :src="selectedUserForFace.facePhoto" fit="cover" class="preview-img">
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>暂无照片</span>
                </div>
              </template>
            </el-image>
            <div v-else class="image-error">
              <el-icon><Picture /></el-icon>
              <span>暂无照片</span>
            </div>
          </div>
        </div>

        <el-divider>更新照片</el-divider>

        <div class="upload-section">
          <el-upload
            class="admin-face-uploader"
            action="#"
            :show-file-list="false"
            :http-request="handleAdminFaceUpload"
            :loading="faceUploading"
          >
            <el-button type="primary">
              <el-icon><Camera /></el-icon>选择并上传新照片
            </el-button>
          </el-upload>
          <p class="upload-tip">支持 JPG/PNG，大小不超过 2MB</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="showFaceManageDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Delete, Upload, User as UserIcon, Camera, Picture } from '@element-plus/icons-vue'
import {
  getUserListWithPagination,
  createUser,
  updateUser,
  deleteUser as deleteUserAPI,
  batchDeleteUsers as batchDeleteUsersAPI,
  toggleUserStatus as toggleUserStatusAPI,
  importUsers,
  changeUserPassword,
  adminUpdateUserFace
} from '@/api/admin'
import { getRoleList } from '@/api/admin'

const loading = ref(false)
const users = ref([])
const roles = ref([])
const showCreateDialog = ref(false)
const editingUser = ref(null)
const userFormRef = ref()
const selectedUsers = ref([])

const showImportDialog = ref(false)
const uploadRef = ref(null)
const selectedFile = ref(null) // 导入 Excel 文件

const showErrorDialog = ref(false)
const importErrors = ref([])

// 修改密码相关
const showChangePasswordDialog = ref(false)
const selectedUserForPasswordChange = ref(null)

// 人脸管理相关
const showFaceManageDialog = ref(false)
const selectedUserForFace = ref(null)
const faceUploading = ref(false)

const passwordFormRef = ref()
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

const passwordFormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const searchForm = reactive({
  name: '',
  username: '',
  email: '',
  isActive: null
})

const userForm = reactive({
  username: '',
  password: '',
  email: '',
  roleIds: []
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const userFormRules = {
  name: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '用户姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { 
      required: () => !editingUser.value, 
      message: '请输入密码', 
      trigger: 'blur' 
    },
    { 
      validator: (rule, value, callback) => {
        if (editingUser.value && !value) {
          // 编辑模式下可以不输入密码
          callback();
        } else if (value && (value.length < 6 || value.length > 20)) {
          callback(new Error('密码长度在 6 到 20 个字符'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 加载用户列表
const loadUsers = async (searchParams = {}) => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      ...searchParams,
      page: pagination.page,
      size: pagination.size
    }
    const response = await getUserListWithPagination(params)
    users.value = response.data.content || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载用户列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 加载角色列表
const loadRoles = async () => {
  try {
    const response = await getRoleList()
    roles.value = response.data || []
  } catch (error) {
    ElMessage.error('加载角色列表失败: ' + (error.response?.data?.message || error.message))
  }
}

// 防抖函数
let searchTimer = null
const debounceSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    pagination.page = 1 // 搜索时重置到第一页
    loadUsers()
  }, 500) // 500ms 防抖延迟
}

// 搜索用户
const searchUsers = () => {
  pagination.page = 1
  loadUsers()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.username = ''
  searchForm.email = ''
  searchForm.isActive = null
  pagination.page = 1
  loadUsers()
}

// 监听搜索表单变化，自动搜索
watch(
  () => [searchForm.name, searchForm.username, searchForm.email, searchForm.isActive],
  () => {
    debounceSearch()
  },
  { deep: true }
)

// 编辑用户
const editUser = (user) => {
  editingUser.value = user
  userForm.name = user.name
  userForm.username = user.username
  userForm.password = ''
  userForm.email = user.email
  userForm.roleIds = user.roles.map(role => role.id)
  showCreateDialog.value = true
}

// 保存用户
const saveUser = async () => {
  try {
    await userFormRef.value.validate()

    if (editingUser.value) {
      await updateUser(editingUser.value.id, userForm)
      ElMessage.success('用户更新成功')
    } else {
      await createUser(userForm)
      ElMessage.success('用户创建成功')
    }

    showCreateDialog.value = false
    resetForm()
    loadUsers()
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.response?.data?.message || error.message))
  }
}

// 切换用户状态
const toggleUserStatus = async (user) => {
  try {
    await ElMessageBox.confirm(
        `确定要${user.isActive ? '禁用' : '启用'}用户 ${user.username} 吗？`,
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    await toggleUserStatusAPI(user.id)
    user.isActive = !user.isActive
    ElMessage.success(`用户${user.isActive ? '启用' : '禁用'}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 删除用户
const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除用户 ${user.username} 吗？`,
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    await deleteUserAPI(user.id)
    ElMessage.success('用户删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 处理显示创建对话框
const handleShowCreateDialog = () => {
  resetForm()
  showCreateDialog.value = true
}

// 重置表单
const resetForm = () => {
  editingUser.value = null
  userForm.username = ''
  userForm.password = ''
  userForm.email = ''
  userForm.roleIds = []
  userFormRef.value?.resetFields()
}

// 修改密码相关函数
const showChangePassword = (user) => {
  selectedUserForPasswordChange.value = user
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  showChangePasswordDialog.value = true
}

// 显示人脸管理对话框
const showFaceDialog = (user) => {
  selectedUserForFace.value = user
  showFaceManageDialog.value = true
}

// 管理员上传人脸照片处理
const handleAdminFaceUpload = async (options) => {
  const { file } = options
  
  // 校验文件类型
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJPG) {
    ElMessage.error('上传照片只能是 JPG 或 PNG 格式!')
    return false
  }
  
  // 校验文件大小 (限制 2MB)
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('上传照片大小不能超过 2MB!')
    return false
  }

  try {
    faceUploading.value = true
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await adminUpdateUserFace(selectedUserForFace.value.id, formData)
    if (response.code === 200) {
      selectedUserForFace.value.facePhoto = response.data
      ElMessage.success('用户人脸照片更新成功')
      
      // 刷新表格中的数据
      loadUsers()
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    console.error('上传人脸照片出错:', error)
    ElMessage.error('上传人脸照片出错')
  } finally {
    faceUploading.value = false
  }
}

const submitChangePassword = async () => {
  try {
    if (!selectedUserForPasswordChange.value) return
    
    // 验证表单
    await passwordFormRef.value.validate()
    
    await changeUserPassword(selectedUserForPasswordChange.value.id, {
      password: passwordForm.newPassword // 注意：根据后端接口，参数名可能需要调整
    })
    
    ElMessage.success('密码修改成功')
    showChangePasswordDialog.value = false
  } catch (error) {
    // 表单验证失败不显示错误提示
    if (error.name !== 'Error') {
      ElMessage.error('密码修改失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 批量删除用户
const batchDeleteUsers = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }

  try {
    await ElMessageBox.confirm(
        `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`,
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    const userIds = selectedUsers.value.map(user => user.id)
    await batchDeleteUsersAPI(userIds)
    ElMessage.success('批量删除成功')
    selectedUsers.value = []
    loadUsers()
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
  loadUsers()
}

// 页码变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadUsers()
}

// 导入 Excel 用户逻辑
const beforeUpload = (file) => {
  const isExcel =
      file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
      file.type === 'application/vnd.ms-excel'
  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件 (.xls 或 .xlsx)')
  }
  return isExcel
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

// 下载用户导入模板
const handleDownloadTemplate = () => {
  try {
    // 创建一个临时链接下载模板文件
    const link = document.createElement('a')
    link.href = '/templates/用户导入模板.xlsx'
    link.download = '用户导入模板.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败，请重试')
  }
}

const submitUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    const response = await importUsers(formData)

    if (response.code === 200 && response.data) {
      // 提示成功/失败数量
      ElMessage.success(`导入完成！成功: ${response.data.success}，失败: ${response.data.fail}`)

      // 如果有失败记录，处理并弹出对话框
      if (response.data.failedRecords && response.data.failedRecords.length > 0) {
        importErrors.value = response.data.failedRecords.map(item => ({
          name: item.name,
          username: item.username,
          email: item.email,
          phone: item.phone,
          classId: item.classId,
          roleName: item.roleName,
          message: item.error
        }))
        showErrorDialog.value = true
      }

      // 关闭导入对话框
      showImportDialog.value = false

      // 刷新用户列表
      loadUsers()

      // 清空选中的文件
      selectedFile.value = null
      uploadRef.value.clearFiles()
    } else {
      ElMessage.error(response.message || '导入失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('导入失败，请检查服务器日志')
  }
}


onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<style scoped>
.user-management {
  padding: 0;
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

/* 人脸管理对话框样式 */
.admin-face-manage {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.face-preview-box {
  width: 180px;
  height: 180px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  margin-top: 8px;
}

.preview-img {
  width: 100%;
  height: 100%;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
  gap: 8px;
}

.image-error .el-icon {
  font-size: 32px;
}

.upload-section {
  text-align: center;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>

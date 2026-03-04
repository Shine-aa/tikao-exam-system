<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>个人资料</h2>
      <p>管理您的个人信息</p>
    </div>

    <el-card class="profile-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button 
            type="primary" 
            @click="editProfile"
            :disabled="isEditing"
          >
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </template>

      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
        class="profile-form"
      >
        <!-- 人脸照片录入区域 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="人脸录入">
              <div class="face-upload-container">
                <el-upload
                  class="face-uploader"
                  action="#"
                  :show-file-list="false"
                  :http-request="handleFaceUpload"
                >
                  <img v-if="profileForm.facePhoto" :src="profileForm.facePhoto" class="face-image" />
                  <el-icon v-else class="face-uploader-icon"><Camera /></el-icon>
                  <div class="face-uploader-mask" v-if="profileForm.facePhoto">
                    <el-icon><Edit /></el-icon>
                    <span>更新照片</span>
                  </div>
                </el-upload>
                <div class="face-tips">
                  <p>1. 请上传考生的正脸近照，确保光线充足，五官清晰。</p>
                  <p>2. 该照片将作为考前人脸识别的基准对比照片。</p>
                  <p>3. 支持 JPG/PNG 格式，大小不超过 2MB。</p>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input 
                v-model="profileForm.username" 
                :disabled="!isEditing"
                placeholder="请输入用户名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input 
                v-model="profileForm.email" 
                :disabled="!isEditing"
                placeholder="请输入邮箱"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input 
                v-model="profileForm.phone" 
                :disabled="!isEditing"
                placeholder="请输入手机号"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-input 
                :value="getRoleDisplayName(profileForm.role)" 
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input 
                :value="formatDateTime(profileForm.createTime)" 
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最后登录">
              <el-input 
                :value="formatDateTime(profileForm.lastLoginTime)" 
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag :type="profileForm.isActive ? 'success' : 'danger'">
                {{ profileForm.isActive ? '正常' : '禁用' }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 密码修改区域 -->
        <el-divider content-position="left">修改密码</el-divider>
        
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
        >
          <el-form-item label="当前密码" prop="currentPassword">
            <el-input 
              v-model="passwordForm.currentPassword" 
              type="password" 
              placeholder="请输入当前密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="passwordForm.newPassword" 
              type="password" 
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="passwordForm.confirmPassword" 
              type="password" 
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              @click="changePassword"
              :loading="passwordLoading"
            >
              修改密码
            </el-button>
            <el-button @click="resetPasswordForm">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-form>

      <!-- 操作按钮 -->
      <div v-if="isEditing" class="form-actions">
        <el-button 
          type="primary" 
          @click="saveProfile"
          :loading="saving"
        >
          保存修改
        </el-button>
        <el-button @click="cancelEdit">
          取消
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Plus, Camera } from '@element-plus/icons-vue'
import { getUserInfo, updateProfile, changePassword as changePasswordApi, uploadFace } from '../../api/user'

// 响应式数据
const isEditing = ref(false)
const saving = ref(false)
const faceLoading = ref(false)
const passwordLoading = ref(false)
const profileFormRef = ref()
const passwordFormRef = ref()

// 个人资料表单
const profileForm = reactive({
  id: null,
  username: '',
  email: '',
  phone: '',
  role: '',
  isActive: true,
  facePhoto: '',
  createTime: null,
  lastLoginTime: null
})

// 人脸照片上传处理
const handleFaceUpload = async (options) => {
  const { file } = options
  
  // 校验文件类型
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJPG) {
    ElMessage.error('上传人脸照片只能是 JPG 或 PNG 格式!')
    return false
  }
  
  // 校验文件大小 (限制 2MB)
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('上传人脸照片大小不能超过 2MB!')
    return false
  }

  try {
    faceLoading.value = true
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await uploadFace(formData)
    if (response.code === 200) {
      profileForm.facePhoto = response.data
      ElMessage.success('人脸照片上传成功')
      
      // 同步更新本地存储
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      userInfo.facePhoto = response.data
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    console.error('上传人脸照片出错:', error)
    ElMessage.error('上传人脸照片出错')
  } finally {
    faceLoading.value = false
  }
}

// 密码修改表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 密码验证规则
const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await getUserInfo()
    if (response.code === 200) {
      Object.assign(profileForm, response.data)
    } else {
      ElMessage.error(response.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 编辑资料
const editProfile = () => {
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  loadUserInfo() // 重新加载原始数据
}

// 保存资料
const saveProfile = async () => {
  try {
    await profileFormRef.value.validate()
    
    saving.value = true
    const response = await updateProfile(profileForm)
    
    if (response.code === 200) {
      ElMessage.success('资料更新成功')
      isEditing.value = false
      
      // 更新本地存储的用户信息
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      Object.assign(userInfo, profileForm)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新资料失败:', error)
    ElMessage.error('更新资料失败')
  } finally {
    saving.value = false
  }
}

// 修改密码
const changePassword = async () => {
  try {
    await profileFormRef.value.validateField(['currentPassword', 'newPassword', 'confirmPassword'])
    
    passwordLoading.value = true
    const response = await changePasswordApi({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (response.code === 200) {
      ElMessage.success('密码修改成功')
      resetPasswordForm()
    } else {
      ElMessage.error(response.message || '密码修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  profileFormRef.value?.clearValidate(['currentPassword', 'newPassword', 'confirmPassword'])
}

// 获取角色显示名称
const getRoleDisplayName = (role) => {
  const roleMap = {
    'SUPER_ADMIN': '超级管理员',
    'USER': '普通用户'
  }
  return roleMap[role] || role
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
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

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-form {
  max-width: 800px;
}

.form-actions {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  text-align: right;
}

.form-actions .el-button {
  margin-left: 10px;
}

.el-divider {
  margin: 30px 0 20px 0;
}

/* 人脸照片上传样式 */
.face-upload-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.face-uploader {
  position: relative;
  width: 140px;
  height: 140px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}

.face-uploader:hover {
  border-color: #409eff;
}

.face-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 140px;
  height: 140px;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.face-image {
  width: 140px;
  height: 140px;
  object-fit: cover;
  display: block;
}

.face-uploader-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 12px;
}

.face-uploader:hover .face-uploader-mask {
  opacity: 1;
}

.face-tips {
  color: #909399;
  font-size: 13px;
  line-height: 1.6;
}

.face-tips p {
  margin: 0;
}
</style>

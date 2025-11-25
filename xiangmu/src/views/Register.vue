<template>
  <div class="register-container">
    <div class="register-box">
      <!-- Swagger 开发者工具按钮 -->
      <div class="developer-tools">
        <el-button 
          type="info" 
          size="small" 
          @click="openSwagger"
          icon="Document"
          plain
        >
          API文档
        </el-button>
      </div>
      
      <div class="register-header">
        <div class="logo">
          <div class="logo-icon">钛</div>
          <div class="logo-text">钛考</div>
        </div>
        <h2>用户注册</h2>
        <p>欢迎加入我们</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
      
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="name">
          <el-input
            v-model="registerForm.name"
            placeholder="请输入您的真实姓名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            size="large"
            maxlength="11"
          />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="smsCode">
          <div class="sms-code-input">
            <el-input
              v-model="registerForm.smsCode"
              placeholder="请输入短信验证码"
              prefix-icon="Message"
              size="large"
              maxlength="6"
            />
            <el-button
              type="primary"
              size="large"
              :disabled="smsCountdown > 0 || !isPhoneValid"
              @click="sendSmsCode"
            >
              {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            立即注册
          </el-button>
        </el-form-item>
        
        <div class="login-link">
          已有账号？
          <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/user'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

// 短信验证码倒计时
const smsCountdown = ref(0)

// 手机号格式验证
const isPhoneValid = computed(() => {
  return /^1[3-9]\d{9}$/.test(registerForm.phone)
})

const registerForm = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
  smsCode: ''
})

// 表单验证规则
const registerRules = {
  name: [
    { required: true, message: '请输入您的真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '用户姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  smsCode: [
    { required: true, message: '请输入短信验证码', trigger: 'blur' },
    { pattern: /^\d{4}$/, message: '请输入4位数字验证码', trigger: 'blur' }
  ]
}

// 发送短信验证码
const sendSmsCode = async () => {
  if (!isPhoneValid.value) {
    ElMessage.error('请输入正确的手机号')
    return
  }
  
  try {
    const response = await fetch('/api/auth/sms/send', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        phone: registerForm.phone,
        type: 'register'
      })
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      ElMessage.success('验证码发送成功')
      startCountdown()
      
      // 测试时显示验证码
      if (result.data && result.data.code) {
        console.log('短信验证码:', result.data.code)
        ElMessage.info(`验证码: ${result.data.code}`)
      }
    } else {
      ElMessage.error(result.message || '发送失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('发送验证码失败')
  }
}

// 开始倒计时
const startCountdown = () => {
  smsCountdown.value = 60
  const timer = setInterval(() => {
    smsCountdown.value--
    if (smsCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    const { name,username, password, phone, email, smsCode } = registerForm
    const response = await register({ name, username, password, phone, email, smsCode })
    
    ElMessage.success('注册成功！')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开Swagger API文档
const openSwagger = () => {
  window.open('http://localhost:3000/swagger', '_blank')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
}

.developer-tools {
  position: absolute;
  top: 15px;
  right: 15px;
  z-index: 10;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  gap: 8px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  letter-spacing: 1px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.register-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

.sms-code-input {
  display: flex;
  gap: 10px;
}

.sms-code-input .el-input {
  flex: 1;
}

.sms-code-input .el-button {
  min-width: 120px;
}
</style>

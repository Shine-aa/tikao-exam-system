<template>
  <div class="login-container">
    <div class="login-box">
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
      
      <div class="login-header">
        <div class="logo">
          <div class="logo-icon">钛</div>
          <div class="logo-text">钛考</div>
        </div>
        <h2>用户登录</h2>
        <p>欢迎回来</p>
        <div class="login-tabs">
          <el-button 
            :type="loginType === 'password' ? 'primary' : ''" 
            @click="loginType = 'password'"
            size="small"
          >
            密码登录
          </el-button>
          <el-button 
            :type="loginType === 'sms' ? 'primary' : ''" 
            @click="loginType = 'sms'"
            size="small"
          >
            短信登录
          </el-button>
        </div>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <!-- 密码登录 -->
        <template v-if="loginType === 'password'">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
        </template>
        
        <!-- 短信登录 -->
        <template v-if="loginType === 'sms'">
          <el-form-item prop="phone">
            <el-input
              v-model="loginForm.phone"
              placeholder="请输入手机号"
              prefix-icon="Phone"
              size="large"
              maxlength="11"
            />
          </el-form-item>
          
          <el-form-item prop="smsCode">
            <div class="sms-code-input">
              <el-input
                v-model="loginForm.smsCode"
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
        </template>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            立即登录
          </el-button>
        </el-form-item>
        
        <div class="register-link">
          还没有账号？
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)
const loginType = ref('password') // password 或 sms
const smsCountdown = ref(0)

const loginForm = reactive({
  username: '',
  password: '',
  phone: '',
  smsCode: '',
  rememberMe: false
})

// 手机号格式验证
const isPhoneValid = computed(() => {
  return /^1[3-9]\d{9}$/.test(loginForm.phone)
})

// 表单验证规则
const loginRules = computed(() => {
  const rules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' }
    ],
    phone: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
    ],
    smsCode: [
      { required: true, message: '请输入短信验证码', trigger: 'blur' },
      { pattern: /^\d{4}$/, message: '请输入4位数字验证码', trigger: 'blur' }
    ]
  }
  
  // 根据登录类型返回不同的验证规则
  if (loginType.value === 'password') {
    return {
      username: rules.username,
      password: rules.password
    }
  } else {
    return {
      phone: rules.phone,
      smsCode: rules.smsCode
    }
  }
})

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
        phone: loginForm.phone,
        type: 'login'
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

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    let response
    
    if (loginType.value === 'password') {
      // 密码登录
      const { username, password, rememberMe } = loginForm
      
      response = await login({ 
        username, 
        password, 
        rememberMe 
      })
    } else {
      // 短信登录
      const { phone, smsCode, rememberMe } = loginForm
      
      // 先验证短信验证码
      const verifyResponse = await fetch('/api/auth/sms/verify', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          phone,
          code: smsCode,
          type: 'login'
        })
      })
      
      const verifyResult = await verifyResponse.json()
      if (verifyResult.code !== 200) {
        throw new Error(verifyResult.message || '验证码验证失败')
      }
      
      // 验证码验证成功后，使用手机号登录
      response = await login({ 
        username: phone, // 使用手机号作为用户名
        password: '', // 短信登录不需要密码
        rememberMe,
        loginType: 'sms' // 标识为短信登录
      })
    }
    
    // 保存token和用户信息
    localStorage.setItem('token', response.data.token)
    localStorage.setItem('userInfo', JSON.stringify(response.data.userInfo))
    
    ElMessage.success('登录成功！')
    
    // 跳转到成功页面，显示用户信息
    router.push('/success')
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 检查是否有保存的登录信息（简化版本，主要让路由守卫处理）
const checkSavedLoginInfo = () => {
  // 这里不再自动跳转，让路由守卫来处理
  // 如果需要，可以在这里添加一些初始化逻辑
  
  // 添加一些调试信息
  const token = localStorage.getItem('token')
  const rememberMe = localStorage.getItem('remember_me')
  const rememberToken = localStorage.getItem('remember_token')
  
  console.log('当前登录状态:', {
    hasToken: !!token,
    hasRememberMe: rememberMe === 'true',
    hasRememberToken: !!rememberToken
  })
}

// 打开Swagger API文档
const openSwagger = () => {
  window.open('http://localhost:3000/swagger', '_blank')
}

// 页面加载时获取验证码和检查登录状态
onMounted(() => {
  checkSavedLoginInfo()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
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

.login-header {
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

.login-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.login-tabs {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  justify-content: center;
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

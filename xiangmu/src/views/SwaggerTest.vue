<template>
  <div class="swagger-test">
    <el-card class="swagger-card">
      <template #header>
        <div class="card-header">
          <h2>API文档测试</h2>
          <el-button type="primary" @click="openSwaggerUI">打开Swagger UI</el-button>
        </div>
      </template>
      
      <div class="swagger-info">
        <el-alert
          title="Swagger API文档"
          type="info"
          :closable="false"
          show-icon>
          <template #default>
            <p>点击下方按钮打开Swagger UI，可以查看和测试所有API接口：</p>
            <ul>
              <li>查看所有API接口文档</li>
              <li>在线测试API接口</li>
              <li>查看请求和响应格式</li>
              <li>获取JWT Token进行认证</li>
            </ul>
          </template>
        </el-alert>
        
        <div class="api-links">
          <h3>API文档链接：</h3>
          <el-link 
            :href="swaggerUrl" 
            target="_blank" 
            type="primary"
            class="api-link">
            {{ swaggerUrl }}
          </el-link>
          <br><br>
          <el-alert
            title="API文档功能"
            type="success"
            :closable="false"
            show-icon>
            <template #default>
              <ul>
                <li><strong>自动扫描：</strong>自动扫描所有Controller接口</li>
                <li><strong>分类展示：</strong>按功能模块分类显示API</li>
                <li><strong>在线测试：</strong>直接在文档中测试API接口</li>
                <li><strong>参数说明：</strong>详细的请求参数和响应格式说明</li>
                <li><strong>认证支持：</strong>支持JWT Token认证测试</li>
              </ul>
            </template>
          </el-alert>
        </div>
        
        <div class="auth-info">
          <h3>认证说明：</h3>
          <el-steps :active="2" finish-status="success">
            <el-step title="获取Token" description="调用登录接口获取JWT Token" />
            <el-step title="设置认证" description="在Swagger UI中点击Authorize按钮" />
            <el-step title="输入Token" description="输入Bearer {token}格式的认证信息" />
            <el-step title="测试接口" description="现在可以测试需要认证的接口了" />
          </el-steps>
        </div>
        
        <div class="test-token">
          <h3>快速获取Token：</h3>
          <el-form :model="loginForm" @submit.prevent="getToken">
            <el-form-item label="用户名">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getToken" :loading="loading">
                获取Token
              </el-button>
            </el-form-item>
          </el-form>
          
          <div v-if="token" class="token-display">
            <h4>获取到的Token：</h4>
            <el-input 
              v-model="token" 
              readonly 
              type="textarea" 
              :rows="3"
              class="token-input" />
            <el-button @click="copyToken" type="success" size="small">
              复制Token
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'

const swaggerUrl = 'http://localhost:8080/swagger-ui.html'
const loading = ref(false)
const token = ref('')

const loginForm = reactive({
  username: 'admin',
  password: '123456'
})

const openSwaggerUI = () => {
  window.open(swaggerUrl, '_blank')
}

const getToken = async () => {
  loading.value = true
  try {
    const response = await login(loginForm)
    if (response.code === 200) {
      token.value = response.data.token
      ElMessage.success('Token获取成功')
    }
  } catch (error) {
    ElMessage.error('获取Token失败')
  } finally {
    loading.value = false
  }
}

const copyToken = () => {
  navigator.clipboard.writeText(token.value).then(() => {
    ElMessage.success('Token已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}
</script>

<style scoped>
.swagger-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.swagger-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.swagger-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.api-links {
  margin: 20px 0;
}

.api-link {
  font-size: 16px;
  word-break: break-all;
}

.auth-info {
  margin: 20px 0;
}

.test-token {
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.token-display {
  margin-top: 20px;
}

.token-input {
  margin: 10px 0;
}

ul {
  margin: 10px 0;
  padding-left: 20px;
}

li {
  margin: 5px 0;
}
</style>

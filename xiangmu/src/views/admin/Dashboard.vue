<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon role-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalRoles }}</div>
              <div class="stat-label">总角色数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon permission-icon">
              <el-icon><Lock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalPermissions }}</div>
              <div class="stat-label">总权限数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon active-icon">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近用户</span>
            </div>
          </template>
          <el-table :data="recentUsers" style="width: 100%">
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column prop="isActive" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
                  {{ scope.row.isActive ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <div class="system-info">
            <div class="info-item">
              <span class="info-label">系统版本:</span>
              <span class="info-value">v1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">运行时间:</span>
              <span class="info-value">{{ systemInfo.uptime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">数据库状态:</span>
              <span class="info-value status-success">正常</span>
            </div>
            <div class="info-item">
              <span class="info-label">Redis状态:</span>
              <span class="info-value status-success">正常</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, UserFilled, Lock, Check } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/user'

const stats = ref({
  totalUsers: 0,
  totalRoles: 0,
  totalPermissions: 0,
  activeUsers: 0
})

const recentUsers = ref([])

const systemInfo = ref({
  uptime: '2天3小时'
})

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    const response = await getDashboardStats()
    if (response.code === 200) {
      const data = response.data
      stats.value = {
        totalUsers: data.totalUsers,
        totalRoles: data.totalRoles,
        totalPermissions: data.totalPermissions,
        activeUsers: data.activeUsers
      }
      recentUsers.value = data.recentUsers || []
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载仪表盘数据失败')
    
    // 如果API调用失败，使用默认值
    stats.value = {
      totalUsers: 0,
      totalRoles: 0,
      totalPermissions: 0,
      activeUsers: 0
    }
    recentUsers.value = []
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.role-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.permission-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.active-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.system-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #606266;
  font-weight: 500;
}

.info-value {
  color: #303133;
}

.status-success {
  color: #67c23a;
}
</style>

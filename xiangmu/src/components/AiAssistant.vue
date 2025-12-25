<template>
  <div>
    <!-- 悬浮按钮 -->
    <div 
      class="ai-assistant-float-btn" 
      v-if="!visible" 
      @click="visible = true"
      @mousedown="handleFloatBtnDragStart"
      :style="floatButtonStyle"
    >
      <img src="/src/assets/vue.svg" alt="AI助手" class="ai-logo" />
      <span>AI助手</span>
    </div>
    <!-- 对话框 -->
    <transition name="ai-dialog">
      <div 
        class="ai-assistant-dialog" 
        v-if="visible"
        :style="dialogStyle"
      >
        <div class="ai-header" @mousedown="handleDialogDragStart">
          <img src="/src/assets/vue.svg" alt="logo" class="ai-logo" />
          <span class="ai-title">你好，我是钛考 AI 助理</span>
          <button class="ai-close" @click="visible = false">×</button>
        </div>
        <div class="ai-body" ref="chatBody">
          <div v-for="(msg, idx) in messages" :key="idx" :class="['ai-msg', msg.role]">
            <span v-if="msg.role === 'user'" class="ai-user">我：</span>
            <span v-if="msg.role === 'ai'" class="ai-ai">AI：</span>
            <span>{{ msg.text }}</span>
          </div>
        </div>
        <div class="ai-footer">
          <input
              v-model="input"
              @keyup.enter="send"
              placeholder="请输入您的问题"
              class="ai-input"
          />
          <button @click="send" class="ai-send">发送</button>
          <button @click="send" style="margin-left:8px;background:#ff9800;color:#fff;">测试触发</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted, computed } from 'vue'
import { callChatMemory } from '@/api/chatMemory'

const visible = ref(false)
const input = ref('')
const messages = ref([])
const chatBody = ref(null)
const scrollTop = ref(0)

// 拖拽相关状态
const isDragging = ref(false)
const dragOffset = ref({ x: 0, y: 0 })
const isDraggingFloatBtn = ref(false)

// 悬浮按钮位置（相对于视口右下角的偏移量）
const floatBtnPosition = ref({ right: 32, bottom: 32 })

// 计算悬浮按钮和对话框的位置样式
const floatButtonStyle = computed(() => ({
  right: `${floatBtnPosition.value.right}px`,
  bottom: `${floatBtnPosition.value.bottom}px`,
  transform: `translateY(${scrollTop.value}px)`
}))

const dialogStyle = computed(() => ({
  right: `${floatBtnPosition.value.right}px`,
  bottom: `${floatBtnPosition.value.bottom}px`,
  transform: `translateY(${scrollTop.value}px)`
}))

// 处理滚动事件
const handleScroll = () => {
  scrollTop.value = window.scrollY || document.documentElement.scrollTop
}

// 悬浮按钮拖拽开始
const handleFloatBtnDragStart = (e) => {
  isDragging.value = true
  isDraggingFloatBtn.value = true
  // 计算鼠标相对于悬浮按钮右上角的偏移量
  const btnRect = e.target.closest('.ai-assistant-float-btn').getBoundingClientRect()
  dragOffset.value = {
    x: btnRect.right - e.clientX,
    y: btnRect.bottom - e.clientY
  }
  // 添加鼠标移动和释放事件监听
  document.addEventListener('mousemove', handleDragMove)
  document.addEventListener('mouseup', handleDragEnd)
  e.preventDefault() // 防止拖动时选中文本
}

// 对话框拖拽开始
const handleDialogDragStart = (e) => {
  if (e.target.closest('.ai-header')) {
    isDragging.value = true
    isDraggingFloatBtn.value = false
    // 计算鼠标相对于对话框右上角的偏移量
    const dialogRect = e.target.closest('.ai-assistant-dialog').getBoundingClientRect()
    dragOffset.value = {
      x: dialogRect.right - e.clientX,
      y: dialogRect.bottom - e.clientY
    }
    // 添加鼠标移动和释放事件监听
    document.addEventListener('mousemove', handleDragMove)
    document.addEventListener('mouseup', handleDragEnd)
    e.preventDefault() // 防止拖动时选中文本
  }
}

// 拖拽移动
const handleDragMove = (e) => {
  if (isDragging.value) {
    // 更新悬浮按钮位置（相对于视口右下角的偏移量）
    floatBtnPosition.value = {
      right: e.clientX + dragOffset.value.x > window.innerWidth ? 32 : window.innerWidth - e.clientX - dragOffset.value.x,
      bottom: e.clientY + dragOffset.value.y > window.innerHeight ? 32 : window.innerHeight - e.clientY - dragOffset.value.y
    }
    
    // 限制位置在合理范围内
    floatBtnPosition.value.right = Math.max(20, Math.min(floatBtnPosition.value.right, window.innerWidth - 100))
    floatBtnPosition.value.bottom = Math.max(20, Math.min(floatBtnPosition.value.bottom, window.innerHeight - 100))
  }
}

// 拖拽结束
const handleDragEnd = () => {
  isDragging.value = false
  isDraggingFloatBtn.value = false
  // 移除鼠标事件监听
  document.removeEventListener('mousemove', handleDragMove)
  document.removeEventListener('mouseup', handleDragEnd)
}

// 发送消息
function send() {
  console.log('send方法被调用')
  if (!input.value.trim()) return
  messages.value.push({ role: 'user', text: input.value })
  const question = input.value
  input.value = ''
  console.log('准备请求AI')
  callChatMemory(question, 'taikao-user')
    .then(reply => {
      console.log('请求已发出')
      console.log('AI回复内容:', reply) // 调试输出AI回复
      messages.value.push({ role: 'ai', text: reply })
      console.log('messages数组:', messages.value) // 输出所有消息
      nextTick(() => {
        if (chatBody.value) chatBody.value.scrollTop = chatBody.value.scrollHeight
      })
    })
    .catch(err => {
      console.log('请求失败', err)
    })
}

// 组件挂载时添加滚动监听
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  // 初始化位置
  handleScroll()
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('mousemove', handleDragMove)
  document.removeEventListener('mouseup', handleDragEnd)
})
</script>

<style scoped>
.ai-assistant-float-btn {
  position: fixed;
  right: 32px;
  bottom: 32px;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  padding: 8px 16px;
  display: flex;
  align-items: center;
  cursor: move;
  z-index: 9999;
  transition: box-shadow 0.2s;
  will-change: transform;
}
.ai-assistant-float-btn:active {
  cursor: grabbing;
}
.ai-assistant-float-btn:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.18);
}
.ai-logo {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}
.ai-assistant-dialog {
  position: fixed;
  right: 32px;
  bottom: 32px;
  width: 360px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  z-index: 10000;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: fadeIn 0.3s;
  will-change: transform;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(40px);}
  to { opacity: 1; transform: translateY(0);}
}
.ai-header {
  display: flex;
  align-items: center;
  padding: 16px;
  background: linear-gradient(90deg,#4f8cff 0%,#a084fa 100%);
  color: #fff;
  font-size: 16px;
  position: relative;
}
.ai-title {
  flex: 1;
  font-weight: bold;
  margin-left: 8px;
}
.ai-close {
  background: none;
  border: none;
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  position: absolute;
  right: 16px;
  top: 16px;
}
.ai-body {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f7f8fa;
  min-height: 180px;
  max-height: 320px;
}
.ai-msg {
  margin-bottom: 12px;
  word-break: break-all;
}
.ai-msg.user {
  text-align: right;
  color: #333;
}
.ai-msg.ai {
  text-align: left;
  color: #4f8cff;
}
.ai-footer {
  display: flex;
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #eee;
}
.ai-input {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px;
  font-size: 15px;
  outline: none;
}
.ai-send {
  margin-left: 8px;
  background: #4f8cff;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}
.ai-send:hover {
  background: #3a6fd8;
}
</style>
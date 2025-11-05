<template>
  <div class="ai-assistant-container">
    <!-- 悬浮按钮 -->
    <transition name="fade">
      <button v-if="!isOpen" @click="toggleChat" class="assistant-fab">
        <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="currentColor"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/></svg>
        <span>钛考AI助手</span>
      </button>
    </transition>

    <!-- 聊天对话框 -->
    <transition name="slide-up">
      <div v-if="isOpen" class="chat-dialog">
        <!-- 头部 -->
        <div class="chat-header">
          <h3>钛考AI助手</h3>
          <button @click="toggleChat" class="close-btn">&times;</button>
        </div>

        <!-- 聊天记录 -->
        <div class="chat-body" ref="chatBodyRef">
          <div v-for="(msg, index) in messages" :key="index" class="message-wrapper" :class="msg.sender">
            <div class="chat-bubble">
              <p v-html="msg.text"></p>
            </div>
          </div>
          <div v-if="isLoading" class="message-wrapper ai">
            <div class="chat-bubble">
              <span class="loading-dots">
                <span>.</span><span>.</span><span>.</span>
              </span>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-footer">
          <input
            type="text"
            v-model="newMessage"
            @keyup.enter="sendMessage"
            placeholder="请输入你的问题..."
            class="chat-input"
          />
          <button @click="sendMessage" class="send-btn">发送</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { postChatMessage } from '@/api/ai'; // 引入 AI API 请求
import { getUserInfo } from '@/api/user'; // 引入获取用户信息的 API

// --- 响应式状态 ---
const isOpen = ref(false); // 控制对话框的显示与隐藏
const newMessage = ref(''); // 绑定输入框内容
const isLoading = ref(false); // 控制 AI 回复加载状态
const chatBodyRef = ref(null); // 用于访问聊天记录DOM，实现自动滚动
const currentUserId = ref(null); // 存储当前用户ID

// 聊天消息列表，包含初始问候语
const messages = ref([
  { sender: 'ai', text: '你好，我是钛考AI助手，可以为你提供帮助。' }
]);

// --- 方法 ---

// 切换聊天框的显示状态
const toggleChat = () => {
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    scrollToBottom();
  }
};

// 自动滚动到聊天记录底部
const scrollToBottom = async () => {
  await nextTick(); // 等待 DOM 更新
  const chatBody = chatBodyRef.value;
  if (chatBody) {
    chatBody.scrollTop = chatBody.scrollHeight;
  }
};

// 发送消息
const sendMessage = async () => {
  const messageText = newMessage.value.trim();
  if (!messageText || isLoading.value) return;

  // 1. 将用户消息添加到列表
  messages.value.push({ sender: 'user', text: messageText });
  newMessage.value = '';
  isLoading.value = true;
  scrollToBottom();

  try {
    // 2. 调用后端 API，使用获取到的用户ID
    if (!currentUserId.value) {
      throw new Error('无法获取用户ID');
    }
    const response = await postChatMessage({ userId: currentUserId.value, message: messageText });

    // 3. 将 AI 的回复添加到列表
    messages.value.push({ sender: 'ai', text: response.data.reply });

  } catch (error) {
    console.error('AI request failed:', error);
    messages.value.push({ sender: 'ai', text: '抱歉，我暂时无法回答你的问题，请稍后再试。' });
  } finally {
    isLoading.value = false;
    scrollToBottom();
  }
};

// 组件挂载后，获取用户ID
onMounted(async () => {
  if (isOpen.value) {
    scrollToBottom();
  }
  try {
    const res = await getUserInfo();
    if (res.data && res.data.id) {
      currentUserId.value = res.data.id;
    } else {
      console.error('获取用户ID失败: ', res);
    }
  } catch (error) {
    console.error('请求用户信息失败: ', error);
  }
});
</script>

<style scoped>
/* --- 容器和定位 --- */
.ai-assistant-container {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  z-index: 1000;
}

/* --- 悬浮按钮 --- */
.assistant-fab {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #409EFF; /* Element Plus 主题色 */
  color: white;
  border: none;
  border-radius: 50px;
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}
.assistant-fab:hover {
  background-color: #79bbff;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}
.assistant-fab .icon {
  width: 1.5rem;
  height: 1.5rem;
  margin-right: 0.5rem;
}

/* --- 对话框 --- */
.chat-dialog {
  width: 380px;
  height: 550px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* --- 头部 --- */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background-color: #f7f8fa;
  border-bottom: 1px solid #e8e8e8;
}
.chat-header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
}
.close-btn {
  background: none;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: #999;
  padding: 0;
  line-height: 1;
}
.close-btn:hover {
  color: #333;
}

/* --- 聊天记录 --- */
.chat-body {
  flex-grow: 1;
  padding: 1rem;
  overflow-y: auto;
  background-color: #f9f9f9;
}
.message-wrapper {
  display: flex;
  margin-bottom: 1rem;
}
.message-wrapper.user {
  justify-content: flex-end;
}
.message-wrapper.ai {
  justify-content: flex-start;
}
.chat-bubble {
  max-width: 80%;
  padding: 0.8rem 1rem;
  border-radius: 12px;
  word-wrap: break-word;
}
.chat-bubble p {
  margin: 0;
  line-height: 1.5;
}
.message-wrapper.user .chat-bubble {
  background-color: #d9ecff; /* 浅蓝色 */
  border-top-right-radius: 4px;
}
.message-wrapper.ai .chat-bubble {
  background-color: #f1f1f1; /* 灰白色 */
  border-top-left-radius: 4px;
}

/* --- 输入区域 --- */
.chat-footer {
  display: flex;
  padding: 1rem;
  border-top: 1px solid #e8e8e8;
  background-color: #fff;
}
.chat-input {
  flex-grow: 1;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 0.7rem 1rem;
  font-size: 1rem;
  margin-right: 0.5rem;
  outline: none;
  transition: border-color 0.2s;
}
.chat-input:focus {
  border-color: #409EFF;
}
.send-btn {
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 0.7rem 1.5rem;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}
.send-btn:hover {
  background-color: #79bbff;
}

/* --- 加载动画 --- */
.loading-dots span {
  display: inline-block;
  animation: blink 1.4s infinite both;
}
.loading-dots span:nth-child(2) {
  animation-delay: 0.2s;
}
.loading-dots span:nth-child(3) {
  animation-delay: 0.4s;
}
@keyframes blink {
  0%, 80%, 100% { opacity: 0; }
  40% { opacity: 1; }
}

/* --- 过渡动画 --- */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.slide-up-enter-from, .slide-up-leave-to {
  transform: translateY(30px);
  opacity: 0;
}
</style>

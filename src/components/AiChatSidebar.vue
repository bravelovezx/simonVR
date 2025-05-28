<template>
  <transition name="slide">
    <div v-if="showSidebar" class="ai-chat-sidebar" :class="{ open: isOpen }">
      <button class="toggle-btn" @click="toggleOpen">
        <transition name="icon-trans" mode="out-in">
          <div v-if="isOpen" key="close" class="toggle-icon">➔</div>
          <div v-else key="open" class="toggle-label">
            <span class="ai-icon"></span>
            <span class="label-text">Ask AI</span>
          </div>
        </transition>
      </button>
      <div v-if="isOpen" class="sidebar-content">
        <div class="chat-header">
          <div class="header-content">
            <span class="ai-avatar">✨</span>
            <h3>AI英语学习助手</h3>
            <span class="online-dot"></span>
          </div>
        </div>
        <div class="chat-body">
          <div class="chat-content">
            <div class="message ai-message">
              <div class="message-bubble">您好！我是您的AI助手，有什么可以帮您？</div>
            </div>
          </div>
        </div>
        <div class="chat-footer">
          <div class="input-container">
            <input 
              class="chat-input" 
              placeholder="向AI提问..." 
              @keyup.enter="handleSend"
            />
            <button class="send-btn">
              <span class="send-icon">✈️</span>
            </button>
          </div>
          <p class="tip-text">支持Markdown语法</p>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const isOpen = ref(false)
const route = useRoute()
const showSidebar = computed(() =>
  ['/home', '/reading', '/writing', '/collection', '/chat'].includes(route.path)
)
const toggleOpen = () => { isOpen.value = !isOpen.value }
</script>

<style scoped>
/* 颜色变量 */
:root {
  --primary-color: #7c3aed;
  --primary-light: #a78bfa;
  --bg-color: #f8fafc;
  --text-color: #1e293b;
}

.ai-chat-sidebar {
  position: fixed;
  top: 80px;
  right: 0;
  height: calc(100% - 80px);
  z-index: 9999;
  display: flex;
  filter: drop-shadow(2px 0 8px rgba(0,0,0,0.08));
}

.toggle-btn {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #251d1d;
  border: none;
  border-radius: 8px 0 0 8px;
  padding: 12px 6px;
  cursor: pointer;
  margin-top: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: -2px 0 8px rgba(0,0,0,0.1);
  height: 100px;
  align-self: center;
}

.toggle-btn:hover {
  transform: translateX(-2px);
  box-shadow: -4px 0 12px rgba(0,0,0,0.15);
}

.toggle-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.ai-icon {
  font-size: 1.5rem;
}

.label-text {
  font-size: 0.85rem;
  writing-mode: vertical-rl;
  transform: rotate(180deg);
  
}

.sidebar-content {
  width: 360px;
  height: 100%;
  background: var(--bg-color);
  display: flex;
  flex-direction: column;
  border-radius: 12px 0 0 0;
  backdrop-filter: blur(4px);
}

.chat-header {
  padding: 16px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: rgb(23, 16, 16);
  border-radius: 12px 0 0 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar {
  background: white;
  color: var(--primary-color);
  padding: 8px;
  border-radius: 50%;
  font-size: 1.2rem;
}

.online-dot {
  width: 8px;
  height: 8px;
  background: #4ade80;
  border-radius: 50%;
  margin-left: auto;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: rgba(255,255,255,0.9);
}

.message {
  margin-bottom: 16px;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  max-width: 80%;
  line-height: 1.5;
}

.ai-message .message-bubble {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px 16px 16px 4px;
}

.chat-footer {
  padding: 16px;
  border-top: 1px solid #e2e8f0;
  background: white;
}

.input-container {
  position: relative;
  display: flex;
  gap: 8px;
}

.chat-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  background: white;
  transition: all 0.3s;
}

.chat-input:focus {
  outline: none;
  border-color: var(--primary-light);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.2);
}

.send-btn {
  /* background: var(--primary-color);
   */
  
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s;
}

.send-btn:hover {
  transform: translateY(-2px);
}

.tip-text {
  color: #64748b;
  font-size: 0.75rem;
  text-align: center;
  margin-top: 8px;
}

/* 动画优化 */
.slide-enter-active, .slide-leave-active {
  transition: all 0.3s cubic-bezier(0.68, -0.55, 0.27, 1.55);
}

.slide-enter-from, .slide-leave-to {
  transform: translateX(120%);
  opacity: 0;
}

.icon-trans-enter-active, .icon-trans-leave-active {
  transition: all 0.2s;
}

.icon-trans-enter-from, .icon-trans-leave-to {
  opacity: 0;
  transform: rotate(90deg);
}

@media (max-width: 480px) {
  .sidebar-content {
    width: 100vw;
  }
}
</style>
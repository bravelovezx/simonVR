<template>
  <transition name="slide">
    <div v-if="showSidebar" class="ai-chat-sidebar" :class="{ open: isOpen }">
      <button class="toggle-btn" @click="toggleOpen">
        {{ isOpen ? '→' : 'AI助手' }}
      </button>
      <div v-if="isOpen" class="sidebar-content">
        <div class="chat-header">AI 聊天助手</div>
        <div class="chat-body">
          <!-- 聊天内容区 -->
          <div class="chat-content">AI聊天内容区</div>
        </div>
        <input class="chat-input" placeholder="向AI提问..." />
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
.ai-chat-sidebar {
  position: fixed;
  top: 80px;
  right: 0;
  height: calc(100% - 80px);
  z-index: 9999;
  display: flex;
  align-items: flex-start;
}
.toggle-btn {
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 4px 0 0 4px;
  padding: 10px 16px;
  cursor: pointer;
  margin-top: 20px;
}
.sidebar-content {
  width: 320px;
  height: 100%;
  background: #fff;
  border-left: 1px solid #eee;
  box-shadow: -2px 0 8px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
}
.chat-header {
  padding: 16px;
  font-weight: bold;
  border-bottom: 1px solid #eee;
  background: #f5f7fa;
}
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}
.chat-content {
  min-height: 200px;
}
.chat-input {
  border: none;
  border-top: 1px solid #eee;
  padding: 10px;
  width: 100%;
  outline: none;
  background: #fafbfc;
}
.slide-enter-active, .slide-leave-active {
  transition: all 0.3s;
}
.slide-enter-from, .slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>
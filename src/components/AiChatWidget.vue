<template>
  <div v-if="showWidget" class="ai-chat-widget" :class="{ open: isOpen }">
    <button class="toggle-btn" @click="toggleOpen">
      {{ isOpen ? '关闭AI助手' : 'AI助手' }}
    </button>
    <div v-if="isOpen" class="chat-window">
      <!-- 聊天内容和输入框 -->
      <div class="chat-content">AI聊天内容区</div>
      <input class="chat-input" placeholder="向AI提问..." />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const isOpen = ref(false)
const route = useRoute()
const showWidget = computed(() =>
  ['/home', '/reading', '/writing', '/collection', '/chat'].includes(route.path)
)
const toggleOpen = () => { isOpen.value = !isOpen.value }
</script>

<style scoped>
.ai-chat-widget {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 9999;
}
.toggle-btn {
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  cursor: pointer;
}
.chat-window {
  width: 320px;
  height: 400px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-top: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
}
.chat-content {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
}
.chat-input {
  border: none;
  border-top: 1px solid #eee;
  padding: 10px;
  width: 100%;
  outline: none;
}
</style>
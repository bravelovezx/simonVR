<template>
  <el-container direction="vertical" style="min-height: 100vh;">
    <Header />
    <el-main class="main-content">
      <router-view />
    </el-main>
     <AiChatWidget />
  </el-container>
</template>

<script setup>
import Header from '@/components/Header.vue';
import AiChatWidget from '@/components/AiChatWidget.vue'; // 引入AI聊天组件
import AiChatSidebar from '@/components/AiChatSidebar.vue';
</script>

<style scoped>
.main-content {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
<template>
  <el-container direction="vertical" style="min-height: 100vh;">
    <Header />
    <el-main class="main-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import Header from '@/components/Header.vue';
</script>

<style scoped>
.main-content {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
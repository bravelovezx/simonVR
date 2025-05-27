<template>
  <el-header class="header">
    <div class="header-left">
      <h2 class="site-name">📚 阅读笔记平台</h2>
      <el-menu
        :default-active="$route.path"
        mode="horizontal"
        background-color="#ffffff"
        text-color="#333"
        active-text-color="#1890ff"
        router
      >
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/reading">阅读</el-menu-item>
        <el-menu-item index="/writing">写作</el-menu-item>
        <el-menu-item index="/collection">积累</el-menu-item>
        <!-- <el-menu-item index="/chat">聊天</el-menu-item> -->
        <el-menu-item index="/chat">对话</el-menu-item>
      </el-menu>
    </div>

    <div class="header-right">
      <template v-if="isLoggedIn">
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            👤 用户名<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <template #dropdown>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <el-button plain @click="$router.push({ name: 'login' })">登录</el-button>
        <el-button type="primary" @click="$router.push({ name: 'register' })">注册</el-button>
      </template>
    </div>
  </el-header>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const isLoggedIn = ref(true); // 模拟登录状态，可改为真实逻辑

const handleCommand = (command) => {
  if (command === 'logout') {
    isLoggedIn.value = false;
    ElMessage.success('已退出');
  } else if (command === 'profile') {
    router.push('/profile'); // 假设存在 profile 页面
  }
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  flex-grow: 1; /* 新增 */
  min-width: 0; /* 新增：允许内容截断 */
}

.site-name {
  margin-right: 20px;
  color: #1890ff;
  white-space: nowrap; /* 新增：防止标题换行 */
}

/* 新增菜单容器样式 */
.header-left .el-menu {
  flex-grow: 1;
  min-width: 0; /* 重要：允许菜单项压缩 */
  overflow: hidden; /* 隐藏原生更多菜单 */
}

/* 强制显示所有菜单项 */
.header-left .el-menu :deep(.el-menu-item) {
  flex-shrink: 0;
}

.header-right .el-button {
  margin-left: 10px;
}
</style>
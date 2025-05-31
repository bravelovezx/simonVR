<template>
  <el-header class="header">
    <div class="header-left">
      <h2 class="site-name">Simon AI+英语学习平台</h2>
      <el-menu
        :default-active="$route.path"
        mode="horizontal"
        router
        :ellipsis="false"
      >
        <el-menu-item index="/home"><el-icon><HomeFilled /></el-icon> 首页</el-menu-item>
        <el-menu-item index="/reading"><el-icon><Memo /></el-icon> 阅读</el-menu-item>
        <el-menu-item index="/writing"><el-icon><EditPen /></el-icon> 写作</el-menu-item>
        <el-menu-item index="/chat"><el-icon><ChatLineRound /></el-icon> 对话</el-menu-item>
        <!-- <el-menu-item index="/collection"><el-icon><Notebook /></el-icon> 积累</el-menu-item> -->
        <el-sub-menu index="ai-features">
          <template #title>
            <el-icon><Notebook /></el-icon> 积累
          </template>
          <el-menu-item index="/vocabulary"><el-icon class="ai-icon"><Collection /></el-icon>词汇积累</el-menu-item>
          <el-menu-item index="/sentence"><el-icon class="ai-icon"><Document /></el-icon>句子积累</el-menu-item>
        </el-sub-menu>
        <!-- <el-menu-item index="/chat">聊天</el-menu-item> -->
        <!--  -->
      </el-menu>
    </div>


  <div class="header-right">
    <template v-if="isLoggedIn">
      <!-- 使用 el-dropdown 包裹头像 -->
      <el-dropdown @command="handleCommand" trigger="hover">
        <el-avatar
          :src=testavator
          size="medium"
          style="cursor: pointer;"
        ></el-avatar>

        <!-- 下拉菜单内容 -->
        <template #dropdown>
          <el-dropdown-item command="profile"><el-icon><User /></el-icon> 个人中心</el-dropdown-item>
          <el-dropdown-item command="logout"><el-icon><DArrowRight /></el-icon> 退出登录</el-dropdown-item>
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
import testavator from '../assets/ai.png';
import { useUserStore } from '@/store/uesr';
import { ElNotification } from 'element-plus'
const userStore = useUserStore();

const router = useRouter();
const isLoggedIn = ref(true); // 模拟登录状态，可改为真实逻辑

const handleCommand = (command) => {
  if (command === 'logout') {
    isLoggedIn.value = false;
    userStore.logout(); // 调用用户存储的登出方法
    ElNotification({
      title: '退出登录成功',
      message: '欢迎再次使用！',
      type: 'success',
      duration: 3000
    });
    router.push('/login'); // 跳转到登录页面
    // ElMessage.success('已退出');
  } else if (command === 'profile') {
    router.push('/profile'); // 假设存在 profile 页面
  }
};
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index:1000;
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

.header-right .el-avatar {
  vertical-align: middle;
  border: 1px solid #ddd;
  transition: all 0.2s ease-in-out;
}

.header-right .el-avatar:hover {
  border-color: #409EFF;
  box-shadow: 0 0 5px rgba(64, 158, 255, 0.5);
}
</style>
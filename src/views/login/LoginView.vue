<script setup>
import { ref } from 'vue';
import qs from 'qs';
import { useRouter } from 'vue-router';
import { ElForm, ElFormItem, ElInput, ElButton, ElCard, ElIcon, ElLink } from 'element-plus';
import request from '@/utils/request';
import { ElNotification } from 'element-plus'
import axios from 'axios';
import { useUserStore } from '@/store/uesr';
const userStore = useUserStore();
// const useStore = userStore();
const router = useRouter();
const formData = ref({
  username: '',
  password: ''
});
const loading = ref(false);



const handleLogin = async () => {
  loading.value = true;
  try {
    const response=await request.post('/api/users/login', {
      username: formData.value.username,
      password: formData.value.password
    },{
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    // 调用登录API
  //   console.log('登录数据:', formData.value);
  //   const response = await request.post('/api/users/login',  headers: {
  //   'Content-Type': 'application/x-www-form-urlencoded'
  // },qs.stringify(formData));
    console.log(response)
    
    // 登录成功后保存token
    // localStorage.setItem('token', response.data.token);
    ElNotification({
      title: '登录成功',
      message: '欢迎回来！',
      type: 'success',
      duration: 1500
    });
    userStore.login(formData.value.username, response.token);
    
    // 跳转到首页
    router.push('/');
  } catch (error) {
    console.error('登录失败:', error);
    loading.value = false;
    ElNotification({
      title: '登录失败',
      message: error.response?.data?.message || '请检查用户名和密码',
      type: 'error',
      duration: 1500
    });
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-container">
    <!-- 新增动态背景容器 -->
    <div class="decorative-bg">
      <div class="ai-wave"></div>
      <div class="glowing-orb"></div>
    </div>
    
    <el-card class="login-card">
      <!-- 增加品牌LOGO区 -->
      <div class="branding">
        <!-- <img src="@/assets/ai-logo.png" class="logo" alt="AI English"> -->
        <h2 class="slogan">Smart Learning, Global Communication</h2>
      </div>
      
      <h2 class="login-title">Welcome Back! 👋</h2>
      <el-form>
        <!-- 增加输入图标 -->
        <el-form-item label="账号" prop="username">
          <el-input v-model="formData.username" placeholder="">
            <template #prefix>
              <el-icon><user /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码 " prop="password">
          <el-input v-model="formData.password" type="password" placeholder="">
            <template #prefix>
              <el-icon><lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 美化按钮 -->
        <el-button 
          class="login-btn"
          native-type="submit"
          :loading="loading"
          @click="handleLogin"
        >
          <span class="btn-text">Unlock Your Potential</span>
          <el-icon class="icon-arrow"><arrow-right /></el-icon>
        </el-button>

        <!-- 社交登录
        <div class="social-login">
          <span class="divider">Or continue with</span>
          <div class="social-icons">
            <el-icon class="icon-google"><google /></el-icon>
            <el-icon class="icon-apple"><apple /></el-icon>
          </div>
        </div> -->

        <div class="register-link">
          New to AI English? 
          <el-link type="primary" @click="router.push('/register')">Start Journey Now →</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  position: relative;
  overflow: hidden;

  /* 动态背景特效 */
  .decorative-bg {
    .ai-wave {
      position: absolute;
      bottom: -50px;
      left: 0;
      right: 0;
      height: 100px;
      background: url('~@/assets/wave-pattern.svg') repeat-x;
      animation: waveAnim 20s linear infinite;
    }

    .glowing-orb {
      position: absolute;
      top: 20%;
      right: 10%;
      width: 200px;
      height: 200px;
      background: radial-gradient(circle, rgba(99,102,241,0.2) 0%, transparent 70%);
      filter: blur(60px);
      z-index: 0;
    }
  }
}

.login-card {
  width: 440px;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 12px 32px rgba(0,0,0,0.1);
  position: relative;
  z-index: 1;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(8px);
  
  .branding {
    text-align: center;
    margin-bottom: 32px;
    
    .logo {
      width: 120px;
      margin-bottom: 16px;
    }
    
    .slogan {
      font-size: 14px;
      color: #6b7280;
      letter-spacing: 0.5px;
      font-weight: 500;
    }
  }
}

.login-title {
  text-align: center;
  margin: 24px 0;
  font-size: 26px;
  background: linear-gradient(45deg, #6366f1, #8b5cf6);
  -webkit-background-clip: text;
  color: transparent;
  letter-spacing: -0.5px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151 !important;
  margin-bottom: 8px !important;
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(45deg, #6366f1, #8b5cf6);
  border: none;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(99,102,241,0.2);
    
    .btn-text {
      letter-spacing: 0.8px;
    }
    
    .icon-arrow {
      transform: translateX(3px);
    }
  }
  
  .btn-text {
    font-weight: 600;
    transition: letter-spacing 0.3s ease;
  }
  
  .icon-arrow {
    margin-left: 8px;
    transition: transform 0.3s ease;
  }
}

.social-login {
  margin: 32px 0;
  text-align: center;
  
  .divider {
    color: #6b7280;
    font-size: 14px;
    position: relative;
    
    &::before, &::after {
      content: '';
      position: absolute;
      top: 50%;
      width: 100px;
      height: 1px;
      background: #e5e7eb;
    }
    
    &::before { left: -110px; }
    &::after { right: -110px; }
  }
  
  .social-icons {
    margin-top: 20px;
    
    .el-icon {
      width: 40px;
      height: 40px;
      margin: 0 12px;
      padding: 8px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &.icon-google {
        color: #db4437;
        border: 1px solid #e5e7eb;
        
        &:hover {
          background: rgba(219,68,55,0.1);
        }
      }
      
      &.icon-apple {
        color: #000;
        border: 1px solid #e5e7eb;
        
        &:hover {
          background: rgba(0,0,0,0.05);
        }
      }
    }
  }
}

.register-link {
  text-align: center;
  color: #6b7280;
  margin-top: 24px;
  
  .el-link {
    font-weight: 600;
    margin-left: 8px;
  }
}

@keyframes waveAnim {
  0% { background-position-x: 0; }
  100% { background-position-x: 1000px; }
}
</style>

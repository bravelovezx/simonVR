<template>
  <div class="accumulate-container">
    <!-- 标题部分 -->
    <div class="header-section">
      <h2 class="ai-title">
        <i class="iconfont el-icon-ai" /> 
        AI智能阅读系统
      </h2>
      <p class="sub-title">让AI为你的英语学习注入智能动力</p>
    </div>

    <!-- 功能卡片区域 -->
    <div class="card-wrapper">
      <el-row :gutter="30" justify="center">
        <!-- 词汇积累卡片 -->
        <el-col :xs="24" :sm="12" :md="10" :lg="8">
          <el-card 
            class="ai-card vocabulary-card" 
            shadow="hover"
            @click="router.push('/reading')"
          >
            <div class="card-content">
              <div class="icon-box bg-blue">
                <el-icon class="ai-icon"><Reading /></el-icon>
              </div>
              <h3 class="card-title">智能阅读</h3>
              <p class="card-desc">
                AI根据你的学习进度推荐个性化词汇<br>
                结合记忆曲线智能复习
              </p>
              <div class="ai-tag">AI PRO</div>
            </div>
          </el-card>
        </el-col>

        <!-- 句子积累卡片 -->
        <!-- <el-col :xs="24" :sm="12" :md="10" :lg="8">
          <el-card 
            class="ai-card sentence-card" 
            shadow="hover"
            @click="router.push('/sentence')"
          >
            <div class="card-content">
              <div class="icon-box bg-purple">
                <el-icon class="ai-icon"><Document /></el-icon>
              </div>
              <h3 class="card-title">智能句子积累</h3>
              <p class="card-desc">
                AI生成情景化例句<br>
                智能分析语法结构
              </p>
              <div class="ai-tag">AI PRO</div>
            </div>
          </el-card>
        </el-col> -->
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Collection, Document } from '@element-plus/icons-vue'

const router = useRouter()

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped lang="scss">
.accumulate-container {
  padding: 40px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  min-height: 100vh;
}

.header-section {
  text-align: center;
  margin-bottom: 50px;

  .ai-title {
    font-size: 2.5rem;
    color: #2c3e50;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
  }

  .sub-title {
    color: #7f8c8d;
    font-size: 1.1rem;
  }
}

.card-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.ai-card {
  margin: 20px 0;
  border-radius: 15px;
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
  cursor: pointer;
  border: none;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1) !important;

    .icon-box {
      transform: rotate(10deg) scale(1.1);
    }
  }

  .card-content {
    padding: 30px;
    text-align: center;
    position: relative;
  }

  .icon-box {
    width: 80px;
    height: 80px;
    border-radius: 20px;
    margin: 0 auto 25px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;

    .ai-icon {
      font-size: 40px;
      color: white;
    }

    &.bg-blue {
      background: linear-gradient(45deg, #4a90e2, #5fa3f5);
    }

    &.bg-purple {
      background: linear-gradient(45deg, #a855f7, #c084fc);
    }
  }

  .card-title {
    color: #2c3e50;
    font-size: 1.5rem;
    margin-bottom: 15px;
  }

  .card-desc {
    color: #7f8c8d;
    line-height: 1.8;
    margin-bottom: 25px;
  }

  .ai-tag {
    position: absolute;
    top: -12px;
    right: -12px;
    background: linear-gradient(45deg, #4a90e2, #5fa3f5);
    color: white;
    padding: 6px 15px;
    border-radius: 20px;
    font-size: 0.85rem;
    box-shadow: 0 2px 8px rgba(74, 144, 226, 0.3);
  }
}

@media (max-width: 768px) {
  .header-section .ai-title {
    font-size: 2rem;
  }
  
  .ai-card {
    margin: 10px 0;
  }
}
</style>

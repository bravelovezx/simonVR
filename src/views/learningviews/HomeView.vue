<template>
  <div class="home-container">
    <!-- 科技感波浪背景 -->
    <div class="wave-background"></div>

    <!-- 核心功能展示区 -->
    <el-main class="main-content">
      <!-- 欢迎标语 -->
      <div class="hero-section">
        <h1 class="gradient-text">Smart English Learning with AI</h1>
        <p class="subtitle">开启您的个性化英语学习之旅</p>
        <div class="stats-container">
          <el-statistic 
            title="今日活跃学习者" 
            :value="3856" 
            animation
          />
          <el-statistic 
            title="AI生成练习" 
            :value="12892" 
            animation
          />
        </div>
      </div>

      <!-- 功能卡片网格 -->
      <div class="feature-grid">
        <el-row :gutter="30">
          <el-col 
            v-for="(feature, index) in features"
            :key="index"
            :xs="24" 
            :sm="12" 
            :md="6"
          >
            <el-card 
              class="feature-card"
              :style="{ '--hover-color': feature.color }"
              shadow="hover"
              @click="navigateTo(feature.path)"
            >
              <div class="card-icon">
                <component 
                  :is="feature.icon" 
                  class="animated-icon"
                />
              </div>
              <h3 class="card-title">{{ feature.title }}</h3>
              <p class="card-desc">{{ feature.desc }}</p>
              <el-button 
                type="primary" 
                plain 
                class="enter-btn"
              >
                立即体验 →
              </el-button>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- AI特色展示 -->
      <div class="ai-showcase">
        <el-divider content-position="left">
          <span class="highlight-text">AI智能赋能</span>
        </el-divider>
        <div class="ai-features">
          <el-carousel 
            height="400px" 
            indicator-position="outside"
          >
            <el-carousel-item 
              v-for="(item, index) in aiFeatures" 
              :key="index"
            >
              <div class="carousel-content">
                <div class="text-content">
                  <h2>{{ item.title }}</h2>
                  <ul class="feature-list">
                    <li 
                      v-for="(point, i) in item.points" 
                      :key="i"
                    >
                      <el-icon><Select /></el-icon>
                      {{ point }}
                    </li>
                  </ul>
                </div>
                <el-image 
                  class="ai-image"
                  :src="item.image"
                  fit="contain"
                />
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Notebook, EditPen, ChatLineRound, Reading,
  MagicStick, DataAnalysis, Place
} from '@element-plus/icons-vue'

const router = useRouter()

const features = ref([
  {
    title: '智能阅读',
    desc: 'AI辅助理解，实时词汇解析',
    icon: Reading,
    color: '#1890ff',
    path: '/introreading'
  },
  {
    title: 'AI写作',
    desc: '智能语法纠错，写作风格优化',
    icon: EditPen,
    color: '#7c4dff',
    path: '/introwriting'
  },

  {
    title: '虚拟对话',
    desc: '情景化口语练习，发音实时评分',
    icon: ChatLineRound,
    color: '#ff9100',
    path: '/introchat'
  },
    {
    title: '知识积累',
    desc: '个性化词库管理，记忆曲线提醒',
    icon: Notebook,
    color: '#00c853',
    path: '/collection'
  },
])

const aiFeatures = ref([
  {
    title: '智能学习引擎',
    points: [
      '自然语言处理驱动',
      '个性化学习路径规划',
      '实时进度追踪分析'
    ],
    image: 'https://example.com/ai-engine.png'
  },
  {
    title: '沉浸式学习体验',
    points: [
      '虚拟情景对话模拟',
      '多模态学习材料',
      '跨平台学习同步'
    ],
    image: 'https://example.com/immerse.png'
  }
])

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.home-container {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.wave-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 600px;
  background: linear-gradient(160deg, #44b8f6 0%, #926ff2 100%);
  clip-path: ellipse(80% 100% at 50% 0%);
}

.hero-section {
  text-align: center;
  padding: 80px 0 40px;
  position: relative;
  z-index: 1;
}

.gradient-text {
  background: linear-gradient(45deg, #1890ff, #7c4dff);
  -webkit-background-clip: text;
  background-clip: text;
  color: white;
  font-size: 3.5rem;
  margin-bottom: 20px;
}

.subtitle {
  color: #F5F7FA;
  font-size: 1.7rem;
  margin-bottom: 40px;
}

.stats-container {
  display: flex;
  justify-content: center;
  gap: 50px;
  margin-top: 30px;
}

.feature-grid {
  padding: 40px 0;
  position: relative;
  z-index: 2;
}

.feature-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  border: none;
  margin-bottom: 30px;
  background: rgba(255,255,255,0.9);
  
  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.1);
    
    .animated-icon {
      transform: rotate(15deg) scale(1.1);
      color: var(--hover-color);
    }
  }
}

.card-icon {
  font-size: 3rem;
  margin: 20px 0;
  color: #1890ff;
  transition: color 0.3s;
}

.card-title {
  color: #333;
  margin: 15px 0;
}

.card-desc {
  color: #666;
  min-height: 60px;
}

.enter-btn {
  margin-top: 15px;
  transition: all 0.3s;
  
  &:hover {
    letter-spacing: 1px;
  }
}

.ai-showcase {
  margin-top: 60px;
  padding: 40px 0;
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.carousel-content {
  display: flex;
  align-items: center;
  padding: 0 50px;
  height: 100%;
}

.text-content {
  flex: 1;
  padding-right: 50px;
}

.feature-list {
  list-style: none;
  padding: 0;
  
  li {
    font-size: 1.1rem;
    margin: 15px 0;
    display: flex;
    align-items: center;
    
    .el-icon {
      color: #1890ff;
      margin-right: 10px;
    }
  }
}

.ai-image {
  flex: 1;
  height: 350px;
}

.highlight-text {
  font-size: 1.5rem;
  background: linear-gradient(45deg, #1890ff, #7c4dff);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

@media (max-width: 768px) {
  .carousel-content {
    flex-direction: column;
    padding: 20px;
  }
  
  .ai-image {
    height: 200px;
    margin-top: 20px;
  }
  
  .gradient-text {
    font-size: 1.8rem;
  }
}
</style>

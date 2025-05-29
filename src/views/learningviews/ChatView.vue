<template>
  <div class="chat-view">
    <!-- 左侧：历史对话列表 -->
    <el-aside class="chat-history" width="300px">
      <!-- <div class="history-title">历史对话</div>
        -->
      <el-header class="history-title">
        <h3>📁 历史对话</h3>
      </el-header>
      <!-- <el-scrollbar> -->
        <el-menu 
          :default-active="selectedIdx.toString()"
          class="history-list"
          @select="selectHistory"
        >
          <el-menu-item 
            v-for="(item, idx) in history"
            :key="item.id"
            :index="idx.toString()"
            :class="{ 'is-active': idx === selectedIdx }"
          >
            <el-icon><avatar /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </el-menu>
      <!-- </el-scrollbar> -->
    </el-aside>

    <!-- 右侧：对话详情 -->
    <el-main class="chat-detail">
      <div class="chat-header">
        <el-avatar :size="40" :src="currentHistory.avatar" class="mr-3" />
        <div class="text-xl font-bold">{{ currentHistory.title }}</div>
      </div>
      
      <el-scrollbar class="chat-messages">
        <div 
          v-for="(msg, idx) in currentHistory.messages"
          :key="idx"
          class="message-item"
          :class="msg.role"
        >
          <div class="message-wrapper">
            <el-avatar 
              :size="36" 
              :src="msg.role === 'me' ? myAvatar : currentHistory.avatar"
              class="avatar"
            />
            <div class="message-content">
              <div class="username">{{ msg.role === 'me' ? '我' : currentHistory.title }}</div>
              <div class="bubble-wrapper">
                <div class="bubble">
                  <div class="text">{{ msg.content }}</div>
                  <div class="actions">
                    <el-button 
                      type="primary" 
                      :icon="EditPen" 
                      circle 
                      size="small"
                      @click="editMessage(idx)"
                    />
                    <el-button 
                      type="success" 
                      :icon="Star" 
                      circle 
                      size="small"
                      @click="addToCollection(idx)"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-scrollbar>
    </el-main>
  </div>
</template>

<script setup>
import myAvatarImg from '@/assets/ai.png'
import defaultAvatarImg from '@/assets/green.png'
import { ref, computed } from 'vue'
import { Avatar, EditPen, Star } from '@element-plus/icons-vue'

// 模拟头像地址
const myAvatar = myAvatarImg
const defaultAvatar = defaultAvatarImg
const currentHistory = computed(() => history.value[selectedIdx.value] || { messages: [], avatar: '', title: '' })
const selectedIdx = ref(0)

const history = ref([
  {
    id: 1,
    title: '小明',
    avatar: defaultAvatar,
    messages: [
      { role: 'me', content: '你好，小明！' },
      { role: 'other', content: '你好呀！' },
      { role: 'me', content: '最近怎么样？' },
      { role: 'other', content: '挺好的，你呢？' }
    ]
  },
  // ...其他数据
])


function selectHistory(idx) {
  selectedIdx.value = Number(idx)
}

function editMessage(idx) {
  // 这里只是入口
  alert('润色功能入口，句子索引：' + idx)
}

function addToCollection(idx) {
  // 这里只是入口
  alert('添加到积累功能入口，句子索引：' + idx)
}
// ...原有脚本逻辑
</script>

<style scoped>
.chat-view {
  display: flex;
  height: 100vh;
  background: #f5f6f7;
}

.chat-history {
  background: #ffffff;
  border-right: 1px solid #ebeef5;
  
  .history-title {
      padding: 15px 20px;
      background-color: #565C63;
      border-bottom: 1px solid #e4e7ed;
  }
  .history-title h3 {
    margin: 0;
    font-size: 16px;
    color: #ffffff;
  }
  
  :deep(.el-menu-item) {
    height: 64px;
    display: flex;
    align-items: center;
    
    &.is-active {
      background-color: #f0f7ff;
      color: #409eff;
    }
  }
}

.chat-detail {
  padding: 0;
  display: flex;
  flex-direction: column;
  
  .chat-header {
    padding: 16px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    background-color: #F2F2F2;
  }
}

.message-item {
  padding: 12px 20px;
  
  &.me {
    .message-wrapper {
      flex-direction: row-reverse;
    }
    
    .bubble {
      background: #409eff;
      color: white;
      
      .actions {
        right: unset;
        left: 0;
      }
    }
  }
}

.message-wrapper {
  display: flex;
  max-width: 80%;
  margin: 8px 0;
}

.avatar {
  flex-shrink: 0;
}

.message-content {
  margin: 0 12px;
  
  .username {
    font-size: 12px;
    color: #909399;
    margin: 4px 8px;
  }
}

.bubble {
  position: relative;
  background: #f0f0f0;
  border-radius: 8px;
  padding: 12px;
  min-width: 60px;
  transition: all 0.2s;
  
  .text {
    line-height: 1.5;
    word-break: break-word;
  }
  
  .actions {
    position: absolute;
    right: 0;
    top: -24px;
    display: none;
    gap: 4px;
    background: white;
    padding: 4px;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
  
  &:hover .actions {
    display: flex;
  }
}
</style>

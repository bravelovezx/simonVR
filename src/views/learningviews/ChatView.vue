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
            v-for="(item, idx) in ChatHistory"
            :key="item.id"
            :index="idx.toString()"
            :class="{ 'is-active': idx === selectedIdx }"
            @click=getChatContent(item.sessionId,idx)
          >
            <el-icon><List /></el-icon>
            <span>{{ item.scene }}</span>
            <!-- 用 div 包住标签，实现换行 -->
            <!-- <div style="margin-top: 4px;">
              <el-tag type="info">{{ item.startedAt }}</el-tag>
            </div> -->
          </el-menu-item>
        </el-menu>
      <!-- </el-scrollbar> -->
    </el-aside>

    <!-- 右侧：对话详情 -->
    <el-main class="chat-detail">
      <div class="chat-header">
        对话场景：
        <div class="text-xl font-bold">{{ currentHistory.scene }}</div>
      </div>
      
      <el-scrollbar class="chat-messages">
        <div 
          v-for="(msg, idx) in currentHistory.messages"
          :key="idx"
          class="message-item"
          :class="msg.speaker"
        >
          <div class="message-wrapper">
            <el-avatar 
              :size="36" 
              :src="msg.speaker === 'ai' ? defaultAvatarImg : myAvatarImg"
              class="avatar"
            />
            <div class="message-content">
              <div class="username">{{ msg.speaker}}</div>
              <div class="bubble-wrapper">
                <div class="bubble">
                  <div class="text">{{ msg.rawText }}</div>
                  <div style="font-family:'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif ;font-weight: 300;">{{ msg.correctedText }}</div>
                  <!-- <div class="actions">
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
                  </div> -->
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
import defaultAvatarImg from '@/assets/logo.png'
import { ref, computed,onMounted } from 'vue'
import { Avatar, EditPen, Star } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
// import { my } from 'element-plus/es/locale'

// 模拟头像地址
const myAvatar = myAvatarImg
const defaultAvatar = defaultAvatarImg
const currentHistory = computed(() => ChatHistory.value[selectedIdx.value] || { messages: [], avatar: '', title: '' })
const selectedIdx = ref(0)

// const ChatHistory=ref([])

const ChatHistory = ref([
{
        "sessionId": 1,
        "userId": 5,
        "scene": "cafe",
        "startedAt": "2025-05-30 10:37:15",
        "endedAt": "2025-05-30 10:37:20"
    },
  // ...其他数据
])


function selectHistory(idx) {
  selectedIdx.value = Number(idx)
}

function editMessage(idx) {
    ElMessageBox.prompt('请输入你的批注', 'Tip', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        inputValidator: function (value) {
          if (!value || value.trim() === '') {
            return 'Input cannot be empty'
          }
          return true
        },
      })
        .then(function(result) {
          ElMessage({
            type: 'success',
            message: '已添加到批注 ✅'
          })
        })
        .catch(function() {
          ElMessage({
            type: 'info',
            message: '取消批注',
          })
        })
}

function addToCollection(idx) {
  // 这里只是入口
  alert('添加到积累功能入口，句子索引：' + idx)
}

onMounted(async () => {
  // 从服务器获取历史对话
  const response = await request.get('/api/dialogues/sessions')
  
  // 为每个会话对象添加一个空的 messages 数组
  ChatHistory.value = response.map(session => ({
    ...session,
    messages: [
         
    ]
  }))
  console.log(ChatHistory.value)

  getChatContent(ChatHistory.value[selectedIdx.value].sessionId, selectedIdx.value)
})


const getChatContent = async(sessionId,idx) => {
  // selectedIdx.value = idx
  // 这里可以添加获取具体聊天内容的逻辑
  console.log('选中对话sessionId:', sessionId)
  const response=await request.get(`/api/dialogues/sessions/${sessionId}/turns`)
  console.log('获取到的对话内容:', response)
  ChatHistory.value[idx].messages = response.map(turn => ({
    turnId: turn.turnId,
    sessionId: turn.sessionId,
    turnNumber: turn.turnNumber,
    speaker: turn.speaker,
    rawText: turn.rawText,
    correctedText: turn.correctedText,
    createdAt: turn.createdAt
  }))
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
  
  &.user {
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

<template>
  <div class="chat-view">
    <!-- 左侧：历史对话列表 -->
    <el-aside class="chat-history" width="300px">
      <el-header class="history-title">
        <h3>📁 历史对话</h3>
      </el-header>
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
          @click="getChatContent(item.sessionId, idx)"
        >
          <el-icon><List /></el-icon>
          <span>{{ item.scene }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧：对话详情 -->
    <el-main class="chat-detail">
      <div class="chat-header">
        对话场景：
        <div class="text-xl font-bold">{{ currentHistory.scene }}</div>
      </div>
      
      <el-scrollbar class="chat-messages" ref="messagesScrollbar">
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
                <!-- 修复：使用div包裹文本内容并添加鼠标事件 -->
                <div 
                  class="bubble"
                  @mouseup="handleTextSelect($event, idx)"
                >
                  <div class="text">{{ msg.rawText }}</div>
                  <div style="font-family:'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif ;font-weight: 300;">{{ msg.correctedText }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 选中文本的浮窗 -->
        <div 
          v-if="showPopup" 
          class="selection-popup"
          :style="{ top: popupPosition.top + 'px', left: popupPosition.left + 'px' }"
        >
          <el-button plain type="primary" size="small" @click="translateSelectedText">翻译</el-button>
          <el-button plain type="success" size="small" @click="collectSelectedText">积累</el-button>
        </div>
      </el-scrollbar>
    </el-main>

    <!-- 翻译弹窗 -->
    <el-dialog 
      v-model="showTranslateDialog" 
      title="翻译结果" 
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="translate-result">
        <p><strong>原文:</strong> {{ selectedText }}</p>
        <p><strong>翻译:</strong> {{ translatedText }}</p>
      </div>
      <template #footer>
        <el-button @click="showTranslateDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 积累弹窗 -->
    <el-dialog 
      v-model="showCollectDialog" 
      title="添加到积累" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="collectForm">
        <el-form-item label="原文">
          <el-input v-model="selectedText" type="textarea" :rows="2" disabled />
        </el-form-item>
        <el-form-item label="释义">
          <el-input v-model="collectForm.notes" type="textarea" :rows="3" placeholder="添加释义..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCollectDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmCollect">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import myAvatarImg from '@/assets/ai.png'
import defaultAvatarImg from '@/assets/logo.png'
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 模拟头像地址
const myAvatar = myAvatarImg
const defaultAvatar = defaultAvatarImg
const currentHistory = computed(() => ChatHistory.value[selectedIdx.value] || { messages: [], avatar: '', title: '' })
const selectedIdx = ref(0)
const messagesScrollbar = ref(null)

const ChatHistory = ref([
  {
    "sessionId": 1,
    "userId": 5,
    "scene": "cafe",
    "startedAt": "2025-05-30 10:37:15",
    "endedAt": "2025-05-30 10:37:20"
  }
])

// 选中文本相关状态
const showPopup = ref(false)
const popupPosition = ref({ top: 0, left: 0 })
const selectedText = ref('')
const selectedMessageIndex = ref(-1)

// 翻译相关状态
const showTranslateDialog = ref(false)
const translatedText = ref('')

// 积累相关状态
const showCollectDialog = ref(false)
const collectForm = ref({
  notes: '',
  tags: []
})

function selectHistory(idx) {
  selectedIdx.value = Number(idx)
}

onMounted(async () => {
  // 从服务器获取历史对话
  const response = await request.get('/api/dialogues/sessions')
  
  // 为每个会话对象添加一个空的 messages 数组
  ChatHistory.value = response.map(session => ({
    ...session,
    messages: []
  }))
  
  getChatContent(ChatHistory.value[selectedIdx.value].sessionId, selectedIdx.value)
  
  // 添加全局点击事件监听，用于关闭浮窗
  document.addEventListener('mousedown', handleGlobalClick)
})

onBeforeUnmount(() => {
  // 移除全局点击事件监听
  document.removeEventListener('mousedown', handleGlobalClick)
})

const getChatContent = async(sessionId, idx) => {
  const response = await request.get(`/api/dialogues/sessions/${sessionId}/turns`)
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

// 修复：处理文本选中事件
const handleTextSelect = (event, messageIndex) => {
  // 获取选中的文本
  const selection = window.getSelection()
  const text = selection.toString().trim()
  
  if (text) {
    selectedText.value = text
    selectedMessageIndex.value = messageIndex
    
    // 获取文本的边界矩形
    const range = selection.getRangeAt(0)
    const rect = range.getBoundingClientRect()
    
    // 计算浮窗位置（使用鼠标事件位置）
    popupPosition.value = {
      top: event.clientY + 10, // 鼠标位置下方10px
      left: event.clientX
    }
    
    // 显示浮窗
    showPopup.value = true
    console.log('显示浮窗', popupPosition.value, '选中文本:', text)
    
    // 阻止事件冒泡，防止触发全局点击事件
    event.stopPropagation()
  } else {
    showPopup.value = false
  }
}

// 修复：处理全局点击事件
const handleGlobalClick = (event) => {
  // 如果点击的不是浮窗本身，则关闭浮窗
  if (showPopup.value && !event.target.closest('.selection-popup')) {
    showPopup.value = false
  }
}

// 翻译选中文本
const translateSelectedText = async () => {
  try {
    // 发送翻译请求
    const response = await request.post('/api/translate/translate', {
      q: selectedText.value,
      from: 'auto',
      to: 'auto',
      vocabId: 1 // 可以根据实际情况调整
    })
    
    translatedText.value = response.translation[0]
    showTranslateDialog.value = true
    showPopup.value = false
  } catch (error) {
    ElMessage.error('翻译失败，请稍后再试')
    console.error('翻译错误:', error)
  }
}

// 积累选中文本
const collectSelectedText = () => {
  showCollectDialog.value = true
  showPopup.value = false
}

// 确认添加积累
const confirmCollect = async () => {
  try {
    // 判断选中文本是否包含空格，决定类型是word还是sentence
    const type = selectedText.value.includes(' ') ? 'sentence' : 'word'
    
    // 发送请求将内容添加到积累
    await request.post('/api/accumulations', {
      type, // 根据是否包含空格动态设置类型
      content: selectedText.value,
      meaning: collectForm.value.notes,
      position: {
        module: "dialogue",
        refId: currentHistory.value.sessionId || 1,
        startPos: selectedMessageIndex.value,
        endPos: 100000
      }
    })
    
    ElMessage.success('已添加到积累')
    showCollectDialog.value = false
    collectForm.value = { notes: '', tags: [] }
  } catch (error) {
    ElMessage.error('添加失败，请稍后再试')
    console.error('添加积累错误:', error)
  }
}
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
  cursor: text;
  user-select: text;
  
  .text {
    line-height: 1.5;
    word-break: break-word;
  }
}

/* 修复：选中文本的浮窗样式 */
.selection-popup {
  position: fixed;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  padding: 6px;
  z-index: 9999;
  display: flex;
  gap: 8px;
  transform: translateX(-50%);
}

/* 翻译结果样式 */
.translate-result {
  line-height: 1.8;
  
  p {
    margin-bottom: 10px;
  }
}

/* 修复：确保聊天消息容器可以定位 */
.chat-messages {
  position: relative;
  height: calc(100vh - 60px);
}
</style>
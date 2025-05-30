<template>
  <div class="collection-container">
    <!-- 分类导航 -->
    <el-tabs v-model="activeTab" class="smart-tabs">
      <el-tab-pane label="词汇库" name="vocabulary">
        <!-- 词汇操作区 -->
        <div class="action-bar">
          <el-button type="primary" @click="openWordDialog(null)">
            <el-icon><CirclePlus /></el-icon> 添加新词
          </el-button>
          <el-input 
            v-model="searchWord" 
            placeholder="搜索词汇/释义" 
            clearable 
            class="search-input"
            @input="filterItems"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 词汇表格 -->
        <el-table 
          :data="filteredWords" 
          stripe 
          style="width: 100%"
          class="smart-table"
        >
          <el-table-column prop="word" label="单词" sortable>
            <template #default="{ row }">
              <div class="word-cell">
                <span class="word">{{ row.word }}</span>
                <span class="phonetic">/{{ row.phonetic }}/</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="translation" label="释义" width="200" />
          <el-table-column label="标签" width="180">
            <template #default="{ row }">
              <el-tag 
                v-for="tag in row.tags" 
                :key="tag" 
                type="info" 
                size="small"
                class="mr-1"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="添加时间" sortable width="140" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button 
                link 
                type="primary" 
                @click="openWordDialog(row)"
              >
                编辑
              </el-button>
              <el-popconfirm 
                title="确认删除该词汇？" 
                @confirm="deleteWord(row.id)"
              >
                <template #reference>
                  <el-button link type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      
    </el-tabs>

    <!-- 词汇编辑对话框 -->
    <word-dialog 
      v-model="wordDialogVisible"
      :current-word="currentWord"
      @refresh="loadWords"
    />
  </div>
</template>

<script setup>
import { ref, computed,watch } from 'vue'
import { Clock, Document, Search, CirclePlus } from '@element-plus/icons-vue'
import WordDialog from '@/components/WordDialog.vue'
// import SentenceDialog from './SentenceDialog.vue'

const activeTab = ref('vocabulary')

// 词汇相关逻辑
const searchWord = ref('')
const wordList = ref([])
const filteredWords = computed(() => {
  return wordList.value.filter(item => 
    item.word.toLowerCase().includes(searchWord.value.toLowerCase()) ||
    item.translation.toLowerCase().includes(searchWord.value.toLowerCase())
  )
})

// 对话框控制
const wordDialogVisible = ref(false)
const currentWord = ref(null)
const sentenceDialogVisible = ref(false)
const currentSentence = ref(null)

// 方法示例
const openWordDialog = (word) => {
  currentWord.value = word ? { ...word } : null
  wordDialogVisible.value = true
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleDateString()
}

// 此处应添加数据加载、筛选、API交互等逻辑
</script>

<style scoped>
.collection-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.action-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.search-input {
  max-width: 300px;
}

.smart-table {
  --el-table-header-bg-color: #f8f9fa;
  --el-table-row-hover-bg-color: #f8fafe;
}

.word-cell {
  display: flex;
  flex-direction: column;
}
.word {
  font-weight: 600;
  font-size: 1.1em;
}
.phonetic {
  color: #666;
  font-size: 0.9em;
}

.sentence-grid {
  margin-top: 15px;
}

.sentence-card {
  margin-bottom: 20px;
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-3px);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sentence-text {
  font-size: 1.1em;
  line-height: 1.5;
}

.translation {
  color: #666;
  font-size: 0.95em;
  margin: 10px 0;
}

.meta-info {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 0.85em;
  margin: 12px 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.filter-select {
  width: 200px;
  margin-left: auto;
}

.smart-tabs {
  :deep(.el-tabs__header) {
    margin: 0 0 20px;
  }
  
  :deep(.el-tabs__nav-wrap)::after {
    height: 1px;
    background-color: var(--el-border-color-light);
  }
}
</style>

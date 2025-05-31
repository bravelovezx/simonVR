<template>
  <div class="collection-container">
    <el-tabs v-model="activeTab" class="smart-tabs">
      <!-- 原有词汇库标签页 -->

      <!-- 新增句子库标签页 -->
      <el-tab-pane label="句子积累" name="sentences">
        <!-- 操作栏 -->
        <div class="action-bar">
          <el-button type="primary" @click="openSentenceDialog(null)">
            <el-icon><DocumentAdd /></el-icon> 添加句子
          </el-button>
          <div class="filter-group">
            <el-input
              v-model="searchQuery"
              placeholder="搜索句子/翻译"
              clearable
              class="search-input"
              style="width: 260px"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select 
              v-model="filterSource" 
              placeholder="筛选来源" 
              clearable
              style="width: 180px"
            >
              <el-option
                v-for="source in uniqueSources"
                :key="source"
                :label="source"
                :value="source"
              />
            </el-select>
          </div>
        </div>

        <!-- 句子卡片列表 -->
        <div class="sentence-list">
          <el-card 
            v-for="sentence in filteredSentences" 
            :key="sentence.id"
            class="sentence-card"
          >
            <template #header>
              <div class="card-header">
                <span class="sentence-source">
                  《{{ sentence.source.title }}》 - {{ sentence.source.author }}
                  <el-link 
                    :href="sentence.source.url" 
                    target="_blank" 
                    type="info" 
                    :underline="false"
                    style="margin-left: 8px; font-size: 12px"
                  >
                    查看原文
                  </el-link>
                </span>
                <div class="card-actions">
                  <el-button 
                    type="primary" 
                    link 
                    @click="openSentenceDialog(sentence)"
                  >
                    编辑
                  </el-button>
                  <el-popconfirm 
                    title="确认删除该句子？" 
                    @confirm="deleteSentence(sentence.id)"
                  >
                    <template #reference>
                      <el-button type="danger" link>删除</el-button>
                    </template>
                  </el-popconfirm>
                </div>
              </div>
            </template>

            <!-- 句子内容 -->
            <div class="sentence-content">
              <div class="sentence-text">{{ sentence.sentence }}</div>
              <div class="sentence-translation">{{ sentence.translation }}</div>
              
              <!-- 批注展示 -->
              <div class="annotations">
                <div 
                  v-for="annotation in sentence.annotations"
                  :key="annotation.id"
                  class="annotation-item"
                >
                  <div class="annotation-header">
                    <el-tag 
                      :type="annotationTypeMap[annotation.type] || 'info'" 
                      size="small"
                    >
                      {{ annotation.type }}
                    </el-tag>
                    <span class="annotation-time">
                      {{ formatTime(annotation.createTime) }}
                    </span>
                  </div>
                  <div class="annotation-content">{{ annotation.content }}</div>
                </div>
              </div>

              <!-- 添加新批注 -->
              <div class="new-annotation">
                <el-input
                  v-model="newAnnotationContent"
                  placeholder="添加新批注..."
                  type="textarea"
                  :rows="2"
                  class="annotation-input"
                />
                <div class="annotation-actions">
                  <el-select 
                    v-model="newAnnotationType" 
                    placeholder="选择类型" 
                    size="small"
                    style="width: 120px"
                  >
                    <el-option
                      v-for="type in annotationTypes"
                      :key="type.value"
                      :label="type.label"
                      :value="type.value"
                    />
                  </el-select>
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="addAnnotation(sentence)"
                  >
                    添加批注
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 句子编辑对话框 -->
    <sentence-dialog 
      v-model="sentenceDialogVisible"
      :current-sentence="currentSentence"
      @refresh="loadSentences"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search, DocumentAdd } from '@element-plus/icons-vue'
import { ElTabs, ElTabPane, ElButton, ElInput, ElSelect, ElOption, ElCard, ElTag, ElLink, ElPopconfirm } from 'element-plus'
import { ElIcon } from 'element-plus'

import SentenceDialog from '@/components/SentenceDialog.vue'
const activeTab = ref('sentences')

// 示例数据
const sentences = ref([
  {
    id: 1,
    sentence: "The only way to do great work is to love what you do.",
    translation: "成就一番伟业的唯一途径就是热爱自己的事业。",
    source: {
      title: "Steve Jobs' Stanford Commencement Speech",
      author: "Steve Jobs",
      url: "https://example.com/speech",
      date: "2005-06-12"
    },
    annotations: [
      {
        id: 1,
        type: "哲理",
        content: "强调热情对工作成就的重要性",
        createTime: 1677826800000
      },
      {
        id: 2,
        type: "语法",
        content: "不定式作表语的结构：The way to do... is to...",
        createTime: 1677913200000
      }
    ],
    createTime: 1677826800000
  },
  {
    id: 2,
    sentence: "In the middle of difficulty lies opportunity.",
    translation: "困难中孕育着机会。",
    source: {
      title: "爱因斯坦文集",
      author: "Albert Einstein",
      url: "https://example.com/einstein",
      date: "1938"
    },
    annotations: [
      {
        id: 3,
        type: "结构分析",
        content: "倒装句结构，强调opportunity",
        createTime: 1678086000000
      }
    ],
    createTime: 1678000000000
  }
])

// 批注相关逻辑
const annotationTypes = [
  { label: '词汇解析', value: '词汇' },
  { label: '语法分析', value: '语法' },
  { label: '文化背景', value: '文化' },
  { label: '个人感悟', value: '感悟' }
]

const annotationTypeMap = {
  '词汇': 'success',
  '语法': 'warning',
  '文化': 'danger',
  '感悟': 'info'
}

const newAnnotationContent = ref('')
const newAnnotationType = ref('词汇')

// 搜索和过滤
const searchQuery = ref('')
const filterSource = ref('')
const uniqueSources = computed(() => [...new Set(sentences.value.map(s => s.source.title))])

const filteredSentences = computed(() => {
  return sentences.value.filter(s => {
    const matchSearch = s.sentence.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      s.translation.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchSource = filterSource.value ? s.source.title === filterSource.value : true
    return matchSearch && matchSource
  })
})

// 操作方法
const addAnnotation = (sentence) => {
  if (!newAnnotationContent.value) return
  
  sentence.annotations.push({
    id: Date.now(),
    type: newAnnotationType.value,
    content: newAnnotationContent.value,
    createTime: Date.now()
  })
  newAnnotationContent.value = ''
}

const deleteSentence = (id) => {
  sentences.value = sentences.value.filter(s => s.id !== id)
}

// 时间格式化（复用词汇库的函数）
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')}`
}

// 对话框控制（需要创建SentenceDialog组件）
const sentenceDialogVisible = ref(false)
const currentSentence = ref(null)

const openSentenceDialog = (sentence) => {
  currentSentence.value = sentence ? { ...sentence } : null
  sentenceDialogVisible.value = true
}
</script>

<style scoped>
.sentence-list {
  display: grid;
  gap: 16px;
}

.sentence-card {
  margin-bottom: 16px;
  transition: box-shadow 0.3s;
  
  &:hover {
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9em;
  color: #666;
}

.sentence-content {
  line-height: 1.6;
}

.sentence-text {
  font-size: 1.1em;
  margin-bottom: 8px;
  color: #333;
}

.sentence-translation {
  color: #666;
  border-left: 3px solid #eee;
  padding-left: 12px;
  margin: 12px 0;
}

.annotations {
  margin: 16px 0;
  border-top: 1px solid #eee;
  padding-top: 12px;
}

.annotation-item {
  margin: 8px 0;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
}

.annotation-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.annotation-time {
  font-size: 0.8em;
  color: #999;
}

.new-annotation {
  margin-top: 16px;
}

.annotation-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.filter-group {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>

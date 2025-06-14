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
            <!-- <el-select 
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
            </el-select> -->
          </div>
        </div>

        <!-- 句子卡片列表 -->
        <div class="sentence-list">
          <el-card 
            v-for="sentence in filteredSentences" 
            :key="sentence.accumulationId"
            class="sentence-card"
          >
            <template #header>
              <div class="card-header">
                <!-- <span class="sentence-source">
                   - 位置：
                  {{ sentence.position.row }} - {{ sentence.position.column }}
                  <el-link 
                    target="_blank" 
                    type="info" 
                    :underline="false"
                    style="margin-left: 8px; font-size: 12px"
                  >
                    查看原文
                  </el-link>
                </span> -->
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
                    @confirm="deleteSentence(sentence.accumulationId)"
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
              <div class="sentence-text">{{ sentence.content }}</div>
              <div class="sentence-translation">{{ sentence.meaning}}</div>
              
              <!-- 批注展示 -->
              <!-- <div class="annotations">
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
              </div> -->

              <!-- 添加新批注 -->
              <!-- <div class="new-annotation">
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
              </div> -->
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
import { ref, computed,onMounted } from 'vue'
import { Search, DocumentAdd } from '@element-plus/icons-vue'
import { ElTabs, ElTabPane, ElButton, ElInput, ElSelect, ElOption, ElCard, ElTag, ElLink, ElPopconfirm } from 'element-plus'
import { ElIcon } from 'element-plus'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

import SentenceDialog from '@/components/SentenceDialog.vue'
const activeTab = ref('sentences')

// 示例数据
const sentences = ref([
    {
        "createdAt": "2025-06-01 18:13:49",
        "updatedAt": "2025-06-01 18:13:51",
        "accumulationId": 7,
        "userId": 5,
        "type": "sentence",
        "content": "password",
        "meaning": "密码",
        "position": {
            "module": "reading",
            "refId": 99,
            "row": 99,
            "column": 99
        }
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
// const uniqueSources = computed(() => [...new Set(sentences.value.map(s => s.source.title))])

const filteredSentences = computed(() => {
  return sentences.value.filter(s => {
    const matchSearch = s.content.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      s.meaning.toLowerCase().includes(searchQuery.value.toLowerCase())
    // const matchSource = filterSource.value ? s.source.title === filterSource.value : true
    return matchSearch
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

const deleteSentence = async(accumulationId) => {
    const response=await request.delete(`/api/accumulations/${accumulationId}`)
  if(response.success){
    ElMessage.success('句子删除成功')
  }else{
    ElMessage.error('句子删除失败')
  }
  getUserSentences()
}

// 时间格式化（复用词汇库的函数）
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')}`
}

const loadSentences=async()=>{
  getUserSentences()
}

// 对话框控制（需要创建SentenceDialog组件）
const sentenceDialogVisible = ref(false)
const currentSentence = ref(null)

const openSentenceDialog = (sentence) => {
  currentSentence.value = sentence ? { ...sentence } : null
  sentenceDialogVisible.value = true
}

const getUserSentences = async() => {
  try{
      const response=await request.get('/api/accumulations/my/type/sentence')
      console.log(response)
      if(response.success){
          sentences.value = response.data || []
          console.log('获取用户句子成功:', sentences.value)
      }else{
          ElMessage.error('获取用户句子失败，请稍后重试')
      }
      // 模拟获取用户句子数据
  }catch(error){
      console.error('获取用户句子失败:', error)
      ElMessage.error('获取用户句子失败，请稍后重试')
  }

  // return sentences.value
}


onMounted(()=>{
  getUserSentences()
})

</script>

<style scoped>
.collection-container {
  padding: 20px;
  background: #f9f9f9;
}

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

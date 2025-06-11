<template>
  <el-container class="main-container">
    <!-- 左侧文章列表 -->

    <el-aside width="300px" class="article-list">
      <el-header class="aside-header" >
        <h3>📁 文章列表</h3>
      </el-header>
      <el-menu :default-active="activeArticle" @select="handleSelectArticle">
        <el-menu-item 
          v-for="article in articles" 
          :key="article.id" 
          :index="article.id.toString()"
        >
          <span>{{ article.title }}</span>
          <el-tag v-if="article.isCollected" type="warning" size="small" style="margin-left: 5px;">已收藏</el-tag>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧文章内容 -->
    <el-main class="article-content">
      <div 
        v-if="currentArticle" 
        class="content-box"
        @mouseup="handleTextSelection"
      >
        <h2>{{ currentArticle.title }}</h2>
        <pre class="article-body">{{ currentArticle.content }}</pre>
        
        <!-- 浮动操作工具栏 -->
        <div 
          v-if="showToolbar" 
          class="selection-toolbar"
          :style="{ left: toolbarPos.x + 'px', top: toolbarPos.y + 'px' }"
        >
          <el-button-group>
            <el-button size="small" @click="handleLookup">查词</el-button>
            <el-button size="small" @click="showAnnotationDialog = true">批注</el-button>
            <el-button 
              size="small" 
              :type="isCollected ? 'warning' : ''"
              @click="toggleCollect"
            >
              {{ isCollected ? '已收藏' : '收藏' }}
            </el-button>
            <!-- <el-button size="small" @click="showToolbar=false">取消显示</el-button> -->
          </el-button-group>
        </div>
      </div>

      <!-- 查询对话框 -->
       <el-dialog v-model="showLookupDialog" :title="`查词：${selectedText}`" width="40%">
        <div v-if="isFetching" style="text-align: center;">
          <el-spinner />
          <p>正在查询...</p>
        </div>
      
        <div v-else-if="lookupResult">
          <h4>📘 单词释义</h4>
          <p><strong>发音：</strong>{{ lookupResult.phonetic || '暂无' }}</p>
        
          <div v-for="(meaning, index) in lookupResult.meanings" :key="index">
            <h5>👉 {{ meaning.partOfSpeech }}</h5>
            <ul>
              <li v-for="(def, i) in meaning.definitions" :key="i">
                {{ def.definition }}
                <br>
                <em v-if="def.example">例句：{{ def.example }}</em>
              </li>
            </ul>
          </div>
        
          <h4>🌐 中文翻译</h4>
          <p>{{ lookupResult.translation || '暂无翻译' }}</p>
        </div>
      
        <div v-else>
          <p>暂无查询结果</p>
        </div>
      
        <template #footer>
          <el-button type="primary" @click="showLookupDialog = false">积累</el-button>
          <el-button @click="showLookupDialog = false">关闭</el-button>
        </template>
      </el-dialog>

      <!-- 批注对话框 -->
      <el-dialog v-model="showAnnotationDialog" title="添加批注" width="30%" >
        <blockquote style="margin: 10px 0; padding: 10px; background-color: #f9f9f9;">
          {{ selectedText }}
        </blockquote>
        <el-input
          v-model="annotationText"
          type="textarea"
          :rows="4"
          placeholder="请输入批注内容"
          
        />
        <template #footer>
          <el-button @click="showAnnotationDialog = false">取消</el-button>
          <el-button type="primary" @click="saveAnnotation">保存</el-button>
        </template>
      </el-dialog>

      <!-- 积累对话框 -->
      <el-dialog v-model="showCollectDialog" title="收藏并积累" width="30%">
        <p>您正在收藏以下内容：</p>
        <blockquote style="margin: 10px 0; padding: 10px; background-color: #f9f9f9;">
          {{ selectedText }}
        </blockquote>
      
        <el-input
          v-model="collectMeaning"
          type="textarea"
          :rows="3"
          placeholder="请输入该词/句的意思或用法"
        />
      
        <template #footer>
          <el-button @click="showCollectDialog = false">取消</el-button>
          <el-button type="primary" @click="saveToCollection">积累</el-button>
        </template>
      </el-dialog>


    </el-main>
  </el-container>
</template>

<script setup>
import { ref, reactive,onMounted,onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const isSelecting = ref(false)
// 文章示例数据
const articles = ref([
  {
    id: 1,
    title: 'Life Is Wonderful',
    content: `Face your past without regret.
Handle your present with confidence.
Prepare for the future without fear.
Keep faith and drop the fear.
Don't believe your doubts and never doubt your beliefs.
Life is wonderful if you know how to live it.`,
    isCollected: false,
    annotations: []
  },
  {
    id: 2,
    title: 'Modern Technology Development',
    content: 'Recent advancements in AI have revolutionized...',
    isCollected: true,
    annotations: []
  }
])

// 当前选中文章
const currentArticle = ref(articles.value[0])
const activeArticle = ref('1')

// 文本选择相关
const showToolbar = ref(false)
const toolbarPos = reactive({ x: 0, y: 0 })
const selectedText = ref('')

// 批注相关
const showAnnotationDialog = ref(false)
const annotationText = ref('')

// 收藏状态
const isCollected = ref(false)

// 文章选择处理


// 全局点击事件处理
const handleClickOutside = (e) => {
  if (!isSelecting.value) {
    showToolbar.value = false
  }
  isSelecting.value = false
}

// 监听 document 点击
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

// 移除监听
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})


const handleSelectArticle = (index) => {
  currentArticle.value = articles.value.find(a => a.id === Number(index))
  isCollected.value = currentArticle.value.isCollected
}




// 文本选择处理
const handleTextSelection = (e) => {
  const selection = window.getSelection()
  const selected = selection.toString().trim()

  if (!selected) return

  selectedText.value = selected
  toolbarPos.x = e.clientX
  toolbarPos.y = e.clientY - 40
  showToolbar.value = true
  isSelecting.value = true // 标记为正在选择
}

// 查词功能

const lookupResult=ref(null)
const showLookupDialog=ref(false)//显示查词弹窗
const isFetching=ref(false)//是否正在请求

const handleLookup = async() => {
  console.log('查询单词:', selectedText.value)
  if (!selectedText.value.trim()) return
  isFetching.value = true
  lookupResult.value = null
  showLookupDialog.value = true
  // alert(`查询单词: ${selectedText.value}`)
  try {
    const response = await request.post('/api/lookup', {
      text: selectedText.value
    })

    lookupResult.value = response.data
  } catch (error) {
    ElMessage.error('查询失败，请稍后再试')
    console.error('查词失败:', error)
  } finally {
    isFetching.value = false
  }
  showToolbar.value = false
}

// 收藏功能

const showCollectDialog = ref(false) // 是否显示收藏对话框
const collectMeaning = ref('')       // 用户输入的释义
const toggleCollect = () => {
  if (isCollected.value) {
    // 已收藏 → 取消收藏（可选）
    isCollected.value = false
    currentArticle.value.isCollected = false
    showToolbar.value = false
    return
  }

  // 未收藏 → 弹出对话框让用户输入释义
  showCollectDialog.value = true
}

const saveToCollection = () => {
  const meaning = collectMeaning.value.trim()
  const text = selectedText.value.trim()

  if (!text || !meaning) {
    ElMessage.warning('请填写完整内容')
    return
  }

  // 将选中内容和释义保存到当前文章的 collectedItems 数组中
  currentArticle.value.collectedItems.push({
    text,
    meaning,
    timestamp: new Date().toISOString()
  })

  // 更新收藏状态
  isCollected.value = true
  currentArticle.value.isCollected = true

  // 关闭对话框
  showCollectDialog.value = false
  collectMeaning.value = ''
  showToolbar.value = false

  ElMessage.success('已成功积累')
}



// 保存批注
const saveAnnotation = () => {
  if (annotationText.value.trim()) {
    currentArticle.value.annotations.push({
      text: annotationText.value,
      selection: selectedText.value,
      timestamp: new Date().toISOString()
    })
    showAnnotationDialog.value = false
    annotationText.value = ''
    ElMessage({
    message: '添加批注成功',
    type: 'success',
    plain: true,
  })
}
}
</script>

<style scoped>

.aside-header {
  padding: 15px 20px;
  background-color: #565C63;
  border-bottom: 1px solid #e4e7ed;
}

.aside-header h3 {
  margin: 0;
  font-size: 16px;
  color: #ffffff;
}
.main-container {
  height: 100vh;
}

.article-list {
  border-right: 1px solid #eee;
}

.article-content {
  position: relative;
  padding: 20px;
}

.content-box {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
}

.article-body {
  white-space: pre-wrap;
  line-height: 1.6;
  font-family: 'Arial', sans-serif;
}

.selection-toolbar {
  position: fixed;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  border-radius: 4px;
  padding: 4px;
  z-index: 1000;
}
</style>

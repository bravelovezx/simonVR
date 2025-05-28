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
            <el-button size="small" @click="showToolbar=false">取消显示</el-button>
          </el-button-group>
        </div>
      </div>

      <!-- 批注对话框 -->
      <el-dialog v-model="showAnnotationDialog" title="添加批注" width="30%" >
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
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, reactive } from 'vue'

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
const handleSelectArticle = (index) => {
  currentArticle.value = articles.value.find(a => a.id === Number(index))
  isCollected.value = currentArticle.value.isCollected
}

// 文本选择处理
const handleTextSelection = (e) => {
  const selection = window.getSelection()
  if (!selection.toString().trim()) return
  
  selectedText.value = selection.toString()
  showToolbar.value = true
  toolbarPos.x = e.clientX
  toolbarPos.y = e.clientY - 40
}

// 查词功能
const handleLookup = () => {
  console.log('查询单词:', selectedText.value)
  alert(`查询单词: ${selectedText.value}`)
  showToolbar.value = false
}

// 收藏功能
const toggleCollect = () => {
  alert("收藏")
  isCollected.value = !isCollected.value
  currentArticle.value.isCollected = isCollected.value
  showToolbar.value = false
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

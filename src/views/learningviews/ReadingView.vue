<template>
  <el-container class="main-container">
    <!-- 左侧文章列表 -->

    <el-aside width="300px" class="article-list">
      <!-- <el-header class="aside-header" >
        <h3>📁 文章列表</h3>
      </el-header> -->
      <el-header class="aside-header">
        <div class="upload-section">
          <el-upload
            action="#"
            :show-file-list="false"
            :before-upload="beforeUpload"
            accept=".txt,.docx"
          >
            <el-button type="primary" plain class="upload-btn">
              <el-icon><Upload /></el-icon> 上传阅读材料
            </el-button>
          </el-upload>
        </div>
      </el-header>
      <el-menu :default-active="activeArticle" @select="handleSelectArticle">
        <!-- <el-menu-item >文章记录</el-menu-item> -->
        <el-menu-item 
          v-for="article in articles" 
          :key="article.readingId" 
          :index="article.readingId.toString()"
        >
          <span>{{ article.articleTitle }}</span>
          <!-- <el-tag v-if="article.isCollected" type="warning" size="small" style="margin-left: 5px;">已收藏</el-tag> -->
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
        <h2>{{ currentArticle.articleTitle }}</h2>
        <el-tag>{{ currentArticle.sourceType }}</el-tag>
        <!-- <pre class="article-body">{{ currentArticle.articleContent}}</pre> -->

        <div class="article-body">
          <template v-for="(part, index) in renderedContent" :key="index">
            <span v-if="part.type === 'text'">{{ part.value }}</span>
            <el-popover
              v-else
              placement="top"
              trigger="click"
              :content="part.annotation.annotationContent"
              :title="`批注于 ${formatDate(part.annotation.createdAt)}`"
              width="250"
            >
              <template #reference>
                <mark class="highlight">
                  {{ part.value }}
                </mark>
              </template>
            </el-popover>
          </template>
        </div>

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
              @click="toggleCollect"
            >
              积累
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
import { ref, reactive,onMounted,onUnmounted,computed } from 'vue'
import { ElMessage } from 'element-plus'
import { ElPopover } from 'element-plus'
import request from '@/utils/request'

const isSelecting = ref(false)
// 文章示例数据
const articles = ref([
   {
            "createdAt": "2025-06-14 16:23:24",
            "updatedAt": "2025-06-14 16:23:24",
            "readingId": 10,
            "userId": 5,
            "sourceType": "recommended",
            "articleTitle": "Life Is Splendid",
            "articleContent": "Face your past without regret.\nHandle your present with confidence.\nPrepare for the future without fear.\nKeep faith and drop the fear.\nDon't believe your doubts and never doubt your beliefs."
        },
        {
            "createdAt": "2025-06-11 12:40:29",
            "updatedAt": "2025-06-11 12:40:29",
            "readingId": 9,
            "userId": 5,
            "sourceType": "recommended",
            "articleTitle": "Life Is Wonderful",
            "articleContent": "Face your past without regret.\nHandle your present with confidence.\nPrepare for the future without fear.\nKeep faith and drop the fear.\nDon't believe your doubts and never doubt your beliefs."
        },
])

// 当前选中文章
const currentArticle = ref(null)
const activeArticle = ref('')

// 文本选择相关
const showToolbar = ref(false)
const toolbarPos = reactive({ x: 0, y: 0 })
const selectedText = ref('')

// 批注相关
const showAnnotationDialog = ref(false)
const annotationText = ref('')

// 收藏状态
// const isCollected = ref(false)

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
  getArticleList() // 获取文章列表
})

// 移除监听
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})


const getAnnotations = async () => {
  try {
    const res = await request.get(`api/readings/${currentArticle.value.readingId}`)
    console.log('获取批注:', res)

    if (res.success && res.data?.annotations) {
      annotationsWithHighlight.value = res.data.annotations.map(annotation => ({
        ...annotation,
        highlighted: false
      }))
      console.log('批注数据:', annotationsWithHighlight.value)
    }
  } catch (error) {
    console.error('获取批注失败:', error)
    ElMessage.error('获取批注失败，请稍后重试')
  }
}

const handleSelectArticle = (index) => {
  currentArticle.value = articles.value.find(a => a.readingId === Number(index))
  // isCollected.value = currentArticle.value.isCollected
  console.log('选中文章:', currentArticle.value)
  getAnnotations() // 获取当前文章的批注
  
}


const getArticleList=async ()=>{
  const res=await request.get("/api/readings/my")
  console.log('获取文章列表:', res)
  if(res.success){
    articles.value=res.data
    // if(articles.value.length>0){
    //   currentArticle.value=articles.value[0]
    //   activeArticle.value=articles.value[0].id.toString()
    // }
  }
}

const tempAnnotationRange = ref(null) // 临时存储选中文本的起止位置

const annotationsWithHighlight = ref([]) //存储高亮信息

// 文本选择处理
const handleTextSelection = (e) => {
  const selection = window.getSelection()
  const selected = selection.toString().trim()

  if (!selected) return

  // 获取整个文章内容
  const content = currentArticle.value.articleContent
    // 查找选中文本在文章中的起始和结束位置
  const startPos = content.indexOf(selected)
  const endPos = startPos + selected.length


  selectedText.value = selected
  toolbarPos.x = e.clientX
  toolbarPos.y = e.clientY - 40
  showToolbar.value = true
  isSelecting.value = true // 标记为正在选择

    // 存储选中文本的起止位置，用于保存批注时发送给后端
  tempAnnotationRange.value = { startPos, endPos }
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
    const response = await request.post('/api/translate/translate', {
      q: selectedText.value,
      from:"en",
      to:"zh-CHS",
      vocabId:5
    })
    console.log('查询结果:', response.data)
    // lookupResult.value = response.data
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

  // 未收藏 → 弹出对话框让用户输入释义
  showCollectDialog.value = true
}

//判断单词还是句子
function determineType(text) {
  const trimmedText = text.trim();

  // 如果包含空格或句号/问号/感叹号等，认为是句子
  if (/\s/.test(trimmedText) || /[.!?]/.test(trimmedText)) {
    return "sentence";
  } else {
    return "word";
  }
}


const saveToCollection = async() => {
  const meaning = collectMeaning.value.trim()
  const text = selectedText.value.trim()

  if (!text || !meaning) {
    ElMessage.warning('请填写完整内容')
    return
  }
  console.log('保存到收藏:', text, meaning)
  const type=determineType(text)
  const response=await request.post('/api/accumulations',{
    type:type,
    content:text,
    meaning:meaning,
    position:{
      module:'reading',
      refId:123,
      row:1,
      column:1
    }
    
  })
  console.log('保存响应:', response.success)
  if(response.success){
    ElMessage.success('已成功积累')
  }else{
    ElMessage.error('积累失败，请稍后重试')
  }
  // 将选中内容和释义保存到当前文章的 collectedItems 数组中
  // currentArticle.value.collectedItems.push({
  //   text,
  //   meaning,
  //   timestamp: new Date().toISOString()
  // })

  // 更新收藏状态
  // isCollected.value = true
  // currentArticle.value.isCollected = true

  // 关闭对话框
  showCollectDialog.value = false
  collectMeaning.value = ''
  showToolbar.value = false

  // ElMessage.success('已成功积累')
}



// 高亮处理
const renderedContent = computed(() => {
  const content = currentArticle.value?.articleContent || ''
  const annotations = annotationsWithHighlight.value || []

  const parts = []
  let lastIndex = 0

  // 按照 startPos 排序，避免重叠冲突
  const sortedAnnotations = [...annotations].sort((a, b) => a.position.startPos - b.position.startPos)

  sortedAnnotations.forEach(annotation => {
    const { startPos, endPos } = annotation.position
    const before = content.slice(lastIndex, startPos)
    const marked = content.slice(startPos, endPos)

    if (before) parts.push({ type: 'text', value: before })
    parts.push({ type: 'highlight', value: marked, annotation })

    lastIndex = endPos
  })

  const remaining = content.slice(lastIndex)
  if (remaining) parts.push({ type: 'text', value: remaining })

  return parts
})


const formatDate = (dateString) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')} ${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`
}


// 保存批注
const saveAnnotation = async () => {
  if (!annotationText.value.trim()) return

  const { startPos, endPos } = tempAnnotationRange.value

  try {
    const res = await request.post('/api/annotations', {
      position: {
        module: 'reading',
        refId: currentArticle.value.readingId,
        startPos,
        endPos
      },
      original: selectedText.value,
      annotationContent: annotationText.value
    })

    if (res.success) {
      ElMessage.success('添加批注成功')

      // 将新批注加入本地列表，用于后续高亮
      annotationsWithHighlight.value.push({
        ...res.data,
        position: { startPos, endPos },
        original: selectedText.value,
        annotationContent: annotationText.value
      })
    }

  } catch (error) {
    ElMessage.error('添加批注失败，请稍后重试')
  } finally {
    showAnnotationDialog.value = false
    annotationText.value = ''
  }
}
// }
</script>

<style scoped>

.aside-header {
  margin-top: 10px;
  margin-left: 10px;
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

.highlight {
  background-color: yellow;
  padding: 2px 4px;
  border-radius: 4px;
  cursor: pointer;
}
</style>

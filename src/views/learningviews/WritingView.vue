<template>
  <el-container class="main-container">
    <!-- 左侧文章列表 -->
    <el-aside width="300px" class="article-list">
      <el-header class="aside-header">
        <div class="upload-section">
          <el-upload
            action="#"
            :show-file-list="false"
            :before-upload="beforeUpload"
            accept=".txt,.docx"
          >
            <el-button type="primary" plain class="upload-btn">
              <el-icon><Upload /></el-icon> 上传文章
            </el-button>


          </el-upload>
            <el-button type="primary" plain @click="addNewArticle">
               新建作文
            </el-button>
        </div>
      </el-header>
      
      <el-menu 
        :default-active="activeArticle" 
        @select="handleSelectArticle"
        class="version-menu"
      >
        <el-sub-menu 
          v-for="article in articles" 
          :key="article.writing.writingId" 
          :index="`article-${article.writing.writingId}`"
        >
          <template #title>
            <div class="article-title">
              <span>{{ article.writing.writingTopic }}</span>
              <!-- <el-tag 
                v-if="article.isCollected" 
                type="warning" 
                size="small" 
                effect="dark"
              >
                已收藏
              </el-tag> -->
            </div>
          </template>
          
          <!-- 版本列表 -->
          <el-menu-item 
            v-for="version in article.versions"
            :key="version.versionId"
            :index="`version-${article.writing.writingId}-${version.versionId}`"
          >
            <div class="version-item">
              <el-icon><Document /></el-icon>
              <span class="version-name">{{ version.user }}</span>
              <!-- <span class="version-time">{{ formatTime(version.time) }}</span> -->
            </div>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 右侧文章内容 -->
    <el-main class="article-content">
      
      <div v-if="currentVersion" class="content-box">
        <div v-if="showToolbar" class="selection-toolbar" :style="{ left: toolbarPos.x + 'px', top: toolbarPos.y + 'px' }"></div>
        <div class="action-bar">
          <el-button 
            type="primary" 
            @click="handlePolish"
            :loading="polishing"
          >
            <el-icon><MagicStick /></el-icon>
            AI润色
          </el-button>
          <el-tag type="info" effect="dark">
            创建时间: {{ currentVersion.createdAt }}
          </el-tag>
        </div>

        <!-- 内容编辑区 -->
        <div 
          class="content-editor"
          @mouseup="handleTextSelection"
        >
          <!-- <h2>{{ currentArticle.title }}</h2> -->
          <!-- <el-input
            v-if="editing"
            v-model="currentVersion.content"
            type="textarea"
            :rows="15"
            resize="none"
          /> -->
          <pre  class="article-body" @mouseup="handleTextSelection">{{ currentVersion.correctedDraft }}</pre>
           <!-- 浮动操作工具栏 -->
        <div 
          v-if="showToolbar" 
          class="selection-toolbar"
          :style="{ left: toolbarPos.x + 'px', top: toolbarPos.y + 'px' }"
        >
          <el-button-group>
            <el-button size="small" @click="handleLookup">查词</el-button>
            <el-button size="small" @click="showAnotation">批注</el-button>
            <el-button 
              size="small" 
              :type="isCollected ? 'warning' : ''"
              @click="toggleCollect"
            >
              积累
            </el-button>
            <el-button size="small">ai润色</el-button>
            <!-- <el-button size="small" @click="showToolbar=false">取消显示</el-button> -->
          </el-button-group>
        </div>
        </div>

        <!-- 原有工具栏和对话框... -->



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
      </div>

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

      <!-- AI润色对话框 -->
      <el-dialog 
        v-model="showPolishDialog" 
        title="AI润色建议" 
        fullscreen
      >
        <div class="polish-container">
          <div class="polish-column">
            <h3>原文内容</h3>
            <pre  class="article-body">{{ currentVersion.content }}</pre>
          </div>
          <div class="polish-column">
            <h3>润色建议 
              <el-tag type="success" effect="dark">AI建议</el-tag>
            </h3>
            <pre class="polish-text">{{ polishedContent }}</pre>
          </div>
        </div>
        <template #footer>
          <el-button @click="showPolishDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="applyPolish"
            :disabled="!polishedContent"
          >
            应用修改
          </el-button>
        </template>
      </el-dialog>


      <!-- 添加新文章对话框 -->
      <el-dialog v-model="showNewArticleDialog" title="新建作文" width="40%">
    <el-form>
      <el-form-item label="文章标题">
        <el-input v-model="newArticleForm.articleTitle" placeholder="请输入文章标题" />
      </el-form-item>
      <el-form-item label="版本标题">
        <el-input v-model="newArticleForm.versionTitle" placeholder="如：初稿/草稿/正式版" />
      </el-form-item>
      <el-form-item label="内容">
        <el-input
          v-model="newArticleForm.content"
          type="textarea"
          :rows="8"
          placeholder="请输入文章内容"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showNewArticleDialog = false">取消</el-button>
      <el-button type="primary" @click="saveNewArticle">保存</el-button>
    </template>
  </el-dialog>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, reactive, computed,onMounted,onUnmounted } from 'vue'
import { Document, Upload, MagicStick } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';
import request from '@/utils/request'
const annotationText = ref('') // 用于存储批注内容
const showAnnotationDialog = ref(false)

// 增强后的文章数据结构
const articles = ref([
    {
        "versions": [    //作文所有版本
            {
                "versionId": 5,   //作文版本id
                "writingId": 11,  //作文id
                "versionNumber": 58,  //不用管这个字段，没用
                "userDraft": "occaecat ad elit",  //作文正文
                "correctedDraft": "sit dolor Ut irure",  //润色后版本
                "createdAt": "2025-06-14 15:08:45"  //创建时间
            },
            {
                "versionId": 6,
                "writingId": 11,
                "versionNumber": 58,
                "userDraft": "occaecat ad elit",
                "correctedDraft": "sit dolor Ut irure",
                "createdAt": "2025-06-14 15:08:46"
            },
            {
                "versionId": 7,
                "writingId": 11,
                "versionNumber": 58,
                "userDraft": "occaecat ad elit",
                "correctedDraft": "sit dolor Ut irure",
                "createdAt": "2025-06-14 15:08:48"
            }
        ],
        "writing": {
            "writingId": 11,  //作文id
            "userId": 22,  //user id
            "writingTopic": "https400/400?lock=5847176496914640",  //作文题目
            "sourceType": "user_input",  //来源（enum字段，咱们的都是user_input,不用考虑别的）
            "createdAt": "2025-06-14 15:05:27", //创建时间
            "updatedAt": "2025-06-14 15:05:27"  //修改时间
        }
    }
]);


const getWritingArticles = async() => {
  // 模拟从API获取文章数据
  try{
    const res=await request.get('/api/writings/with-versions')
    console.log('获取文章数据:', res)
    articles.value=res
  }catch(error){
    console.error('获取文章数据失败:', error)
    ElMessage.error('获取文章数据失败，请稍后再试')
  }
  

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



//批注部分
const showAnotation = () => {
  console.log('selectedText:', selectedText.value)
  if (!selectedText.value) {
    ElMessage.warning('请先选中一段文字')
    return
  }
  showAnnotationDialog.value = true
  console.log('showAnnotationDialog:', showAnnotationDialog.value)
}


// 保存批注
const saveAnnotation = async() => {
  // if (annotationText.value.trim()) {
  //   currentArticle.value.annotations.push({
  //     text: annotationText.value,
  //     selection: selectedText.value,
  //     timestamp: new Date().toISOString()
  //   })
    try{
const res=await request.post('/api/annotations', {
      position:{
        module: 'writing',
        refId:currentArticle.value.writing.writingId,
        
      },
      original: selectedText.value,
      annotationContent: annotationText.value
    })

    console.log('this is res:',res)
    if (res.success) {
      ElMessage.success('添加批注成功')
      // editDialogVisible.value = false
    }else{
      ElMessage.error('添加批注，请稍后重试')
      // editDialogVisible.value = false
    }
    
    
    // fetchAnnotations()
    
  } catch (error) {
    ElMessage.error('修改失败',error)
    // editDialogVisible.value = false
  }finally{
// editDialogVisible.value = false
    showAnnotationDialog.value = false
    annotationText.value = ''
  }
    
}

//积累部分
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



// 当前选中状态
const activeArticle = ref('')
const currentArticle = computed(() => {
  const [_, articleId] = activeArticle.value.split('-')
  return articles.value.find(a => a.id === parseInt(articleId))
})

const currentVersion = ref(null)

// AI润色相关
const showPolishDialog = ref(false)
const polishedContent = ref('')
const polishing = ref(false)

// 文件上传处理
const beforeUpload = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    const newArticle = {
      id: Date.now(),
      title: file.name.replace(/\.[^/.]+$/, ""), // 去除扩展名
      isCollected: false,
      versions: [{
        id: 1,
        name: '初始版本',
        content: e.target.result,
        time: Date.now()
      }]
    }
    articles.value.unshift(newArticle)
  }
  reader.readAsText(file)
  return false // 阻止默认上传
}

// 版本选择处理
const handleSelectArticle = (index) => {
  activeArticle.value = index
  if (index.startsWith('version')) {
    const [_, articleId, versionId] = index.split('-')
    const article = articles.value.find(a => a.writing.writingId === parseInt(articleId))
    currentVersion.value = article.versions.find(v => v.versionId === parseInt(versionId))
    console.log('当前版本:', currentVersion.value)
  }
}

// AI润色处理（模拟）
const handlePolish = async () => {
  polishing.value = true
  // 模拟API调用
  await new Promise(resolve => setTimeout(resolve, 1000))
  polishedContent.value = currentVersion.value.content
    .replace(/confidence/g, 'strong confidence')
    .replace(/fear/g, 'apprehension')
    .replace(/Life is/g, 'Existence becomes')
  showPolishDialog.value = true
  polishing.value = false
}

// 应用润色结果
const applyPolish = () => {
  const newVersion = {
    id: currentArticle.value.versions.length + 1,
    name: `润色版 v${currentArticle.value.versions.length}`,
    content: polishedContent.value,
    time: Date.now()
  }
  currentArticle.value.versions.push(newVersion)
  showPolishDialog.value = false
}

// 时间格式化
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate()}`
}

// 添加新文章

const showNewArticleDialog = ref(false)
const newArticleForm = ref({
  articleTitle: '',
  versionTitle: '',
  content: ''
})

// 修改：点击按钮只弹窗
const addNewArticle = () => {
  showNewArticleDialog.value = true
}

// 新增：保存新文章
const saveNewArticle = () => {
  console.log('Saving new article:', newArticleForm)
  if (!newArticleForm.value.articleTitle || !newArticleForm.value.versionTitle) 
  {
    ElMessage.error('文章标题和版本标题不能为空')
    return
  }
  const newId = Date.now()
  const newArticle = {
    id: newId,
    title: newArticleForm.value.articleTitle,
    isCollected: false,
    versions: [{
      id: 1,
      name: newArticleForm.value.versionTitle,
      content: newArticleForm.value.content,
      time: Date.now()
    }]
  }
  articles.value.unshift(newArticle)
  activeArticle.value = `article-${newId}`
  // 重置表单并关闭弹窗
  newArticleForm.articleTitle = ''
  newArticleForm.versionTitle = ''
  newArticleForm.content = ''
  showNewArticleDialog.value = false
}

const showToolbar = ref(false) // 是否显示工具栏
const selectedText = ref('')   // 当前选中的文字
const toolbarPos = reactive({ x: 0, y: 0 }) // 工具栏坐标
const isSelecting = ref(false) // 是否正在选择

// 处理文本选择事件

// const handleTextSelection = (e) => {
//   const selection = window.getSelection()
//   const selected = selection.toString().trim()
//   if (!selected) return

//   // 获取整个文章内容
//   const content = currentArticle.value.articleContent

//   // 查找选中文本在文章中的起始和结束位置
//   const startPos = content.indexOf(selected)
//   const endPos = startPos + selected.length

//   selectedText.value = selected
//   toolbarPos.x = e.clientX
//   toolbarPos.y = e.clientY - 40
//   showToolbar.value = true
//   isSelecting.value = true

//   // 存储选中文本的起止位置，用于保存批注时发送给后端
//   tempAnnotationRange.value = { startPos, endPos }
// }
const handleTextSelection=(e)=>{
  const selection = window.getSelection()
  if (!selection.toString().trim()) return // 如果没有选中内容则返回


    // 打印 clientX / Y 看是否为有效值
  console.log('clientX:', e.clientX)
  console.log('clientY:', e.clientY)
  selectedText.value = selection.toString() // 保存选中的文字
  console.log('选择的文件 selectedText:', selectedText.value)
  showToolbar.value = true // 显示工具栏
  toolbarPos.x = e.clientX // 设置工具栏 X 坐标
  toolbarPos.y = e.clientY - 40 // 设置工具栏 Y 坐标
  console.log(toolbarPos)
  isSelecting.value = true // 标记为正在选择
}

const handleClickOutside = (e) => {
  if (!isSelecting.value) {
    showToolbar.value = false
  }
  isSelecting.value = false
}

onMounted(() => {
  getWritingArticles() // 初始化时获取文章数据
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.version-menu {
  user-select: none;
}

.article-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.version-item {
  display: grid;
  grid-template-columns: 20px 1fr auto;
  align-items: center;
  gap: 8px;
  width: 100%;
  font-size: 0.95em;
  
  .version-time {
    color: #999;
    font-size: 0.85em;
  }
}

.upload-section {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-bottom: 1px solid #eee;
  
  .upload-btn {
    width: 100%;
  }
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.polish-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  height: 70vh;
  overflow: auto;
  
  .polish-column {
    border: 1px solid #eee;
    padding: 20px;
    border-radius: 4px;
    
    pre {
      white-space: pre-wrap;
      line-height: 1.6;
      height: calc(70vh - 100px);
      overflow: auto;
    }
  }
}

.content-editor {
  position: relative;
  max-width: 100%;
  overflow: auto;
  :deep(.el-textarea__inner) {
    font-family: Monaco, Consolas, monospace;
    line-height: 1.6;
  }
}

/* 预格式文本显示优化 */
.article-body {
  white-space: pre-wrap; /* 保留换行但允许自动换行 */
  word-wrap: break-word; /* 允许单词内断行 */
  overflow-wrap: break-word; /* 现代浏览器换行标准 */
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell;
  line-height: 1.8;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
  max-height: 70vh;
  overflow: auto;
}

.selection-toolbar {
  position: fixed; /* 或 absolute */
}

</style>

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
       <!-- <el-menu-item >作文记录</el-menu-item> -->
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
              <span class="version-name">{{ version.versionName}}</span>
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
        <!-- <span class="highlighted-text">{{ selectedText }}</span> -->
        <div class="action-bar">
          <!-- <el-button 
            type="primary" 
            @click="enableEditing"
            :loading="polishing"
          >
            打开编辑
          </el-button> -->
            <el-popover
              placement="right-start"
              title="提示"
              :width="200"
              trigger="hover"
              content="打开编译以使用ai润色功能"
              
            >
              <template #reference>
                <el-button 
                  type="primary" 
                  @click="enableEditing"
                  :loading="polishing"
                >
                  <!-- <el-icon><MagicStick /></el-icon> -->
                  打开编辑
                </el-button>
              </template>
            </el-popover>
          <!-- <el-button type="primary" @click="console.log(hasUnsavedChanges)">
            点击
          </el-button> -->
          <!-- <el-tag type="primary">
            进入编辑模式以使用ai润色功能
          </el-tag> -->

          <el-tag type="info" effect="dark">
            创建时间: {{ currentVersion.createdAt }}
          </el-tag>
        </div>

        <!-- 内容编辑区 -->
        <div 
          class="content-editor"
          @mouseup="handleTextSelection"
        >
          <h2>{{ currentVersion.versionName }}</h2>
          <!-- <el-input
            v-if="editing"
            v-model="currentVersion.content"
            type="textarea"
            :rows="15"
            resize="none"
          /> -->
          
          <el-input
            v-if="editing"
            v-model="currentVersion.content"
            type="textarea"
            :rows="15"
            resize="none"
          />
          <div v-else class="article-body" >
            {{ currentVersion.content }}
          </div>

          <!-- 添加保存按钮 -->
          <div class="edit-actions" v-if="editing">
            <el-button type="primary" @click="saveToNowEdition" style="margin: 5px;">保存到该版本</el-button>
            <el-button type="primary" @click="saveToAnotherEdition"  style="margin: 5px;">保存到新版本</el-button>
            <el-button @click="cancelEdit"  style="margin: 5px;">取消</el-button>
          </div>


           <!-- 浮动操作工具栏 -->
        <div 
          v-if="showToolbar" 
          class="selection-toolbar"
          :style="{ left: toolbarPos.x + 'px', top: toolbarPos.y + 'px' }"
        >
          <el-button-group>
            <el-button plain size="small" type="primary" @click="handleLookup">查词/翻译</el-button>
            <el-button plain size="small" type="success" @click="showAnotation">批注</el-button>
            <el-button 
            plain
              size="small" 
              @click="toggleCollect"
              type="primary"
            >
              积累
            </el-button>
            <el-button size="small"  type="success" v-if="editing" @click="handlePolish">ai润色</el-button>
            <!-- <el-button size="small" @click="showToolbar=false">取消显示</el-button> -->
          </el-button-group>
        </div>
        </div>

        <!-- 原有工具栏和对话框... -->



        <!-- 查询对话框 -->
      <el-dialog v-model="showLookupDialog" :title="`翻译/查词：${selectedText}`" width="40%">
        <div v-if="isFetching" style="text-align: center;">
          <el-spinner />
          <p>正在查询...</p>
        </div>
      
        <div v-else-if="lookupResult">
        
          <h4>🌐 中文翻译</h4>
          <p>{{ lookupResult|| '暂无翻译' }}</p>
        </div>
      
        <div v-else>
          <p>暂无查询结果</p>
        </div>
      
        <template #footer>
          <el-button type="primary" @click="addToCollection">积累</el-button>
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
      <el-dialog v-model="showPolishDialog" title="AI润色建议" width="80%" top="5vh">
        <div class="polish-dialog-content">
          <!-- 原文内容 -->
          <div class="polish-section">
            <h4 class="section-title">📝 原文内容</h4>
            <pre class="content-box original">{{ selectedText }}</pre>
          </div>
        
          <!-- 润色后的内容 -->
          <div class="polish-section">
            <h4 class="section-title">✨ 润色后的内容</h4>
            <pre class="content-box polished">{{ polishedContent || '正在润色中...' }}</pre>
          </div>
        </div>
      
        <!-- 底部操作 -->
        <template #footer>
          <el-button @click="showPolishDialog = false">关闭</el-button>
          <el-button type="primary" @click="applyPolish" :disabled="!polishedContent">
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
import { watch } from 'vue'
import { Document, Upload, MagicStick } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';
import request from '@/utils/request'
// import { ElPopover } from 'element-plus'
const editing = ref(false)
const annotationText = ref('') // 用于存储批注内容
const showAnnotationDialog = ref(false)
const currentVersion = ref(null)

const hasUnsavedChanges = ref(false) // 是否有未保存的更改

// 增强后的文章数据结构
const articles = ref([
    {
        "versions": [
            {
                "versionId": 10,
                "writingId": 16,
                "versionName": "My Expectation of Campus Food Festival",
                "content": "Last week, I hear our school will have a food festival. It will be a big activity in our campus. Many students and teacher will join. I feel very exciting for this festival.There will have many kinds of food from different place. Like Sichuan food, Guangdong food, Western food and so on. I like spicy food, so I hope can eat a lot of Sichuan dish. Also, maybe some food we never eat before. It is good chance to try new things.Also, I think this festival not only let us eat delicious food, but also help us to understand different culture. Because food is part of culture. I also hope can make new friends in the activity. Maybe talk about what food is their favorite.In one word, I feel this festival is very meaningful. I looking forward to come and join it.",
                "createdAt": "2025-06-16 19:59:17"
            },
            {
                "versionId": 11,
                "writingId": 16,
                "versionName": "My Expectation of Campus Food Festival",
                "content": "Last week, I heard that our university will hold a campus food festival. It will be a grand event, and many students and teachers are expected to take part in it. I feel very excited about this festival.There will be a wide variety of foods from different regions, such as Sichuan cuisine, Cantonese dishes, and even Western-style meals. Since I enjoy spicy food, I especially look forward to tasting the Sichuan dishes. Besides, I hope to try some foods that I have never had before. It’s a great opportunity to explore new flavors.More importantly, I believe this festival is not just about eating delicious food. It also offers us a chance to learn more about different cultures, because food is an important part of culture. I also hope to make new friends by sharing our thoughts on food and discussing our favorite dishes.In a word, I think this festival is both meaningful and enjoyable. I’m really looking forward to joining it.",
                "createdAt": "2025-06-16 20:00:22"
            },
            {
                "versionId": 12,
                "writingId": 16,
                "versionName": "My Expectation of Campus Food Festival",
                "content": "Recently, I was thrilled to learn that our university will host a campus food festival. As a passionate food lover, I see this as a wonderful opportunity to not only enjoy a variety of delicious dishes but also experience the richness of different cultures.The festival is expected to feature cuisines from across China and beyond, including spicy Sichuan food, delicate Cantonese dishes, and flavorful Western meals. Personally, I am especially looking forward to the Sichuan cuisine for its bold and exciting flavors. More importantly, I hope to discover some lesser-known traditional snacks or international foods I’ve never tried before.Beyond satisfying our taste buds, the food festival carries deeper cultural significance. Food is more than just nourishment—it reflects history, lifestyle, and values. By tasting diverse foods and talking with others, we can gain a better understanding of different regions and backgrounds. I also hope to make new friends and share our favorite dishes with each other.In short, I believe the campus food festival will be a memorable and meaningful experience. I can’t wait to take part in it and create lasting memories with classmates and teachers alike.",
                "createdAt": "2025-06-16 20:00:43"
            }
        ],
        "writing": {
            "writingId": 16,
            "userId": 5,
            "writingTopic": "My Expectation of Campus Food Festival",
            "sourceType": "user_input",
            "direction": "Suppose your university is going to hold a campus food festival. Write a composition to describe the food festival, and explain what you expect from it.",
            "createdAt": "2025-06-16 19:48:59",
            "updatedAt": "2025-06-16 19:48:59"
        }
    }
]);

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
    const response = await request.post('/api/translate/translate', {
      q: selectedText.value,
      from:"en",
      to:"zh-CHS",
      vocabId:5
    })
    console.log('查询结果:', response)
    lookupResult.value = response.translation[0] || null
    // lookupResult.value = response.data
  } catch (error) {
    ElMessage.error('查询失败，请稍后再试')
    console.error('查词失败:', error)
  } finally {
    isFetching.value = false
  }
  showToolbar.value = false
}



// 进入编辑模式
const enableEditing = () => {
  editing.value = true
  hasUnsavedChanges.value = false // 重置状态
}
watch(
  () => editing.value,
  (isEditing) => {
    if (isEditing) {
      window.addEventListener('beforeunload', handleBeforeUnload)
    } else {
      window.removeEventListener('beforeunload', handleBeforeUnload)
    }
  }
)


// beforeunload 处理函数
const handleBeforeUnload = (e) => {
  if (hasUnsavedChanges.value) {
    const message = '您有未保存的更改，确定要离开吗？'
    e.returnValue = message // 标准方式触发浏览器确认对话框
    return message
  }
}

const saveToNowEdition = async() => {
  if (!currentVersion.value) return
  try {
    const res = await request.post(`/api/writings/${currentVersion.value.versionId}/versions}`, )
    console.log('保存到当前版本:', res)
    if (res.success) {
      ElMessage.success('保存成功')
      editing.value = false // 退出编辑模式
       hasUnsavedChanges.value = false // 清除未保存标记
    } else {
      ElMessage.error('保存失败，请稍后重试')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  }
}

// 取消编辑
// 取消编辑
const cancelEdit = () => {
  editing.value = false
  hasUnsavedChanges.value = false
}



//批注部分
const annotationsWithHighlight = ref([])


const renderedContent = computed(() => {
  const content = currentVersion.value?.content || ''
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
  console.log('--------------',selectedText.value, annotationText.value)
  console.log('tempAnnotationRange:', tempAnnotationRange.value)
  console.log("refid:",currentVersion.value.versionId)
    try{
      const res=await request.post('/api/annotations', {
          position:{
            module: 'writing',
            refId:currentVersion.value.versionId,
            startPos: tempAnnotationRange.value.startPos,
            endPos: tempAnnotationRange.value.endPos
            
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

const addToCollection=async()=>{
  const meaning = lookupResult.value.trim()
  const text=selectedText.value.trim()
  if (!text || !meaning) {
    ElMessage.warning('请填写完整内容')
    return
  }
  const type=determineType(text)
    const response=await request.post('/api/accumulations',{
    type:type,
    content:text,
    meaning:meaning,
    position:{
      module:'writing',
      refId:currentVersion.value.versionId,
      startPos: tempAnnotationRange.value.startPos,
      endPos: tempAnnotationRange.value.endPos
    }
    
  })
  console.log('保存响应:', response.success)
  if(response.success){
    ElMessage.success('已成功积累')
  }else{
    ElMessage.error('积累失败，请稍后重试')
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
      module:'writing',
      refId:currentVersion.value.versionId,
      startPos: tempAnnotationRange.value.startPos,
      endPos: tempAnnotationRange.value.endPos
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
  return articles.value.find(a => a.writing.writingId === parseInt(articleId))
})



// AI润色相关
const showPolishDialog = ref(false) // 控制是否显示润色结果对话框
const polishedContent = ref('')     // 存储 AI 返回的润色结果

// 发起 AI 润色请求
const handlePolish = async () => {
  if (!selectedText.value.trim()) return ElMessage.warning("请先选择一段文本")
  
  polishedContent.value = '润色结果测试' // 清空上次的润色结果
  showPolishDialog.value = true
  // try {
  //   const res = await request.post("/api/polish", {
  //     text: selectedText.value
  //   })
    
  //   if (res.success && res.data?.polished) {
  //     polishedContent.value = res.data.polished
  //     showPolishDialog.value = true
  //   } else {
  //     ElMessage.error("润色失败，请稍后再试")
  //   }
  // } catch (error) {
  //   console.error("AI润色请求失败:", error)
  //   ElMessage.error("网络错误，请检查连接")
  // }
}


// 应用润色结果
const applyPolish = () => {
  if (!currentVersion.value || !polishedContent.value) return

  const originalText = selectedText.value
  const newText = polishedContent.value

  // 替换选中的文本为润色后的内容
  currentVersion.value.content = currentVersion.value.content.replace(originalText, newText)

  selectedText.value = newText // 更新选中的文本

  ElMessage.success("已成功应用润色内容！")
  showPolishDialog.value = false
}

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

    // 找到对应的文章对象
    const article = articles.value.find(a => a.writing.writingId === parseInt(articleId))

    if (!article) {
      console.warn(`找不到 ID 为 ${articleId} 的文章`)
      return
    }

    // 再从该文章中找对应的版本
    const version = article.versions.find(v => v.versionId === parseInt(versionId))

    if (!version) {
      console.warn(`找不到 versionId 为 ${versionId} 的版本`)
      return
    }

    // 设置当前版本
    currentVersion.value = version
    console.log('当前版本:', currentVersion.value)
  }
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
const tempAnnotationRange = ref(null) // 临时存储选中文本的起止位置


const handleTextSelection=(e)=>{
  const selection = window.getSelection()
  const selected = selection.toString().trim() // 获取选中的文本.
  if (!selection.toString().trim()) return // 如果没有选中内容则返回

  //获取当前文章内容
  const content=currentVersion.value.content
  const startPos=content.indexOf(selected)
  const endPos=startPos+selected.length

  // selectedText.value = `<u>${selected}</u>`
  
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

  tempAnnotationRange.value = { startPos, endPos }
  console.log('选中文本:', selectedText.value, '起始位置:', startPos, '结束位置:', endPos)
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


// 监听当前版本内容的变化
watch(
  () => currentVersion.value?.content,
  (newContent, oldContent) => {
    if (editing.value && newContent !== oldContent) {
      console.log("newContent:", newContent, "oldContent:", oldContent)
      console.log('***********************',hasUnsavedChanges.value)
      hasUnsavedChanges.value = true
    }
  }
)
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

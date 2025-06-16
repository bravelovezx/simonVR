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
          :key="article.id" 
          :index="`article-${article.id}`"
        >
          <template #title>
            <div class="article-title">
              <span>{{ article.title }}</span>
              <el-tag 
                v-if="article.isCollected" 
                type="warning" 
                size="small" 
                effect="dark"
              >
                已收藏
              </el-tag>
            </div>
          </template>
          
          <!-- 版本列表 -->
          <el-menu-item 
            v-for="version in article.versions"
            :key="version.id"
            :index="`version-${article.id}-${version.id}`"
          >
            <div class="version-item">
              <el-icon><Document /></el-icon>
              <span class="version-name">{{ version.name }}</span>
              <span class="version-time">{{ formatTime(version.time) }}</span>
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
            当前版本: {{ currentVersion.name }}
          </el-tag>
        </div>

        <!-- 内容编辑区 -->
        <div 
          class="content-editor"
          @mouseup="handleTextSelection"
        >
          <h2>{{ currentArticle.title }}</h2>
          <!-- <el-input
            v-if="editing"
            v-model="currentVersion.content"
            type="textarea"
            :rows="15"
            resize="none"
          /> -->
          <pre  class="article-body" @mouseup="handleTextSelection">{{ currentVersion.content }}</pre>
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
const showAnnotationDialog = ref(false)

// 增强后的文章数据结构
const articles = ref([
  {
    id: 1,
    title: '人工智能的伦理思考',
    isCollected: true,
    versions: [
      {
        id: 1,
        name: '初稿',
        content: `The rapid development of artificial intelligence brings numerous ethical issues. Algorithmic bias may lead to discrimination, the moral dilemmas of autonomous driving need to be addressed, and data privacy protection faces challenges. We must establish a sound ethical framework to ensure that technological progress does not deviate from humanistic values.`,
        time: 1672502400000,
        annotations: [
          {
            text: "具体案例支撑 needed",
            selection: "算法偏见可能导致歧视",
            timestamp: 1672503000000
          }
        ]
      },
      {
        id: 2,
        name: '修订版',
        content: `The exponential development of artificial intelligence is triggering profound ethical reflections. Take the U.S. COMPAS judicial evaluation system as an example—its algorithm shows a bias error of up to 45% against minorities, revealing how black-box algorithms may exacerbate social discrimination. In autonomous driving, the well-known variant of the "trolley problem" requires systems to make ethical decisions instantly, necessitating engineers to predefine ethical priorities. Additionally, the boundaries of data collection in medical AI blur the balance between privacy and technological advancement. Establishing ethics committees with diverse stakeholders and creating traceable algorithm audit mechanisms may be prerequisites for human-machine symbiosis.`,
        time: 1675180800000,
        annotations: []
      },
      {
        id: 3,
        name: 'AI润色版',
        content: `The rapid advancement of AI technology is pushing human society into deep ethical waters. The controversial case of the COMPAS judicial assessment system shows algorithmic bias can result in up to 45% misjudgment against ethnic groups, exposing the potential threat of black-box algorithms to social fairness. In autonomous driving, the classic trolley problem has evolved—when an inevitable collision occurs, how should the system prioritize between the elderly and children, or passengers and pedestrians? This transcends mere technology and touches philosophical essence. On another front, the insatiable data collection by medical AI is reshaping the boundaries of privacy. Perhaps only by building interdisciplinary ethical governance frameworks and developing explainable, auditable, and traceable AI systems can we find a balance point between innovation and humanity.`,
        time: 1677600000000,
        annotations: []
      }
    ]
  },
  {
    id: 2,
    title: '数字经济赋能乡村振兴',
    isCollected: false,
    versions: [
      {
        id: 1,
        name: '调研笔记',
        content: `Current Status of Rural E-commerce Development:
- Livestream e-commerce adoption is increasing
- Logistics costs remain relatively high
- Low standardization of agricultural products
- Noticeable shortage of digital talent

Typical case: Suichang County, Zhejiang trained 500+ farmer livestreamers through the "Village Broadcasting Program", with online sales exceeding 300 million yuan in 2022.`,
        time: 1676102400000,
        annotations: [
          {
            text: "Needs specific data source",
            selection: "2022年线上销售额破3亿",
            timestamp: 1676103000000
          }
        ]
      },
      {
        id: 2,
        name: '正式报告',
        content: `According to the 2023 White Paper from the Ministry of Agriculture and Rural Affairs, China's rural online retail sales have reached 2.17 trillion yuan, an 8.4% year-on-year increase. However, constraints remain significant: the standardization rate of agricultural products is below 30%, making quality control difficult; county-level logistics costs are 42% higher than in urban areas; and the digital talent gap exceeds 2 million. Suichang County's "Village Broadcasting Academy" model is worth emulating. With government-led training, 587 farmer broadcasters were cultivated in three years, promoting specialty products like dried sweet potatoes and camellia oil, achieving an average annual online growth rate of 300%. The GMV in 2022 reached 320 million yuan (Source: Suichang Bureau of Commerce). It is recommended to build a "cloud warehouse + production base" supply chain system, implement agricultural product traceability codes, and integrate digital skills training into new farmer development programs.`,
        time: 1678742400000,
        annotations: []
      }
    ]
  },
  {
    id: 3,
    title: '《追风筝的人》书评',
    isCollected: true,
    versions: [
      {
        id: 1,
        name: '随笔草稿',
        content: `Amir’s journey of redemption is filled with metaphor. The kite symbolizes both childhood memories and a sense of guilt. Hassan’s cleft lip scar, like a smiling wound, suggests the insurmountability of class division. The act of kite running is essentially a pursuit of courage and conscience.`,
        time: 1679472000000,
        annotations: [
          {
            text: "Add specific plot reference",
            selection: "哈桑的兔唇手术痕迹",
            timestamp: 1679472600000
          }
        ]
      },
      {
        id: 2,
        name: '深度分析',
        content: `Hosseini skillfully constructs a symbolic system through two surgeries: Hassan’s cleft lip repair, which on the surface represents redemption from Amir’s father, in reality exposes the limitations of class salvation—even if physical defects are sutured, the cracks in master-servant relations remain. Amir’s eventual act of risking his life to adopt Sohrab is a "psychological surgery" whose traumatic healing mirrors Hassan’s earlier assault by Assef during the kite chase. In the novel, the kite string is like a double helix of fate, entwining betrayal and loyalty, cowardice and bravery. When Amir shouts, "For you, a thousand times over," it is not just personal redemption but a literary healing of Afghanistan's national trauma.`,
        time: 1680307200000,
        annotations: []
      }
    ]
  },
  {
    id: 4,
    title: '量子计算技术简报',
    isCollected: true,
    versions: [
      {
        id: 1,
        name: '会议记录',
        content: `Controversy over Google’s quantum supremacy:
- In 2019, claimed its 53-qubit Sycamore processor completed in 200 seconds a task that would take a supercomputer 10,000 years
- IBM questioned the claim, citing room for classical algorithm optimization
- Practical application still faces challenges such as decoherence time and error rates`,
        time: 1682899200000,
        annotations: [
          {
            text: "Need updated 2023 progress",
            selection: "2019年宣称53量子位",
            timestamp: 1682900000000
          }
        ]
      },
      {
        id: 2,
        name: '技术白皮书',
        content: `According to a recent study published in *Nature* (April 2023), quantum computing is evolving from "proof of supremacy" toward practical application:
1. Quantum advantage: IBM launched the 433-qubit Osprey processor, reducing error rates to 0.1%
2. Algorithm breakthroughs: Quantum chemical simulations achieved practical progress in material discovery, increasing lithium battery R&D efficiency by 40%
3. Hybrid architecture: Microsoft Azure Quantum enabled quantum-classical hybrid programming to solve logistics optimization problems
4. Error correction: Advances in surface code theory brought logical qubit error rates into the 10^-4 threshold

However, quantum decoherence time (currently averaging 50μs) remains the main bottleneck for practical use, requiring exploration of new approaches combining superconducting and ion trap technologies.`,
        time: 1685577600000,
        annotations: []
      }
    ]
  }
]);


//获取作文
// 获取文章数据的接口
const fetchArticles = async () => {
  try {
    const response = await request.get('/api/writings/with-versions');
    if (response && response.length > 0) {
      // 将接口返回的数据映射为当前页面使用的 articles 结构
      const mappedArticles = response.map(item => ({
        id: item.writing.writingId,
        title: item.writing.writingTopic,
        isCollected: false, // 默认未收藏
        versions: item.versions.map(version => ({
          id: version.versionId,
          name: `版本 ${version.versionNumber}`, // 可以自定义版本名称
          content: version.userDraft, // 使用用户草稿作为内容
          time: new Date(version.createdAt).getTime(), // 转换为时间戳
          annotations: [] // 默认无批注
        }))
      }));
      // 更新本地 articles 数据
      articles.value = [...mappedArticles, ...articles.value]; // 合并已有数据
    }
  } catch (error) {
    ElMessage.error('获取文章数据失败，请稍后再试');
    console.error('获取文章失败:', error);
  }
};


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

// 在 script setup 中添加 saveAnnotation 方法

const annotationText = ref('')
const saveAnnotation = async () => {
  if (!selectedText.value || !annotationText.value.trim()) return

  try {
    const res = await request.post('/api/annotations', {
      content: annotationText.value,
      text: selectedText.value,
      articleId: currentArticle.value.id,
      timestamp: new Date().toISOString()
    })

    if (res.success) {
      ElMessage.success('批注已保存')
      // 可选：将批注加入当前文章版本 annotations 数组
      currentVersion.value.annotations.push({
        text: annotationText.value,
        selection: selectedText.value,
        timestamp: new Date().toISOString()
      })
      showAnnotationDialog.value = false
      annotationText.value = ''
    } else {
      ElMessage.error('保存失败，请重试')
    }
  } catch (error) {
    console.error('保存批注出错:', error)
    ElMessage.error('网络错误，请稍后再试')
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
    const article = articles.value.find(a => a.id === parseInt(articleId))
    currentVersion.value = article.versions.find(v => v.id === parseInt(versionId))
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
const handleTextSelection=(e)=>{
  const selection = window.getSelection()
  if (!selection.toString().trim()) return // 如果没有选中内容则返回


    // 打印 clientX / Y 看是否为有效值
  console.log('clientX:', e.clientX)
  console.log('clientY:', e.clientY)
  selectedText.value = selection.toString() // 保存选中的文字
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
  fetchArticles() // 初始化时获取文章数据
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
  z-index: 9999;
}

</style>

<template>
  <div class="annotation-container">
    <!-- 批注列表 -->
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">📚 我的学习批注库（共{{ annotations.length }}条）</span>
          <el-tooltip content="批注自动保存于阅读过程中" placement="top">
            <el-icon><InfoFilled /></el-icon>
          </el-tooltip>
        </div>
      </template>

      <el-table 
        :data="annotations" 
        v-loading="loading"
        style="width: 100%"
        row-key="annotationId"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column prop="createdAt" label="创建时间" width="180" sortable>
          <template #default="{ row }">
            <span class="time-text">{{ formatTime(row.createdAt) }}</span>
            <el-tag v-if="row.createdAt !== row.updatedAt" type="info" size="small" effect="plain">
              已编辑
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="原文定位" width="200">
          <template #default="{ row }">
            <div class="position-info">
              <el-tag type="success" size="small" @click="console.log(row.position.refId)">{{ row.position?.module||'other' }}</el-tag>
              <span class="ref-id">#{{ row.position?.refId || 'id' }}</span>
              <span class="location">{{ row.position?.startPos || "start"}}-{{ row.position?.endPos||"other" }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="原文内容">
          <template #default="{ row }">
            <div class="original-text">
              {{ truncateText(row.original, 50) }}
              <el-tooltip v-if="row.original.length > 50" :content="row.original" placement="top">
                <el-icon><View /></el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="annotationContent" label="我的批注">
          <template #default="{ row }">
            <div class="annotation-content">
              {{ row.annotationContent }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-popconfirm 
              title="确定要删除这个批注吗？"
              @confirm="handleDelete(row.annotationId)"
            >
              <template #reference>
                <el-button type="danger" link>
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog 
      v-model="editDialogVisible" 
      title="✍️ 编辑批注" 
      width="600px"
      destroy-on-close
    >
      <el-form :model="currentAnnotation" label-width="80px">
        <el-form-item label="原文内容">
          <el-input 
            type="textarea" 
            :autosize="{ minRows: 2 }"
            v-model="currentAnnotation.original"
            readonly
          />
        </el-form-item>
        
        <el-form-item 
          label="我的批注"
          prop="annotationContent"
          :rules="[{ required: true, message: '批注内容不能为空', trigger: 'blur' }]"
        >
          <el-input
            type="textarea"
            :autosize="{ minRows: 4 }"
            v-model="currentAnnotation.annotationContent"
            placeholder="请输入你的批注内容..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存修改</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- <button @click="fetchAnnotations">dianji </button> -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/utils/request'

// 模拟数据（实际使用时替换为API调用）

const annotations = ref([])
const loading = ref(false)
const editDialogVisible = ref(false)
const currentAnnotation = ref(null)

// 格式化时间显示
const formatTime = (timeStr) => {
  return dayjs(timeStr).format('YYYY-MM-DD HH:mm')
}

// 文本截断
const truncateText = (text, limit) => {
  return text.length > limit ? text.substring(0, limit) + '...' : text
}

// 获取批注数据
const fetchAnnotations = async () => {
  try {
    loading.value = true
    // 实际替换为API调用
    // const res = await axios.get('/api/annotations')
    const res=await request.get('/api/annotations/my')
    console.log('------------')
    console.log('000000000',res)
    if(res.success){
        annotations.value= res.data
        console.log('获取批注数据成功', annotations.value)
    }else{
        ElMessage.error(res.data.message || '获取批注数据失败')
    }
    // annotations.value = mockData.data
  } catch (error) {
    ElMessage.error('获取批注数据失败')
  } finally {
    loading.value = false
  }
}

// 编辑批注
const handleEdit = (annotation) => {
  currentAnnotation.value = { ...annotation }
  editDialogVisible.value = true
}

// 保存修改
const handleSave = async () => {
  try {
    // 实际替换为API调用
    // await axios.put(`/api/annotations/${currentAnnotation.value.annotationId}`, {
    //   content: currentAnnotation.value.annotationContent
    // })

    const testdata={
      position: currentAnnotation.value.position,
      original: currentAnnotation.value.original,
      annotationContent: currentAnnotation.value.annotationContent
    }
    console.log("0000000000",testdata)
    const res=await request.put(`/api/annotations/${currentAnnotation.value.annotationId}`, {
      position: currentAnnotation.value.position,
      original: currentAnnotation.value.original,
      annotationContent: currentAnnotation.value.annotationContent
    })

    console.log('this is res:',res)
    if (!res.success) {
      ElMessage.error('修改失败，请稍后重试')
    }
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    fetchAnnotations()
  } catch (error) {
    ElMessage.error('修改失败')
  }
}

// 删除批注
const handleDelete = async (id) => {
  try {
    // 实际替换为API调用
    // await axios.delete(`/api/annotations/${id}`)
    const res=await request.delete(`/api/annotations/${id}`)
    if (!res.success) {
      ElMessage.error('删除失败，请稍后重试')
      return
    }
    ElMessage.success('删除成功')
    fetchAnnotations()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchAnnotations()
})
</script>

<style scoped>
.annotation-container {
  padding: 20px;
}

.main-card {
  margin: 20px;
  min-height: 600px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  font-size: 18px;
  font-weight: 500;
}

.time-text {
  margin-right: 8px;
}

.position-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.original-text {
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.annotation-content {
  color: #2c3e50;
  font-weight: 500;
}
</style>

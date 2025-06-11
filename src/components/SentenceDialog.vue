<!-- SentenceDialog.vue -->
<template>
  <el-dialog
    :title="currentSentence ? '编辑句子' : '添加新句子'"
    v-model="visible"
    width="800px"
  >
    <el-form :model="form" label-width="80px">
      <el-form-item label="原句" required>
        <el-input v-model="form.content" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="翻译" required>
        <el-input v-model="form.meaning" type="textarea" :rows="3" />
      </el-form-item>
      
      <el-divider>出处信息</el-divider>
      
      <el-form-item label="文章标题" required>
        <el-input v-model="form.position.module" />
      </el-form-item>
      <!-- <div class="source-grid">
        <el-form-item label="作者" required>
          <el-input v-model="form.source.author" />
        </el-form-item>
        <el-form-item label="原文链接">
          <el-input v-model="form.source.url" />
        </el-form-item>
        <el-form-item label="发表日期">
          <el-date-picker
            v-model="form.source.date"
            type="date"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </div> -->
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
// 实现逻辑参考WordDialog
import { ref, computed,watch } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
const props = defineProps({
  modelValue: Boolean,
  currentSentence: Object
})


const emit = defineEmits(['update:modelValue', 'refresh'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isEdit = computed(() => !!props.currentSentence)

const form = ref(initForm())

watch(() => props.currentSentence, (newVal) => {
  form.value = newVal ? { ...newVal } : initForm()
})

function initForm() {
  return {
    type:"sentence",
    content: '',
    meaning: '',
    position:{
      module: 'reading',
      refId:123,
      row: 1,
      column: 1
    }
  }
}
console.log('props', props.currentSentence)


const handleSubmit = async () => {
  if (!form.value.content || !form.value.meaning) {
    return ElMessage.error('请填写必填字段')
  }

  visible.value = false

  if (isEdit.value) {
    // 编辑模式，只允许修改释义
    console.log('编辑模式', form.value)
    console.log('更新词汇', form.value.accumulationId)
    const payload={
      type: props.currentSentence.type,
      content: props.currentSentence.content,
      meaning: form.value.meaning,
      position:{
        module: props.currentSentence.position.module,
        refId: props.currentSentence.position.refId,
        row: props.currentSentence.position.row,
        column: props.currentSentence.position.column
      }
    }
    console.log('更新负载', payload)
    const response = await request.put(`/api/accumulations/${form.value.accumulationId}`, payload)
    console.log('更新响应', response)
    ElMessage.success('更新成功')
    emit('refresh')
  } else {
    // 新增模式
    const response = await request.post('/api/accumulations', form.value)
    ElMessage.success('添加成功')
    emit('refresh')
  }
}

</script>

<style scoped>
.source-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
</style>

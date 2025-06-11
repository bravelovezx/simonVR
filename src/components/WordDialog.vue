<template>
  <el-dialog
    :title="currentWord ? '编辑词汇' : '添加新词'"
    v-model="visible"
    width="600px"
  >
    <el-form :model="form" label-width="80px">
      <el-form-item label="单词" required >
        <el-input v-model="form.content"  :readonly="isEdit"/>
      </el-form-item>
      <!-- <el-form-item label="音标">
        <el-input v-model="form.phonetic" />
      </el-form-item> -->
      <el-form-item label="释义" required>
        <el-input v-model="form.meaning" />
      </el-form-item>
      <!-- <el-form-item label="标签">
        <el-select
          v-model="form.tags"
          multiple
          filterable
          allow-create
          style="width: 100%"
        >
          <el-option
            v-for="tag in presetTags"
            :key="tag"
            :label="tag"
            :value="tag"
          />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="例句">
        <el-input 
          v-model="form.example" 
          type="textarea" 
          :rows="3"
          placeholder="请输入单词的用法例句"
        />
      </el-form-item> -->
    </el-form>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed,watch } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
const props = defineProps({
  modelValue: Boolean,
  currentWord: Object
})

console.log('props', props.currentWord)

const emit = defineEmits(['update:modelValue', 'refresh'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isEdit = computed(() => !!props.currentWord)

const form = ref(initForm())

const presetTags = ['高频词', '专业词汇', '易错词', '短语搭配']

// 监听currentWord变化初始化表单
watch(() => props.currentWord, (newVal) => {
  form.value = newVal ? { ...newVal } : initForm()
})

function initForm() {
  return {
    type:"word",
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
      type: props.currentWord.type,
      content: props.currentWord.content,
      meaning: form.value.meaning,
      position:{
        module: props.currentWord.position.module,
        refId: props.currentWord.position.refId,
        row: props.currentWord.position.row,
        column: props.currentWord.position.column
      }
    }
    console.log('更新负载', payload)
    const response = await request.put(`/api/accumulations/${form.value.accumulationId}`, payload)
    console.log('更新响应', response)
    ElMessage.success('更新成功')
    emit('refresh')
  } else {
    // 新增模式
    try{
        const response = await request.post('/api/accumulations', form.value)
    
        ElMessage.success('添加成功')
    }catch (error) {
        console.error('添加失败', error)
        ElMessage.error('添加失败，请稍后重试')
        return
    }
    
    emit('refresh')
  }
}
</script>

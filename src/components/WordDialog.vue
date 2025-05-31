<template>
  <el-dialog
    :title="currentWord ? '编辑词汇' : '添加新词'"
    v-model="visible"
    width="600px"
  >
    <el-form :model="form" label-width="80px">
      <el-form-item label="单词" required>
        <el-input v-model="form.word" />
      </el-form-item>
      <el-form-item label="音标">
        <el-input v-model="form.phonetic" />
      </el-form-item>
      <el-form-item label="释义" required>
        <el-input v-model="form.translation" />
      </el-form-item>
      <el-form-item label="标签">
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
      </el-form-item>
      <el-form-item label="例句">
        <el-input 
          v-model="form.example" 
          type="textarea" 
          :rows="3"
          placeholder="请输入单词的用法例句"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed,watch } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  currentWord: Object
})

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
    word: '',
    phonetic: '',
    translation: '',
    tags: [],
    example: ''
  }
}

const handleSubmit = () => {
  if (!form.value.word || !form.value.translation) {
    return ElMessage.error('请填写必填字段')
  }
  emit('refresh')
  visible.value = false
}
</script>

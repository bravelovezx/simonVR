<template>
  <el-dialog 
    v-model="visible" 
    :title="isEdit ? '编辑词汇' : '添加新词'" 
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
        <el-input 
          v-model="form.translation" 
          type="textarea" 
          :rows="3" 
        />
      </el-form-item>
      
      <el-form-item label="标签">
        <el-select 
          v-model="form.tags" 
          multiple 
          allow-create
          filterable
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
    </el-form>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确认</el-button>
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

const form = ref({
  word: '',
  phonetic: '',
  translation: '',
  tags: []
})

const presetTags = ['高频词', '专业词汇', '易错词', '短语搭配']

// 监听currentWord变化初始化表单
watch(() => props.currentWord, (val) => {
  if (val) {
    form.value = { ...val }
  } else {
    form.value = {
      word: '',
      phonetic: '',
      translation: '',
      tags: []
    }
  }
})

const submitForm = async () => {
  // 这里添加提交逻辑
  emit('refresh')
  visible.value = false
}
</script>

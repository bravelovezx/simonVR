<!-- SentenceDialog.vue -->
<template>
  <el-dialog
    :title="currentSentence ? '编辑句子' : '添加新句子'"
    v-model="visible"
    width="800px"
  >
    <el-form :model="form" label-width="80px">
      <el-form-item label="原句" required>
        <el-input v-model="form.sentence" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="翻译" required>
        <el-input v-model="form.translation" type="textarea" :rows="3" />
      </el-form-item>
      
      <el-divider>出处信息</el-divider>
      
      <el-form-item label="文章标题" required>
        <el-input v-model="form.source.title" />
      </el-form-item>
      <div class="source-grid">
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
      </div>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
// 实现逻辑参考WordDialog
</script>

<style scoped>
.source-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
</style>

<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="register-title">用户注册</h2>
      <el-form
        ref="form"
        :model="formData"
        :rules="rules"
        label-position="top"
        @submit.prevent="onSubmit"
      >
        <el-form-item label="账号名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入账号名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>

        <el-button
          native-type="submit"
          type="primary"
          style="width: 100%"
          :loading="loading"
        >
          注册
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const form = ref(null);
const loading = ref(false);

const formData = ref({
  username: '',
  password: ''
});

const rules = {
  username: [
    { required: true, message: '请输入账号名', trigger: 'blur' },
    { min: 3, max: 15, message: '长度在3到15个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
};

const onSubmit = () => {
  form.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      // 模拟提交请求
      setTimeout(() => {
        ElMessage.success('注册成功');
        loading.value = false;
      }, 1000);
    } else {
      ElMessage.error('请检查输入');
      return false;
    }
  });
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
  width: 400px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background-color: #ffffff;
}

.register-title {
  text-align: center;
  margin-bottom: 20px;
  color: #1890ff;
}
</style>
<template>
  <el-container class="user-container">
    <!-- 头部信息 -->
    <el-header class="user-header">
      <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar" />
      <div class="user-meta">
        <h1 class="username">{{ userInfo.username }}</h1>
        <p class="member-info">
          <el-icon><User /></el-icon>
          VIP会员 剩余{{ userInfo.memberDays }}天
        </p>
      </div>
    </el-header>

    <el-main class="user-main">
      <el-row :gutter="20">
        <!-- 左侧信息栏 -->
        <el-col :md="16" :sm="24">
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <span>基本信息</span>
                
                <el-button type="primary" size="small" @click="handleSave">保存修改</el-button>
              </div>
            </template>

            <el-form :model="userInfo" label-width="100px" :rules="formRules" ref="userForm">
              <el-form-item label="姓名" prop="realname">
                <el-input v-model="userInfo.realname" placeholder="请输入真实姓名" />
              </el-form-item>

              <el-form-item label="电子邮箱" prop="email">
                <el-input v-model="userInfo.email" placeholder="请输入邮箱地址">
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="出生日期" prop="birthday">
                <el-date-picker
                  v-model="userInfo.birthday"
                  type="date"
                  placeholder="选择生日"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>

              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="userInfo.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="更换头像">
                <el-upload
                  class="avatar-uploader"
                  action="/api/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                >
                  <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item label="密码">
                  <el-button type="primary" size="small" @click="ChangePassword">修改密码</el-button>
              </el-form-item>
              
            </el-form>
            
          </el-card>
        </el-col>

        <!-- 右侧统计栏 -->
        <el-col :md="8" :sm="24">
          <el-card class="stats-card">
            <div class="stats-item">
              <el-icon class="stats-icon"><Calendar /></el-icon>
              <div class="stats-content">
                <div class="stats-title">连续学习</div>
                <div class="stats-value">{{ studyStats.consecutiveDays }}天</div>
              </div>
            </div>

            <div class="stats-item">
              <el-icon class="stats-icon"><Notebook /></el-icon>
              <div class="stats-content">
                <div class="stats-title">已学单词</div>
                <div class="stats-value">{{ studyStats.learnedWords }}个</div>
              </div>
            </div>

            <el-progress 
              :percentage="studyStats.courseProgress" 
              :format="progressFormat"
              status="success"
            />

            <el-timeline class="study-timeline">
              <el-timeline-item
                v-for="(item, index) in studyRecords"
                :key="index"
                :timestamp="item.time"
              >
                {{ item.content }}
              </el-timeline-item>
            </el-timeline>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/uesr';
import request from '@/utils/request'
import {
  User,
  Message,
  Calendar,
  Notebook,
  Plus
} from '@element-plus/icons-vue'

const userStore = useUserStore();

// 用户信息数据
const userInfo = ref({
  username: userStore.username,
  realname: '张小明',
  email: 'user@example.com',
  gender: 1,
  birthday: '1995-05-15',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
  memberDays: 128
})

// 学习统计
const studyStats = reactive({
  consecutiveDays: 28,
  learnedWords: 1580,
  courseProgress: 65
})

// 学习记录时间线
const studyRecords = ref([
  { time: '2023-07-15 08:30', content: '完成「商务英语」课程' },
  { time: '2023-07-14 19:20', content: '复习50个四级单词' },
  { time: '2023-07-13 21:10', content: '完成每日听力训练' }
])

// 表单验证规则
const formRules = reactive({
  realname: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
})

// 进度条格式
const progressFormat = (percentage) => `课程进度 ${percentage}%`

// 头像上传成功处理
const handleAvatarSuccess = (res) => {
  userInfo.avatar = res.data.url
  ElMessage.success('头像更新成功')
}

const ChangePassword = () => {
  const username= userStore.username;
  
  ElMessage.success('密码修改功能待实现')
}

// 保存处理
const handleSave = () => {
  ElMessage.success('用户信息保存成功')
}
</script>

<style scoped>
.user-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.user-header {
  display: flex;
  align-items: center;
  padding: 30px 0;
  border-bottom: 1px solid #ebeef5;
}

.user-avatar {
  margin-right: 24px;
}

.username {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.member-info {
  color: #909399;
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-card {
  margin-bottom: 20px;
}

.stats-item {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.stats-icon {
  font-size: 28px;
  color: #409eff;
  margin-right: 16px;
}

.stats-title {
  color: #909399;
  font-size: 14px;
}

.stats-value {
  font-size: 20px;
  color: #303133;
  margin-top: 4px;
}

.study-timeline {
  margin-top: 20px;
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }
  
  :deep(.el-upload:hover) {
    border-color: var(--el-color-primary);
  }
  
  .avatar {
    width: 120px;
    height: 120px;
    display: block;
  }
  
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    text-align: center;
    line-height: 120px;
  }
}
</style>

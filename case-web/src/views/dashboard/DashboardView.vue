<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">进行中项目</div>
          <div class="stat-value">0</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">本月收入</div>
          <div class="stat-value">¥0.00</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">账户余额</div>
          <div class="stat-value">¥0.00</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">待归档档案</div>
          <div class="stat-value">0</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="welcome-card" shadow="hover">
      <h3>欢迎使用律所账户台账系统</h3>
      <p>当前用户：{{ userStore.userInfo?.realName }}（{{ userStore.userInfo?.username }}）</p>
      <p>角色：{{ userStore.userInfo?.roles?.join('，') }}</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.dashboard {
  min-height: 100%;
}
.stat-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
}
.stat-value {
  color: #303133;
  font-size: 28px;
  font-weight: bold;
}
.welcome-card {
  margin-top: 20px;
}
.welcome-card h3 {
  margin-top: 0;
}
.welcome-card p {
  color: #606266;
  line-height: 1.8;
}
</style>

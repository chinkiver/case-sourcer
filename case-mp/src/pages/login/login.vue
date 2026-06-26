<template>
  <view class="login-container">
    <view class="login-box">
      <view class="title">律所台账</view>
      <view class="subtitle">Law Firm Ledger</view>
      <input class="input" v-model="form.username" placeholder="用户名" />
      <input class="input" v-model="form.password" password placeholder="密码" />
      <button class="login-btn" type="primary" :loading="loading" @click="handleLogin">登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { login } from '@/api/auth'

const form = reactive({
  username: 'admin',
  password: 'admin123',
})
const loading = ref(false)

const handleLogin = async () => {
  if (!form.username || !form.password) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res = await login(form)
    uni.setStorageSync('token', res.token)
    uni.setStorageSync('userInfo', res.userInfo)
    uni.reLaunch({ url: '/pages/index/index' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}
.login-box {
  width: 100%;
  background: #fff;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.2);
}
.title {
  font-size: 48rpx;
  font-weight: bold;
  text-align: center;
  color: #303133;
}
.subtitle {
  font-size: 26rpx;
  text-align: center;
  color: #909399;
  margin-bottom: 60rpx;
  margin-top: 10rpx;
}
.input {
  height: 90rpx;
  border: 1rpx solid #dcdfe6;
  border-radius: 12rpx;
  padding: 0 24rpx;
  margin-bottom: 30rpx;
  font-size: 30rpx;
}
.login-btn {
  margin-top: 20rpx;
  height: 90rpx;
  line-height: 90rpx;
  font-size: 32rpx;
  border-radius: 12rpx;
}
</style>

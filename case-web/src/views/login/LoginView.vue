<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-brand">
        <div class="brand-icon">
          <el-icon :size="28" color="#fff"><ScaleToOriginal /></el-icon>
        </div>
        <span class="brand-name">律所台账系统</span>
      </div>
      <div class="login-title">
        <h2>Welcome back</h2>
        <p>登录您的账户以继续</p>
      </div>
      <el-form :model="form" :rules="rules" ref="loginFormRef" label-position="top" @keyup.enter="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-hint">
        Demo: admin / admin123
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, ScaleToOriginal } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    await userStore.loginAction(form)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error: any) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
}
.login-box {
  width: 420px;
  max-width: 90%;
  padding: 40px;
  background-color: #fff;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
}
.login-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 32px;
}
.brand-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.35);
}
.brand-name {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}
.login-title {
  text-align: left;
  margin-bottom: 28px;
}
.login-title h2 {
  margin: 0 0 8px;
  font-size: 28px;
  color: #111827;
}
.login-title p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}
.login-btn {
  width: 100%;
  height: 46px;
  margin-top: 8px;
  font-size: 16px;
}
.login-hint {
  margin-top: 20px;
  padding: 10px 16px;
  background-color: #eef2ff;
  border-radius: 10px;
  color: #6366f1;
  font-size: 13px;
  text-align: center;
}
</style>

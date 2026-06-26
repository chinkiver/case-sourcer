<template>
  <div class="login-container">
    <el-card class="login-box" shadow="always">
      <div class="login-title">
        <h2>律所账户台账系统</h2>
        <p>Law Firm Account Ledger</p>
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
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
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
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
}
.login-box {
  width: 420px;
  max-width: 90%;
  padding: 20px;
  border-radius: 12px;
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
}
.login-title h2 {
  margin: 0;
  color: #303133;
}
.login-title p {
  margin: 8px 0 0;
  color: #909399;
  font-size: 14px;
}
.login-btn {
  width: 100%;
}
</style>

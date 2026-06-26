<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <span>律所台账系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        class="menu"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-sub-menu index="/project" v-if="userStore.hasPermission('project')">
          <template #title>
            <el-icon><Folder /></el-icon>
            <span>项目管理</span>
          </template>
          <el-menu-item index="/project/list" v-if="userStore.hasPermission('project:list')">项目列表</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/income" v-if="userStore.hasPermission('income')">
          <el-icon><Money /></el-icon>
          <span>收入登记</span>
        </el-menu-item>
        <el-menu-item index="/ledger" v-if="userStore.hasPermission('ledger')">
          <el-icon><Document /></el-icon>
          <span>个人账户</span>
        </el-menu-item>
        <el-menu-item index="/archive/list" v-if="userStore.hasPermission('archive')">
          <el-icon><Box /></el-icon>
          <span>档案费管理</span>
        </el-menu-item>
        <el-sub-menu index="/social" v-if="userStore.hasPermission('social')">
          <template #title>
            <el-icon><Wallet /></el-icon>
            <span>社保公积金</span>
          </template>
          <el-menu-item index="/social/config" v-if="userStore.hasPermission('social:config')">配置管理</el-menu-item>
          <el-menu-item index="/social/record" v-if="userStore.hasPermission('social:record')">扣缴记录</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/tax" v-if="userStore.hasPermission('tax')">
          <template #title>
            <el-icon><Coin /></el-icon>
            <span>个税管理</span>
          </template>
          <el-menu-item index="/tax/record" v-if="userStore.hasPermission('tax:record')">个税扣缴</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/import" v-if="userStore.hasPermission('import')">
          <el-icon><UploadFilled /></el-icon>
          <span>数据导入</span>
        </el-menu-item>
        <el-sub-menu index="/system" v-if="userStore.hasPermission('system')">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user" v-if="userStore.hasPermission('system:user')">用户管理</el-menu-item>
          <el-menu-item index="/system/role" v-if="userStore.hasPermission('system:role')">角色管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ userStore.userInfo?.realName || userStore.userInfo?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { HomeFilled, Folder, Money, Document, Box, Wallet, Coin, UploadFilled, Setting, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
  } else if (command === 'password') {
    router.push('/password')
  }
}
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100%;
  display: flex;
  overflow: hidden;
}
.aside {
  background-color: #304156;
  flex-shrink: 0;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.menu {
  border-right: none;
}
.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  cursor: pointer;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 4px;
}
.main-container {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.main {
  background-color: #f0f2f5;
  padding: 20px;
  flex: 1;
  overflow: auto;
}
</style>

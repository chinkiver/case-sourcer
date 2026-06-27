<template>
  <el-container class="layout-container">
    <el-aside :width="sidebarWidth" class="aside">
      <div class="logo">
        <div class="logo-icon">
          <el-icon :size="22" color="#fff"><ScaleToOriginal /></el-icon>
        </div>
        <span class="logo-text">律所台账系统</span>
      </div>
      <el-scrollbar class="menu-scroll">
        <el-menu
          :default-active="activeMenu"
          router
          class="menu"
        >
          <el-menu-item-group title="概览">
            <el-menu-item index="/">
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </el-menu-item>
          </el-menu-item-group>

          <el-menu-item-group title="业务">
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
            <el-menu-item index="/expense/list" v-if="userStore.hasPermission('expense')">
              <el-icon><CreditCard /></el-icon>
              <span>支出管理</span>
            </el-menu-item>
          </el-menu-item-group>

          <el-menu-item-group title="财务">
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
          </el-menu-item-group>

          <el-menu-item-group title="数据">
            <el-menu-item index="/import" v-if="userStore.hasPermission('import')">
              <el-icon><UploadFilled /></el-icon>
              <span>数据导入</span>
            </el-menu-item>
          </el-menu-item-group>

          <el-menu-item-group title="系统">
            <el-sub-menu index="/system" v-if="userStore.hasPermission('system')">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/system/user" v-if="userStore.hasPermission('system:user')">用户管理</el-menu-item>
              <el-menu-item index="/system/role" v-if="userStore.hasPermission('system:role')">角色管理</el-menu-item>
            </el-sub-menu>
          </el-menu-item-group>
        </el-menu>
      </el-scrollbar>
      <div class="sidebar-footer">
        <div class="plan-card">
          <div class="plan-info">
            <div class="plan-title">当前版本</div>
            <div class="plan-desc">v1.0.0</div>
          </div>
        </div>
      </div>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <div class="page-title">{{ pageTitle }}</div>
          <div class="page-subtitle">{{ pageSubtitle }}</div>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <div class="user-avatar">{{ userInitial }}</div>
              <div class="user-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</div>
              <el-icon><ArrowDown /></el-icon>
            </div>
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
import {
  HomeFilled, Folder, Money, CreditCard, Document, Box, Wallet, Coin,
  UploadFilled, Setting, ArrowDown, ScaleToOriginal
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const sidebarWidth = '260px'
const activeMenu = computed(() => route.path)

const userInitial = computed(() => {
  const name = userStore.userInfo?.realName || userStore.userInfo?.username || 'U'
  return name.charAt(0).toUpperCase()
})

const pageTitleMap: Record<string, string> = {
  '/': '首页',
  '/project/list': '项目列表',
  '/income': '收入登记',
  '/expense/list': '支出管理',
  '/ledger': '个人账户',
  '/archive/list': '档案费管理',
  '/social/config': '社保公积金配置',
  '/social/record': '社保公积金扣缴记录',
  '/tax/record': '个税扣缴',
  '/import': '数据导入',
  '/system/user': '用户管理',
  '/system/role': '角色管理',
}

const pageTitle = computed(() => pageTitleMap[route.path] || '律所台账系统')
const pageSubtitle = computed(() => {
  const subs: Record<string, string> = {
    '/': '业务概览与快捷入口',
    '/ledger': '查看账户余额、流水明细与可提款金额',
  }
  return subs[route.path] || ''
})

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
  background-color: #fff;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e5e7eb;
}
.logo {
  height: 70px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 24px;
  border-bottom: 1px solid #f3f4f6;
}
.logo-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}
.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}
.menu-scroll {
  flex: 1;
  min-height: 0;
}
.menu {
  border-right: none;
  padding: 12px;
}
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #f3f4f6;
}
.plan-card {
  background: linear-gradient(135deg, #eef2ff 0%, #f5f3ff 100%);
  border-radius: 14px;
  padding: 14px 16px;
}
.plan-title {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
}
.plan-desc {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}
.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: none;
  height: 70px;
}
.header-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}
.page-subtitle {
  font-size: 13px;
  color: #6b7280;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 10px;
  transition: background 0.2s;
}
.user-info:hover {
  background-color: #f3f4f6;
}
.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}
.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}
.main-container {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.main {
  background-color: #f3f4f6;
  padding: 24px;
  flex: 1;
  overflow: auto;
}
</style>

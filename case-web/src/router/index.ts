import { createRouter, createWebHistory } from 'vue-router'
import Cookies from 'js-cookie'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/layout/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: MainLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
        },
        {
          path: 'project/list',
          name: 'project-list',
          component: () => import('@/views/project/ProjectList.vue'),
        },
        {
          path: 'ledger/account',
          name: 'ledger-account',
          component: () => import('@/views/ledger/LedgerView.vue'),
        },
        {
          path: 'archive/list',
          name: 'archive-list',
          component: () => import('@/views/archive/ArchiveFeeList.vue'),
        },
        {
          path: 'social/config',
          name: 'social-config',
          component: () => import('@/views/social/SocialConfig.vue'),
        },
        {
          path: 'social/record',
          name: 'social-record',
          component: () => import('@/views/social/SocialRecord.vue'),
        },
        {
          path: 'tax/record',
          name: 'tax-record',
          component: () => import('@/views/tax/TaxRecord.vue'),
        },
        {
          path: 'import',
          name: 'excel-import',
          component: () => import('@/views/import/ExcelImport.vue'),
        },
        {
          path: 'system/user',
          name: 'system-user',
          component: () => import('@/views/system/UserList.vue'),
        },
        {
          path: 'system/role',
          name: 'system-role',
          component: () => import('@/views/system/RoleList.vue'),
        },
        {
          path: 'expense/list',
          name: 'expense-list',
          component: () => import('@/views/expense/ExpenseList.vue'),
        },
      ],
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const token = Cookies.get('token')
  const userStore = useUserStore()

  if (to.meta.public) {
    if (token && to.path === '/login') {
      return next('/')
    }
    return next()
  }

  if (!token) {
    return next('/login')
  }

  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      userStore.logout()
      return next('/login')
    }
  }

  next()
})

export default router

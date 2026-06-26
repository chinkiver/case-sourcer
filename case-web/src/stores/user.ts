import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import Cookies from 'js-cookie'
import { login, getUserInfo, type LoginRequest, type UserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(Cookies.get('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const permissions = computed(() => userInfo.value?.permissions || [])
  const roles = computed(() => userInfo.value?.roles || [])

  const setToken = (value: string) => {
    token.value = value
    Cookies.set('token', value, { expires: 1 })
  }

  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    Cookies.remove('token')
  }

  const loginAction = async (data: LoginRequest) => {
    const res = await login(data)
    setToken(res.token)
    userInfo.value = res.userInfo
    return res
  }

  const fetchUserInfo = async () => {
    const res = await getUserInfo()
    userInfo.value = res
    return res
  }

  const logout = () => {
    clearToken()
    window.location.href = '/login'
  }

  const hasPermission = (code: string) => {
    return permissions.value.includes(code) || roles.value.includes('admin')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    permissions,
    roles,
    setToken,
    clearToken,
    loginAction,
    fetchUserInfo,
    logout,
    hasPermission,
  }
})

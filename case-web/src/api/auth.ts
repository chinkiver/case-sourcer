import request from '@/utils/request'

export interface LoginRequest {
  username: string
  password: string
}

export interface UserInfo {
  userId: number
  username: string
  realName: string
  avatar: string
  roles: string[]
  permissions: string[]
}

export interface LoginResponse {
  token: string
  tokenType: string
  expiresIn: number
  userInfo: UserInfo
}

export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return request.post('/api/auth/login', data)
}

export const getUserInfo = (): Promise<UserInfo> => {
  return request.get('/api/auth/info')
}

export const changePassword = (data: { oldPassword: string; newPassword: string }) => {
  return request.post('/api/auth/change-password', data)
}

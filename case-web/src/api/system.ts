import request from '@/utils/request'

export interface User {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  avatar: string
  status: number
  roleIds: number[]
  roleNames: string[]
  createTime: string
  updateTime: string
}

export interface UserForm {
  username: string
  password?: string
  realName: string
  phone: string
  email: string
  status: number
  roleIds: number[]
}

export interface Role {
  id: number
  roleCode: string
  roleName: string
  description: string
  permissionIds: number[]
  permissionNames: string[]
  createTime: string
  updateTime: string
}

export interface RoleForm {
  roleCode: string
  roleName: string
  description: string
  permissionIds: number[]
}

export interface Permission {
  id: number
  permissionCode: string
  permissionName: string
  type: string
  children: Permission[]
}

export const getUserList = (params: any): Promise<{ records: User[]; total: number }> => {
  return request.get('/api/users', { params })
}

export const getUser = (id: number): Promise<User> => {
  return request.get(`/api/users/${id}`)
}

export const createUser = (data: UserForm): Promise<User> => {
  return request.post('/api/users', data)
}

export const updateUser = (id: number, data: UserForm): Promise<User> => {
  return request.put(`/api/users/${id}`, data)
}

export const deleteUser = (id: number) => {
  return request.delete(`/api/users/${id}`)
}

export const resetUserPassword = (id: number) => {
  return request.put(`/api/users/${id}/reset-password`)
}

export const getRoleList = (params: any): Promise<{ records: Role[]; total: number }> => {
  return request.get('/api/roles', { params })
}

export const getAllRoles = (): Promise<Role[]> => {
  return request.get('/api/roles/all')
}

export const getRole = (id: number): Promise<Role> => {
  return request.get(`/api/roles/${id}`)
}

export const createRole = (data: RoleForm): Promise<Role> => {
  return request.post('/api/roles', data)
}

export const updateRole = (id: number, data: RoleForm): Promise<Role> => {
  return request.put(`/api/roles/${id}`, data)
}

export const deleteRole = (id: number) => {
  return request.delete(`/api/roles/${id}`)
}

export const getPermissionTree = (): Promise<Permission[]> => {
  return request.get('/api/permissions')
}

import request from '@/utils/request'

export interface Client {
  id: number
  name: string
  clientType: number
  phone?: string
  idCard?: string
  birthDate?: string
  gender?: '男' | '女' | string
  /** 服务端按当前日期动态计算 */
  age?: number
  address?: string
  remark?: string
}

export interface ClientForm {
  name: string
  /** 1 委托人 / 2 当事人 */
  clientType: number
  phone?: string
  idCard?: string
  address?: string
  remark?: string
}

export const getClientList = (type?: number): Promise<Client[]> => {
  return request.get('/api/clients', { params: { type } })
}

export const createClient = (data: ClientForm): Promise<Client> => {
  return request.post('/api/clients', data)
}
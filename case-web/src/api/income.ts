import request from '@/utils/request'

export interface IncomeForm {
  projectId?: number
  incomeDate: string
  incomeAmount: number
  hostAmount: number
  assistAmount: number
  remark: string
}

export const createIncome = (data: IncomeForm) => {
  return request.post('/api/incomes', data)
}

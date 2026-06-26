import request from '@/utils/request'

export interface Expense {
  id: number
  lawyerId: number
  lawyerName: string
  expenseDate: string
  expenseMonth: string
  expenseType: string
  amount: number
  remark: string
  createTime: string
  updateTime: string
}

export interface ExpenseForm {
  lawyerId?: number
  expenseDate: string
  expenseType: string
  amount: number
  remark: string
}

export const getExpenseList = (params: any): Promise<{ records: Expense[]; total: number }> => {
  return request.get('/api/expenses', { params })
}

export const createExpense = (data: ExpenseForm): Promise<Expense> => {
  return request.post('/api/expenses', data)
}

export const deleteExpense = (id: number) => {
  return request.delete(`/api/expenses/${id}`)
}

export const expenseTypeOptions = [
  { label: '个人报销', value: '个人报销' },
  { label: '工资发放', value: '工资发放' },
  { label: '其他支出', value: '其他支出' },
]

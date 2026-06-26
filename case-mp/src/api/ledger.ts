import request from '@/utils/request'

export interface AccountSummary {
  lawyerId: number
  lawyerName: string
  openingBalance: number
  totalIncome: number
  totalExpense: number
  currentBalance: number
  pendingArchiveFee: number
}

export interface AccountTransaction {
  id: number
  txnDate: string
  txnMonth: string
  projectNo: string
  projectName: string
  txnType: string
  amount: number
  balance: number
  remark: string
}

export const getAccountSummary = (lawyerId?: number): Promise<AccountSummary> => {
  return request.get('/api/ledger/summary', { data: { lawyerId } })
}

export const getAccountTransactions = (params: any): Promise<{ records: AccountTransaction[]; total: number }> => {
  return request.get('/api/ledger/transactions', { data: params })
}

import request from '@/utils/request'

export interface AccountSummary {
  lawyerId: number
  lawyerName: string
  openingBalance: number
  totalIncome: number
  totalExpense: number
  currentBalance: number
  pendingArchiveFee: number
  totalProjectIncome?: number
  totalCommission?: number
  totalSocialInsurance?: number
  totalTax?: number
  totalPayout?: number
  availableWithdrawal?: number
}

export interface AccountTransaction {
  id: number
  lawyerId: number
  lawyerName: string
  txnDate: string
  txnMonth: string
  projectId: number
  projectNo: string
  projectName: string
  txnType: string
  amount: number
  balance: number
  remark: string
}

export const getAccountSummary = (lawyerId?: number): Promise<AccountSummary> => {
  return request.get('/api/ledger/summary', { params: { lawyerId } })
}

export const getAccountTransactions = (params: any): Promise<{ records: AccountTransaction[]; total: number }> => {
  return request.get('/api/ledger/transactions', { params })
}

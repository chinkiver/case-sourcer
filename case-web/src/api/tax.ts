import request from '@/utils/request'

export interface TaxRecord {
  id: number
  lawyerId: number
  lawyerName: string
  year: number
  month: number
  incomeAmount: number
  deductibleAmount: number
  specialDeduction: number
  additionalDeduction: number
  otherDeduction: number
  taxableIncome: number
  taxRate: number
  quickDeduction: number
  taxAmount: number
  paidTax: number
  cumulativeIncome: number
  cumulativeTaxable: number
  cumulativeTax: number
  createTime: string
  updateTime: string
}

export const getTaxRecordList = (params: any): Promise<{ records: TaxRecord[]; total: number }> => {
  return request.get('/api/tax-records', { params })
}

export const generateTaxRecords = (params: { year: number; month: number; lawyerIds?: number[]; additionalDeduction?: number; otherDeduction?: number }): Promise<number> => {
  const query: any = { year: params.year, month: params.month }
  if (params.lawyerIds && params.lawyerIds.length > 0) {
    query.lawyerIds = params.lawyerIds.join(',')
  }
  if (params.additionalDeduction !== undefined) {
    query.additionalDeduction = params.additionalDeduction
  }
  if (params.otherDeduction !== undefined) {
    query.otherDeduction = params.otherDeduction
  }
  return request.post('/api/tax-records/generate', null, { params: query })
}

export const deleteTaxRecord = (id: number) => {
  return request.delete(`/api/tax-records/${id}`)
}

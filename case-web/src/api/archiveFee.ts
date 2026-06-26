import request from '@/utils/request'

export interface ArchiveFee {
  id: number
  projectId: number
  projectNo: string
  projectName: string
  lawyerId: number
  lawyerName: string
  feeAmount: number
  archiveStatus: number
  archiveDate: string
  refundStatus: number
  remark: string
}

export interface ArchiveFeeForm {
  projectId?: number
  lawyerId?: number
  archiveDate: string
  remark: string
}

export const archiveProject = (data: ArchiveFeeForm): Promise<ArchiveFee> => {
  return request.post('/api/archive-fees/archive', data)
}

export const getArchiveFeeList = (params: any): Promise<{ records: ArchiveFee[]; total: number }> => {
  return request.get('/api/archive-fees', { params })
}

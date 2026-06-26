import request from '@/utils/request'

export interface ImportResult {
  totalRows: number
  successRows: number
  failRows: number
  errors: string[]
}

export interface ImportSummary {
  lawyer?: ImportResult
  client?: ImportResult
  project?: ImportResult
  income?: ImportResult
}

export const importExcel = (file: File): Promise<ImportSummary> => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/import/excel', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

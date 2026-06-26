import request from '@/utils/request'

export interface SocialInsuranceConfig {
  id: number
  year: number
  month: number
  baseAmount: number
  pensionRatePersonal: number
  pensionRateCompany: number
  medicalRatePersonal: number
  medicalRateCompany: number
  unemploymentRatePersonal: number
  unemploymentRateCompany: number
  injuryRateCompany: number
  maternityRateCompany: number
  housingRatePersonal: number
  housingRateCompany: number
  createTime: string
  updateTime: string
}

export interface SocialInsuranceConfigForm {
  year: number
  month: number
  baseAmount: number
  pensionRatePersonal?: number
  pensionRateCompany?: number
  medicalRatePersonal?: number
  medicalRateCompany?: number
  unemploymentRatePersonal?: number
  unemploymentRateCompany?: number
  injuryRateCompany?: number
  maternityRateCompany?: number
  housingRatePersonal?: number
  housingRateCompany?: number
}

export interface SocialInsuranceRecord {
  id: number
  lawyerId: number
  lawyerName: string
  year: number
  month: number
  baseAmount: number
  pensionPersonal: number
  pensionCompany: number
  medicalPersonal: number
  medicalCompany: number
  unemploymentPersonal: number
  unemploymentCompany: number
  injuryCompany: number
  maternityCompany: number
  housingPersonal: number
  housingCompany: number
  totalPersonal: number
  totalCompany: number
  createTime: string
  updateTime: string
}

export const getSocialInsuranceConfigList = (params: any): Promise<{ records: SocialInsuranceConfig[]; total: number }> => {
  return request.get('/api/social-insurance-configs', { params })
}

export const getSocialInsuranceConfig = (id: number): Promise<SocialInsuranceConfig> => {
  return request.get(`/api/social-insurance-configs/${id}`)
}

export const createSocialInsuranceConfig = (data: SocialInsuranceConfigForm): Promise<SocialInsuranceConfig> => {
  return request.post('/api/social-insurance-configs', data)
}

export const updateSocialInsuranceConfig = (id: number, data: SocialInsuranceConfigForm): Promise<SocialInsuranceConfig> => {
  return request.put(`/api/social-insurance-configs/${id}`, data)
}

export const deleteSocialInsuranceConfig = (id: number) => {
  return request.delete(`/api/social-insurance-configs/${id}`)
}

export const getSocialInsuranceRecordList = (params: any): Promise<{ records: SocialInsuranceRecord[]; total: number }> => {
  return request.get('/api/social-insurance-records', { params })
}

export const generateSocialInsuranceRecords = (params: { year: number; month: number; lawyerIds?: number[] }): Promise<number> => {
  const query: any = { year: params.year, month: params.month }
  if (params.lawyerIds && params.lawyerIds.length > 0) {
    query.lawyerIds = params.lawyerIds.join(',')
  }
  return request.post('/api/social-insurance-records/generate', null, { params: query })
}

export const deleteSocialInsuranceRecord = (id: number) => {
  return request.delete(`/api/social-insurance-records/${id}`)
}

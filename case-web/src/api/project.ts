import request from '@/utils/request'

export interface Project {
  id: number
  projectNo: string
  projectName: string
  clientId: number
  clientName: string
  partyId: number
  partyName: string
  caseCause: string
  partyIdentity: string
  businessType: string
  hostLawyerId: number
  hostLawyerName: string
  assistLawyerId: number
  assistLawyerName: string
  caseDate: string
  contractAmount: number
  receivedAmount: number
  receiveRatio: number
  archiveFee: number
  archiveStatus: number
  caseStatus: number
  remark: string
}

export interface ProjectForm {
  projectName: string
  clientId?: number
  partyId?: number
  caseCause: string
  partyIdentity: string
  businessType: string
  hostLawyerId?: number
  assistLawyerId?: number
  caseDate: string
  contractAmount: number
  archiveFee: number
  remark: string
}

export const getProjectList = (params: any): Promise<{ records: Project[]; total: number }> => {
  return request.get('/api/projects', { params })
}

export const createProject = (data: ProjectForm): Promise<Project> => {
  return request.post('/api/projects', data)
}

export const updateProject = (id: number, data: ProjectForm): Promise<Project> => {
  return request.put(`/api/projects/${id}`, data)
}

export const deleteProject = (id: number) => {
  return request.delete(`/api/projects/${id}`)
}

export const getLawyerList = (): Promise<any[]> => {
  return request.get('/api/lawyers')
}

export const getClientList = (type?: number): Promise<any[]> => {
  return request.get('/api/clients', { params: { type } })
}

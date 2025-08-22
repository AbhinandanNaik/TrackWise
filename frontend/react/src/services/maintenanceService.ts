import { request } from './api'

export type UUID = string

export type MaintenanceRequestDTO = {
  description: string
  maintenanceDate?: string
  performedBy?: string
}

export type MaintenanceResponseDTO = {
  logId: UUID
  assetId: UUID
  assetName: string
  description: string
  maintenanceDate?: string
  performedBy?: string
}

export async function addMaintenance(assetId: UUID, payload: MaintenanceRequestDTO): Promise<MaintenanceResponseDTO> {
  return request(`/api/maintenance/${assetId}`, { method: 'POST', body: JSON.stringify(payload) })
}

export async function listByAsset(assetId: UUID): Promise<MaintenanceResponseDTO[]> {
  return request(`/api/maintenance/${assetId}`)
}

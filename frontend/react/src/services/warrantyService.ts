import { request } from './api'

export type UUID = string

export type WarrantyRequestDTO = {
  assetId: UUID
  startDate: string
  endDate: string
  vendor?: string
}

export type WarrantyResponseDTO = {
  warrantyId: UUID
  assetId: UUID
  assetName: string
  startDate: string
  endDate: string
  vendor?: string
}

export async function createOrUpdate(payload: WarrantyRequestDTO): Promise<WarrantyResponseDTO> {
  return request('/api/warranties', { method: 'POST', body: JSON.stringify(payload) })
}

export async function findExpiringBetween(from: string, to: string): Promise<WarrantyResponseDTO[]> {
  return request(`/api/warranties/expiring?from=${from}&to=${to}`)
}

export async function extendWarranty(id: UUID, newEndDate: string): Promise<WarrantyResponseDTO> {
  return request(`/api/warranties/${id}/extend?newEndDate=${newEndDate}`, { method: 'PUT' })
}

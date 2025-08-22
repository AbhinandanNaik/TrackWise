import { request } from './api'

export type UUID = string

export type AssetRequest = {
  name: string
  categoryId?: UUID
  status?: string
  warrantyExpiryDate?: string
  employeeId?: UUID
  purchaseDate?: string
  serialNumber?: string
}

export type AssetResponse = {
  id: UUID
  name: string
  categoryName?: string
  status?: string
  assignedEmployee?: string
  purchaseDate?: string
  serialNumber?: string
  warrantyExpiryDate?: string
}

export type Page<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export async function list(page = 0, size = 50): Promise<Page<AssetResponse>> {
  return request(`/api/assets?page=${page}&size=${size}`)
}

export async function get(id: UUID): Promise<AssetResponse> {
  return request(`/api/assets/${id}`)
}

export async function create(a: AssetRequest): Promise<AssetResponse> {
  return request('/api/assets', { method: 'POST', body: JSON.stringify(a) })
}

export async function update(id: UUID, a: Partial<AssetRequest>): Promise<AssetResponse> {
  return request(`/api/assets/${id}`, { method: 'PUT', body: JSON.stringify(a) })
}

export async function remove(id: UUID) {
  return request(`/api/assets/${id}`, { method: 'DELETE' })
}

export async function assign(assetId: UUID, employeeId: UUID): Promise<AssetResponse> {
  return request(`/api/assets/${assetId}/assign/${employeeId}`, { method: 'POST' })
}

export async function unassign(assetId: UUID): Promise<AssetResponse> {
  return request(`/api/assets/${assetId}/unassign`, { method: 'POST' })
}

export async function findByStatus(status: string): Promise<AssetResponse[]> {
  return request(`/api/assets/status/${status}`)
}

export async function findWarrantyExpiringBetween(from: string, to: string): Promise<AssetResponse[]> {
  return request(`/api/assets/warranty-expiring?from=${from}&to=${to}`)
}

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

export type Category = { id: UUID, name: string, description?: string }

export async function listCategories(): Promise<Category[]> {
  return request('/api/categories')
}

export async function createCategory(name: string, description?: string): Promise<Category> {
  return request('/api/categories', { method:'POST', body: JSON.stringify({ name, description }) })
}

export async function list(page = 0, size = 50): Promise<Page<AssetResponse>> {
  return request(`/api/assets?page=${page}&size=${size}`)
}

export async function searchByName(name: string, page = 0, size = 50): Promise<Page<AssetResponse>> {
  const q = new URLSearchParams({ name, page: String(page), size: String(size) }).toString()
  return request(`/api/assets/search?${q}`)
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

export async function findByStatus(status: string): Promise<AssetResponse[]> {
  return request(`/api/assets/status/${status}`)
}

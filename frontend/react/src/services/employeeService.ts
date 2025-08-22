import { request } from './api'

export type UUID = string

export type EmployeeRequestDTO = {
  firstName: string
  lastName: string
  email: string
  phone?: string
  departmentId?: UUID
}

export type EmployeeResponseDTO = {
  id: UUID
  firstName: string
  lastName: string
  email: string
  phone?: string
  departmentId?: UUID
  departmentName?: string
}

export type Page<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export async function list(page = 0, size = 50): Promise<Page<EmployeeResponseDTO>> {
  return request(`/api/employees?page=${page}&size=${size}`)
}

export async function create(e: EmployeeRequestDTO): Promise<EmployeeResponseDTO> {
  return request('/api/employees', { method: 'POST', body: JSON.stringify(e) })
}

export async function update(id: UUID, e: Partial<EmployeeRequestDTO>): Promise<EmployeeResponseDTO> {
  return request(`/api/employees/${id}`, { method: 'PUT', body: JSON.stringify(e) })
}

export async function remove(id: UUID) {
  return request(`/api/employees/${id}`, { method: 'DELETE' })
}

export async function findByEmail(email: string): Promise<EmployeeResponseDTO | null> {
  try { return await request(`/api/employees/search?email=${encodeURIComponent(email)}`) } catch { return null }
}

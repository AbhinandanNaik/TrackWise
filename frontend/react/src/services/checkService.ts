import { request } from './api'

export type UUID = string

export type CheckInOutRequestDTO = {
  assetId: UUID
  employeeId: UUID
}

export type CheckInOutResponseDTO = {
  id: UUID
  assetId: UUID
  assetName: string
  employeeId: UUID
  employeeName: string
  checkOutTime?: string
  checkInTime?: string
}

export type AssetScanRequestDTO = {
  assetId: UUID
  employeeId: UUID
}

export async function checkoutAsset(payload: CheckInOutRequestDTO): Promise<CheckInOutResponseDTO> {
  return request('/api/checkinout/checkout', { method: 'POST', body: JSON.stringify(payload) })
}

export async function checkinAsset(payload: CheckInOutRequestDTO): Promise<CheckInOutResponseDTO> {
  return request('/api/checkinout/checkin', { method: 'POST', body: JSON.stringify(payload) })
}

export async function historyByAsset(assetId: UUID): Promise<CheckInOutResponseDTO[]> {
  return request(`/api/checkinout/asset/${assetId}/history`)
}

export async function historyByEmployee(employeeId: UUID): Promise<CheckInOutResponseDTO[]> {
  return request(`/api/checkinout/employee/${employeeId}/history`)
}

export async function processScan(payload: AssetScanRequestDTO): Promise<CheckInOutResponseDTO> {
  return request('/api/checkinout/scan', { method: 'POST', body: JSON.stringify(payload) })
}

export async function getCurrentCheckoutByEmployee(employeeId: UUID): Promise<CheckInOutResponseDTO | null> {
  try { return await request(`/api/checkinout/employee/${employeeId}/current`) } catch { return null }
}
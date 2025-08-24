import { request } from './api'

export type UUID = string

export type IoTDataRequestDTO = {
  assetId: UUID
  temperature?: number
  batteryLevel?: number
  inUse?: boolean
  latitude?: number
  longitude?: number
}

export type IoTDataResponseDTO = {
  logId: UUID
  assetId: UUID
  assetName: string
  temperature?: number
  batteryLevel?: number
  inUse?: boolean
  timestamp: string
}

export async function ingest(payload: IoTDataRequestDTO): Promise<IoTDataResponseDTO> {
  return request('/api/iot/ingest', { method: 'POST', body: JSON.stringify(payload) })
}

export async function processSensorData(assetId: UUID, params: { temperature: number, batteryLevel: number, inUse: boolean, latitude: number, longitude: number }): Promise<IoTDataResponseDTO> {
  const q = new URLSearchParams({
    temperature: String(params.temperature),
    batteryLevel: String(params.batteryLevel),
    inUse: String(params.inUse),
    latitude: String(params.latitude),
    longitude: String(params.longitude)
  }).toString()
  return request(`/api/iot/process/${assetId}?${q}`, { method: 'POST' })
}

export async function startSimulator(): Promise<void> {
  await request('/api/iot/simulator/start', { method: 'POST' })
}

export async function stopSimulator(): Promise<void> {
  await request('/api/iot/simulator/stop', { method: 'POST' })
}



import { request } from './api'

export type UUID = string

export type NotificationRequestDTO = {
  recipientId: UUID
  message: string
}

export type NotificationResponseDTO = {
  id: UUID
  recipientId: UUID
  recipientName?: string
  message: string
  read: boolean
  createdAt: string
}

export async function createInApp(payload: NotificationRequestDTO): Promise<NotificationResponseDTO> {
  return request('/api/notifications/in-app', { method: 'POST', body: JSON.stringify(payload) })
}

export async function sendEmail(to: string, subject: string, body: string): Promise<void> {
  await request('/api/notifications/email', { method: 'POST', body: JSON.stringify({ to, subject, body }) })
}

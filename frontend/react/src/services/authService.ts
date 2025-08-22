import { request } from './api'
export async function login(username:string, password:string): Promise<string> {
  const data = await request('/api/auth/login', { method:'POST', body: JSON.stringify({ username, password }) })
  const res = data as any
  if (res?.username) localStorage.setItem('tw_username', res.username)
  if (res?.role) localStorage.setItem('tw_role', res.role)
  return res.jwt || res.token
}

export type RegistrationRequest = {
  firstName: string
  lastName: string
  email: string
  phone?: string
  username: string
  password: string
}

export type UserResponse = {
  userId: number
  employeeId?: string
  username: string
  fullName?: string
  email?: string
  role: string
  status: string
}

export async function register(payload: RegistrationRequest): Promise<UserResponse> {
  return request('/api/auth/register', { method:'POST', body: JSON.stringify(payload) })
}

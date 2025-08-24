import { request } from './api'

function decodeBase64Url(input: string): string {
  const base64 = input.replace(/-/g, '+').replace(/_/g, '/')
  const pad = base64.length % 4
  const padded = pad ? base64 + '='.repeat(4 - pad) : base64
  try { return decodeURIComponent(atob(padded).split('').map(c=>'%'+('00'+c.charCodeAt(0).toString(16)).slice(-2)).join('')) } catch { return atob(padded) }
}

function parseJwt(token: string): any | null {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return null
    const payload = JSON.parse(decodeBase64Url(parts[1]))
    return payload
  } catch { return null }
}

export async function login(username:string, password:string): Promise<string> {
  const data = await request('/api/auth/login', { method:'POST', body: JSON.stringify({ username, password }) })
  const res = data as any
  const token: string = res?.jwt || res?.token
  if (token) {
    const payload = parseJwt(token) || {}
    const subject = payload?.sub as string | undefined
    const authorities = (payload?.roles as any[] | undefined) || []
    const roleNames = authorities.map((r:any)=> r?.authority || r).filter(Boolean)
    const role = roleNames.includes('ROLE_ADMIN') ? 'ROLE_ADMIN' : 'ROLE_USER'
    if (subject) localStorage.setItem('tw_username', subject)
    localStorage.setItem('tw_role', role)
    // Fetch server-side user profile to capture linked employeeId/email accurately
    try {
      const me = await request('/api/auth/me') as any
      if (me?.email) localStorage.setItem('tw_username', me.email)
      if (me?.employeeId) localStorage.setItem('tw_employee_id', me.employeeId)
      if (me?.role) localStorage.setItem('tw_role', me.role)
    } catch {}
  }
  return token
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

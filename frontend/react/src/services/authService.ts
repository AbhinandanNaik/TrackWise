import { request } from './api'
export async function login(email:string, password:string): Promise<string> {
  const data = await request('/api/auth/login', { method:'POST', body: JSON.stringify({ email, password }) })
  return (data as any).token
}

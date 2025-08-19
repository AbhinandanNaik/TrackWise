const BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
export async function request(path:string, options: RequestInit = {}){
  const token = localStorage.getItem('tw_token')
  const headers: Record<string,string> = { 'Content-Type':'application/json', ...(options.headers as any || {}) }
  if (token) headers['Authorization'] = `Bearer ${token}`
  const res = await fetch(BASE + path, { ...options, headers })
  if (!res.ok){ const text = await res.text(); throw new Error(text || res.statusText) }
  if (res.status === 204) return null
  const ct = res.headers.get('content-type') || ''
  return ct.includes('application/json') ? res.json() : res.blob()
}

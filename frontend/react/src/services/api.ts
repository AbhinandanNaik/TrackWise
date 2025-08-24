const BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const SEND_AUTH = true
export async function request(path:string, options: RequestInit = {}){
  const baseHeaders: Record<string,string> = { 'Accept':'application/json, */*' }
  // Only set Content-Type when there is a body (to avoid unnecessary preflight)
  if (options.body && !(options.headers as any)?.['Content-Type']) {
    baseHeaders['Content-Type'] = 'application/json'
  }
  const headers: Record<string,string> = { ...baseHeaders, ...(options.headers as any || {}) }
  if (SEND_AUTH) {
    const token = localStorage.getItem('tw_token')
    if (token) headers['Authorization'] = `Bearer ${token}`
  }
  const res = await fetch(BASE + path, { ...options, headers, credentials: 'omit', mode: 'cors' })
  if (!res.ok){
    let msg = `${res.status} ${res.statusText} ${res.url}`
    try {
      const data = await res.json()
      if (data && (data.message || data.error)) msg = `${res.status} ${data.error || ''} ${data.message || ''}`.trim()
    } catch { /* body not JSON */ }
    throw new Error(msg)
  }
  if (res.status === 204) return null
  const ct = res.headers.get('content-type') || ''
  if (ct.includes('application/json')) return res.json()
  return res.blob()
}

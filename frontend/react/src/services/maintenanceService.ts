import { request } from './api'
export type Maintenance = { id?:number, assetId:number, description:string, status?:'OPEN'|'IN_PROGRESS'|'DONE', createdAt?:string }
export async function list(): Promise<Maintenance[]>{ return request('/api/maintenance') }
export async function create(m:Maintenance): Promise<Maintenance>{ return request('/api/maintenance', { method:'POST', body: JSON.stringify(m) }) }
export async function update(id:number, m:Partial<Maintenance>): Promise<Maintenance>{ return request(`/api/maintenance/${id}`, { method:'PUT', body: JSON.stringify(m) }) }
export async function remove(id:number){ return request(`/api/maintenance/${id}`, { method:'DELETE' }) }

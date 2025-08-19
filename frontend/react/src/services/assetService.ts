import { request } from './api'
export type Asset = { id?:number, name:string, serialNumber:string, category?:string, purchaseDate?:string, warrantyExpiry?:string, assignedToEmployeeId?:number }
export async function list(): Promise<Asset[]>{ return request('/api/assets') }
export async function create(a:Asset): Promise<Asset>{ return request('/api/assets', { method:'POST', body: JSON.stringify(a) }) }
export async function update(id:number, a:Partial<Asset>): Promise<Asset>{ return request(`/api/assets/${id}`, { method:'PUT', body: JSON.stringify(a) }) }
export async function remove(id:number){ return request(`/api/assets/${id}`, { method:'DELETE' }) }
export async function assign(assetId:number, employeeId:number){ return request(`/api/assets/${assetId}/assign/${employeeId}`, { method:'POST' }) }

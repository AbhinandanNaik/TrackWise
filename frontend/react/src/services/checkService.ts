import { request } from './api'
export type CheckRecord = { id?:number, assetId:number, employeeId:number, action:'CHECK_IN'|'CHECK_OUT', timestamp?:string }
export async function list(): Promise<CheckRecord[]>{ return request('/api/check') }
export async function checkOut(assetId:number, employeeId:number){ return request('/api/check/out', { method:'POST', body: JSON.stringify({ assetId, employeeId }) }) }
export async function checkIn(assetId:number, employeeId:number){ return request('/api/check/in', { method:'POST', body: JSON.stringify({ assetId, employeeId }) }) }

import { request } from './api'
export type Notification = { id:number, message:string, createdAt:string, level?:'INFO'|'WARN'|'ERROR' }
export async function list(): Promise<Notification[]>{ return request('/api/notifications') }

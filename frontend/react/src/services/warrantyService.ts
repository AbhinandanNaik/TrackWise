import { request } from './api'
export type Alert = { assetId:number, assetName:string, warrantyExpiry:string, daysLeft:number }
export async function alerts(): Promise<Alert[]>{ return request('/api/warranty/alerts') }

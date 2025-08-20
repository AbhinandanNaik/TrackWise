import { request } from './api'
export type Employee = { id?:number, name:string, email:string, department?:string }
export async function list(): Promise<Employee[]>{ return request('/api/employees') }
export async function create(e:Employee): Promise<Employee>{ return request('/api/employees', { method:'POST', body: JSON.stringify(e) }) }
export async function update(id:number, e:Partial<Employee>): Promise<Employee>{ return request(`/api/employees/${id}`, { method:'PUT', body: JSON.stringify(e) }) }
export async function remove(id:number){ return request(`/api/employees/${id}`, { method:'DELETE' }) }

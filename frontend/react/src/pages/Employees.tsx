import { useEffect, useState, FormEvent } from 'react'
import * as svc from '../services/employeeService'

export default function Employees(){
  const [items, setItems] = useState<svc.Employee[]>([])
  const [form, setForm] = useState<svc.Employee>({ name:'', email:'' } as any)

  function load(){ svc.list().then(setItems).catch(()=>setItems([])) }
  useEffect(load, [])

  function onCreate(e:FormEvent){ e.preventDefault(); svc.create(form).then(()=>{ setForm({ name:'', email:'' } as any); load() }) }
  function onUpdate(u:svc.Employee){
    const name = prompt('New name', u.name) || u.name
    svc.update(u.id!, { name }).then(load)
  }
  function onDelete(u:svc.Employee){ if(confirm('Delete employee?')) svc.remove(u.id!).then(load) }

  return <div className="grid-2">
    <div className="card">
      <h3>Employees</h3>
      <table><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Dept</th><th>Actions</th></tr></thead>
      <tbody>
        {items.map(u=>(<tr key={u.id}><td>{u.id}</td><td>{u.name}</td><td>{u.email}</td><td>{u.department||'-'}</td>
        <td><button onClick={()=>onUpdate(u)}>Edit</button> <button onClick={()=>onDelete(u)}>Delete</button></td></tr>))}
        {!items.length && <tr><td colSpan={5} className="small">No employees</td></tr>}
      </tbody></table>
    </div>
    <form className="card" onSubmit={onCreate}>
      <h3>Create Employee</h3>
      <div><label>Name</label><input value={form.name} onChange={e=>setForm({...form, name:e.target.value})} required/></div>
      <div><label>Email</label><input type="email" value={form.email} onChange={e=>setForm({...form, email:e.target.value})} required/></div>
      <div><label>Department</label><input value={form.department||''} onChange={e=>setForm({...form, department:e.target.value})}/></div>
      <button>Create</button>
    </form>
  </div>
}

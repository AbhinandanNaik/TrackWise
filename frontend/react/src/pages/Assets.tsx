import { useEffect, useState, FormEvent } from 'react'
import * as api from '../services/assetService'
import * as emp from '../services/employeeService'

export default function Assets(){
  const [items, setItems] = useState<api.Asset[]>([])
  const [employees, setEmployees] = useState<emp.Employee[]>([])
  const [form, setForm] = useState<api.Asset>({ name:'', serialNumber:'' })
  const [assign, setAssign] = useState<{assetId:number, employeeId:number}>({ assetId: 0, employeeId: 0 })

  function load(){
    api.list().then(setItems).catch(()=>setItems([]))
    emp.list().then(setEmployees).catch(()=>setEmployees([]))
  }
  useEffect(load, [])

  function onCreate(e:FormEvent){
    e.preventDefault()
    api.create(form).then(()=>{ setForm({ name:'', serialNumber:'' }); load() })
  }
  function onUpdate(a:api.Asset){
    const name = prompt('New name', a.name) || a.name
    api.update(a.id!, { name }).then(load)
  }
  function onDelete(a:api.Asset){
    if(confirm('Delete asset?')) api.remove(a.id!).then(load)
  }
  function onAssign(e:FormEvent){
    e.preventDefault()
    if(assign.assetId && assign.employeeId) api.assign(assign.assetId, assign.employeeId).then(load)
  }

  return <div className="grid-2">
    <div className="card">
      <h3>Assets</h3>
      <table><thead><tr><th>ID</th><th>Name</th><th>Serial</th><th>Assigned To</th><th>Actions</th></tr></thead>
      <tbody>
        {items.map(a=>(<tr key={a.id}>
          <td>{a.id}</td><td>{a.name}</td><td>{a.serialNumber}</td><td>{a.assignedToEmployeeId ?? '-'}</td>
          <td><button onClick={()=>onUpdate(a)}>Edit</button> <button onClick={()=>onDelete(a)}>Delete</button></td>
        </tr>))}
        {!items.length && <tr><td colSpan={5} className="small">No assets</td></tr>}
      </tbody></table>
    </div>
    <div>
      <form className="card" onSubmit={onCreate}>
        <h3>Create Asset</h3>
        <div><label>Name</label><input value={form.name} onChange={e=>setForm({...form, name:e.target.value})} required/></div>
        <div><label>Serial</label><input value={form.serialNumber} onChange={e=>setForm({...form, serialNumber:e.target.value})} required/></div>
        <button>Create</button>
      </form>
      <form className="card" onSubmit={onAssign}>
        <h3>Assign Asset</h3>
        <div><label>Asset</label><select value={assign.assetId} onChange={e=>setAssign({...assign, assetId:Number(e.target.value)})}>
          <option value={0}>Select asset</option>{items.map(a=><option key={a.id} value={a.id!}>{a.name} #{a.id}</option>)}
        </select></div>
        <div><label>Employee</label><select value={assign.employeeId} onChange={e=>setAssign({...assign, employeeId:Number(e.target.value)})}>
          <option value={0}>Select employee</option>{employees.map(u=><option key={u.id} value={u.id!}>{u.name}</option>)}
        </select></div>
        <button>Assign</button>
      </form>
    </div>
  </div>
}

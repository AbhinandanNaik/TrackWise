import { useEffect, useState, FormEvent } from 'react'
import * as svc from '../services/maintenanceService'
import * as assets from '../services/assetService'

export default function Maintenance(){
  const [items, setItems] = useState<svc.Maintenance[]>([])
  const [assetList, setAssetList] = useState<assets.Asset[]>([])
  const [form, setForm] = useState<svc.Maintenance>({ assetId:0, description:'' })

  function load(){ svc.list().then(setItems).catch(()=>setItems([])); assets.list().then(setAssetList).catch(()=>setAssetList([])) }
  useEffect(load, [])

  function onCreate(e:FormEvent){ e.preventDefault(); svc.create(form).then(()=>{ setForm({ assetId:0, description:'' }); load() }) }
  function onUpdate(m:svc.Maintenance){
    const status = prompt('Status (OPEN, IN_PROGRESS, DONE)', m.status || 'OPEN') || m.status
    if (status) svc.update(m.id!, { status: status as any }).then(load)
  }
  function onDelete(m:svc.Maintenance){ if(confirm('Delete record?')) svc.remove(m.id!).then(load) }

  return <div className="grid-2">
    <div className="card">
      <h3>Maintenance</h3>
      <table><thead><tr><th>ID</th><th>Asset</th><th>Description</th><th>Status</th><th>Created</th><th>Actions</th></tr></thead>
      <tbody>{items.map(m=>(<tr key={m.id}><td>{m.id}</td><td>{m.assetId}</td><td>{m.description}</td><td>{m.status}</td><td>{m.createdAt}</td>
      <td><button onClick={()=>onUpdate(m)}>Update</button> <button onClick={()=>onDelete(m)}>Delete</button></td></tr>))}
      {!items.length && <tr><td colSpan={6} className="small">No maintenance records</td></tr>}</tbody></table>
    </div>
    <form className="card" onSubmit={onCreate}>
      <h3>Log Maintenance</h3>
      <div><label>Asset</label><select value={form.assetId} onChange={e=>setForm({...form, assetId:Number(e.target.value)})}>
        <option value={0}>Select asset</option>{assetList.map(a=><option key={a.id} value={a.id!}>{a.name} #{a.id}</option>)}
      </select></div>
      <div><label>Description</label><textarea value={form.description} onChange={e=>setForm({...form, description:e.target.value})} required/></div>
      <button>Create</button>
    </form>
  </div>
}

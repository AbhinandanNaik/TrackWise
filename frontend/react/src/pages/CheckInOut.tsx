import { useEffect, useState, FormEvent } from 'react'
import * as check from '../services/checkService'
import * as emp from '../services/employeeService'
import * as assets from '../services/assetService'

export default function CheckInOut(){
  const [log, setLog] = useState<check.CheckRecord[]>([])
  const [employees, setEmployees] = useState<emp.Employee[]>([])
  const [assetList, setAssetList] = useState<assets.Asset[]>([])
  const [form, setForm] = useState({ assetId:0, employeeId:0, action:'CHECK_OUT' as 'CHECK_OUT'|'CHECK_IN' })

  function load(){
    check.list().then(setLog).catch(()=>setLog([]))
    emp.list().then(setEmployees).catch(()=>setEmployees([]))
    assets.list().then(setAssetList).catch(()=>setAssetList([]))
  }
  useEffect(load, [])

  function submit(e:FormEvent){
    e.preventDefault()
    const fn = form.action === 'CHECK_OUT' ? check.checkOut : check.checkIn
    fn(form.assetId, form.employeeId).then(load)
  }

  return <div className="grid-2">
    <div className="card">
      <h3>Check Log</h3>
      <table><thead><tr><th>Time</th><th>Asset</th><th>Employee</th><th>Action</th></tr></thead>
      <tbody>{log.map((r,i)=>(<tr key={i}><td>{r.timestamp}</td><td>{r.assetId}</td><td>{r.employeeId}</td><td>{r.action}</td></tr>))}
      {!log.length && <tr><td colSpan={4} className="small">No activity</td></tr>}</tbody></table>
    </div>
    <form className="card" onSubmit={submit}>
      <h3>New Action</h3>
      <div><label>Asset</label><select value={form.assetId} onChange={e=>setForm({...form, assetId:Number(e.target.value)})}>
        <option value={0}>Select asset</option>{assetList.map(a=><option key={a.id} value={a.id!}>{a.name} #{a.id}</option>)}
      </select></div>
      <div><label>Employee</label><select value={form.employeeId} onChange={e=>setForm({...form, employeeId:Number(e.target.value)})}>
        <option value={0}>Select employee</option>{employees.map(u=><option key={u.id} value={u.id!}>{u.name}</option>)}
      </select></div>
      <div><label>Action</label>
        <select value={form.action} onChange={e=>setForm({...form, action: e.target.value as any})}>
          <option value="CHECK_OUT">Check Out</option><option value="CHECK_IN">Check In</option>
        </select>
      </div>
      <button>Submit</button>
    </form>
  </div>
}

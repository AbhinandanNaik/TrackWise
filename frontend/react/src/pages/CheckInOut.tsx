import { useEffect, useState, FormEvent } from 'react'
import * as check from '../services/checkService'
import * as emp from '../services/employeeService'
import * as assets from '../services/assetService'

export default function CheckInOut(){
  const [log, setLog] = useState<check.CheckInOutResponseDTO[]>([])
  const [employees, setEmployees] = useState<emp.EmployeeResponseDTO[]>([])
  const [assetPage, setAssetPage] = useState<assets.Page<assets.AssetResponse>>({ content: [], number: 0, size: 20, totalElements: 0, totalPages: 0 })
  const [form, setForm] = useState<{ assetId: string, employeeId: string, action:'CHECK_OUT'|'CHECK_IN' }>({ assetId:'', employeeId:'', action:'CHECK_OUT' })
  const [since, setSince] = useState('')
  const [overdue, setOverdue] = useState<check.CheckInOutResponseDTO[]>([])
  const [scan, setScan] = useState<{ assetId: string, employeeId: string }>({ assetId:'', employeeId:'' })

  function load(){
    emp.list(0, 100).then(p=>setEmployees(p.content)).catch(()=>setEmployees([]))
    assets.list(0, 100).then(p=>setAssetPage(p)).catch(()=>setAssetPage({ content: [], number: 0, size: 20, totalElements: 0, totalPages: 0 }))
  }
  useEffect(load, [])

  function submit(e:FormEvent){
    e.preventDefault()
    const payload = { assetId: form.assetId, employeeId: form.employeeId }
    const fn = form.action === 'CHECK_OUT' ? check.checkoutAsset : check.checkinAsset
    fn(payload).then(()=>{
      setForm({ assetId:'', employeeId:'', action: form.action })
      if (form.assetId) check.historyByAsset(form.assetId).then(setLog).catch(()=>setLog([]))
    })
  }

  return <div className="grid-2">
    <div className="card">
      <h3>Check History</h3>
      <table><thead><tr><th>Checkout</th><th>Checkin</th><th>Asset</th><th>Employee</th></tr></thead>
      <tbody>{log.map((r,i)=>(<tr key={i}><td>{r.checkOutTime||'-'}</td><td>{r.checkInTime||'-'}</td><td>{r.assetName}</td><td>{r.employeeName}</td></tr>))}
      {!log.length && <tr><td colSpan={4} className="small">No activity</td></tr>}</tbody></table>
    </div>
    <form className="card" onSubmit={e=>{ e.preventDefault(); if(!form.assetId) return; check.historyByAsset(form.assetId).then(setLog).catch(()=>setLog([])) }}>
      <h3>View History by Asset</h3>
      <div><label>Asset</label>
        <select value={form.assetId} onChange={e=>setForm({...form, assetId: e.target.value})}>
          <option value=''>Select asset</option>{assetPage.content.map(a=> <option key={a.id} value={a.id}>{a.name} #{a.id}</option>)}
        </select>
        <button style={{marginLeft:8}} disabled={!form.assetId}>Load</button>
      </div>
    </form>
    <form className="card" onSubmit={e=>{ e.preventDefault(); if(!form.employeeId) return; check.historyByEmployee(form.employeeId).then(setLog).catch(()=>setLog([])) }}>
      <h3>View History by Employee</h3>
      <div><label>Employee</label>
        <select value={form.employeeId} onChange={e=>setForm({...form, employeeId: (e.target.value)})}>
          <option value=''>Select employee</option>{employees.map(u=> <option key={u.id} value={u.id}>{u.firstName} {u.lastName}</option>)}
        </select>
        <button style={{marginLeft:8}} disabled={!form.employeeId}>Load</button>
      </div>
    </form>
    <form className="card" onSubmit={submit}>
      <h3>New Action</h3>
      <div><label>Asset</label>
        <select value={form.assetId} onChange={e=>setForm({...form, assetId:e.target.value})}>
          <option value=''>Select asset</option>{assetPage.content.map(a=> <option key={a.id} value={a.id}>{a.name} #{a.id}</option>)}
        </select>
      </div>
      <div><label>Employee</label>
        <select value={form.employeeId} onChange={e=>setForm({...form, employeeId:(e.target.value)})}>
          <option value=''>Select employee</option>{employees.map(u=><option key={u.id} value={u.id}>{u.firstName} {u.lastName}</option>)}
        </select>
      </div>
      <div><label>Action</label>
        <select value={form.action} onChange={e=>setForm({...form, action: e.target.value as any})}>
          <option value="CHECK_OUT">Check Out</option><option value="CHECK_IN">Check In</option>
        </select>
      </div>
      <button>Submit</button>
    </form>
    <form className="card" onSubmit={e=>{ e.preventDefault(); if(!scan.assetId||!scan.employeeId) return; check.processScan(scan).then(r=>{ setScan({assetId:'',employeeId:''}); setLog([r, ...log]) }).catch(()=>{}) }}>
      <h3>Process Scan</h3>
      <div><label>Asset ID</label><input value={scan.assetId} onChange={e=>setScan({...scan, assetId:e.target.value})} required/></div>
      <div><label>Employee ID</label><input value={scan.employeeId} onChange={e=>setScan({...scan, employeeId:e.target.value})} required/></div>
      <button>Process</button>
    </form>
    <form className="card" onSubmit={e=>{ e.preventDefault(); if(!since) return; fetch(`/api/checkinout/overdue?since=${since}`, { headers:{ 'Authorization': `Bearer ${localStorage.getItem('tw_token')}` } }).then(r=>r.json()).then(setOverdue).catch(()=>setOverdue([])) }}>
      <h3>Overdue Assets</h3>
      <div><label>Since</label><input type="date" value={since} onChange={e=>setSince(e.target.value)} /> <button disabled={!since}>Load</button></div>
      {!!overdue.length && <table style={{marginTop:8}}><thead><tr><th>Asset</th><th>Employee</th><th>Checkout</th></tr></thead>
      <tbody>{overdue.map((r,i)=>(<tr key={i}><td>{r.assetName}</td><td>{r.employeeName}</td><td>{r.checkOutTime}</td></tr>))}</tbody></table>}
      {!overdue.length && <div className="small">No overdue items</div>}
    </form>
  </div>
}

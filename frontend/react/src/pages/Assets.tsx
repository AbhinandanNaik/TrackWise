import { useEffect, useState, FormEvent } from 'react'
import * as api from '../services/assetService'
import * as emp from '../services/employeeService'

export default function Assets(){
  const [page, setPage] = useState(0)
  const [items, setItems] = useState<api.AssetResponse[]>([])
  const [total, setTotal] = useState(0)
  const [employees, setEmployees] = useState<emp.EmployeeResponseDTO[]>([])
  const [form, setForm] = useState<api.AssetRequest>({ name:'', serialNumber:'', status:'AVAILABLE' })
  const [assign, setAssign] = useState<{assetId:string, employeeId:string}>({ assetId: '', employeeId: '' })
  const [statusFilter, setStatusFilter] = useState('')
  const [from, setFrom] = useState('')
  const [to, setTo] = useState('')
  const [expiring, setExpiring] = useState<api.AssetResponse[]>([])
  const [editing, setEditing] = useState<api.AssetResponse | null>(null)
  const [createError, setCreateError] = useState<string | null>(null)
  const [editError, setEditError] = useState<string | null>(null)

  function isValidUuid(value?: string){
    if(!value) return false
    return /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/.test(value)
  }

  function load(){
    if (statusFilter) {
      api.findByStatus(statusFilter).then(setItems).catch(()=>setItems([])); setTotal(0)
    } else {
      api.list(page, 20).then(p=>{ setItems(p.content); setTotal(p.totalElements) }).catch(()=>{ setItems([]); setTotal(0) })
    }
    emp.list(0, 100).then(p=>setEmployees(p.content)).catch(()=>setEmployees([]))
  }
  useEffect(load, [page])

  function onCreate(e:FormEvent){
    e.preventDefault()
    setCreateError(null)
    // Enforce required by DB: categoryId must be a valid UUID
    const categoryId = (form.categoryId as string) || ''
    if(!isValidUuid(categoryId)){
      setCreateError('Category ID must be a valid UUID (required)')
      return
    }
    const employeeId = (form.employeeId as string) || ''
    const payload: api.AssetRequest = {
      name: form.name,
      serialNumber: form.serialNumber,
      categoryId,
      purchaseDate: form.purchaseDate || undefined,
      warrantyExpiryDate: form.warrantyExpiryDate || undefined,
      status: form.status || undefined,
      employeeId: employeeId && isValidUuid(employeeId) ? employeeId : undefined,
    }
    api.create(payload).then(()=>{ setForm({ name:'', serialNumber:'', status:'AVAILABLE' }); load() }).catch(err=>setCreateError(err.message||'Failed to create'))
  }
  function onUpdate(a:api.AssetResponse){
    setEditing(a)
  }
  function onDelete(a:api.AssetResponse){
    if(confirm('Delete asset?')) api.remove(a.id).then(load)
  }
  function onAssign(e:FormEvent){
    e.preventDefault()
    if(assign.assetId && assign.employeeId) api.assign(assign.assetId, assign.employeeId).then(load)
  }

  return <div className="grid-2">
    <div className="card">
      <h3>Assets</h3>
      <div style={{marginBottom:8}}>
        <label>Status</label>
        <select value={statusFilter} onChange={e=>{ setStatusFilter(e.target.value); setPage(0); setItems([]); setTotal(0); }}>
          <option value=''>All</option>
          <option value='AVAILABLE'>AVAILABLE</option>
          <option value='ASSIGNED'>ASSIGNED</option>
          <option value='UNDER_MAINTENANCE'>UNDER_MAINTENANCE</option>
          <option value='RETIRED'>RETIRED</option>
          <option value='LOST'>LOST</option>
        </select>
      </div>
      <table><thead><tr><th>ID</th><th>Name</th><th>Serial</th><th>Status</th><th>Assigned To</th><th>Actions</th></tr></thead>
      <tbody>
        {items.map(a=>(<tr key={a.id}>
          <td>{a.id}</td><td>{a.name}</td><td>{a.serialNumber}</td><td>{a.status}</td><td>{a.assignedEmployee ?? '-'}</td>
          <td><button onClick={()=>onUpdate(a)}>Edit</button> <button onClick={()=>onDelete(a)}>Delete</button> <button onClick={()=>api.unassign(a.id).then(load)}>Unassign</button></td>
        </tr>))}
        {!items.length && <tr><td colSpan={6} className="small">No assets</td></tr>}
      </tbody></table>
      <div className="small" style={{display:'flex', gap:8, alignItems:'center'}}>
        <button disabled={page===0} onClick={()=>setPage(p=>p-1)}>Prev</button>
        <span>Page {page+1} / {Math.max(1, Math.ceil(total/20))}</span>
        <button disabled={(page+1) >= Math.ceil(total/20)} onClick={()=>setPage(p=>p+1)}>Next</button>
      </div>
    </div>
    <div>
      {editing && <form className="card" onSubmit={e=>{ e.preventDefault(); setEditError(null); const cat = (document.getElementById('edit-asset-category-id') as HTMLInputElement)?.value || ''; const empId = (document.getElementById('edit-asset-employee-id') as HTMLSelectElement)?.value || ''; if (cat && !isValidUuid(cat)) { setEditError('Category ID must be a valid UUID'); return } if (empId && !isValidUuid(empId)) { setEditError('Employee ID must be a valid UUID'); return } api.update(editing.id, {
        name: editing.name,
        serialNumber: editing.serialNumber,
        purchaseDate: editing.purchaseDate,
        status: editing.status,
        warrantyExpiryDate: editing.warrantyExpiryDate,
        // Set category/employee if provided below
        categoryId: cat || undefined,
        employeeId: empId || undefined,
      }).then(()=>{ setEditing(null); load() }).catch(err=>setEditError(err.message||'Failed to update')) }}>
        <h3>Edit Asset</h3>
        {editError && <div className="small danger" style={{marginBottom:8}}>{editError}</div>}
        <div className="row"><label>Name</label><input value={editing.name} onChange={e=>setEditing({...editing, name:e.target.value})}/></div>
        <div className="row"><label>Serial</label><input value={editing.serialNumber||''} onChange={e=>setEditing({...editing, serialNumber:e.target.value})}/></div>
        <div className="row"><label>Purchase Date</label><input type="date" value={editing.purchaseDate||''} onChange={e=>setEditing({...editing, purchaseDate:e.target.value})}/></div>
        <div className="row"><label>Status</label>
          <select value={editing.status||''} onChange={e=>setEditing({...editing, status:e.target.value})}>
            <option value="">(auto)</option>
            <option value="AVAILABLE">AVAILABLE</option>
            <option value="ASSIGNED">ASSIGNED</option>
            <option value="UNDER_MAINTENANCE">UNDER_MAINTENANCE</option>
            <option value="RETIRED">RETIRED</option>
            <option value="LOST">LOST</option>
          </select>
        </div>
        <div className="row"><label>Warranty Expiry</label><input type="date" value={editing.warrantyExpiryDate||''} onChange={e=>setEditing({...editing, warrantyExpiryDate:e.target.value})}/></div>
        <div className="row"><label>Category ID</label><input id="edit-asset-category-id" placeholder="UUID" /></div>
        <div className="row"><label>Assign Employee</label>
          <select id="edit-asset-employee-id" defaultValue="">
            <option value="">(no change)</option>
            {employees.map(u=> <option key={u.id} value={u.id}>{u.firstName} {u.lastName}</option>)}
          </select>
        </div>
        <div className="row" style={{gap:8}}>
          <button>Save</button>
          <button type="button" onClick={()=>setEditing(null)}>Cancel</button>
        </div>
      </form>}
      <form id="create-asset" className="card" onSubmit={onCreate}>
        <h3>Create Asset</h3>
        {createError && <div className="small danger" style={{marginBottom:8}}>{createError}</div>}
        <div><label>Name</label><input value={form.name} onChange={e=>setForm({...form, name:e.target.value})} required/></div>
        <div><label>Serial</label><input value={form.serialNumber||''} onChange={e=>setForm({...form, serialNumber:e.target.value})} required/></div>
        <div><label>Category ID</label><input placeholder="UUID" value={(form.categoryId as string)||''} onChange={e=>setForm({...form, categoryId: e.target.value})} required/></div>
        <div><label>Purchase Date</label><input type="date" value={form.purchaseDate||''} onChange={e=>setForm({...form, purchaseDate: e.target.value})} placeholder="YYYY-MM-DD" /></div>
        <div><label>Warranty Expiry</label><input type="date" value={form.warrantyExpiryDate||''} onChange={e=>setForm({...form, warrantyExpiryDate: e.target.value})} placeholder="YYYY-MM-DD" /></div>
        <div><label>Status</label>
          <select value={form.status||''} onChange={e=>setForm({...form, status: e.target.value})}>
            <option value="AVAILABLE">AVAILABLE</option>
            <option value="ASSIGNED">ASSIGNED</option>
            <option value="UNDER_MAINTENANCE">UNDER_MAINTENANCE</option>
            <option value="RETIRED">RETIRED</option>
            <option value="LOST">LOST</option>
          </select>
        </div>
        <div><label>Assign Employee</label>
          <select value={(form.employeeId as string)||''} onChange={e=>setForm({...form, employeeId: e.target.value})}>
            <option value="">(none)</option>
            {employees.map(u=> <option key={u.id} value={u.id}>{u.firstName} {u.lastName}</option>)}
          </select>
        </div>
        <button>Create</button>
      </form>
      <form className="card" onSubmit={onAssign}>
        <h3>Assign Asset</h3>
        <div><label>Asset</label><select value={assign.assetId} onChange={e=>setAssign({...assign, assetId:(e.target.value)})}>
          <option value=''>Select asset</option>{items.map(a=><option key={a.id} value={a.id}>{a.name} #{a.id}</option>)}
        </select></div>
        <div><label>Employee</label><select value={assign.employeeId} onChange={e=>setAssign({...assign, employeeId:(e.target.value)})}>
          <option value=''>Select employee</option>{employees.map(u=><option key={u.id} value={u.id}>{u.firstName} {u.lastName}</option>)}
        </select></div>
        <button>Assign</button>
      </form>
      <form className="card" onSubmit={e=>{ e.preventDefault(); api.findWarrantyExpiringBetween(from, to).then(setExpiring).catch(()=>setExpiring([])) }}>
        <h3>Warranty Expiring</h3>
        <div><label>From</label><input type="date" value={from} onChange={e=>setFrom(e.target.value)} />
        <label style={{marginLeft:8}}>To</label><input type="date" value={to} onChange={e=>setTo(e.target.value)} />
        <button disabled={!from || !to}>Search</button></div>
        {!!expiring.length && <table style={{marginTop:8}}><thead><tr><th>ID</th><th>Name</th><th>Expiry</th></tr></thead>
        <tbody>{expiring.map(x=>(<tr key={x.id}><td>{x.id}</td><td>{x.name}</td><td>{x.warrantyExpiryDate}</td></tr>))}</tbody></table>}
      </form>
    </div>
  </div>
}

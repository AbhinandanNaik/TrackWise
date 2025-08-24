import { useEffect, useState, FormEvent } from 'react'
import * as svc from '../services/employeeService'

export default function Employees(){
  const [page, setPage] = useState(0)
  const [items, setItems] = useState<svc.EmployeeResponseDTO[]>([])
  const [total, setTotal] = useState(0)
  const [form, setForm] = useState<svc.EmployeeRequestDTO>({ firstName:'', lastName:'', email:'', phone:'', departmentId: undefined })
  const [searchEmail, setSearchEmail] = useState('')
  const [searchResult, setSearchResult] = useState<svc.EmployeeResponseDTO | null>(null)
  const [editing, setEditing] = useState<svc.EmployeeResponseDTO | null>(null)
  const [searchName, setSearchName] = useState('')
  const [deptId, setDeptId] = useState('')

  function load(){ svc.list(page, 20).then(p=>{ setItems(p.content); setTotal(p.totalElements) }).catch(()=>{ setItems([]); setTotal(0) }) }
  useEffect(load, [page])

  function onCreate(e:FormEvent){ e.preventDefault(); svc.create(form).then(()=>{ setForm({ firstName:'', lastName:'', email:'' }); load() }) }
  function onUpdate(u:svc.EmployeeResponseDTO){ setEditing(u) }
  function onDelete(u:svc.EmployeeResponseDTO){ if(confirm('Delete employee?')) svc.remove(u.id).then(load) }

  return <div className="grid-2">
    <div className="card">
      <h3>Employees</h3>
      <table><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Dept</th><th>Actions</th></tr></thead>
      <tbody>
        {items.map(u=>(<tr key={u.id}><td>{u.id}</td><td>{u.firstName} {u.lastName}</td><td>{u.email}</td><td>{u.departmentName||'-'}</td>
        <td><button onClick={()=>onUpdate(u)}>Edit</button> <button onClick={()=>onDelete(u)}>Delete</button></td></tr>))}
        {!items.length && <tr><td colSpan={5} className="small">No employees</td></tr>}
      </tbody></table>
      <div className="small" style={{display:'flex', gap:8, alignItems:'center'}}>
        <button disabled={page===0} onClick={()=>setPage(p=>p-1)}>Prev</button>
        <span>Page {page+1} / {Math.max(1, Math.ceil(total/20))}</span>
        <button disabled={(page+1) >= Math.ceil(total/20)} onClick={()=>setPage(p=>p+1)}>Next</button>
      </div>
    </div>
    {editing && <form className="card" onSubmit={e=>{ e.preventDefault(); svc.update(editing.id, {
      firstName: editing.firstName,
      lastName: editing.lastName,
      email: editing.email,
      phone: editing.phone,
      departmentId: (document.getElementById('edit-emp-dept-id') as HTMLInputElement)?.value || undefined,
    }).then(()=>{ setEditing(null); load() }) }}>
      <h3>Edit Employee</h3>
      <div><label>First Name</label><input value={editing.firstName} onChange={e=>setEditing({...editing!, firstName:e.target.value})} required/></div>
      <div><label>Last Name</label><input value={editing.lastName} onChange={e=>setEditing({...editing!, lastName:e.target.value})} required/></div>
      <div><label>Email</label><input type="email" value={editing.email} onChange={e=>setEditing({...editing!, email:e.target.value})} required/></div>
      <div><label>Phone</label><input value={editing.phone||''} onChange={e=>setEditing({...editing!, phone:e.target.value})} /></div>
      <div><label>Department ID</label><input id="edit-emp-dept-id" placeholder="UUID" defaultValue={editing.departmentId||''} /></div>
      <div className="row" style={{gap:8}}>
        <button>Save</button>
        <button type="button" onClick={()=>setEditing(null)}>Cancel</button>
      </div>
    </form>}
    <form id="create-employee" className="card" onSubmit={onCreate}>
      <h3>Create Employee</h3>
      <div><label>First Name</label><input value={form.firstName} onChange={e=>setForm({...form, firstName:e.target.value})} required/></div>
      <div><label>Last Name</label><input value={form.lastName} onChange={e=>setForm({...form, lastName:e.target.value})} required/></div>
      <div><label>Email</label><input type="email" value={form.email} onChange={e=>setForm({...form, email:e.target.value})} required/></div>
      <div><label>Phone</label><input value={form.phone||''} onChange={e=>setForm({...form, phone:e.target.value})}/></div>
      <div><label>Department ID</label><input placeholder="UUID" value={(form.departmentId as string)||''} onChange={e=>setForm({...form, departmentId: e.target.value})}/></div>
      <button>Create</button>
    </form>
    <form className="card" onSubmit={e=>{ e.preventDefault(); svc.findByEmail(searchEmail).then(setSearchResult).catch(()=>setSearchResult(null)) }}>
      <h3>Find by Email</h3>
      <div><label>Email</label><input type="email" value={searchEmail} onChange={e=>setSearchEmail(e.target.value)} /></div>
      <button>Search</button>
      {searchResult && <p className="small">Result: {searchResult.firstName} {searchResult.lastName} ({searchResult.email})</p>}
    </form>
    <form className="card" onSubmit={e=>{ e.preventDefault(); fetch(`/api/employees/search/by-name?name=${encodeURIComponent(searchName)}`, { headers:{ 'Authorization': `Bearer ${localStorage.getItem('tw_token')}` } }).then(r=>r.json()).then((p)=>{ setItems(p.content||[]); setTotal(p.totalElements||p.length||0) }).catch(()=>{}) }}>
      <h3>Search by Name</h3>
      <div className="row" style={{gap:8}}>
        <input placeholder="Name contains" value={searchName} onChange={e=>setSearchName(e.target.value)} />
        <button>Search</button>
      </div>
    </form>
    <form className="card" onSubmit={e=>{ e.preventDefault(); if(!editing || !deptId) return; fetch(`/api/employees/${editing.id}/assign-department?departmentId=${deptId}`, { method:'PUT', headers:{ 'Authorization': `Bearer ${localStorage.getItem('tw_token')}` } }).then(()=>{ setDeptId(''); load() }) }}>
      <h3>Assign Department</h3>
      <div><label>Employee (select above)</label><div className="small">{editing ? `${editing.firstName} ${editing.lastName}` : 'No employee selected'}</div></div>
      <div><label>Department ID</label><input value={deptId} onChange={e=>setDeptId(e.target.value)} placeholder="UUID" /></div>
      <button disabled={!editing || !deptId}>Assign</button>
    </form>
  </div>
}

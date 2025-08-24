import { useEffect, useState } from 'react'
import * as emp from '../services/employeeService'
import * as check from '../services/checkService'
import '../style/Dashboard.css'
import { request } from '../services/api'
import * as news from '../services/newsService'

export default function EmployeeDashboard(){
  const [me, setMe] = useState<emp.EmployeeResponseDTO | null>(null)
  const [history, setHistory] = useState<check.CheckInOutResponseDTO[]>([])
  const [current, setCurrent] = useState<check.CheckInOutResponseDTO | null>(null)
  const [articles, setArticles] = useState<news.Article[]>([])
  const [newsQuery, setNewsQuery] = useState('')
  const [assignedAssets, setAssignedAssets] = useState<{ id:string, name:string }[]>([])
  const [currentAssets, setCurrentAssets] = useState<{ id:string, name:string, checkOutTime?: string }[]>([])

  useEffect(()=>{
    async function init(){
      const storedId = localStorage.getItem('tw_employee_id') || ''
      if (storedId){
        try { const u = await emp.get(storedId); setMe(u); check.historyByEmployee(u.id).then(setHistory).catch(()=>setHistory([])); check.getCurrentCheckoutByEmployee(u.id).then(setCurrent).catch(()=>setCurrent(null)); return } catch {}
      }
      try {
        const meProfile = await request('/api/auth/me') as any
        if (meProfile?.employeeId){
          localStorage.setItem('tw_employee_id', meProfile.employeeId)
          const u = await emp.get(meProfile.employeeId)
          setMe(u)
          check.historyByEmployee(u.id).then(setHistory).catch(()=>setHistory([]))
          check.getCurrentCheckoutByEmployee(u.id).then(setCurrent).catch(()=>setCurrent(null))
          return
        }
        if (meProfile?.email){
          const u = await emp.findByEmail(meProfile.email)
          setMe(u)
          if (u){
            check.historyByEmployee(u.id).then(setHistory).catch(()=>setHistory([]))
            check.getCurrentCheckoutByEmployee(u.id).then(setCurrent).catch(()=>setCurrent(null))
          }
        }
      } catch {
        const username = localStorage.getItem('tw_username') || ''
        if (username){
          const u = await emp.findByEmail(username)
          setMe(u)
          if (u){
            check.historyByEmployee(u.id).then(setHistory).catch(()=>setHistory([]))
            check.getCurrentCheckoutByEmployee(u.id).then(setCurrent).catch(()=>setCurrent(null))
          }
        }
      }
    }
    init()
  },[])

  useEffect(()=>{
    // Derive unique assets ever assigned to the employee
    const seen = new Map<string,string>()
    for (const h of history){ if(h.assetId && h.assetName) seen.set(h.assetId, h.assetName) }
    setAssignedAssets(Array.from(seen, ([id, name])=>({ id, name })))
  }, [history])

  useEffect(()=>{
    // Derive all currently open (not checked in) assets for this employee
    const openByAsset = new Map<string, { id:string, name:string, checkOutTime?: string }>()
    for (const h of history){
      if (h.assetId && !h.checkInTime){
        if (!openByAsset.has(h.assetId)) openByAsset.set(h.assetId, { id: h.assetId, name: h.assetName, checkOutTime: h.checkOutTime })
      }
    }
    setCurrentAssets(Array.from(openByAsset.values()))
  }, [history])

  useEffect(()=>{
    // Auto-load news for the current asset name if available
    const keyword = current?.assetName || newsQuery
    if (keyword){ news.search(keyword).then(setArticles).catch(()=>setArticles([])) }
  }, [current, newsQuery])

  return <div className="dashboard-wrapper">
    <header className="dashboard-header">
      <h1>My Dashboard</h1>
      <p>Welcome back{me ? `, ${me.firstName}` : ''}</p>
    </header>

    {!me && <div className="card"><p className="small">No employee profile found for current user.</p><p className="small">If you just signed up, wait for admin approval or profile linkage.</p></div>}

    {me && <div className="dashboard-container">
      <div className="dashboard-card">
        <h3>Name</h3>
        <p>{me.firstName} {me.lastName}</p>
      </div>
      <div className="dashboard-card">
        <h3>Email</h3>
        <p style={{ overflowWrap:'anywhere' }}>{me.email}</p>
      </div>
      <div className="dashboard-card">
        <h3>Department</h3>
        <p>{me.departmentName || '-'}</p>
      </div>
      <div className="dashboard-card">
        <h3>Current Assets</h3>
        {currentAssets.length ? <ul style={{marginTop:4}}>
          {currentAssets.map(a=> <li key={a.id}><b style={{color:'#000'}}>{a.name}</b> <span className="small" style={{opacity:.7}}>{a.checkOutTime ? `(since ${a.checkOutTime})` : ''}</span></li>)}
        </ul> : <p>None</p>}
      </div>
    </div>}

    {me && <div className="grid-2" style={{marginTop:16}}>
      <div className="card">
        <h3>My Assigned Assets</h3>
        <ul style={{marginTop:8}}>
          {assignedAssets.map(a=> <li key={a.id}>{a.name} <span className="small" style={{opacity:.7}}>#{a.id}</span></li>)}
          {!assignedAssets.length && <li className="small">No assigned assets yet</li>}
        </ul>
      </div>
      <div className="card">
        <h3>News</h3>
        <div className="row" style={{gap:8, marginBottom:8}}>
          <input placeholder="Search news by keyword" value={newsQuery} onChange={e=>setNewsQuery(e.target.value)} />
          <button onClick={()=>setNewsQuery(newsQuery)}>Search</button>
        </div>
        <div style={{display:'grid', gap:8}}>
          {articles.map((a, i)=> (
            <div key={i} className="small" style={{border:'1px solid #eee', borderRadius:8, padding:8}}>
              <b>{a.title}</b>
              {a.description && <div style={{marginTop:4}}>{a.description}</div>}
              {a.url && <div style={{marginTop:4}}><a href={a.url} target="_blank">Read more</a></div>}
            </div>
          ))}
          {!articles.length && <div className="small">No news to display</div>}
        </div>
      </div>
    </div>}

    {me && <div className="card" style={{marginTop:16}}>
      <h3>My Check In/Out History</h3>
      <table><thead><tr><th>Checkout</th><th>Checkin</th><th>Asset</th></tr></thead>
        <tbody>{history.map((r,i)=>(<tr key={i}><td>{r.checkOutTime||'-'}</td><td>{r.checkInTime||'-'}</td><td>{r.assetName}</td></tr>))}
        {!history.length && <tr><td colSpan={3} className="small">No recent activity</td></tr>}</tbody></table>
    </div>}
  </div>
}



import { useEffect, useState } from 'react'
import * as emp from '../services/employeeService'
import * as check from '../services/checkService'

export default function EmployeeDashboard(){
  const [me, setMe] = useState<emp.EmployeeResponseDTO | null>(null)
  const [history, setHistory] = useState<check.CheckInOutResponseDTO[]>([])

  useEffect(()=>{
    const username = localStorage.getItem('tw_username') || ''
    // Use username as email if it looks like one
    if (username.includes('@')){
      emp.findByEmail(username).then(u=>{ setMe(u); if(u) check.historyByEmployee(u.id).then(setHistory).catch(()=>setHistory([])) }).catch(()=>setMe(null))
    }
  },[])

  return <div className="card">
    <h3>Employee Dashboard</h3>
    {!me && <p className="small">No employee profile found for current user.</p>}
    {me && <>
      <p><b>{me.firstName} {me.lastName}</b> — {me.email}</p>
      <h4>Check In/Out History</h4>
      <table><thead><tr><th>Checkout</th><th>Checkin</th><th>Asset</th></tr></thead>
        <tbody>{history.map((r,i)=>(<tr key={i}><td>{r.checkOutTime||'-'}</td><td>{r.checkInTime||'-'}</td><td>{r.assetName}</td></tr>))}
        {!history.length && <tr><td colSpan={3} className="small">No recent activity</td></tr>}</tbody></table>
    </>}
  </div>
}



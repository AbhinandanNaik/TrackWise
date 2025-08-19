import { useEffect, useState } from 'react'
import * as svc from '../services/warrantyService'
export default function Warranty(){
  const [items, setItems] = useState<svc.Alert[]>([])
  useEffect(()=>{ svc.alerts().then(setItems).catch(()=>setItems([])) }, [])
  return <div className="card">
    <h3>Warranty Alerts</h3>
    <table><thead><tr><th>Asset</th><th>Expiry</th><th>Days Left</th></tr></thead>
      <tbody>{items.map((w,i)=>(<tr key={i}><td>{w.assetName} #{w.assetId}</td><td>{w.warrantyExpiry}</td><td>{w.daysLeft}</td></tr>))}
      {!items.length && <tr><td colSpan={3} className="small">No upcoming expiries</td></tr>}</tbody></table>
  </div>
}

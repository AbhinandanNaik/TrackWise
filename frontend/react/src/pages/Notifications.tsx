import { useEffect, useState } from 'react'
import * as svc from '../services/notificationService'
export default function Notifications(){
  const [items, setItems] = useState<svc.Notification[]>([])
  useEffect(()=>{ svc.list().then(setItems).catch(()=>setItems([])) }, [])
  return <div className="card">
    <h3>Notifications</h3>
    <table><thead><tr><th>Time</th><th>Level</th><th>Message</th></tr></thead>
      <tbody>{items.map(n=>(<tr key={n.id}><td>{n.createdAt}</td><td>{n.level}</td><td>{n.message}</td></tr>))}
      {!items.length && <tr><td colSpan={3} className="small">No notifications</td></tr>}</tbody></table>
  </div>
}

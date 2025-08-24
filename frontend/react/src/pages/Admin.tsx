import { useState } from 'react'
import * as admin from '../services/adminService'

export default function Admin(){
  const [msg, setMsg] = useState('')
  return <div className="card">
    <h3>Admin Jobs</h3>
    <div style={{display:'flex', gap:8}}>
      <button onClick={()=>admin.runNewsScan().then(r=>setMsg(String(r))).catch(e=>setMsg(e.message))}>Run News Scan</button>
      <button onClick={()=>admin.runPerformanceAnalysis().then(r=>setMsg(String(r))).catch(e=>setMsg(e.message))}>Run Performance Analysis</button>
    </div>
    {msg && <p className="small" style={{marginTop:8}}>{msg}</p>}
  </div>
}



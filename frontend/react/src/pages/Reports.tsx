import { useState } from 'react'
import { downloadAssetAging, downloadWarrantyExpiry } from '../services/reportService'

export default function Reports(){
  const [from, setFrom] = useState('')
  const [to, setTo] = useState('')
  const [olderThanDays, setOlderThanDays] = useState(365)
  return <div className="card">
    <h3>Reports</h3>
    <div style={{display:'grid', gap:12}}>
      <div>
        <h4>Warranty Expiry</h4>
        <div>
          <label>From</label> <input type="date" value={from} onChange={e=>setFrom(e.target.value)} />
          <label style={{marginLeft:8}}>To</label> <input type="date" value={to} onChange={e=>setTo(e.target.value)} />
          <button disabled={!from || !to} onClick={()=>downloadWarrantyExpiry(from, to)}>Download</button>
        </div>
      </div>
      <div>
        <h4>Asset Aging</h4>
        <div>
          <label>Older Than Days</label> <input type="number" value={olderThanDays} onChange={e=>setOlderThanDays(Number(e.target.value))} />
          <button onClick={()=>downloadAssetAging(olderThanDays)}>Download</button>
        </div>
      </div>
    </div>
  </div>
}

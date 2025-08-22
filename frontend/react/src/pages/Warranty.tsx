import { useState } from 'react'
import * as warranty from '../services/warrantyService'
import * as assets from '../services/assetService'

export default function Warranty(){
  const [from, setFrom] = useState('')
  const [to, setTo] = useState('')
  const [expiring, setExpiring] = useState<warranty.WarrantyResponseDTO[]>([])
  const [assetId, setAssetId] = useState('')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [vendor, setVendor] = useState('')
  const [extendId, setExtendId] = useState('')
  const [extendDate, setExtendDate] = useState('')

  function search(){ warranty.findExpiringBetween(from, to).then(setExpiring).catch(()=>setExpiring([])) }

  return <div className="grid-2">
    <div className="card">
      <h3>Find Expiring Warranties</h3>
      <div><label>From</label><input type="date" value={from} onChange={e=>setFrom(e.target.value)} />
      <label style={{marginLeft:8}}>To</label><input type="date" value={to} onChange={e=>setTo(e.target.value)} />
      <button disabled={!from || !to} onClick={search}>Search</button></div>
      <table><thead><tr><th>ID</th><th>Asset</th><th>Start</th><th>End</th><th>Vendor</th></tr></thead>
        <tbody>{expiring.map(w=>(<tr key={w.warrantyId}><td>{w.warrantyId}</td><td>{w.assetName}</td><td>{w.startDate}</td><td>{w.endDate}</td><td>{w.vendor||'-'}</td></tr>))}
        {!expiring.length && <tr><td colSpan={5} className="small">No results</td></tr>}</tbody></table>
    </div>
    <div>
      <form className="card" onSubmit={e=>{ e.preventDefault(); warranty.createOrUpdate({ assetId, startDate, endDate, vendor }).then(()=>{ setAssetId(''); setStartDate(''); setEndDate(''); setVendor('') })}}>
        <h3>Create/Update Warranty</h3>
        <div><label>Asset ID</label><input value={assetId} onChange={e=>setAssetId(e.target.value)} required/></div>
        <div><label>Start</label><input type="date" value={startDate} onChange={e=>setStartDate(e.target.value)} required/></div>
        <div><label>End</label><input type="date" value={endDate} onChange={e=>setEndDate(e.target.value)} required/></div>
        <div><label>Vendor</label><input value={vendor} onChange={e=>setVendor(e.target.value)} /></div>
        <button>Save</button>
      </form>
      <form className="card" onSubmit={e=>{ e.preventDefault(); warranty.extendWarranty(extendId, extendDate).then(()=>{ setExtendId(''); setExtendDate('') })}}>
        <h3>Extend Warranty</h3>
        <div><label>Warranty ID</label><input value={extendId} onChange={e=>setExtendId(e.target.value)} required/></div>
        <div><label>New End Date</label><input type="date" value={extendDate} onChange={e=>setExtendDate(e.target.value)} required/></div>
        <button>Extend</button>
      </form>
    </div>
  </div>
}

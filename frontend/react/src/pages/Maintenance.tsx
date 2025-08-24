import { useEffect, useState, FormEvent } from 'react'
import * as svc from '../services/maintenanceService'
import * as assets from '../services/assetService'

export default function Maintenance(){
  const [selectedAssetId, setSelectedAssetId] = useState('')
  const [items, setItems] = useState<svc.MaintenanceResponseDTO[]>([])
  const [assetPage, setAssetPage] = useState<assets.Page<assets.AssetResponse>>({ content: [], number: 0, size: 20, totalElements: 0, totalPages: 0 })
  const [form, setForm] = useState<svc.MaintenanceRequestDTO>({ description:'' })

  useEffect(() => { assets.list(0, 100).then(p=>setAssetPage(p)).catch(()=>setAssetPage({content:[],number:0,size:20,totalElements:0,totalPages:0})) }, [])
  useEffect(() => { if (selectedAssetId) svc.listByAsset(selectedAssetId).then(setItems).catch(()=>setItems([])) }, [selectedAssetId])

  function onCreate(e:FormEvent){ e.preventDefault(); if(!selectedAssetId) return; svc.addMaintenance(selectedAssetId, form).then(()=>{ setForm({ description:'' }); svc.listByAsset(selectedAssetId).then(setItems) }) }

  return <div className="grid-2">
    <div className="card">
      <h3>Maintenance</h3>
      <div style={{marginBottom:12}}>
        <label>Asset</label>
        <select value={selectedAssetId} onChange={e=>setSelectedAssetId(e.target.value)}>
          <option value=''>Select asset</option>{assetPage.content.map(a=> <option key={a.id} value={a.id}>{a.name} #{a.id}</option>)}
        </select>
      </div>
      <table><thead><tr><th>Log ID</th><th>Asset</th><th>Description</th><th>Date</th><th>Performed By</th></tr></thead>
      <tbody>{items.map(m=>(<tr key={m.logId}><td>{m.logId}</td><td>{m.assetName}</td><td>{m.description}</td><td>{m.maintenanceDate||'-'}</td><td>{m.performedBy||'-'}</td></tr>))}
      {!items.length && <tr><td colSpan={5} className="small">No maintenance records</td></tr>}</tbody></table>
    </div>
    <form className="card" onSubmit={onCreate}>
      <h3>Log Maintenance</h3>
      <div><label>Description</label><textarea value={form.description} onChange={e=>setForm({...form, description:e.target.value})} required/></div>
      <div><label>Date</label><input type="date" value={form.maintenanceDate||''} onChange={e=>setForm({...form, maintenanceDate:e.target.value})} /></div>
      <div><label>Performed By</label><input value={form.performedBy||''} onChange={e=>setForm({...form, performedBy:e.target.value})} /></div>
      <button disabled={!selectedAssetId}>Create</button>
    </form>
  </div>
}

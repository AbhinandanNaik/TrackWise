import { useEffect, useState } from 'react'
import * as assets from '../services/assetService'
import { useAuth } from '../context/AuthContext'
import '../style/Dashboard.css'

export default function Dashboard() {
  const { role } = useAuth()
  const [assetCount, setAssetCount] = useState(0)
  const [availableCount, setAvailableCount] = useState(0)
  const [assignedCount, setAssignedCount] = useState(0)
  const [warrantyAlerts, setWarrantyAlerts] = useState(0)

  useEffect(() => {
    const today = new Date()
    const toDate = new Date(today)
    toDate.setDate(today.getDate() + 30)
    const fmt = (d: Date) => d.toISOString().slice(0,10)

    assets.list(0, 1).then(r => setAssetCount(r.totalElements)).catch(() => setAssetCount(0))
    assets.findByStatus('AVAILABLE').then(r => setAvailableCount(r.length)).catch(() => setAvailableCount(0))
    assets.findByStatus('ASSIGNED').then(r => setAssignedCount(r.length)).catch(() => setAssignedCount(0))
    assets.findWarrantyExpiringBetween(fmt(today), fmt(toDate)).then(r => setWarrantyAlerts(r.length)).catch(() => setWarrantyAlerts(0))
  }, [])

  return (
    <div className="dashboard-wrapper">
      <header className="dashboard-header">
        <h1>TrackWise</h1>
        <p>Asset Management Dashboard</p>
      </header>

      <div className="dashboard-container">
        <div className="dashboard-card">
          <h3>Assets</h3>
          <p>{assetCount}</p>
        </div>
        {role==='ROLE_ADMIN' && <div className="dashboard-card">
          <h3>Available</h3>
          <p>{availableCount}</p>
        </div>}
        {role==='ROLE_ADMIN' && <div className="dashboard-card">
          <h3>Assigned</h3>
          <p>{assignedCount}</p>
        </div>}
        <div className="dashboard-card">
          <h3>Warranty Alerts (30d)</h3>
          <p>{warrantyAlerts}</p>
        </div>
      </div>
      {role==='ROLE_ADMIN' && <div className="card" style={{marginTop:16}}>
        <h3>Quick Actions</h3>
        <div className="row" style={{gap:12, flexWrap:'wrap'}}>
          <a href="/employees#create-employee">Add Employee</a>
          <a href="/assets#create-asset">Add Asset</a>
          <a href="/assets">Manage Assets</a>
          <a href="/employees">Manage Employees</a>
          <a href="/check">Check In/Out</a>
          <a href="/maintenance">Log Maintenance</a>
          <a href="/reports">Download Reports</a>
          <a href="/warranty">Manage Warranties</a>
        </div>
      </div>}
    </div>
  )
}

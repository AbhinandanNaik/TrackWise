import { useEffect, useState } from 'react'
import * as assets from '../services/assetService'
import * as maintenance from '../services/maintenanceService'
import * as warranty from '../services/warrantyService'
import * as notifications from '../services/notificationService'
import '../style/Dashboard.css'

export default function Dashboard() {
  const [assetCount, setAssetCount] = useState(0)
  const [openMaint, setOpenMaint] = useState(0)
  const [alerts, setAlerts] = useState(0)
  const [notifs, setNotifs] = useState(0)

  useEffect(() => {
    Promise.all([
      assets.list().then(r => setAssetCount(r.length)).catch(() => setAssetCount(0)),
      maintenance.list().then(r => setOpenMaint(r.filter(x => x.status !== 'DONE').length)).catch(() => setOpenMaint(0)),
      warranty.alerts().then(r => setAlerts(r.length)).catch(() => setAlerts(0)),
      notifications.list().then(r => setNotifs(r.length)).catch(() => setNotifs(0)),
    ])
  }, [])

  return (
    <div className="dashboard-wrapper">
      <header className="dashboard-header">
        <h1>GoDigit</h1>
        <p>Asset Management Dashboard</p>
      </header>

      <div className="dashboard-container">
        <div className="dashboard-card">
          <h3>Assets</h3>
          <p>{assetCount}</p>
        </div>
        <div className="dashboard-card">
          <h3>Open Maintenance</h3>
          <p>{openMaint}</p>
        </div>
        <div className="dashboard-card">
          <h3>Warranty Alerts</h3>
          <p>{alerts}</p>
        </div>
        <div className="dashboard-card">
          <h3>Notifications</h3>
          <p>{notifs}</p>
        </div>
      </div>
    </div>
  )
}

import { Routes, Route, Navigate, Link } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import NavBar from './components/NavBar'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Assets from './pages/Assets'
import Employees from './pages/Employees'
import CheckInOut from './pages/CheckInOut'
import Maintenance from './pages/Maintenance'
import Reports from './pages/Reports'
import Warranty from './pages/Warranty'
import Notifications from './pages/Notifications'

// Protected route wrapper
function Protected({ children }: { children: JSX.Element }) {
  const { token } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  return children
}

export default function App() {
  return (
    <AuthProvider>
      <NavBar />
      <div className="container">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<Dashboard />} /> {/* Public Dashboard */}
          <Route path="/assets" element={<Protected><Assets /></Protected>} />
          <Route path="/employees" element={<Protected><Employees /></Protected>} />
          <Route path="/check" element={<Protected><CheckInOut /></Protected>} />
          <Route path="/maintenance" element={<Protected><Maintenance /></Protected>} />
          <Route path="/reports" element={<Protected><Reports /></Protected>} />
          <Route path="/warranty" element={<Protected><Warranty /></Protected>} />
          <Route path="/notifications" element={<Protected><Notifications /></Protected>} />
          <Route
            path="*"
            element={
              <div className="container">
                <h3>Not Found</h3>
                <Link to="/">Go Home</Link>
              </div>
            }
          />
        </Routes>
      </div>
    </AuthProvider>
  )
}

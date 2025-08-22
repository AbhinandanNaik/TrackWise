import { Routes, Route, Link } from 'react-router-dom'
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
import Admin from './pages/Admin'
import IoT from './pages/IoT'
import Signup from './pages/Signup'
import EmployeeDashboard from './pages/EmployeeDashboard'

// Protected route wrapper
import { Navigate } from 'react-router-dom'
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
          <Route path="/signup" element={<Signup />} />
          <Route path="/" element={<Protected><Dashboard /></Protected>} />
          <Route path="/assets" element={<Protected><Assets /></Protected>} />
          <Route path="/employees" element={<Protected><Employees /></Protected>} />
          <Route path="/check" element={<Protected><CheckInOut /></Protected>} />
          <Route path="/maintenance" element={<Protected><Maintenance /></Protected>} />
          <Route path="/reports" element={<Protected><Reports /></Protected>} />
          <Route path="/warranty" element={<Protected><Warranty /></Protected>} />
          <Route path="/notifications" element={<Protected><Notifications /></Protected>} />
          <Route path="/admin" element={<Protected><Admin /></Protected>} />
          <Route path="/iot" element={<Protected><IoT /></Protected>} />
          <Route path="/me" element={<Protected><EmployeeDashboard /></Protected>} />
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

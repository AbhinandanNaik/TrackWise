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

function AdminOnly({ children }: { children: JSX.Element }){
  const { token, role } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (role !== 'ROLE_ADMIN') return <Navigate to="/" replace />
  return children
}

function UserOnly({ children }: { children: JSX.Element }){
  const { token, role } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (role === 'ROLE_ADMIN') return <Navigate to="/" replace />
  return children
}

function Home(){
  const { role } = useAuth()
  return role === 'ROLE_ADMIN' ? <Dashboard/> : <EmployeeDashboard/>
}

export default function App() {
  return (
    <AuthProvider>
      <NavBar />
      <div className="container">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/" element={<Protected><Home /></Protected>} />
          <Route path="/assets" element={<AdminOnly><Assets /></AdminOnly>} />
          <Route path="/employees" element={<AdminOnly><Employees /></AdminOnly>} />
          <Route path="/check" element={<AdminOnly><CheckInOut /></AdminOnly>} />
          <Route path="/maintenance" element={<AdminOnly><Maintenance /></AdminOnly>} />
          <Route path="/reports" element={<AdminOnly><Reports /></AdminOnly>} />
          <Route path="/warranty" element={<AdminOnly><Warranty /></AdminOnly>} />
          <Route path="/notifications" element={<AdminOnly><Notifications /></AdminOnly>} />
          <Route path="/admin" element={<AdminOnly><Admin /></AdminOnly>} />
          <Route path="/iot" element={<AdminOnly><IoT /></AdminOnly>} />
          <Route path="/me" element={<UserOnly><EmployeeDashboard /></UserOnly>} />
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

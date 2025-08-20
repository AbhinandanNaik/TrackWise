import { Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
export default function NavBar(){
  const { token, logout } = useAuth()
  const loc = useLocation()
  const isLogin = loc.pathname === '/login'
  return <nav>
    <b><Link to="/">TrackWise</Link></b>
    {!isLogin && token && <>
      <Link to="/">Dashboard</Link><Link to="/assets">Assets</Link><Link to="/employees">Employees</Link>
      <Link to="/check">Check In/Out</Link><Link to="/maintenance">Maintenance</Link><Link to="/reports">Reports</Link>
      <Link to="/warranty">Warranty</Link><Link to="/notifications">Notifications</Link>
    </>}
    <span style={{marginLeft:'auto'}}>{token ? <button onClick={logout}>Logout</button> : <Link to="/login">Login</Link>}</span>
  </nav>
}

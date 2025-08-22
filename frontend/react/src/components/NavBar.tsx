import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
export default function NavBar(){
  const { token, role, logout } = useAuth()
  return <nav>
    <b><Link to="/">TrackWise</Link></b>
    {token && <>
      <Link to="/">Dashboard</Link>
      {role==='ROLE_ADMIN' && <>
        <Link to="/assets">Assets</Link><Link to="/employees">Employees</Link>
        <Link to="/check">Check In/Out</Link><Link to="/maintenance">Maintenance</Link><Link to="/reports">Reports</Link>
        <Link to="/warranty">Warranty</Link><Link to="/notifications">Notifications</Link><Link to="/iot">IoT</Link><Link to="/admin">Admin</Link>
      </>}
      <Link to="/me">My Dashboard</Link>
    </>}
    <span style={{marginLeft:'auto'}}>{token ? <button onClick={logout}>Logout</button> : <><Link to="/login">Login</Link> <Link to="/signup">Signup</Link></>}</span>
  </nav>
}

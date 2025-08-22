import { useState, FormEvent } from 'react'
import { register } from '../services/authService'

export default function Signup(){
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  async function onSubmit(e:FormEvent){
    e.preventDefault()
    setError(null); setSuccess(null)
    try{
      const res = await register({ firstName, lastName, email, phone, username, password })
      setSuccess('Account created for '+res.username)
    }catch(err:any){ setError(err.message||'Registration failed') }
  }

  return <div className="login-container">
    <div className="login-card">
      <h2 className="login-title">Sign Up</h2>
      <form onSubmit={onSubmit} className="login-form">
        {error && <div className="error-message">{error}</div>}
        {success && <div className="small" style={{color:'#22c55e'}}>{success}</div>}
        <div className="form-group"><label>First Name</label><input value={firstName} onChange={e=>setFirstName(e.target.value)} required/></div>
        <div className="form-group"><label>Last Name</label><input value={lastName} onChange={e=>setLastName(e.target.value)} required/></div>
        <div className="form-group"><label>Email</label><input type="email" value={email} onChange={e=>setEmail(e.target.value)} required/></div>
        <div className="form-group"><label>Phone no</label><input value={phone} onChange={e=>setPhone(e.target.value)} /></div>
        <div className="form-group"><label>Username</label><input value={username} onChange={e=>setUsername(e.target.value)} required/></div>
        <div className="form-group"><label>Password</label><input type="password" value={password} onChange={e=>setPassword(e.target.value)} required/></div>
        <button className="login-button">Create Account</button>
      </form>
    </div>
  </div>
}



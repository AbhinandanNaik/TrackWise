import React, { createContext, useContext, useEffect, useState } from 'react'
import { login as apiLogin } from '../services/authService'
type Ctx = { token:string|null, role:string|null, login:(username:string, password:string)=>Promise<void>, logout:()=>void }
const Ctx = createContext<Ctx>({ token:null, role:null, login: async ()=>{}, logout: ()=>{} })
export function AuthProvider({ children }:{ children: React.ReactNode }){
  const [token, setToken] = useState<string | null>(localStorage.getItem('tw_token'))
  const [role, setRole] = useState<string | null>(localStorage.getItem('tw_role'))
  useEffect(()=>{ if(token) localStorage.setItem('tw_token', token); else localStorage.removeItem('tw_token') }, [token])
  useEffect(()=>{ if(role) localStorage.setItem('tw_role', role); else localStorage.removeItem('tw_role') }, [role])
  async function login(username:string, password:string){ const t = await apiLogin(username, password); setToken(t); const r = localStorage.getItem('tw_role'); if(r) setRole(r); localStorage.setItem('tw_username', username) }
  function logout(){ setToken(null); setRole(null); localStorage.removeItem('tw_username'); localStorage.removeItem('tw_role') }
  return <Ctx.Provider value={{ token, role, login, logout }}>{children}</Ctx.Provider>
}
export function useAuth(){ return useContext(Ctx) }

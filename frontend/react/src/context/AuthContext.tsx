import React, { createContext, useContext, useEffect, useState } from 'react'
import { login as apiLogin } from '../services/authService'
type Ctx = { token:string|null, login:(email:string, password:string)=>Promise<void>, logout:()=>void }
const Ctx = createContext<Ctx>({ token:null, login: async ()=>{}, logout: ()=>{} })
export function AuthProvider({ children }:{ children: React.ReactNode }){
  const [token, setToken] = useState<string | null>(localStorage.getItem('tw_token'))
  useEffect(()=>{ if(token) localStorage.setItem('tw_token', token); else localStorage.removeItem('tw_token') }, [token])
  async function login(email:string, password:string){ const t = await apiLogin(email, password); setToken(t) }
  function logout(){ setToken(null) }
  return <Ctx.Provider value={{ token, login, logout }}>{children}</Ctx.Provider>
}
export function useAuth(){ return useContext(Ctx) }

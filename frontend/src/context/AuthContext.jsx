import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import axios from 'axios'

// Backend base URL (can be overridden via .env)
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// Axios instance
export const api = axios.create({ baseURL: API_BASE })

// Attach JWT if present
api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // Check who I am on initial load
  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (!token) { setLoading(false); return }
    api.get('/api/auth/me')
      .then(res => setUser(res.data))
      .catch(() => { localStorage.removeItem('accessToken'); setUser(null) })
      .finally(() => setLoading(false))
  }, [])

  const login = async (credentials) => {
    const res = await api.post('/api/auth/login', credentials)
    const token = res.data.accessToken || res.data.token
    localStorage.setItem('accessToken', token)
    const me = await api.get('/api/auth/me')
    setUser(me.data)
    return me.data // return user so callers can route by role
  }

  const register = async (payload) => {
    const res = await api.post('/api/auth/register', payload)
    const token = res.data.accessToken || res.data.token
    localStorage.setItem('accessToken', token)
    const me = await api.get('/api/auth/me')
    setUser(me.data)
    return me.data // return user so callers can route by role
  }

  const logout = () => {
    localStorage.removeItem('accessToken')
    setUser(null)
  }

  const value = useMemo(() => ({ user, loading, login, register, logout }), [user, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)

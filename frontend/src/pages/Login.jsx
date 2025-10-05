import { useForm } from 'react-hook-form'
import { useAuth } from '../context/AuthContext.jsx'
import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'

export default function Login() {
  const { register, handleSubmit } = useForm()
  const { login } = useAuth()
  const navigate = useNavigate()
  const [showPw, setShowPw] = useState(false)

  const onSubmit = async (data) => {
    try {
      const me = await login(data)
      if (me?.role === 'tutor') navigate('/dashboard-tutor')
      else navigate('/dashboard-student')
    } catch {
      alert('Login failed')
    }
  }

  return (
    <div className="auth-wrap">
      <div className="card glass auth-card">
        <h1 className="form-title">Login</h1>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="field">
            <label>Email</label>
            <input className="input" placeholder="you@example.com" {...register('email', { required: true })} />
          </div>

          <div className="field pw-row">
            <label>Password</label>
            <input
              className="input"
              type={showPw ? 'text' : 'password'}
              placeholder="••••••••"
              {...register('password', { required: true })}
            />
            <button type="button" className="pw-toggle" onClick={() => setShowPw(s => !s)}>
              {showPw ? 'Hide' : 'Show'}
            </button>
          </div>

          <div className="helper-row">
            <div className="row" style={{gap:8}}>
              <Link to="/login-student">Student Login</Link>
              <span style={{color:'var(--muted)'}}> | </span>
              <Link to="/login-tutor">Tutor Login</Link>
            </div>
            <button className="btn btn--primary" type="submit">Login</button>
          </div>
        </form>

        <p className="hint" style={{marginTop:12}}>
          No account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  )
}

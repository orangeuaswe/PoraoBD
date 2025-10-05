import { useForm } from 'react-hook-form'
import { useAuth } from '../context/AuthContext.jsx'
import { useNavigate } from 'react-router-dom'
import { useState } from 'react'

export default function Register() {
  const { register: reg, handleSubmit } = useForm()
  const { register: registerUser } = useAuth()
  const navigate = useNavigate()
  const [showPw, setShowPw] = useState(false)

  const onSubmit = async (data) => {
    try {
      const me = await registerUser(data)
      if (me?.role === 'tutor') navigate('/dashboard-tutor')
      else navigate('/dashboard-student')
    } catch {
      alert('Registration failed')
    }
  }

  return (
    <div className="auth-wrap">
      <div className="card glass auth-card">
        <h1 className="form-title">Register</h1>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="field">
            <label>Name</label>
            <input className="input" placeholder="Full name" {...reg('name', { required: true })} />
          </div>

          <div className="field">
            <label>Email</label>
            <input className="input" placeholder="you@example.com" {...reg('email', { required: true })} />
          </div>

          <div className="field pw-row">
            <label>Password</label>
            <input
              className="input"
              type={showPw ? 'text' : 'password'}
              placeholder="Create a password"
              {...reg('password', { required: true, minLength: 6 })}
            />
            <button type="button" className="pw-toggle" onClick={() => setShowPw(s => !s)}>
              {showPw ? 'Hide' : 'Show'}
            </button>
          </div>

          <div className="field">
            <label>Role</label>
            <select className="input" {...reg('role', { required: true })}>
              <option value="student">Student</option>
              <option value="tutor">Tutor</option>
            </select>
            <p className="hint">You can complete your profile after sign up.</p>
          </div>

          <div className="helper-row">
            <div />
            <button className="btn btn--primary" type="submit">Sign Up</button>
          </div>
        </form>
      </div>
    </div>
  )
}

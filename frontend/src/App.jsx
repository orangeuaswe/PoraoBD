import { Routes, Route, Navigate, Link, useNavigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext.jsx'

import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import LoginStudent from './pages/LoginStudent.jsx'
import LoginTutor from './pages/LoginTutor.jsx'
import Tutors from './pages/Tutors.jsx'
import TutorDetail from './pages/TutorDetail.jsx'
import Chat from './pages/Chat.jsx'
import DashboardStudent from './pages/DashboardStudent.jsx'
import DashboardTutor from './pages/DashboardTutor.jsx'

function Protected({ children, role }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="container" style={{padding:'24px'}}>Loading…</div>
  if (!user) return <Navigate to="/login" replace />
  if (role && user.role !== role) return <Navigate to="/" replace />
  return children
}

function Navbar(){
  const { user, logout } = useAuth()
  const nav = useNavigate()
  return (
    <header className="nav">
      <div className="container nav-inner">
        {/* Logo only, larger & prominent */}
        <Link className="brand" to="/" aria-label="PoraoBD Home">
          <img className="logo-img" src="/porabd-logo.png" alt="PoraoBD" />
        </Link>

        <nav className="row" style={{gap:16}}>
          <Link to="/tutors">Tutors</Link>
          {user ? (
            <>
              {user.role === 'student' && <Link to="/dashboard-student">Student</Link>}
              {user.role === 'tutor' && <Link to="/dashboard-tutor">Tutor</Link>}
              <Link to="/chat">Chat</Link>
              <button className="btn btn--ghost" onClick={logout}>Logout</button>
              <button className="btn btn--primary" onClick={()=>nav('/tutors')}>Find a Tutor</button>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
              <button className="btn btn--primary" onClick={()=>nav('/tutors')}>Find a Tutor</button>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}

function Home(){
  const { user } = useAuth()
  const nav = useNavigate()

  return (
    <section className="hero-landing">
      <div className="hero-inner">
        <div className="container">
          <div className="hero-grid">
            <div className="card glass">
              <span className="pill pill--accent">Premier tutor network</span>
              <h1 style={{marginTop:8}}>
                Find the <span className="text-gradient">right tutor</span> for your goals
              </h1>
              <p style={{color:'var(--muted)', lineHeight:1.7}}>
                Smart search by subject, location and budget. Verified profiles, reviews and secure payments.
                Students and tutors each get a focused dashboard with chat and bookings.
              </p>

              <div className="row" style={{gap:12, marginTop:10}}>
                <button className="btn btn--primary" onClick={()=>nav('/tutors')}>Browse Tutors</button>
                {!user && <button className="btn btn--white" onClick={()=>nav('/register')}>Become a Tutor</button>}
                <span className="pill pill--light">A touch of white ✨</span>
              </div>

              <div className="row" style={{gap:10, marginTop:10}}>
                <span className="badge">Verified profiles</span>
                <span className="badge">AI recommendations</span>
                <span className="badge">Secure payments</span>
              </div>
            </div>

            <div className="card glass">
              <h3>How it works</h3>
              <ol style={{margin:'2px 0 0 18px', color:'var(--muted)'}}>
                <li>Search or get AI matches for tutors.</li>
                <li>Open chat to discuss schedule and fit.</li>
                <li>Book and pay securely, then learn!</li>
              </ol>

              <div style={{marginTop:16}}>
                {user ? (
                  user.role === 'student'
                    ? <button className="btn btn--success" onClick={()=>nav('/dashboard-student')}>Go to Student Dashboard</button>
                    : <button className="btn btn--success" onClick={()=>nav('/dashboard-tutor')}>Go to Tutor Dashboard</button>
                ) : (
                  <button className="btn btn--ghost" onClick={()=>nav('/login')}>Have an account? Log in</button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default function App(){
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home/>} />
        <Route path="/login" element={<div className="container" style={{padding:'28px 0'}}><Login /></div>} />
        <Route path="/register" element={<div className="container" style={{padding:'28px 0'}}><Register /></div>} />
        <Route path="/login-student" element={<div className="container" style={{padding:'28px 0'}}><LoginStudent /></div>} />
        <Route path="/login-tutor" element={<div className="container" style={{padding:'28px 0'}}><LoginTutor /></div>} />

        <Route path="/tutors" element={<div className="container" style={{padding:'28px 0'}}><Tutors /></div>} />
        <Route path="/tutors/:id" element={<div className="container" style={{padding:'28px 0'}}><TutorDetail /></div>} />

        <Route path="/chat" element={<div className="container" style={{padding:'28px 0'}}><Protected><Chat /></Protected></div>} />
        <Route path="/dashboard-student" element={<div className="container" style={{padding:'28px 0'}}><Protected role="student"><DashboardStudent /></Protected></div>} />
        <Route path="/dashboard-tutor" element={<div className="container" style={{padding:'28px 0'}}><Protected role="tutor"><DashboardTutor /></Protected></div>} />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      <footer style={{borderTop:'1px solid var(--border)', marginTop:32}}>
        <div className="container" style={{padding:'18px 0', color:'var(--muted)'}}>
          © {new Date().getFullYear()} PoraoBD — connecting students & tutors across
        </div>
      </footer>
    </>
  )
}

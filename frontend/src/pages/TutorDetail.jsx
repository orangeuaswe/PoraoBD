import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { api } from '../context/AuthContext.jsx'
import { useAuth } from '../context/AuthContext.jsx'

export default function TutorDetail() {
  const { id } = useParams()
  const nav = useNavigate()
  const { user } = useAuth()
  const [tutor, setTutor] = useState(null)

  useEffect(() => { api.get(`/api/tutors/${id}`).then(res => setTutor(res.data)).catch(()=>setTutor(null)) }, [id])
  if (!tutor) return <div className="card">Loading…</div>

  return (
    <div className="grid grid-2">
      <div className="card">
        <h1 style={{marginBottom:4}}>{tutor.name}</h1>
        <div style={{color:'var(--muted)'}}>{(tutor.subjects||[]).join(', ')}</div>
        <p style={{marginTop:12, lineHeight:1.6}}>{tutor.bio || 'No bio provided.'}</p>

        <div className="row" style={{gap:10, marginTop:16}}>
          <span className="pill">City: {tutor.city || '—'}</span>
          <span className="pill">Rate: BDT {tutor.hourlyRate || '—'}/hr</span>
          <span className="pill">Rating: {tutor.rating ?? '—'}</span>
        </div>

        <div style={{marginTop:16}}>
          <h3>Availability</h3>
          <ul style={{margin:0, paddingLeft:18, color:'var(--muted)'}}>
            {(tutor.availability||[]).map((s,i)=><li key={i}>{s}</li>)}
            {(!tutor.availability || tutor.availability.length===0) && <li>Ask the tutor about availability.</li>}
          </ul>
        </div>
      </div>

      <aside className="card stack">
        <h3>Next step</h3>
        <button
          className="btn btn--primary"
          onClick={() => nav('/chat', { state: { toUserId: tutor.userId || tutor.id } })}
          disabled={!user}
          title={user ? '' : 'Login to chat'}
        >
          Message Tutor
        </button>
        <button className="btn" onClick={() => alert('Booking flow can call /api/bookings')}>Request Booking</button>
        {!user && <div style={{color:'var(--muted)'}}>You must log in to start a chat.</div>}
      </aside>
    </div>
  )
}

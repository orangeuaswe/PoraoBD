import { useEffect, useState } from 'react'
import { api } from '../context/AuthContext.jsx'

export default function DashboardTutor() {
  const [profile, setProfile] = useState({ subjects: [], hourlyRate: '', bio: '', city: '' })
  const [requests, setRequests] = useState([])

  useEffect(() => {
    api.get('/api/tutors/me').then(res => setProfile(res.data || {}))
    api.get('/api/bookings?role=tutor').then(res => setRequests(res.data?.requests || res.data || []))
  }, [])

  const save = async () => {
    await api.patch('/api/tutors/me', profile)
    alert('Profile updated')
  }

  return (
    <div className="grid grid-2">
      <section className="card stack">
        <h2>Your Profile</h2>
        <div className="grid grid-2">
          <div className="stack">
            <label>Subjects (comma-separated)</label>
            <input className="input" value={profile.subjects?.join(', ') || ''} onChange={e=>setProfile(p=>({...p, subjects:e.target.value.split(',').map(s=>s.trim()).filter(Boolean)}))}/>
          </div>
          <div className="stack">
            <label>Hourly Rate (BDT)</label>
            <input className="input" value={profile.hourlyRate || ''} onChange={e=>setProfile(p=>({...p, hourlyRate:e.target.value}))}/>
          </div>
          <div className="stack">
            <label>City</label>
            <input className="input" value={profile.city || ''} onChange={e=>setProfile(p=>({...p, city:e.target.value}))}/>
          </div>
          <div className="stack" style={{gridColumn:'1 / -1'}}>
            <label>Bio</label>
            <textarea className="input" rows="5" value={profile.bio || ''} onChange={e=>setProfile(p=>({...p, bio:e.target.value}))}/>
          </div>
        </div>
        <div className="row" style={{gap:10}}>
          <button className="btn btn--primary" onClick={save}>Save Profile</button>
          <span className="pill">Profile completeness: 80%</span>
        </div>
      </section>

      <aside className="card">
        <h2>Booking Requests</h2>
        <div className="stack">
          {requests.map(r => (
            <div key={r.id} className="row" style={{justifyContent:'space-between'}}>
              <div>
                <div className="tutor-name">{r.studentName}</div>
                <div className="tutor-subjects">{r.subject} — {r.slot}</div>
              </div>
              <div className="row" style={{gap:8}}>
                <button className="btn btn--ghost" onClick={()=>api.post(`/api/bookings/${r.id}/reject`).then(()=>location.reload())}>Reject</button>
                <button className="btn btn--primary" onClick={()=>api.post(`/api/bookings/${r.id}/accept`).then(()=>location.reload())}>Accept</button>
              </div>
            </div>
          ))}
          {requests.length===0 && <div style={{color:'var(--muted)'}}>No pending requests.</div>}
        </div>
      </aside>
    </div>
  )
}

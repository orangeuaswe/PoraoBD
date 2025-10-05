import { useEffect, useState } from 'react'
import { api } from '../context/AuthContext.jsx'

export default function DashboardStudent() {
  const [bookings, setBookings] = useState([])
  const [history, setHistory] = useState([])
  const [feed, setFeed] = useState([])

  useEffect(() => {
    api.get('/api/bookings?role=student').then(res => setBookings(res.data?.active || res.data || []))
    api.get('/api/bookings/history?role=student').then(res => setHistory(res.data || []))

    // Recommendations: try AI match, then fallback to recommended list
    (async () => {
      try {
        const { data } = await api.post('/api/match', { query: '' })
        setFeed(data?.results || data || [])
      } catch {
        const { data } = await api.get('/api/tutors?recommended=true')
        setFeed(data?.content || data || [])
      }
    })()
  }, [])

  return (
    <div className="grid grid-2">
      <section className="card">
        <h2>Current Tutors / Bookings</h2>
        <table className="table">
          <thead><tr><th>Tutor</th><th>Subject</th><th>Time</th><th>Price</th><th>Status</th></tr></thead>
          <tbody>
            {bookings.map(b=>(
              <tr key={b.id}>
                <td>{b.tutorName}</td><td>{b.subject}</td><td>{b.slot}</td><td>BDT {b.price}</td><td>{b.status}</td>
              </tr>
            ))}
            {bookings.length===0 && <tr><td colSpan="5" style={{color:'var(--muted)'}}>No active bookings.</td></tr>}
          </tbody>
        </table>

        <h2 style={{marginTop:24}}>Past Tutors</h2>
        <table className="table">
          <thead><tr><th>Tutor</th><th>Subject</th><th>Completed</th></tr></thead>
          <tbody>
            {history.map(h=>(
              <tr key={h.id}><td>{h.tutorName}</td><td>{h.subject}</td><td>{h.completedAt}</td></tr>
            ))}
            {history.length===0 && <tr><td colSpan="3" style={{color:'var(--muted)'}}>No past sessions.</td></tr>}
          </tbody>
        </table>
      </section>

      <aside className="card">
        <h2>Recommended for you</h2>
        <div className="stack">
          {feed.map(t => (
            <div key={t.id} className="row" style={{justifyContent:'space-between'}}>
              <div>
                <div className="tutor-name">{t.name}</div>
                <div className="tutor-subjects">{(t.subjects||[]).join(', ')}</div>
              </div>
              <div className="row" style={{gap:8}}>
                <span className="pill">BDT {t.hourlyRate}/hr</span>
                <a className="btn btn--ghost" href={`/tutors/${t.id}`}>View</a>
              </div>
            </div>
          ))}
          {feed.length===0 && <div style={{color:'var(--muted)'}}>Personalized recommendations will appear here.</div>}
        </div>
      </aside>
    </div>
  )
}

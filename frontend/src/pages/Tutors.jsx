import { useEffect, useState } from 'react'
import { api } from '../context/AuthContext.jsx'
import { Link } from 'react-router-dom'

export default function Tutors() {
  const [query, setQuery] = useState({ subject: '', city: '', maxRate: '' })
  const [tutors, setTutors] = useState([])
  const [loading, setLoading] = useState(false)

  const fetchTutors = async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams()
      if (query.subject) params.append('subject', query.subject)
      if (query.city) params.append('city', query.city)
      if (query.maxRate) params.append('maxRate', query.maxRate)
      const { data } = await api.get(`/api/tutors?${params.toString()}`)
      setTutors(data.content || data)
    } catch {
      setTutors([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchTutors() }, [])

  return (
    <div className="stack">
      <h1>Tutors</h1>

      {/* Filter Bar */}
      <div className="card">
        <div className="grid grid-3">
          <input className="input" placeholder="Subject (e.g., Math)" value={query.subject} onChange={e=>setQuery(q=>({...q, subject:e.target.value}))}/>
          <input className="input" placeholder="City (e.g., Dhaka)" value={query.city} onChange={e=>setQuery(q=>({...q, city:e.target.value}))}/>
          <input className="input" placeholder="Max rate (BDT/hr)" value={query.maxRate} onChange={e=>setQuery(q=>({...q, maxRate:e.target.value}))}/>
        </div>
        <div className="row" style={{marginTop:12}}>
          <button className="btn btn--primary" onClick={fetchTutors}>Search</button>
          <button className="btn btn--ghost" onClick={()=>{ setQuery({subject:'',city:'',maxRate:''}); fetchTutors() }}>Reset</button>
        </div>
      </div>

      {/* Results */}
      {loading ? <p style={{color:'var(--muted)'}}>Loading tutors…</p> : (
        <div className="grid grid-2">
          {tutors.map(t => (
            <Link to={`/tutors/${t.id}`} key={t.id} className="card tutor-card" style={{textDecoration:'none', color:'inherit'}}>
              <div className="tutor-head">
                <div>
                  <div className="tutor-name">{t.name || t.fullName}</div>
                  <div className="tutor-subjects">{(t.subjects || []).join(', ')}</div>
                </div>
                <div style={{textAlign:'right'}}>
                  <div className="pill">BDT <strong style={{marginLeft:6}}>{t.hourlyRate || '—'}</strong>/hr</div>
                  {t.rating != null && <div className="pill" style={{marginTop:6}}>★ {t.rating.toFixed ? t.rating.toFixed(1) : t.rating}</div>}
                </div>
              </div>
              <div style={{color:'var(--muted)'}}>{t.city || '—'}</div>
              <div className="row" style={{gap:8, marginTop:8}}>
                <span className="badge">Verified</span>
                <span className="badge">Fast reply</span>
              </div>
            </Link>
          ))}
          {tutors.length === 0 && <div className="card">No tutors found.</div>}
        </div>
      )}
    </div>
  )
}

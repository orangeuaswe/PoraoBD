import { useEffect, useRef, useState } from 'react'
import { api } from '../context/AuthContext.jsx'
import { useLocation } from 'react-router-dom'

export default function Chat() {
  const [chats, setChats] = useState([])
  const [activeId, setActiveId] = useState(null)
  const [messages, setMessages] = useState([])
  const [text, setText] = useState('')
  const bottomRef = useRef(null)
  const location = useLocation()

  // Load chats
  useEffect(() => {
    api.get('/api/chats').then(res => {
      setChats(res.data)
      if (res.data.length > 0) setActiveId(res.data[0].id)
    })
  }, [])

  // Auto open chat if redirected from TutorDetail
  useEffect(() => {
    const toUserId = location.state?.toUserId
    if (!toUserId) return
    api.post('/api/chats', { toUserId }).then(res => {
      const chat = res.data
      setChats(prev => prev.find(c => c.id === chat.id) ? prev : [chat, ...prev])
      setActiveId(chat.id)
    })
  }, [location.state])

  // Poll messages
  useEffect(() => {
    if (!activeId) return
    const load = () => api.get(`/api/chats/${activeId}/messages`).then(res => setMessages(res.data))
    load()
    const interval = setInterval(load, 2500)
    return () => clearInterval(interval)
  }, [activeId])

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const send = async () => {
    if (!text.trim()) return
    const tmp = { id: `tmp-${Date.now()}`, text, pending: true }
    setMessages(m => [...m, tmp])
    setText('')
    try { await api.post(`/api/chats/${activeId}/messages`, { text }) } catch {}
  }

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '1rem' }}>
      <aside>
        <h3>Chats</h3>
        <ul>
          {chats.map(c => (
            <li key={c.id}>
              <button onClick={() => setActiveId(c.id)}>
                {c.title || `Chat ${c.id}`}
              </button>
            </li>
          ))}
        </ul>
      </aside>
      <section style={{ border: '1px solid #ccc', padding: '1rem', height: '60vh', display: 'flex', flexDirection: 'column' }}>
        <div style={{ flex: 1, overflowY: 'auto' }}>
          {messages.map(m => (
            <div key={m.id} style={{ margin: '0.5rem 0' }}>
              {m.text}
            </div>
          ))}
          <div ref={bottomRef} />
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <input
            value={text}
            onChange={e => setText(e.target.value)}
            style={{ flex: 1 }}
          />
          <button onClick={send}>Send</button>
        </div>
      </section>
    </div>
  )
}

import { useState, useEffect } from 'react'
import { getTasks, getProgressSummary, getActiveSchedule, generateSchedule } from '../api'
import { Link } from 'react-router-dom'
import { format, parseISO } from 'date-fns'

export default function DashboardPage() {
  const [tasks, setTasks] = useState([])
  const [summary, setSummary] = useState(null)
  const [schedule, setSchedule] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        const [t, s] = await Promise.all([getTasks(), getProgressSummary()])
        setTasks(t.data)
        setSummary(s.data)
        try {
          const sc = await getActiveSchedule()
          setSchedule(sc.data)
        } catch { /* no schedule yet */ }
      } catch { /* ignore */ }
      setLoading(false)
    }
    load()
  }, [])

  const urgentTasks = tasks.filter(t => !t.completed).filter(t => {
    const d = Math.ceil((new Date(t.deadline) - new Date()) / 86400000)
    return d <= 3
  }).slice(0, 5)

  const todayStr = format(new Date(), 'yyyy-MM-dd')
  const todayItems = schedule
    ? schedule.items.filter(i => i.date === todayStr && i.item_type === 'study').slice(0, 4)
    : []

  if (loading) return <div className="loading"><div className="spinner" /></div>

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="page-subtitle">{format(new Date(), 'EEEE, MMMM d, yyyy')}</p>
        </div>
      </div>

      {/* Stats row */}
      {summary && (
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-value">{summary.total_tasks}</div>
            <div className="stat-label">Total Tasks</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{summary.remaining_tasks}</div>
            <div className="stat-label">Remaining</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{summary.completion_rate}%</div>
            <div className="stat-label">Completion</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">🔥{summary.current_streak}</div>
            <div className="stat-label">Day Streak</div>
          </div>
        </div>
      )}

      <div style={{display:'grid',gridTemplateColumns:'1fr 1fr',gap:'1.25rem'}}>
        {/* Urgent tasks */}
        <div className="card">
          <div className="card-title" style={{display:'flex',justifyContent:'space-between'}}>
            🚨 Urgent Tasks
            <Link to="/tasks" style={{fontSize:'.8rem',color:'var(--primary)'}}>View all →</Link>
          </div>
          {urgentTasks.length === 0 ? (
            <p style={{color:'var(--text-muted)',fontSize:'.875rem'}}>No urgent tasks. Great job staying on top of things!</p>
          ) : urgentTasks.map(t => {
            const d = Math.ceil((new Date(t.deadline) - new Date()) / 86400000)
            return (
              <div key={t.id} style={{display:'flex',justifyContent:'space-between',padding:'.6rem 0',borderBottom:'1px solid var(--border)'}}>
                <div>
                  <div style={{fontWeight:500}}>{t.title}</div>
                  <div style={{fontSize:'.78rem',color:'var(--text-muted)'}}>{t.course} · {t.estimated_hours}h</div>
                </div>
                <span style={{color: d < 0 ? '#ef4444' : d === 0 ? '#f59e0b' : '#f59e0b',fontWeight:600,fontSize:'.85rem'}}>
                  {d < 0 ? `${Math.abs(d)}d overdue` : d === 0 ? 'Due today' : `${d}d left`}
                </span>
              </div>
            )
          })}
        </div>

        {/* Today's schedule */}
        <div className="card">
          <div className="card-title" style={{display:'flex',justifyContent:'space-between'}}>
            📅 Today's Schedule
            <Link to="/schedule" style={{fontSize:'.8rem',color:'var(--primary)'}}>Full schedule →</Link>
          </div>
          {todayItems.length === 0 ? (
            <div>
              <p style={{color:'var(--text-muted)',fontSize:'.875rem',marginBottom:'1rem'}}>No study sessions scheduled for today.</p>
              <Link to="/schedule" className="btn btn-primary btn-sm">⚡ Generate Schedule</Link>
            </div>
          ) : todayItems.map(item => (
            <div key={item.id} style={{display:'flex',gap:'.75rem',padding:'.5rem 0',borderBottom:'1px solid var(--border)',alignItems:'center'}}>
              <span style={{fontSize:'.8rem',color:'var(--text-muted)',minWidth:'90px'}}>{item.start_time}–{item.end_time}</span>
              <span style={{fontSize:'.875rem',opacity: item.completed ? .5 : 1, textDecoration: item.completed ? 'line-through' : 'none'}}>
                📚 {item.title}
              </span>
              {item.completed && <span style={{marginLeft:'auto',fontSize:'.75rem',color:'var(--success)'}}>✅</span>}
            </div>
          ))}
        </div>
      </div>

      {/* Quick actions */}
      <div className="card">
        <div className="card-title">⚡ Quick Actions</div>
        <div style={{display:'flex',gap:'1rem',flexWrap:'wrap'}}>
          <Link to="/tasks" className="btn btn-primary">+ Add Task</Link>
          <Link to="/schedule" className="btn btn-secondary">⚡ Generate Schedule</Link>
          <Link to="/study" className="btn btn-success">🎯 Start Study Session</Link>
          <Link to="/preferences" className="btn btn-secondary">⚙️ Preferences</Link>
        </div>
      </div>
    </div>
  )
}

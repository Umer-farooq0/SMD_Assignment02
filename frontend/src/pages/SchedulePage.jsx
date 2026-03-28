import { useState, useEffect, useCallback } from 'react'
import { getActiveSchedule, generateSchedule, markItemComplete } from '../api'
import { format, parseISO } from 'date-fns'

const TYPE_ICON = {
  study: '📚',
  short_break: '☕',
  long_break: '🛋️',
  namaz: '🕌',
}

export default function SchedulePage() {
  const [schedule, setSchedule] = useState(null)
  const [loading, setLoading] = useState(true)
  const [generating, setGenerating] = useState(false)
  const [daysAhead, setDaysAhead] = useState(7)
  const [error, setError] = useState('')

  const load = useCallback(async () => {
    setLoading(true); setError('')
    try {
      const { data } = await getActiveSchedule()
      setSchedule(data)
    } catch (err) {
      if (err.response?.status === 404) setSchedule(null)
      else setError('Failed to load schedule')
    }
    setLoading(false)
  }, [])

  useEffect(() => { load() }, [load])

  const handleGenerate = async () => {
    setGenerating(true); setError('')
    try {
      const { data } = await generateSchedule(daysAhead)
      setSchedule(data)
    } catch { setError('Failed to generate schedule') }
    setGenerating(false)
  }

  const handleToggle = async (item) => {
    try {
      const { data: updated } = await markItemComplete(item.id)
      setSchedule(s => ({
        ...s,
        items: s.items.map(i => i.id === updated.id ? updated : i),
      }))
    } catch { setError('Failed to update item') }
  }

  // Group items by date
  const byDate = schedule ? schedule.items.reduce((acc, item) => {
    const key = item.date
    if (!acc[key]) acc[key] = []
    acc[key].push(item)
    return acc
  }, {}) : {}

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Schedule</h1>
          <p className="page-subtitle">Your AI-generated study plan</p>
        </div>
        <div style={{display:'flex',gap:'.75rem',alignItems:'center'}}>
          <select className="form-control" style={{width:'auto'}} value={daysAhead} onChange={e => setDaysAhead(+e.target.value)}>
            {[3,5,7,10,14].map(d => <option key={d} value={d}>{d} days</option>)}
          </select>
          <button className="btn btn-primary" onClick={handleGenerate} disabled={generating}>
            {generating ? 'Generating…' : '⚡ Generate'}
          </button>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="loading"><div className="spinner" /></div>
      ) : !schedule || schedule.items.length === 0 ? (
        <div className="card" style={{textAlign:'center',padding:'3rem'}}>
          <p style={{color:'var(--text-muted)'}}>No schedule yet. Add some tasks and click <strong>⚡ Generate</strong>.</p>
        </div>
      ) : (
        <>
          <div className="alert alert-info">
            Schedule generated {format(parseISO(schedule.generated_at), 'PPp')} — {schedule.items.length} blocks
          </div>
          {Object.keys(byDate).sort().map(dateKey => (
            <div key={dateKey} className="schedule-day card">
              <div className="schedule-day-title">
                📅 {format(parseISO(dateKey), 'EEEE, MMMM d')}
              </div>
              <div className="schedule-items">
                {byDate[dateKey].sort((a,b) => a.start_time.localeCompare(b.start_time)).map(item => (
                  <div
                    key={item.id}
                    className={`schedule-item ${item.item_type} ${item.completed ? 'completed' : ''}`}
                    onClick={() => item.item_type === 'study' && handleToggle(item)}
                    style={item.item_type === 'study' ? {cursor:'pointer'} : {}}
                    title={item.item_type === 'study' ? 'Click to mark complete' : ''}
                  >
                    <span className="schedule-time">{item.start_time}–{item.end_time}</span>
                    <span>{TYPE_ICON[item.item_type]} {item.title}</span>
                    {item.item_type === 'study' && (
                      <span style={{marginLeft:'auto',fontSize:'.8rem',color:'var(--text-muted)'}}>
                        {item.completed ? '✅ Done' : '○ Click to complete'}
                      </span>
                    )}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </>
      )}
    </div>
  )
}

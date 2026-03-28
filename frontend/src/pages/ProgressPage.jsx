import { useState, useEffect } from 'react'
import { getProgressSummary, getProgress } from '../api'
import { format, parseISO } from 'date-fns'

export default function ProgressPage() {
  const [summary, setSummary] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const [s, h] = await Promise.all([getProgressSummary(), getProgress()])
        setSummary(s.data)
        setHistory(h.data)
      } catch { setError('Failed to load progress') }
      setLoading(false)
    }
    load()
  }, [])

  if (loading) return <div className="loading"><div className="spinner" /></div>
  if (error) return <div className="alert alert-error">{error}</div>

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Progress</h1>
          <p className="page-subtitle">Track your study habits and task completion</p>
        </div>
      </div>

      {summary && (
        <>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-value">{summary.completed_tasks}</div>
              <div className="stat-label">Tasks Completed</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{summary.remaining_tasks}</div>
              <div className="stat-label">Tasks Remaining</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{summary.completion_rate}%</div>
              <div className="stat-label">Completion Rate</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">🔥 {summary.current_streak}</div>
              <div className="stat-label">Day Streak</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{summary.total_hours_studied}h</div>
              <div className="stat-label">Total Hours Studied</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{summary.today_sessions}</div>
              <div className="stat-label">Sessions Today</div>
            </div>
          </div>

          <div className="card">
            <div className="card-title">Overall Progress</div>
            <div style={{marginBottom:'.5rem',fontSize:'.875rem',color:'var(--text-muted)'}}>
              {summary.completed_tasks} of {summary.total_tasks} tasks completed
            </div>
            <div className="progress-bar-wrap">
              <div className="progress-bar-fill" style={{width:`${summary.completion_rate}%`}} />
            </div>
          </div>
        </>
      )}

      {history.length > 0 && (
        <div className="card">
          <div className="card-title">Recent Study History</div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Sessions</th>
                  <th>Hours</th>
                  <th>Tasks Done</th>
                  <th>Streak</th>
                </tr>
              </thead>
              <tbody>
                {history.map(p => (
                  <tr key={p.id}>
                    <td>{format(parseISO(p.date), 'MMM d, yyyy')}</td>
                    <td>{p.sessions_completed}</td>
                    <td>{p.hours_studied}h</td>
                    <td>{p.tasks_completed}</td>
                    <td>🔥 {p.streak_days}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {history.length === 0 && summary?.total_tasks === 0 && (
        <div className="card" style={{textAlign:'center',padding:'3rem'}}>
          <p style={{color:'var(--text-muted)'}}>No study sessions recorded yet. Start by generating a schedule and completing sessions!</p>
        </div>
      )}
    </div>
  )
}

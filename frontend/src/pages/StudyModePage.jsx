import { useState, useEffect } from 'react'

export default function StudyModePage() {
  const [active, setActive] = useState(false)
  const [timeLeft, setTimeLeft] = useState(50 * 60) // 50 min default
  const [sessionMin, setSessionMin] = useState(50)
  const [running, setRunning] = useState(false)

  useEffect(() => {
    if (!running) return
    const id = setInterval(() => {
      setTimeLeft(t => {
        if (t <= 1) { setRunning(false); setActive(false); return 0 }
        return t - 1
      })
    }, 1000)
    return () => clearInterval(id)
  }, [running])

  const startSession = () => {
    setTimeLeft(sessionMin * 60)
    setActive(true)
    setRunning(true)
  }

  const pauseResume = () => setRunning(r => !r)

  const stop = () => {
    setRunning(false)
    setActive(false)
    setTimeLeft(sessionMin * 60)
  }

  const mm = String(Math.floor(timeLeft / 60)).padStart(2, '0')
  const ss = String(timeLeft % 60).padStart(2, '0')
  const pct = active ? ((sessionMin * 60 - timeLeft) / (sessionMin * 60)) * 100 : 0

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Study Mode</h1>
          <p className="page-subtitle">Focus timer and distraction blocker</p>
        </div>
      </div>

      {active ? (
        <div className="study-mode-banner">
          <h2>🎯 Focus Time</h2>
          <p>Stay focused. You've got this! Distractions are blocked.</p>
          <div style={{fontSize:'4rem',fontWeight:700,letterSpacing:'0.05em',margin:'1.5rem 0'}}>
            {mm}:{ss}
          </div>
          <div style={{background:'rgba(255,255,255,.2)',borderRadius:'999px',height:'8px',maxWidth:'400px',margin:'0 auto 1.5rem'}}>
            <div style={{width:`${pct}%`,background:'#fff',height:'100%',borderRadius:'999px',transition:'width 1s linear'}} />
          </div>
          <div style={{display:'flex',gap:'1rem',justifyContent:'center'}}>
            <button className="btn btn-secondary" onClick={pauseResume}>{running ? '⏸ Pause' : '▶ Resume'}</button>
            <button className="btn btn-danger" onClick={stop}>⏹ End Session</button>
          </div>
        </div>
      ) : (
        <div className="card" style={{textAlign:'center',padding:'3rem'}}>
          <div style={{fontSize:'3rem',marginBottom:'1rem'}}>🎯</div>
          <h2 style={{marginBottom:'.5rem'}}>Ready to Focus?</h2>
          <p style={{color:'var(--text-muted)',marginBottom:'2rem'}}>
            Start a focus session. Social media and distractions will be blocked.
          </p>

          <div style={{maxWidth:'280px',margin:'0 auto 1.5rem'}}>
            <label style={{display:'block',marginBottom:'.5rem',fontSize:'.9rem',color:'var(--text-muted)'}}>
              Session length: <strong>{sessionMin} minutes</strong>
            </label>
            <input type="range" className="form-control" value={sessionMin} onChange={e => { setSessionMin(+e.target.value); setTimeLeft(+e.target.value*60) }}
              min={15} max={120} step={5} style={{padding:'8px 0'}} />
            <div style={{display:'flex',justifyContent:'space-between',fontSize:'.75rem',color:'var(--text-muted)'}}>
              <span>15 min</span><span>120 min</span>
            </div>
          </div>

          <button className="btn btn-primary" style={{fontSize:'1rem',padding:'.75rem 2rem'}} onClick={startSession}>
            🚀 Start Focus Session
          </button>
        </div>
      )}

      <div className="card">
        <div className="card-title">📋 Study Mode Features</div>
        <ul style={{listStyle:'none',display:'flex',flexDirection:'column',gap:'.75rem'}}>
          <li style={{display:'flex',gap:'.75rem',alignItems:'flex-start'}}>
            <span style={{fontSize:'1.25rem'}}>⏱️</span>
            <div>
              <strong>Pomodoro Timer</strong>
              <div style={{fontSize:'.85rem',color:'var(--text-muted)'}}>Customizable focus sessions from 15 to 120 minutes</div>
            </div>
          </li>
          <li style={{display:'flex',gap:'.75rem',alignItems:'flex-start'}}>
            <span style={{fontSize:'1.25rem'}}>🚫</span>
            <div>
              <strong>Distraction Blocker (Placeholder)</strong>
              <div style={{fontSize:'.85rem',color:'var(--text-muted)'}}>In a full deployment, social media & notifications would be blocked during sessions</div>
            </div>
          </li>
          <li style={{display:'flex',gap:'.75rem',alignItems:'flex-start'}}>
            <span style={{fontSize:'1.25rem'}}>📞</span>
            <div>
              <strong>Emergency Calls Allowed</strong>
              <div style={{fontSize:'.85rem',color:'var(--text-muted)'}}>Phone calls remain enabled for emergencies</div>
            </div>
          </li>
          <li style={{display:'flex',gap:'.75rem',alignItems:'flex-start'}}>
            <span style={{fontSize:'1.25rem'}}>📊</span>
            <div>
              <strong>Session Tracking</strong>
              <div style={{fontSize:'.85rem',color:'var(--text-muted)'}}>Completed sessions are tracked in your progress dashboard</div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  )
}

import { useState, useEffect } from 'react'
import { getPreferences, updatePreferences } from '../api'

export default function PreferencesPage() {
  const [form, setForm] = useState(null)
  const [saving, setSaving] = useState(false)
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    getPreferences().then(r => setForm(r.data)).catch(() => setError('Failed to load preferences'))
  }, [])

  const set = (field) => (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked :
      e.target.type === 'range' || e.target.type === 'number' ? +e.target.value : e.target.value
    setForm(f => ({ ...f, [field]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault(); setSaving(true); setSuccess(false); setError('')
    try {
      await updatePreferences(form)
      setSuccess(true)
      setTimeout(() => setSuccess(false), 3000)
    } catch { setError('Failed to save preferences') }
    setSaving(false)
  }

  if (!form) return <div className="loading"><div className="spinner" /></div>

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Preferences</h1>
          <p className="page-subtitle">Configure your study schedule settings</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">✅ Preferences saved!</div>}

      <form onSubmit={handleSubmit}>
        {/* Study Hours */}
        <div className="card">
          <div className="card-title">⏰ Study Hours</div>
          <div className="form-grid">
            <div className="form-group">
              <label>Study Start Time</label>
              <input type="time" className="form-control" value={form.study_start_time} onChange={set('study_start_time')} />
            </div>
            <div className="form-group">
              <label>Study End Time</label>
              <input type="time" className="form-control" value={form.study_end_time} onChange={set('study_end_time')} />
            </div>
            <div className="form-group">
              <label>Max Hours / Day: <strong>{form.study_hours_per_day}h</strong></label>
              <input type="range" className="form-control" value={form.study_hours_per_day} onChange={set('study_hours_per_day')} min={1} max={16} step={0.5} style={{padding:'8px 0'}} />
            </div>
            <div className="form-group">
              <label>Session Length (min): <strong>{form.session_length_minutes}</strong></label>
              <input type="range" className="form-control" value={form.session_length_minutes} onChange={set('session_length_minutes')} min={25} max={120} step={5} style={{padding:'8px 0'}} />
            </div>
          </div>
        </div>

        {/* Break Settings */}
        <div className="card">
          <div className="card-title">☕ Break Settings</div>
          <div className="form-grid">
            <div className="form-group">
              <label>Short Break (min): <strong>{form.short_break_minutes}</strong></label>
              <input type="range" className="form-control" value={form.short_break_minutes} onChange={set('short_break_minutes')} min={5} max={30} step={5} style={{padding:'8px 0'}} />
            </div>
            <div className="form-group">
              <label>Long Break (min): <strong>{form.long_break_minutes}</strong></label>
              <input type="range" className="form-control" value={form.long_break_minutes} onChange={set('long_break_minutes')} min={15} max={60} step={5} style={{padding:'8px 0'}} />
            </div>
            <div className="form-group">
              <label>Sessions before long break: <strong>{form.sessions_before_long_break}</strong></label>
              <input type="range" className="form-control" value={form.sessions_before_long_break} onChange={set('sessions_before_long_break')} min={2} max={8} step={1} style={{padding:'8px 0'}} />
            </div>
          </div>
        </div>

        {/* Namaz Breaks */}
        <div className="card">
          <div className="card-title">🕌 Namaz Breaks</div>
          <div className="form-group">
            <div className="toggle-wrap">
              <div className={`toggle ${form.namaz_enabled ? 'on' : ''}`} onClick={() => setForm(f => ({...f, namaz_enabled: !f.namaz_enabled}))} />
              <label style={{marginBottom:0,cursor:'pointer'}} onClick={() => setForm(f => ({...f, namaz_enabled: !f.namaz_enabled}))}>
                {form.namaz_enabled ? 'Namaz breaks enabled' : 'Enable Namaz breaks'}
              </label>
            </div>
          </div>

          {form.namaz_enabled && (
            <>
              <div className="form-grid">
                {['fajr_time','dhuhr_time','asr_time','maghrib_time','isha_time'].map(key => (
                  <div className="form-group" key={key}>
                    <label>{key.replace('_time','').charAt(0).toUpperCase()+key.replace('_time','').slice(1)} Time</label>
                    <input type="time" className="form-control" value={form[key]} onChange={set(key)} />
                  </div>
                ))}
                <div className="form-group">
                  <label>Duration (min): <strong>{form.namaz_duration_minutes}</strong></label>
                  <input type="range" className="form-control" value={form.namaz_duration_minutes} onChange={set('namaz_duration_minutes')} min={5} max={60} step={5} style={{padding:'8px 0'}} />
                </div>
              </div>
            </>
          )}
        </div>

        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? 'Saving…' : '💾 Save Preferences'}
        </button>
      </form>
    </div>
  )
}

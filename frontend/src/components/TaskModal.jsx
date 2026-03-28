import { useState, useEffect } from 'react'
import { createTask, updateTask } from '../api'

const EMPTY = {
  title: '', description: '', task_type: 'assignment',
  deadline: '', estimated_hours: 1, difficulty: 3, importance: 3, course: '',
}

export default function TaskModal({ task, onClose, onSaved }) {
  const [form, setForm] = useState(task ? { ...task, deadline: task.deadline } : EMPTY)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const set = (field) => (e) => setForm(f => ({ ...f, [field]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true); setError('')
    try {
      const payload = {
        ...form,
        estimated_hours: parseFloat(form.estimated_hours),
        difficulty: parseInt(form.difficulty),
        importance: parseInt(form.importance),
      }
      if (task) {
        await updateTask(task.id, payload)
      } else {
        await createTask(payload)
      }
      onSaved()
    } catch (err) {
      setError(err.response?.data?.detail || 'Failed to save task')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">{task ? 'Edit Task' : 'Add Task'}</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Title *</label>
            <input className="form-control" value={form.title} onChange={set('title')} required />
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea className="form-control" value={form.description || ''} onChange={set('description')} rows={2} />
          </div>
          <div className="form-grid-2">
            <div className="form-group">
              <label>Type</label>
              <select className="form-control" value={form.task_type} onChange={set('task_type')}>
                {['assignment','quiz','midterm','final','other'].map(t =>
                  <option key={t} value={t}>{t.charAt(0).toUpperCase()+t.slice(1)}</option>
                )}
              </select>
            </div>
            <div className="form-group">
              <label>Course</label>
              <input className="form-control" value={form.course || ''} onChange={set('course')} placeholder="e.g. CS101" />
            </div>
          </div>
          <div className="form-grid-2">
            <div className="form-group">
              <label>Deadline *</label>
              <input type="date" className="form-control" value={form.deadline} onChange={set('deadline')} required />
            </div>
            <div className="form-group">
              <label>Est. Hours *</label>
              <input type="number" className="form-control" value={form.estimated_hours} onChange={set('estimated_hours')} min="0.5" max="100" step="0.5" required />
            </div>
          </div>
          <div className="form-grid-2">
            <div className="form-group">
              <label>Difficulty (1–5)</label>
              <input type="range" className="form-control" value={form.difficulty} onChange={set('difficulty')} min={1} max={5} style={{padding:'8px 0'}} />
              <span style={{fontSize:'.8rem',color:'#6b7280'}}>Selected: {form.difficulty}</span>
            </div>
            <div className="form-group">
              <label>Importance (1–5)</label>
              <input type="range" className="form-control" value={form.importance} onChange={set('importance')} min={1} max={5} style={{padding:'8px 0'}} />
              <span style={{fontSize:'.8rem',color:'#6b7280'}}>Selected: {form.importance}</span>
            </div>
          </div>
          <div style={{display:'flex',gap:'.75rem',justifyContent:'flex-end',marginTop:'1.25rem'}}>
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : task ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

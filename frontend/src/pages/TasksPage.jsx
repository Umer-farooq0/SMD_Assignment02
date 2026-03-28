import { useState, useEffect, useCallback } from 'react'
import { getTasks, deleteTask, updateTask } from '../api'
import TaskModal from '../components/TaskModal'

const BADGE = (type) => <span className={`badge badge-${type}`}>{type}</span>

export default function TasksPage() {
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modal, setModal] = useState(null) // null | 'create' | task-object

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const { data } = await getTasks()
      setTasks(data)
    } catch { setError('Failed to load tasks') }
    setLoading(false)
  }, [])

  useEffect(() => { load() }, [load])

  const handleDelete = async (id) => {
    if (!confirm('Delete this task?')) return
    try { await deleteTask(id); load() }
    catch { setError('Failed to delete task') }
  }

  const handleToggleDone = async (task) => {
    try { await updateTask(task.id, { completed: !task.completed }); load() }
    catch { setError('Failed to update task') }
  }

  const daysUntil = (d) => {
    const diff = Math.ceil((new Date(d) - new Date()) / 86400000)
    if (diff < 0) return <span style={{color:'#ef4444',fontWeight:600}}>Overdue {Math.abs(diff)}d</span>
    if (diff === 0) return <span style={{color:'#f59e0b',fontWeight:600}}>Today</span>
    return <span style={{color: diff <= 3 ? '#f59e0b' : '#374151'}}>{diff}d left</span>
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Tasks</h1>
          <p className="page-subtitle">Manage your academic tasks</p>
        </div>
        <button className="btn btn-primary" onClick={() => setModal('create')}>+ Add Task</button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="loading"><div className="spinner" /></div>
      ) : tasks.length === 0 ? (
        <div className="card" style={{textAlign:'center',padding:'3rem'}}>
          <p style={{color:'var(--text-muted)',fontSize:'1rem'}}>No tasks yet. Click <strong>+ Add Task</strong> to get started.</p>
        </div>
      ) : (
        <div className="card">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Done</th>
                  <th>Title</th>
                  <th>Type</th>
                  <th>Course</th>
                  <th>Deadline</th>
                  <th>Hours</th>
                  <th>Diff</th>
                  <th>Imp</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {tasks.map(t => (
                  <tr key={t.id} style={{opacity: t.completed ? .5 : 1}}>
                    <td>
                      <input type="checkbox" checked={t.completed}
                        onChange={() => handleToggleDone(t)} />
                    </td>
                    <td style={{fontWeight:500,textDecoration: t.completed ? 'line-through' : 'none'}}>
                      {t.title}
                      {t.description && <div style={{fontSize:'.78rem',color:'var(--text-muted)',marginTop:'.1rem'}}>{t.description.slice(0,60)}{t.description.length>60?'…':''}</div>}
                    </td>
                    <td>{BADGE(t.task_type)}</td>
                    <td style={{color:'var(--text-muted)'}}>{t.course || '–'}</td>
                    <td>{daysUntil(t.deadline)}<div style={{fontSize:'.75rem',color:'var(--text-muted)'}}>{t.deadline}</div></td>
                    <td>{t.estimated_hours}h</td>
                    <td>{'★'.repeat(t.difficulty)}</td>
                    <td>{'★'.repeat(t.importance)}</td>
                    <td>
                      <div style={{display:'flex',gap:'.4rem'}}>
                        <button className="btn btn-secondary btn-sm" onClick={() => setModal(t)}>Edit</button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(t.id)}>Del</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {modal && (
        <TaskModal
          task={modal === 'create' ? null : modal}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); load() }}
        />
      )}
    </div>
  )
}

import { NavLink } from 'react-router-dom'

const NAV_ITEMS = [
  { path: '/', label: '📊 Dashboard', end: true },
  { path: '/tasks', label: '📝 Tasks' },
  { path: '/schedule', label: '📅 Schedule' },
  { path: '/progress', label: '📈 Progress' },
  { path: '/preferences', label: '⚙️ Preferences' },
  { path: '/study', label: '🎯 Study Mode' },
]

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        AI Productivity
        <span>Maximizer for Students</span>
      </div>
      <ul className="sidebar-nav">
        {NAV_ITEMS.map(({ path, label, end }) => (
          <li key={path}>
            <NavLink to={path} end={end}
              className={({ isActive }) => isActive ? 'active' : ''}
            >
              {label}
            </NavLink>
          </li>
        ))}
      </ul>
    </aside>
  )
}

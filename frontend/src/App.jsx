import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Sidebar from './components/Sidebar'
import DashboardPage from './pages/DashboardPage'
import TasksPage from './pages/TasksPage'
import SchedulePage from './pages/SchedulePage'
import ProgressPage from './pages/ProgressPage'
import PreferencesPage from './pages/PreferencesPage'
import StudyModePage from './pages/StudyModePage'

export default function App() {
  return (
    <BrowserRouter>
      <div className="app-layout">
        <Sidebar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/tasks" element={<TasksPage />} />
            <Route path="/schedule" element={<SchedulePage />} />
            <Route path="/progress" element={<ProgressPage />} />
            <Route path="/preferences" element={<PreferencesPage />} />
            <Route path="/study" element={<StudyModePage />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  )
}

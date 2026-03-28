import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL || '/api'

const api = axios.create({ baseURL: BASE_URL })

// ── Tasks ─────────────────────────────────────────────────────────────────────
export const getTasks = () => api.get('/tasks/')
export const createTask = (data) => api.post('/tasks/', data)
export const updateTask = (id, data) => api.put(`/tasks/${id}`, data)
export const deleteTask = (id) => api.delete(`/tasks/${id}`)

// ── Preferences ───────────────────────────────────────────────────────────────
export const getPreferences = () => api.get('/preferences/')
export const updatePreferences = (data) => api.put('/preferences/', data)

// ── Schedule ──────────────────────────────────────────────────────────────────
export const generateSchedule = (daysAhead = 7) =>
  api.post('/schedule/generate', { days_ahead: daysAhead })
export const getActiveSchedule = () => api.get('/schedule/active')
export const markItemComplete = (id) => api.patch(`/schedule/items/${id}/complete`)

// ── Progress ──────────────────────────────────────────────────────────────────
export const getProgressSummary = () => api.get('/progress/summary')
export const getProgress = () => api.get('/progress/')

# AI Productivity Maximizer for Students

An end-to-end AI-Based Productivity Maximizer that helps students manage academic tasks, generates optimized study schedules, tracks progress, and includes a study focus timer.

## Project Structure

```
├── backend/          # Python FastAPI backend + AI scheduler
│   ├── app/
│   │   ├── main.py        # FastAPI application entry point
│   │   ├── models.py      # SQLAlchemy database models
│   │   ├── schemas.py     # Pydantic request/response schemas
│   │   ├── scheduler.py   # AI scheduling agent
│   │   ├── database.py    # Database configuration
│   │   └── routers/       # API route handlers
│   ├── tests/             # pytest test suite
│   ├── seed.py            # Demo data seeder
│   └── requirements.txt
├── frontend/         # React + Vite frontend
│   ├── src/
│   │   ├── pages/         # Page components
│   │   ├── components/    # Reusable components
│   │   └── api/           # API client
│   └── package.json
└── README.md
```

## Features

1. **Task Management** – CRUD tasks with type, deadline, estimated hours, difficulty (1–5), importance (1–5), and course.
2. **AI Scheduler** – Priority scoring combining urgency (days-to-deadline), difficulty, importance, and task type. Auto-generates daily/weekly study blocks.
3. **Smart Breaks** – Short breaks (configurable) between sessions; long breaks after N sessions.
4. **Namaz Breaks** – Optional prayer-time blocks (Fajr, Dhuhr, Asr, Maghrib, Isha) that the scheduler respects.
5. **Schedule View** – Day-by-day study plan with colour-coded blocks; click to mark sessions complete.
6. **Progress Tracking** – Completion stats, study streaks, and history.
7. **Study Mode** – Pomodoro-style focus timer with distraction-blocking placeholder.

---

## Quick Start

### Prerequisites

- Python 3.10+
- Node.js 18+

---

### Backend

```bash
cd backend
python -m venv .venv
source .venv/bin/activate        # Windows: .venv\Scripts\activate
pip install -r requirements.txt

# (Optional) Seed demo data
python seed.py

# Start the API server
uvicorn app.main:app --reload
```

The API will be available at **http://localhost:8000**  
Interactive API docs (Swagger UI): **http://localhost:8000/docs**

---

### Frontend

```bash
cd frontend
cp .env.example .env              # adjust VITE_API_URL if needed
npm install
npm run dev
```

The app will open at **http://localhost:5173**

---

### Run Tests

```bash
cd backend
python -m pytest tests/ -v
```

---

## API Reference (summary)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/tasks/` | List all tasks |
| POST | `/tasks/` | Create a task |
| PUT | `/tasks/{id}` | Update a task |
| DELETE | `/tasks/{id}` | Delete a task |
| GET | `/preferences/` | Get preferences |
| PUT | `/preferences/` | Update preferences |
| POST | `/schedule/generate` | Generate a new schedule |
| GET | `/schedule/active` | Get active schedule |
| PATCH | `/schedule/items/{id}/complete` | Toggle schedule item complete |
| GET | `/progress/summary` | Get progress summary |
| GET | `/progress/` | Get progress history |

Full interactive docs: http://localhost:8000/docs

---

## AI Scheduler Details

### Priority Score Formula

```
urgency        = max(0, 10 - days_to_deadline)  [overdue → 15]
type_bonus     = 3 (final) | 2 (midterm) | 1 (quiz) | 0 (assignment/other)
priority_score = urgency × 0.5 + difficulty × 0.25 + importance × 0.25 + type_bonus
```

Higher score → scheduled earlier in the day and week.

### Schedule Generation

1. Tasks sorted by `priority_score` descending.
2. Each day is divided into slots using `study_start_time` → `study_end_time`.
3. Namaz blocks are reserved first (if enabled).
4. Tasks are split into `session_length_minutes` chunks.
5. Short break inserted after each session; long break after every N sessions.
6. Overdue tasks get the maximum urgency score.

---

## Docker (optional)

```bash
# Start both backend and frontend
docker-compose up --build
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Python 3.12, FastAPI, SQLAlchemy, SQLite |
| AI Agent | Pure Python heuristic scheduler |
| Frontend | React 18, Vite, React Router v6, Axios, date-fns |
| Testing | pytest, FastAPI TestClient |

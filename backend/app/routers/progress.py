from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from datetime import date, timedelta
from typing import List
from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/progress", tags=["progress"])


@router.get("/summary", response_model=schemas.ProgressSummary)
def get_summary(db: Session = Depends(get_db)):
    total_tasks = db.query(models.Task).count()
    completed_tasks = db.query(models.Task).filter(models.Task.completed == True).count()
    remaining_tasks = total_tasks - completed_tasks
    completion_rate = round((completed_tasks / total_tasks * 100), 1) if total_tasks > 0 else 0.0

    # Streak: count consecutive days where progress was recorded
    today = date.today()
    streak = 0
    check_date = today
    while True:
        entry = db.query(models.Progress).filter(models.Progress.date == check_date).first()
        if entry and (entry.sessions_completed > 0 or entry.tasks_completed > 0):
            streak += 1
            check_date -= timedelta(days=1)
        else:
            break

    all_progress = db.query(models.Progress).all()
    total_hours = sum(p.hours_studied for p in all_progress)

    today_entry = db.query(models.Progress).filter(models.Progress.date == today).first()
    today_sessions = today_entry.sessions_completed if today_entry else 0

    return schemas.ProgressSummary(
        total_tasks=total_tasks,
        completed_tasks=completed_tasks,
        remaining_tasks=remaining_tasks,
        completion_rate=completion_rate,
        current_streak=streak,
        total_hours_studied=round(total_hours, 2),
        today_sessions=today_sessions,
    )


@router.get("/", response_model=List[schemas.ProgressResponse])
def list_progress(db: Session = Depends(get_db)):
    return db.query(models.Progress).order_by(models.Progress.date.desc()).limit(30).all()


@router.post("/log-session", response_model=schemas.ProgressResponse)
def log_session(
    session_minutes: float,
    db: Session = Depends(get_db),
):
    today = date.today()
    entry = db.query(models.Progress).filter(models.Progress.date == today).first()
    if not entry:
        entry = models.Progress(date=today)
        db.add(entry)

    entry.sessions_completed += 1
    entry.hours_studied += round(session_minutes / 60, 2)

    # Update streak
    yesterday = today - timedelta(days=1)
    yesterday_entry = db.query(models.Progress).filter(models.Progress.date == yesterday).first()
    if yesterday_entry and yesterday_entry.sessions_completed > 0:
        entry.streak_days = yesterday_entry.streak_days + 1
    else:
        entry.streak_days = 1

    db.commit()
    db.refresh(entry)
    return entry

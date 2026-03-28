"""Seed script – populate the database with demo tasks and preferences."""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from datetime import date, timedelta
from app.database import SessionLocal, engine
from app import models

models.Base.metadata.create_all(bind=engine)

db = SessionLocal()

# Clear existing data
db.query(models.ScheduleItem).delete()
db.query(models.Schedule).delete()
db.query(models.Task).delete()
db.query(models.Preference).delete()
db.query(models.Progress).delete()
db.commit()

today = date.today()

# Preferences
prefs = models.Preference(
    study_hours_per_day=6.0,
    study_start_time="09:00",
    study_end_time="21:00",
    short_break_minutes=10,
    long_break_minutes=30,
    sessions_before_long_break=4,
    session_length_minutes=50,
    namaz_enabled=True,
    fajr_time="05:00",
    dhuhr_time="13:00",
    asr_time="16:30",
    maghrib_time="18:45",
    isha_time="20:30",
    namaz_duration_minutes=20,
)
db.add(prefs)

# Tasks
tasks_data = [
    {
        "title": "Data Structures Assignment",
        "description": "Implement a binary search tree with insert, delete, and search.",
        "task_type": "assignment",
        "deadline": today + timedelta(days=3),
        "estimated_hours": 4.0,
        "difficulty": 4,
        "importance": 4,
        "course": "CS201",
    },
    {
        "title": "Calculus Midterm Prep",
        "description": "Review chapters 1-5: limits, derivatives, integrals.",
        "task_type": "midterm",
        "deadline": today + timedelta(days=5),
        "estimated_hours": 8.0,
        "difficulty": 5,
        "importance": 5,
        "course": "MATH101",
    },
    {
        "title": "English Essay",
        "description": "Write a 1500-word persuasive essay on climate change.",
        "task_type": "assignment",
        "deadline": today + timedelta(days=7),
        "estimated_hours": 3.0,
        "difficulty": 2,
        "importance": 3,
        "course": "ENG102",
    },
    {
        "title": "Operating Systems Quiz",
        "description": "Short quiz on process scheduling algorithms.",
        "task_type": "quiz",
        "deadline": today + timedelta(days=2),
        "estimated_hours": 2.0,
        "difficulty": 3,
        "importance": 4,
        "course": "CS303",
    },
    {
        "title": "Database Final Exam Prep",
        "description": "Comprehensive review: ER diagrams, SQL, normalization, transactions.",
        "task_type": "final",
        "deadline": today + timedelta(days=14),
        "estimated_hours": 12.0,
        "difficulty": 5,
        "importance": 5,
        "course": "CS401",
    },
    {
        "title": "Physics Lab Report",
        "description": "Write up results from the optics experiment.",
        "task_type": "assignment",
        "deadline": today + timedelta(days=4),
        "estimated_hours": 2.5,
        "difficulty": 3,
        "importance": 3,
        "course": "PHY201",
    },
]

for t in tasks_data:
    db.add(models.Task(**t))

db.commit()
print(f"✅  Seeded {len(tasks_data)} tasks and preferences.")
db.close()

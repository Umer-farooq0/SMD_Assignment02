from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime, Text, ForeignKey, Date
from sqlalchemy.orm import relationship
from datetime import datetime
from .database import Base


class Task(Base):
    __tablename__ = "tasks"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(255), nullable=False)
    description = Column(Text, nullable=True)
    task_type = Column(String(50), nullable=False, default="other")  # assignment/quiz/midterm/final/other
    deadline = Column(Date, nullable=False)
    estimated_hours = Column(Float, nullable=False)
    difficulty = Column(Integer, nullable=False)  # 1-5
    importance = Column(Integer, nullable=False)  # 1-5
    course = Column(String(255), nullable=True)
    completed = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    schedule_items = relationship("ScheduleItem", back_populates="task", cascade="all, delete-orphan")


class Preference(Base):
    __tablename__ = "preferences"

    id = Column(Integer, primary_key=True, index=True)
    study_hours_per_day = Column(Float, default=6.0)
    study_start_time = Column(String(10), default="09:00")  # HH:MM
    study_end_time = Column(String(10), default="21:00")    # HH:MM
    short_break_minutes = Column(Integer, default=10)
    long_break_minutes = Column(Integer, default=30)
    sessions_before_long_break = Column(Integer, default=4)  # number of sessions before a long break
    session_length_minutes = Column(Integer, default=50)     # length of each study session
    namaz_enabled = Column(Boolean, default=False)
    fajr_time = Column(String(10), default="05:00")
    dhuhr_time = Column(String(10), default="13:00")
    asr_time = Column(String(10), default="16:30")
    maghrib_time = Column(String(10), default="18:45")
    isha_time = Column(String(10), default="20:30")
    namaz_duration_minutes = Column(Integer, default=20)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)


class Schedule(Base):
    __tablename__ = "schedules"

    id = Column(Integer, primary_key=True, index=True)
    generated_at = Column(DateTime, default=datetime.utcnow)
    is_active = Column(Boolean, default=True)

    items = relationship("ScheduleItem", back_populates="schedule", cascade="all, delete-orphan")


class ScheduleItem(Base):
    __tablename__ = "schedule_items"

    id = Column(Integer, primary_key=True, index=True)
    schedule_id = Column(Integer, ForeignKey("schedules.id"), nullable=False)
    task_id = Column(Integer, ForeignKey("tasks.id"), nullable=True)  # null for breaks/namaz
    date = Column(Date, nullable=False)
    start_time = Column(String(10), nullable=False)   # HH:MM
    end_time = Column(String(10), nullable=False)     # HH:MM
    item_type = Column(String(50), nullable=False)    # study/short_break/long_break/namaz
    title = Column(String(255), nullable=False)
    completed = Column(Boolean, default=False)
    session_number = Column(Integer, default=1)       # which session of the task

    schedule = relationship("Schedule", back_populates="items")
    task = relationship("Task", back_populates="schedule_items")


class Progress(Base):
    __tablename__ = "progress"

    id = Column(Integer, primary_key=True, index=True)
    date = Column(Date, nullable=False, unique=True)
    sessions_completed = Column(Integer, default=0)
    hours_studied = Column(Float, default=0.0)
    tasks_completed = Column(Integer, default=0)
    streak_days = Column(Integer, default=0)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

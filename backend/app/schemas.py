from pydantic import BaseModel, Field, validator
from typing import Optional, List
from datetime import date, datetime


# ── Task schemas ──────────────────────────────────────────────────────────────

class TaskBase(BaseModel):
    title: str = Field(..., min_length=1, max_length=255)
    description: Optional[str] = None
    task_type: str = Field(default="other")
    deadline: date
    estimated_hours: float = Field(..., gt=0, le=100)
    difficulty: int = Field(..., ge=1, le=5)
    importance: int = Field(..., ge=1, le=5)
    course: Optional[str] = None

    @validator("task_type")
    def validate_task_type(cls, v):
        allowed = {"assignment", "quiz", "midterm", "final", "other"}
        if v not in allowed:
            raise ValueError(f"task_type must be one of {allowed}")
        return v


class TaskCreate(TaskBase):
    pass


class TaskUpdate(BaseModel):
    title: Optional[str] = Field(None, min_length=1, max_length=255)
    description: Optional[str] = None
    task_type: Optional[str] = None
    deadline: Optional[date] = None
    estimated_hours: Optional[float] = Field(None, gt=0, le=100)
    difficulty: Optional[int] = Field(None, ge=1, le=5)
    importance: Optional[int] = Field(None, ge=1, le=5)
    course: Optional[str] = None
    completed: Optional[bool] = None

    @validator("task_type")
    def validate_task_type(cls, v):
        if v is None:
            return v
        allowed = {"assignment", "quiz", "midterm", "final", "other"}
        if v not in allowed:
            raise ValueError(f"task_type must be one of {allowed}")
        return v


class TaskResponse(TaskBase):
    id: int
    completed: bool
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True


# ── Preference schemas ────────────────────────────────────────────────────────

class PreferenceBase(BaseModel):
    study_hours_per_day: float = Field(default=6.0, gt=0, le=16)
    study_start_time: str = Field(default="09:00")
    study_end_time: str = Field(default="21:00")
    short_break_minutes: int = Field(default=10, ge=5, le=30)
    long_break_minutes: int = Field(default=30, ge=15, le=60)
    sessions_before_long_break: int = Field(default=4, ge=2, le=8)
    session_length_minutes: int = Field(default=50, ge=25, le=120)
    namaz_enabled: bool = False
    fajr_time: str = "05:00"
    dhuhr_time: str = "13:00"
    asr_time: str = "16:30"
    maghrib_time: str = "18:45"
    isha_time: str = "20:30"
    namaz_duration_minutes: int = Field(default=20, ge=5, le=60)


class PreferenceUpdate(PreferenceBase):
    pass


class PreferenceResponse(PreferenceBase):
    id: int
    updated_at: datetime

    class Config:
        from_attributes = True


# ── Schedule schemas ──────────────────────────────────────────────────────────

class ScheduleItemResponse(BaseModel):
    id: int
    schedule_id: int
    task_id: Optional[int]
    date: date
    start_time: str
    end_time: str
    item_type: str
    title: str
    completed: bool
    session_number: int

    class Config:
        from_attributes = True


class ScheduleResponse(BaseModel):
    id: int
    generated_at: datetime
    is_active: bool
    items: List[ScheduleItemResponse]

    class Config:
        from_attributes = True


class ScheduleGenerateRequest(BaseModel):
    days_ahead: int = Field(default=7, ge=1, le=30)


# ── Progress schemas ──────────────────────────────────────────────────────────

class ProgressResponse(BaseModel):
    id: int
    date: date
    sessions_completed: int
    hours_studied: float
    tasks_completed: int
    streak_days: int
    updated_at: datetime

    class Config:
        from_attributes = True


class ProgressSummary(BaseModel):
    total_tasks: int
    completed_tasks: int
    remaining_tasks: int
    completion_rate: float
    current_streak: int
    total_hours_studied: float
    today_sessions: int

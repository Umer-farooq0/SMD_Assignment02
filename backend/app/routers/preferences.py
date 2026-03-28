from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/preferences", tags=["preferences"])

DEFAULT_PREFS = {
    "study_hours_per_day": 6.0,
    "study_start_time": "09:00",
    "study_end_time": "21:00",
    "short_break_minutes": 10,
    "long_break_minutes": 30,
    "sessions_before_long_break": 4,
    "session_length_minutes": 50,
    "namaz_enabled": False,
    "fajr_time": "05:00",
    "dhuhr_time": "13:00",
    "asr_time": "16:30",
    "maghrib_time": "18:45",
    "isha_time": "20:30",
    "namaz_duration_minutes": 20,
}


def _get_or_create_prefs(db: Session) -> models.Preference:
    prefs = db.query(models.Preference).first()
    if not prefs:
        prefs = models.Preference(**DEFAULT_PREFS)
        db.add(prefs)
        db.commit()
        db.refresh(prefs)
    return prefs


@router.get("/", response_model=schemas.PreferenceResponse)
def get_preferences(db: Session = Depends(get_db)):
    return _get_or_create_prefs(db)


@router.put("/", response_model=schemas.PreferenceResponse)
def update_preferences(prefs_update: schemas.PreferenceUpdate, db: Session = Depends(get_db)):
    prefs = _get_or_create_prefs(db)
    for field, value in prefs_update.model_dump().items():
        setattr(prefs, field, value)
    db.commit()
    db.refresh(prefs)
    return prefs

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List
from datetime import date
from .. import models, schemas
from ..database import get_db
from ..scheduler import generate_schedule
from .preferences import _get_or_create_prefs

router = APIRouter(prefix="/schedule", tags=["schedule"])


@router.post("/generate", response_model=schemas.ScheduleResponse)
def generate(
    request: schemas.ScheduleGenerateRequest,
    db: Session = Depends(get_db),
):
    # Deactivate any existing active schedules
    db.query(models.Schedule).filter(models.Schedule.is_active == True).update({"is_active": False})
    db.commit()

    tasks = db.query(models.Task).all()
    prefs = _get_or_create_prefs(db)
    today = date.today()

    items_data = generate_schedule(tasks, prefs, today, request.days_ahead)

    new_schedule = models.Schedule()
    db.add(new_schedule)
    db.flush()  # get the ID

    for item in items_data:
        db_item = models.ScheduleItem(
            schedule_id=new_schedule.id,
            **item,
        )
        db.add(db_item)

    db.commit()
    db.refresh(new_schedule)
    return new_schedule


@router.get("/active", response_model=schemas.ScheduleResponse)
def get_active_schedule(db: Session = Depends(get_db)):
    schedule = db.query(models.Schedule).filter(models.Schedule.is_active == True).first()
    if not schedule:
        raise HTTPException(status_code=404, detail="No active schedule found. Generate one first.")
    return schedule


@router.get("/", response_model=List[schemas.ScheduleResponse])
def list_schedules(db: Session = Depends(get_db)):
    return db.query(models.Schedule).order_by(models.Schedule.generated_at.desc()).all()


@router.patch("/items/{item_id}/complete", response_model=schemas.ScheduleItemResponse)
def mark_item_complete(item_id: int, db: Session = Depends(get_db)):
    item = db.query(models.ScheduleItem).filter(models.ScheduleItem.id == item_id).first()
    if not item:
        raise HTTPException(status_code=404, detail="Schedule item not found")
    item.completed = not item.completed
    db.commit()
    db.refresh(item)
    return item

"""API integration tests."""
import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from datetime import date, timedelta

from app.main import app
from app.database import get_db, Base
from app import models

# Use an in-memory SQLite database for tests
TEST_DATABASE_URL = "sqlite:///./test.db"

engine = create_engine(TEST_DATABASE_URL, connect_args={"check_same_thread": False})
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def override_get_db():
    db = TestingSessionLocal()
    try:
        yield db
    finally:
        db.close()


app.dependency_overrides[get_db] = override_get_db


@pytest.fixture(autouse=True)
def setup_db():
    Base.metadata.create_all(bind=engine)
    yield
    Base.metadata.drop_all(bind=engine)


@pytest.fixture
def client():
    with TestClient(app) as c:
        yield c


# ── root / health ─────────────────────────────────────────────────────────────

def test_root(client):
    response = client.get("/")
    assert response.status_code == 200
    assert "AI Productivity Maximizer" in response.json()["message"]


def test_health(client):
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"


# ── tasks ─────────────────────────────────────────────────────────────────────

TASK_PAYLOAD = {
    "title": "Test Assignment",
    "description": "Test description",
    "task_type": "assignment",
    "deadline": str(date.today() + timedelta(days=5)),
    "estimated_hours": 3.0,
    "difficulty": 3,
    "importance": 4,
    "course": "CS101",
}


def test_create_task(client):
    response = client.post("/tasks/", json=TASK_PAYLOAD)
    assert response.status_code == 201
    data = response.json()
    assert data["title"] == "Test Assignment"
    assert data["completed"] is False
    assert "id" in data


def test_list_tasks(client):
    client.post("/tasks/", json=TASK_PAYLOAD)
    response = client.get("/tasks/")
    assert response.status_code == 200
    assert len(response.json()) == 1


def test_get_task(client):
    create_resp = client.post("/tasks/", json=TASK_PAYLOAD)
    task_id = create_resp.json()["id"]
    response = client.get(f"/tasks/{task_id}")
    assert response.status_code == 200
    assert response.json()["id"] == task_id


def test_get_task_not_found(client):
    response = client.get("/tasks/9999")
    assert response.status_code == 404


def test_update_task(client):
    create_resp = client.post("/tasks/", json=TASK_PAYLOAD)
    task_id = create_resp.json()["id"]
    response = client.put(f"/tasks/{task_id}", json={"title": "Updated Title", "completed": True})
    assert response.status_code == 200
    assert response.json()["title"] == "Updated Title"
    assert response.json()["completed"] is True


def test_delete_task(client):
    create_resp = client.post("/tasks/", json=TASK_PAYLOAD)
    task_id = create_resp.json()["id"]
    response = client.delete(f"/tasks/{task_id}")
    assert response.status_code == 204
    response = client.get(f"/tasks/{task_id}")
    assert response.status_code == 404


def test_create_task_invalid_type(client):
    payload = {**TASK_PAYLOAD, "task_type": "invalid_type"}
    response = client.post("/tasks/", json=payload)
    assert response.status_code == 422


def test_create_task_invalid_difficulty(client):
    payload = {**TASK_PAYLOAD, "difficulty": 10}
    response = client.post("/tasks/", json=payload)
    assert response.status_code == 422


# ── preferences ───────────────────────────────────────────────────────────────

def test_get_preferences_creates_defaults(client):
    response = client.get("/preferences/")
    assert response.status_code == 200
    data = response.json()
    assert data["study_hours_per_day"] == 6.0


def test_update_preferences(client):
    response = client.put("/preferences/", json={
        "study_hours_per_day": 8.0,
        "study_start_time": "08:00",
        "study_end_time": "22:00",
        "short_break_minutes": 10,
        "long_break_minutes": 30,
        "sessions_before_long_break": 4,
        "session_length_minutes": 50,
        "namaz_enabled": True,
        "fajr_time": "05:00",
        "dhuhr_time": "13:00",
        "asr_time": "16:30",
        "maghrib_time": "18:45",
        "isha_time": "20:30",
        "namaz_duration_minutes": 20,
    })
    assert response.status_code == 200
    assert response.json()["study_hours_per_day"] == 8.0
    assert response.json()["namaz_enabled"] is True


# ── schedule ──────────────────────────────────────────────────────────────────

def test_generate_schedule_no_tasks(client):
    response = client.post("/schedule/generate", json={"days_ahead": 3})
    assert response.status_code == 200
    assert response.json()["is_active"] is True


def test_generate_schedule_with_tasks(client):
    client.post("/tasks/", json=TASK_PAYLOAD)
    response = client.post("/schedule/generate", json={"days_ahead": 3})
    assert response.status_code == 200
    data = response.json()
    assert len(data["items"]) > 0


def test_get_active_schedule(client):
    client.post("/schedule/generate", json={"days_ahead": 3})
    response = client.get("/schedule/active")
    assert response.status_code == 200


def test_get_active_schedule_not_found(client):
    response = client.get("/schedule/active")
    assert response.status_code == 404


def test_mark_item_complete(client):
    client.post("/tasks/", json=TASK_PAYLOAD)
    gen_resp = client.post("/schedule/generate", json={"days_ahead": 3})
    items = gen_resp.json()["items"]
    study_items = [i for i in items if i["item_type"] == "study"]
    if study_items:
        item_id = study_items[0]["id"]
        response = client.patch(f"/schedule/items/{item_id}/complete")
        assert response.status_code == 200
        assert response.json()["completed"] is True


# ── progress ──────────────────────────────────────────────────────────────────

def test_progress_summary_empty(client):
    response = client.get("/progress/summary")
    assert response.status_code == 200
    data = response.json()
    assert data["total_tasks"] == 0
    assert data["completion_rate"] == 0.0


def test_progress_summary_with_tasks(client):
    client.post("/tasks/", json=TASK_PAYLOAD)
    response = client.get("/progress/summary")
    assert response.status_code == 200
    data = response.json()
    assert data["total_tasks"] == 1
    assert data["completed_tasks"] == 0

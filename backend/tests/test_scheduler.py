"""Tests for the AI scheduler."""
from datetime import date, timedelta
import pytest
from app.scheduler import compute_priority_score, generate_schedule, _time_to_minutes, _minutes_to_time


class MockTask:
    def __init__(self, id, title, deadline_offset, estimated_hours, difficulty, importance,
                 task_type="assignment", completed=False):
        self.id = id
        self.title = title
        self.deadline = date.today() + timedelta(days=deadline_offset)
        self.estimated_hours = estimated_hours
        self.difficulty = difficulty
        self.importance = importance
        self.task_type = task_type
        self.completed = completed


class MockPreferences:
    def __init__(self, namaz_enabled=False):
        self.study_hours_per_day = 6.0
        self.study_start_time = "09:00"
        self.study_end_time = "21:00"
        self.short_break_minutes = 10
        self.long_break_minutes = 30
        self.sessions_before_long_break = 4
        self.session_length_minutes = 50
        self.namaz_enabled = namaz_enabled
        self.fajr_time = "05:00"
        self.dhuhr_time = "13:00"
        self.asr_time = "16:30"
        self.maghrib_time = "18:45"
        self.isha_time = "20:30"
        self.namaz_duration_minutes = 20


# ── helper tests ──────────────────────────────────────────────────────────────

def test_time_to_minutes():
    assert _time_to_minutes("09:00") == 540
    assert _time_to_minutes("21:00") == 1260
    assert _time_to_minutes("00:30") == 30


def test_minutes_to_time():
    assert _minutes_to_time(540) == "09:00"
    assert _minutes_to_time(1260) == "21:00"
    assert _minutes_to_time(65) == "01:05"


# ── priority scoring tests ────────────────────────────────────────────────────

def test_overdue_task_gets_high_score():
    task = MockTask(1, "Overdue", -2, 3.0, 3, 3)
    score = compute_priority_score(task, date.today())
    # overdue urgency=15: 15*0.5 + 3*0.25 + 3*0.25 = 9.0; non-overdue 5-day task: 5*0.5+3*0.25+3*0.25 = 4.0
    assert score >= 9.0  # overdue tasks score higher than non-overdue ones

    # Verify overdue scores higher than a task with same difficulty/importance but 5 days away
    non_overdue = MockTask(2, "Non-overdue", 5, 3.0, 3, 3)
    assert score > compute_priority_score(non_overdue, date.today())


def test_closer_deadline_scores_higher():
    task_soon = MockTask(1, "Soon", 1, 3.0, 3, 3)
    task_later = MockTask(2, "Later", 10, 3.0, 3, 3)
    today = date.today()
    assert compute_priority_score(task_soon, today) > compute_priority_score(task_later, today)


def test_final_type_has_bonus():
    task_final = MockTask(1, "Final", 5, 3.0, 3, 3, task_type="final")
    task_assignment = MockTask(2, "Assignment", 5, 3.0, 3, 3, task_type="assignment")
    today = date.today()
    assert compute_priority_score(task_final, today) > compute_priority_score(task_assignment, today)


def test_higher_difficulty_scores_higher():
    task_hard = MockTask(1, "Hard", 5, 3.0, 5, 3)
    task_easy = MockTask(2, "Easy", 5, 3.0, 1, 3)
    today = date.today()
    assert compute_priority_score(task_hard, today) > compute_priority_score(task_easy, today)


# ── schedule generation tests ────────────────────────────────────────────────

def test_schedule_generates_items():
    tasks = [MockTask(1, "Math", 3, 2.0, 3, 4)]
    prefs = MockPreferences()
    items = generate_schedule(tasks, prefs, date.today(), days_ahead=3)
    study_items = [i for i in items if i["item_type"] == "study"]
    assert len(study_items) > 0


def test_completed_tasks_excluded():
    tasks = [MockTask(1, "Done", 3, 2.0, 3, 4, completed=True)]
    prefs = MockPreferences()
    items = generate_schedule(tasks, prefs, date.today(), days_ahead=3)
    study_items = [i for i in items if i["item_type"] == "study"]
    assert len(study_items) == 0


def test_breaks_inserted_between_sessions():
    tasks = [MockTask(1, "Long Task", 3, 10.0, 3, 4)]
    prefs = MockPreferences()
    items = generate_schedule(tasks, prefs, date.today(), days_ahead=7)
    break_items = [i for i in items if i["item_type"] in ("short_break", "long_break")]
    assert len(break_items) > 0


def test_namaz_blocks_inserted_when_enabled():
    tasks = [MockTask(1, "Task", 3, 2.0, 3, 4)]
    prefs = MockPreferences(namaz_enabled=True)
    items = generate_schedule(tasks, prefs, date.today(), days_ahead=1)
    namaz_items = [i for i in items if i["item_type"] == "namaz"]
    # At least some namaz blocks should be in the study window (13:00 is Dhuhr, within 09:00-21:00)
    assert len(namaz_items) > 0


def test_schedule_items_have_required_fields():
    tasks = [MockTask(1, "Task", 3, 2.0, 3, 4)]
    prefs = MockPreferences()
    items = generate_schedule(tasks, prefs, date.today(), days_ahead=1)
    for item in items:
        assert "task_id" in item
        assert "date" in item
        assert "start_time" in item
        assert "end_time" in item
        assert "item_type" in item
        assert "title" in item
        assert "session_number" in item


def test_time_ordering_within_day():
    """Items within a single day should be ordered chronologically."""
    tasks = [MockTask(1, "Task", 3, 5.0, 3, 4)]
    prefs = MockPreferences()
    today = date.today()
    items = generate_schedule(tasks, prefs, today, days_ahead=1)
    day_items = [i for i in items if i["date"] == today]
    for i in range(len(day_items) - 1):
        assert _time_to_minutes(day_items[i]["start_time"]) <= _time_to_minutes(day_items[i + 1]["start_time"])


def test_sessions_do_not_exceed_day_end():
    tasks = [MockTask(1, "Task", 3, 20.0, 3, 4)]
    prefs = MockPreferences()
    today = date.today()
    items = generate_schedule(tasks, prefs, today, days_ahead=1)
    day_end = _time_to_minutes(prefs.study_end_time)
    for item in items:
        if item["date"] == today:
            assert _time_to_minutes(item["end_time"]) <= day_end


def test_higher_priority_task_scheduled_first():
    urgent = MockTask(1, "Urgent", 1, 1.0, 5, 5)
    relaxed = MockTask(2, "Relaxed", 10, 1.0, 1, 1)
    prefs = MockPreferences()
    items = generate_schedule([relaxed, urgent], prefs, date.today(), days_ahead=1)
    study_items = [i for i in items if i["item_type"] == "study"]
    # First study session should be for the urgent task
    assert "Urgent" in study_items[0]["title"]

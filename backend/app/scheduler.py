"""
AI Scheduling Agent

Prioritization formula:
    urgency_score   = max(0, 10 - days_to_deadline)  (closer deadline → higher)
    priority_score  = (urgency_score * 0.5) + (difficulty * 0.25) + (importance * 0.25)

Schedule generation:
  - Iterate over each day in the requested range
  - For each day, determine available minutes (respecting study_start / study_end times)
  - Reserve Namaz blocks if enabled
  - Fill available slots with task sessions (split by session_length_minutes)
  - Insert short breaks between sessions; long break after every N sessions
"""

from datetime import date, timedelta, datetime
from typing import List, Dict, Optional, Tuple
import math


TASK_TYPE_URGENCY_BONUS = {
    "final": 3,
    "midterm": 2,
    "quiz": 1,
    "assignment": 0,
    "other": 0,
}


def _parse_time(time_str: str) -> Tuple[int, int]:
    """Parse 'HH:MM' into (hour, minute)."""
    h, m = time_str.split(":")
    return int(h), int(m)


def _time_to_minutes(time_str: str) -> int:
    h, m = _parse_time(time_str)
    return h * 60 + m


def _minutes_to_time(minutes: int) -> str:
    h = (minutes // 60) % 24
    m = minutes % 60
    return f"{h:02d}:{m:02d}"


def compute_priority_score(task, today: date) -> float:
    """Return a numeric priority score for a task (higher = more urgent)."""
    days_remaining = (task.deadline - today).days
    # overdue tasks get maximum urgency
    if days_remaining < 0:
        urgency = 15.0
    else:
        urgency = max(0.0, 10.0 - days_remaining)

    type_bonus = TASK_TYPE_URGENCY_BONUS.get(task.task_type, 0)
    score = (urgency * 0.5) + (task.difficulty * 0.25) + (task.importance * 0.25) + type_bonus
    return round(score, 3)


def _get_next_namaz(cursor: int, day_end: int, namaz_times: List[Dict]) -> Optional[Dict]:
    """Return the soonest upcoming Namaz block that starts at or after cursor."""
    upcoming = [nb for nb in namaz_times if nb["start_minutes"] >= cursor and nb["start_minutes"] < day_end]
    if not upcoming:
        return None
    return min(upcoming, key=lambda nb: nb["start_minutes"])


def generate_schedule(tasks, preferences, today: date, days_ahead: int = 7) -> List[Dict]:
    """
    Generate a list of schedule item dicts for the given date range.

    Returns list of dicts with keys:
        task_id, date, start_time, end_time, item_type, title, session_number
    """
    # Filter out completed tasks
    pending_tasks = [t for t in tasks if not t.completed]

    # Sort tasks by priority (descending)
    sorted_tasks = sorted(
        pending_tasks,
        key=lambda t: compute_priority_score(t, today),
        reverse=True
    )

    # Build a work queue: each entry tracks remaining work for a task
    work_queue: List[Dict] = []
    for task in sorted_tasks:
        work_queue.append({
            "task": task,
            "remaining_minutes": int(task.estimated_hours * 60),
            "session_counter": 0,
        })

    # Build sorted Namaz blocks for the study window (if enabled)
    namaz_times: List[Dict] = []
    if preferences.namaz_enabled:
        prayer_names = ["Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"]
        raw_fields = [
            preferences.fajr_time,
            preferences.dhuhr_time,
            preferences.asr_time,
            preferences.maghrib_time,
            preferences.isha_time,
        ]
        dur = preferences.namaz_duration_minutes
        for name, t in zip(prayer_names, raw_fields):
            start_m = _time_to_minutes(t)
            namaz_times.append({
                "name": name,
                "start_minutes": start_m,
                "end_minutes": start_m + dur,
            })
        # Keep only prayers that fall within the study window
        day_start_m = _time_to_minutes(preferences.study_start_time)
        day_end_m = _time_to_minutes(preferences.study_end_time)
        namaz_times = [
            nb for nb in namaz_times
            if nb["start_minutes"] < day_end_m and nb["end_minutes"] > day_start_m
        ]
        namaz_times.sort(key=lambda nb: nb["start_minutes"])

    schedule_items = []
    session_length = preferences.session_length_minutes
    short_break = preferences.short_break_minutes
    long_break = preferences.long_break_minutes
    sessions_before_long = preferences.sessions_before_long_break

    day_start = _time_to_minutes(preferences.study_start_time)
    day_end = _time_to_minutes(preferences.study_end_time)

    consecutive_sessions = 0  # track sessions since last long break

    for day_offset in range(days_ahead):
        current_date = today + timedelta(days=day_offset)
        cursor = day_start  # minutes from midnight
        # Make a per-day copy of unseen Namaz blocks
        pending_namaz = list(namaz_times)

        while cursor < day_end:
            # Insert any Namaz blocks that start at or before cursor
            inserted_namaz = False
            for nb in list(pending_namaz):
                if nb["start_minutes"] <= cursor:
                    actual_start = max(cursor, nb["start_minutes"])
                    actual_end = min(day_end, nb["end_minutes"])
                    if actual_start < actual_end:
                        schedule_items.append({
                            "task_id": None,
                            "date": current_date,
                            "start_time": _minutes_to_time(actual_start),
                            "end_time": _minutes_to_time(actual_end),
                            "item_type": "namaz",
                            "title": f"{nb['name']} Prayer",
                            "session_number": 0,
                        })
                        cursor = actual_end
                    pending_namaz.remove(nb)
                    inserted_namaz = True
            if inserted_namaz:
                continue

            if not work_queue:
                break

            # Find next Namaz boundary
            next_nb = _get_next_namaz(cursor, day_end, pending_namaz)
            available_until = next_nb["start_minutes"] if next_nb else day_end
            available_until = min(available_until, day_end)

            # Can we fit any work before the next boundary?
            if cursor >= available_until:
                # Jump to end of the blocking Namaz
                if next_nb:
                    actual_end = min(day_end, next_nb["end_minutes"])
                    schedule_items.append({
                        "task_id": None,
                        "date": current_date,
                        "start_time": _minutes_to_time(cursor),
                        "end_time": _minutes_to_time(actual_end),
                        "item_type": "namaz",
                        "title": f"{next_nb['name']} Prayer",
                        "session_number": 0,
                    })
                    cursor = actual_end
                    pending_namaz = [nb for nb in pending_namaz if nb != next_nb]
                else:
                    break
                continue

            # Not enough room for minimum useful work
            if available_until - cursor < 15:
                # Advance past this gap
                cursor = available_until
                continue

            # Take next task from queue
            entry = work_queue[0]
            task = entry["task"]
            minutes_this_session = min(session_length, entry["remaining_minutes"])

            if cursor + minutes_this_session > available_until:
                minutes_this_session = available_until - cursor
                if minutes_this_session < 15:
                    # Jump to Namaz time and handle it next iteration
                    cursor = available_until
                    continue

            entry["remaining_minutes"] -= minutes_this_session
            entry["session_counter"] += 1
            session_num = entry["session_counter"]

            schedule_items.append({
                "task_id": task.id,
                "date": current_date,
                "start_time": _minutes_to_time(cursor),
                "end_time": _minutes_to_time(cursor + minutes_this_session),
                "item_type": "study",
                "title": f"{task.title} – Session {session_num}",
                "session_number": session_num,
            })

            cursor += minutes_this_session
            consecutive_sessions += 1

            # Remove task from queue if done
            if entry["remaining_minutes"] <= 0:
                work_queue.pop(0)

            # No more time today?
            if cursor >= day_end:
                break

            # Insert Namaz blocks that start right now before adding a break
            next_nb_check = _get_next_namaz(cursor, day_end, pending_namaz)
            if next_nb_check and next_nb_check["start_minutes"] <= cursor:
                continue  # will be handled at top of loop

            # Add break if there is more work to do
            if work_queue or (work_queue and work_queue[0]["remaining_minutes"] > 0):
                if consecutive_sessions > 0 and consecutive_sessions % sessions_before_long == 0:
                    break_duration = long_break
                    break_type = "long_break"
                    break_title = "Long Break"
                    consecutive_sessions = 0
                else:
                    break_duration = short_break
                    break_type = "short_break"
                    break_title = "Short Break"

                break_end = min(cursor + break_duration, day_end)
                # Don't schedule a break into a Namaz window
                if next_nb_check and break_end > next_nb_check["start_minutes"]:
                    break_end = next_nb_check["start_minutes"]

                if cursor < break_end:
                    schedule_items.append({
                        "task_id": None,
                        "date": current_date,
                        "start_time": _minutes_to_time(cursor),
                        "end_time": _minutes_to_time(break_end),
                        "item_type": break_type,
                        "title": break_title,
                        "session_number": 0,
                    })
                    cursor = break_end

        # Add any remaining Namaz blocks for today that weren't reached
        for nb in pending_namaz:
            actual_start = nb["start_minutes"]
            actual_end = min(day_end, nb["end_minutes"])
            if actual_start < day_end and actual_start >= cursor:
                schedule_items.append({
                    "task_id": None,
                    "date": current_date,
                    "start_time": _minutes_to_time(actual_start),
                    "end_time": _minutes_to_time(actual_end),
                    "item_type": "namaz",
                    "title": f"{nb['name']} Prayer",
                    "session_number": 0,
                })

        # Reset consecutive sessions counter at end of each day
        consecutive_sessions = 0

    return schedule_items

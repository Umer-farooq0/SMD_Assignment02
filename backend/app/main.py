from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .database import engine
from . import models
from .routers import tasks, preferences, schedule, progress

# Create all tables
models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="AI Productivity Maximizer API",
    description="Backend API for the AI-Based Productivity Maximizer for Students",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(tasks.router)
app.include_router(preferences.router)
app.include_router(schedule.router)
app.include_router(progress.router)


@app.get("/", tags=["root"])
def root():
    return {
        "message": "AI Productivity Maximizer API",
        "docs": "/docs",
        "version": "1.0.0",
    }


@app.get("/health", tags=["root"])
def health():
    return {"status": "ok"}

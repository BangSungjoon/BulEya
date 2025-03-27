# FastAPI 메인 실행파일
from fastapi import FastAPI
from app.api.v1.cctv import router as cctv_router
from dotenv import load_dotenv
load_dotenv()

app = FastAPI()

app.include_router(cctv_router, prefix="/ai")


# Redis 연결 테스트 용 (삭제해도 무방)
import redis
import os

r = redis.Redis(
    host=os.getenv("REDIS_HOST", "redis"),
    port=int(os.getenv("REDIS_PORT", 6379)),
    password=os.getenv("REDIS_PASSWORD"),
    decode_responses=True
)

@app.get("/")
def read_root():
    r.set("message", "Redis랑 FastAPI 연결 성공이요~")
    return {"redis": r.get("message")}
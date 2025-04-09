# FastAPI 메인 실행파일
from fastapi import FastAPI
from app.api.v1.cctv import router as cctv_router
from fastapi.middleware.cors import CORSMiddleware
from fastapi.openapi.utils import get_openapi
from dotenv import load_dotenv
load_dotenv()

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 모든 도메인 허용 (배포 시에는 제한)
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


def custom_openapi():
    if app.openapi_schema:
        return app.openapi_schema

    openapi_schema = get_openapi(
        title="CCTV AI Server",
        version="1.0.0",
        description="화재 감지 API입니다.",
        routes=app.routes,
    )
    # Swagger UI가 브라우저에서 요청할 서버 주소를 정확히 명시
    openapi_schema["servers"] = [
        {"url": "http://localhost:8000"}
    ]
    app.openapi_schema = openapi_schema
    return app.openapi_schema

app.openapi = custom_openapi

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
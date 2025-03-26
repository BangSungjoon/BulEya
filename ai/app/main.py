# FastAPI 메인 실행파일
from fastapi import FastAPI
from app.api.v1.cctv import router as cctv_router

app = FastAPI()

app.include_router(cctv_router, prefix="/ai")
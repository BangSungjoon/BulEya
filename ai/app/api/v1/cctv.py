from fastapi import APIRouter, UploadFile, File, Form
from typing import List
from app.services.detection import analyze_images
import json

router = APIRouter()

@router.post("/cctv-frame", tags=["cctv"])
async def receive_cctv_data(
    station_id: str = Form(...),
    cctv_list: str = Form(...),  # JSON 문자열
    files: List[UploadFile] = File(...)
):
    """
    Node.js로부터 받은 이미지와 JSON 데이터를 처리 후 AI 검사
    """
    # JSON 문자열을 파싱
    try:
        cctv_data = json.loads(cctv_list)
    except json.JSONDecodeError:
        return {"error": "Invalid JSON, cctv_data"}
    
    # 파일명을 기준으로 딕셔너리 변환
    image_dict = {
        file.filename.split('.')[0]: file
        for file in files
    }

    fire_images, fire_beacons = await analyze_images(image_dict, cctv_data)

    return { 
        "fire_beacons": fire_beacons,
        # "fire_images": fire_images,
    }

   # 수신된 파일 목록 출력(디버깅용)
    # file_info = [file.filename for file in files]

    # return {
    #     "message": "데이터 정상 수신",
    #     "station_id": station_id,
    #     "cctv_count": len(cctv_data),
    #     "receviced_files": file_info
    # }

@router.get("/test", tags=["test"])
async def test():
    return {"message": "test"}
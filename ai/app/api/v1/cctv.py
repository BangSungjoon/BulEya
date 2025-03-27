from fastapi import APIRouter, UploadFile, File, Form
from typing import List
from app.services.detection import analyze_images
from app.services.fire_report import report_fire
from app.core.redis import has_station, add_station, remove_station
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

    if fire_beacons:
        # 화재가 감지된 경우
        await report_fire(station_id, fire_images, cctv_data)
        if not has_station(station_id):
            # Redis에 화재가 난 역 id 값이 없다면, 추가
            add_station(station_id)
    else:
        if has_station(station_id):
            # Redis에 화재가 난 역 id 값이 있다면, 삭제
            remove_station(station_id)
            await report_fire(station_id, fire_images, cctv_data)

    # return { 
    #     "fire_beacons": fire_beacons,
    #     # "fire_images": fire_images,
    # }

@router.get("/test", tags=["test"])
async def test():
    return {"message": "test"}
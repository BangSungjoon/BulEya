from fastapi import APIRouter, UploadFile, File, Form
from typing import List
from app.services.detection import analyze_images
from app.services.fire_report import report_fire
from app.core.redis import (
    get_beacon_fire_count, set_beacon_fire_count, delete_beacon, has_beacon,
    get_station_fire_count, set_station_fire_count, delete_station, has_station
)
import json
import torch

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
    fire_beacon_set = set(int(b) for b in fire_beacons)  # 빠른 lookup을 위해 set 사용

    beacon_list = []  # 최종 전송할 beacon_list

    # 감지 결과 기반으로 Redis 상태 갱신
    for beacon in cctv_data:
        code = int(beacon["beacon_code"])

        if code in fire_beacon_set:
            # 화재 감지됨 → Redis에 초기화 또는 추가
            set_beacon_fire_count(code, 0)
        else:
            # 화재 감지되지 않음
            if has_beacon(code):
                count = get_beacon_fire_count(code) + 1
                if count >= 3:
                    delete_beacon(code)
                else:
                    set_beacon_fire_count(code, count)

    # 전송할 beacon_list 구성 (감지된 + Redis에 남은 것 포함)
    for beacon in cctv_data:
        code = int(beacon["beacon_code"])
        is_fire = 1 if code in fire_beacon_set or has_beacon(code) else 0
        
        beacon_list.append({
            "beacon_code": code,
            "is_active_fire": is_fire
        })

    # 전체적으로 화재가 감지되었는지 판단
    if fire_beacons:
        # 하나라도 화재 감지 → 역 단위 fire count 초기화
        set_station_fire_count(station_id, 0)
        await report_fire(station_id, fire_images, beacon_list)

    else:
        # 전체적으로 감지된 화재 없음
        current_count = get_station_fire_count(station_id) + 1
        if current_count >= 3:
            # 3회 연속 화재 없음 → 화재 종료 판단
            await report_fire(station_id, {}, beacon_list)
            delete_station(station_id)
        else:
            set_station_fire_count(station_id, current_count)

    return { 
        # "cctv_list": cctv_list,
        "fire_beacons": fire_beacons,
        "gpu": torch.cuda.is_available(),
    }

@router.get("/test", tags=["test"])
async def test():
    return {"message": "test"}
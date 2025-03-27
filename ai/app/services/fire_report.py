import requests
import json
from typing import Dict, List
# from fastapi import UploadFile
import os

# SPRING_ENDPOINT = "http://localhost:8080/api/fire-report"
SPRING_ENDPOINT = os.getenv("SPRING_BOOT_ENDPOINT")

async def report_fire(
        station_id: str, 
        # fire_images: Dict[str, UploadFile], # 불난 사진들만
        fire_images: Dict[str, dict],
        cctv_list: List[dict]):             # 전체 비콘 목록
    """
    화재 감지 결과를 Spring 서버로 전송하는 함수

    Args:
        station_id: 역 id
        fire_images: 화재로 감지된 이미지 파일
        cctv_list: 전체 CCTV 목록
    """
    files = []
    beacon_list = []

    for beacon_code, value in fire_images.items():
        print(f"[디버깅] beacon_code: {beacon_code}, 타입: {type(value)}")


    for beacon in cctv_list:
        beacon_code = beacon["beacon_code"]

        # 화재로 감지된 경우
        if beacon_code in fire_images:
            file_info = fire_images[beacon_code]

            # Spring이 기대하는 구조: ("files", (filename, data, content_type))
            files.append((
                "files",
                (file_info["filename"], file_info["data"], file_info["content_type"])
            ))

            # 불이야~
            beacon_list.append({
                "beacon_code": int(beacon_code),
                "is_active_fire": 1
            })
        else:
            # 정상으로 보고
            beacon_list.append({
                "beacon_code": int(beacon_code),
                "is_active_fire": 0
            })

    payload = {
        "station_id": station_id,
        "beacon_list": beacon_list
    }

    # JSON도 files 파라미터에 같이 넣기 (None 대신 str)
    files.append((
        "fireReportDto",
        ("fireReportDto", json.dumps(payload), "application/json")
    ))

    try:
        response = requests.post(
            SPRING_ENDPOINT,
            # data={"fireReportDto": (None, json.dumps(payload), "application/json")},
            files=files
        )
        print("[전송 JSON]", json.dumps(payload, indent=2))
        print("[전송 파일 목록]", [f[1][0] for f in files])

        print(f"Spring 전송 완료: {response.status_code}")
        print(f"Spring 응답: {response.text}")
        if response.status_code != 200:
            print("Spring 응답 오류:", response.text)
    except Exception as e:
        print("전송 중 오류:", e)

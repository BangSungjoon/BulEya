# import requests
# import json
# from typing import Dict, List
# import os

# # SPRING_ENDPOINT = "http://localhost:8080/api/fire-report"
# SPRING_ENDPOINT = os.getenv("SPRING_BOOT_ENDPOINT")

# async def report_fire(
#         station_id: str, 
#         # fire_images: Dict[str, UploadFile], # 불난 사진들만
#         fire_images: Dict[str, dict],
#         cctv_list: List[dict]):             # 전체 비콘 목록
#     """
#     화재 감지 결과를 Spring 서버로 전송하는 함수

#     Args:
#         station_id: 역 id
#         fire_images: 화재로 감지된 이미지 파일
#         cctv_list: 전체 CCTV 목록
#     """
#     if not SPRING_ENDPOINT:
#         raise RuntimeError("환경변수 SPRING_BOOT_ENDPOINT가 설정되어 있지 않습니다.")
    
#     files = []
#     beacon_list = []

#     for beacon in cctv_list:
#         beacon_code = beacon["beacon_code"]

#         # 화재로 감지된 경우
#         if beacon_code in fire_images:
#             file_info = fire_images[beacon_code]

#             # Spring이 기대하는 구조: ("files", (filename, data, content_type))
#             files.append((
#                 "files",
#                 (file_info["filename"], file_info["data"], file_info["content_type"])
#             ))

#             # 불이야~
#             beacon_list.append({
#                 "beacon_code": int(beacon_code),
#                 "is_active_fire": 1
#             })
#         else:
#             # 정상으로 보고
#             beacon_list.append({
#                 "beacon_code": int(beacon_code),
#                 "is_active_fire": 0
#             })

#     payload = {
#         "station_id": station_id,
#         "beacon_list": beacon_list
#     }

#     # JSON도 files 파라미터에 같이 넣기 (None 대신 str)
#     files.append((
#         "fireReportDto",
#         ("fireReportDto", json.dumps(payload), "application/json")
#     ))

#     try:
#         response = requests.post(
#             SPRING_ENDPOINT,
#             # data={"fireReportDto": (None, json.dumps(payload), "application/json")},
#             files=files
#         )
#         print("[전송 JSON]", json.dumps(payload, indent=2))
#         print("[전송 파일 목록]", [f[1][0] for f in files])

#         print(f"Spring 전송 완료: {response.status_code}")
#         print(f"Spring 응답: {response.text}")
#         if response.status_code != 200:
#             print("Spring 응답 오류:", response.text)
#     except Exception as e:
#         print("전송 중 오류:", e)

import httpx
import json
from typing import Dict, List
import os
from datetime import datetime

SPRING_ENDPOINT = os.getenv("SPRING_BOOT_ENDPOINT")

async def report_fire(
    station_id: str,
    fire_images: Dict[str, dict],  # { beacon_code: {filename, content_type, data} }
    beacon_list: List[dict]          # [{ beacon_code, rtsp_ip }]
):
    """
    화재 감지 결과를 Spring 서버로 전송하는 함수

    Args:
        station_id: 역 id
        fire_images: 화재로 감지된 이미지 파일
        cctv_list: 전체 CCTV 목록
    """

    if not SPRING_ENDPOINT:
        raise RuntimeError("환경변수 SPRING_BOOT_ENDPOINT가 설정되어 있지 않습니다.")

    # files = []
    # beacon_list = []
    # fire_codes = set(fire_images.keys())  # Set으로 빠르게 확인

    # # 파일 추가
    # for beacon_code in fire_codes:
    #     file_info = fire_images[beacon_code]
    #     files.append((
    #         "files",
    #         (file_info["filename"], file_info["data"], file_info["content_type"])
    #     ))

    # # 비콘 리스트 구성
    # for beacon in cctv_list:
    #     beacon_code = beacon["beacon_code"]
    #     is_fire = 1 if beacon_code in fire_codes else 0
    #     beacon_list.append({
    #         "beacon_code": int(beacon_code),
    #         "is_active_fire": is_fire
    #     })

    # # JSON DTO 추가
    # payload = {
    #     "station_id": station_id,
    #     "beacon_list": beacon_list
    # }

    # files.append((
    #     "json",
    #     ("json", json.dumps(payload), "application/json")
    # ))

    # try:
    #     async with httpx.AsyncClient() as client:
    #         response = await client.post(SPRING_ENDPOINT, files=files)

    #     print("[전송 시각]", datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
    #     print("[전송 JSON]", json.dumps(payload, indent=2))
    #     print("[전송 파일 목록]", [f[1][0] for f in files])
    #     print(f"Spring 응답 코드: {response.status_code}")
    #     print(f"Spring 응답: {response.text}")

    #     if response.status_code != 200:
    #         print("Spring 응답 오류:", response.text)

    # except Exception as e:
    #     print("전송 중 오류:", e)

    files = []

    # 이미지 파일은 fire_images에 존재하는 beacon만 전송 (is_active_fire == 1인 것들)
    for beacon_code, file_info in fire_images.items():
        files.append((
            "files",
            (file_info["filename"], file_info["data"], file_info["content_type"])
        ))

    # JSON DTO (beacon_list 전체 포함)
    payload = {
        "station_id": station_id,
        "beacon_list": beacon_list
    }

    files.append((
        "json",
        ("json", json.dumps(payload), "application/json")
    ))

    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(SPRING_ENDPOINT, files=files)

        # 로그 출력
        print("[전송 JSON]", json.dumps(payload, indent=2))
        print("[전송 파일 목록]", [f[1][0] for f in files])
        print(f"Spring 전송 완료: {response.status_code}")
        print(f"Spring 응답: {response.text}")

        if response.status_code != 200:
            print("Spring 응답 오류:", response.text)

    except Exception as e:
        print("전송 중 오류:", e)
# from ultralytics import YOLO
# from PIL import Image
# import io

# model_n = YOLO("models/n_best.pt")
# model_m = YOLO("models/m_best.pt")

# async def analyze_images(file_dict: dict, cctv_list: list):
#     """
#     업로드된 이미지를 분석하여 화재를 탐지하는 함수

#     Args:
#         file_dict: 파일명을 기준으로 딕셔너리로 변환된 이미지 파일
#         cctv_list: [{ beacon_code, rtsp_ip }]
#     """

#     fire_images = {}   # beacon_code: image 바이트
#     fire_beacons = []  # fire로 확정된 beacon_code 리스트

#     for beacon in cctv_list:
#         beacon_code = beacon["beacon_code"]

#         # beacon_code와 일치하는 이미지가 업로드됐는지 확인
#         if beacon_code not in file_dict:
#             print("일치하는 이미지가 아닌데?")
#             continue

#         upload_file = file_dict[beacon_code]
#         image_data = await upload_file.read()
#         image = Image.open(io.BytesIO(image_data)).convert("RGB")

#         # 1차 감지 (YOLOv11n)
#         results_n = model_n.predict(image)
#         if detect_fire(results_n):
#             print('1차 감지 됨')
#             # 2차 감지 (YOLOv11m)
#             results_m = model_m.predict(image)
#             if detect_fire(results_m):
#                 print('2차 감지 됨')
#                 # 화재 확정
#                 fire_images[beacon_code] = {
#                     "filename": upload_file.filename,
#                     "content_type": upload_file.content_type,
#                     "data": image_data
#                 }
#                 fire_beacons.append(beacon_code)

#     return fire_images, fire_beacons



# def detect_fire(results) -> bool:
#     """
#     YOLO 감지 결과에서 'fl' 클래스를 탐지했는지 확인하는 함수

#     Args:
#         results (List[DetectionResult]): YOLO 감지 결과

#     Returns:
#         bool: 'fl' 클래스를 탐지했는지 여부
#     """
#     for result in results:
#         names = result.names  # 클래스 ID → 클래스명 매핑
#         for box in result.boxes:
#             class_id = int(box.cls[0].item())
#             label = names[class_id]
#             if "fl" in label.lower():
#                 return True
#     return False
from ultralytics import YOLO
from PIL import Image
import io
import asyncio
from concurrent.futures import ThreadPoolExecutor

model_n = YOLO("models/n_best.pt")
model_m = YOLO("models/m_best.pt")
executor = ThreadPoolExecutor()


def detect_fire(results) -> bool:
    for result in results:
        names = result.names
        for box in result.boxes:
            class_id = int(box.cls[0].item())
            label = names[class_id]
            if "fl" in label.lower():
                return True
    return False


def run_detection(beacon_code, image_data, filename, content_type):
    image = Image.open(io.BytesIO(image_data)).convert("RGB")

    # 1차 감지
    results_n = model_n.predict(image)
    if detect_fire(results_n):
        print(f"{beacon_code}: 1차 감지 됨")
        # 2차 감지
        results_m = model_m.predict(image)
        if detect_fire(results_m):
            print(f"{beacon_code}: 2차 감지 됨")
            return beacon_code, {
                "filename": filename,
                "content_type": content_type,
                "data": image_data
            }
    return None


async def analyze_images(file_dict: dict, cctv_list: list):
    fire_images = {}
    fire_beacons = []

    # 1. 이미지 비동기적으로 읽어오기
    read_tasks = []
    for beacon in cctv_list:
        beacon_code = beacon["beacon_code"]
        if beacon_code in file_dict:
            upload_file = file_dict[beacon_code]
            read_tasks.append((beacon_code, upload_file))

    async def read_image(beacon_code, upload_file):
        image_data = await upload_file.read()
        return beacon_code, image_data, upload_file.filename, upload_file.content_type

    read_results = await asyncio.gather(*[
        read_image(b_code, u_file) for b_code, u_file in read_tasks
    ])

    # 2. YOLO 감지 병렬 처리
    loop = asyncio.get_event_loop()
    detect_tasks = [
        loop.run_in_executor(executor, run_detection, b_code, img_data, filename, content_type)
        for b_code, img_data, filename, content_type in read_results
    ]

    detect_results = await asyncio.gather(*detect_tasks)

    for result in detect_results:
        if result:
            beacon_code, img_info = result
            fire_images[beacon_code] = img_info
            fire_beacons.append(beacon_code)

    return fire_images, fire_beacons

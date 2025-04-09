from ultralytics import YOLO
from PIL import Image
import io
import asyncio
from concurrent.futures import ThreadPoolExecutor

model_n = YOLO("models/n_best.pt")
model_m = YOLO("models/m_best.pt")
model_x = YOLO("models/x_best.pt")
executor = ThreadPoolExecutor()

import os
from datetime import datetime

SAVE_DIR = "outputs"  # 저장 폴더

os.makedirs(f"{SAVE_DIR}/fire", exist_ok=True)
os.makedirs(f"{SAVE_DIR}/normal", exist_ok=True)

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

    # 저장용 타임스탬프
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    save_name = f"{beacon_code}_{timestamp}.jpg"

    # 1차 감지
    results_n = model_n.predict(image)

    if detect_fire(results_n):
        print(f"{beacon_code}: 1차 감지 됨")
        # 2차 감지
        results_x = model_x.predict(image)
        if detect_fire(results_x):
            print(f"{beacon_code}: 2차 감지 됨")

            # 화재 이미지 저장
            results_x[0].save(filename=f"{SAVE_DIR}/fire/{save_name}")

            return beacon_code, {
                "filename": filename,
                "content_type": content_type,
                "data": image_data
            }
        
    # 정상 이미지 저장 (1차 결과라도)
    results_n[0].save(filename=f"{SAVE_DIR}/normal/{save_name}")
    
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
    # loop = asyncio.get_event_loop()
    # detect_tasks = [
    #     loop.run_in_executor(executor, run_detection, b_code, img_data, filename, content_type)
    #     for b_code, img_data, filename, content_type in read_results
    # ]

    # detect_results = await asyncio.gather(*detect_tasks)

    # 2. YOLO 감지 비동기 처리(병렬 처리는는 순서가 꼬일 가능성이 있다.)
    detect_results = []
    for beacon_code, img_data, filename, content_type in read_results:
        result = run_detection(beacon_code, img_data, filename, content_type)
        if result:
            detect_results.append(result)

    for result in detect_results:
        if result:
            beacon_code, img_info = result
            fire_images[beacon_code] = img_info
            fire_beacons.append(beacon_code)

    return fire_images, fire_beacons

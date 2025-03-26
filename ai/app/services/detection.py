from ultralytics import YOLO
from PIL import Image
import io

model_n = YOLO("models/n_best.pt")
model_m = YOLO("models/m_best.pt")

async def analyze_images(file_dict: dict, cctv_list: list):
    """
    업로드된 이미지를 분석하여 화재를 탐지하는 함수

    Args:
        file_dict: 파일명을 기준으로 딕셔너리로 변환된 이미지 파일
        cctv_list: [{ beacon_code, rtsp_ip }]
    """

    fire_images = {}   # beacon_code: image 바이트
    fire_beacons = []  # fire로 확정된 beacon_code 리스트

    for beacon in cctv_list:
        beacon_code = beacon["beacon_code"]

        # beacon_code와 일치하는 이미지가 업로드됐는지 확인
        if beacon_code not in file_dict:
            print("일치하는 이미지가 아닌데?")
            continue

        upload_file = file_dict[beacon_code]
        image_data = await upload_file.read()
        image = Image.open(io.BytesIO(image_data)).convert("RGB")

        # 1차 감지 (YOLOv11n)
        results_n = model_n.predict(image)
        if detect_fire(results_n):
            print('1차 감지 됨')
            # 2차 감지 (YOLOv11m)
            results_m = model_m.predict(image)
            if detect_fire(results_m):
                print('2차 감지 됨')
                # 화재 확정
                fire_images[beacon_code] = image_data
                fire_beacons.append(beacon_code)

    return fire_images, fire_beacons



def detect_fire(results) -> bool:
    """
    YOLO 감지 결과에서 'fl' 클래스를 탐지했는지 확인하는 함수

    Args:
        results (List[DetectionResult]): YOLO 감지 결과

    Returns:
        bool: 'fl' 클래스를 탐지했는지 여부
    """
    for result in results:
        names = result.names  # 클래스 ID → 클래스명 매핑
        for box in result.boxes:
            class_id = int(box.cls[0].item())
            label = names[class_id]
            if "fl" in label.lower():
                return True
    return False
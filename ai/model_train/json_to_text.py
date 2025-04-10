# AIHUB 데이터셋을 YOLO 형식으로 변환하는 스크립트입니다.
# YOLO 형식은 클래스 ID, 중심 좌표 (x, y), 너비, 높이로 구성됩니다.
# 이 스크립트는 JSON 파일을 읽고 YOLO 형식으로 변환하여 .txt 파일로 저장합니다.
# YOLO 형식은 다음과 같습니다:
# class_id x_center y_center width height

import os
import json

input_dir = 'aihub_data/Valid/label_original'  # JSON이 있는 폴더
output_dir = 'aihub_data/Valid/label'  # YOLO .txt 저장 위치

os.makedirs(output_dir, exist_ok=True)

# 클래스 매핑 (YOLO는 0부터 시작)
class_mapping = {
    1: 0,  # 'fl' → 0
    2: 1,  # 'sm' → 1
    3: 2   # 'none' → 2
}

for file_name in os.listdir(input_dir):
    if not file_name.endswith('.json'):
        continue

    json_path = os.path.join(input_dir, file_name)
    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    image_info = data['image']
    width = image_info['width']
    height = image_info['height']
    image_filename = os.path.splitext(image_info['filename'])[0]  # 확장자 제거

    annotations = data.get('annotations', [])
    yolo_lines = []

    for ann in annotations:
        original_class_id = ann['categories_id']
        class_id = class_mapping.get(original_class_id, -1)
        if class_id == -1:
            print(f"⚠️ 알 수 없는 클래스 ID: {original_class_id} (파일: {file_name})")
            continue

        x_min, y_min, box_width, box_height = ann['bbox']

        # 중심 좌표 계산 및 정규화
        x_center = (x_min + box_width / 2) / width
        y_center = (y_min + box_height / 2) / height
        norm_width = box_width / width
        norm_height = box_height / height

        yolo_lines.append(f"{class_id} {x_center:.6f} {y_center:.6f} {norm_width:.6f} {norm_height:.6f}")

    # YOLO 형식 라벨 파일 저장
    output_path = os.path.join(output_dir, image_filename + '.txt')
    with open(output_path, 'w') as out_f:
        out_f.write('\n'.join(yolo_lines))

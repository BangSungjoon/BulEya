# 화재 감지 AI 분석 백엔드
## 폴더 구조
```
ai
├── models
│   ├── n_best.pt    # YOLOv11 nano 모델 (아주 가볍고 빠름)
|   ├── m_best.pt    # YOLOv11 medium 모델 (성능과 속도 절충)
│   └── x_best.pt    # YOLOv11 extra-large 모델 (가장 크고, 가장 정확하지만 느림)
|
├── model_train
│   ├── data.yaml    # 모델 학습과 검증에 필요한 데이터와 클래스 지정 파일
│   ├── json_to_text.py     # AIHUB 데이터셋을 YOLO 형식으로 변환하는 스크립트
│   ├── n_model_train.ipynb     # YOLOv11n 경량형 모델을 학습, 성능 평가
│   └── x_m_model_train.ipynb    # YOLOv11m, YOLOv11x 모델을 학습, 성능 평가 
│
├── app
│   ├── api
│   │   └── v1
│   │       └── cctv.py        # CCTV 엔드포인트 관리
│   │
│   ├── core
│   │   └── redis.py           # redis 클라이언트
│   │
│   ├── schemas
│   │   └── cctv.py            # 입력 및 출력 Pydantic 스키마
│   │
│   ├── services
|   │   ├── fire_report.py     # Spring 서버로 전달 + Redis 로직
│   │   └── detection.py       # YOLO 모델 로딩 및 감지 로직 관리
│   │
│   └── main.py                # FastAPI 메인 실행파일
│
├── Dockerfile                 # Docker 배포용
├── requirements.txt           # 의존성 관리
└── .env                       # 환경변수 관리 (API 엔드포인트 등)
```
## 🔥 딥러닝 모델 파일 다운로드 안내
본 프로젝트는 대용량 모델 파일이 GitHub 저장소 용량 제한으로 인해 포함되어 있지 않습니다. <br>
아래의 방법을 따라 모델 파일을 다운로드하고 프로젝트 폴더에 넣어주세요.
### 1. Google Drive에서 모델 파일 다운로드
[대형 모델 YOLOv11x](https://drive.google.com/file/d/1gvq4KXg1KGtwxt3rvuAyfUiP0-JQhvbS/view?usp=drive_link)<br>
[중형 모델 YOLOv11m](https://drive.google.com/file/d/1_ixAtJfFetxK05pleDWCpMyQyf-63tp5/view?usp=drive_link)
### 2. 다운로드한 파일을 아래 경로에 복사하세요
```
ai/models/
```
예시:
- ai/models/m_best.pt
- ai/models/x_best.pt
### 3. 프로젝트 실행
모델 파일을 올바른 위치에 넣은 후, 프로젝트를 실행하시면 정상적으로 동작합니다.

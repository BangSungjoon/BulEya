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
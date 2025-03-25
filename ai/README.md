# 화재 감지 AI 분석 백엔드
## 폴더 구조
```
cctv_fire_detection
├── models
│   ├── n_best.pt    # YOLOv11 nano 모델 (경량)
│   └── best.pt    # YOLOv11 medium 모델 (정확성 높음)
│
├── app
│   ├── api
│   │   └── v1
│   │       └── cctv.py        # CCTV 엔드포인트 관리
│   │
│   ├── core
│   │   ├── config.py          # 환경 변수 및 설정
│   │   └── utils.py           # 공통 유틸리티
│   │
│   ├── schemas
│   │   └── cctv.py            # 입력 및 출력 Pydantic 스키마
│   │
│   ├── services
│   │   └── detection.py       # YOLO 모델 로딩 및 감지 로직 관리
│   │
│   └── main.py                # FastAPI 메인 실행파일
│
├── Dockerfile                 # Docker 배포용 (필요시)
├── requirements.txt           # 의존성 관리
└── .env                       # 환경변수 관리 (API 엔드포인트 등)
```
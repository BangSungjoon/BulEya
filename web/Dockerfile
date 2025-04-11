# -------- 1단계: Node 환경에서 Vite 빌드 --------
    FROM node:23-alpine AS builder

    # 작업 디렉토리
    WORKDIR /app
    
    # 의존성 설치
    COPY package*.json ./
    RUN npm install
    
    # 프로젝트 소스 복사
    COPY . .
    
    # Vite 빌드 (dist 폴더 생성)
    RUN npm run build
    
    # -------- 2단계: Nginx 이미지로 정적 파일 서빙 --------
    FROM nginx:alpine
    
    # 빌드된 파일 복사
    COPY --from=builder /app/dist /usr/share/nginx/html
    
    # (선택) nginx 설정 복사 (SPA 라우팅 등)
    COPY ./nginx.conf /etc/nginx/conf.d/default.conf
    
    # 80 포트 오픈
    EXPOSE 80
    
    # nginx 실행
    CMD ["nginx", "-g", "daemon off;"]
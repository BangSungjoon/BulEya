const express = require('express');
const routes = require('./routes/index');
const frameService = require('./services/frameService');
const streamService = require('./services/streamService');
const apiService = require('./services/apiService');
const { initializeDb } = require('./config/database');
require('dotenv').config();
const cors = require('cors');

const app = express();
app.use(cors({
  origin: 'http://localhost:5173', // 프론트엔드 출처(배포된 주소로 변경)
  methods: ['GET', 'POST'],       // 허용할 HTTP 메서드
  allowedHeaders: ['Content-Type'] // 허용할 헤더
}));
app.use(express.json());
app.use('/api', routes);
// CORS 설정: 프론트 출처 허용

(async () => {
  await initializeDb(); // DB 초기화
  const stationId = process.env.STATION_ID;
  const cctvData = await apiService.fetchCctvData(stationId); // CCTV 정보 가져오기
  console.log('CCTV 데이터:', cctvData);
  const cctvList = cctvData.result
  console.log('CCTV 리스트:', cctvList);

  frameService.initializeFrameExtraction(cctvList);
  await frameService.processFrames(stationId, cctvList);
  await streamService.startStreams(stationId, cctvList);
})();

const PORT = process.env.PORT;
app.listen(PORT, () => console.log(`서버 시작, 포트 번호: ${PORT}`));

process.on('SIGINT', () => {
  frameService.stopFrameExtraction();
  process.exit();
});
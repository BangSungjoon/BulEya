const express = require('express');
const routes = require('./routes/index');
const apiService = require('./services/apiService');
const db = require('./config/database');

const Stream = require('node-rtsp-stream');
const { spawn } = require('child_process');
const axios = require('axios');
require('dotenv').config();

const app = express();
app.use(express.json()); // JSON 파싱
app.use('/api', routes); // 클라이언트 요청 라우팅

// 사용 중인 포트 추적 (중복 방지)
const usedPorts = new Set();
const BASE_WS_PORT = 9999;

// 사용 가능한 포트 찾기
const getAvailablePort = () => {
  let port = BASE_WS_PORT;
  while (usedPorts.has(port)) {
    port++;
  }
  usedPorts.add(port);
  return port;
};

// 프레임 단위 이미지 추출
const extractFrame = (rtspUrl) => {
  const ffmpeg = spawn('ffmpeg', [
    '-rtsp_transport', 'tcp',
    '-i', rtspUrl,
    '-vf', 'fps=1', // 초당 1프레임 추출
    '-f', 'mjpeg', // image2 사용
    '-c:v', 'mjpeg', // mjpeg 코덱 사용
    '-b:v', '5000k', // 비트레이트 증가 (예: 5000kb/s)
    '-q:v', '2', // 화질 설정(2~31, 낮을수록 고품질질)
    'pipe:1', // jpeg 데이터를 파일에 저장하지 않고 stdout으로 출력
  ]);

  ffmpeg.stdout.on('data', (chunk) => {
    console.log(`프레임 데이터 수신 (크기: ${chunk.length})`);
    sendFrame(chunk);
  });

  ffmpeg.stderr.on('data', (data) => {
    console.error(`FFmpeg 데이터: ${data.toString()}`);
  });

  ffmpeg.on('close', (code) => {
    console.log(`FFmpeg 종료 (코드: ${code})`);
  });
};

// 프레임 POST 요청
const sendFrame = async (jpegBuffer) => {
  try {
    const response = await axios.post(process.env.FASTAPI_API_URL, jpegBuffer, {
      headers: { 'Content-Type': 'image/jpeg' },
    });
    console.log('전송 성공:', response.status);
  } catch (error) {
    console.error('전송 에러 ', error.message);
  }
};

// cctv 데이터 요청 -> rtsp url로 웹 소켓 서버 생성, 이미지 보내기
const startStream = async () => {
  console.log('스트림 시작');
  const stationId = process.env.STATION_ID;
  try {
    // // 1. spring에서 cctv 데이터 요청
    // const cctvData = await apiService.fetchCctvInfo(stationId);
    // console.log('cctv 데이터', cctvData);

    const cctvData = {
      cctvList: [
        {
          "beacon_code": 1,
          "rtsp_url": "rtsp://70.12.247.93:554/live"
        }
      ]
    }

    // 2. 비콘 코드 리스트 추출
    const cctvList = cctvData.cctvList;
    if (!Array.isArray(cctvList) || cctvList.length === 0) {
      throw new Error('rtsp url이 존재하지 않습니다.');
    }

    // 3. rtsp url로 웹 소켓 서버 생성
    for (const cctv of cctvList) {
      const rtspUrl = cctv.rtsp_url;
      const beaconCode = cctv.beacon_code;
      const wsPort = getAvailablePort();
      console.log('rtsp 주소:', rtspUrl, 'on port:', wsPort);

      let stream = new Stream({
        name: `rtsp_stream_${stationId}_${wsPort}`,
        streamUrl: rtspUrl,
        wsPort: wsPort,
      });

      // 웹소켓 연결 로그
      stream.wsServer.on('connection', (socket) => {
        console.log(`웹소켓 연결됨: ws://localhost:${wsPort}`);
      });
      stream.on('error', (err) => {
        console.error(`스트리밍 오류: ${err.message}`);
        console.log('스트림 종료, 재시작 시도');
        setTimeout(() => startStream(), 3000); // 3초 후 재시작
      });


      // 4. 웹소캣 주소 db에 저장
      const wsUrl = `ws://localhost:${wsPort}`;
      await db.storeCctvData(stationId, beaconCode, wsUrl);
      console.log('웹소켓 주소', wsUrl);

      // 5. RTSP 프레임 추출 및 post 요청
      extractFrame(rtspUrl);
    }
  } catch (error) {
    console.error('fetch 실패');
  }
}

// 서버 시작 시 cctv 데이터 요청
startStream();


const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
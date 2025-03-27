const express = require('express');
const routes = require('./routes/index');
const apiService = require('./services/apiService');
const db = require('./config/database');
const FormData = require('form-data');

const Stream = require('node-rtsp-stream');
const { spawn } = require('child_process');
const axios = require('axios');
require('dotenv').config();

const app = express();
app.use(express.json()); // JSON 파싱
app.use('/api', routes); // 클라이언트 요청 라우팅

// // spring에서 cctv 데이터 요청
// const cctvData = await apiService.fetchCctvInfo(stationId);
// console.log('cctv 데이터', cctvData);

const cctvData = {
  cctvList: [
    {
      "beacon_code": 1,
      "rtsp_url": "rtsp://70.12.247.93:554/live"
    },
    {
      "beacon_code": 2,
      "rtsp_url": "rtsp://70.12.247.93:554/live"
    }
  ]
}

// cctv 리스트 추출
const cctvList = cctvData.cctvList;


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

// 프레임 데이터를 저장할 배열
let files = [];

// 프레임 단위 이미지 추출
const extractFrame = (rtspUrl, beaconCode) => {
  const ffmpeg = spawn('ffmpeg', [
    '-rtsp_transport', 'tcp',
    '-i', rtspUrl,
    '-vf', 'fps=1', // 초당 1프레임 추출
    '-f', 'mjpeg', // image2 사용
    '-c:v', 'mjpeg', // mjpeg 코덱 사용
    '-q:v', '2', // 화질 설정(2~31, 낮을수록 고품질)
    'pipe:1', // jpeg 데이터를 파일에 저장하지 않고 stdout으로 출력
  ]);

  ffmpeg.stdout.on('data', (chunk) => {
    console.log(`프레임 데이터 수신 (크기: ${chunk.length})`);
    form.append('files', chunk, {
      filename: `${beaconCode}.jpg`,
      contentType: 'image/jpeg',
    });
  });

  ffmpeg.stderr.on('data', (data) => {
    console.error(`FFmpeg 데이터: ${data.toString()}`);
  });

  ffmpeg.on('close', (code) => {
    console.log(`FFmpeg 종료 (코드: ${code})`);
  });

  return ffmpeg;
};

// 프레임 POST 요청
const sendFrames = async () => {
  const fastapi_url = "http://127.0.0.1:8000";
  const form = new FormData();

  try {
    form.append('station_id', 1);
    form.append('cctv_list', JSON.stringify());
    form.append('files', jpegBuffer, {
      filename: '1.jpg',
      contentType: 'image/jpeg',
    });

    const response = await axios.post(`${fastapi_url}/ai/cctv-frame`, form, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
    });
    console.log('전송 성공:', response.status);
  } catch (error) {
    console.error('전송 에러 ', error.message, error.response?.data || '');
  }
};

// cctv 데이터 요청 -> rtsp url로 웹 소켓 서버 생성, 이미지 보내기
const startStream = async () => {
  console.log('스트림 시작');
  try {
    // 각 RTSP URL에서 프레임 추출 시작
    for (const cctv of cctvList) {
      const rtspUrl = cctv.rtsp_url;
      const beaconCode = cctv.beacon_code;

      // 프레임 추출 시작
      extractFrame(rtspUrl, beaconCode);
    }

    // 1초마다 프레임 전송
    setInterval(() => {
      sendFrames();
    }, 1000);

    // // 1. rtsp url로 웹 소켓 서버 생성
    // for (const cctv of cctvList) {
    //   const rtspUrl = cctv.rtsp_url;
    //   const beaconCode = cctv.beacon_code;
    //   const wsPort = getAvailablePort();
    //   console.log('rtsp 주소:', rtspUrl, 'on port:', wsPort);

      // let stream = new Stream({
      //   name: `rtsp_stream_${stationId}_${wsPort}`,
      //   streamUrl: rtspUrl,
      //   wsPort: wsPort,
      //   // ffmpegOptions: {
      //   //   '-vf': 'fps=1',
      //   //   '-f': 'mjpeg',
      //   //   '-c:v': 'mjpeg',
      //   //   '-b:v': '5000k',
      //   //   '-q:v': '2',
      //   // },
      // });

      // // 웹소켓 연결 로그
      // stream.wsServer.on('connection', (socket) => {
      //   console.log(`웹소켓 연결됨: ws://localhost:${wsPort}`);
      // });
      // // stream.on('data', (chunk) => {
      // //   console.log(`프레임 데이터 수신 (크기: ${chunk.length})`);
      // //   sendFrame(chunk);
      // // });
      // stream.on('error', (err) => {
      //   console.error(`스트리밍 오류: ${err.message}`);
      //   console.log('스트림 종료, 재시작 시도');
      //   setTimeout(() => startStream(), 3000); // 3초 후 재시작
      // });

      // // 4. 웹소캣 주소 db에 저장
      // const wsUrl = `ws://localhost:${wsPort}`;
      // await db.storeCctvData(stationId, beaconCode, wsUrl);
      // console.log('db에 저장할 웹소캣 주소', wsUrl);
    // }
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
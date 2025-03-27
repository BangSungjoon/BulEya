const { spawn } = require('child_process');
const Stream = require('node-rtsp-stream');
const streamUrl = "rtsp://70.12.247.156/live"

const express = require('express');
const app = express();
const port = 5000;

const axios = require('axios');


// 스트림
const stream = new Stream({
    name: 'rtsp_stream',
    streamUrl: streamUrl,
    wsPort: 9999,
});

// // ffmpeg를 사용하여 RTSP 스트림을 매초 1프레임으로 추출, 이미지 데이터를 pipe로 출력합니다.
// const ffmpeg = spawn('ffmpeg', [
//   '-rtsp_transport', 'tcp',
//   '-i', streamUrl,
//   '-vf', 'fps=1',
//   '-f', 'image2pipe',
//   '-vcodec', 'mjpeg',
//   'pipe:1'
// ]);

// let frameBuffer = Buffer.alloc(0);
// ffmpeg.stdout.on('data', chunk => {
//   frameBuffer = Buffer.concat([frameBuffer, chunk]);
  
//   // JPEG 시작과 끝 바이트(FF D8, FF D9)를 찾아서 하나의 이미지 프레임을 추출합니다.
//   let start = frameBuffer.indexOf(Buffer.from([0xFF, 0xD8]));
//   let end = frameBuffer.indexOf(Buffer.from([0xFF, 0xD9]));
  
//   while (start !== -1 && end !== -1 && end > start) {
//     // Buffer.from을 사용하여 subarray 결과를 복제합니다.
//     const jpegBuffer = Buffer.from(frameBuffer.subarray(start, end + 2));
//     sendFrame(jpegBuffer); // 추출된 프레임을 API 엔드포인트로 전송
//     frameBuffer = frameBuffer.subarray(end + 2);
    
//     start = frameBuffer.indexOf(Buffer.from([0xFF, 0xD8]));
//     end = frameBuffer.indexOf(Buffer.from([0xFF, 0xD9]));
//   }
// });

// ffmpeg를 사용하여 RTSP 스트림에서 매초마다 프레임 하나씩 캡처하여 frames 폴더에 저장합니다.
const ffmpeg = spawn('ffmpeg', [
    '-rtsp_transport', 'tcp',
    '-i', streamUrl,
    '-vf', 'fps=1',       // 초당 1프레임 추출
    'C:/DEV/cctv_stream/cctv_stream/frames/frame%03d.jpg'  // 프레임 저장 경로
]);

ffmpeg.stderr.on('data', data => {
    console.error(`ffmpeg stderr: ${data}`);
});

ffmpeg.on('close', code => {
    console.log(`ffmpeg process exited with code ${code}`);
});



// // API 엔드포인트로 이미지 전송 (헤더 수정 필요)
// function sendFrame(jpegBuffer) {
//   axios.post('http://localhost:5000/api/image', jpegBuffer, {
//     headers: {
//       'Content-Type': 'image/jpeg'
//     }
//   })
//   .then(response => {
//     console.log('Frame sent successfully:', response.status);
//   })
//   .catch(error => {
//     console.error('Error sending frame:', error.message);
//   });
// }
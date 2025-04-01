const ffmpeg = require('fluent-ffmpeg');
const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { Blob } = require('node:buffer');

// 파일 저장 경로: src/frames -> 없으면 생성
const FRAME_DIR = path.join(__dirname, '../frames');
if (!fs.existsSync(FRAME_DIR)) fs.mkdirSync(FRAME_DIR);

// 프레임 추출 목록
const frameExtractors = new Map();

// 프레임 단위 추출 시작
const startFrameExtraction = (rtspUrl, beaconCode) => {
  const outputFile = path.join(FRAME_DIR, `cctv_${beaconCode}_latest.jpg`);
  const proc = ffmpeg(rtspUrl)
    .inputOptions([
      '-rtsp_transport', 'tcp',
      '-probesize', '10000000',
      '-analyzeduration', '10000000',
      '-fflags', '+nobuffer',
      '-err_detect', 'ignore_err',
    ])
    .videoFilter('fps=1,scale=-1:-1')
    .outputOptions([
      '-q:v', '1',
      '-qmin', '1',
      '-qmax', '1',
      '-update', '1',
      '-compression_level', '0',
      '-preset', 'slow',
      '-vsync', '1',
      '-pix_fmt', 'yuvj444p',
    ])
    .outputFormat('image2')
    .output(outputFile)
    .on('start', () => console.log(`프레임 추출 시작: CCTV ${beaconCode} -> ${outputFile}`))
    .on('error', (err) => {
      console.error(`프레임 추출 오류 (CCTV ${beaconCode}): ${err.message}`);
      frameExtractors.delete(beaconCode);                                // 오류 발생 시 목록에서 제거
      setTimeout(() => startFrameExtraction(rtspUrl, beaconCode), 1000); // 1초 후 재시작
    })
    .on('end', () => {
      console.log(`프레임 추출 종료 (CCTV ${beaconCode})`);
      frameExtractors.delete(beaconCode);                                // 종료 시 목록에서 제거
      setTimeout(() => startFrameExtraction(rtspUrl, beaconCode), 1000); // 1초 후 재시작
    })
    .on('stderr', (data) => console.error(`FFmpeg 로그 (CCTV ${beaconCode}): ${data}`));

  proc.run();
  frameExtractors.set(beaconCode, proc); // 추출했으면 목록에 추가
};

const waitForFile = (filePath, timeout = 20000) => {
  return new Promise((resolve, reject) => {
    const startTime = Date.now();
    const checkInterval = setInterval(() => {
      if (fs.existsSync(filePath) && fs.statSync(filePath).size > 0) {
        clearInterval(checkInterval);
        resolve();
      } else if (Date.now() - startTime > timeout) {
        clearInterval(checkInterval);
        reject(new Error(`파일 생성 타임아웃: ${filePath}`));
      }
    }, 500);
  });
};

// 프레임 전송
const sendFrames = async (stationId, cctvList) => {
  const fast_api_url = process.env.FAST_API_URL;
  const form = new global.FormData();

  form.append('station_id', stationId.toString());
  const cctvListStringified = cctvList.map(cctv => ({
    ...cctv,
    beacon_code: cctv.beacon_code.toString(),
  }));
  form.append('cctv_list', JSON.stringify(cctvListStringified));

  const filePromises = cctvList.map(async ({ beacon_code }) => {
    const filePath = path.join(FRAME_DIR, `cctv_${beacon_code}_latest.jpg`);
    try {
      await waitForFile(filePath);
      const frameBuffer = fs.readFileSync(filePath);
      const frameBlob = new Blob([frameBuffer], { type: 'image/jpeg' });
      console.log(`프레임 추가: CCTV ${beacon_code}, 크기=${frameBuffer.length}`);
      form.append('files', frameBlob, `${beacon_code}.jpg`);
    } catch (error) {
      console.warn(`프레임 파일 대기 실패: CCTV ${beacon_code} - ${error.message}`);
    }
  });

  await Promise.all(filePromises);
  
  try {
    const response = await axios.post(`${fast_api_url}/ai/cctv-frame`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    console.log('전송 성공:', response.status);
  } catch (error) {
    console.error('전송 에러:', error.message, error.response?.data || '');
  }
};

const initializeFrameExtraction = (cctvList) => {
  cctvList.forEach(({ rtsp_url, beacon_code }) => startFrameExtraction(rtsp_url, beacon_code));
};

const processFrames = async (stationId, cctvList) => {
  console.log('프레임 처리 시작');
  await sendFrames(stationId, cctvList);
  setTimeout(() => processFrames(stationId, cctvList), 1000);
};

const stopFrameExtraction = () => {
  frameExtractors.forEach((proc, beaconCode) => {
    proc.kill();
    console.log(`프레임 추출 종료: CCTV ${beaconCode}`);
  });
};

module.exports = {
  initializeFrameExtraction,
  processFrames,
  stopFrameExtraction,
};
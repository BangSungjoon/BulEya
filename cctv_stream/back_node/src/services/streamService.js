const Stream = require('node-rtsp-stream');
const { storeCctvData } = require('../config/database');
const os = require('os');

const usedPorts = new Set();
const BASE_WS_PORT = 9999;
const activeStreams = new Map();

const getAvailablePort = () => {
  let port = BASE_WS_PORT;
  while (usedPorts.has(port)) port++;
  usedPorts.add(port);
  return port;
};

const startStreams = async (stationId, cctvList) => {
  await Promise.all(
    cctvList.map(async (cctv) => {
      const { rtsp_url, beacon_code } = cctv;
      const wsPort = getAvailablePort();

      if (activeStreams.has(beacon_code)) {
        console.log(`이미 실행 중: CCTV ${beacon_code}`);
        return;
      }

      const stream = new Stream({
        name: `rtsp_stream_${stationId}_${wsPort}`,
        streamUrl: rtsp_url,
        wsPort: wsPort,
      });
      
      //로컬 ip 주소
      const ipAddress = Object.values(os.networkInterfaces()).flat().find(iface => iface.family === 'IPv4' && !iface.internal)?.address;
      // const ipAddress = 'localhost'; // 로컬에서 테스트할 때는 localhost 사용

      stream.wsServer.on('connection', () => {
        console.log(`웹 소켓 연결됨: ws://${ipAddress}:${wsPort}`);
      });

      stream.on('error', (err) => {
        console.error(`스트리밍 오류 (CCTV ${beacon_code}): ${err.message}`);
        activeStreams.delete(beacon_code);
        usedPorts.delete(wsPort);
      });

      activeStreams.set(beacon_code, stream);

      try {
        const wsUrl = `ws://${ipAddress}:${wsPort}`;
        await storeCctvData(stationId, beacon_code, wsUrl);
        console.log(`DB에 저장된 웹소켓 주소: ${wsUrl}`);
      } catch (err) {
        console.error(`DB 저장 실패 (CCTV ${beacon_code}): ${err.message}`);
      }
    })
  );
};

module.exports = { startStreams };
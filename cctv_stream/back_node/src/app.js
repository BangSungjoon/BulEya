const express = require('express');
const routes = require('./routes/index');
const frameService = require('./services/frameService');
const streamService = require('./services/streamService');
const apiService = require('./services/apiService');
require('dotenv').config();

const app = express();
app.use(express.json());
app.use('/api', routes);

(async () => {
  const stationId = process.env.STATION_ID;
  // Spring에서 CCTV 데이터 요청
  // const cctvData = await apiService.fetchCctvInfo(stationId);
  // console.log('cctv 데이터', cctvData);

  const cctvData = {
    cctvList: [
      { beacon_code: 1, rtsp_url: "rtsp://70.12.247.93:554/live" },
      // { beacon_code: 2, rtsp_url: "rtsp://70.12.247.156:554/live" },
    ],
  };
  
  const { cctvList } = cctvData;

  frameService.initializeFrameExtraction(cctvList);
  await frameService.processFrames(stationId, cctvList);
  await streamService.startStreams(stationId, cctvList);
})();

const PORT = process.env.PORT;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

process.on('SIGINT', () => {
  frameService.stopFrameExtraction();
  process.exit();
});
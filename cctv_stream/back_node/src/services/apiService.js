const axios = require('axios');
const db = require('../config/database');
require('dotenv').config();

const fetchCctvData = async (station_id) => {
  try {
    const response = await axios.get(`${process.env.SPRING_API_URL}/api/cctv-info?station_id=${station_id}`);
    return response.data;
  } catch (error) {
    throw new Error(`CCTV 데이터 가져오기 오류: ${error.message}`);
  }
}

const getCctvData = async (station_id, beacon_code) => {
  try {
    const cctvData = await db.getCctvData(station_id, beacon_code);
    console.log('CCTV 데이터:', cctvData);
    if (!cctvData) {
      throw new Error('CCTV 데이터가 없습니다.');
    }
    return cctvData;
  } catch (err) {
    throw new Error(`CCTV 데이터 가져오기 오류: ${err.message}`);
  }
}

module.exports = { fetchCctvData, getCctvData };
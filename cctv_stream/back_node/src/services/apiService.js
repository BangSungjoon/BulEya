const axios = require('axios');
require('dotenv').config();

const fetchCctvInfo = async (station_id) => {
  const response = await axios.get(`${process.env.SPRING_API_URL}/api/cctv-info?station_id=${station_id}`);
  return response;
};

module.exports = { fetchCctvInfo };
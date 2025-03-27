const axios = require('axios');
require('dotenv').config();

const fetchCctvInfo = async (station_id) => {
  const url = `${process.env.SPRING_API_URL}/api/cctv-info?station_id=${station_id}`;
  console.log('url', url);
  const response = await axios.get(url);
  return response.data;
};

module.exports = { fetchCctvInfo };
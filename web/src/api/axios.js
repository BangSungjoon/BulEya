import axios from 'axios'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 지도 이미지 URL 받아오는 API
export const fetchMapImage = async (stationId) => {
  return instance.get('/api/map/admin', {
    params: {
      station_id: stationId,
    },
  })
}

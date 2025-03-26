import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://localhost:8080', // 추후 배포 주소로 변경 필요
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

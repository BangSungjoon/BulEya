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

// 장비 정보 등록하는 API
export const createFacility = async (data) => {
  return instance.post('/api/beacon', data)
}

// 장비 정보 삭제하는 API
export const deleteBeacon = async (beacon_id) => {
  return instance.delete('/api/beacon', {
    data: { beacon_id },
  })
}

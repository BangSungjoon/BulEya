import axios from 'axios'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
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

// 간선 등록 API
export const createEdge = async (data) => {
  return instance.post('/api/edge', data)
}

// 간선 삭제 API
export const deleteEdge = async (data) => {
  return instance.delete('/api/edge', { data })
}

// 장비 정보 삭제하는 API
export const deleteBeacon = async (beacon_id) => {
  return instance.delete('/api/beacon', {
    data: { beacon_id },
  })
}

// 로그인 API
export const logIn = async (stationId, accessKey) => {
  return instance.post('api/station/admin-login', {
    station_id: stationId,
    access_key: accessKey,
  })
}

// 로그아웃 API
export const logOut = async () => {
  return instance.get('api/station/admin-logout')
}

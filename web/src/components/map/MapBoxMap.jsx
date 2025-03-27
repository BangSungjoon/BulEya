import { useRef, useEffect } from 'react'
import mapboxgl from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'

// MapBox access 토큰
mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_ACCESS_TOKEN

const MapBoxMap = ({ mode, mapImageUrl }) => {
  console.log('[🗺️ 지도 url]', mapImageUrl)

  // 지도 컨테이너 요소를 참조할 ref
  const mapContainer = useRef(null)
  // Mapbox의 Map 객체를 저장할 ref (재렌더링 방지)
  const mapRef = useRef(null)

  // 이미지 사이즈 (픽셀)
  const imageWidth = 5000
  const imageHeight = 7800

  // 이미지 비율 (가로 / 세로)
  const imageAspectRatio = imageWidth / imageHeight // 약 0.641

  // 지도 좌표계로 사용할 가상의 위도/경도 범위 설정
  const coordinateHeight = 60 // 전체 세로 범위를 60도로 가정
  const coordinateWidth = coordinateHeight * imageAspectRatio // 비율 유지해서 가로 범위 계산

  // 가상의 지도 좌표 경계
  const top = coordinateHeight / 2 // +30
  const bottom = -coordinateHeight / 2 // -30
  const left = -coordinateWidth / 2 // -19.25
  const right = coordinateWidth / 2 // +19.25

  // 픽셀 좌표를 가상의 위경도 좌표로 변환하는 함수
  const convertPixelToLngLat = (x, y, width, height) => {
    const lng = left + (x / width) * (right - left)
    const lat = top - (y / height) * (top - bottom) // Y축은 반대로 내려가므로 빼줌
    return [lng, lat]
  }

  // 1. 최초 지도 객체 생성
  useEffect(() => {
    // 이미지가 없거나 이미 생성되었으면 무시
    if (!mapImageUrl || mapRef.current) return

    // Map 객체 생성
    mapRef.current = new mapboxgl.Map({
      container: mapContainer.current,
      style: {
        version: 8,
        sources: {}, // 초기에는 source, layer 없이 시작
        layers: [],
      },
      center: convertPixelToLngLat(imageWidth / 2, imageHeight / 2, imageWidth, imageHeight), // 중앙 위치
      zoom: 0.8,
      pitch: 0, // 위에서 수직으로 보기
      bearing: 0, // 회전 없음
      interactive: true,
      dragRotate: true,
      renderWorldCopies: false, // 지도 반복 방지
    })
    console.log('[🗺️ 지도 최초 생성]')
  }, [])

  // 2. mapImageUrl이 바뀔 때마다 지도에 이미지 갱신
  useEffect(() => {
    const map = mapRef.current
    if (!map || !mapImageUrl) return

    // 이미지 네 귀퉁이를 위경도로 변환해 imageBounds로 정의
    const imageBounds = [
      convertPixelToLngLat(0, 0, imageWidth, imageHeight), // top-left
      convertPixelToLngLat(imageWidth, 0, imageWidth, imageHeight), // top-right
      convertPixelToLngLat(imageWidth, imageHeight, imageWidth, imageHeight), // bottom-right
      convertPixelToLngLat(0, imageHeight, imageWidth, imageHeight), // bottom-left
    ]

    // 지도에 소스와 레이어를 추가하는 함수 정의
    const updateImageLayer = () => {
      // 기존 레이어/소스가 있다면 제거
      if (map.getLayer('custom-map-layer')) {
        map.removeLayer('custom-map-layer')
      }
      if (map.getSource('custom-map')) {
        map.removeSource('custom-map')
      }

      // 새로운 이미지 소스 추가
      map.addSource('custom-map', {
        type: 'image',
        url: mapImageUrl,
        coordinates: imageBounds, // 위경도 좌표에 맞춰서 이미지 붙이기
      })

      // 새로운 이미지 레이어 추가
      map.addLayer({
        id: 'custom-map-layer',
        type: 'raster',
        source: 'custom-map',
        paint: {
          'raster-opacity': 1,
        },
      })

      // 지도 중심 재설정 (안 해도 되지만 UX상 편리함)
      map.setCenter(convertPixelToLngLat(imageWidth / 2, imageHeight / 2, imageWidth, imageHeight))
      map.setZoom(0.8)
    }

    // 스타일이 이미 로드되었다면 바로 추가
    if (map.isStyleLoaded()) {
      updateImageLayer()
    } else {
      // 스타일이 아직 안 끝났으면 이벤트 대기
      map.once('styledata', updateImageLayer)
    }
  }, [mapImageUrl])

  return <div ref={mapContainer} className="h-full w-full" />
}

export default MapBoxMap

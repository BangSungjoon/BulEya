import { useRef, useEffect } from 'react'
import mapboxgl from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'

// MapBox access 토큰
mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_ACCESS_TOKEN

const MapBoxMap = ({ mode, mapImageUrl }) => {
  // mapContainer : 지도를 렌더링할 div 요소의 참조
  const mapContainer = useRef(null)
  // mapRef : map 객체 자체를 보관할 ref (재렌더링 방지용)
  const mapRef = useRef(null)

  // 이미지 사이즈 (픽셀)
  const imageWidth = 5000
  const imageHeight = 7800
  const imageAspectRatio = imageWidth / imageHeight // 약 0.641

  // Mapbox에서 사용할 위경도 좌표 범위
  const coordinateHeight = 60 // 기존보다 줄임
  const coordinateWidth = coordinateHeight * imageAspectRatio // ≈ 38.5

  const top = coordinateHeight / 2 // +30
  const bottom = -coordinateHeight / 2 // -30
  const left = -coordinateWidth / 2 // -19.25
  const right = coordinateWidth / 2 // +19.25

  // 픽셀 좌표 → 위경도 변환 함수
  const convertPixelToLngLat = (x, y, width, height) => {
    const lng = left + (x / width) * (right - left)
    const lat = top - (y / height) * (top - bottom)
    return [lng, lat]
  }

  useEffect(() => {
    // 아직 이미지 URL이 정의되지 않았다면 실행하지 않음
    // 이미 초기화 된 경우 다시 실행하지 않음
    if (!mapImageUrl || mapRef.current) return

    // 1. MapBox 지도 생성
    mapRef.current = new mapboxgl.Map({
      container: mapContainer.current,
      style: {
        version: 8,
        sources: {},
        layers: [],
      },
      center: convertPixelToLngLat(imageWidth / 2, imageHeight / 2, imageWidth, imageHeight),
      zoom: 0.8,
      pitch: 0, // 위에서 수직으로 보기
      bearing: 0, // 회전 없음
      interactive: true,
      dragRotate: true, // 회전 비활성화
      renderWorldCopies: false, // 지구 반복 비활성화!
    })

    // 2. 로드 후 이미지 소스와 레이어 추가
    mapRef.current.on('load', () => {
      // 이미지 좌표계
      const imageBounds = [
        convertPixelToLngLat(0, 0, imageWidth, imageHeight), // top-left
        convertPixelToLngLat(imageWidth, 0, imageWidth, imageHeight), // top-right
        convertPixelToLngLat(imageWidth, imageHeight, imageWidth, imageHeight), //  bottom-right
        convertPixelToLngLat(0, imageHeight, imageWidth, imageHeight), //  bottom-left
      ]

      mapRef.current.addSource('custom-map', {
        type: 'image',
        url: mapImageUrl, // 서버에서 받아온 이미지 URL
        coordinates: imageBounds,
      })

      mapRef.current.addLayer({
        id: 'custom-map-layer',
        type: 'raster',
        source: 'custom-map',
        paint: {
          'raster-opacity': 1,
        },
      })
      // ✅ 테스트용 마커 추가
      const markerPos = convertPixelToLngLat(2500, 3900, imageWidth, imageHeight)
      new mapboxgl.Marker().setLngLat(markerPos).addTo(mapRef.current)

      console.log('여기1', mapRef.current.getStyle().sources)
      console.log('여기2', mapRef.current.getStyle().layers)
      console.log('변환된 좌표들:', imageBounds)
    })
  }, [mapImageUrl]) // mapImageUrl이 변경되면 다시 실행

  return <div ref={mapContainer} className="h-full w-full" />
}

export default MapBoxMap

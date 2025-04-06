import { useRef, useEffect, useState } from 'react'
import mapboxgl from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'
import ReactDOM from 'react-dom/client'

// 아이콘
import Beacon from '@/assets/icons/Beacon.svg?react'
import CCTV from '@/assets/icons/CCTV.svg?react'
import Exit from '@/assets/icons/Exit.svg?react'
import Delete from '@/assets/icons/Delete.svg?react'

// MapBox access 토큰
mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_ACCESS_TOKEN

const MapBoxMap = ({
  mode,
  mapImageUrl,
  beaconList = [],
  edgeList = [],
  selectedIcon,
  onMapClick,
  onMarkerClick,
  onDeleteEdge,
  tempMarker,
}) => {
  // 지도 컨테이너 요소를 참조할 ref
  const mapContainer = useRef(null)
  // Mapbox의 Map 객체를 저장할 ref (재렌더링 방지)
  const mapRef = useRef(null)
  // 기존 마커들을 지우기 위해 따로 저장
  const markerRefList = useRef([])

  // ===========================================
  // 지도 좌표계 설정
  // ===========================================

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
  // ===========================================

  // 간선 레이어 ID 고정
  const lineLayerId = 'beacon-edges'

  // 픽셀 좌표를 가상의 위경도 좌표로 변환하는 함수
  const convertPixelToLngLat = (x, y, width, height) => {
    const lng = left + (x / width) * (right - left)
    const lat = top - (y / height) * (top - bottom) // Y축은 반대로 내려가므로 빼줌
    return [lng, lat]
  }

  // 최신 props 유지
  const modeRef = useRef(mode)
  const onMarkerClickRef = useRef(onMarkerClick)

  useEffect(() => {
    modeRef.current = mode
  }, [mode])

  useEffect(() => {
    onMarkerClickRef.current = onMarkerClick
  }, [onMarkerClick])

  // =============
  // 간선 삭제
  // =============

  // 간선 선택 상태
  const [selectedEdge, setSelectedEdge] = useState(null)
  const [xButtonTick, setXButtonTick] = useState(0) // 간선 삭제 버튼 위치 리렌더링용 상태

  // X 버튼 지도에 따라다니게
  useEffect(() => {
    const map = mapRef.current
    if (!map) return

    const handleMove = () => {
      if (selectedEdge) {
        setXButtonTick((prev) => prev + 1) // 리렌더 트리거
      }
    }

    map.on('move', handleMove)
    return () => map.off('move', handleMove)
  }, [selectedEdge])

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
      zoom: 1.0,
      maxZoom: 8.0, // 최대 줌 (더 가까이 못 가게)
      pitch: 0, // 위에서 수직으로 보기
      bearing: 0, // 회전 없음
      interactive: true,
      dragRotate: true,
      renderWorldCopies: false, // 지도 반복 방지
      maxBounds: [
        [left, bottom],
        [right, top],
      ],
    })
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
      map.setZoom(1.0)
    }

    // 스타일이 이미 로드되었다면 바로 추가
    if (map.isStyleLoaded()) {
      updateImageLayer()
    } else {
      // 스타일이 아직 안 끝났으면 이벤트 대기
      map.once('styledata', updateImageLayer)
    }
  }, [mapImageUrl])

  // 지도 클릭 이벤트 전달 로직
  useEffect(() => {
    const map = mapRef.current
    if (!map || !onMapClick) return

    const handleGlobalClick = (event) => {
      const { lng, lat } = event.lngLat

      // 간선이 선택된 상태인데 이번 클릭이 간선이 아니면 해제
      const clickedEdgeLayer = event.features?.some((f) => f.layer?.id === lineLayerId)
      if (selectedEdge && !clickedEdgeLayer) {
        setSelectedEdge(null)
      }

      onMapClick({ coord_x: lng, coord_y: lat })
    }

    map.on('click', handleGlobalClick)
    return () => map.off('click', handleGlobalClick)
  }, [onMapClick, selectedEdge])

  // 마커 및 간선 그리기
  useEffect(() => {
    const map = mapRef.current
    if (!map) return

    const drawAll = () => {
      try {
        // 기존 마커 제거
        markerRefList.current.forEach((m) => m.remove())
        markerRefList.current = []

        // 더미 데이터 때문에 크기 조정
        // const xScale = imageWidth / 600
        // const yScale = imageHeight / 400

        // beacon_code → [lng, lat] 매핑용 객체
        const beaconMap = {}

        beaconList.forEach((beacon) => {
          const { coord_x, coord_y, is_exit, is_cctv, name, beacon_code } = beacon

          // const scaledX = (coord_x + 100) * xScale
          // const scaledY = coord_y * yScale
          // const [lng, lat] = convertPixelToLngLat(coord_x, coord_y, imageWidth, imageHeight)
          const [lng, lat] = [coord_x, coord_y]

          // 위치 저장
          beaconMap[beacon_code] = [lng, lat]

          let IconComponent = Beacon
          if (is_exit) IconComponent = Exit
          else if (is_cctv) IconComponent = CCTV

          const container = document.createElement('div')
          ReactDOM.createRoot(container).render(<IconComponent className="text-primary h-8 w-8" />)

          // 이벤트 여기다 넣어!!
          container.addEventListener('click', () => {
            if (modeRef.current === 'route' || modeRef.current === 'map') {
              onMarkerClickRef.current?.(beacon)
            }
          })

          const marker = new mapboxgl.Marker({ element: container })
            .setLngLat([lng, lat])
            .addTo(map)

          markerRefList.current.push(marker)
        })

        // 기존 선 레이어 제거
        if (map.getLayer(lineLayerId)) map.removeLayer(lineLayerId)
        if (map.getSource(lineLayerId)) map.removeSource(lineLayerId)

        // edgeList → GeoJSON 변환
        const lineFeatures = edgeList
          .map((edge) => {
            const a = beaconMap[edge.beacon_a_code]
            const b = beaconMap[edge.beacon_b_code]
            if (!a || !b) return null
            return {
              type: 'Feature',
              geometry: {
                type: 'LineString',
                coordinates: [a, b],
              },
              properties: {
                edge_id: edge.edge_id, // 간선 삭제를 위해 추가
              },
            }
          })
          .filter(Boolean)

        // GeoJSON source 생성
        map.addSource(lineLayerId, {
          type: 'geojson',
          data: {
            type: 'FeatureCollection',
            features: lineFeatures,
          },
        })

        // 선 레이어 추가
        map.addLayer({
          id: lineLayerId,
          type: 'line',
          source: lineLayerId,
          paint: {
            'line-color': '#8aea52',
            'line-width': 6,
          },
        })

        console.log('✅ 레이어 확인:', map.getLayer(lineLayerId))

        // 간선 클릭 이벤트 등록 (레이어 클릭용)
        if (map.getLayer(lineLayerId)) {
          map.on('click', lineLayerId, (e) => {
            console.log('🔥 간선 클릭됨:', e.features)
            const edge = e.features?.[0]
            if (modeRef.current !== 'route') return
            if (edge?.properties?.edge_id) {
              setSelectedEdge(edge)
            }
          })
        }
      } catch (error) {
        console.error('지도 그리기 실패:', error)
      }
    }
    if (map.isStyleLoaded()) {
      drawAll()
    } else {
      map.once('styledata', drawAll)
    }
  }, [beaconList, edgeList, mapImageUrl, mode])

  return (
    <div className="relative h-full w-full">
      {/* 실제 map을 그릴 container */}
      <div ref={mapContainer} className="h-full w-full" />

      {/* tempMarker가 있을 경우 미리보기 마커 아이콘 표시 */}
      {tempMarker &&
        mapRef.current &&
        (() => {
          const projected = mapRef.current.project([tempMarker.coord_x, tempMarker.coord_y])

          const iconMap = {
            cctv: CCTV,
            beacon: Beacon,
            exit: Exit,
          }

          const Icon = iconMap[tempMarker.iconId]

          return (
            <div
              className="pointer-events-none absolute z-10"
              style={{
                top: `${projected.y}px`,
                left: `${projected.x}px`,
                transform: 'translate(-50%, -100%)',
              }}
            >
              {Icon && <Icon className="text-primary h-6 w-6" />}
            </div>
          )
        })()}

      {/* X 버튼: selectedEdge가 있을 때만 */}
      {mode === 'route' &&
        selectedEdge &&
        (() => {
          const [a, b] = selectedEdge.geometry.coordinates
          const projectedA = mapRef.current.project(a)
          const projectedB = mapRef.current.project(b)

          return (
            <div
              key={xButtonTick} // 리렌더 유도
              className="absolute z-50"
              style={{
                left: `${(projectedA.x + projectedB.x) / 2}px`,
                top: `${(projectedA.y + projectedB.y) / 2}px`,
                transform: 'translate(-50%, -50%)',
              }}
            >
              <button
                onClick={() => {
                  const edgeId = selectedEdge.properties.edge_id
                  if (edgeId) onDeleteEdge?.(edgeId)
                  setSelectedEdge(null)
                }}
                className="rounded-sm bg-red-600 p-1 text-xs text-white shadow-md hover:bg-red-500"
              >
                <Delete className="h-6 w-6" />
              </button>
            </div>
          )
        })()}
    </div>
  )
}

export default MapBoxMap

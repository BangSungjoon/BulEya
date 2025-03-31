import { useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'

// 컴포넌트
import MapBoxMap from '@/components/map/MapBoxMap'
import FloorNavigator from '@/components/map/FloorNavigator'
import IconBox from '@/components/map/IconBox'
import FacilityEditModal from '@/components/modals/FacilityEditModal'

import CCTV from '@/assets/icons/CCTV.svg?react'
import Beacon from '@/assets/icons/Beacon.svg?react'
import Exit from '@/assets/icons/Exit.svg?react'

// api 요청
import { fetchMapImage } from '@/api/axios'

export default function MapPage() {
  const location = useLocation()
  const mode = location.pathname.replace('/', '') || 'map'
  const stationId = 222 // 역사 번호 입력 페이지 구현 후 변경 필요

  const [floorDataList, setFloorDataList] = useState([]) // 전체 응답 저장
  const [selectedFloor, setSelectedFloor] = useState(null) // 선택된 층 번호
  const [selectedIcon, setSelectedIcon] = useState(null) // IconBox 관련

  // 지도 이미지 불러오는 API 호출
  useEffect(() => {
    const loadFloorData = async () => {
      try {
        const response = await fetchMapImage(stationId)
        console.log('응답:', response.data)
        const result = response.data.result
        // is_success에 따른 에러 처리 필요
        // const data = response.data.result

        // setFloorDataList(data)
        // setSelectedFloor(data[0].floor) // 첫 번째 층이 기본

        const parsedData = result.map((data) => ({
          floor: data.floor, // 층 정보가 이 안에 있다고 가정
          image_url: data.image_url,
          beacon_list: data.beacon_list,
          edge_list: data.edge_list,
        }))
        console.log('파싱된 데이터:', parsedData)

        setFloorDataList(parsedData)
        setSelectedFloor(parsedData[0].floor) // 첫 번째 층이 기본
      } catch (error) {
        console.error('지도 데이터 로드 실패: ', error)
      }
    }

    loadFloorData()
  }, [stationId])

  // 현재 선택된 층에 해당하는 데이터 추출
  const selectedData = floorDataList.find((f) => f.floor === selectedFloor)

  // -----------------------
  // 마우스 아이콘 관련
  // -----------------------

  // 마우스 위치 상태
  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 })

  const iconComponent = {
    cctv: CCTV,
    beacon: Beacon,
    exit: Exit,
  }[selectedIcon]

  const Icon = iconComponent

  useEffect(() => {
    const handelMouseMove = (event) => {
      setMousePosition({ x: event.clientX, y: event.clientY })
    }

    if (selectedIcon) {
      window.addEventListener('mousemove', handelMouseMove)
    }

    return () => {
      window.removeEventListener('movemove', handelMouseMove)
    }
  }, [selectedIcon])

  // -----------------------
  // Marker 추가 관련
  // -----------------------

  // 지도 클릭 위치 + 선택된 아이콘을 저장할 상태
  const [tempMarker, setTempMarker] = useState(null)

  const handleMapClick = ({ coord_x, coord_y }) => {
    // 아이콘이 선택된 상태일 때만 작동
    if (!selectedIcon) return

    setTempMarker({
      coord_x,
      coord_y,
      iconId: selectedIcon,
      floor: selectedFloor,
    })

    // 아이콘 선택은 한 번만 유효하게 (선택 해제)
    setSelectedIcon(null)
  }

  useEffect(() => {
    if (tempMarker) {
      console.log('🟢 tempMarker 업데이트됨:', tempMarker)
    }
  }, [tempMarker])

  // -------------------
  // 안내문 관련
  // -------------------
  const modeGuideText =
    mode === 'add'
      ? '등록할 장비를 선택한 후, 지도를 클릭해 마커를 등록해주세요.'
      : mode === 'route'
        ? '장비를 클릭하여 경로를 등록해주세요.'
        : null // map일 경우는 null

  return (
    <div className="h-full w-full">
      {/* 지도 렌더링 */}
      {selectedData && (
        <MapBoxMap
          mode={mode}
          mapImageUrl={selectedData.image_url}
          beaconList={selectedData.beacon_list}
          edgeList={selectedData.edge_list}
          selectedIcon={selectedIcon}
        />
      )}

      {/* 층 선택 UI */}
      <FloorNavigator
        floors={floorDataList.map((f) => f.floor)}
        selected={selectedFloor}
        onSelect={setSelectedFloor}
      />

      {/* 마우스 따라다니는 아이콘 렌더링 위치 */}
      {selectedIcon && iconComponent && (
        <div
          className="pointer-events-none fixed z-50"
          style={{
            top: mousePosition.y - 10,
            left: mousePosition.x - 10,
          }}
        >
          <Icon className="text-primary h-6 w-6" />
        </div>
      )}
      <div className="pointer-events-none absolute inset-0 z-10 mx-5 mt-30 mb-5 grid grid-cols-12">
        {/* 장비 등록/삭제/수정 모달 */}
        {/* FacilityEditModal만 pointer-events 살림 */}
        <div className="pointer-events-auto col-span-3">
          <FacilityEditModal
            initialData={{
              station_id: stationId,
              floor: selectedFloor,
              coord_x: tempMarker?.coord_x,
              coord_y: tempMarker?.coord_y,
            }}
          />
        </div>
      </div>
    </div>
  )
}

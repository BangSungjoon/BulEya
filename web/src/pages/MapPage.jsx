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

  // 지도 이미지 불러오는 API 호출
  useEffect(() => {
    const loadFloorData = async () => {
      try {
        const response = await fetchMapImage(stationId)
        // is_success에 따른 에러 처리 필요
        const data = response.data.result

        setFloorDataList(data)
        setSelectedFloor(data[0].floor) // 첫 번째 층이 기본
      } catch (error) {
        console.error('지도 데이터 로드 실패: ', error)
      }
    }

    loadFloorData()
  }, [stationId])

  // 현재 선택된 층에 해당하는 데이터 추출
  let selectedData = floorDataList.find((f) => f.floor === selectedFloor)

  // ----------------------
  // IconBox 관련
  // ----------------------
  const [selectedIcon, setSelectedIcon] = useState(null)

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
      coord_x: Math.round(coord_x),
      coord_y: Math.round(coord_y),
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

  // ==================
  // 모달 관련
  // ==================
  const isCctv = tempMarker?.iconId === 'cctv'
  const isExit = tempMarker?.iconId === 'exit'

  const handleCloseModal = () => {
    setIsModalVisible(false) // 먼저 애니메이션 시작

    setTimeout(() => {
      setTempMarker(null) // 애니메이션 끝난 후 제거, 모달 닫으면 임시 마커 삭제
    }, 300) // duration과 맞춰주기 (ms)
  }

  // 모달 애니메이션션
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    if (tempMarker) {
      setIsModalVisible(true)
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
          onMapClick={mode === 'add' ? handleMapClick : undefined}
          tempMarker={tempMarker}
        />
      )}

      {/* 모드 안내 문구 */}
      {modeGuideText && (
        <div className="text-primary text-caption absolute top-4 left-1/2 z-40 h-fit w-fit -translate-x-1/2 rounded-full bg-gray-600 px-4 py-2 text-sm whitespace-nowrap shadow-md">
          {modeGuideText}
        </div>
      )}

      {/* 아이콘 선택 UI는 add 모드일 때만 */}
      {mode === 'add' && <IconBox selectedIcon={selectedIcon} onSelect={setSelectedIcon} />}

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

      {/* 장비 등록/삭제/수정 모달 */}
      {mode === 'add' && tempMarker && (
        <div className="pointer-events-none absolute inset-0 z-40 mx-5 mt-30 mb-5 grid grid-cols-12">
          <div
            className={`pointer-events-auto col-span-5 transform transition-all duration-300 md:col-span-3 ${isModalVisible ? 'translate-x-0 opacity-100' : '-translate-x-full opacity-0'}`}
          >
            <FacilityEditModal
              initialData={{
                station_id: stationId,
                floor: selectedFloor,
                coord_x: tempMarker.coord_x,
                coord_y: tempMarker.coord_y,
                is_cctv: isCctv,
                is_exit: isExit,
              }}
              onClose={handleCloseModal}
            />
          </div>
        </div>
      )}
    </div>
  )
}

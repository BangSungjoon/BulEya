import { useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'

// 컴포넌트
import MapBoxMap from '@/components/map/MapBoxMap'
import FloorNavigator from '@/components/map/FloorNavigator'
import IconBox from '@/components/map/IconBox'

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

  return (
    <div className="relative h-full w-full">
      {/* 지도 렌더링 */}
      {selectedData && <MapBoxMap mode={mode} mapImageUrl={selectedData.image_url} />}

      {/* 층 선택 UI */}
      <FloorNavigator
        floors={floorDataList.map((f) => f.floor)}
        selected={selectedFloor}
        onSelect={setSelectedFloor}
      />

      {/* 아이콘 선택 UI는 add 모드일 때만 */}
      {mode === 'add' && <IconBox selectedIcon={selectedIcon} onSelect={setSelectedIcon} />}

      {/* 마우스 따라다니는 아이콘 렌더링 위치 */}
      {selectedIcon && iconComponent && (
        <div
          className="fixed z-50"
          style={{
            top: mousePosition.y - 10,
            left: mousePosition.x - 10,
          }}
        >
          <Icon className="text-primary h-6 w-6" />
        </div>
      )}
    </div>
  )
}

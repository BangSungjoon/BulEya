import { useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'

// 컴포넌트
import MapBoxMap from '@/components/map/MapBoxMap'
import FloorNavigator from '@/components/map/FloorNavigator'
// import { IconBoxModal } from '@/components/map/IconBoxModal'

// api 요청
import { fetchMapImage } from '@/api/axios'

export default function MapPage() {
  const location = useLocation()
  const mode = location.pathname.replace('/', '') || 'map'
  const stationId = 222 // 역사 번호 입력 페이지 구현 후 변경 필요

  const [floorDataList, setFloorDataList] = useState([]) // 전체 응답 저장
  const [selectedFloor, setSelectedFloor] = useState(null) // 선택된 층 번호

  // API 호출
  useEffect(() => {
    const loadFloorData = async () => {
      try {
        const response = await fetchMapImage(stationId)
        // is_success에 따른 에러 처리 필요
        const data = response.data.result

        console.log('[✅ API 응답]', response.data)

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
  console.log('[🧩 선택된 층 데이터]', selectedData)

  return (
    <div className="relative h-full w-full">
      {/* 지도 렌더링 */}
      {selectedData && <MapBoxMap mode={mode} mapImageUrl={selectedData.image_url} />}
      {/* 층 선택 UI */}
      <FloorNavigator
        floors={floorDataList.map((f) => f.floor)}
        selected={selectedFloor}
        onSelect={(floor) => setSelectedFloor(floor)}
      />
    </div>
  )
}

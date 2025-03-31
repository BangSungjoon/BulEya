import { useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'

// 컴포넌트
import MapBoxMap from '@/components/map/MapBoxMap'
import FloorNavigator from '@/components/map/FloorNavigator'
import IconBox from '@/components/map/IconBox'

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

  return (
    <div className="relative h-full w-full">
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
        onSelect={(floor) => setSelectedFloor(floor)}
      />

      {/* 아이콘 선택 UI는 add 모드일 때만 */}
      {mode === 'add' && <IconBox selectedIcon={selectedIcon} onSelect={setSelectedIcon} />}
    </div>
  )
}

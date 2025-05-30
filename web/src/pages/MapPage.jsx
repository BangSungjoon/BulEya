import { useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'

// 컴포넌트
import MapBoxMap from '@/components/map/MapBoxMap'
import FloorNavigator from '@/components/map/FloorNavigator'
import IconBox from '@/components/map/IconBox'
import FacilityEditModal from '@/components/modals/FacilityEditModal'
import FacilityDetailModal from '@/components/modals/FacilityDetailModal'

import CCTV from '@/assets/icons/CCTV.svg?react'
import Beacon from '@/assets/icons/Beacon.svg?react'
import Exit from '@/assets/icons/Exit.svg?react'
import Pin from '@/assets/icons/Pin.svg?react'

// api 요청
import { fetchMapImage, createEdge, deleteEdge } from '@/api/axios'

export default function MapPage() {
  const location = useLocation()
  const mode = location.pathname.replace('/', '') || 'map'
  const stationId = Number(localStorage.getItem('stationId'))

  if (stationId) {
    // stationId가 존재하면 이 아래 로직 실행
    console.log('선택된 역 ID:', stationId)
  } else {
    // 없으면 로그인 페이지로 이동
    window.location.href = '/login' // 로그인 페이지 URL로 변경
  }

  const [floorDataList, setFloorDataList] = useState([]) // 전체 응답 저장
  const [selectedFloor, setSelectedFloor] = useState(null) // 선택된 층 번호
  const [selectedIcon, setSelectedIcon] = useState(null) // IconBox 관련

  // [성준] 마커 선택 상태를 외부에서 제어 가능하게
  const [selectedMarkerId, setSelectedMarkerId] = useState(null)

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
    const handleMouseMove = (event) => {
      setMousePosition({ x: event.clientX, y: event.clientY })
    }

    if (selectedIcon) {
      window.addEventListener('mousemove', handleMouseMove)
    }

    return () => {
      window.removeEventListener('mousemove', handleMouseMove)
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
      coord_x: Number(coord_x.toFixed(6)),
      coord_y: Number(coord_y.toFixed(6)),
      iconId: selectedIcon,
      floor: selectedFloor,
    })

    // 아이콘 선택은 한 번만 유효하게 (선택 해제)
    setSelectedIcon(null)
  }

  // ==================
  // 모달 관련
  // ==================
  const is_cctv = tempMarker?.iconId === 'cctv'
  const is_exit = tempMarker?.iconId === 'exit'

  const handleCloseModal = () => {
    setIsModalVisible(false) // 먼저 애니메이션 시작

    setTimeout(() => {
      setTempMarker(null) // 애니메이션 끝난 후 제거, 모달 닫으면 임시 마커 삭제
    }, 300) // duration과 맞춰주기 (ms)
  }

  // 모달 애니메이션
  const [isModalVisible, setIsModalVisible] = useState(false)

  // 모드 변경 시 detail 모달 닫기
  useEffect(() => {
    if (mode !== 'map') {
      setIsDetailVisible(false)
      setDisplayedFacility(null)
      setSelectedFacility(null)
      setSelectedMarkerId(null)
    }
  }, [mode])

  useEffect(() => {
    if (tempMarker) {
      setIsModalVisible(true)
    }
  }, [tempMarker])

  // ==================
  // 마커 선택 관련
  // =================
  const [selectedFacility, setSelectedFacility] = useState(null)
  const [displayedFacility, setDisplayedFacility] = useState(null) // 모달에 실제로 보여줄 facility
  const [isDetailVisible, setIsDetailVisible] = useState(false)

  useEffect(() => {
    if (selectedFacility) {
      setIsDetailVisible(true)
    }
  }, [selectedFacility])

  const handleMarkerDetailClick = (facilityData) => {
    // 1. 현재 모달 보이고 있다면 → exit 먼저
    if (displayedFacility) {
      setIsDetailVisible(false) // 사라지게
      setTimeout(() => {
        setDisplayedFacility(facilityData) // 데이터 교체
        setIsDetailVisible(true) // 다시 나타나게
      }, 300)
    } else {
      // 처음 클릭이라면 바로 보여줌
      setDisplayedFacility(facilityData)
      setIsDetailVisible(true)
    }
  }

  const handleCloseDetailModal = () => {
    setIsDetailVisible(false) // 애니메이션 먼저

    setTimeout(() => {
      setSelectedFacility(null) // 모달 실제 제거
      setSelectedMarkerId(null) // [성준]
    }, 300) // transition duration과 맞춰주기 (ms 단위)
  }

  // 시설 등록 되면 호출될 함수
  const reloadFloorData = async () => {
    try {
      const response = await fetchMapImage(stationId)
      const result = response.data.result

      const parsedData = result.map((data) => ({
        floor: data.floor,
        image_url: data.image_url,
        beacon_list: data.beacon_list,
        edge_list: data.edge_list,
      }))

      setFloorDataList(parsedData)
      setSelectedFloor((prev) => prev) // 현재 층 그대로 유지
    } catch (err) {
      console.error('설비 재로딩 실패:', err)
    }
  }

  const reloadEdgeOnly = async () => {
    try {
      const response = await fetchMapImage(stationId)
      const result = response.data.result
      const updatedEdges = result.find((d) => d.floor === selectedFloor)?.edge_list || []

      setFloorDataList((prevList) =>
        prevList.map((floor) =>
          floor.floor === selectedFloor ? { ...floor, edge_list: updatedEdges } : floor,
        ),
      )
    } catch (err) {
      console.error('간선만 재로딩 실패:', err)
    }
  }

  const handleRegisterSuccess = async () => {
    await reloadBeaconOnly()
    setTempMarker(null) // ✅ 임시 마커 제거!
    setIsModalVisible(false) // ✅ 모달도 닫기
  }

  const reloadBeaconOnly = async () => {
    try {
      const response = await fetchMapImage(stationId)
      const result = response.data.result
      const updatedBeacons = result.find((d) => d.floor === selectedFloor)?.beacon_list || []

      setFloorDataList((prevList) =>
        prevList.map((floor) =>
          floor.floor === selectedFloor ? { ...floor, beacon_list: updatedBeacons } : floor,
        ),
      )
    } catch (err) {
      console.error('비콘만 재로딩 실패:', err)
    }
  }

  // ==================
  // 간선 등록 관련
  // ==================
  // 상태
  const [selectedNodes, setSelectedNodes] = useState([])

  // 감지해서 간선 등록
  useEffect(() => {
    if (selectedNodes.length === 2) {
      const [a, b] = selectedNodes
      const distance = calcDistance(a, b)

      registerEdge({
        station_id: stationId,
        floor: selectedFloor,
        beacon_a_code: a.beacon_code,
        beacon_b_code: b.beacon_code,
        distance,
      })

      setSelectedNodes([]) // 초기화는 여기서!
    }
  }, [selectedNodes]) // ← selectedNodes가 바뀔 때만 실행됨

  // 클릭 시에는 상태만 바꿈
  // const handleMarkerClick = (beacon) => {
  //   setSelectedNodes((prev) => {
  //     const already = prev.some((b) => b.beacon_code === beacon.beacon_code)
  //     if (already) return prev
  //     return [...prev, beacon]
  //   })
  // }
  // [성준] 클릭 시에는 상태만 바꿈
  // selectedMarkerId가 null일 때 모달 상태도 닫기
  useEffect(() => {
    if (selectedMarkerId === null) {
      setIsDetailVisible(false)
      setDisplayedFacility(null)
      setSelectedFacility(null)
    }
  }, [selectedMarkerId])

  const handleMarkerClick = (beacon) => {
    if (mode === 'map') {
      if (!beacon) {
        setIsDetailVisible(false)
        setTimeout(() => {
          setDisplayedFacility(null)
          setSelectedFacility(null)
          setSelectedMarkerId(null)
        }, 300)
        return
      }

      if (selectedMarkerId === beacon.beacon_code) {
        // 닫을 때
        setIsDetailVisible(false)
        setTimeout(() => {
          setDisplayedFacility(null)
          setSelectedFacility(null)
          setSelectedMarkerId(null)
        }, 300)
      } else {
        // 열 때는 반드시 순서대로 해줘야 애니메이션 발생
        setDisplayedFacility(beacon) // DOM에 추가 (모달 off 상태로 렌더링)
        setSelectedFacility(beacon)

        // 여기가 중요: 아주 짧은 타임아웃 뒤에 true로 변경
        setIsDetailVisible(false) // 모달 off로 초기화
        setTimeout(() => {
          setIsDetailVisible(true) // 모달 on (애니메이션 효과 시작)
          setSelectedMarkerId(beacon.beacon_code) // 모달 열기
        }, 10)
      }
    }

    if (mode === 'route') {
      setSelectedNodes((prev) => {
        const already = prev.some((b) => b.beacon_code === beacon.beacon_code)
        if (already) return prev
        return [...prev, beacon]
      })
    }
  }

  // 거리 계산 함수
  const calcDistance = (a, b) => {
    const dx = a.coord_x - b.coord_x
    const dy = a.coord_y - b.coord_y
    return Math.round(Math.sqrt(dx * dx + dy * dy))
  }

  // 간선 등록 API 호출 함수
  const registerEdge = async (payload) => {
    try {
      console.log('자 여기!!', payload)

      const response = await createEdge(payload)
      alert('✅ 간선 등록 완료!')

      console.log('서버 응답:', response)

      setSelectedNodes([])
      // await reloadFloorData()
      await reloadEdgeOnly()
    } catch (err) {
      console.error('❌ 간선 등록 실패:', err)
      alert('간선 등록에 실패했습니다.')
      setSelectedNodes([])
    }
  }

  // ==================
  // 간선 삭제 관련
  // ==================
  const handleDeleteEdge = async (edgeId) => {
    try {
      await deleteEdge({ edge_id: edgeId })
      alert('✅ 간선 삭제 완료')
      // await reloadFloorData()
      await reloadEdgeOnly()
    } catch (err) {
      console.error('삭제 실패:', err)
      alert('❌ 간선 삭제 실패')
    }
  }

  // [성준] 마커 삭제 핸들러 (자식 컴포넌트 MapBoxMap에서 호출됨)
  const handleDeleteFacility = (beacon_id) => {
    const updatedList = floorDataList.map((floor) => {
      if (floor.floor !== selectedFloor) return floor

      return {
        ...floor,
        beacon_list: floor.beacon_list.filter((b) => b.beacon_id !== beacon_id),
      }
    })

    setFloorDataList(updatedList)
    setSelectedFacility(null)
  }

  // -------------------
  // 안내문 관련
  // -------------------
  const modeGuideText =
    mode === 'add'
      ? '등록할 장비를 선택한 후, 지도를 클릭해 마커를 등록해주세요.'
      : mode === 'route'
        ? '장비를 클릭하여 경로를 등록해주세요.'
        : null // map일 경우는 null

  const [displayGuide, setDisplayGuide] = useState(false)
  const [visibleGuide, setVisibleGuide] = useState(false)

  useEffect(() => {
    setDisplayGuide(true) // 컴포넌트 자체를 보여줌
    setTimeout(() => setVisibleGuide(true), 10) // 약간 딜레이 후 진짜 트랜지션 시작

    const timer = setTimeout(() => {
      setVisibleGuide(false) // 트랜지션 out
      setTimeout(() => setDisplayGuide(false), 300) // DOM 제거 (300ms 후)
    }, 2000)

    return () => clearTimeout(timer)
  }, [mode])

  return (
    <div className="h-full w-full">
      {/* 지도 렌더링 */}
      {selectedData && (
        <MapBoxMap
          mode={mode}
          mapImageUrl={selectedData.image_url}
          beaconList={selectedData.beacon_list} // [성준] 얕은 복사로 강제로 새로운 참조
          edgeList={selectedData.edge_list}
          selectedIcon={selectedIcon}
          onMapClick={mode === 'add' ? handleMapClick : undefined}
          // onMarkerClick={mode === 'route' ? handleMarkerClick : handleMarkerDetailClick}
          onMarkerClick={handleMarkerClick} // [성준] 클릭 시 모달 열기
          onDeleteEdge={handleDeleteEdge}
          tempMarker={tempMarker}
          selectedNodes={selectedNodes} // 간선 추가 시 선택된 노트 하이라이트
          selectedMarkerId={selectedMarkerId}
          setSelectedMarkerId={setSelectedMarkerId}
        />
      )}

      {/* 모드 안내 문구 */}
      {displayGuide && modeGuideText && (
        <div
          className={`absolute top-4 left-1/2 z-40 w-fit -translate-x-1/2 transform overflow-hidden rounded-full bg-gray-600 py-2 text-sm shadow-md transition-all duration-300 ${visibleGuide ? 'translate-y-0 opacity-100' : '-translate-y-10 opacity-0'} `}
        >
          <div className="text-primary text-caption px-4 whitespace-nowrap">{modeGuideText}</div>
          <div className="mt-1 h-[2px] w-full overflow-hidden rounded-full bg-gray-400">
            <div className="bg-primary animate-guide-bar h-full" />
          </div>
        </div>
      )}

      {/* 역사 번호 안내 */}
      <div className="text-caption absolute top-5 left-5 flex flex-row items-center gap-2 rounded-full bg-gray-600 px-2 py-1">
        <Pin />
        <p className="text-gray-100">강남역</p>
        <p className="text-gray-400">{stationId}</p>
      </div>

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

      {/* 장비 등록/수정 모달 */}
      {mode === 'add' && tempMarker && (
        <div className="pointer-events-none absolute inset-0 z-20 mx-5 mt-30 mb-5 grid grid-cols-12">
          <div
            className={`pointer-events-auto col-span-5 transform transition-all duration-300 md:col-span-3 ${isModalVisible ? 'translate-x-0 opacity-100' : '-translate-x-full opacity-0'}`}
          >
            <FacilityEditModal
              initialData={{
                station_id: stationId,
                floor: selectedFloor,
                coord_x: tempMarker.coord_x,
                coord_y: tempMarker.coord_y,
                is_cctv: is_cctv,
                is_exit: is_exit,
              }}
              onClose={handleCloseModal}
              // onSuccess={reloadBeaconOnly}
              onSuccess={handleRegisterSuccess}
            />
          </div>
        </div>
      )}

      {/* 장비 상세 모달 */}
      {/* {(isDetailVisible || displayedFacility) && (
        <div
          className={`pointer-events-none absolute inset-0 z-20 mx-5 mt-15 mb-5 grid grid-cols-12`}
        >
          <div
            className={`pointer-events-auto col-span-5 transform transition-all duration-300 md:col-span-3 ${
              isDetailVisible ? 'translate-x-0 opacity-100' : '-translate-x-full opacity-0'
            }`}
          >
            <FacilityDetailModal
              data={displayedFacility}
              onClose={handleCloseDetailModal}
              onDelete={handleDeleteFacility}
            />
          </div>
        </div>
      )}
       */}
      {displayedFacility && (
        <div className="pointer-events-none absolute inset-0 z-20 mx-5 mt-15 mb-5 grid grid-cols-12">
          <div
            key={displayedFacility.beacon_code} // key로 강제 리렌더링
            className={`pointer-events-auto col-span-5 transform transition-all duration-300 md:col-span-3 ${
              isDetailVisible ? 'translate-x-0 opacity-100' : '-translate-x-full opacity-0'
            }`}
          >
            <FacilityDetailModal
              data={displayedFacility}
              onClose={handleCloseDetailModal}
              onDelete={handleDeleteFacility}
            />
          </div>
        </div>
      )}
    </div>
  )
}

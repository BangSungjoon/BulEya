import { useEffect, useState } from 'react'
import MapBoxMap from '@/components/map/MapBoxMap'
// import { IconBoxModal } from '@/components/map/IconBoxModal'

// api 요청
import { fetchMapImage } from '@/api/axios'

export default function MapPage({ mode }) {
  const [mapImageUrl, setMapImageUrl] = useState(null)
  const stationId = 222 // 역사 번호 입력 페이지 구현 후 변경 필요

  useEffect(() => {
    const loadImageUrl = async () => {
      try {
        const response = await fetchMapImage(stationId)
        console.log('API 응답:', response.data)

        setMapImageUrl(response.data.result[0].image_url) // 응답을 바탕으로 setMapImageUrl 설정 (일단 B1층 지도만 가져옴, 추후 변경 필요요)
        console.log(response.data.result[0].image_url)
        console.log('지도', mapImageUrl)
      } catch (error) {
        console.error('지도 불러오기 실패 : ', error)
      }
    }

    loadImageUrl()
    console.log('[imageUrl]', mapImageUrl)
  }, [stationId])

  useEffect(() => {
    console.log('이미지 URL:', mapImageUrl)
    const img = new Image()
    img.src = mapImageUrl
    img.onload = () => {
      console.log('이미지 사이즈:', img.naturalWidth, img.naturalHeight)
    }
  }, [mapImageUrl])

  return (
    <div className="h-full w-full">
      {/* 지도 항상 보여줌 */}
      <MapBoxMap mode={mode} mapImageUrl={mapImageUrl} />
    </div>
  )
}

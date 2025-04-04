import Delete from '@/assets/icons/Delete.svg?react'
import { deleteBeacon, fetchCctvWebSocket } from '@/api/axios.js'
import { useEffect, useRef } from 'react'

export default function FacilityDetailModal({ data, onClose, onDelete }) {
  const { name, beacon_code, beacon_id, cctv_ip, is_cctv = false, is_exit = false } = data || {}
  const stationId = 222
  const canvasRef = useRef(null)
  const wsRef = useRef(null)
  const playerRef = useRef(null)

  useEffect(() => {
    if (!is_cctv) return
    
    const cctvStream = async () => {
      try {
        const response = await fetchCctvWebSocket(stationId, beacon_code)
        const wsUrl = response.data.socket_url
        const ws = new WebSocket(wsUrl)
        wsRef.current = ws

        ws.onopen = () => {
          console.log('WebSocket 연결 성공!');
          if (!canvasRef.current) return
          
        }
        ws.onerror = (error) => {
          console.error('WebSocket 에러:', error);
        }

        const player = new window.jsmpeg(ws, {
          canvas: canvasRef.current,
          autoplay: true,
          loop: false,
          onLoad: () => {
            console.log('스트리밍 시작!');
          }
        });
        playerRef.current = player;

      } catch (error) {
        console.error('CCTV WebSocket 연결 실패:', error)
        alert('CCTV 스트리밍을 가져오는 데 실패했습니다.')
      }
      }
      cctvStream()

    // 컴포넌트 언마운트 시 자원 정리
    return () => {
      if (playerRef.current) {
        playerRef.current.destroy()
        playerRef.current = null
      }
      if (wsRef.current) {
        wsRef.current.close()
        wsRef.current = null
      }
    }
  }, [])
  
  const handleDelete = async () => {
    if (!data?.beacon_id) return alert('삭제할 비콘 ID가 없습니다.')

    const confirmDelete = window.confirm('정말 삭제하시겠습니까?')
    if (!confirmDelete) return

    try {
      await deleteBeacon(data.beacon_id)
      alert('삭제가 완료되었습니다.')
      onClose()
      onDelete && onDelete(data.beacon_id) // 부모에게 삭제 알림!
    } catch (error) {
      console.error('비콘 삭제 실패:', error)
      alert('삭제에 실패했습니다.')
    }
  }

  

  return (
    <div className="relative flex h-full w-full flex-col overflow-hidden rounded-3xl bg-gray-500 text-white">
      {/* 닫기 버튼 - 영상 위 오른쪽 상단 */}
      <button onClick={onClose} className="absolute top-3 right-3 z-10">
        <Delete className="h-6 w-6 text-white" />
      </button>
      {/* CCTV 모드일 경우 상단에 비디오 */}
      {is_cctv && (
        <div className="aspect-video w-full bg-black">
          {/* RTSP 영상 스트리밍 */}
          <canvas
            ref={canvasRef}
            className="h-full w-full object-cover"
          ></canvas>
        </div>
      )}

      {/* 콘텐츠 영역 */}
      <div className="flex flex-1 flex-col gap-4 p-4">
        {/* 이름 */}
        <div>
          <p className="text-h3">{name || '시설명 없음'}</p>
        </div>

        {/* 비콘 코드 */}
        <div>
          <p className="text-caption mb-1 text-gray-400">비콘 코드</p>
          <div className="text-body break-all">{beacon_code || '-'}</div>
        </div>

        {/* 출구 여부 */}
        <div>
          <p className="text-caption mb-1 text-gray-400">출구</p>
          <div className="flex items-center justify-between gap-2">
            <p className="text-overline max-w-[80%] text-sm leading-snug text-gray-300">
              공사 등 출구로 사용할 수 없는 곳이라면, 선택을 해제해주세요.
            </p>
            <input
              type="checkbox"
              className="accent-primary mt-1 h-5 w-5"
              checked={!!is_exit}
              readOnly
            />
          </div>
        </div>

        {/* CCTV 여부 */}
        <div>
          <p className="text-caption mb-1 text-gray-400">CCTV</p>
          <div className="flex items-center justify-between gap-2">
            <p className="text-overline max-w-[80%] text-sm leading-snug text-gray-300">
              현재 비콘에 CCTV가 위치해있을 경우 선택해주세요
            </p>
            <input
              type="checkbox"
              className="accent-primary mt-1 h-5 w-5"
              checked={!!is_cctv}
              readOnly
            />
          </div>
        </div>
      </div>

      {/* 버튼 영역 */}
      <div className="flex gap-2 p-4">
        <button className="bg-primary hover:bg-primary/80 text-h3 h-10 flex-1 rounded-lg text-gray-600 transition-all duration-200 hover:-translate-y-0.5">
          수정
        </button>
        <button
          onClick={handleDelete}
          className="text-h3 h-10 flex-1 rounded-lg bg-red-500 text-white transition-all duration-200 hover:-translate-y-0.5 hover:bg-red-400"
        >
          삭제
        </button>
      </div>
    </div>
  )
}

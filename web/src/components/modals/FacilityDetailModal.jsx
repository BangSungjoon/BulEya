import Delete from '@/assets/icons/Delete.svg?react'

export default function FacilityDetailModal({ data, onClose }) {
  const { name, beacon_code, cctv_ip, is_cctv = false, is_exit = false } = data || {}

  return (
    <div className="flex h-full w-full flex-col overflow-hidden rounded-3xl bg-gray-500 text-white">
      {/* CCTV 모드일 경우 상단에 비디오 */}
      {is_cctv && (
        <div className="relative aspect-video w-full bg-black">
          {/* RTSP 영상 스트리밍 (예시로 video 태그 사용) */}
          <video src={cctv_ip} controls autoPlay muted className="h-full w-full object-cover" />

          {/* 닫기 버튼 - 영상 위 오른쪽 상단 */}
          <button onClick={onClose} className="absolute top-3 right-3 z-10">
            <Delete className="h-6 w-6 text-white" />
          </button>
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
          저장
        </button>
        <button className="text-h3 h-10 flex-1 rounded-lg bg-red-500 text-white transition-all duration-200 hover:-translate-y-0.5 hover:bg-red-400">
          삭제
        </button>
      </div>
    </div>
  )
}

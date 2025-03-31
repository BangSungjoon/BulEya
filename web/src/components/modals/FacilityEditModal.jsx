import { useState } from 'react'

import Delete from '@/assets/icons/Delete.svg?react'
import Check from '@/assets/icons/Check.svg?react'

export default function FacilityEditModal({ initailData }) {
  const [formData, setFormData] = useState({
    beacon_code: '',
    name: '',
    is_exit: false,
    is_cctv: false,
    cctv_ip: '',
    ...initailData, // 마커 좌표, 층, station_id 등을 포함함
  })

  return (
    <div className="flex h-full w-full flex-col rounded-3xl bg-gray-500 p-4 text-white">
      <div className="flex-1">
        <div className="mb-5 flex flex-row text-gray-100">
          <p className="flex-1">시설 등록</p>
          <Delete className="h-6 w-6 text-gray-100" />
        </div>

        <div className="flex flex-col gap-2">
          <div>
            <p className="text-caption mb-1 text-gray-400">시설명</p>
            <input type="text" className="h-8 w-full rounded-sm bg-gray-600" />
          </div>
          <div>
            <p className="text-caption mb-1 text-gray-400">비콘 코드</p>
            <input type="text" className="h-8 w-full rounded-sm bg-gray-600" />
          </div>
          <div>
            <p className="text-caption mb-1 text-gray-400">RTSP 주소</p>
            <input type="text" className="h-8 w-full rounded-sm bg-gray-600" />
          </div>
          <div>
            <p className="text-caption mb-1 text-gray-400">출구</p>
            <div className="flex items-center justify-between gap-2">
              <p className="text-overline max-w-[80%] text-sm leading-snug text-gray-300">
                공사 등 출구로 사용할 수 없는 곳이라면, 선택을 해제해주세요.
              </p>
              <input
                type="checkbox"
                className="accent-primary mt-1 h-5 w-5"
                checked={formData.is_exit}
                onChange={(e) => setFormData({ ...formData, is_exit: e.target.checked })}
              />
            </div>
          </div>
          <div>
            <p className="text-caption mb-1 text-gray-400">CCTV</p>
            <div className="flex items-center justify-between gap-2">
              <p className="text-overline max-w-[80%] text-sm leading-snug text-gray-300">
                현재 비콘에 CCTV가 위치해있을 경우 선택해주세요
              </p>
              <input
                type="checkbox"
                className="accent-primary mt-1 h-5 w-5"
                checked={formData.is_cctv}
                onChange={(e) => setFormData({ ...formData, is_cctv: e.target.checked })}
              />
            </div>
          </div>
        </div>
      </div>

      {/* 버튼 */}
      <button className="bg-primary hover:bg-primary/80 text-h3 h-10 w-full rounded-lg text-gray-600 transition-all duration-200 hover:-translate-y-0.5">
        저장
      </button>
    </div>
  )
}

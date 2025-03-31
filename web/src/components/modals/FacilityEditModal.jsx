import { useState } from 'react'

import Delete from '@/assets/icons/Delete.svg?react'

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
    <div className="col-span-3 h-full w-full rounded-xl bg-gray-500 p-4 text-white">
      <div className="flex flex-row text-gray-100">
        <p className="flex-1">시설 등록</p>
        <Delete className="h-6 w-6 text-gray-100" />
      </div>

      <div>
        <p className="text-caption text-gray-400">시설명</p>
        <input type="text" className="w-full rounded-sm bg-gray-600" />
      </div>
      <div>
        <p className="text-caption text-gray-400">비콘 코드</p>
        <input type="text" className="w-full rounded-sm bg-gray-600" />
      </div>
    </div>
  )
}

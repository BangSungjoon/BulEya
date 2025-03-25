import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'

import NavBar from '@/components/map/NavBar'
import MapPage from '@/pages/MapPage'

import Map from '@/assets/icons/Map.svg?react'
import Plus from '@/assets/icons/Plus.svg?react'
import Flag from '@/assets/icons/Flag.svg?react'

// Navbar 메뉴 리스트 정의
const navItems = [
  { id: 'map', icon: Map, label: '역사 지도' },
  { id: 'add', icon: Plus, label: '장비 등록' },
  { id: 'route', icon: Flag, label: '대피 경로' },
]

function App() {
  // 현재 선태된 메뉴 상태 관리 (기본값: map)
  const [selected, setSelected] = useState('map')

  return (
    // 전체 레이아웃: 사이드 네비게이션 + 본문
    <div className="flex h-screen flex-row">
      {/* 네비게이션에 메뉴 항목과 상태 전달 */}
      <NavBar items={navItems} activeItem={selected} onSelect={setSelected} />

      {/* 본문 */}
      <main className="ml-6 p-4">
        <MapPage mode={selected} />
      </main>
    </div>
  )
}

export default App

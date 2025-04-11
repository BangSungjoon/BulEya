import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  useNavigate,
  useLocation,
} from 'react-router-dom'

import NavBar from '@/components/map/NavBar'
import MapPage from '@/pages/MapPage'
import LoginPage from '@/pages/LoginPage'

import Map from '@/assets/icons/Map.svg?react'
import Plus from '@/assets/icons/Plus.svg?react'
import Flag from '@/assets/icons/Flag.svg?react'

// 네비게이션 항목
const navItems = [
  { id: 'map', icon: Map, label: '역사 지도' },
  { id: 'add', icon: Plus, label: '장비 등록' },
  { id: 'route', icon: Flag, label: '대피 경로' },
]

// 실제 Layout 컴포넌트: 네비게이션 + 본문 영역 포함
function Layout() {
  const navigate = useNavigate()
  const location = useLocation()

  // 현재 경로(pathname)를 추출해 현재 선택된 메뉴 ID로 사용
  const current = location.pathname.replace('/', '') || 'map'
  const isLoginPage = location.pathname === '/login'

  return (
    <div className="flex h-screen">
      {/* 사이드 네비게이션 */}
      {!isLoginPage && (
        <NavBar items={navItems} activeItem={current} onSelect={(id) => navigate(`/${id}`)} />
      )}

      {/* 본문 영역 - 라우팅에 따라 MapPage 렌더링 */}
      <main className="relative flex-1">
        <Routes>
          {/* 역사 로그인 페이지 */}
          <Route path="/login" element={<LoginPage />} />

          {/* 각 경로는 MapPage를 렌더링하며, 내부에서 모드 분기 처리 */}
          <Route path="/map" element={<MapPage />} />
          <Route path="/add" element={<MapPage />} />
          <Route path="/route" element={<MapPage />} />

          {/* 기본 경로 접속 시 /map으로 리디렉션 */}
          <Route path="/" element={<Navigate to="/map" replace />} />

          {/* 존재하지 않는 경로 접속 시 404 안내 */}
          <Route path="*" element={<div>404: 페이지를 찾을 수 없습니다</div>} />
        </Routes>
      </main>
    </div>
  )
}

function App() {
  return (
    <Router>
      <Layout />
    </Router>
  )
}

export default App

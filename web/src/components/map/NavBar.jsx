import React from 'react'
import { useNavigate } from 'react-router-dom'

import LogOut from '@/assets/icons/LogOut.svg?react'
import { logOut } from '@/api/axios'

// NavBar
// props:
// - items: {id, icon, label} 객체들의 배열
// - activeItem: 현재 선택된 메뉴의 id
// - onSelect: 메뉴 클릭 시 실행할 콜백 함수
export default function NavBar({ items, activeItem, onSelect }) {
  const navigate = useNavigate()

  const handleLogout = async () => {
    try {
      const response = await logOut()
      sessionStorage.removeItem('stationId')
      alert('로그아웃 성공')
      navigate('/login') // 성공 시 login 페이지로 이동
    } catch (error) {
      console.log('로그아웃 요청 실패: ', error)
    }
  }

  return (
    <nav aria-label="Navigation Bar" className="h-screen w-20 bg-gray-600">
      <div className="flex h-full w-full flex-col gap-4">
        {/* 로고 영역 (클릭 시 기본 상태로 이동: map) */}
        <div
          id="logo"
          className="flex h-20 w-full items-center justify-center text-gray-100"
          onClick={() => onSelect('map')}
        >
          로고
        </div>

        {/* 메뉴 리스트 */}
        <ul className="flex-1">
          {items.map((item) => {
            // 현재 항목이 선택된 상태인지 확인
            const isActive = activeItem === item.id

            return (
              <li key={item.id}>
                {/* 버튼을 눌렀을 때 onSelect 함수 실행 */}
                <button
                  onClick={() => onSelect(item.id)}
                  className={`group relative flex h-12 w-full items-center justify-center ${isActive ? 'text-primary' : 'text-gray-100'}`}
                  aria-current={isActive ? 'page' : undefined}
                >
                  <item.icon className="h-6 w-6" />

                  {/* 툴팁 (hover 시 보임) */}
                  <span className="bg-primary text-body-2 pointer-events-none absolute top-1/2 left-full z-50 mx-2 w-full -translate-y-1/2 scale-0 rounded-sm whitespace-nowrap text-gray-600 transition-all group-hover:scale-100">
                    {item.label}
                  </span>
                </button>
              </li>
            )
          })}
        </ul>

        {/* 로그아웃 버튼 */}
        <div
          id="logout"
          className="flex h-20 w-full items-center justify-center text-gray-100"
          onClick={handleLogout}
        >
          <LogOut className="h-6 w-6 text-gray-100" />
        </div>
      </div>
    </nav>
  )
}

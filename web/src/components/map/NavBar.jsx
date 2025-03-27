import React from 'react'

// NavBar
// props:
// - items: {id, icon, label} 객체들의 배열
// - activeItem: 현재 선택된 메뉴의 id
// - onSelect: 메뉴 클릭 시 실행할 콜백 함수
export default function NavBar({ items, activeItem, onSelect }) {
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
        <ul>
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
                  <span className="bg-primary text-body-2 pointer-events-none absolute top-1/2 left-full z-10 mx-2 w-full -translate-y-1/2 scale-0 rounded-sm whitespace-nowrap text-gray-600 transition-all group-hover:scale-100">
                    {item.label}
                  </span>
                </button>
              </li>
            )
          })}
        </ul>
      </div>
    </nav>
  )
}

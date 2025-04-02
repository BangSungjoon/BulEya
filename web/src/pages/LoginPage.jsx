import { useState, useRef, useEffect } from 'react'

function CustomDropdown({ selected, onSelect }) {
  const [isOpen, setIsOpen] = useState(false)
  const dropdownRef = useRef(null) // 드롭다운 외부 클릭 감지용

  const StationList = [
    { code: 222, name: '강남역' },
    { code: 2, name: '서울역' },
    { code: 3, name: '잠실역' },
  ]

  const selectedLabel = StationList.find((s) => s.code === selected)?.name || '역을 선택해주세요'

  // 외부 클릭 시 드롭다운 닫기
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [])

  return (
    <div className="relative w-full" ref={dropdownRef}>
      {/* 버튼 - 선택된 항목 */}
      <button
        onClick={() => setIsOpen((prev) => !prev)}
        className="flex w-full items-center justify-between rounded-lg bg-gray-500 p-3 text-left text-gray-100"
      >
        {selectedLabel}
        <svg
          className={`ml-2 inline h-4 w-4 text-gray-300 transition-transform duration-200 ${
            isOpen ? 'rotate-180' : ''
          }`}
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
        </svg>
      </button>

      {/* 드롭다운 메뉴 */}
      <ul
        className={`absolute z-10 mt-1 w-full overflow-hidden rounded-lg bg-white shadow-lg transition-all duration-200 ${
          isOpen ? 'scale-100 opacity-100' : 'pointer-events-none scale-95 opacity-0'
        }`}
      >
        {StationList.map((station) => (
          <li
            key={station.code}
            onClick={() => {
              onSelect(station.code)
              setIsOpen(false)
            }}
            className="hover:bg-primary cursor-pointer px-4 py-2 text-gray-800 hover:text-white"
          >
            {station.name}
          </li>
        ))}
      </ul>
    </div>
  )
}

export default function LoginPage() {
  const [selectedStation, setSelectedStation] = useState('')

  return (
    <div className="flex h-screen w-full items-center justify-center bg-gray-600">
      <div className="w-full flex-col p-20 text-gray-100 lg:w-[50%]">
        <div className="mb-20 flex w-full items-center text-4xl font-bold">
          {/* 로고/서비스 명 */}
          <div id="logo">
            {/* 로고 svg 넣기 */}
            <p>서비스명</p>
          </div>
        </div>

        <div className="flex w-full flex-1 flex-col gap-4">
          {/* 역사 선택 */}
          <div className="flex flex-col gap-1">
            <p className="text-sm text-gray-300">역 선택</p>
            <CustomDropdown selected={selectedStation} onSelect={setSelectedStation} />
          </div>

          {/* 비밀번호 입력 */}
          <div className="flex flex-col gap-1">
            <p className="text-sm text-gray-300">비밀번호</p>
            <input
              type="text"
              placeholder="비밀번호"
              className="rounded-lg bg-gray-500 p-3 text-white placeholder:text-gray-400"
            />
          </div>

          {/* 로그인 버튼 */}
          <button
            className="bg-primary text-h3 hover:bg-primary/80 h-12 w-full rounded-xl text-gray-600 transition-all duration-200 hover:-translate-y-0.5"
            disabled={!selectedStation}
          >
            로그인
          </button>
        </div>
      </div>
    </div>
  )
}

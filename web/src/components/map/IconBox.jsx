import CCTV from '@/assets/icons/CCTV.svg?react'
import Beacon from '@/assets/icons/Beacon.svg?react'
import Exit from '@/assets/icons/Exit.svg?react'

export default function IconBox({ selectedIcon, onSelect }) {
  const items = [
    { id: 'cctv', icon: CCTV, label: 'CCTV' },
    { id: 'beacon', icon: Beacon, label: '비콘' },
    { id: 'exit', icon: Exit, label: '출구' },
  ]

  return (
    <div className="absolute top-12 left-10 z-10 flex flex-row gap-2 rounded-full border border-gray-300 bg-gray-200 px-4 py-2">
      {items.map((item) => (
        <button
          key={item.id}
          onClick={() => onSelect(item.id)}
          className={`group flex flex-row items-center overflow-hidden rounded-full border border-gray-300 bg-gray-100 ${selectedIcon === item.id ? 'text-primary' : 'text-gray-100'} `}
        >
          <item.icon className="h-8 w-8" />
          {/* hover 중일 때만 설명 */}
          <span className="text-h3 h-0 w-0 origin-left scale-0 p-0 text-gray-600 transition-all duration-200 group-hover:h-fit group-hover:w-fit group-hover:scale-100 group-hover:pr-4 group-hover:pl-2">
            {item.label}
          </span>
        </button>
      ))}
    </div>
  )
}

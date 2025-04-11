export default function FloorNavigator({ floors, selected, onSelect }) {
  // 층 번호 변환 함수
  const formatFloorLabel = (floor) => {
    return floor >= 1000 ? `B${floor - 1000}` : `${floor}F`
  }

  return (
    <div className="absolute right-10 bottom-9 z-10 flex h-fit w-fit flex-col justify-center overflow-hidden rounded-2xl border border-gray-300 drop-shadow-md">
      {floors.map((floor) => (
        <button
          key={floor}
          onClick={() => onSelect(floor)}
          className={`text-h3 h-16 w-16 ${selected === floor ? 'bg-primary text-white' : 'bg-gray-200 text-gray-500'}`}
        >
          {formatFloorLabel(floor)}
        </button>
      ))}
    </div>
  )
}

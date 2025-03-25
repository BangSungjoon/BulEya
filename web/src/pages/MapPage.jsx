export default function MapPage({ mode }) {
  return (
    <div>{mode}</div>
    //   <div className="relative w-full h-full">
    //     {/* 실제 지도 */}
    //     <MapComponent />

    //     {/* add 모드일 때 모달 */}
    //     {mode === 'add' && <IconBoxModal />}

    //     {/* route 모드일 때 경로 편집 툴 활성화 */}
    //     {mode === 'route' && <RouteEditOverlay />}
    //   </div>
  )
}

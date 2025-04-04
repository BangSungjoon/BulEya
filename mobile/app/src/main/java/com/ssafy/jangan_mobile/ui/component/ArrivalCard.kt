package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.jangan_mobile.ui.theme.Body1
import com.ssafy.jangan_mobile.ui.theme.Headline
import kotlinx.coroutines.delay

@Composable
fun ArrivalCard(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsLeft by remember { mutableStateOf(3) }

    // ⏳ 3초 후 자동 닫힘
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
        onDismiss()
    }

    Box(
        modifier = Modifier
            .width(380.dp)
            .height(183.dp)
            .background(color = Color(0xFF1B1B1D), shape = RoundedCornerShape(40.dp))
            .padding(start = 10.dp, top = 12.dp, end = 10.dp, bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "경로 안내가 완료되었습니다.",
                    style = Headline.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center)
                    )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "안전한 화재 대피를 기원합니다.",
                    style = Body1.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center)
                    )
            }

            // ✅ 확인 버튼 (초 포함)
            Box(
                modifier = Modifier
                    .width(360.dp)
                    .height(72.dp)
                    .background(color = Color(0xFF8AEA52), shape = RoundedCornerShape(60.dp))
                    .padding(start = 32.dp, top = 22.dp, end = 32.dp, bottom = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "확인",
                        style = Headline.copy(
                            color = Color.Black,
                            textAlign = TextAlign.Center)
                    )

                    // 🕒 타이머 숫자
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.Black, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = secondsLeft.toString(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


//@Composable
//fun EscapeRouteMapScreen(
//    navController: NavController,
//    viewModel: EscapeRouteViewModel = viewModel(),
//    mapViewModel: MapViewModel = hiltViewModel()
//) {
//    val context = LocalContext.current
//    val mapView = remember { MapView(context) }
//
//    val fireNotificationDto: FireNotificationDto? by FireNotificationStore.fireNotificationDto.observeAsState()
//    val currentLocationCode by FireNotificationStore.currentLocationBeaconCode.observeAsState()
//    val routePoints by viewModel.route.observeAsState(emptyList())
//    val imageUrl by mapViewModel.mapImageUrl.collectAsState()
//
//    val showRoute = remember { mutableStateOf(false) }
//
//    val selectedFloor = remember { mutableStateOf("B1") }
//
//    //지도에 사용할 마커 및 선 관리
//    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }
//    val polylineManager = remember { mutableStateOf<PolylineAnnotationManager?>(null) }
//
//    // 이미지 위치 계산 관련 설정
//    val imageWidth = 5000
//    val imageHeight = 7800
//    val aspectRatio = imageWidth.toDouble() / imageHeight.toDouble()
//    val coordinateHeight = 60.0
//    val coordinateWidth = coordinateHeight * aspectRatio
//    val top = coordinateHeight / 2
//    val bottom = -coordinateHeight / 2
//    val left = -coordinateWidth / 2
//    val right = coordinateWidth / 2
//
//
//    fun convertPixelToLngLat(x: Int, y: Int): List<Double> {
//        val lng = left + (x.toDouble() / imageWidth) * (right - left)
//        val lat = top - (y.toDouble() / imageHeight) * (top - bottom)
//        return listOf(lng, lat)
//    }
//
//
//    val focusMval isCardVisible = remember { mutableStateOf(true) }anager = LocalFocusManager.current
//    val myLocation by viewModel.myLocation.observeAsState()
//
//
//    // 문자열 층을 API 코드로 변환
//    fun floorStringToCode(floor: String): Int? {
//        return when (floor) {
//            "B1" -> 1001
//            "B2" -> 1002
//            "B3" -> 1003
//            else -> null
//        }
//    }
//
//    // 🔥 실제 화재 발생한 비콘 중 첫 번째 찾기
//    val targetBeaconDto = fireNotificationDto
//        ?.beaconNotificationDtos
//        ?.firstOrNull { it.isNewFire == 1 }
//
//    // 내위치 로드
//    LaunchedEffect(currentLocationCode) {
//        Log.d("MyLocation", "📡 내 위치 요청 시작: $currentLocationCode")
//        currentLocationCode?.toInt()?.let { beaconCode ->
//            viewModel.fetchMyLocation(222, beaconCode)
//        }
//    }
//
//    // 이미지 로드
//    LaunchedEffect(Unit) {
//        Log.d("EscapeRouteScreen", "✅ EscapeRouteMapScreen 진입")
//        mapViewModel.fetchMapImage("222")
//    }
//
//    // 지도 스타일 및 마커 갱신
//    LaunchedEffect(fireNotificationDto, currentLocationCode, routePoints, myLocation, imageUrl) {
//        Log.d("EscapeRouteScreen", "✅ 지도 갱신 조건 발생")
//        if (imageUrl != null) {
//            mapView.mapboxMap.loadStyle(
//                style {
//                    +backgroundLayer("background") {
//                        backgroundColor("#EFF0F1")
//                    }
//                    +image(
//                        "marker-icon",
//                        BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)
//                    ) {}
//                    +image(
//                        "fire-icon",
//                        BitmapFactory.decodeResource(context.resources, R.drawable.fire)
//                    ) {}
//                    +image(
//                        "destination-icon",
//                        BitmapFactory.decodeResource(context.resources, R.drawable.goal)
//                    ) {}
//                    +imageSource("custom-map") {
//                        url(imageUrl!!)
//                        coordinates(
//                            listOf(
//                                convertPixelToLngLat(0, 0),
//                                convertPixelToLngLat(imageWidth, 0),
//                                convertPixelToLngLat(imageWidth, imageHeight),
//                                convertPixelToLngLat(0, imageHeight),
//                            )
//                        )
//                    }
//                    +rasterLayer("custom-map-layer", "custom-map") {
//                        rasterOpacity(1.0)
//                    }
//                }
//            ) {
//                val center = convertPixelToLngLat(imageWidth / 2, imageHeight / 2)
//                mapView.getMapboxMap().setCamera(
//                    CameraOptions.Builder()
//                        .center(Point.fromLngLat(center[0], center[1]))
//                        .zoom(3.0)
//                        .build()
//                )
//
//                mapView.mapboxMap.setBounds(
//                    CameraBoundsOptions.Builder()
//                        .bounds(
//                            CoordinateBounds(
//                                Point.fromLngLat(left, bottom),
//                                Point.fromLngLat(right, top)
//                            )
//                        )
//                        .build()
//                )
//
//
//                mapView.gestures.pitchEnabled = true
//                mapView.gestures.rotateEnabled = true
//                mapView.gestures.doubleTapToZoomInEnabled = true
//
//                // 기존 마커 및 선 삭제
//                pointAnnotationManager.value?.deleteAll()
//                polylineManager.value?.deleteAll()
//
//
//                val annotationApi = mapView.annotations
//                pointAnnotationManager.value = annotationApi.createPointAnnotationManager()
//                polylineManager.value = annotationApi.createPolylineAnnotationManager()
//
//
//                val selectedFloorCode = floorStringToCode(selectedFloor.value)
//
//
//                // 🔥 화재 위치
//                Log.d(
//                    "EscapeRouteScreen",
//                    "🔥 fireNotificationDto = $fireNotificationDto $fireNotificationDto?.stationId $fireNotificationDto?.stationName"
//                )
//                fireNotificationDto?.beaconNotificationDtos?.forEach {
//                    Log.d("EscapeRouteScreen", "🔥 beacon = $it")
//                    Log.d(
//                        "EscapeRouteScreen",
//                        "🔥 Beacon floor=${it.floor}, coord=(${it.coordX}, ${it.coordY})"
//                    )
//                }
//                Log.d("EscapeRouteScreen", "📍 currentLocationCode = $currentLocationCode")
//                Log.d(
//                    "EscapeRouteScreen",
//                    "✅ selectedFloor = ${selectedFloor.value}, code = $selectedFloorCode"
//                )
//
//                fireNotificationDto?.beaconNotificationDtos
////                    ?.filter { it.floor == selectedFloorCode }
//                    ?.forEach { beacon ->
//                        val fireMarker = PointAnnotationOptions()
//                            .withPoint(Point.fromLngLat(beacon.coordX, beacon.coordY))
//                            .withIconImage("fire-icon")
//                            .withIconSize(0.25)
//                        pointAnnotationManager.value?.create(fireMarker)
//                    }
//
//
//                // 🧍 내 위치
//                myLocation?.let { beacon ->
//                    val myMarker = PointAnnotationOptions()
//                        .withPoint(Point.fromLngLat(beacon.coordX, beacon.coordY))
//                        .withIconImage("marker-icon")
//                        .withIconSize(0.5)
//                    pointAnnotationManager.value?.create(myMarker)
//                }
//
//                // 경로 안내
//                if (showRoute.value && routePoints.size >= 2) {
//                    Log.d("EscapeRoute", "🔗 경로 선 연결 시작")
//                    for (i in 0 until routePoints.size - 1) {
//                        val start = routePoints[i]
//                        val end = routePoints[i + 1]
//
//                        // ✅ 층이 다르면 건너뛴다 (선택사항)
////                        if (start.floor != end.floor) continue
//
//                        val polyline = PolylineAnnotationOptions()
//                            .withPoints(listOf(
//                                Point.fromLngLat(start.x, start.y),
//                                Point.fromLngLat(end.x, end.y)
//                            ))
//                            .withLineColor("#00FF00")
//                            .withLineWidth(6.0)
//
//                        polylineManager.value?.create(polyline)
//                    }
//                    // 마지막 지점 마커 (목적지 마커)
//                    val destination = routePoints.last()
//                    val endMarker = PointAnnotationOptions()
//                        .withPoint(Point.fromLngLat(destination.x, destination.y))
//                        .withIconImage("destination-icon") // 👈 원하는 아이콘 등록 필요
//                        .withIconSize(0.5) // 적절한 크기로 설정
//                    pointAnnotationManager.value?.create(endMarker)
//                }
//            }
//        }
//    }
//
//    // UI 구성
//    Box(
//        modifier = Modifier.fillMaxSize()
//            .clickable {
//                if (isCardVisible.value) {
//                    Log.d("EscapeRouteUI", "🛑 화면 클릭 → 모달 닫기")
//                    isCardVisible.value = false
//                }
//            }) {
//        // 1. 지도 뷰 (배경 역할)
//        AndroidView(factory = { mapView })
//
//        // 2. 전체 오버레이
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column {
//                // 🔥 화재 상태 모달
//                if (isCardVisible.value && targetBeaconDto?.imageUrl?.isNotEmpty() == true) {
//                    Log.d("EscapeRouteUI", "✅ FireNotificationCard 호출됨")
//                    FireNotificationCard(
//                        gateName = targetBeaconDto.beaconName ?: "알 수 없음",
//                        imageUrl = targetBeaconDto.imageUrl ?: ""
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                }
//                Box(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    // ⬆️ 층 선택 컴포넌트
//                    FloorSelector(
//                        selectedFloor = selectedFloor.value,
//                        onFloorSelected = { selectedFloor.value = it },
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(start = 16.dp, bottom = 100.dp)
//                    )
//
//                    EvacuationButton(
//                        onClick = {
//                            currentLocationCode?.let { code ->
//                                viewModel.fetchEscapeRoute(222, code)
//                                showRoute.value = true
//                            }
//                        },
//                        modifier = Modifier
//                            .align(Alignment.BottomCenter)
//                            .padding(bottom = 50.dp)
//                    )
//                }
//            }
//        }
//    }
//}



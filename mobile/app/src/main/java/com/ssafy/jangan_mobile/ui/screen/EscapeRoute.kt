package com.ssafy.jangan_mobile.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun planBScreen(
    navController: NavController,
    stationId: Int,
    beaconCode: Int
) {
    // viewModel로 API 호출 + map 경로 그리기
    // UI: 층 선택, "안내 종료하기" 버튼 등 포함
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
//    val myLocation by viewModel.myLocation.observeAsState()
//
//    val showRoute = remember { mutableStateOf(false) }
//    val selectedFloor = remember { mutableStateOf("B1") }
//    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }
//    val polylineManager = remember { mutableStateOf<PolylineAnnotationManager?>(null) }
//
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
//    fun convertPixelToLngLat(x: Int, y: Int): List<Double> {
//        val lng = left + (x.toDouble() / imageWidth) * (right - left)
//        val lat = top - (y.toDouble() / imageHeight) * (top - bottom)
//        return listOf(lng, lat)
//    }
//
//    // 마커 관리
//    val fireMarkers = remember { mutableStateMapOf<Int, MutableList<PointAnnotation>>() } // 층별 저장
//    val myLocationAnnotation = remember { mutableStateOf<PointAnnotation?>(null) }
//    val goalMarker = remember { mutableStateOf<PointAnnotation?>(null) }
//
//    val showArrivalCard = remember { mutableStateOf(false) }
//    val isGuiding = remember { mutableStateOf(false) }
//    val isCardVisible = remember { mutableStateOf(true) }
//
//    // 층 코드 변환
//    fun floorStringToCode(floor: String): Int? = when (floor) {
//        "B1" -> 1001
//        "B2" -> 1002
//        "B3" -> 1003
//        else -> null
//    }
//
////     이미지 로드
//    LaunchedEffect(Unit) {
//        Log.d("EscapeRouteScreen", "✅ EscapeRouteMapScreen 진입")
//        mapViewModel.fetchMapImage("222", "1001")
//    }
//
//    // 내 위치 요청
//    LaunchedEffect(currentLocationCode) {
//        currentLocationCode?.toInt()?.let {
//            Log.d("EscapeRouteScreen", "✅ 내 위치 요청")
//            viewModel.fetchMyLocation(222, it)
//        }
//    }
//
//    // 층 변경 시 이미지 요청
//    LaunchedEffect(selectedFloor.value) {
//        val floorCode = floorStringToCode(selectedFloor.value)?.toString() ?: return@LaunchedEffect
//        mapViewModel.fetchMapImage("222", floorCode)
//    }
//
//    // 지도 초기화
//    LaunchedEffect(imageUrl) {
//        if (imageUrl != null) {
//            mapView.mapboxMap.loadStyle(
//                style {
//                    +backgroundLayer("background") { backgroundColor("#EFF0F1") }
//                    +image("marker-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)) {}
//                    +image("fire-icon", BitmapFactory.decodeResource(context.resources, R.drawable.fire)) {}
//                    +image("destination-icon", BitmapFactory.decodeResource(context.resources, R.drawable.goal)) {}
//                    +imageSource("custom-map") {
//                        url(imageUrl!!)
//                        coordinates(
//                            listOf(
//                                convertPixelToLngLat(0, 0),
//                                convertPixelToLngLat(imageWidth, 0),
//                                convertPixelToLngLat(imageWidth, imageHeight),
//                                convertPixelToLngLat(0, imageHeight)
//                            )
//                        )
//                    }
//                    +rasterLayer("custom-map-layer", "custom-map") { rasterOpacity(1.0) }
//                }
//            ) {
//                val center = convertPixelToLngLat(imageWidth / 2, imageHeight / 2)
//                mapView.mapboxMap.setCamera(CameraOptions.Builder().center(Point.fromLngLat(center[0], center[1])).zoom(5.0).build())
//                mapView.mapboxMap.setBounds(CameraBoundsOptions.Builder().bounds(CoordinateBounds(Point.fromLngLat(left, bottom), Point.fromLngLat(right, top))).build())
//
//                val annotationApi = mapView.annotations
//                pointAnnotationManager.value = annotationApi.createPointAnnotationManager()
//                polylineManager.value = annotationApi.createPolylineAnnotationManager()
//            }
//        }
//    }
//
//    // 🔥 화재 마커
//    LaunchedEffect(fireNotificationDto, selectedFloor.value) {
//        val floorCode = floorStringToCode(selectedFloor.value) ?: return@LaunchedEffect
//        val manager = pointAnnotationManager.value ?: return@LaunchedEffect
//        val currentMarkers = fireMarkers.getOrPut(floorCode) { mutableListOf() }
//
//        if (currentMarkers.isEmpty()) {
//            val beacons = fireNotificationDto?.beaconNotificationDtos?.filter { it.floor == floorCode } ?: return@LaunchedEffect
//            beacons.forEach { beacon ->
//                val marker = PointAnnotationOptions()
//                    .withPoint(Point.fromLngLat(beacon.coordX, beacon.coordY))
//                    .withIconImage("fire-icon")
//                    .withIconSize(0.25)
//                currentMarkers += manager.create(marker)
//            }
//        }
//    }
//
//    // 📍 내 위치 마커
//    LaunchedEffect(myLocation, selectedFloor.value) {
//        val manager = pointAnnotationManager.value ?: return@LaunchedEffect
//        myLocationAnnotation.value?.let { manager.delete(it) }
//
//        val floorCode = floorStringToCode(selectedFloor.value)
//        if (myLocation != null && myLocation!!.floor == floorCode) {
//            val marker = PointAnnotationOptions()
//                .withPoint(Point.fromLngLat(myLocation!!.coordX, myLocation!!.coordY))
//                .withIconImage("marker-icon")
//                .withIconSize(0.3)
//            myLocationAnnotation.value = manager.create(marker)
//        }
//    }
//
//    // 📌 경로 & 도착 마커
//    LaunchedEffect(routePoints, showRoute.value, selectedFloor.value) {
//        val manager = pointAnnotationManager.value ?: return@LaunchedEffect
//        val polyline = polylineManager.value ?: return@LaunchedEffect
//        val floorCode = floorStringToCode(selectedFloor.value) ?: return@LaunchedEffect
//
//        polyline.deleteAll()
//        goalMarker.value?.let { manager.delete(it) }
//
//        if (!showRoute.value || routePoints.isEmpty()) return@LaunchedEffect
//
//        val destination = routePoints.first()
//        val current = routePoints.last()
//
//        // 도착 or 포인트 1개면 경로 선 그리지 않음
//        val isArrived = destination == current
//        if (isArrived || routePoints.size == 1) {
//            showArrivalCard.value = isArrived
//            return@LaunchedEffect // ❌ 경로 그리기 생략
//        }
//
//        // 👉 여기서만 경로 선 그리기
//        for (i in 0 until routePoints.size - 1) {
//            val s = routePoints[i]
//            val e = routePoints[i + 1]
//            if (s.floor == floorCode && e.floor == floorCode) {
//                polyline.create(
//                    PolylineAnnotationOptions()
//                        .withPoints(listOf(Point.fromLngLat(s.x, s.y), Point.fromLngLat(e.x, e.y)))
//                        .withLineColor("#8AEA52")
//                        .withLineWidth(6.0)
//                )
//            }
//        }
//
//        if (destination.floor == floorCode) {
//            val marker = PointAnnotationOptions()
//                .withPoint(Point.fromLngLat(destination.x, destination.y))
//                .withIconImage("destination-icon")
//                .withIconSize(0.4)
//            goalMarker.value = manager.create(marker)
//        }
//    }
//
////        if (routePoints.size > 1 && destination != current) {
////            for (i in 0 until routePoints.size - 1) {
////                val s = routePoints[i]
////                val e = routePoints[i + 1]
////                if (s.floor == floorCode && e.floor == floorCode) {
////                    polyline.create(
////                        PolylineAnnotationOptions()
////                            .withPoints(listOf(Point.fromLngLat(s.x, s.y), Point.fromLngLat(e.x, e.y)))
////                            .withLineColor("#8AEA52")
////                            .withLineWidth(6.0)
////                    )
////                }
////            }
////        } else if (destination == current) {
////            showArrivalCard.value = true
////        }
////    }
//
//    // 도착 카드
//    LaunchedEffect(showArrivalCard.value) {
//        if (showArrivalCard.value) {
//          kotlinx.coroutines.delay(10000)
//            showArrivalCard.value = false
//            isGuiding.value = false
//        }
//    }
//
//    // 🧭 안내 버튼 클릭
//    fun onEvacuationClick() {
//        if (isGuiding.value) {
//            showRoute.value = false
//            isGuiding.value = false
//            polylineManager.value?.deleteAll()
//            goalMarker.value?.let { pointAnnotationManager.value?.delete(it) }
//        } else {
//            currentLocationCode?.let {
//                viewModel.fetchEscapeRoute(222, it)
//                showRoute.value = true
//                isGuiding.value = true
//            }
//        }
//    }
//
//    //// UI 구성
//Box(
//    modifier = Modifier
//        .fillMaxSize()
//        .clickable {
//            if (isCardVisible.value) {
//                Log.d("EscapeRouteUI", "🛑 화면 클릭 → 화재 모달 닫기")
//                isCardVisible.value = false
//            }
//        }
//) {
//    // 1. 지도 배경
//    AndroidView(factory = { mapView })
//
//    // ✅ 도착 알림 카드
//    if (showArrivalCard.value) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            ArrivalCard(
//                onDismiss = {
//                    showArrivalCard.value = false
//                    pointAnnotationManager.value?.deleteAll()
//                },
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(top = 24.dp)
//            )
//        }
//    }
//
//    // 2. 오버레이 전체 (층 버튼, 안내 버튼 등)
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        Spacer(modifier = Modifier.height(12.dp)) // 상단 공간
//
//        // ✅ 버튼 영역
//        Box(modifier = Modifier.fillMaxWidth()) {
//            Column(
//                modifier = Modifier
//                    .align(Alignment.BottomStart)
//                    .padding(start = 16.dp,
//                        bottom = 50.dp),
//                horizontalAlignment = Alignment.Start
//            ) {
//                FloorSelector(
//                    selectedFloor = selectedFloor.value,
//                    onFloorSelected = { selectedFloor.value = it }
//                )
//                if (!showArrivalCard.value) {
//                    Spacer(modifier = Modifier.height(16.dp))
//                    EvacuationButton(
//                        isGuiding = isGuiding.value,
//                        onClick = {
//                            if (isGuiding.value) {
//                                // ✅ 안내 종료 처리
//                                isGuiding.value = false
//                                showRoute.value = false
//                                polylineManager.value?.deleteAll()
//                                goalMarker.value?.let { pointAnnotationManager.value?.delete(it) }
//                                myLocationAnnotation.value?.let { pointAnnotationManager.value?.delete(it) }
//                            } else {
//                                // ✅ 안내 시작
//                                currentLocationCode?.let { code ->
//                                    viewModel.fetchEscapeRoute(222, code)
//                                    showRoute.value = true
//                                    isGuiding.value = true
//                                }
//                            }
//                        }
//                    )
//                }
//                }
//        }
//    }
//
//    val targetBeaconDto = fireNotificationDto?.beaconNotificationDtos?.firstOrNull()
//    // ✅ 🔥 화재 모달 오버레이 (최상단 분리)
//        if (isCardVisible.value && targetBeaconDto?.imageUrl?.isNotEmpty() == true) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onTap = {
//                                Log.d("FireModal", "🛑 바깥 탭 → 모달 닫기")
//                                isCardVisible.value = false
//                            }
//                        )
//                    }
//            ) {
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .padding(top = 60.dp)
//                        .clickable(
//                            interactionSource = remember { MutableInteractionSource() },
//                            indication = null
//                        ) { /* 내부 탭 무시 */ }
//                ) {
//                    FireNotificationCard(
//                        gateName = targetBeaconDto.beaconName ?: "알 수 없음",
//                        imageUrl = targetBeaconDto.imageUrl ?: ""
//                    )
//                }
//            }
//        }
//    }
//}

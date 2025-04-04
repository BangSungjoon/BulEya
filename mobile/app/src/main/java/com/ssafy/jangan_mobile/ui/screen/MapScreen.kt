package com.ssafy.jangan_mobile.ui.screen

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.backgroundLayer
import com.mapbox.maps.extension.style.layers.generated.rasterLayer
import com.mapbox.maps.extension.style.sources.generated.imageSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.gestures.gestures
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.service.dto.BeaconNotificationDto
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto
import com.ssafy.jangan_mobile.store.FireNotificationStore
import com.ssafy.jangan_mobile.ui.component.ArrivalCard
import com.ssafy.jangan_mobile.ui.component.EvacuationButton
import com.ssafy.jangan_mobile.ui.component.FireNotificationCard
import com.ssafy.jangan_mobile.ui.component.FloorSelector
import com.ssafy.jangan_mobile.ui.viewmodel.MapViewModel
import com.ssafy.jangan_mobile.viewmodel.EscapeRouteViewModel
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource

@Composable
fun EscapeRouteMapScreen(
    navController: NavController,
    viewModel: EscapeRouteViewModel = viewModel(),
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val fireNotificationDto: FireNotificationDto? by FireNotificationStore.fireNotificationDto.observeAsState()
    val currentLocationCode by FireNotificationStore.currentLocationBeaconCode.observeAsState()
    val routePoints by viewModel.route.observeAsState(emptyList())
    val imageUrl by mapViewModel.mapImageUrl.collectAsState()
    val myLocation by viewModel.myLocation.observeAsState()

    val showRoute = remember { mutableStateOf(false) }
    val selectedFloor = remember { mutableStateOf("B1") }
    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }
    val polylineManager = remember { mutableStateOf<PolylineAnnotationManager?>(null) }

    val imageWidth = 5000
    val imageHeight = 7800
    val aspectRatio = imageWidth.toDouble() / imageHeight.toDouble()
    val coordinateHeight = 60.0
    val coordinateWidth = coordinateHeight * aspectRatio
    val top = coordinateHeight / 2
    val bottom = -coordinateHeight / 2
    val left = -coordinateWidth / 2
    val right = coordinateWidth / 2

    fun convertPixelToLngLat(x: Int, y: Int): List<Double> {
        val lng = left + (x.toDouble() / imageWidth) * (right - left)
        val lat = top - (y.toDouble() / imageHeight) * (top - bottom)
        return listOf(lng, lat)
    }

    val isCardVisible = remember { mutableStateOf(true) }

    // 경로 도착 상태 변수
    val hasArrived = remember { mutableStateOf(false) }
    val showArrivalCard = remember { mutableStateOf(false) }

    val goalMarker = remember { mutableStateOf<PointAnnotation?>(null) }

    val fireMarkers = remember { mutableStateListOf<PointAnnotation>() }

    // 이미지 로드
    LaunchedEffect(Unit) {
        Log.d("EscapeRouteScreen", "✅ EscapeRouteMapScreen 진입")
        mapViewModel.fetchMapImage("222", "1001")
    }

    // 내 위치 요청
    LaunchedEffect(currentLocationCode) {
        currentLocationCode?.toInt()?.let {
            Log.d("EscapeRouteScreen", "✅ 내 위치 요청")
            viewModel.fetchMyLocation(222, it)
        }
    }

    // 문자열 층을 API 코드로 변환
    fun floorStringToCode(floor: String): Int? {
        return when (floor) {
            "B1" -> 1001
            "B2" -> 1002
            "B3" -> 1003
            else -> null
        }
    }

    // 층 변경 시 이미지 요청
    LaunchedEffect(selectedFloor.value) {
        val floorCode = floorStringToCode(selectedFloor.value)?.toString() ?: return@LaunchedEffect
        mapViewModel.fetchMapImage("222", floorCode)
    }

    // 🔥 실제 화재 발생한 비콘 중 첫 번째 찾기
    val targetBeaconDto = fireNotificationDto
        ?.beaconNotificationDtos
        ?.firstOrNull { it.isNewFire == 1 }

    // 지도 초기 스타일 설정 (최초 한 번만 실행)
    LaunchedEffect(imageUrl) {
        if (imageUrl != null) {
            mapView.mapboxMap.loadStyle(
                style {
                    +backgroundLayer("background") {
                        backgroundColor("#EFF0F1")
                    }
                    +image(
                        "marker-icon",
                        BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)
                    ) {}
                    +image(
                        "fire-icon",
                        BitmapFactory.decodeResource(context.resources, R.drawable.fire)
                    ) {}
                    +image(
                        "destination-icon",
                        BitmapFactory.decodeResource(context.resources, R.drawable.goal)
                    ) {}
                    +imageSource("custom-map") {
                        url(imageUrl!!)
                        coordinates(
                            listOf(
                                convertPixelToLngLat(0, 0),
                                convertPixelToLngLat(imageWidth, 0),
                                convertPixelToLngLat(imageWidth, imageHeight),
                                convertPixelToLngLat(0, imageHeight)
                            )
                        )
                    }
                    +rasterLayer("custom-map-layer", "custom-map") {
                        rasterOpacity(1.0)
                    }
                }
            ) {
                val center = convertPixelToLngLat(imageWidth / 2, imageHeight / 2)
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(center[0], center[1]))
                        .zoom(3.0)
                        .build()
                )
                mapView.mapboxMap.setBounds(
                    CameraBoundsOptions.Builder()
                        .bounds(
                            CoordinateBounds(
                                Point.fromLngLat(left, bottom),
                                Point.fromLngLat(right, top)
                            )
                        )
                        .build()
                )

                val annotationApi = mapView.annotations
                pointAnnotationManager.value = annotationApi.createPointAnnotationManager()
                polylineManager.value = annotationApi.createPolylineAnnotationManager()
            }
        }
    }

    // 🔁 내 위치 마커만 따로 관리
    LaunchedEffect(myLocation, selectedFloor.value) {
        pointAnnotationManager.value?.deleteAll()
        val selectedFloorCode = floorStringToCode(selectedFloor.value)
        myLocation?.let { beacon ->
            if (beacon.floor == selectedFloorCode) {
                val marker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(beacon.coordX, beacon.coordY))
                    .withIconImage("marker-icon")
                    .withIconSize(0.5)
                pointAnnotationManager.value?.create(marker)
            }
        }
    }

    // 화재 위치만 따로 관리
    LaunchedEffect(fireNotificationDto, selectedFloor.value, showArrivalCard.value) {
        if (showArrivalCard.value) {
            Log.d("FireMarker", "✅ 도착 후 화재 마커 표시 생략")
            return@LaunchedEffect
        }

        val selectedFloorCode = floorStringToCode(selectedFloor.value)
        Log.d("FireMarker", "🔥 LaunchedEffect 호출됨. 현재 층: $selectedFloorCode")
        pointAnnotationManager.value?.let { manager ->
            manager.deleteAll()

            val fireBeacons = fireNotificationDto?.beaconNotificationDtos
                ?.filter { it.floor == selectedFloorCode } ?: run {
                Log.w("FireMarker", "⚠️ fireNotificationDto가 null이거나 해당 층의 화재 없음")
                return@let
            }
            fireBeacons.forEachIndexed { index, beacon ->
                Log.d(
                    "FireMarker",
                    "🔥 [$index] 화재 마커 생성 → coord=(${beacon.coordX}, ${beacon.coordY}), floor=${beacon.floor}, beaconCode=${beacon.beaconCode}"
                )
                val marker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(beacon.coordX, beacon.coordY))
                    .withIconImage("fire-icon")
                    .withIconSize(0.25)
                val fireMarker = manager.create(marker)

                // ✅ 마커 클릭 이벤트 등록
                manager.addClickListener { clicked ->
                    if (clicked == fireMarker) {
                        Log.d("FireMarker", "🔥 화재 마커 클릭됨! → 모달 다시 열기")
                        isCardVisible.value = true
                        true
                    } else false
                }
            }
        }
    }

    // 🔁 경로 표시도 분리해서 관리
    LaunchedEffect(routePoints, showRoute.value, selectedFloor.value) {
        val selectedFloorCode = floorStringToCode(selectedFloor.value)


        // 경로 숨기기거나 포인트 부족할 경우 라인 & 마커 모두 삭제
        if (!showRoute.value || routePoints.isEmpty()) {
            polylineManager.value?.deleteAll()
            pointAnnotationManager.value?.deleteAll()
            return@LaunchedEffect
        }
        // 라인만 지우기 (routePoints가 1개인 경우)
        if (!showRoute.value || routePoints.size == 1) {
            // ✅ 무조건 routePoints[0]에 목적지 마커 표시
            val destination = routePoints.first()
            Log.d("EscapeRouteMap", "📍 목적지 마커 추가: (${destination.x}, ${destination.y})")


            if (destination.floor == selectedFloorCode) {
                val endMarker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(destination.x, destination.y))
                    .withIconImage("destination-icon")
                    .withIconSize(0.5)
                pointAnnotationManager.value?.create(endMarker)
            }
            // ✅ 내 위치 마커 (routePoints.last())
            val currentPosition = routePoints.last()
            if (currentPosition.floor == selectedFloorCode) {
                Log.d("EscapeRouteMap", "📍 내위치 마커 추가: (${currentPosition.x}, ${currentPosition.y})")
                val myLocationMarker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(currentPosition.x, currentPosition.y))
                    .withIconImage("marker-icon") // 내 위치 아이콘
                    .withIconSize(0.5)
                pointAnnotationManager.value?.create(myLocationMarker)
            } else {
                Log.d(
                    "EscapeRouteMap",
                    "⚠️ 목적지 층(${destination.floor})이 현재 선택된 층($selectedFloorCode)과 다름"
                )
            }

            // ✅ 도착 여부: 객체 값 일치로만 판단
            if (destination.floor == currentPosition.floor &&
                destination.x == currentPosition.x &&
                destination.y == currentPosition.y
            ) {
                Log.d("EscapeRouteMap", "📍 도착")
                if (!showArrivalCard.value) {
                    Log.d("EscapeRouteMap", "🎉 목적지 도착 (좌표 동일) → 안내 카드 표시")
                    showArrivalCard.value = true
                }
            } else {
                Log.w(
                    "EscapeRouteMap",
                    "❌ 도착 아님 → destination=(${destination.x}, ${destination.y}, floor=${destination.floor}) | " +
                            "current=(${currentPosition.x}, ${currentPosition.y}, floor=${currentPosition.floor})"
                )
            }
            return@LaunchedEffect
        }


        if (showRoute.value && routePoints.size >= 2) {
            Log.d("EscapeRouteMap", "🟩 전체 경로 좌표:")
            routePoints.forEachIndexed { index, point ->
                Log.d("EscapeRouteMap", "[$index] (${point.x}, ${point.y}) on floor ${point.floor}")
            }
            polylineManager.value?.deleteAll()
            pointAnnotationManager.value?.deleteAll()

            for (i in 0 until routePoints.size - 1) {
                val start = routePoints[i]
                val end = routePoints[i + 1]

                if (start.floor != selectedFloorCode || end.floor != selectedFloorCode) continue

                val polyline = PolylineAnnotationOptions()
                    .withPoints(
                        listOf(
                            Point.fromLngLat(start.x, start.y),
                            Point.fromLngLat(end.x, end.y)
                        )
                    )
                    .withLineColor("#8AEA52")
                    .withLineWidth(6.0)
                polylineManager.value?.create(polyline)
            }

            // ✅ 무조건 routePoints[0]에 목적지 마커 표시
            val destination = routePoints.first()
            Log.d("EscapeRouteMap", "📍 목적지 마커 추가: (${destination.x}, ${destination.y})")


            if (destination.floor == selectedFloorCode) {
                val endMarker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(destination.x, destination.y))
                    .withIconImage("destination-icon")
                    .withIconSize(0.5)
                pointAnnotationManager.value?.create(endMarker)
            }
            // ✅ 내 위치 마커 (routePoints.last())
            val currentPosition = routePoints.last()
            if (currentPosition.floor == selectedFloorCode) {
                Log.d("EscapeRouteMap", "📍 내위치 마커 추가: (${currentPosition.x}, ${currentPosition.y})")
                val myLocationMarker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(currentPosition.x, currentPosition.y))
                    .withIconImage("marker-icon") // 내 위치 아이콘
                    .withIconSize(0.5)
                pointAnnotationManager.value?.create(myLocationMarker)
            } else {
                Log.d(
                    "EscapeRouteMap",
                    "⚠️ 목적지 층(${destination.floor})이 현재 선택된 층($selectedFloorCode)과 다름"
                )
            }

            // ✅ 도착 여부: 객체 값 일치로만 판단
            if (destination.floor == currentPosition.floor &&
                destination.x == currentPosition.x &&
                destination.y == currentPosition.y
            ) {
                Log.d("EscapeRouteMap", "📍 도착")
                if (!showArrivalCard.value) {
                    Log.d("EscapeRouteMap", "🎉 목적지 도착 (좌표 동일) → 안내 카드 표시")
                    showArrivalCard.value = true
                }
            } else {
                    Log.w(
                        "EscapeRouteMap",
                        "❌ 도착 아님 → destination=(${destination.x}, ${destination.y}, floor=${destination.floor}) | " +
                                "current=(${currentPosition.x}, ${currentPosition.y}, floor=${currentPosition.floor})"
                    )
                }
        }
    }


    // UI 구성
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (isCardVisible.value) {
                    Log.d("EscapeRouteUI", "🛑 화면 클릭 → 화재 모달 닫기")
                    isCardVisible.value = false
                }
            }
    ) {
        // 1. 지도 배경
        AndroidView(factory = { mapView })

        // ✅ 도착 알림 카드
        if (showArrivalCard.value) {
            ArrivalCard(
                onDismiss = {
                    showArrivalCard.value = false
                    pointAnnotationManager.value?.deleteAll()
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            )
        }

        // 2. 오버레이 전체 (층 버튼, 안내 버튼 등)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp)) // 상단 공간

            // ✅ 버튼 영역
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 50.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    FloorSelector(
                        selectedFloor = selectedFloor.value,
                        onFloorSelected = { selectedFloor.value = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    EvacuationButton(
                        onClick = {
                            currentLocationCode?.let { code ->
                                viewModel.fetchEscapeRoute(222, code)
                                showRoute.value = true
                            }
                        }
                    )
                }
            }
        }
        // ✅ 🔥 화재 모달 오버레이 (최상단 분리)
            if (isCardVisible.value && targetBeaconDto?.imageUrl?.isNotEmpty() == true) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    Log.d("FireModal", "🛑 바깥 탭 → 모달 닫기")
                                    isCardVisible.value = false
                                }
                            )
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 60.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { /* 내부 탭 무시 */ }
                    ) {
                        FireNotificationCard(
                            gateName = targetBeaconDto.beaconName ?: "알 수 없음",
                            imageUrl = targetBeaconDto.imageUrl ?: ""
                        )
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
//    val isCardVisible = remember { mutableStateOf(true) }
//
//    // 경로 도착 상태 변수
//    val hasArrived = remember { mutableStateOf(false) }
//    val showArrivalCard = remember { mutableStateOf(false) }
//
//    val goalMarker = remember { mutableStateOf<PointAnnotation?>(null) }
//
//    // 이미지 로드
//    LaunchedEffect(Unit) {
//        Log.d("EscapeRouteScreen", "✅ EscapeRouteMapScreen 진입")
//        mapViewModel.fetchMapImage("222")
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
//    // 지도 초기 스타일 설정 (최초 한 번만 실행)
//    LaunchedEffect(imageUrl) {
//        if (imageUrl != null) {
//            mapView.mapboxMap.loadStyle(
//                style {
//                    +backgroundLayer("background") {
//                        backgroundColor("#EFF0F1")
//                    }
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
//                    +rasterLayer("custom-map-layer", "custom-map") {
//                        rasterOpacity(1.0)
//                    }
//                }
//            ) {
//                val center = convertPixelToLngLat(imageWidth / 2, imageHeight / 2)
//                mapView.mapboxMap.setCamera(
//                    CameraOptions.Builder()
//                        .center(Point.fromLngLat(center[0], center[1]))
//                        .zoom(3.0)
//                        .build()
//                )
//                mapView.mapboxMap.setBounds(
//                    CameraBoundsOptions.Builder()
//                        .bounds(CoordinateBounds(Point.fromLngLat(left, bottom), Point.fromLngLat(right, top)))
//                        .build()
//                )
//
//                val annotationApi = mapView.annotations
//                pointAnnotationManager.value = annotationApi.createPointAnnotationManager()
//                polylineManager.value = annotationApi.createPolylineAnnotationManager()
//            }
//        }
//    }
//
//    // 🔁 내 위치 마커만 따로 관리
//    LaunchedEffect(myLocation) {
//        pointAnnotationManager.value?.deleteAll()
//
//        myLocation?.let { beacon ->
//            val marker = PointAnnotationOptions()
//                .withPoint(Point.fromLngLat(beacon.coordX, beacon.coordY))
//                .withIconImage("marker-icon")
//                .withIconSize(0.5)
//            pointAnnotationManager.value?.create(marker)
//        }
//    }
//
//    // 화재 위치만 따로 관리
//    LaunchedEffect(fireNotificationDto) {
//        pointAnnotationManager.value?.let { manager ->
//            val fireBeacons = fireNotificationDto?.beaconNotificationDtos ?: return@let
//            fireBeacons.forEach { beacon ->
//                val marker = PointAnnotationOptions()
//                    .withPoint(Point.fromLngLat(beacon.coordX, beacon.coordY))
//                    .withIconImage("fire-icon")
//                    .withIconSize(0.25)
//                manager.create(marker)
//            }
//        }
//    }
//
//    // 🔁 경로 표시도 분리해서 관리
//    LaunchedEffect(routePoints, showRoute.value) {
//        if (showRoute.value && routePoints.size >= 2) {
//            polylineManager.value?.deleteAll()
////            pointAnnotationManager.value?.deleteAll()
//
//            for (i in 0 until routePoints.size - 1) {
//                val start = routePoints[i]
//                val end = routePoints[i + 1]
//
//                val polyline = PolylineAnnotationOptions()
//                    .withPoints(
//                        listOf(
//                            Point.fromLngLat(start.x, start.y),
//                            Point.fromLngLat(end.x, end.y)
//                        )
//                    )
//                    .withLineColor("#8AEA52")
//                    .withLineWidth(6.0)
//                polylineManager.value?.create(polyline)
//            }
//            if (goalMarker == null) {
//                val destination = routePoints.last()
//                val endMarker = PointAnnotationOptions()
//                    .withPoint(Point.fromLngLat(destination.x, destination.y))
//                    .withIconImage("destination-icon")
//                    .withIconSize(0.5)
//                pointAnnotationManager.value?.create(endMarker)
//            }
//        }
//    }
//
//
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
//
//        if (showArrivalCard.value) {
//            ArrivalCard(
//                exitName = "3번 출구",
//                userCount = 12,
//                onDismiss = { showArrivalCard.value = false },
//                modifier = Modifier.align(Alignment.TopCenter)
//            )
//        }
//    }
//}
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
//    val isCardVisible = remember { mutableStateOf(true) }
//    val focusManager = LocalFocusManager.current
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


// 오면은 경로 연결하기
// 화재 이미지 불러오는 컴포넌트 다시하기
// 위경도 바꾸기
// api 생기면, 출구 인식해서 안내종료 컴포넌트 하기
// 내위치 api 생기면 내위치 마커 연결하기


//
//if (isCardVisible.value && targetBeaconDto?.imageUrl?.isNotEmpty() == true) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onTap = {
//                        Log.d("FireModal", "🛑 바깥 탭 → 모달 닫기")
//                        isCardVisible.value = false
//                    }
//                )
//            }
//    ) {
//        Box(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(top = 60.dp)
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null
//                ) { /* 내부 탭 무시 */ }
//        ) {
//            FireNotificationCard(
//                gateName = targetBeaconDto.beaconName ?: "알 수 없음",
//                imageUrl = targetBeaconDto.imageUrl ?: ""
//            )
//        }
//    }
//}

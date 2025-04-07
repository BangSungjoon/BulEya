package com.ssafy.jangan_mobile.ui.screen

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto
import com.ssafy.jangan_mobile.store.FireNotificationStore
import com.ssafy.jangan_mobile.ui.component.ArrivalCard
import com.ssafy.jangan_mobile.ui.component.EvacuationButton
import com.ssafy.jangan_mobile.ui.component.FireDetailBottomSheet
import com.ssafy.jangan_mobile.ui.component.FloorSelector
import com.ssafy.jangan_mobile.ui.viewmodel.MapViewModel
import com.ssafy.jangan_mobile.viewmodel.EscapeRouteViewModel
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.ssafy.jangan_mobile.service.dto.BeaconNotificationDto
import com.ssafy.jangan_mobile.ui.component.FireNotificationCard
import com.ssafy.jangan_mobile.ui.component.FireStation

import androidx.compose.ui.res.painterResource
import com.mapbox.common.toValue
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo

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
    val isTracking by viewModel.isTracking.observeAsState()

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


    // 상태 변수
    val hasArrived = remember { mutableStateOf(false) }
    val showArrivalCard = remember { mutableStateOf(false) }
    val isCardVisible = remember { mutableStateOf(true) } // 도착카드
    val isGuiding = remember { mutableStateOf(false) }
    val isFireNotificationCardVisible = remember { mutableStateOf(true) } // 마커 클릭용
    val fireNotification = fireNotificationDto
    val isFireStationShown = remember { mutableStateOf(false) } // 최초 알림용
    val cctvImageUrl = remember { mutableStateOf<String?>(null) }
    val selectedFireBeaconDto = remember { mutableStateOf<BeaconNotificationDto?>(null) }

    // 마커들
    val myLocationAnnotation = remember { mutableStateOf<PointAnnotation?>(null) }
    val goalMarker = remember { mutableStateOf<PointAnnotation?>(null) }
    val fireMarkers = remember { mutableStateListOf<PointAnnotation>() }
    val destinationMarker = remember { mutableStateOf<PointAnnotation?>(null) }
    val routeMarkers = remember { mutableListOf<PointAnnotation>() }

    // 이미지 로드
    LaunchedEffect(Unit) {
        Log.d("EscapeRouteScreen", "✅ EscapeRouteMapScreen 진입")
        mapViewModel.fetchMapImage("222", "1001")
    }

    // 내 위치 요청
    LaunchedEffect(currentLocationCode) {
        while (true) {
            currentLocationCode?.toInt()?.let {
                viewModel.fetchMyLocation(222, it)
            }
            kotlinx.coroutines.delay(2000) // 2초마다 업데이트
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
                        BitmapFactory.decodeResource(context.resources, R.drawable.ellipse)
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
                var center = convertPixelToLngLat(imageWidth / 2, imageHeight / 2)
                // 화재알람 존재 시 지도 처음 위치를 화재 위치로
                if( !(fireNotificationDto?.beaconNotificationDtos?.isEmpty() ?: true)){
                    fireNotificationDto!!.beaconNotificationDtos.forEach { dto ->
                        if(dto.isNewFire == 1){
                            center = listOf(dto.coordX, dto.coordY)
                        }
                    }
                }
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(center[0], center[1]))
                        .zoom(5.0)
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
    LaunchedEffect(myLocation, selectedFloor.value, isTracking) {
        myLocationAnnotation.value?.let {
            pointAnnotationManager.value?.delete(it)
            myLocationAnnotation.value = null
        }
        val selectedFloorCode = floorStringToCode(selectedFloor.value)
        myLocation?.let { beacon ->
            if (beacon.floor == selectedFloorCode) {
                val marker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(beacon.coordX, beacon.coordY))
                    .withIconImage("marker-icon")
                    .withIconSize(0.15)
                myLocationAnnotation.value = pointAnnotationManager.value?.create(marker)
            }
        }
        // 현재 위치 카메라 추적
        if(isTracking?: false && myLocation != null){
            mapView.mapboxMap.flyTo(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(myLocation!!.coordX, myLocation!!.coordY))
                    .zoom(5.0)
                    .build(),
                mapAnimationOptions {
                    duration(1500L)
                }
            )
        }
    }

    // 화재 위치만 따로 관리
    LaunchedEffect(fireNotificationDto, selectedFloor.value, showArrivalCard.value) {

        val selectedFloorCode = floorStringToCode(selectedFloor.value)
        Log.d("FireMarker", "🔥 LaunchedEffect 호출됨. 현재 층: $selectedFloorCode")

        pointAnnotationManager.value?.let { manager ->
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

                        // beacon.beaconCode와 fireNotification.stationId를 함께 사용
                        val stationId = fireNotification?.stationId ?: 0
                        val beaconCode = beacon.beaconCode

                        viewModel.fetchCctvImage(stationId, beaconCode) { url ->
                            selectedFireBeaconDto.value = beacon.copy(imageUrl = url)
                            isFireNotificationCardVisible.value = true
                        }

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
            routeMarkers.forEach { marker -> pointAnnotationManager.value?.delete(marker) }
            routeMarkers.clear()


            return@LaunchedEffect
        }
        // 라인만 지우기 (routePoints가 1개인 경우)
        if (!showRoute.value || routePoints.size == 1) {

            polylineManager.value?.deleteAll()
            // ✅ 무조건 routePoints[0]에 목적지 마커 표시
            val destination = routePoints.first()
            Log.d("EscapeRouteMap", "📍 목적지 마커 추가: (${destination.x}, ${destination.y})")


            if (destination.floor == selectedFloorCode) {
                val endMarker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(destination.x, destination.y))
                    .withIconImage("destination-icon")
                    .withIconSize(0.15)
                pointAnnotationManager.value?.create(endMarker)
            }
            // ✅ 내 위치 마커 (routePoints.last())
            val currentPosition = routePoints.last()


            // ✅ 도착 여부: 객체 값 일치로만 판단
            if (destination.floor == currentPosition.floor &&
                destination.x == currentPosition.x &&
                destination.y == currentPosition.y
            )

            {
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
            routeMarkers.forEach { marker ->
                pointAnnotationManager.value?.delete(marker)
            }
            routeMarkers.clear()

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
                val marker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(destination.x, destination.y))
                    .withIconImage("destination-icon")
                    .withIconSize(0.2)
                destinationMarker.value = pointAnnotationManager.value?.create(marker)
            }
            // ✅ 내 위치 마커 (routePoints.last())
            val currentPosition = routePoints.last()

            // ✅ 도착 여부: 객체 값 일치로만 판단
            if (destination.floor == currentPosition.floor &&
                destination.x == currentPosition.x &&
                destination.y == currentPosition.y
            ) {
                Log.d("EscapeRouteMap", "📍 도착")
                if (!showArrivalCard.value) {
                    Log.d("EscapeRouteMap", "🎉 목적지 도착 (좌표 동일) → 안내 카드 표시")
                    showArrivalCard.value = true

//                    // 목적지 마커만 제거
//                    destinationMarker.value?.let {
//                        pointAnnotationManager.value?.delete(it)
//                        destinationMarker.value = null
//                    }
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

    // 안내 종료 모달
    LaunchedEffect(showArrivalCard.value) {
        if (showArrivalCard.value) {
            kotlinx.coroutines.delay(10000)
            showArrivalCard.value = false
            isGuiding.value = false // ✅ 안내 종료 버튼도 함께 사라지게

            // 도착지 마커 제거
            destinationMarker.value?.let {
                pointAnnotationManager.value?.delete(it)
                destinationMarker.value = null
            }

            // 경로 마커 제거
            routeMarkers.forEach {
                pointAnnotationManager.value?.delete(it)
            }
            routeMarkers.clear()

            // 경로 선 제거
            polylineManager.value?.deleteAll()
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

    // 화재 역정보 모달
    if (isCardVisible.value && targetBeaconDto?.imageUrl?.isNotEmpty() == true && fireNotification != null) {
        LaunchedEffect(Unit) {
            // 최초 진입 시 FireStation 한 번만 보여줌
            isFireStationShown.value = true
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            contentAlignment = Alignment.TopCenter // ✅ 여기에서 위치 고정!
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { -100 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -100 }) + fadeOut()
            ) {
                FireStation(
                    stationName = fireNotification.stationName,
                    status = "화재 발생",
                    gateName = targetBeaconDto.beaconName,
                    onDismiss = {
                        Log.d("FireModal", "🛑 모달 닫기 버튼 클릭")
                        isFireStationShown.value = false
                    },
                )
            }
        }
    }

    // ✅ 도착 알림 카드
    if (showArrivalCard.value) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ArrivalCard(
                onDismiss = {
                    showArrivalCard.value = false
                    isGuiding.value = false
//                    pointAnnotationManager.value?.deleteAll()
                },
                onRetry = {
                    Log.d("ArrivalCard", "🔁 경로 재안내 요청")
                    showArrivalCard.value = false
                    isGuiding.value = true
                    showRoute.value = true

                    currentLocationCode?.let {
                        viewModel.fetchEscapeRoute(it, it)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )
        }
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
                    .padding(start = 16.dp,
                        bottom = 50.dp),
                horizontalAlignment = Alignment.Start
            ) {
                FloorSelector(
                    selectedFloor = selectedFloor.value,
                    onFloorSelected = { selectedFloor.value = it }
                )
                if (!showArrivalCard.value) {
                    Spacer(modifier = Modifier.height(16.dp))
                    EvacuationButton(
                        isGuiding = isGuiding.value,
                        onClick = {
                            if (isGuiding.value) {
                                // ✅ 안내 종료 처리
                                isGuiding.value = false
                                showRoute.value = false
                                polylineManager.value?.deleteAll()
                                goalMarker.value?.let { pointAnnotationManager.value?.delete(it) }
                                myLocationAnnotation.value?.let { pointAnnotationManager.value?.delete(it) }
                                viewModel.setIsTracking(false)
                            } else {
                                // ✅ 안내 시작
                                currentLocationCode?.let { code ->
                                    viewModel.fetchEscapeRoute(222, code)
                                    showRoute.value = true
                                    isGuiding.value = true
                                    viewModel.setIsTracking(true)
                                }
                            }
                        }
                    )
                }
                }
        }
    }
    // ✅ 🔥 화재 실시간 사진
        if (isFireNotificationCardVisible.value && selectedFireBeaconDto.value != null) {
            // ✅ 🔥 상세 모달 (FireNotificationCard → FireDetailBottomSheet 교체)
            AnimatedVisibility(
                visible = isCardVisible.value && targetBeaconDto?.imageUrl?.isNotEmpty() == true,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    Log.d("FireModal", "🛑 배경 클릭 → 모달 닫기")
                                    isCardVisible.value = false
                                }
                            )
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { /* 내부 탭 무시 */ }
                    ) {
                        FireNotificationCard(
                            beaconName = targetBeaconDto?.beaconName ?: "알 수 없음",
                            imageUrl = targetBeaconDto?.imageUrl ?: "",
                            isVisible = isCardVisible.value,
                            onDismiss = {
                                Log.d("FireModal", "🛑 모달 닫기 버튼 클릭")
                                isFireNotificationCardVisible.value = false
                            },
                            onGuideClick = {
                                Log.d("FireModal", "➡️ 대피 경로 찾기 클릭됨")
                                isFireNotificationCardVisible.value = false
                                currentLocationCode?.let { code ->
                                    viewModel.fetchEscapeRoute(222, code)
                                    showRoute.value = true
                                    isGuiding.value = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


package com.ssafy.jangan_mobile.ui.screen


import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.jangan_mobile.model.LatLngData
import com.ssafy.jangan_mobile.service.RetrofitInstance
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.rasterLayer
import com.mapbox.maps.extension.style.sources.generated.imageSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.ssafy.jangan_mobile.ui.theme.JanganmobileTheme
import com.ssafy.jangan_mobile.ui.viewmodel.MapViewModel
import kotlinx.coroutines.launch
import com.ssafy.jangan_mobile.R



class MapScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JanganmobileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)) {
                        MapViewScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MapViewScreen() {
    val viewModel: MapViewModel = viewModel()
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val imageUrl by viewModel.mapImageUrl.collectAsState()

    // 이미지 기본 정보

    val imageWidth = 5000
    val imageHeight = 7800
    val imageAspectRatio = imageWidth.toDouble() / imageHeight.toDouble()

    val coordinateHeight = 60.0
    val coordinateWidth = coordinateHeight * imageAspectRatio

    val top = coordinateHeight / 2
    val bottom = -coordinateHeight / 2
    val left = -coordinateWidth / 2
    val right = coordinateWidth / 2

    fun convertPixelToLngLat(x: Int, y: Int): List<Double> {
        val lng = left + (x.toDouble() / imageWidth) * (right - left)
        val lat = top - (y.toDouble() / imageHeight) * (top - bottom)
        return listOf(lng, lat)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchMapImage("222") // 이미지 URL 요청
    }

    LaunchedEffect(imageUrl) {
        Log.d("✅MapScreen", "받은 이미지 URL: $imageUrl")

        if (imageUrl != null) {
            mapView.getMapboxMap().loadStyle(
                styleExtension = style(Style.LIGHT) {
                    +image("marker-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)) {}

                    +imageSource("custom-map") {
                        url(imageUrl!!)
                        coordinates(
                            listOf(
                                convertPixelToLngLat(0, 0),
                                convertPixelToLngLat(imageWidth, 0),
                                convertPixelToLngLat(imageWidth, imageHeight),
                                convertPixelToLngLat(0, imageHeight),
                            )
                        )
                    }

                    +rasterLayer("custom-map-layer", "custom-map") {
                        rasterOpacity(1.0)
                    }
                }
            ) {
                val lngLat = convertPixelToLngLat(2500, 3900)
                val annotationManager = mapView.annotations.createPointAnnotationManager()
                val marker = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(lngLat[0], lngLat[1]))
                    .withIconImage("marker-icon")

                annotationManager.create(marker)
            }
        }
    }
    AndroidView(factory = { mapView })
}

//    LaunchedEffect(Unit) {
//        viewModel.fetchMapImage("222")
//        mapView.getMapboxMap().loadStyle(
//            styleExtension = style(Style.LIGHT) {
//                // 마커 아이콘 등록
//                +image("marker-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)) {}
//
//                // 이미지 소스 등록
//                +imageSource("custom-map") {
//                    url(imageUrl!!)
//                    coordinates(
//                        listOf(
//                            convertPixelToLngLat(0, 0), // top-left
//                            convertPixelToLngLat(imageWidth, 0), // top-right
//                            convertPixelToLngLat(imageWidth, imageHeight), // bottom-right
//                            convertPixelToLngLat(0, imageHeight), // bottom-left
//                        )
//                    )
//                }
//                // 레이어 등록
//                +rasterLayer("custom-map-layer", "custom-map") {
//                    rasterOpacity(1.0)
//                }
//            }
//        ) {
//            // 마커 추가
//            val lngLat = convertPixelToLngLat(2500, 3900)
//            val annotationManager = mapView.annotations.createPointAnnotationManager()
//            val marker = PointAnnotationOptions()
//                .withPoint(Point.fromLngLat(lngLat[0], lngLat[1]))
//                .withIconImage("marker-icon")
//
//            annotationManager.create(marker)
//        }
//    }

//보험 3
//class MapScreen : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            JanganmobileTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
//                    Box(modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding))
//                    val viewModel: MapViewModel = viewModel()
//                    MapViewScreen(viewModel)
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun MapViewScreen(mapViewModel: MapViewModel) {
//    val context = LocalContext.current
//    val mapView = remember { MapView(context) }
//    val imageUrl by mapViewModel.mapImageUrl.collectAsState()
//
//    // 1. 이미지 URL 요청
//    LaunchedEffect(Unit) {
//        mapViewModel.fetchMapImage("222") // 역 코드
//    }
//
//    // 2. Mapbox 스타일 적용
//    LaunchedEffect(imageUrl) {
//        Log.d("✅MapboxTest", "이미지 URL: $imageUrl") // ✅ 로그 추가
//        if (imageUrl != null) {
//            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)
//            mapView.getMapboxMap().loadStyle(
//                styleExtension = style(Style.LIGHT) {
//                    +image("marker-icon", bitmap) { }
//                    +imageSource("indoor-source") {
//                        url(imageUrl!!)
//                        coordinates(
//                            listOf(
//                                listOf(126.99, 37.51),
//                                listOf(127.01, 37.51),
//                                listOf(127.01, 37.49),
//                                listOf(126.99, 37.49)
//                            )
//                        )
//                    }
//                    +rasterLayer("indoor-layer", "indoor-source") { }
////                    +image("marker-icon") {
////                        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)
//                }
//            ) {
//                // 임의 마커 좌표
//                val myTestLocation = listOf(
//                    LatLngData(127.0, 37.5)
//                )
//                addMarkers(mapView, myTestLocation)
//            }
//        }
//    }
//
//    AndroidView(factory = { mapView })
//}
//
//
//fun addMarkers(mapView: MapView, coordinates: List<LatLngData>) {
//    val pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
//    coordinates.forEach { coord ->
//        val marker = PointAnnotationOptions()
//            .withPoint(Point.fromLngLat(coord.coord_x, coord.coord_y))
//            .withIconImage("marker_icon")
//        pointAnnotationManager.create(marker)
//    }
//}

// 보험 2
//class MapScreen : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            val mapViewModel: MapViewModel = viewModel()
//            MapViewScreen(mapViewModel)
//        }
//    }
//}
//
//@Composable
//fun MapViewScreen(mapViewModel: MapViewModel) {
//    val mapView = remember { MapView(LocalContext.current) }
//    val coordinates by mapViewModel.coordinates.collectAsState()
//
//    LaunchedEffect(Unit) {
//        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
//            mapViewModel.fetchCoordinates("222") // ✅ 실제 역 코드 입력
//        }
//    }
//
//    // 마커 추가
//    LaunchedEffect(coordinates) {
//        if (coordinates.isNotEmpty()) {
//            mapViewModel.addMarkers(mapView, coordinates)
//        }
//    }
//
//    AndroidView(factory = { mapView })
//}




//처음 보험
//@Composable
//fun MapScreen(viewModel: MapViewModel, stationId: String, modifier: Modifier = Modifier) {
//    val coordinates by viewModel.coordinates.collectAsState()
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AndroidView(
//            factory = { context ->
//                MapView(context).apply {
//                    getMapboxMap().loadStyle(Style.MAPBOX_STREETS) { style ->
//                        viewModel.addMarkers(this, coordinates)
//                    }
//                }
//            },
//            update = { mapView ->
//                viewModel.addMarkers(mapView, coordinates)
//            }
//        )
//    }
//
//    LaunchedEffect(stationId) {
//        viewModel.fetchCoordinates(stationId)
//    }
//}
//
//
//class MapScreen : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            MapViewScreen()
//        }
//    }
//}
//
//@Composable
//fun MapViewScreen() {
//    val context = LocalContext.current
//    val mapView = remember { MapView(context) }
//
//    // 📌 하드코딩된 테스트 좌표 데이터
//    val testData = listOf(
//        Pair(12.0, 5.0),   // 첫 번째 마커
//        Pair(20.5, 10.3),  // 두 번째 마커
//        Pair(30.2, 40.1)   // 세 번째 마커
//    )
//
//    LaunchedEffect(Unit) {
//        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
//            val annotationManager = mapView.annotations.createPointAnnotationManager()
//            testData.forEach { (x, y) ->
//                addMarker(annotationManager, x, y)
//            }
//        }
//    }
//}
//
//fun addMarker(annotationManager: PointAnnotationManager, x: Double, y: Double) {
//    val marker = PointAnnotationOptions()
//        .withPoint(Point.fromLngLat(x, y))
//        .withIconImage("marker-icon") // 마커 아이콘 필요하면 추가
//    annotationManager.create(marker)
//}
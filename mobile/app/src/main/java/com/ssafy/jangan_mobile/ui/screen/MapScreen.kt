package com.ssafy.jangan_mobile.ui.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.backgroundLayer
import com.mapbox.maps.extension.style.layers.generated.rasterLayer
import com.mapbox.maps.extension.style.sources.generated.imageSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.LineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createLineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.store.FireNotificationStore
import com.ssafy.jangan_mobile.ui.viewmodel.EscapeRouteViewModel
import kotlinx.coroutines.launch

@Composable
fun EscapeRouteMapScreen(
    navController: NavController,
    viewModel: EscapeRouteViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val fireNotificationDto by FireNotificationStore.fireNotificationDto.observeAsState()
    val currentLocationCode by FireNotificationStore.currentLocationBeaconCode.observeAsState()
    val routePoints by viewModel.route.observeAsState(emptyList())
    val imageUrl by mapViewModel.mapImageUrl.collectAsState()

    val showRoute = remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        mapViewModel.fetchMapImage("222") // 실제 역코드로 교체
    }

    LaunchedEffect(imageUrl) {
        if (imageUrl != null) {
            mapView.getMapboxMap().loadStyle(
                style {
                    +backgroundLayer("background") { backgroundColor("#EFF0F1") }
                    +image("marker-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)) {}
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
                    +rasterLayer("custom-map-layer", "custom-map") { rasterOpacity(1.0) }
                }
            ) {
                val center = convertPixelToLngLat(imageWidth / 2, imageHeight / 2)
                mapView.getMapboxMap().setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(center[0], center[1]))
                        .zoom(1.0)
                        .build()
                )
                mapView.getMapboxMap().setBounds(
                    CoordinateBounds(
                        Point.fromLngLat(left, bottom),
                        Point.fromLngLat(right, top)
                    )
                )
                mapView.gestures.pitchEnabled = true
                mapView.gestures.rotateEnabled = true
                mapView.gestures.doubleTapToZoomInEnabled = true
                mapView.getMapboxMap().setRenderWorldCopies(false)

                fireNotificationDto?.let { fire ->
                    val firePos = convertPixelToLngLat(fire.coordX, fire.CoordY)
                    val fireMarker = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(firePos[0], firePos[1]))
                        .withIconImage("marker-icon")
                    mapView.annotations.createPointAnnotationManager().create(fireMarker)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView })

        fireNotificationDto?.let {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("🔥 화재 발생!", color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
                // 좌표로 임시테스트
                Button(onClick = {
                    viewModel.loadMockRoute()  // 실제 API 호출 대신 임시 경로
                    showRoute.value = true
                }) {
                    Text("대피경로 테스트")
                }
                // 실제 API 호출
                Button(onClick = {
                    currentLocationCode?.let { code ->
                        viewModel.fetchEscapeRoute(222, code) // 222는 station_id 예시
                        showRoute.value = true
                    }
                }) {
                    Text("대피경로 찾기")
                }
            }
        }
    }

    if (showRoute.value && routePoints.isNotEmpty()) {
        LaunchedEffect(routePoints) {
            mapView.getMapboxMap().getStyle()?.let {
                val lineManager = mapView.annotations.createLineAnnotationManager()
                val line = LineAnnotationOptions()
                    .withPoints(routePoints.map { pt ->
                        val lngLat = convertPixelToLngLat(pt.x, pt.y)
                        Point.fromLngLat(lngLat[0], lngLat[1])
                    })
                    .withLineColor("#00FF00")
                    .withLineWidth(6.0)
                lineManager.create(line)
            }
        }
    }
}


//
//import android.content.Context
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.ssafy.jangan_mobile.model.LatLngData
//import com.ssafy.jangan_mobile.service.RetrofitInstance
//import com.mapbox.maps.MapView
//import com.mapbox.maps.Style
//import com.mapbox.geojson.Point
//import com.mapbox.maps.extension.style.image.image
//import com.mapbox.maps.extension.style.layers.generated.rasterLayer
//import com.mapbox.maps.extension.style.sources.generated.imageSource
//import com.mapbox.maps.extension.style.style
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
//import com.mapbox.maps.plugin.annotation.annotations
//import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
//import com.ssafy.jangan_mobile.ui.theme.JanganmobileTheme
//import com.ssafy.jangan_mobile.ui.viewmodel.MapViewModel
//import kotlinx.coroutines.launch
//import com.ssafy.jangan_mobile.R
//import com.mapbox.maps.extension.style.expressions.dsl.generated.literal
//import com.mapbox.maps.extension.style.layers.generated.rasterLayer
//import com.mapbox.maps.extension.style.sources.generated.imageSource
//import com.mapbox.maps.extension.style.style
//import com.mapbox.maps.extension.style.image.image
//import com.mapbox.maps.CameraOptions
//import com.mapbox.maps.CameraBoundsOptions
//import com.mapbox.geojson.BoundingBox
//import com.mapbox.maps.CoordinateBounds
//import com.mapbox.maps.extension.style.layers.generated.backgroundLayer
//import com.mapbox.maps.plugin.gestures.gestures
//
//@Composable
//fun MapViewScreen(navController: NavController) {
//    val viewModel: MapViewModel = viewModel()
//    val context = LocalContext.current
//    val mapView = remember { MapView(context) }
//
//    val imageUrl by viewModel.mapImageUrl.collectAsState()
//
//    val imageWidth = 5000
//    val imageHeight = 7800
//    val aspectRatio = imageWidth.toDouble() / imageHeight.toDouble()
//
//    val coordinateHeight = 60.0
//    val coordinateWidth = coordinateHeight * aspectRatio
//
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
//    LaunchedEffect(Unit) {
//        viewModel.fetchMapImage("222")
//    }
//
//    LaunchedEffect(imageUrl) {
//        if (imageUrl != null) {
//            mapView.getMapboxMap().loadStyle(
//                style {
//                    // 👇 회색 배경 레이어 먼저 추가
//                    +backgroundLayer("background") {
//                        backgroundColor("#EFF0F1") // 원하는 배경색으로 설정
//                    }
//
//                    +image("marker-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)) {}
//
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
//
//                    +rasterLayer("custom-map-layer", "custom-map") {
//                        rasterOpacity(1.0)
//                    }
//                }
//            ) {
//                val center = convertPixelToLngLat(imageWidth / 2, imageHeight / 2)
//                mapView.getMapboxMap().setCamera(
//                    CameraOptions.Builder()
//                        .center(Point.fromLngLat(center[0], center[1]))
//                        .zoom(1.0)
//                        .pitch(0.0)
//                        .bearing(0.0)
//                        .build()
//                )
//
//                mapView.getMapboxMap().setBounds(
//                    com.mapbox.maps.CameraBoundsOptions.Builder()
//                        .minZoom(1.0)
//                        .maxZoom(8.0)
//                        .bounds(
//                            CoordinateBounds(
//                                Point.fromLngLat(left, bottom),
//                                Point.fromLngLat(right, top)
//                            )
//                        )
//                        .build()
//                )
//
//                mapView.gestures.pitchEnabled = true // 회전 허용
//                mapView.gestures.rotateEnabled = true
//                mapView.gestures.doubleTapToZoomInEnabled = true // 더블탭 줌인 허용
//                mapView.getMapboxMap().setRenderWorldCopies(false)
//
//                val lngLat = convertPixelToLngLat(2500, 3900)
//                val marker = PointAnnotationOptions()
//                    .withPoint(Point.fromLngLat(lngLat[0], lngLat[1]))
//                    .withIconImage("marker-icon")
//                mapView.annotations.createPointAnnotationManager().create(marker)
//            }
//        }
//    }
//
//    AndroidView(factory = { mapView })
//}

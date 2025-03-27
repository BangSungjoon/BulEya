package com.ssafy.jangan_mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jangan_mobile.model.LatLngData
import com.ssafy.jangan_mobile.service.RetrofitInstance
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _coordinates = MutableStateFlow<List<LatLngData>>(emptyList())
    val coordinates: StateFlow<List<LatLngData>> = _coordinates

    private val _mapImageUrl = MutableStateFlow<String?>(null)
    val mapImageUrl: StateFlow<String?> = _mapImageUrl

    fun fetchMapImage(stationId: String) {
        Log.d("✅MapViewModel", "💥 함수 진입 확인됨!")
        viewModelScope.launch {
            try {
                Log.d("✅MapViewModel", "fetchMapImage 호출됨! stationId=$stationId")
                val response = RetrofitInstance.api.getMapImage(stationId)
                if (response.isSuccessful) {
                    Log.d("✅MapViewModel", "API 응답 성공")

                    val floorImages = response.body()?.result
                    val targetFloor = floorImages?.find { it.floor == 1 } // 1층만 일단 사용
                    Log.d("✅MapViewModel", "선택된 층: $targetFloor")

                    _mapImageUrl.value = targetFloor?.imageUrl
                    Log.d("✅MapViewModel", "이미지 URL 저장됨: ${_mapImageUrl.value}")
                } else {
                    Log.e("❌MapViewModel", "API 응답 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("❌MapViewModel", "예외 발생: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

fun addMarkers(mapView: MapView, coordinates: List<LatLngData>) {
    val pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
    coordinates.forEach { coord ->
        val marker = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(coord.coord_x, coord.coord_y))
            .withIconImage("marker_icon")
        pointAnnotationManager.create(marker)
    }
}


//class MapViewModel : ViewModel() {
//    private val _coordinates = MutableStateFlow<List<LatLngData>>(emptyList())
//    val coordinates: StateFlow<List<LatLngData>> = _coordinates
//
//    fun fetchCoordinates(stationId: String) {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.getCoordinates(stationId)
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    println("Response body: $responseBody")
//                    responseBody?.coordinates?.let {
//                        _coordinates.value = it
//                    }
//                } else {
//                    println("Response not successful: ${response.code()}")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun addMarkers(mapView: MapView, coordinates: List<LatLngData>) {
////        val annotationApi = mapView.getPlugin(AnnotationPlugin::class.java)
////        val pointAnnotationManager = annotationApi.createPointAnnotationManager()
//        val pointAnnotationManager: PointAnnotationManager = mapView.annotations.createPointAnnotationManager()
//        coordinates.forEach { coord ->
//            val marker = PointAnnotationOptions()
////                .withPoint(Point.fromLngLat(coord.lng, coord.lat))
//                .withPoint(Point.fromLngLat(coord.coord_x, coord.coord_y))
//                .withIconImage("marker-icon")
//            pointAnnotationManager.create(marker)
//        }
//    }
//}
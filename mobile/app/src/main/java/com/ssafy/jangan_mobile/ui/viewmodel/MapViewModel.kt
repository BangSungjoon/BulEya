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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _coordinates = MutableStateFlow<List<LatLngData>>(emptyList())
    val coordinates: StateFlow<List<LatLngData>> = _coordinates

    private val _mapImageUrl = MutableStateFlow<String?>(null)
    val mapImageUrl: StateFlow<String?> = _mapImageUrl

    fun fetchMapImage(stationId: String, floorCode: String) {
        Log.d("✅MapViewModel", "💥 함수 진입 확인됨!")
        viewModelScope.launch {
            try {
                Log.d("✅MapViewModel", "fetchMapImage 호출됨! stationId=$stationId, floorCode=$floorCode")
                val response = RetrofitInstance.api.getMapImage(stationId)
                Log.d("✅MapViewModel", "API 응답 받음")

                if (response.isSuccessful) {
                    Log.d("✅MapViewModel", "API 응답 성공")

                    val floorImages = response.body()?.result
                    Log.d("✅MapViewModel", "전체 층 이미지 응답: $floorImages")

                    // floorCode는 문자열이므로 정수로 변환 후 비교
                    val targetFloor = floorImages?.find { it.floor.toString() == floorCode }
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

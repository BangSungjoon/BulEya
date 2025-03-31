package com.ssafy.jangan_mobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
//import com.ssafy.jangan_mobile.model.CoordPoint
//import com.ssafy.jangan_mobile.util.convertPixelToLngLat
import com.ssafy.jangan_mobile.service.dto.EscapeRouteResponse
import com.ssafy.jangan_mobile.service.dto.EscapeRoutePoint

@HiltViewModel
class EscapeRouteViewModel @Inject constructor(
    private val repository: EscapeRouteRepository
) : ViewModel() {

    private val _route = MutableLiveData<List<LatLng>>()
    val route: LiveData<List<LatLng>> = _route

    // API로 가져오는 것
    fun fetchEscapeRoute(stationId: Int, beaconCode: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getEscapeRoute(stationId, beaconCode)
                val convertedRoute = response.route.map { point ->
                    convertPixelToLngLat(point.coord_x, point.coord_y)  // 이 함수는 LngLat 리스트를 반환해야 함
                }
                _route.value = convertedRoute
            } catch (e: Exception) {
                Log.e("EscapeRoute", "에러: ${e.message}")
            }
        }
    }

    // 임시 좌표
    fun loadMockRoute() {
        _route.value = listOf(
            convertPixelToLngLat(2500, 3900),
            convertPixelToLngLat(2700, 4100),
            convertPixelToLngLat(2900, 4300),
            convertPixelToLngLat(3100, 4400),
            convertPixelToLngLat(3300, 4600),
        )
    }

    fun convertPixelToLngLat(x: Int, y: Int): LatLng {
        val imageWidth = 5000
        val imageHeight = 7800
        val coordinateHeight = 60.0
        val aspectRatio = imageWidth / imageHeight.toDouble()
        val coordinateWidth = coordinateHeight * aspectRatio

        val top = coordinateHeight / 2
        val bottom = -coordinateHeight / 2
        val left = -coordinateWidth / 2
        val right = coordinateWidth / 2

        val lng = left + (x.toDouble() / imageWidth) * (right - left)
        val lat = top - (y.toDouble() / imageHeight) * (top - bottom)

        return LatLng(lat, lng)
    }
}

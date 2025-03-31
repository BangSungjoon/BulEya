package com.ssafy.jangan_mobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.jangan_mobile.model.PixelLatLng
import com.ssafy.jangan_mobile.service.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EscapeRouteViewModel : ViewModel() {

    private val _route = MutableLiveData<List<PixelLatLng>>()
    val route: LiveData<List<PixelLatLng>> = _route

    fun fetchEscapeRoute(stationId: Int, beaconCode: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getEscapeRoute(stationId, beaconCode)
                val convertedRoute = response.route.map { point ->
                    PixelLatLng(point.coord_x, point.coord_y)
                }
                _route.postValue(convertedRoute)
            } catch (e: Exception) {
                Log.e("EscapeRoute", "에러: ${e.message}")
            }
        }
    }

    fun loadMockRoute() {
        _route.value = listOf(
            PixelLatLng(2500, 3900),
            PixelLatLng(2700, 4100),
            PixelLatLng(2900, 4300),
            PixelLatLng(3100, 4400),
            PixelLatLng(3300, 4600),
        )
    }
}




// HiltViewmodel

//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.android.gms.maps.model.LatLng
//import com.ssafy.jangan_mobile.model.PixelLatLng
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//import com.ssafy.jangan_mobile.model.CoordPoint
//import com.ssafy.jangan_mobile.util.convertPixelToLngLat
//import com.ssafy.jangan_mobile.service.dto.EscapeRouteResponse
//import com.ssafy.jangan_mobile.service.dto.EscapeRoutePoint


//@HiltViewModel
//class EscapeRouteViewModel @Inject constructor(
//    private val repository: EscapeRouteRepository
//) : ViewModel() {
//
//    private val _route = MutableLiveData<List<PixelLatLng>>()  // ✅ 변경
//    val route: LiveData<List<PixelLatLng>> = _route
//
//    fun fetchEscapeRoute(stationId: Int, beaconCode: Int) {
//        viewModelScope.launch {
//            try {
//                val response = repository.getEscapeRoute(stationId, beaconCode)
//                val convertedRoute = response.route.map { point ->
//                    PixelLatLng(point.coord_x, point.coord_y) // ✅ LatLng → PixelLatLng 변경
//                }
//                _route.value = convertedRoute
//            } catch (e: Exception) {
//                Log.e("EscapeRoute", "에러: ${e.message}")
//            }
//        }
//    }
//
//    fun loadMockRoute() {
//        _route.value = listOf(
//            PixelLatLng(2500, 3900),
//            PixelLatLng(2700, 4100),
//            PixelLatLng(2900, 4300),
//            PixelLatLng(3100, 4400),
//            PixelLatLng(3300, 4600),
//        )
//    }
//}
//
//    fun convertPixelToLngLat(x: Int, y: Int): LatLng {
//        val imageWidth = 5000
//        val imageHeight = 7800
//        val coordinateHeight = 60.0
//        val aspectRatio = imageWidth / imageHeight.toDouble()
//        val coordinateWidth = coordinateHeight * aspectRatio
//
//        val top = coordinateHeight / 2
//        val bottom = -coordinateHeight / 2
//        val left = -coordinateWidth / 2
//        val right = coordinateWidth / 2
//
//        val lng = left + (x.toDouble() / imageWidth) * (right - left)
//        val lat = top - (y.toDouble() / imageHeight) * (top - bottom)
//
//        return LatLng(lat, lng)
//    }


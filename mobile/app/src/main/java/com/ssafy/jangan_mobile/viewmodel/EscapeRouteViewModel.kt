package com.ssafy.jangan_mobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.jangan_mobile.model.PixelLatLng
import com.ssafy.jangan_mobile.service.RetrofitInstance
import com.ssafy.jangan_mobile.store.FireNotificationStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EscapeRouteViewModel : ViewModel() {

    private val _route = MutableLiveData<List<PixelLatLng>>()
    val route: LiveData<List<PixelLatLng>> = _route

    // 내 위치 변수
    private val stationIdLiveData = FireNotificationStore.currentLocationStationId
    private val beaconCodeLiveData = FireNotificationStore.currentLocationBeaconCode

    // stationIdLiveData와 beaconCodeLiveData 감시. 둘 다 값이 있을 때 routeTrigger에 (stationId, beaconCode)를 넣는 역할
    //두 값이 모두 null이 아닐 때만 fetchEscapeRoute()가 동작
    private val routeTrigger = MediatorLiveData<Pair<Int, Int>>()


    init {
        Log.d("EscapeRoute", "✅ EscapeRouteViewModel 생성됨")

        // stationId가 바뀔 때 동작
        routeTrigger.addSource(stationIdLiveData) { stationId ->
            val beaconCode = beaconCodeLiveData.value
            if (stationId != null && beaconCode != null) {
                Log.d("EscapeRoute", "🚀 Station ID 변화 감지")
                routeTrigger.value = stationId to beaconCode
            }
        }

        // beaconCode가 바뀔 때도 동일한 로직
        routeTrigger.addSource(beaconCodeLiveData) { beaconCode ->
            val stationId = stationIdLiveData.value
            if (stationId != null && beaconCode != null) {
                Log.d("EscapeRoute", "🚀 Beacon Code 변화 감지")
                routeTrigger.value = stationId to beaconCode
            }
        }

        // routeTrigger가 값 가질 때마다 fetch 실행
        routeTrigger.observeForever { (stationId, beaconCode) ->
            fetchEscapeRoute(stationId, beaconCode)
        }
    }




    fun fetchEscapeRoute(stationId: Int, beaconCode: Int) {
        Log.d("EscapeRoute", "✅ fetchEscapeRoute 호출됨: stationId=$stationId, beaconCode=$beaconCode")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                //JSON 경로 데이터 받기
                val response = RetrofitInstance.api.getEscapeRoute(stationId, beaconCode).body()
                Log.d("EscapeRoute", "✅ API 응답 받음: ${stationId}")
                Log.d("EscapeRoute", "✅ API 응답 받음: ${response}")

                val routeList = response?.result ?: run {
                    Log.w("EscapeRoute", "⚠️ API 응답이 null: route가 없음")
                    emptyList()
                }

                val convertedRoute = routeList.map { point ->
                    PixelLatLng(point.coordX, point.coordY, point.floor)
                }
                Log.d("EscapeRoute", "✅ 경로 변환 완료: ${convertedRoute.size}개 좌표")
                _route.postValue(convertedRoute)

                _route.postValue(convertedRoute)
            } catch (e: Exception) {
                Log.e("EscapeRoute", "❌에러: ${e.message}")
            }
        }
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


package com.ssafy.jangan_mobile.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.ssafy.jangan_mobile.model.PixelLatLng
import com.ssafy.jangan_mobile.service.RetrofitInstance
import com.ssafy.jangan_mobile.service.dto.CurrentLocationDto
import com.ssafy.jangan_mobile.service.dto.CurrentLocationResponse
import com.ssafy.jangan_mobile.store.FireNotificationStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EscapeRouteViewModel : ViewModel() {

    private val _route = MutableLiveData<List<PixelLatLng>>()
    val route: LiveData<List<PixelLatLng>> = _route

//    private var hasFetchedRoute = false // ✅ 경로 요청 여부 저장

    private val _myLocation = MutableLiveData<CurrentLocationDto?>()
    val myLocation: LiveData<CurrentLocationDto?> = _myLocation

    private val _isTracking = MutableLiveData<Boolean?>()
    val isTracking: LiveData<Boolean?> = _isTracking

    fun setIsTracking(flag: Boolean){
        _isTracking.value = flag
    }

    // 내 위치 변수
    private val stationIdLiveData = FireNotificationStore.currentLocationStationId
    private val beaconCodeLiveData = FireNotificationStore.currentLocationBeaconCode

    private var goalMarker: PointAnnotation? = null

    // stationIdLiveData와 beaconCodeLiveData 감시. 둘 다 값이 있을 때 routeTrigger에 (stationId, beaconCode)를 넣는 역할
    //두 값이 모두 null이 아닐 때만 fetchEscapeRoute()가 동작
    private val routeTrigger = MediatorLiveData<Pair<Int, Int>>()


    // cctv 사진 불러오기
    val cctvImageUrl = mutableStateOf<String?>(null)

    init {
        Log.d("EscapeRoute", "✅ EscapeRouteViewModel 생성됨")

        // stationId가 바뀔 때 동작
        routeTrigger.addSource(stationIdLiveData) { stationId ->
            val beaconCode = beaconCodeLiveData.value
            if (stationId != null && beaconCode != null && routeTrigger.value?.first != stationId) {
                Log.d("EscapeRoute", "🚀 Station ID 변화 감지")
                routeTrigger.value = stationId to beaconCode
            }
        }

        // beaconCode가 바뀔 때도 동일한 로직
        routeTrigger.addSource(beaconCodeLiveData) { beaconCode ->
            val stationId = stationIdLiveData.value
            if (stationId != null && beaconCode != null && routeTrigger.value?.second != beaconCode) {
                Log.d("EscapeRoute", "🚀 Beacon Code 변화 감지")
                routeTrigger.value = stationId to beaconCode
            }
        }
        // routeTrigger 바뀔 때마다 경로 요청
        routeTrigger.observeForever { (stationId, beaconCode) ->
            fetchEscapeRoute(stationId, beaconCode)
        }
    }




    fun fetchEscapeRoute(stationId: Int, beaconCode: Int, ) {
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
                Log.d("EscapeRoute", "✅ 경로 변환 완료: ${convertedRoute.size}개 좌표")
                _route.postValue(convertedRoute)

                _route.postValue(convertedRoute)
            } catch (e: Exception) {
                Log.e("EscapeRoute", "❌에러: ${e.message}")
            }
        }
    }

    fun fetchMyLocation(stationId: Int, beaconCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getBeaconLocation(stationId, beaconCode)
                if (response.isSuccessful) {
                    val location = response.body()?.result
                    Log.d("EscapeRoute", "📍 내 위치 응답: $location")
                    _myLocation.postValue(location)
                } else {
                    Log.w("EscapeRoute", "❗ 응답 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("EscapeRoute", "❌ 위치 요청 실패: ${e.message}")
            }
        }
    }

    fun fetchCctvImage(stationId: Int, beaconCode: Int, callback: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("CCTV", "🌐 API 요청 → stationId=$stationId, beaconCode=$beaconCode")
                val response = RetrofitInstance.api.getCctvImage(stationId, beaconCode)
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.result?.image_url
                    Log.d("CCTV", "✅ 응답 성공 → imageUrl=$imageUrl")

                    if (!imageUrl.isNullOrEmpty()) {
                        cctvImageUrl.value = imageUrl  // 상태로도 저장
                        callback(imageUrl)             // 콜백에도 전달
                        Log.d("CCTV", "✅ CCTV 이미지 URL: $imageUrl")
                    }
                } else {
                    Log.w("CCTV", "❌ 응답 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CCTV", "❗ 예외 발생", e)
            }
        }
    }
}

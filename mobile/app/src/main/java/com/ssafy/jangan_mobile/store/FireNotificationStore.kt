package com.ssafy.jangan_mobile.store

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto

object FireNotificationStore {
    // 화재 정보 코드, 추적해야하는 것
    private val _fireNotificationDto = MutableLiveData<FireNotificationDto?>()
    val fireNotificationDto: LiveData<FireNotificationDto?> = _fireNotificationDto

    // 마지막으로 화재가 발생한 비콘 코드 -> 조회한 화재코드도 포함 -> 사람의 손길이든, 알림이든 가장 최신화된 비콘코드
    private val _currentNotificationBeaconCode = MutableLiveData<Int?>()
    val currentNotificationBeaconCode = _currentNotificationBeaconCode

    //현재 위치 코드, 추적해야하는 것
    private val _currentLocationStationId = MutableLiveData<Int?>()
    val currentLocationStationId = _currentLocationStationId

    private val _currentLocationBeaconCode = MutableLiveData<Int?>()
    val currentLocationBeaconCode = _currentLocationBeaconCode

    fun setNotification(dto: FireNotificationDto?, context: Context) {
        if (isAppInForeground(context)) {
            _fireNotificationDto.value = dto
        } else {
            _fireNotificationDto.postValue(dto)
        }
    }

    fun setCurrentNotificationBeaconCode(beaconCode: Int?, context: Context) {
        if (isAppInForeground(context)) {
            _currentNotificationBeaconCode.value = beaconCode
        } else {
            _currentNotificationBeaconCode.postValue(beaconCode)
        }
    }

    fun setCurrentLocationStationId(stationId: Int?, context: Context) {
        if (isAppInForeground(context)) {
            _currentLocationStationId.value = stationId
        } else {
            _currentLocationStationId.postValue(stationId)
        }
    }

    fun setCurrentLocationBeaconCode(beaconCode: Int?, context: Context) {
        if (isAppInForeground(context)) {
            _currentLocationBeaconCode.value = beaconCode
        } else {
            _currentLocationBeaconCode.postValue(beaconCode)
        }
    }

    private fun isAppInForeground(context: Context): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
}
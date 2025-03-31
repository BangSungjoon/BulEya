package com.ssafy.jangan_mobile.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto

object FireNotificationStore {
    private val _fireNotificationDto = MutableLiveData<FireNotificationDto?>()
    val fireNotificationDto: LiveData<FireNotificationDto?> = _fireNotificationDto

    private val _currentNotificationBeaconCode = MutableLiveData<Int?>()
    val currentNotificationBeaconCode = _currentNotificationBeaconCode

    private val _currentLocationStationId = MutableLiveData<Int?>()
    val currentLocationStationId = _currentLocationStationId

    private val _currentLocationBeaconCode = MutableLiveData<Int?>()
    val currentLocationBeaconCode = _currentLocationBeaconCode

    fun setNotification(dto: FireNotificationDto?){
        _fireNotificationDto.value = dto
    }
    fun setCurrentNotificationBeaconCode(beaconCode: Int?){
        _currentNotificationBeaconCode.value = beaconCode
    }
    fun setCurrentLocationStationId(stationId: Int?){
        _currentLocationStationId.value = stationId
    }
    fun setCurrentLocationBeaconCode(beaconCode: Int?){
        _currentLocationBeaconCode.value = beaconCode
    }
}
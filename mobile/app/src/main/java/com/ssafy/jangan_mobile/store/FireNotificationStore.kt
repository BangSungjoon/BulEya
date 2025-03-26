package com.ssafy.jangan_mobile.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto

object FireNotificationStore {
    //var fireNotificationDto: FireNotificationDto? = null

    private val _fireNotificationDto = MutableLiveData<FireNotificationDto?>()
    val fireNotificationDto: LiveData<FireNotificationDto?> = _fireNotificationDto

    fun setNotification(dto: FireNotificationDto?){
        _fireNotificationDto.value = dto
    }
}
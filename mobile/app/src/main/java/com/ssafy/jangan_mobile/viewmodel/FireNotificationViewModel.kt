package com.ssafy.jangan_mobile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto

class FireNotificationViewModel : ViewModel() {
    private val _fireNotificationDto = MutableLiveData<FireNotificationDto?>()

    fun setNotification(notification: FireNotificationDto){
        _fireNotificationDto.value = notification
    }

    fun clear(){
        _fireNotificationDto.value = null
    }


}
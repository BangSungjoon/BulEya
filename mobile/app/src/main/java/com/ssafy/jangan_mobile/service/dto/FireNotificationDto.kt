package com.ssafy.jangan_mobile.service.dto

data class FireNotificationDto (
    val stationName: String,
    val stationId: Int,
    val beaconNotificationDtos: List<BeaconNotificationDto>
)
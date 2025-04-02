package com.ssafy.jangan_mobile.service.dto

data class BeaconNotificationDto(
    val beaconName: String,
    val beaconCode: Int,
    val coordX: Double,
    val coordY: Double,
    val floor: Int,
    val imageUrl: String,
    val isNewFire: Int
)

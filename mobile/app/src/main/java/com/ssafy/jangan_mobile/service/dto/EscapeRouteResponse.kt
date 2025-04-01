package com.ssafy.jangan_mobile.service.dto

import com.google.gson.annotations.SerializedName


data class EscapeRouteResponse(
    val result: List<RouteNodeDto>?
)

data class RouteNodeDto(
    @SerializedName("beacon_code") val beaconcode: Int,
    @SerializedName("floor") val floor: Int,
    @SerializedName("coord_x") val coordX: Int,
    @SerializedName("coord_y") val coordY: Int
)
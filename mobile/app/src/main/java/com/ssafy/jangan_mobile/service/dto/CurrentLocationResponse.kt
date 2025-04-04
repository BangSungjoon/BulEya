package com.ssafy.jangan_mobile.service.dto

import com.google.gson.annotations.SerializedName

data class CurrentLocationResponse(
    val result: CurrentLocationDto
)

data class CurrentLocationDto(
    @SerializedName("coord_x") val coordX: Double,
    @SerializedName("coord_y") val coordY: Double,
    @SerializedName("floor") val floor: Int
)
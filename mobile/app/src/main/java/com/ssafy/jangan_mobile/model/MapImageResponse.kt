package com.ssafy.jangan_mobile.model

data class MapImageResponse(
    val result: List<FloorImage>
)

data class FloorImage(
    val floor: Int,
    val imageUrl: String
)
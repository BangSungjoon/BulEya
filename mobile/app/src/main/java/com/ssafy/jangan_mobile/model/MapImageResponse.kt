package com.ssafy.jangan_mobile.model

import com.google.gson.annotations.SerializedName

data class MapImageResponse(
    val result: List<FloorImage>
)

data class FloorImage(
    val floor: Int,
    @SerializedName("image_url")
    val imageUrl: String
)
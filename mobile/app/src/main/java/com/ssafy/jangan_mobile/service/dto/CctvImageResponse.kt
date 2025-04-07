package com.ssafy.jangan_mobile.service.dto

import com.google.gson.annotations.SerializedName

data class CctvImageResponse(
    val result: CctvImageResult
)

data class CctvImageResult(
    @SerializedName("image_url") val image_url: String
)

package com.ssafy.jangan_mobile.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FcmService {
    @FormUrlEncoded
    @POST("/api/refresh-fcm-token")
    suspend fun registerFcmToken(
        @Field("uuid") uuid: String,
        @Field("token") token: String
        ): ResponseBody
}
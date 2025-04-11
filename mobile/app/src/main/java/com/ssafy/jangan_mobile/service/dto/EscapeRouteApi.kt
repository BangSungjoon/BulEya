package com.ssafy.jangan_mobile.service.dto

import retrofit2.http.GET
import retrofit2.http.Query

interface EscapeRouteApi {
    @GET("/api/escape-route")
    suspend fun getEscapeRoute(
        @Query("station_id") stationId: Int,
        @Query("beacon_code") beaconCode: Int
    ): EscapeRouteResponse
}
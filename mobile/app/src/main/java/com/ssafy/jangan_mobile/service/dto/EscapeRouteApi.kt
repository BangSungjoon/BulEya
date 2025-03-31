package com.ssafy.jangan_mobile.service.dto

interface EscapeRouteApi {
    @GET("/api/escape-route")
    suspend fun getEscapeRoute(
        @Query("station_id") stationId: Int,
        @Query("beacon_code") beaconCode: Int
    ): EscapeRouteResponse
}
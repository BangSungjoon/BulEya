package com.ssafy.jangan_mobile.service

import com.ssafy.jangan_mobile.model.CoordinateResponse
import com.ssafy.jangan_mobile.model.MapImageResponse
import com.ssafy.jangan_mobile.service.dto.EscapeRouteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("/api/map/mobile")
    suspend fun getCoordinates(
        @Query("station_id") stationId: String
    ): Response<CoordinateResponse>

    @GET("/api/map/mobile")
    suspend fun getMapImage(
        @Query("station_id") stationId: String
    ): Response<MapImageResponse>

    @GET("/api/escape/route")
    suspend fun getEscapeRoute(
        @Query("stationId") stationId: Int,
        @Query("beaconCode") beaconCode: Int
    ): EscapeRouteResponse

}
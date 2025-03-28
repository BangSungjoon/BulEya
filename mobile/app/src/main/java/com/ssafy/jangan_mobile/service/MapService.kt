package com.ssafy.jangan_mobile.service

import com.ssafy.jangan_mobile.model.CoordinateResponse
import com.ssafy.jangan_mobile.model.MapImageResponse
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

}
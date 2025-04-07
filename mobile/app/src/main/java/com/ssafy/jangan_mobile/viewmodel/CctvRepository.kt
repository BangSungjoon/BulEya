package com.ssafy.jangan_mobile.viewmodel

import android.util.Log
import com.ssafy.jangan_mobile.service.MapService

class CctvRepository(private val api: MapService) {
    suspend fun fetchCctvImage(stationId: Int, beaconCode: Int): String? {
        return try {
            val response = api.getCctvImage(stationId, beaconCode)
            if (response.isSuccessful) {
                response.body()?.result?.image_url
            } else {
                Log.e("CctvRepository", "❌ 서버 응답 실패: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CctvRepository", "❌ 이미지 요청 실패", e)
            null
        }
    }
}
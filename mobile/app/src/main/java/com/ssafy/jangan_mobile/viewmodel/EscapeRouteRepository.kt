package com.ssafy.jangan_mobile.viewmodel

import com.ssafy.jangan_mobile.service.dto.EscapeRouteApi
import com.ssafy.jangan_mobile.service.dto.EscapeRouteResponse
import javax.inject.Inject

class EscapeRouteRepository @Inject constructor(
    private val api: EscapeRouteApi
) {
    suspend fun getEscapeRoute(stationId: Int, beaconCode: Int): EscapeRouteResponse {
        return api.getEscapeRoute(stationId, beaconCode)
    }
}

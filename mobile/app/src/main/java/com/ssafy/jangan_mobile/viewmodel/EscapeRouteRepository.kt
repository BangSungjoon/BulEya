package com.ssafy.jangan_mobile.viewmodel

class EscapeRouteRepository @Inject constructor(
    private val api: EscapeRouteApi
) {
    suspend fun getEscapeRoute(stationId: Int, beaconCode: Int): EscapeRouteResponse {
        return api.getEscapeRoute(stationId, beaconCode)
    }
}

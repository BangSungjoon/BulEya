package com.ssafy.jangan_mobile.service.dto


data class EscapeRouteResponse(
    val route: List<EscapeRoutePoint>
)

data class EscapeRoutePoint(
    val beacon_code: Int,
    val floor: Int,
    val coord_x: Int,
    val coord_y: Int
)
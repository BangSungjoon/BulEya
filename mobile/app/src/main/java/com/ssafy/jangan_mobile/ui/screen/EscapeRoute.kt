package com.ssafy.jangan_mobile.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun EscapeRouteScreen(
    navController: NavController,
    stationId: Int,
    beaconCode: Int
) {
    // viewModel로 API 호출 + map 경로 그리기
    // UI: 층 선택, "안내 종료하기" 버튼 등 포함
}
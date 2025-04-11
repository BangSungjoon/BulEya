package com.ssafy.jangan_mobile.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ssafy.jangan_mobile.service.dto.BeaconNotificationDto
import com.ssafy.jangan_mobile.store.FireNotificationStore


@Composable
fun FireNotificationScreen(navController: NavController) {
    val fireNotificationDto = FireNotificationStore.fireNotificationDto.value
    var notificationBeaconCode = FireNotificationStore.currentNotificationBeaconCode.value

    val beaconNotificationDto = fireNotificationDto
        ?.beaconNotificationDtos
        ?.firstOrNull { it.beaconCode == notificationBeaconCode }



    LaunchedEffect(key1 = beaconNotificationDto == null) {
        if (beaconNotificationDto == null) {
            navController.navigate("home") {
                popUpTo("fire_notification") { inclusive = true }
            }
        }
    }

    if(beaconNotificationDto == null) return

    val stationName = fireNotificationDto.stationName
    val beaconName = beaconNotificationDto.beaconName
    val imageUrl = beaconNotificationDto.imageUrl
    var message = ""
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp)
    ){
        Text(
            text = "${stationName}역에서 화재 발생!",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 8.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.CenterHorizontally)
        )
        Text(
            text = "화재 위치 : ${beaconName}",
            fontSize = 19.sp,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.CenterHorizontally)
        )

        if(!imageUrl.isNullOrEmpty()){
            AsyncImage(
                model = imageUrl,
                contentDescription = "화재 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                onSuccess = {
                    Log.d("Coil", "이미지 로딩 성공")
                },
                onError = {
                    Log.e("Coil", "이미지 로딩 실패", it.result.throwable)
                }
            )
        }
        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true } // 스택 클리어
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("대피 경로 확인")
        }
    }
}
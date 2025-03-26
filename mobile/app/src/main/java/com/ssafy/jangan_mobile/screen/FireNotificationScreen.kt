package com.ssafy.jangan_mobile.screen

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ssafy.jangan_mobile.store.FireNotificationStore


@Composable
fun FireNotificationScreen(navController: NavController) {
    val fireNotificationDto = FireNotificationStore.fireNotificationDto.value
    val stationName = fireNotificationDto?.stationName
    val beaconName = fireNotificationDto?.beaconNotificationDtos?.get(0)?.beaconName
    val imageUrl = fireNotificationDto?.beaconNotificationDtos?.get(0)?.imageUrl
    Log.d("", "이미지유알엘:" + imageUrl)
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp)
    ){
        Text(
            text = "${stationName}역에서 화재 발생!",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .padding(top = 8.dp)
                .width(width = 400.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .size(size = 35.dp)
        )
        Text(
            text = "화재 위치 : ${beaconName}",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .width(width = 400.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .size(size = 25.dp)
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
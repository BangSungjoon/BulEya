package com.ssafy.jangan_mobile.ui.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.store.FireNotificationStore
import com.ssafy.jangan_mobile.ui.theme.Headline
import com.ssafy.jangan_mobile.ui.theme.Subtitle2
import com.ssafy.jangan_mobile.ui.theme.system_red

//@Composable
//fun FireNotificationCard(
//    gateName: String,
//    imageUrl: String
//) {
////    val fireNotificationDto = FireNotificationStore.fireNotificationDto.value
////    val notificationBeaconCode = FireNotificationStore.currentNotificationBeaconCode.value
////
////    val beaconNotificationDto = fireNotificationDto
////        ?.beaconNotificationDtos
////        ?.firstOrNull { it.beaconCode == notificationBeaconCode }
////
////    // null이면 아무것도 표시하지 않음
////    if (beaconNotificationDto == null) return
////
////    val gateName = beaconNotificationDto.beaconName ?: "알 수 없음"
////    val imageUrl = beaconNotificationDto.imageUrl ?: ""
//    Log.d("FireNotificationCard", "🧩 파라미터 gateName=$gateName, imageUrl=$imageUrl")
//
//    Column(
//        modifier = Modifier
//            .width(380.dp)
//            .height(280.dp)
//            .background(Color.Black, shape = RoundedCornerShape(40.dp))
//            .padding(vertical = 12.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        // 🔥 상태 + 개찰구 정보
//        Row(
//            modifier = Modifier
//                .width(360.dp)
//                .height(76.dp)
//                .background(color = system_red, shape = RoundedCornerShape(60.dp))
//                .padding(start = 32.dp, top = 22.dp, end = 32.dp, bottom = 22.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Image(
//                    painter = painterResource(id = R.drawable.fireicon),
//                    contentDescription = "Fire Icon",
//                    modifier = Modifier
//                        .height(32.dp)
//                        .padding(1.dp)
//                )
//                Text(
//                    text = " 화재 발생",
//                    style = Headline
//                )
//            }
//
//            Text(
//                text = gateName,
//                style = Subtitle2,
//                color = Color.White
//            )
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // 🔥 화재 이미지
//        if (imageUrl.isNotEmpty()) {
//            AsyncImage(
//                model = imageUrl,
//                contentDescription = "Fire Image",
//                modifier = Modifier
//                    .width(320.dp)
//                    .height(180.dp)
//                    .clip(RoundedCornerShape(16.dp)),
//                contentScale = ContentScale.Crop,
//                onSuccess = {
//                    Log.d("FireNotificationCard", "✅ 이미지 로딩 성공")
//                },
//                onError = {
//                    Log.e("FireNotificationCard", "❌ 이미지 로딩 실패", it.result.throwable)
//                }
//            )
//        } else {
//            Text(
//                text = "이미지를 불러올 수 없습니다",
//                color = Color.White,
//                modifier = Modifier.padding(top = 12.dp)
//            )
//        }
//    }
//}

// 내가 기존에 만든 거
@Composable
fun FireNotificationCard(
    gateName: String,     // ✅ 개찰구 정보 (예: "B3 개찰구")
    imageUrl: String      // 🔥 화재 이미지 API URL
) {
    Log.d("FireNotificationCard", "🧩 파라미터 gateName=$gateName, imageUrl=$imageUrl")
    Column(
        modifier = Modifier
            .width(380.dp)
            .height(334.dp)
            .background(Color.Black, shape = RoundedCornerShape(40.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 🔥 상태 + 개찰구 정보
        Row(
            modifier = Modifier
                .width(360.dp)
                .height(76.dp)
                .background(color = system_red, shape = RoundedCornerShape(60.dp))
                .padding(start = 32.dp, top = 22.dp, end = 32.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.fireicon),
                    contentDescription = "Fire Icon",
                    modifier = Modifier
                        .height(32.dp) // 원하는 크기로 조정
                        .padding(1.dp)
                )
                Text(
                    text = " 화재 발생",
                    style = Headline
                )
            }

            Text(
                text = gateName,
                style = Subtitle2,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // 📸 화재 이미지 표시
        if (imageUrl.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .width(364.dp)
                    .height(214.dp)
                    .border(
                        width = 5.dp,
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(size = 30.dp)
                    )
                    .clip(RoundedCornerShape(size = 30.dp)) // 이미지도 같이 둥글게 클리핑
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Fire Image",
                    modifier = Modifier
                        .width(320.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    onSuccess = {
                        Log.d("FireNotificationCard", "✅ 이미지 로딩 성공")
                    },
                    onError = {
                        Log.e("FireNotificationCard", "❌ 이미지 로딩 실패", it.result.throwable)
                    }
                )
            }
        } else {
            Text(
                text = "이미지를 불러올 수 없습니다",
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}



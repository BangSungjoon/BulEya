package com.ssafy.jangan_mobile.ui.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.store.FireNotificationStore
import com.ssafy.jangan_mobile.ui.theme.Headline
import com.ssafy.jangan_mobile.ui.theme.Subtitle2
import com.ssafy.jangan_mobile.ui.theme.system_red//


@Composable
fun FireNotificationCard(
    beaconName: String,
    imageUrl: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onGuideClick: () -> Unit
) {
    Log.d("FireNotificationCard", "🧩 파라미터 imageUrl=$imageUrl")

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
                        .height(32.dp)
                        .padding(1.dp)
                )
                Text(
                    text = " 화재 발생",
                    style = Headline
                )
            }

            Text(
                text = beaconName,
                style = Subtitle2,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔥 화재 이미지
        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Fire Image",
                modifier = Modifier
                    .border(
                        width = 5.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .width(364.dp)
                    .height(214.dp),
                contentScale = ContentScale.Crop,
//                onSuccess = {
//                    Log.d("FireNotificationCard", "✅ 이미지 로딩 성공")
//                },
//                onError = {
//                    Log.e("FireNotificationCard", "❌ 이미지 로딩 실패", it.result.throwable)
//                }
            )
        } else {
            Log.w("FireNotificationCard", "⚠️ imageUrl이 비어 있음. 이미지 미표시")
            Text(
                text = "이미지를 불러올 수 없습니다",
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
fun StationStatusCard(
    stationName: String, // ✅ 역 이름
    status: String,      // ✅ 상태 (예: "화재 발생")
    gateName: String     // ✅ 개찰구 정보 (예: "B3 개찰구")
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🚉 역 정보 표시
        StationInfo(stationName = stationName)

        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color(0xFF90EE90), shape = CircleShape)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stationName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔥 상태 + 개찰구 정보
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.Red, shape = RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🔥 $status",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = gateName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}




//@Composable
//fun FireNotificationCard(
//    beaconName: String,
//    imageUrl: String,
//    isVisible: Boolean,
//    onDismiss: () -> Unit,
//    onGuideClick: () -> Unit
//) {
//    Log.d("FireNotificationCard", "🧩 파라미터 gateName=$beaconName, imageUrl=$imageUrl")
//
//    Column(
//        modifier = Modifier
//            .width(380.dp)
//            .height(334.dp)
//            .background(Color.Black, shape = RoundedCornerShape(40.dp))
//            .padding(vertical = 12.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        // 🔥 상태 + 개찰구 정보
//        Box(
//            modifier = Modifier
//                .width(360.dp)
//                .height(76.dp)
//                .background(color = Color.Transparent)
//        ) {
//            // 빨간 박스 전체
//            Row(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(color = system_red, shape = RoundedCornerShape(60.dp))
//                    .padding(horizontal = 24.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Image(
//                        painter = painterResource(id = R.drawable.fireicon),
//                        contentDescription = "Fire Icon",
//                        modifier = Modifier
//                            .height(32.dp)
//                            .padding(1.dp)
//                    )
//                    Text(
//                        text = " 화재 발생",
//                        style = Headline,
//                        color = Color.White
//                    )
//                }
//
//                Text(
//                    text = beaconName,
//                    style = Subtitle2,
//                    color = Color.White
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // 📸 화재 이미지 표시 (둥근 흰색 테두리 포함 박스)
//        Box(
//            modifier = Modifier
//                .width(364.dp)
//                .height(214.dp)
//                .border(
//                    width = 5.dp,
//                    color = Color(0xFFFFFFFF),
//                    shape = RoundedCornerShape(size = 30.dp)
//                )
//                .clip(RoundedCornerShape(size = 30.dp)) // 이미지도 같이 둥글게 클리핑
//        ) {
//            if (imageUrl.isNotEmpty()) {
//                AsyncImage(
//                    model = imageUrl,
//                    contentDescription = "Fire Image",
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clip(RoundedCornerShape(30.dp)),
//                    contentScale = ContentScale.Crop,
//                    onSuccess = {
//                        Log.d("FireNotificationCard", "✅ 이미지 로딩 성공")
//                    },
//                    onError = {
//                        Log.e("FireNotificationCard", "❌ 이미지 로딩 실패", it.result.throwable)
//                    }
//                )
//            } else {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "이미지를 불러올 수 없습니다",
//                        color = Color.White
//                    )
//                }
//            }
//        }
//    }
//}



@Composable
fun FireDetailBottomSheet(
    beaconName: String,
    imageUrl: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onGuideClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() } // 배경 클릭 시 닫기
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(412.dp)
                    .height(457.dp)
                    .background(
                        color = Color(0xFF1B1B1D),
                        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
                    )
                    .padding(start = 17.dp, top = 36.dp, end = 16.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🔠 개찰구 텍스트
                Text(
                    text = beaconName,
                    style = Headline.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 📸 화재 이미지
                Box(
                    modifier = Modifier
                        .width(379.dp)
                        .height(214.dp)
                        .border(4.dp, Color.White, shape = RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "화재 이미지",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // ✅ 대피 경로 찾기 버튼
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .background(Color(0xFF8AEA52))
                        .clickable { onGuideClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "대피 경로 찾기",
                        style = Headline.copy(color = Color.Black)
                    )
                }
            }
        }
    }
}


//// 내가 기존에 만든 거
//@Composable
//fun FireNotificationCard(
//    beaconName: String,
//    imageUrl: String,
//    isVisible: Boolean,
//    onDismiss: () -> Unit,
//    onGuideClick: () -> Unit
//) {
//    Log.d("FireNotificationCard", "🧩 파라미터 gateName=$beaconName, imageUrl=$imageUrl")
//    Column(
//        modifier = Modifier
//            .width(380.dp)
//            .height(334.dp)
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
//                        .height(32.dp) // 원하는 크기로 조정
//                        .padding(1.dp)
//                )
//                Text(
//                    text = " 화재 발생",
//                    style = Headline
//                )
//            }
//
//            Text(
//                text = beaconName,
//                style = Subtitle2,
//                color = Color.White
//            )
//        }
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // 📸 화재 이미지 표시
//        if (imageUrl.isNotEmpty()) {
//            Box(
//                modifier = Modifier
//                    .width(364.dp)
//                    .height(214.dp)
//                    .border(
//                        width = 5.dp,
//                        color = Color(0xFFFFFFFF),
//                        shape = RoundedCornerShape(size = 30.dp)
//                    )
//                    .clip(RoundedCornerShape(size = 30.dp)) // 이미지도 같이 둥글게 클리핑
//            ) {
//                AsyncImage(
//                    model = imageUrl,
//                    contentDescription = "Fire Image",
//                    modifier = Modifier
//                        .width(320.dp)
//                        .height(180.dp)
//                        .clip(RoundedCornerShape(16.dp)),
//                    contentScale = ContentScale.Crop,
//                    onSuccess = {
//                        Log.d("FireNotificationCard", "✅ 이미지 로딩 성공")
//                    },
//                    onError = {
//                        Log.e("FireNotificationCard", "❌ 이미지 로딩 실패", it.result.throwable)
//                    }
//                )
//            }
//        } else {
//            Text(
//                text = "이미지를 불러올 수 없습니다",
//                color = Color.White,
//                modifier = Modifier.padding(top = 12.dp)
//            )
//        }
//    }
//}
//
//

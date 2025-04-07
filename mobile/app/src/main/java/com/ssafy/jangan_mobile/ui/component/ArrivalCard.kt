package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.jangan_mobile.ui.theme.Body1
import com.ssafy.jangan_mobile.ui.theme.Headline
import kotlinx.coroutines.delay



@Composable
fun ArrivalCard(
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    dismissSeconds: Int = 5 // N초 설정 가능
) {
    var secondsLeft by remember { mutableStateOf(dismissSeconds) }

    // ⏳ N초 후 자동 닫힘
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        onDismiss()
    }

    Box(
        modifier = modifier
            .width(380.dp)
            .height(200.dp)
            .background(color = Color(0xFF1B1B1D), shape = RoundedCornerShape(40.dp))
            .padding(start = 10.dp, top = 14.dp, end = 10.dp, bottom = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "목적지 도착",
                    style = Headline.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${secondsLeft}초 뒤 경로 안내가 종료됩니다.",
                    style = Body1.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "경로 재안내를 원하시면 아래 버튼을 눌러주세요.",
                    style = Body1.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )
            }

            // ✅ 재안내 버튼 (타이머 숫자 없음)
            Box(
                modifier = Modifier
                    .width(360.dp)
                    .height(70.dp)
                    .background(color = Color(0xFF8AEA52), shape = RoundedCornerShape(60.dp))
                    .clickable { onRetry() },
//                    .padding(horizontal = 32.dp, vertical = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "경로 재안내",
                    style = Headline.copy(
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
//@Composable
//fun ArrivalCard(
//    onDismiss: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var secondsLeft by remember { mutableStateOf(3) }
//
//    // ⏳ 3초 후 자동 닫힘
//    LaunchedEffect(Unit) {
//        while (secondsLeft > 0) {
//            delay(5000)
//            secondsLeft--
//        }
//        onDismiss()
//    }
//
//    Box(
//        modifier = modifier
//            .width(380.dp)
//            .height(183.dp)
//            .background(color = Color(0xFF1B1B1D), shape = RoundedCornerShape(40.dp))
//            .padding(start = 10.dp, top = 12.dp, end = 10.dp, bottom = 12.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Text(
//                    text = "목적지 도착. 3초 뒤 경로 안내가 종료됩니다.",
//                    style = Headline.copy(
//                        color = Color.White,
//                        textAlign = TextAlign.Center)
//                    )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = "경로 재안내를 원하시면 아래 버튼을 눌러주세요.",
//                    style = Body1.copy(
//                        color = Color.White,
//                        textAlign = TextAlign.Center)
//                    )
//            }
//
//            // ✅ 확인 버튼 (초 포함)
//            Box(
//                modifier = Modifier
//                    .width(360.dp)
//                    .height(72.dp)
//                    .background(color = Color(0xFF8AEA52), shape = RoundedCornerShape(60.dp))
//                    .padding(start = 32.dp, top = 22.dp, end = 32.dp, bottom = 22.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(
//                        text = "경로 재안내",
//                        style = Headline.copy(
//                            color = Color.Black,
//                            textAlign = TextAlign.Center)
//                    )
//
//                    // 🕒 타이머 숫자
//                    Box(
//                        modifier = Modifier
//                            .size(28.dp)
//                            .background(Color.Black, shape = CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = secondsLeft.toString(),
//                            color = Color.White,
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

package com.ssafy.jangan_mobile.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.jangan_mobile.ui.theme.Subtitle1
import com.ssafy.jangan_mobile.ui.theme.primaryColor
import kotlinx.coroutines.delay

@Composable
fun EvacuationButton(
    modifier: Modifier = Modifier,
    isGuiding: Boolean,
    onClick: () -> Unit
) {
    val animatedOffsetY = rememberInfiniteTransition()

    val offsetY by animatedOffsetY.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing // 부드러운 ease-in-out 느낌
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 안내 중이면 offset을 0으로 (흔들림 X)
    val actualOffsetY = if (isGuiding) 0.dp else offsetY.dp

    Box(
        modifier = modifier
            .offset(y = actualOffsetY)
            .width(380.dp)
            .background(color = Color.Black, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isGuiding) "안내 종료하기" else "대피 경로 찾기",
            style = Subtitle1,
            color = primaryColor,
            textAlign = TextAlign.Center
        )
    }
}

//@Composable
//fun EvacuationButton(
//    modifier: Modifier = Modifier,
//    isGuiding: Boolean,
//    onClick: () -> Unit) {
//    var isGuiding by remember { mutableStateOf(false) } // ✅ 상태 기억 (안내 중인지 여부)
//
//    // 흔들림 애니메이션 상태
//    val offsetY = remember { Animatable(0f) }
//
//    // 흔들림 애니메이션 실행
//    LaunchedEffect(key1 = isGuiding) {
//        if (!isGuiding) {
//            while (true) {
//                offsetY.animateTo(
//                    targetValue = -6f,
//                    animationSpec = tween(
//                        durationMillis = 500,
//                        easing = FastOutSlowInEasing)
//                )
//                offsetY.animateTo(
//                    targetValue = 6f,
//                    animationSpec = tween(
//                        durationMillis = 500,
//                        easing = FastOutSlowInEasing)
//                )
//                offsetY.animateTo(
//                    targetValue = 0f,
//                    animationSpec = tween(
//                        durationMillis = 500,
//                        easing = FastOutSlowInEasing)
//                )
//                delay(1500) // 3초마다 한 번 흔들기
//            }
//        } else {
//            offsetY.snapTo(0f) // 안내 중이면 흔들림 초기화
//        }
//    }
//
//
//    Box(
//        modifier = modifier
//            .offset(y = offsetY.value.dp) // 흔들림 적용
//            .width(380.dp)
//            .background(color = Color.Black, shape = RoundedCornerShape(20.dp))
//            .clickable {
//                isGuiding = !isGuiding
//                onClick()
//            } // ✅ 클릭 시 상태 변경
//            .padding(24.dp), // 내부 패딩으로 버튼 높이 조절
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = if (isGuiding) "안내 종료하기" else "대피 경로 찾기", // ✅ 클릭에 따라 문구 변경
//            style = Subtitle1,
//            color = primaryColor,
//            textAlign = TextAlign.Center
//        )
//    }
//}

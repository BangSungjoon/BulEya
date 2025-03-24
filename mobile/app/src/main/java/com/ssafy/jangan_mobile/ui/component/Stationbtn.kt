package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.jangan_mobile.ui.theme.Subtitle2
import com.ssafy.jangan_mobile.ui.theme.primaryColor

@Composable
fun StationInfo(
    stationName: String // ✅ 역 이름을 동적으로 입력
) {
    Row(
        modifier = Modifier
            .width(380.dp)
            .height(45.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 왼쪽 연장된 초록색 선
        Box(
            modifier = Modifier
//                .border(width = 8.dp, color = primaryColor)
//                .padding(8.dp)
                .width(94.5.dp)
                .height(8.dp)
                .background(primaryColor)
        )

        // 중앙 역 이름 표시 (둥근 직사각형)
        Box(
            modifier = Modifier
                .width(182.dp)
                .height(45.dp)
                .background(color = Color.White, shape = RoundedCornerShape(100.dp))
                .border(width = 8.dp, color = primaryColor, shape = RoundedCornerShape(100.dp)),
//                .padding(8.dp)
//                .padding(horizontal = 16.dp, vertical = 12.dp) // 텍스트 여백
            contentAlignment = Alignment.Center
        ) {
                Text(
                    text = stationName,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = Subtitle2
                )
        }

        // 오른쪽 연장된 초록색 선
        Box(
            modifier = Modifier
                .width(94.5.dp)
                .height(8.dp)
                .background(primaryColor)
        )
    }
}

//@Preview(
//    showBackground = true,
//    widthDp = 393, // Note10 기준 (가로 px 1080 / 2.75 ≈ 393dp)
//    heightDp = 800,
//    fontScale = 1f
//)
//@Composable
//fun PreviewStationInfo() {
//    StationInfo(stationName = "강남역")
//}
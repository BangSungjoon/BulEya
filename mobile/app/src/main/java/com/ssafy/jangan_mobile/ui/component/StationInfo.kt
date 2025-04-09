package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
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
                .width(94.5.dp)
                .height(8.dp)
                .background(primaryColor)
        )

        // 중앙 역 이름 표시 (둥근 직사각형)
        Box(
            modifier = Modifier
                .width(182.dp)
                .height(61.dp) // 피그마대로 하면 좁은 것 같아서 볼더값만큼 더해줌
                .background(color = Color.White, shape = RoundedCornerShape(100.dp))
                .border(width = 8.dp, color = primaryColor, shape = RoundedCornerShape(100.dp)),
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


//@Composable
//fun StationStatusCard(
//    stationName: String, // ✅ 역 이름
//    status: String,      // ✅ 상태 (예: "화재 발생")
//    gateName: String     // ✅ 개찰구 정보 (예: "B3 개찰구")
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.Black, shape = RoundedCornerShape(16.dp))
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // 🚉 역 정보 표시
//        StationInfo(stationName = stationName)
//
//        Spacer(modifier = Modifier.height(20.dp))
//        Box(
//            modifier = Modifier
//                .fillMaxWidth(0.8f)
//                .background(Color(0xFF90EE90), shape = CircleShape)
//                .padding(vertical = 8.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = stationName,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // 🔥 상태 + 개찰구 정보
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .background(Color.Red, shape = RoundedCornerShape(16.dp))
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "🔥 $status",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//
//            Text(
//                text = gateName,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.White
//            )
//        }
//    }
//}

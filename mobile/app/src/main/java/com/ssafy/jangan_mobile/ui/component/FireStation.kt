package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.ui.theme.Headline
import com.ssafy.jangan_mobile.ui.theme.Subtitle2
import com.ssafy.jangan_mobile.ui.theme.system_red



@Composable
fun FireStation(
    stationName: String, // ✅ 역 이름
    status: String,      // ✅ 상태 (예: "화재 발생")
    gateName: String,     // ✅ 개찰구 정보 (예: "B3 개찰구")
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .background(Color(0xFF1B1B1D), shape = RoundedCornerShape(40.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ 상단 라벨 (역 이름)
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(50.dp))
                .border(
                    width = 4.dp,
                    color = Color(0xFF8AEA52),
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = stationName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔥 화재 발생 상태 카드
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(color = Color(0xFFEE5B5B), shape = RoundedCornerShape(60.dp))
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 🔥 아이콘 + 텍스트
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.fireicon),
                    contentDescription = "Fire Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // 🚪 개찰구
            Text(
                text = gateName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.ui.theme.Headline
import com.ssafy.jangan_mobile.ui.theme.Subtitle2



@Composable
fun FireStation(
    stationName: String,
    beaconName: String,
    imageUrl: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onGuideClick: () -> Unit
) {
    BoxWithConstraints {
        val cardWidth = maxWidth * 0.9f
        val cardHeight = cardWidth * (213f / 380f) // 기존 비율 유지

        Column(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .background(Color.Black, shape = RoundedCornerShape(size = 40.dp))
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 역 정보
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f), // 전체 높이의 45%
                contentAlignment = Alignment.Center
            ) {
                StationInfo(stationName = stationName)
            }

            Spacer(modifier = Modifier.height(10.dp))


            // 🔥 화재 발생 상태 카드
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .weight(0.45f)
                    .background(color = Color(0xFFEA5252), shape = RoundedCornerShape(60.dp))
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
                        text = "화재 발생",
                        style = Headline,
                        color = Color.Black
                    )
                }

                // 🚪 개찰구
                Text(
                    text = beaconName,
                    style = Subtitle2,
                    color = Color.White
                )
            }
        }
    }
}
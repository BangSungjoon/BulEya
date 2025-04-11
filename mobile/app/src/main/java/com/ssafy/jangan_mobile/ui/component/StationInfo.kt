package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
    stationName: String
) {
    BoxWithConstraints {
        val totalWidth = maxWidth * 0.9f // 전체 너비의 90% 사용
        val lineWidth = totalWidth * 0.25f
        val labelWidth = totalWidth * 0.5f
        val labelHeight = labelWidth * (70f / 182f) // 기존 비율 유지

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(labelHeight),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 왼쪽 선
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .background(primaryColor)
            )

            // 중앙 역 이름 박스
            Box(
                modifier = Modifier
                    .width(labelWidth)
                    .height(labelHeight)
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

            // 오른쪽 선
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .background(primaryColor)
            )
        }
    }
}


package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.ssafy.jangan_mobile.R

@Composable
fun FloorSelector(
    floors: List<String> = listOf("B1", "B2", "B3"),
    selectedFloor: String = "B2",
    onFloorSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(0.3f) // 전체 화면의 30% 높이 차지
            .fillMaxWidth(0.2f), // 전체 화면의 20% 너비 차지
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(2.dp, Color.LightGray, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                floors.forEach { floor ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // 모든 층을 동일한 높이로 설정
                            .background(if (floor == selectedFloor) Color(0xFF90EE90) else Color.White)
                            .clickable { onFloorSelected(floor) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = floor,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (floor == selectedFloor) Color.Black else Color.Gray
                        )
                    }
                }
            }
        }
    }
}


//@Preview
//@Composable
//fun PreviewFloorSelector() {
//    var selectedFloor by remember { mutableStateOf("B2") }
//
//    FloorSelector(
//        selectedFloor = selectedFloor,
//        onFloorSelected = { selectedFloor = it }
//    )
//}

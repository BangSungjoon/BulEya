package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun EvacuationButton(
    text: String = "대피 경로 찾기",
    isActive: Boolean = false, // 🔥 버튼 상태 (false: 검은색, true: 연두색)
    onClick: () -> Unit
) {
    val backgroundColor = if (isActive) Color(0xFF90EE90) else Color(0xFF1B1B1B) // ✅ 연두색 or 검은색
    val textColor = if (isActive) Color.Black else Color(0xFF90EE90) // ✅ 글자색 변경

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) // ✅ 버튼 높이 설정
            .background(color = backgroundColor, shape = RoundedCornerShape(24.dp))
            .clickable { onClick() }, // ✅ 클릭 이벤트 적용
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewEvacuationButton() {
//    var isActive by remember { mutableStateOf(false) }
//
//    EvacuationButton(
//        isActive = isActive,
//        onClick = { isActive = !isActive } // ✅ 버튼 클릭 시 색상 변경
//    )
//}
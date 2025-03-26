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
import com.ssafy.jangan_mobile.ui.theme.Subtitle1
import com.ssafy.jangan_mobile.ui.theme.primaryColor

@Composable
fun EvacuationButton2() {
    var isGuiding by remember { mutableStateOf(false) } // ✅ 상태 기억 (안내 중인지 여부)

    Box(
        modifier = Modifier
            .width(380.dp)
            .background(color = primaryColor, shape = RoundedCornerShape(20.dp))
            .clickable { isGuiding = !isGuiding } // ✅ 클릭 시 상태 변경
            .padding(24.dp), // 내부 패딩으로 버튼 높이 조절
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isGuiding) "안내 종료하기" else "대피 경로 찾기", // ✅ 클릭에 따라 문구 변경
            style = Subtitle1,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}


// 높이가 낮은 높이로 미리 정하면 이미 있는 내부 패딩에 글자가 눌려보일 수 있음 -> 패딩으로 높이를 줘야 함.


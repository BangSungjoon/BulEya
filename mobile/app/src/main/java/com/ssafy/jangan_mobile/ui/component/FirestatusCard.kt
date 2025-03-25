package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.ui.theme.Headline
import com.ssafy.jangan_mobile.ui.theme.Subtitle2
import com.ssafy.jangan_mobile.ui.theme.system_red


@Composable
fun StationStatusCard(
    stationName: String, // ✅ 역 이름
    status: String,      // ✅ 상태 (예: "화재 발생")
    gateName: String     // ✅ 개찰구 정보 (예: "B3 개찰구")
) {
    Column(
        modifier = Modifier
            .width(380.dp)
            .height(213.dp)
            .background(Color.Black, shape = RoundedCornerShape(40.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 🚉 역 정보 표시
        Box(
            modifier = Modifier
                .padding(vertical = 24.dp)
        ){
            StationInfo(stationName = stationName)
        }
        Spacer(modifier = Modifier.height(20.dp))

        // 🔥 상태 + 개찰구 정보
        Row(
            modifier = Modifier
                .width(360.dp)
                .height(76.dp)
                .background(color = system_red, shape = RoundedCornerShape(60.dp))
                .padding(start = 32.dp, top = 22.dp, end = 32.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.fireicon),
                    contentDescription = "Fire Icon",
                    modifier = Modifier
                        .height(32.dp) // 원하는 크기로 조정
                        .padding(1.dp)
                )
                Text(
                    text = " $status",
                    style = Headline
                )
            }

            Text(
                text = gateName,
                style = Subtitle2,
                color = Color.White
            )
        }
    }
}


//@Preview(showBackground = true, name = "Fire Status - Emergency")
//@Composable
//fun PreviewFireStatusCardEmergency() {
//    StationStatusCard(
//        stationName = "강남역",
//        status = "화재 발생",
//        gateName = "B3 개찰구"
//    )
//}

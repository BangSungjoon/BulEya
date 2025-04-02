package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.shadow
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.ui.theme.Subtitle1
import com.ssafy.jangan_mobile.ui.theme.gray100
import com.ssafy.jangan_mobile.ui.theme.gray300
import com.ssafy.jangan_mobile.ui.theme.gray400
import com.ssafy.jangan_mobile.ui.theme.primaryColor

@Composable
fun FloorSelector(
    modifier: Modifier = Modifier,
    floors: List<String> = listOf("B1", "B2", "B3"),
    selectedFloor: String = "B2",
    onFloorSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .width(72.dp)
            .height(192.dp)
            .shadow(elevation = 4.dp, spotColor = gray400, ambientColor = gray400)
            .border(width = 1.dp, color = gray300, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = gray100),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            floors.forEach { floor ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            if (floor == selectedFloor) primaryColor else gray100
                        )
                        .clickable { onFloorSelected(floor) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = floor,
                        style = Subtitle1,
                        color = if (floor == selectedFloor) Color.Black else gray400
                    )
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

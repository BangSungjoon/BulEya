package com.ssafy.jangan_mobile.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import com.ssafy.jangan_mobile.ui.theme.Subtitle1
import com.ssafy.jangan_mobile.ui.theme.gray100
import com.ssafy.jangan_mobile.ui.theme.gray300
import com.ssafy.jangan_mobile.ui.theme.gray400
import com.ssafy.jangan_mobile.ui.theme.primaryColor

@Composable
fun FloorSelector(
    modifier: Modifier = Modifier,
    floors: List<String> = listOf("B1", "B2", "B3"),
    selectedFloor: String,
    firefloor: List<String> = emptyList(),
    onFloorSelected: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        modifier = Modifier
            .width(72.dp)
            .height(192.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(elevation = 4.dp, spotColor = gray400, ambientColor = gray400)
            .border(width = 1.dp, color = gray300, shape = RoundedCornerShape(16.dp))
            .background(color = gray100),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            floors.forEach { floor ->
                val isFireFloor = firefloor.contains(floor)
                val isSelected = floor == selectedFloor

                val backgroundColor = when {
                    isFireFloor -> Color.Red.copy(alpha = blinkAlpha)
                    isSelected -> primaryColor
                    else -> gray100
                }

                val textColor = when {
                    isFireFloor && isSelected -> Color.Black
                    isFireFloor -> Color.White
                    isSelected -> Color.Black
                    else -> gray400
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(backgroundColor)
                        .clickable { onFloorSelected(floor) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = floor,
                        style = Subtitle1,
                        color = textColor
                    )
                }
            }
        }
    }
}

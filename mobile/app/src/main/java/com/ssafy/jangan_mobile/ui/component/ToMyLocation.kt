package com.ssafy.jangan_mobile.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.service.dto.CurrentLocationDto
import com.ssafy.jangan_mobile.ui.theme.gray300

@Composable
fun ToMyLocation(
    modifier: Modifier = Modifier,
    mapView: MapView,
    myLocation: CurrentLocationDto?,
    onClick: () -> Unit) {
    Box(
        modifier = modifier
            .padding(start = 10.dp)
            .width(50.dp)
            .height(50.dp)
            .border(width = 1.dp, color = gray300, shape = RoundedCornerShape(60.dp))
            //.paint(painterResource(id = R.drawable.to_my_location))
            .clip(RoundedCornerShape(60.dp))
            .clickable {
                val point = getPointByMyLocation(myLocation)
                if(point != null) {
                    mapView.mapboxMap.flyTo(
                        CameraOptions.Builder()
                            .center(getPointByMyLocation(myLocation))
                            .zoom(6.5)
                            .build(), mapAnimationOptions {
                            duration(1000L)
                        }
                    )
                }
                onClick()
            }, // ✅ 클릭 시 상태 변경
            //.padding(24.dp), // 내부 패딩으로 버튼 높이 조절
            contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.to_my_location),
            contentDescription = "내 위치로 이동 버튼",
            modifier = Modifier
                .size(50.dp), // 크기 조절 가능
            contentScale = ContentScale.Fit
        )
    }
}

fun getPointByMyLocation(myLocation: CurrentLocationDto?): Point? {
    if(myLocation == null || myLocation.coordX == null)
        return null
    return Point.fromLngLat(myLocation!!.coordX, myLocation!!.coordY)
}
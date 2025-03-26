package com.ssafy.jangan_mobile.ui.component

import android.widget.ImageView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.jangan_mobile.R
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

@Composable
fun ZoomableHighResMap(imageRes: Int) { // ✅ Int 타입으로 변경
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableStateOf(0f) }

    val animatedScale by animateFloatAsState(targetValue = scale, label = "ScaleAnimation")
    val animatedRotation by animateFloatAsState(targetValue = rotation, label = "RotationAnimation")

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotate ->
                    scale = (scale * zoom).coerceIn(1f, 4f)
                    offset += pan
                    rotation += rotate
                }
            }
    ) {
        Image(
            painter = painterResource(id = imageRes), // ✅ imageRes를 사용
            contentDescription = "지도",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = animatedScale,
                    scaleY = animatedScale,
                    translationX = offset.x,
                    translationY = offset.y,
                    rotationZ = animatedRotation
                )
        )
    }
}


//@Composable
//fun ZoomableMap(
//    imageRes: Int,
//    modifier: Modifier = Modifier
//){
//    var scale by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//    var rotation by remember { mutableStateOf(0f) }
//
//    val isPreview = LocalInspectionMode.current // ✅ 프리뷰 감지
////    val imageRes = getMapResourceByScale(scale) // ✅ 배율에 따른 이미지 변경
//
//    Box(
//        modifier = modifier
//            .let {
//                if (!isPreview) { // ✅ Preview에서는 pointerInput을 사용하지 않음
//                    it.pointerInput(Any()) {
//                        detectTransformGestures { _, pan, zoom, rotate ->
//                            scale = (scale * zoom).coerceIn(1f, 3f)
//                            offset += pan
//                            rotation += rotate
//                        }
//                    }
//                } else {
//                    it
//                }
//            }
//    ) {
//        AndroidView(
//            factory = { context ->
//                ImageView(context).apply {
//                    Glide.with(context)
//                        .load(imageRes)  // ✅ WEBP 적용
//                        .diskCacheStrategy(DiskCacheStrategy.ALL) // ✅ 캐싱 활용
//                        .into(this)
//                }
//            },
//            modifier = Modifier
//                .fillMaxSize()
//                .graphicsLayer(
//                    scaleX = scale,
//                    scaleY = scale,
//                    translationX = offset.x,
//                    translationY = offset.y,
//                    rotationZ = rotation // ✅ 회전 적용
//                )
//        )
//    }
//}
//@Composable
//fun ZoomableMap(
//    imageRes : Int,
//    modifier: Modifier = Modifier
//){
//    var scale by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//    var rotation by remember { mutableStateOf(0f) }
//
//    Box(
//        modifier = modifier
//            .fillMaxSize() // ✅ Box 크기 설정
//            .background(Color.Gray) // ✅ 확인용 배경색
//            .pointerInput(Any()) {
//                detectTransformGestures { _, pan, zoom, rotate ->
//                    println("Zoom: ✅ $zoom, Pan: $pan, Rotate: $rotate")
//                    scale = (scale * zoom).coerceIn(1f, 3f)
//                    offset += pan
//                    rotation += rotate
//                }
//            }
//    ) {
//        Image(
//            painter = painterResource(id = imageRes),
//            contentDescription = "지도",
//            modifier = Modifier
//                .fillMaxSize()
//                .graphicsLayer(
//                    scaleX = scale,
//                    scaleY = scale,
//                    translationX = offset.x,
//                    translationY = offset.y,
//                    rotationZ = rotation // 🔄 회전 적용
//                )
//        )
//    }
//}
//
@Preview
@Composable
fun PreviewZoomableMap() {
    Surface {
        ZoomableHighResMap(imageRes = R.drawable.b1)
    }
}
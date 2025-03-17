package com.ssafy.jangan_mobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ssafy.jangan_mobile.R

// Roboto 폰트 패밀리 설정
val roboto = FontFamily(
    Font(R.font.roboto_thin, FontWeight.Thin, FontStyle.Normal),
    Font(R.font.roboto_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.roboto_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.roboto_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.roboto_black, FontWeight.Black, FontStyle.Normal),
)

// 각 스타일별 TextStyle 설정
val Typography = Typography(

    headlineLarge = TextStyle( // Headline (24sp, Semi Bold)
        fontFamily = roboto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),

    titleLarge = TextStyle( // Subtitle1 (20sp, Semi Bold)
        fontFamily = roboto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),

    titleMedium = TextStyle( // Subtitle2 (18sp, Semi Bold)
        fontFamily = roboto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),

    bodyLarge = TextStyle( // Body1 (16sp, Regular)
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    bodyMedium = TextStyle( // Body2 (14sp, Regular)
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),

    labelLarge = TextStyle( // Caption (12sp, Regular)
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),

    labelMedium = TextStyle( // Overline (10sp, Regular)
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
)


// Set of Material typography styles to start with
//val Typography = Typography(
//
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//
//)
//
//// Set of Material typography styles to start with
//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)
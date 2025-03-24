package com.ssafy.jangan_mobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
val Headline = TextStyle( // Headline (24sp, Semi Bold)
        fontFamily = roboto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    )

val Subtitle1 = TextStyle( // Subtitle1 (20sp, Semi Bold)
        fontFamily = roboto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    )

val Subtitle2 = TextStyle( // Subtitle2 (18sp, Semi Bold)
        fontFamily = roboto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    )

val Body1 = TextStyle( // Body1 (16sp, Regular)
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )

val Body2 = TextStyle( // Body2 (14sp, Regular)
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )

val Caption = TextStyle( // Caption (12sp, Regular)
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )

val Overline = TextStyle( // Overline (10sp, Regular)
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )


// Set of Material typography styles to start with
val Typography = Typography(

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
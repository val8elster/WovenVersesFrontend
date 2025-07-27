package com.example.verseverwebt.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.verseverwebt.R

val inspiration = FontFamily(
    Font(R.font.inspiration_regular)
)
val playfair = FontFamily(
    Font(R.font.playfair_display)
)

val CustomTypography = Typography(
    titleLarge = TextStyle(
        fontSize = 45.sp,
        letterSpacing = 0.sp,
        fontFamily = playfair,

        ),
    titleMedium = TextStyle(
        fontSize = 32.sp,
        letterSpacing = 0.sp,
        fontFamily = inspiration,
    ),
    bodyLarge = TextStyle(
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        fontFamily = playfair,
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        letterSpacing = 0.sp,
        fontFamily = playfair,
    ),
    bodySmall = TextStyle(
        fontSize = 5.sp,
        letterSpacing = 0.sp,
        fontFamily = playfair,
    )

)
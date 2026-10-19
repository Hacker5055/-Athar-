package com.athar.athar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/// سُلَّمُ الكتابة في «أثر».
///
/// ⚠️ **تباعدُ الأسطرِ أوسعُ من الافتراضي**: الحرفُ العربيُّ له
/// صعودٌ ونزولٌ أطولُ من اللاتيني، فالأسطرُ الضيّقة تقطع النزول
/// وتلصق السطرَ بسطره.
///
/// ⚠️ و**تباعدُ الحروف صفرٌ**: الأرقامُ السالبةُ في سُلَّم ماتيريال
/// تصلح للّاتيني، والعربيُّ لا يحتملها — فتتناثر حروفُه.
private val lineHeight = { size: Int -> (size * 1.55f).sp }

val AtharTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp,
        lineHeight = lineHeight(52),
        letterSpacing = 0.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = lineHeight(42),
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = lineHeight(34),
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = lineHeight(30),
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = lineHeight(26),
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = lineHeight(22),
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = lineHeight(21),
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = lineHeight(17),
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = lineHeight(15),
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = lineHeight(16),
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = lineHeight(14),
        letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = lineHeight(12),
        letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = lineHeight(14),
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = lineHeight(12),
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = lineHeight(11),
        letterSpacing = 0.sp,
    ),
)

/// نمطُ نصِّ القرآن والأذكار — أميري، بتباعدٍ أوسعَ لأن الحروف
/// النستعليقيةُ لها نزولٌ طويل.
val QuranTextStyle = TextStyle(
    fontFamily = AmiriFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 46.sp,
    letterSpacing = 0.sp,
)

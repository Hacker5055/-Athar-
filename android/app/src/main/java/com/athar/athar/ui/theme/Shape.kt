package com.athar.athar.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/// أركانُ «أثر» — مستديرةٌ ودافئة.
///
/// ⚠️ **لا حادّةٌ ولا دائريةٌ تمامًا**: الحادّةُ (٢dp) تجعل الشاشةَ
/// آلية، والدائريةُ تمامًا (٥٠٪) تجعل البطاقةَ تختفي عن الصفحة.
/// والضعةَ عشرَ استدارةَ توازنٌ بين الأمرين.
val AtharShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

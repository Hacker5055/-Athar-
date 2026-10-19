package com.athar.athar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/// حالةٌ فارغةٌ واحدةٌ لكلِّ التطبيق.
///
/// ⚠️ **الفراغُ ليس خطأً**: صفحةٌ بيضاءُ بنصٍّ واحدٍ في وسطها
/// تُقرأ كأنّ التطبيقَ معطّل. والأيقونةُ في دائرةٍ نعناعيّة
/// تقول «هنا لا شيء» بلطف، والعنوانُ يقول «لماذا».
///
/// ⚠️ والنصُّ لا يوحي بالاتهام: «لا توجد نتائج» تُقرأ كأنّ البحث
/// فشل، و«لم تُحفظ آياتٌ بعد» تُقرأ كحالةٍ لا كعيب.
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    /// فعلٌ اختياريٌّ تحتَ الحالة — «أعد المحاولة» في خطأِ
    /// التحميل مثلًا. فالحالةُ التي لا عملَ فيها تُركتْ
    /// للمستخدمِ حائرًا أمامَ ما لا يُشفي.
    action: (@Composable () -> Unit)? = null,
) {
    // ⚠️ **العرضُ فقطُ لا الارتفاعُ**: `fillMaxSize` داخل عمودٍ
    // يأخذُ ارتفاعَ العمودِ كلَّه فيُزيحُ ما فوقَه. فالارتفاعُ
    // يقرّرُه مَن ينادي (وزنٌ في عمود، أو `fillMaxSize` في صندوق).
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(34.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        if (body != null) {
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        if (action != null) {
            Box(modifier = Modifier.padding(top = 16.dp)) {
                action()
            }
        }
    }
}

package com.athar.athar.ui.quran

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.quran.LastRead
import com.athar.athar.quran.QuranProgress

/// بطاقةُ «تابع القراءة» — في الصفحةِ الرئيسيةِ وتبويبِ القرآن.
///
/// ⚠️ **واحدةٌ في مكانين**: كان موضعُ القراءةِ معروفًا في الرئيسية
/// فقط، فمن فتح تبويبَ القرآن ليقرأ — وهو أغلى استعمالٍ للتطبيق —
/// لم يجدْ موضعَه إلا برجوعٍ إلى التبويبِ الأوّل.
///
/// ⚠️ **تختفي إن لم يُقرأ شيء**: اختصارٌ إلى لا شيءٍ ليس اختصارًا.
///
/// ⚠️ **شريطُ التقدّمِ تحتِ السطر**: الرقمُ يقول «كم بقي»
/// والشريطُ يقول «من أين بدأ» — والاثنان يُقرآن بنظرة.
@Composable
fun ContinueReadingCard(
    last: LastRead?,
    onOpen: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    if (last == null) return
    Card(
        onClick = { onOpen(last.surah, last.ayah) },
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = s.continueReading,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${last.surahName} · ${s.quranAyah} ${last.ayah}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${QuranProgress.percent(last.surah, last.ayah)}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LinearProgressIndicator(
            progress = { QuranProgress.percent(last.surah, last.ayah) / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

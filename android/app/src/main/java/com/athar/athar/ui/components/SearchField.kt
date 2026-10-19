package com.athar.athar.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/// حقلُ بحثٍ واحدٌ لكلِّ التطبيق.
///
/// ⚠️ **حقلٌ واحدٌ بأيقونةٍ واحدة**: كان البحثُ في أربعة مواضع،
/// اثنان منها بأيقونةِ بحثٍ واثنان بلا أيقونة، فمرَّ على حقلٍ في
/// «القرآن» وآخرُ في «الأذكار» فحسّ أنّه تطبيقان.
///
/// ⚠️ **الأيقونةُ تشرحُ الحقلَ قبلَ الكتابة**: حقلٌ بلا أيقونةٍ
/// يبدو مجرّدَ مساحةٍ بيضاء، والأيقونةُ تقول «ابحث» بلا كلمات.
///
/// ⚠️ **استدارةٌ كسائرِ الحقول**: حقلُ البحثِ حقلٌ كسائرِ الحقول،
/// فليَكُنْ شكلُه واحدًا لا زوايا حادّةً في وسطِ التطبيقِ الدافئ.
@Composable
fun AtharSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    /// الهامشُ الجانبيُّ للحقل — ١٦ في أكثرِ الشاشات، و١٢ في
    /// «القرآن» ليبقى الفهرسُ والنتائجُ متساويَين.
    gutter: Dp = 16.dp,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = singleLine,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = gutter, vertical = 8.dp),
    )
}

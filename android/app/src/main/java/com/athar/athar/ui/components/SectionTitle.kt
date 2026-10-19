package com.athar.athar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/// عنوانُ قسمٍ في صفحة.
///
/// ⚠️ **مقاسٌ واحدٌ لكلِّ الأقسام**: كان «المزيد» تسعةَ أقسامٍ،
/// لكلٍّ منها عنوانٌ بمقاسٍ مختلف (٢٤ و٢٢ و٢٠ و١٧)، فبدا القسمُ
/// التاسعُ صفحةً من تطبيقٍ آخر. والعنوانُ هنا بحجمِ العناوين.
///
/// ⚠️ والشرحُ تحتَه **اختياريّ**: العنوانُ وحده يكفي، والشرحُ
/// يُستعمل حين يحتاج القسمُ تعريفًا بجملةٍ واحدة.
///
/// ⚠️ **بلا حشوٍّ أفقيّ**: بعضُ الصفحاتِ تحشو نفسها مسبقًا، فحشوٌ
/// هنا يضاعفُ الهامشِ في تلك ويقسِمُه في سواها.
@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

/// عنوانُ فرعٍ داخل القسم: أصغرُ من عنوان القسم، وأقربُ إلى النص.
///
/// ⚠️ **بين [SectionTitle] و[SubHeading] فرقٌ في المرتبة**: الأوّلُ
/// يقول «أنت في هذا القسم»، وهذا يقول «وهذه إحدى بناته».
/// وحجمُه ١٧ كما كان، فلا يتغيّرُ ما اعتاده المستخدم.
@Composable
fun SubHeading(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(top = 16.dp, bottom = 6.dp),
    )
}

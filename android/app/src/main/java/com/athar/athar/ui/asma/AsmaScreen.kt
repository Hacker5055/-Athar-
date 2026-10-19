package com.athar.athar.ui.asma

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.asma.AsmaRepository
import com.athar.athar.asma.AsmaName
import com.athar.athar.asma.AsmaSearch
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.i18n.localDigits
import com.athar.athar.ui.components.EmptyState
import com.athar.athar.ui.components.SectionTitle
import com.athar.athar.ui.components.AtharSearchField
import androidx.compose.foundation.layout.PaddingValues

/// الأسماء الحسنى — قائمة تبحث بالاسم أو بالمعنى.
///
/// ⚠️ **المعاني بالعربية فقط، عن قصد**: لم نجد مصدرًا مرخّصًا لترجمتها،
/// ولن نكتب نحن نصًا دينيًا. والملاحظة تُعرض للمستخدم صراحة (لا تصمت
/// عنها) — لأن الصمت هنا يوحي بأن العربية هي اللغة الوحيدة المعتمدة.
@Composable
fun AsmaScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val all = remember { AsmaRepository(context).names() }
    val shown = remember(query, all) { AsmaSearch.search(query, all) }

    Column(modifier = modifier.fillMaxSize()) {
        SectionTitle(title = s.moreAsma, modifier = Modifier.padding(horizontal = 16.dp))
        AtharSearchField(
            value = query,
            onValueChange = { query = it },
            label = s.asmaSearchHint,
        )

        if (shown.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Search,
                title = s.asmaEmpty,
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(shown) { name -> NameCard(name) }
                item {
                    Text(
                        s.asmaNote,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    )
                }
            }
        }
    }
}

/// ⚠️ لا يقرأ `s` — دوال `@Composable` المستقلة لا ترى متغيّرات الأب.
@Composable
private fun NameCard(name: AsmaName, modifier: Modifier = Modifier) {
    // ⚠️ **الأرقامُ حسب اللغة**: «٢٥» في واجهةٍ فرنسيةٍ خطأٌ
    // ظاهر، و«25» في واجهةٍ عربيةٍ كذلك.
    val digits = localDigits()
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ⚠️ **الرقمُ في دائرٍ**: تسعةٌ وتسعون اسمًا، والرقمُ هو
            // ما يُحفظ به ترتيبُها. وهو في دائرٍ صغيرةٍ لا في نصٍّ
            // عادي.
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = digits(name.id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    name.nameArabic,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    // ⚠️ **نسخٌ لا سانس`: اسمُ الله اسمٌ يُكتب
                    // ويُنطق، فيستحقّ خطًّا يكتبُه لا خطَّ شاشات.
                    fontFamily = AmiriFamily,
                )
                Text(
                    name.meaning,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

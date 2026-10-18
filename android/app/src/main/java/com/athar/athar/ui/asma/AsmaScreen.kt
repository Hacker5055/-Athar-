package com.athar.athar.ui.asma

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.asma.AsmaRepository
import com.athar.athar.asma.AsmaName
import com.athar.athar.asma.AsmaSearch
import com.athar.athar.i18n.LocalStrings

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

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(s.asmaSearchHint) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        )

        if (shown.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    s.asmaEmpty,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(24.dp),
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(shown) { name -> NameCard(name) }
                item {
                    Text(
                        s.asmaNote,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                    )
                }
            }
        }
    }
}

/// ⚠️ لا يقرأ `s` — دوال `@Composable` المستقلة لا ترى متغيّرات الأب.
@Composable
private fun NameCard(name: AsmaName, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                name.nameArabic,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
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

package com.athar.athar.ui.adhkar

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.content.ContextCompat
import com.athar.athar.adhkar.AdhkarProgressStore
import com.athar.athar.adhkar.AdhkarSections
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.adhkar.AdhkarRepository
import com.athar.athar.adhkar.CustomAdhkar
import com.athar.athar.adhkar.CustomAdhkarStore
import com.athar.athar.adhkar.CustomDhikr
import com.athar.athar.adhkar.AdhkarSection
import com.athar.athar.adhkar.AdhkarSources
import com.athar.athar.adhkar.Dhikr
import com.athar.athar.prayer.CitySearch
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.ui.components.AtharTopBar
import com.athar.athar.ui.components.EmptyState
import com.athar.athar.ui.components.AtharSearchField
import com.athar.athar.ui.components.AtharPageTitle
import com.athar.athar.ui.theme.AtharPageMargin
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AdhkarScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var sections by remember { mutableStateOf<List<AdhkarSection>?>(null) }
    var query by remember { mutableStateOf("") }
    var openSection by remember { mutableStateOf<AdhkarSection?>(null) }
    var showMine by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { sections = AdhkarRepository.load(context) }

    if (showMine) {
        CustomAdhkarScreen(onBack = { showMine = false }, modifier = modifier)
        return
    }

    val current = openSection
    if (current != null) {
        SectionScreen(
            section = current,
            onBack = { openSection = null },
            modifier = modifier,
        )
        return
    }

    val list = sections
    Column(modifier = modifier.fillMaxSize()) {
        // ⚠️ **عنوانُ الفصلّ**: الأذكارُ وحدَها كانت تبدأُ
        // بعنوان، والقرآنُ والسبحةُ والقبلةُ والمزيدُ تبدأُ بمحتواها
        // مباشرةً — فكان نصفُ التطبيقِ بلا بابٍ.
        AtharPageTitle(
            title = s.tabAdhkar,
            modifier = Modifier.padding(horizontal = AtharPageMargin),
        )

        if (list == null) {
            // ⚠️ **الوزنُ لا الامتلاءُ**: الامتلاءُ يأخذُ ارتفاعَ
            // الشاشةِ كلَّه فوقَ العنوانِ فيُقصَّ ما تحته.
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AtharSearchField(
                value = query,
                onValueChange = { query = it },
                label = s.adhkarSearchHint,
            )

            val q = CitySearch.normalize(query.trim())
            val filtered = remember(q, list) {
                if (q.isEmpty()) {
                    list
                } else {
                    list.filter { q in CitySearch.normalize(it.title) }
                }
            }

            // ⚠️ أذكار بعد الصلاة مثبّتة في الأعلى: تُقرأ خمس مرات يوميًّا،
            // ف tapping إضافي في كل مرة عبء متكرّر لا مرة واحدة.
            val pinned = remember(q, list) {
                if (q.isEmpty()) AdhkarSections.postPrayer(list) else null
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                // ⚠️ «أذكاري» **أولًا**: ما كتبه المستخدم يبحث عنه
                // أكثر مما يبحث في الأبواب، وهو ملكه وحده.
                if (q.isEmpty()) {
                    item {
                        AdhkarSectionCard(
                            title = s.adhkarMine,
                            subtitle = s.adhkarMineBody,
                            onClick = { showMine = true },
                        )
                    }
                }
                if (pinned != null) {
                    item {
                        AdhkarSectionCard(
                            title = s.adhkarAfterPrayer,
                            subtitle = s.tArgs(
                                "adhkar_items_count",
                                "n" to pinned.items.size,
                            ),
                            onClick = { openSection = pinned },
                            preview = pinned.items.firstOrNull()?.text,
                        )
                    }
                }
                items(filtered.filter { it.title != pinned?.title }) { section ->
                    AdhkarSectionCard(
                        title = section.title,
                        subtitle = s.tArgs(
                            "adhkar_items_count",
                            "n" to section.items.size,
                        ),
                        onClick = { openSection = section },
                        preview = section.items.firstOrNull()?.text,
                    )
                }
                item {
                    Text(
                        text = AdhkarSources.ATTRIBUTION,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionScreen(
    section: AdhkarSection,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var counts by remember(section.title) {
        mutableStateOf(AdhkarProgressStore.load(context, section.title, section.items.size))
    }

    Column(modifier = modifier.fillMaxSize()) {
        AtharTopBar(
            title = section.title,
            onBack = onBack,
            titleSize = 18.sp,
            divider = true,
        ) {
            if (counts.any { it > 0 }) {
                IconButton(onClick = {
                    counts = List(section.items.size) { 0 }
                    AdhkarProgressStore.reset(context, section.title)
                }) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = s.reset)
                }
            }
        }

        // ⚠️ **الوزنُ لا الامتلاءُ**: القائمةُ تأخذُ ما بقي من
        // الشاشةِ بعد الشريطِ والعنوان، لا الشاشةَ كلَّها.
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(section.items) { index, dhikr ->
                DhikrCard(
                    dhikr = dhikr,
                    done = counts.getOrElse(index) { 0 },
                    onTap = {
                        val before = counts.getOrElse(index) { 0 }
                        val next = (before + 1).coerceAtMost(dhikr.count)
                        if (next != before) {
                            // ⚠️ نوسّع القائمة بدل `it[index] =` المباشر: فهرس
                            // خارج المدى يعني انهيارًا، لا ذكرًا مفقودًا
                            val updated = counts.toMutableList()
                            while (updated.size <= index) updated.add(0)
                            updated[index] = next
                            counts = updated
                            AdhkarProgressStore.save(context, section.title, updated)
                            if (next == dhikr.count) buzz(context)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun DhikrCard(dhikr: Dhikr, done: Int, onTap: () -> Unit) {
    val finished = done >= dhikr.count
    Card(
        onClick = onTap,
        modifier = Modifier
            .fillMaxWidth()
            // ⚠️ **الحاشيةُ ستّةَ عشرَ** كسائرِ البطاقات: الأذكارُ
            // وحدَها كانت على اثني عشر، فبدت ناقصةً في قائمة
            // بطاقاتٍ كلُّها بعرضٍ واحد.
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (finished) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dhikr.text,
                    fontSize = 17.sp,
                    lineHeight = 30.sp,
                    fontFamily = AmiriFamily,
                    textAlign = TextAlign.Start,
                )
                if (dhikr.reference.isNotEmpty()) {
                    Text(
                        text = dhikr.reference,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
            when {
                // ذكر يُقال مرة: علامة صح تكفي، ولا حلقة تقدّم
                dhikr.count == 1 && finished ->
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                dhikr.count > 1 -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(46.dp),
                ) {
                    CircularProgressIndicator(
                        progress = done.coerceAtLeast(0).toFloat() / dhikr.count.toFloat(),
                        modifier = Modifier.fillMaxSize(),
                    )
                    if (finished) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Text(
                            text = "$done",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

/// اهتزاز خفيف عند إتمام العدد (docs/02 · C4).
private fun buzz(context: Context) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java) ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        runCatching {
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}

/// أذكار يكتبها المستخدم لنفسه.
///
/// ⚖️ **لماذا شاشة منفصلة لا باب داخل الكتاب؟** أذكار الكتاب من حصن
/// المسلم، وكل واحد منها بتخريجه. وذكر يكتبه المستخدم **لا تخريج له**،
/// فوضعه في الباب نفسه يوهم أن له من السند ما لغيره.
@Composable
private fun CustomAdhkarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var mine by remember { mutableStateOf(CustomAdhkarStore.load(context)) }
    var done by remember { mutableStateOf(CustomAdhkarStore.done(context)) }
    var draft by remember { mutableStateOf("") }
    var target by remember { mutableStateOf(1) }

    // ⚠️ الحفظ **فوري** عند كل تغيير: المستخدم قد يغلق التطبيق بعد
    // الكتابة، وضياع ذكر كتبه بيده أسوأ من لا ميزة أصلًا.
    fun commit(next: List<CustomDhikr>) {
        mine = next
        CustomAdhkarStore.save(context, next)
    }

    Column(modifier = modifier.fillMaxSize()) {
        AtharTopBar(
            title = s.adhkarMine,
            onBack = onBack,
        )

        // ⚖️ التنبيه **دائم** لا مرة واحدة: كل ذكر هنا بلا تخريج،
        // والنصّ يزول مع التمرير، والتكرار هو ما يبقى.
        Text(
            text = s.adhkarMineBody,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text(s.adhkarAddHint) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
                maxLines = 4,
            )
            IconButton(
                onClick = {
                    val text = CustomAdhkar.sanitize(draft)
                    if (text.isBlank()) return@IconButton
                    // ⚠️ المعرّف من الوقت لا من الترتيب: حذف ذكر من
                    // المنتصف كان يعيد استخدام معرّفه فينقل عدّاده.
                    val id = "u${System.currentTimeMillis().toString(36)}"
                    commit(mine + CustomDhikr(id, text, target))
                    draft = ""
                    target = 1
                },
            ) {
                Icon(Icons.Filled.Add, contentDescription = s.adhkarAdd)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = s.adhkarRepeatCount,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp),
            )
            // ⚠️ درج ثابت لا حقل حرّ: العدّ في الأذكار درجات معلومة
            // (٣ و٧ و٣٣ و١٠٠)، وحقلٌ حرّ يفتح باب «١٧» بلا معنى.
            for (n in listOf(1, 3, 7, 33, 100)) {
                TextButton(onClick = { target = n }) {
                    Text(
                        text = "$n",
                        fontWeight = if (target == n) FontWeight.Bold else FontWeight.Normal,
                        color = if (target == n) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }

        if (mine.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Edit,
                title = s.adhkarMineEmpty,
                modifier = Modifier.weight(1f),
            )
            return@Column
        }

        // ⚠️ **الوزنُ لا الامتلاءُ**: القائمةُ تأخذُ ما بقي من
        // الشاشةِ بعد الحقول، لا الشاشةَ كلَّها.
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(mine, key = { it.id }) { item ->
                val current = done[item.id] ?: 0
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.text)
                        Text(
                            text = "$current / ${item.target}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (current >= item.target) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    TextButton(
                        onClick = {
                            // ⚠️ الوصول إلى الهدف **يعيد العدّ إلى صفر**:
                            // التسبيح يتكرّر، والبقاء على «٣٣/٣٣» للأبد
                            // يجعل الذكر يُقرأ مرة واحدة ثم يُهجر.
                            val next = if (current + 1 >= item.target) 0 else current + 1
                            val map = if (next == 0) done - item.id else done + (item.id to next)
                            done = map
                            CustomAdhkarStore.saveDone(context, map)
                        },
                    ) {
                        Text(text = "$current")
                    }
                    IconButton(
                        onClick = {
                            commit(mine.filter { it.id != item.id })
                            // ⚠️ العدّاد يُحذف مع الذكر: بقايا عدّاد
                            // لذكر محذوف تعود لو أُضيف ذكر بمعرّفه.
                            val map = done - item.id
                            done = map
                            CustomAdhkarStore.saveDone(context, map)
                        },
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = s.adhkarDelete)
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

/// بابٌ من أبواب الأذكار: بطاقةٌ باسم الباب وعدد أذكاره.
///
/// ⚠️ **السهمُ يتقلّب مع الاتجاه**: في العربية يمينُك البداية،
/// وفي الفرنسية يسارُك — والسهمُ الذي لا يتقلّب يجعل الباب
/// يشير إلى اليمين دائمًا.
@Composable
private fun AdhkarSectionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    /// أوّلُ ذكرٍ في الباب — سطرٌ واحدٌ منه.
    preview: String? = null,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AtharPageMargin, vertical = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start,
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (preview != null) {
                // ⚠️ **عيّنةٌ من البابِ بسطرٍ واحد**: الاسمُ والعددُ
                // يقولان كم في الباب، ولا يقولان ماذا فيه. فمن يبحث
                // عن ذكرٍ بعينه كان عليه فتحُ كلِّ بابٍ حتى يجده.
                // وهي بأميري، لأنها كلمةُ ذِكر.
                Text(
                    text = preview,
                    fontFamily = AmiriFamily,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

package com.athar.athar.ui.tasbih

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.athar.athar.athary.HabitId
import com.athar.athar.athary.TrackerStore
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.tasbih.Speaker
import com.athar.athar.tasbih.TasbihCounter
import com.athar.athar.tasbih.TasbihPhrases
import com.athar.athar.tasbih.TasbihRanges
import com.athar.athar.tasbih.TasbihSession
import com.athar.athar.tasbih.TasbihSessionStore
import com.athar.athar.tasbih.TasbihSessions
import com.athar.athar.tasbih.TasbihSpeech
import com.athar.athar.tasbih.TasbihSpeechStore
import com.athar.athar.tasbih.TasbihStore
import com.athar.athar.tasbih.TasbihVolumeStore
import com.athar.athar.tasbih.VolumeCounter
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.ui.theme.AtharCream
import com.athar.athar.ui.theme.AtharHeroBottom
import com.athar.athar.ui.theme.AtharHeroTop
import com.athar.athar.ui.theme.AtharPrimaryContainer
import com.athar.athar.i18n.localDigits
import java.time.LocalDate
import java.time.ZoneId
import com.athar.athar.ui.components.AtharPageTitle
import com.athar.athar.ui.theme.AtharPageMargin

/// السبحة (E1–E4 من docs/02).
///
/// ⚠️ الدائرة **كلها** هدف النقر لا زرّ صغير في منتصفها: التسبيح يُعدّ
/// بالمئات، والإصابة مهمة حين لا ينظر المستخدم إلى الشاشة أصلًا.
@Composable
fun TasbihScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val digits = localDigits()
    val context = LocalContext.current

    // ⚠️ إبقاء الشاشة مضاءة (E4): من يسبّح مئة لا يريد أن يلمس
    // الشاشة كل ١٥ ثانية لئلا تنطفئ. و`onDispose` يردّها، فلا تبقى
    // مضاءة بعد مغادرة الشاشة فتستنزف البطارية.
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    var phraseId by remember { mutableStateOf(TasbihStore.loadPhraseId(context)) }
    val phrase = TasbihPhrases.byId(phraseId)
    var target by remember { mutableIntStateOf(TasbihStore.loadTarget(context, phrase)) }
    var state by remember { mutableStateOf(TasbihStore.loadState(context)) }

    // ⚠️ **الجلسة** تُتتبَّع بنقرةٍ بنقرة: فالمستعمل يسبّح ثم
    // يمضي، فلا معنى لجلستٍ لا تُعرف إلا حين يقطعها.
    //
    // ⚠️ والجلسة القائمة **محفوظة**: فمن ترك الشاشة ورجع بعد
    // دقيقةٍ كجلسةٍ جارية، لا بدأ من جديد. والإلّا ضاعت
    // الجلسة بمجرّد أن ينتقل بين الألسنة.
    var session by remember { mutableStateOf(TasbihSessionStore.loadCurrent(context)) }
    var sessions by remember { mutableStateOf(TasbihSessionStore.load(context)) }

    // ⚠️ النطق **مُطفأ افتراضيًا** (C10): من يسبّح في المسجد
    // قد لا يريد صوته. والمحرّك **لا يُشغَّل أصلًا** إلا إن
    // طلبه المستخدم — ويُحرَّر بمغادرة الشاشة أو إطفائه.
    var speakEnabled by remember { mutableStateOf(TasbihSpeechStore.isEnabled(context)) }

    // ⚠️ **أزرار الصوت** (E5): تُلتقط في هذه الشاشة وحدها، ومطفأةٌ
    // افتراضيًا. فمن يريدها فعّلها، والباقي يعمل كالمعتاد.
    var volumeEnabled by remember { mutableStateOf(TasbihVolumeStore.isEnabled(context)) }
    val speaker = remember(speakEnabled, context) {
        if (speakEnabled) Speaker(context) else null
    }
    DisposableEffect(speaker) {
        onDispose { speaker?.shutdown() }
    }

    // ⚠️ الجلسة القديمة **تُغلق عند الدخول**: فمن غاب ساعةً
    // ثم عاد فسبّح — فجلستُه الأولى يجب أن تُحفظ في السجلّ قبل
    // أن تبدأ الثانية، لا أن تُشطب.
    //
    // ⚠️ وتُصفَّر بعد حفظها: لو بقيت في الذاكرة لأُضيفت مرّتين
    // — مرّةً هنا ومرّةً عند النقرة التالية.
    LaunchedEffect(Unit) {
        val now = System.currentTimeMillis()
        val stale = TasbihSessions.closeIfStale(session, now) ?: return@LaunchedEffect
        sessions = TasbihSessions.add(sessions, stale)
        session = null
        TasbihSessionStore.save(context, sessions)
        TasbihSessionStore.saveCurrent(context, null)
    }

    fun persist() = TasbihStore.save(context, phraseId, target, state)

    /// يُعلّم «أثري» أن دورةَ تسبيحٍ اكتملت (E6 ← دمج).
    ///
    /// ⚠️ **إضافةٌ لا تبديل**: فمن أتمّ دورتين اليوم لا تُلغى
    /// علامةُ الأولى.
    fun markTasbihHabit() {
        val today = LocalDate.now()
        val next = TrackerStore.load(context).mark(today, HabitId.TASBIH).pruned(today)
        TrackerStore.save(context, next)
    }

    /// ⚠️ **العدُّ noble واحد** من مكانين: نقرةٌ على الدائرة أو
    /// ضغطةُ زرّ الصوت (E5). فلو تفرّقت صار في الشاشة عدّادان
    /// لا يعملان معًا — والإجمالي يخطئ.
    fun countOne() {
        val result = TasbihCounter.tap(state, target)
        state = result.state
        if (result.completed) {
            buzz(context)
            markTasbihHabit()
        }
        // ⚠️ الجلسة **تُغلق قبل أن تبدأ الجديدة**: فنقرة
        // بعد انقطاعٍ طويل تُنهي الجلسة السابقة وتبدأ
        // أخرى، فلا تُنسَب إلى الأولى.
        val now = System.currentTimeMillis()
        val closed = TasbihSessions.closeIfStale(session, now)
        if (closed != null) {
            sessions = TasbihSessions.add(sessions, closed)
            // ⚠️ السجلّ **لا يُكتب كل نقرة**: لا يتغيّر
            // إلا حين تُغلق جلسة. وكاتبةُ ملفٍ كاملٍ في
            // كل نقرةٍ إهدارٌ للبطارية.
            TasbihSessionStore.save(context, sessions)
        }
        session = TasbihSessions.onTap(session, now, target)
        persist()
        TasbihSessionStore.saveCurrent(context, session)
        // ⚠️ **العدد المنطوق هو الذي بلغه العدّاد**، لا
        // ما يعرضه بعد التصفير: عند إتمام دورة يعود
        // العدّاد إلى صفر، ومن يسمع «صفر» يظنّ أن عدّه
        // ضاع.
        val spoken = TasbihSpeech.spokenCount(result, target)
        if (TasbihSpeech.shouldSpeak(speakEnabled, spoken)) {
            speaker?.speak(TasbihSpeech.text(spoken))
        }
    }

    // ⚠️ **الالتقاطُ مؤقّت**: يتعلّق بالظهور ويُلغى بالمغادرة.
    // فمن مرّ على السبحة ثم انتقل إلى القرآن لا يجوز أن تُلتقط
    // أزرار الصوت هناك — ومن لا يعرف لماذا يتغيّر الصوت؟!
    DisposableEffect(volumeEnabled) {
        if (volumeEnabled) {
            VolumeCounter.attach { countOne() }
        } else {
            VolumeCounter.detach()
        }
        onDispose { VolumeCounter.detach() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AtharPageMargin, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ⚠️ **عنوانُ الفصلّ**: السبحةُ كانت تبدأُ بدائرتها
        // بلا عنوان، فكان نصفُ التطبيقِ بلا بابٍ.
        AtharPageTitle(
            title = s.tabTasbih,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            // ⚠️ النصّ عربي ثابت: التسبيح يُقال بالعربية في كل لغة
            text = phrase.arabic,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AmiriFamily,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                // ⚠️ **التدرّجُ نفسُه** الذي في بطاقة البطل: التطبيقُ
                // منتَجٌ واحد، فلونُه واحدٌ في كل مكان.
                .background(
                    Brush.verticalGradient(listOf(AtharHeroTop, AtharHeroBottom)),
                )
                // ⚠️ `clickable` على الدائرة كلها، و`role` يخبر قارئ
                // الشاشة أنها زرّ لا صورة.
                .clickable(role = Role.Button) { countOne() },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = digits(state.count),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AmiriFamily,
                    color = AtharCream,
                )
                Text(
                    text = "${digits(state.count)} / ${digits(target)}",
                    fontSize = 13.sp,
                    color = AtharPrimaryContainer,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AssistChip(
                onClick = {
                    state = TasbihCounter.undo(state, target)
                    persist()
                },
                label = { Text(s.tasbihUndo) },
                leadingIcon = { Icon(Icons.Filled.Undo, contentDescription = null) },
            )
            AssistChip(
                onClick = {
                    state = TasbihCounter.reset(state)
                    persist()
                },
                label = { Text(s.tasbihReset) },
                leadingIcon = { Icon(Icons.Filled.Refresh, contentDescription = null) },
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ⚠️ الإحصاء **مقروء** لا مخبوء: «أثر» التطبيق كله قائم على أن
        // المستخدم يرى ما تراكم له.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Stat(label = s.tasbihRounds, value = digits(state.rounds))
            Stat(label = s.tasbihTotal, value = digits(state.lifetime))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = s.target,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TasbihPhrases.TARGETS.forEach { n ->
                FilterChip(
                    selected = target == n,
                    onClick = {
                        target = n
                        // ⚠️ تغيير الهدف **يُصفّر الدورة** لا يتركها
                        // نصف ممتلئة على مقياس آخر: «٢٧ / ١٠٠» بعد أن
                        // كانت «٢٧ / ٣٣» كذب في رقم العبادة.
                        state = TasbihCounter.reset(state)
                        persist()
                    },
                    label = { Text(digits(n)) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = s.tasbihTitle,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TasbihPhrases.ALL.forEach { item ->
                FilterChip(
                    selected = phraseId == item.id,
                    onClick = {
                        if (phraseId == item.id) return@FilterChip
                        phraseId = item.id
                        target = TasbihStore.loadTarget(context, item)
                        state = TasbihCounter.reset(state)
                        persist()
                    },
                    label = { Text(item.arabic) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⚠️ النطق **مفتاحٌ لا زرّ**: مطفأٌ افتراضيًا، ويزيده
        // المستخدم إن أراد. والصوتُ يُحرَّر بمغادرة الشاشة.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(s.tasbihSpeak, style = MaterialTheme.typography.labelLarge)
                Text(
                    text = s.tasbihSpeakHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = speakEnabled,
                onCheckedChange = {
                    speakEnabled = it
                    TasbihSpeechStore.setEnabled(context, it)
                },
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ⚠️ **زرُّ الصوت يصير عدّادًا** في هذه الشاشة وحدها (E5).
        // والعوض أن يبتلع التطبيقُ زرَّ المستخدم: فمن أراد تغيير
        // الصوت يفعلها من إعدادات الجهاز — ويبقى التطبيق صامتًا
        // عن السبب.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(s.tasbihVolume, style = MaterialTheme.typography.labelLarge)
                Text(
                    text = s.tasbihVolumeHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = volumeEnabled,
                onCheckedChange = {
                    volumeEnabled = it
                    TasbihVolumeStore.setEnabled(context, it)
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⚠️ السجلّ **أسفل** الاختيار لا فوقه: هو تاريخٌ يُطالع
        // بعد التسبيح، لا لوحةُ تحكّمٍ تُطالع قبله.
        SessionLog(sessions = sessions)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/// سجلّ الجلسات (E6).
///
/// ⚠️ **الوصف لا التفاف**: «١٢٠ ذكرًا اليوم» و«٤ جلسات». ولا
/// «إنت لم تصل» ولا «أداء ممتاز» — فالعدد ليس تقييمًا، وأحدٌ
/// يذكّر بنفسه قد لا يكون في يومٍ أفضل.
@Composable
private fun SessionLog(sessions: List<TasbihSession>) {
    val s = LocalStrings.current
    val digits = localDigits()
    val zone = remember { ZoneId.systemDefault() }
    // ⚠️ التاريخ **يُحسب في كلّ رسمٍ لا مرّة واحدة**: فمن ترك
    // التطبيق مفتوحًا حتى منتصف الليل يجب أن يرى «اليوم» الجديد
    // لا أمسٍ.
    val today = LocalDate.now()
    val day = remember(today, zone) { TasbihRanges.day(today, zone) }
    val week = remember(today, zone) { TasbihRanges.week(today, zone) }
    val todaySummary = remember(sessions, day) { TasbihSessions.summary(sessions, day.first, day.last + 1) }
    val weekSummary = remember(sessions, week) { TasbihSessions.summary(sessions, week.first, week.last + 1) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = s.tasbihSessions,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (sessions.isEmpty()) {
            Text(
                text = s.tasbihNoSessions,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Column
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Stat(
                s.tasbihToday,
                s.tArgs("tasbih_counts", "n" to digits(todaySummary.rangeCount)),
            )
            Stat(
                s.tasbihWeek,
                s.tArgs("tasbih_counts", "n" to digits(weekSummary.rangeCount)),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Stat(
                s.tasbihSessionsToday,
                s.tArgs("tasbih_sessions_count", "n" to digits(todaySummary.rangeSessions)),
            )
            Stat(
                s.tasbihTotal,
                digits(todaySummary.totalCount),
            )
            Stat(
                s.tasbihBest,
                digits(todaySummary.bestSession),
            )
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/// اهتزازة خفيفة عند إتمام دورة (E3).
private fun buzz(context: Context) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java) ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        runCatching {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}

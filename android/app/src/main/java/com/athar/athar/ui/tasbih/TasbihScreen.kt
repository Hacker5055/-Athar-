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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.athar.athar.core.ArabicDigits
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.tasbih.TasbihCounter
import com.athar.athar.tasbih.TasbihPhrases
import com.athar.athar.tasbih.TasbihStore

/// السبحة (E1–E4 من docs/02).
///
/// ⚠️ الدائرة **كلها** هدف النقر لا زرّ صغير في منتصفها: التسبيح يُعدّ
/// بالمئات، والإصابة مهمة حين لا ينظر المستخدم إلى الشاشة أصلًا.
@Composable
fun TasbihScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
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

    fun persist() = TasbihStore.save(context, phraseId, target, state)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            // ⚠️ النصّ عربي ثابت: التسبيح يُقال بالعربية في كل لغة
            text = phrase.arabic,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                // ⚠️ `clickable` على الدائرة كلها، و`role` يخبر قارئ
                // الشاشة أنها زرّ لا صورة.
                .clickable(role = Role.Button) {
                    val result = TasbihCounter.tap(state, target)
                    state = result.state
                    if (result.completed) buzz(context)
                    persist()
                },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = ArabicDigits.of(state.count),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "${ArabicDigits.of(state.count)} / ${ArabicDigits.of(target)}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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
            Stat(label = s.tasbihRounds, value = ArabicDigits.of(state.rounds))
            Stat(label = s.tasbihTotal, value = ArabicDigits.of(state.lifetime))
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
                    label = { Text(ArabicDigits.of(n)) },
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

        Spacer(modifier = Modifier.height(24.dp))
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

package com.athar.athar.tasbih

import android.content.Context
import androidx.core.content.edit

/// مخزن السبحة.
///
/// ⚠️ `runCatching` في القراءة: ملف تالف يجب أن يُقرأ **كحالة فارغة**
/// لا أن يُسقط الشاشة. وفقدان عدد السبحة مؤلم، لكن تطبيق لا يُفتح أسوأ.
object TasbihStore {

    private const val PREFS_NAME = "athar.tasbih.v1"
    private const val KEY_PHRASE = "phrase"
    private const val KEY_TARGET = "target"
    private const val KEY_COUNT = "count"
    private const val KEY_ROUNDS = "rounds"
    private const val KEY_LIFETIME = "lifetime"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /// ⚠️ الهدف يُحفظ **منفصلًا** عن القالب: من اختار «٣٣» ثم «١٠٠»
    /// ثم عاد إلى «سبحان الله» يجب أن يجدها كما تركها، لا أن تُعاد
    /// إلى عدد القالب الافتراضي.
    fun loadTarget(context: Context, phrase: TasbihPhrase): Int {
        val raw = runCatching { prefs(context).getInt(KEY_TARGET, 0) }.getOrDefault(0)
        return if (raw > 0) raw else phrase.defaultTarget
    }

    fun loadPhraseId(context: Context): String =
        runCatching { prefs(context).getString(KEY_PHRASE, null) }
            .getOrDefault(null) ?: TasbihPhrases.SUBHAN.id

    fun loadState(context: Context): TasbihState = runCatching {
        val p = prefs(context)
        TasbihState(
            count = p.getInt(KEY_COUNT, 0).coerceAtLeast(0),
            rounds = p.getInt(KEY_ROUNDS, 0).coerceAtLeast(0),
            lifetime = p.getInt(KEY_LIFETIME, 0).coerceAtLeast(0),
        )
    }.getOrDefault(TasbihState())

    fun save(context: Context, phraseId: String, target: Int, state: TasbihState) {
        prefs(context).edit {
            putString(KEY_PHRASE, phraseId)
            putInt(KEY_TARGET, target)
            putInt(KEY_COUNT, state.count)
            putInt(KEY_ROUNDS, state.rounds)
            putInt(KEY_LIFETIME, state.lifetime)
        }
    }
}

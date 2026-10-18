package com.athar.athar.adhkar

/// ذكر يكتبه المستخدم لنفسه — منطق نقي بلا `Context`.
///
/// ⚖️ **لماذا قسم منفصل لا باب داخل الكتاب؟** أذكار الكتاب من حصن
/// المسلم، وكل واحد منها بتخريجه (docs/02 · C1). وذكر يكتبه المستخدم
/// **لا تخريج له**: وضعه في الباب نفسه يوهم أن له من السند ما لغيره.
/// فالفصل بينهما ليس ترتيبًا بل أمانة.
data class CustomDhikr(
    val id: String,
    val text: String,
    val target: Int,
)

object CustomAdhkar {

    /// ⚠️ فاصل لا يظهر في نصّ عربي مكتوب باليد، لا `|` ولا `,`:
    /// المستخدم قد يكتب أي حرف، والفاصل الذي يقع داخل النصّ يُفسد
    /// الملف كلّه لا سطرًا واحدًا.
    ///
    /// ⚠️ **رمزان مطبوعان لا محرفَي تحكّم** (U+241E وU+241F لا
    /// U+001C وU+001D): SharedPreferences تُخزَّن في XML، ومحارف
    /// التحكّم غير قانونية فيه فتُسقط بصمت — فيضيع الذكر.
    private const val REC = '\u241F'
    private const val FIELD = '\u241E'

    private val ID_OK = Regex("""[a-z0-9]{1,32}""")

    private const val MAX_TEXT = 2000
    private const val MAX_ITEMS = 200

    /// يُنظّف نصّ المستخدم: يُسقط فواصل التحكّم ويحدّ الطول.
    ///
    /// ⚠️ الحذف لا الرفض: المستخدم لا يعرف ما فاصل التحكّم، ورفض
    /// ذكره كلّه لأنه لصق نصًا فيه رمز نادر عقاب على خطأ لا يراه.
    fun sanitize(raw: String): String =
        raw.replace(REC, ' ').replace(FIELD, ' ').trim().take(MAX_TEXT)

    fun decode(raw: String?): List<CustomDhikr> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(REC)
            .mapNotNull { record ->
                val parts = record.split(FIELD)
                val id = parts.getOrNull(0)?.takeIf { ID_OK.matches(it) } ?: return@mapNotNull null
                val text = parts.getOrNull(1)?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val target = parts.getOrNull(2)?.toIntOrNull()?.coerceIn(1, 10_000) ?: 1
                CustomDhikr(id, text, target)
            }
            .take(MAX_ITEMS)
    }

    fun encode(items: List<CustomDhikr>): String =
        items.take(MAX_ITEMS)
            .filter { it.text.isNotBlank() }
            .joinToString(REC.toString()) { item ->
                listOf(item.id, sanitize(item.text), item.target.coerceIn(1, 10_000))
                    .joinToString(FIELD.toString())
            }
}

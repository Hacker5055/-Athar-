package com.athar.athar.core

/// الأرقام العربية الشرقية — التطبيق عربي أولًا.
///
/// ⚠️ وحدة واحدة مشتركة لا نسخة في كل شاشة: الودجت والسبحة كلاهما
/// يعرض أرقامًا، ونسختان تختلفان يومًا في الصفر أو في السالب.
object ArabicDigits {

    private val DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    fun of(value: Int): String = of(value.toLong())

    fun of(value: Long): String {
        if (value == 0L) return "٠"
        val negative = value < 0
        var rest = if (negative) -value else value
        val out = StringBuilder()
        while (rest > 0) {
            out.append(DIGITS[(rest % 10).toInt()])
            rest /= 10
        }
        if (negative) out.append('-')
        return out.reverse().toString()
    }
}

package com.athar.athar.core

/// الأرقام العربية الشرقية — التطبيق عربي أولًا.
///
/// ⚠️ وحدة واحدة مشتركة لا نسخة في كل شاشة: الودجت والسبحة كلاهما
/// يعرض أرقامًا، ونسختان تختلفان يومًا في الصفر أو في السالب.
object ArabicDigits {

    private val DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    fun of(value: Int): String = of(value.toLong())

    /// نصٌّ **جاهز** بأرقامه العربية: يحوّل كلّ رقم لاتيني
    /// في النصّ ويبقي ما عداه كما هو.
    ///
    /// ⚠️ لماذا لا `of(value.toLong())`؟ لأنّ **التصفير** يضيع:
    /// «٠٥» يجب أن يبقى «٠٥»، و`of(5)` تعطي «٥».
    fun ofText(text: String): String {
        val out = StringBuilder(text.length)
        for (ch in text) {
            out.append(if (ch in '0'..'9') DIGITS[ch - '0'] else ch)
        }
        return out.toString()
    }

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

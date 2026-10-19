package com.athar.athar.core

/// تطبيع النص العربي **للمطابقة فقط** — لا للتخزين ولا للعرض.
///
/// ⚠️ نطاق التشكيل أوسع مما هو معتاد: الرسم العثماني يكتب السكون
/// `ۡ` (U+06E1) لا `ْ` (U+0652)، فمن يحذف U+064B..U+0652 وحده يترك
/// `ۡ` في مكانه ولن يجد «الحمد» في «ٱلۡحَمۡدُ» أبدًا. وكذلك الألف
/// الخنجرية `ٰ` (U+0670) في «ٱلۡعٰلَمِينَ».
object ArabicText {

    /// نطاقات الحروف التي **تُحذف** بالكامل.
    private val DROPPED = listOf(
        0x0610..0x061A, // علامات الآيات والحروف القرآنية الصغيرة
        0x064B..0x0652, // التشكيل المعتاد
        0x0670..0x0670, // الألف الخنجرية
        0x06D6..0x06ED, // ۝ ۞ ۡ ورؤوس الآيات — ومنها سكون الرسم العثماني
        0x06F0..0x06F9, // الأرقام العربية الشرقية
    )

    fun normalize(input: String): String {
        val out = StringBuilder(input.length)
        for (ch in input) {
            when {
                DROPPED.any { ch.code in it } -> Unit
                ch == 'ـ' -> Unit // التطويل
                ch == 'أ' || ch == 'إ' || ch == 'آ' || ch == 'ٱ' -> out.append('ا')
                ch == 'ى' || ch == 'ئ' -> out.append('ي')
                ch == 'ة' -> out.append('ه')
                ch == 'ؤ' -> out.append('و')
                else -> out.append(ch.lowercaseChar())
            }
        }
        return out.toString()
    }
}

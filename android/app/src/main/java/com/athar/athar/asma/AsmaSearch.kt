package com.athar.athar.asma

import com.athar.athar.core.ArabicText

/// البحث في الأسماء الحسنى — **محض بالكامل** ويُختبر على JVM.
///
/// ⚠️ التطبيع للمطابقة فقط: الأسماء مشكولة، فمن يكتب «الرحمن» بلا
/// تشكيل يجب أن يجد «الرَّحْمَنُ». وهذا هو الدرس المستفاد من بحث
/// القرآن، حيث كان سكون الرسم العثماني `ۡ` (U+06E1) خارج نطاق الحذف.
object AsmaSearch {

    fun search(query: String, names: List<AsmaName>): List<AsmaName> {
        val q = ArabicText.normalize(query.trim())
        if (q.isEmpty()) return names
        return names.filter { name ->
            q in ArabicText.normalize(name.name) ||
                q in ArabicText.normalize(name.nameArabic) ||
                q in ArabicText.normalize(name.meaning)
        }
    }
}

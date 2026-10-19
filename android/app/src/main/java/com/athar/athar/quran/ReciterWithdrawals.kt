package com.athar.athar.quran

import org.json.JSONArray
import org.json.JSONObject

/// سحب تلاوة — القاعدة ٣ من `docs/10`.
///
/// ⚖️ حق كل تلاوة يبقى لقارئها. فإن اعترض قارئ على نشر تلاوته، أو
/// تبيّن أن لا حقّ لنا فيها، يجب أن تتوقف التلاوة **فورًا** لا في
/// التحديث القادم. هذه ليست ميزة: هي قدرتنا على التراجع عمّا
/// نشرناه، وهي الشرط الذي يبيح لنا النشر أصلًا.
///
/// ⚠️ لماذا ملف بعيد لا قائمة في الكود: قائمة في الكود تُسحب تلاوتها
/// بتحديث للتطبيق، فيبقى كل من لم يحدّث يسمع ما طُلب سحبه. الملف
/// البعيد يُقرأ في كل فتح، فتختفي التلاوة عند الجميع في ساعة.
object ReciterWithdrawals {

    /// الموضع الوحيد للقائمة — على الفرع الرئيسي، في مستودع عام.
    const val SOURCE_URL =
        "https://raw.githubusercontent.com/Hacker5055/-Athar-/main/athar-withdrawn.json"

    /// يقرأ المُعرّفات المسحوبة من نصّ جسون.
    ///
    /// ⚠️ متسامح عموديًا: ملف تالف أو ناقص أو غير متوقّع يُعيد **قائمة
    /// فارغة** ولا يرمي. الفشل هنا يجب أن يبقي القرّاء ظاهرين لا أن
    /// يُسكت التلاوة كلها — فحذف تلاوة حيّة بسبب جسون مكسور ضرر أكبر
    /// من تأخّر سحب تلاوة مطلوبة.
    fun parse(raw: String?): Set<String> {
        if (raw.isNullOrBlank()) return emptySet()
        return runCatching {
            val array = JSONObject(raw).optJSONArray(WITHDRAWN_KEY) ?: JSONArray()
            val ids = LinkedHashSet<String>()
            for (i in 0 until array.length()) {
                val id = array.optString(i, "").trim()
                // ⚠️ نتجاهل ما ليس على شكل مُعرّف: سطر فارغ أو
                // تعليق أو نصّ عابر في ملف مكتوب باليد يمرّ بلا أثر.
                if (id.matches(Regex("ar\\.[a-z0-9]+"))) ids.add(id)
            }
            ids
        }.getOrDefault(emptySet())
    }

    /// يُخرج القرّاء المسحوبين من الكتالوج.
    ///
    /// ⚠️ معرّف غير معروف يُتجاهل بصمت: القائمة البعيدة قد تحتوي
    /// قارئًا حذفناه من الكتالوج أصلًا، ولا معنى للخطأ فيه.
    fun apply(catalog: List<Reciter>, withdrawn: Set<String>): List<Reciter> =
        if (withdrawn.isEmpty()) catalog else catalog.filterNot { it.id in withdrawn }

    /// يختار قارئًا صالحًا بعد السحب.
    ///
    /// ⚠️ قد يكون القارئ المختار أو الافتراضي نفسه هو المسحوب، فتبقى
    /// الواجهة تشير إلى قارئ لم يعد موجودًا — زرّ يسمي اسمًا لا صوت له.
    fun fallback(catalog: List<Reciter>, current: Reciter, withdrawn: Set<String>): Reciter =
        if (current.id in withdrawn) catalog.firstOrNull() ?: Reciters.DEFAULT else current

    const val WITHDRAWN_KEY = "withdrawn"
}

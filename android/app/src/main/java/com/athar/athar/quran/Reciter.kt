package com.athar.athar.quran

/// أسلوب التلاوة — يظهر للمستخدم ليختار ما اعتاد سماعه.
enum class RecitationStyle {
    /// مرتّل: التلاوة المعتادة بتأنٍّ.
    MURATTAL,

    /// مجوّد: تلاوة بالتجويد والتطريب.
    MUJAWWAD,

    /// معلّم: تلاوة تعليمية مقطّعة.
    MUALLIM,
}

/// كيف يبثّ هذا القارئ فعلًا.
///
/// ⚠️ لماذا نميّز: المصدر يوفّر ملفًا واحدًا للسورة لبعض القرّاء
/// وملفًا لكل آية لبعضهم، ولا علاقة لذلك بجودة القارئ ولا شهرته.
/// قياسنا أثبت أن اثنين فقط يبثّان السورة كاملة، فلو بنينا المشغّل
/// على السورة وحدها لأسكتنا ستّة قرّاء يعملون.
enum class ReciterMode {
    /// ملف واحد للسورة كلها.
    SURAH,

    /// ملف لكل آية — يُشغَّل متتابعًا.
    AYAH,
}

/// قارئ واحد في كتالوج التلاوة.
///
/// ⚖️ `id` ليس اسمًا نختاره: هو مُعرّف النسخة (edition) عند
/// islamic.network، وهو **مفتاح الرابط**. تغييره لتحسين النطق أو
/// الترتيب يقطع الرابط بصمت.
///
/// ⚠️ [bitrate] و[mode] **مقيسان لا منقولان**: التوثيق يصف مخطّط
/// الروابط ولا يضمن التوفّر، فمسحنا كل قارئ على كل جودة وعلى
/// المسارين، وأثبتنا هنا ما ردّ صحيحًا فعلًا. من أراد إضافة قارئ
/// فليقِسه بـ `tool/check_audio_links.py` لا ينقله من التوثيق.
data class Reciter(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val style: RecitationStyle,
    val mode: ReciterMode,
    val bitrate: Int,
)

/// ⚖️ كتالوج القرّاء — أسماء ومُعرّفات فقط، لا صوت ولا نص قرآني.
///
/// المصدر: islamic.network (alquran.cloud). رخصة التوزيع: مجاني
/// لإعادة التوزيع غير التجاري، و**حق كل تلاوة يبقى لقارئها** —
/// لذلك نبثّ من الرابط ولا ننسخ الملفات إلى أصول التطبيق، ونُظهر
/// نسبة المصدر في الواجهة.
object Reciters {
    val ALL: List<Reciter> = listOf(
        // ── يبثّ السورة كاملة ─────────────────────────────
        Reciter("ar.alafasy", "مشاري بن راشد العفاسي", "Mishary Alafasy", RecitationStyle.MURATTAL, ReciterMode.SURAH, 128),
        Reciter("ar.abdulbasitmujawwad", "عبد الباسط عبد الصمد (مجوّد)", "Abdul Basit (Mujawwad)", RecitationStyle.MUJAWWAD, ReciterMode.SURAH, 128),

        // ── يبثّ آية آية ─────────────────────────────────
        Reciter("ar.husary", "محمود خليل الحصري", "Mahmoud Al-Husary", RecitationStyle.MURATTAL, ReciterMode.AYAH, 128),
        Reciter("ar.minshawi", "محمد صديق المنشاوي", "Mohamed Al-Minshawi", RecitationStyle.MURATTAL, ReciterMode.AYAH, 128),
        Reciter("ar.hudhaify", "علي بن عبد الرحمن الحذيفي", "Ali Al-Hudhaify", RecitationStyle.MURATTAL, ReciterMode.AYAH, 128),
        Reciter("ar.muhammadjibreel", "محمد جبريل", "Muhammad Jibreel", RecitationStyle.MURATTAL, ReciterMode.AYAH, 128),
        Reciter("ar.minshawimujawwad", "محمد صديق المنشاوي (مجوّد)", "Al-Minshawi (Mujawwad)", RecitationStyle.MUJAWWAD, ReciterMode.AYAH, 64),
        Reciter("ar.parhizgar", "محمود خليل الحصري (معلّم)", "Al-Husary (Muallim)", RecitationStyle.MUALLIM, ReciterMode.AYAH, 48),
    )

    val DEFAULT: Reciter = ALL.first()

    fun byId(id: String): Reciter? = ALL.firstOrNull { it.id == id }

    /// ⚖️ نسبة المصدر — شرط الرخصة لا ميزة.
    const val CREDIT = "التلاوة: islamic.network (alquran.cloud) — الحق لكل قارئ"
}

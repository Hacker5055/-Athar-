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

/// قارئ واحد في كتالوج التلاوة.
///
/// ⚖️ `id` ليس اسمًا نختاره: هو مُعرّف النسخة (edition) عند
/// islamic.network، وهو **مفتاح الرابط**. تغييره لتحسين النطق أو
/// الترتيب يقطع الرابط بصمت.
data class Reciter(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val style: RecitationStyle,
    /// جودة الصوت بالكيلوبت. تختلف باختلاف القارئ؛ ليس كل قارئ
    /// متاحًا بكل جودة، فنحمل القيمة التي يعلنها المصدر.
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
        Reciter("ar.alafasy", "مشاري بن راشد العفاسي", "Mishary Alafasy", RecitationStyle.MURATTAL, 128),
        Reciter("ar.husary", "محمود خليل الحصري", "Mahmoud Al-Husary", RecitationStyle.MURATTAL, 128),
        Reciter("ar.minshawi", "محمد صديق المنشاوي", "Mohamed Al-Minshawi", RecitationStyle.MURATTAL, 128),
        Reciter("ar.sudais", "عبد الرحمن السديس", "Abdul Rahman Al-Sudais", RecitationStyle.MURATTAL, 192),
        Reciter("ar.shuraim", "سعود الشريم", "Saud Al-Shuraim", RecitationStyle.MURATTAL, 128),
        Reciter("ar.abdulbasit", "عبد الباسط عبد الصمد", "Abdul Basit Abdul Samad", RecitationStyle.MURATTAL, 192),
        Reciter("ar.ajamy", "أحمد بن علي العجمي", "Ahmed Al-Ajamy", RecitationStyle.MURATTAL, 128),
        Reciter("ar.hudhaify", "علي بن عبد الرحمن الحذيفي", "Ali Al-Hudhaify", RecitationStyle.MURATTAL, 128),
        Reciter("ar.muhammadayoub", "محمد أيوب", "Muhammad Ayyoub", RecitationStyle.MURATTAL, 128),
        Reciter("ar.muhammadjibreel", "محمد جبريل", "Muhammad Jibreel", RecitationStyle.MURATTAL, 128),
        Reciter("ar.minshawimujawwad", "محمد صديق المنشاوي (مجوّد)", "Al-Minshawi (Mujawwad)", RecitationStyle.MUJAWWAD, 64),
        Reciter("ar.abdulbasitmujawwad", "عبد الباسط عبد الصمد (مجوّد)", "Abdul Basit (Mujawwad)", RecitationStyle.MUJAWWAD, 192),
        Reciter("ar.parhizgar", "محمود خليل الحصري (معلّم)", "Al-Husary (Muallim)", RecitationStyle.MUALLIM, 64),
    )

    val DEFAULT: Reciter = ALL.first()

    fun byId(id: String): Reciter? = ALL.firstOrNull { it.id == id }

    /// ⚖️ نسبة المصدر — شرط الرخصة لا ميزة.
    const val CREDIT = "التلاوة: islamic.network (alquran.cloud) — الحق لكل قارئ"
}

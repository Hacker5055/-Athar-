package com.athar.athar.quran

/// بناء روابط التلاوة على شبكة islamic.network.
///
/// ⚖️ لماذا نبني رابطًا ولا نضمّن ملفًا: رخصة المصدر تجيز إعادة
/// التوزيع غير التجاري وتبقي حق التلاوة لقارئها. البثّ من المصدر
/// يحترم الأمرين معًا، ولا يضخّم حجم التطبيق بمئات الميجابايت.
///
/// المخطّطان من توثيق المصدر نفسه:
/// - آية: `…/quran/audio/{bitrate}/{edition}/{رقم الآية 1..6236}.mp3`
/// - سورة: `…/quran/audio-surah/{bitrate}/{edition}/{رقم السورة 1..114}.mp3`
object QuranAudio {
    private const val CDN = "https://cdn.islamic.network/quran"

    /// رابط تلاوة سورة كاملة. `null` خارج النطاق — لا نبني رابطًا
    /// مكسورًا ثم نفاجأ بخطأ شبكة غامض.
    fun surahUrl(reciter: Reciter, surah: Int): String? {
        if (surah !in 1..114) return null
        return "$CDN/audio-surah/${reciter.bitrate}/${reciter.id}/$surah.mp3"
    }

    /// رابط تلاوة آية مفردة بمعرّفها الكوني (1..6236).
    fun ayahUrl(reciter: Reciter, verseId: Int): String? {
        if (verseId !in 1..QuranMeta.TOTAL_VERSES) return null
        return "$CDN/audio/${reciter.bitrate}/${reciter.id}/$verseId.mp3"
    }

    /// رابط تلاوة آية بسورتها ورقمها.
    ///
    /// ⚠️ نمرّ عبر [QuranMeta.verseId] لأن الترقيم كونيّ لا يبدأ من
    /// واحد في كل سورة؛ وحين تكون الآية خارج سورتها يُعيد صفرًا،
    /// فنحوّله إلى `null`.
    fun ayahUrl(reciter: Reciter, surah: Int, ayah: Int): String? {
        val id = QuranMeta.verseId(surah, ayah)
        return if (id == 0) null else ayahUrl(reciter, id)
    }
}

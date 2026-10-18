# رخصة التلاوة — islamic.network (alquran.cloud)

## المصدر

التلاوة تُبَثّ من شبكة islamic.network عبر واجهتها alquran.cloud:

- آية: `https://cdn.islamic.network/quran/audio/{bitrate}/{edition}/{رقم الآية 1..6236}.mp3`
- سورة: `https://cdn.islamic.network/quran/audio-surah/{bitrate}/{edition}/{رقم السورة 1..114}.mp3`

## الرخصة

حسب بيان المصدر (`risan/quran-json` → `data/meta/sources.json`، حقلي
`islamic_network_by_ayah` و `islamic_network_by_surah`):

> free to redistribute for non-commercial use; each recitation's copyright
> remains with its reciter

أي: **التوزيع غير التجاري مسموح، وحق كل تلاوة يبقى لقارئها.**

## كيف نلتزم بها في أثر

1. **نبثّ ولا ننسخ.** لا ملفّ صوتي واحد في أصول التطبيق ولا في المستودع.
   الروابط تُبنى عند التشغيل (`QuranAudio.kt`) وتُطلب من المصدر مباشرة.
   فلا نُعيد توزيع شيء، ونبقى في حدّ «الاستماع» لا «النسخ».
2. **النسبة معروضة.** سطر `التلاوة: islamic.network (alquran.cloud) —
   الحق لكل قارئ` يظهر في شاشة القرآن (`Reciters.CREDIT`).
3. **لا إعلانات ولا اشتراك.** أثر مجاني للأبد وبلا إعلانات (قرار
   `docs/09`)، فلا يقع البثّ في نطاق تجاري.

## لماذا هذا المصدر وحده

فحصنا مصادر التلاوة في `risan/quran-json` (`data/audio/`) فوجدنا:

| المصدر | الحالة | القرار |
|---|---|---|
| `islamic_network_by_ayah` / `_by_surah` | رخصة معلنة (غير تجاري، الحق للقارئ) | **مستخدم** |
| `everyayah` و `everyayah_unlisted` | لا رخصة معلنة، وصفحة الرخصة 404 | مرفوض |
| `mp3quran` | لا رخصة معلنة لإعادة التوزيع | مرفوض |

القاعدة: **لا رخصة معلنة ⇒ لا استخدام**، ولو كان الملف متاحًا للتنزيل.
التوافر التقني ليس إذنًا.

## ⚠️ درس: التوثيق يصف المخطّط ولا يضمن التوفّر

قائمة القرّاء في `Reciter.kt` **مقيسة لا منقولة**. مسحٌ أجريناه على
CI قاس كل قارئ على كل جودة وعلى المسارين فأعطى:

- يبثّ السورة كاملة: `ar.alafasy` ١٢٨، `ar.abdulbasitmujawwad` ١٢٨
- يبثّ آية آية: `ar.husary` ١٢٨، `ar.minshawi` ١٢٨، `ar.hudhaify` ١٢٨،
  `ar.muhammadjibreel` ١٢٨، `ar.minshawimujawwad` ٦٤، `ar.parhizgar` ٤٨
- لا يبثّ إطلاقًا: `ar.sudais`، `ar.shuraim`، `ar.abdulbasit`،
  `ar.ajamy`، `ar.muhammadayoub` — فحُذفوا

فبُني المشغّل على **الوضعين** (`ReciterMode.SURAH` / `ReciterMode.AYAH`)
لا على السورة وحدها، ولو بنيناه عليها لأسكتنا ستّة قرّاء يعملون.

الأداة التي تقيس هذا: `android/tool/check_audio_links.py`، وتعمل في CI
ضمن مهمة البناء. **كل قارئ يُضاف إلى الكتالوج يجب أن يُقاس بها أولًا.**

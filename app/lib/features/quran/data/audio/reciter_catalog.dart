import '../../domain/audio/audio_license.dart';
import '../../domain/audio/reciter.dart';

/// كتالوج القرّاء — **٥ فقط، وكل واحد متحقق منه**.
///
/// ### لماذا ٥ لا ١٥؟
/// `docs/02` B6 يطلب ١٠–١٥ قارئًا. لكن شرطنا أسبق: **نسبة صحيحة أو لا
/// نسبة**. تحقّقنا من الأسماء مقابل ميتاداتا المصدر (`edition.name` /
/// `edition.englishName`) فلم نجد إلا ٥ أسماء مؤكّدة. البقية معروفة
/// معرّفاتها فقط، وكتابة أسمائها من الذاكرة مخاطرة لا نتحمّلها في قرآن.
///
/// ### كيف نضيف الباقي (١٣ قارئًا)
/// لكل معرّف: افتح `https://api.alquran.cloud/v1/surah/1/<identifier>`
/// وانسخ `data.edition.name` و `data.edition.englishName` حرفيًا، ثم
/// أضف سطرًا هنا. لا تكتب الاسم من معرفة سابقة.
///
/// ### لماذا هذا المصدر تحديدًا؟
/// Islamic Network CDN هو المصدر الوحيد بين ما فحصنا الذي يصرّح
/// صراحةً (حالة `granted` في فهرس الرخص):
/// «مجاني لإعادة النشر **للاستخدام غير التجاري**، و**حقوق كل تلاوة
/// تفضل لقارئها**».
/// أما everyayah.com و mp3quran.net فحالتهما `unknown` — لا رخصة
/// معلنة، فلا ننشر منهما.
///
/// ### ⚠️ المستبعدون عمدًا
/// • `ar.husary` و `ar.minshawi` (وصيغ `-2`): ٦٢٣٥ ملفًا — **ينقصهم
///   آية**. غير المكتمل يصمت التطبيق عند آية بعينها.
/// • `zh.chinese` و `ru.kuliev-audio`: أصوات **ترجمة** لا تلاوة
///   للنص العربي — وضعها في كتالوج «القرّاء» خطأ في المعنى.
/// • `fr.leclerc`: تلاوة صحيحة، لكن اسمه لم يتأكد من ميتاداتا المصدر
///   بعد، فمؤجّل لا مرفوض.
const List<Reciter> kReciterCatalog = <Reciter>[
  Reciter(
    id: 'ar.alafasy',
    nameAr: 'مشاري العفاسي',
    nameEn: 'Alafasy',
    sourceName: 'Islamic Network CDN (alquran.cloud)',
    license: 'غير تجاري — الحقوق للقارئ',
    licenseUrl: 'https://alquran.cloud/terms-and-conditions',
    status: AudioLicenseStatus.conditional,
    ayahCount: 6236,
    bitrates: <int>[32, 40, 48, 64, 128, 192, 320],
  ),
  Reciter(
    id: 'ar.hudhaify',
    nameAr: 'علي بن عبدالرحمن الحذيفي',
    nameEn: 'Hudhaify',
    sourceName: 'Islamic Network CDN (alquran.cloud)',
    license: 'غير تجاري — الحقوق للقارئ',
    licenseUrl: 'https://alquran.cloud/terms-and-conditions',
    status: AudioLicenseStatus.conditional,
    ayahCount: 6236,
    bitrates: <int>[32, 40, 48, 64, 128, 192, 320],
  ),
  Reciter(
    id: 'ar.mahermuaiqly',
    nameAr: 'ماهر المعيقلي',
    nameEn: 'Maher Al Muaiqly',
    sourceName: 'Islamic Network CDN (alquran.cloud)',
    license: 'غير تجاري — الحقوق للقارئ',
    licenseUrl: 'https://alquran.cloud/terms-and-conditions',
    status: AudioLicenseStatus.conditional,
    ayahCount: 6236,
    bitrates: <int>[32, 40, 48, 64, 128, 192, 320],
  ),
  Reciter(
    id: 'ar.muhammadjibreel',
    nameAr: 'محمد جبريل',
    nameEn: 'Muhammad Jibreel',
    sourceName: 'Islamic Network CDN (alquran.cloud)',
    license: 'غير تجاري — الحقوق للقارئ',
    licenseUrl: 'https://alquran.cloud/terms-and-conditions',
    status: AudioLicenseStatus.conditional,
    ayahCount: 6236,
    bitrates: <int>[32, 40, 48, 64, 128, 192, 320],
  ),
  Reciter(
    id: 'ar.shaatree',
    nameAr: 'أبو بكر الشاطري',
    nameEn: 'Abu Bakr Ash-Shaatree',
    sourceName: 'Islamic Network CDN (alquran.cloud)',
    license: 'غير تجاري — الحقوق للقارئ',
    licenseUrl: 'https://alquran.cloud/terms-and-conditions',
    status: AudioLicenseStatus.conditional,
    ayahCount: 6236,
    bitrates: <int>[32, 40, 48, 64, 128, 192, 320],
  ),
];

/// القرّاء الجاهزون للعرض بعد تطبيق سياسة الرخص والانسحاب.
List<Reciter> get offerableReciters => AudioLicensePolicy.offerable(
      kReciterCatalog.map(AudioLicensePolicy.applyTakedown),
    );

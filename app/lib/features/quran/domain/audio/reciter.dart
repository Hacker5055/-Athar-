/// حالة رخصة التلاوة.
///
/// ⚖️ **هذا التعداد هو الحارس.** لا يُعرض أي قارئ في التطبيق إلا لو
/// كانت حالته [granted] أو [conditional]. القيم الأخرى تعني: لا نعرض.
enum AudioLicenseStatus {
  /// المصدر يصرّح صراحةً بإعادة النشر.
  granted,

  /// يصرّح **بشروط** — أشيعها: للاستخدام غير التجاري فقط، مع حفظ
  /// الحقوق للقارئ. أثر تطبيق غير تجاري، فالشرط متحقّق.
  conditional,

  /// المصدر لا يذكر رخصة (مثل everyayah.com: صفحات الرخصة تعيد ٤٠٤).
  /// ⛔ عدم ذكر الرخصة ليس إذنًا.
  unknown,

  /// المصدر يمنع صراحةً.
  prohibited,

  /// ⚠️ **انسحاب** — طلب القارئ أو ورثته إزالة تلاوته.
  ///
  /// هذه الحالة موجودة لأننا **لا نفترض** رضا أي قارئ. إن حدث واعترض
  /// أحد، تُنقل تلاوته إلى هنا فتختفي من التطبيق، ويُمحى ما نُزّل منها.
  withdrawn,
}

extension AudioLicenseStatusX on AudioLicenseStatus {
  bool get isOfferable =>
      this == AudioLicenseStatus.granted ||
      this == AudioLicenseStatus.conditional;
}

/// قارئ واحد.
///
/// كل حقل هنا إما **متحقق منه من المصدر** أو فارغ — لا أسماء مكتوبة
/// من الذاكرة. نسبة تلاوة لاسم خاطئ أسوأ من عدم عرضها أصلًا.
class Reciter {
  const Reciter({
    required this.id,
    required this.nameAr,
    required this.nameEn,
    required this.sourceName,
    required this.license,
    required this.licenseUrl,
    required this.status,
    this.ayahCount = 6236,
    this.bitrates = const <int>[128],
    this.notes,
  });

  /// معرّف المصدر (`ar.alafasy`) — جزء من مسار الملف على الـ CDN.
  final String id;

  final String nameAr;
  final String nameEn;

  /// اسم المصدر كما يظهر في «المصادر والتراخيص».
  final String sourceName;

  /// سطر الرخصة المختصر.
  final String license;

  /// رابط نص الرخصة الكامل.
  final String licenseUrl;

  final AudioLicenseStatus status;

  /// عدد ملفات الآية المتوفرة — ٦٢٣٦ يعني مكتمل.
  ///
  /// ⚠️ بعض القرّاء ناقصون آية واحدة (٦٢٣٥). غير المكتمل لا يُعرض
  /// للتشغيل آية-بآية، وإلا صمت التطبيق عند آية بعينها.
  final int ayahCount;

  /// معدلات البِتّ المتاحة عند المصدر.
  final List<int> bitrates;

  /// ملاحظة تظهر في شاشة المصادر.
  final String? notes;

  bool get isComplete => ayahCount >= 6236;

  /// نص النسبة — **شرط الرخصة** لا ميزة اختيارية.
  ///
  /// رخصة المصدر تقول حرفيًا: «حقوق كل تلاوة تفضل لقارئها». فالنسبة
  /// ليست مجاملة، بل تنفيذ للشرط.
  String attributionFor(String languageCode) => switch (languageCode) {
        'en' => 'Recitation by $nameEn — rights reserved to the reciter',
        'fr' => 'Récitation de $nameEn — droits réservés au récitateur',
        _ => 'تلاوة $nameAr — الحقوق محفوظة للقارئ',
      };

  @override
  String toString() => 'Reciter($id, $nameAr, ${status.name})';

  @override
  bool operator ==(Object other) => other is Reciter && other.id == id;

  @override
  int get hashCode => id.hashCode;
}

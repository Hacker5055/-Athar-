import '../../../core/l10n/app_locale.dart';

/// كتالوج طرق الحساب — كل طريقة معرّفة **بزواياها صريحةً**.
///
/// ليه مش بنستخدم الـ presets الجاهزة في `adhan_dart` مباشرةً؟
/// 1. عشان نضمن إن القيم هنا مطابقة للي جرّبناها في البروتوتايب
///    (`prototype/assets/js/calc.js`) — فالاختبارات تبقى مفهومة.
/// 2. عشان أي preset جاهز قد يحمل «تعديلات خاصة بالطريقة» (method adjustments)
///    مش عايزينها تتفاجئنا.
///
/// القاعدة: `Other()` في Adhan = زوايا صفر → نبني عليها كل حاجة.
class PrayerMethodSpec {
  const PrayerMethodSpec({
    required this.id,
    required this.fajrAngle,
    required this.ishaAngle,
    required this.nameAr,
    required this.nameEn,
    required this.nameFr,
    this.ishaInterval = 0,
    this.maghribAngle,
    this.noteAr = '',
    this.noteEn = '',
    this.noteFr = '',
  });

  final String id;

  /// زاوية الشمس تحت الأفق وقت الفجر (درجات).
  final double fajrAngle;

  /// زاوية الشمس تحت الأفق وقت العشاء (درجات) — تُتجاهل لو [ishaInterval] > 0.
  final double ishaAngle;

  /// دقائق بعد المغرب (تُستخدم في أم القرى والخليج وقطر = ٩٠).
  final int ishaInterval;

  /// زاوية المغرب (الجعفري/طهران = ٤.٥°) — تُترك `null` لباقي الطرق.
  final double? maghribAngle;

  final String nameAr;
  final String nameEn;
  final String nameFr;

  /// سطر توضيحي قصير يظهر للمستخدم تحت اسم الطريقة.
  final String noteAr;
  final String noteEn;
  final String noteFr;

  bool get usesInterval => ishaInterval > 0;

  String nameFor(AppLocale locale) => switch (locale) {
        AppLocale.ar => nameAr,
        AppLocale.en => nameEn,
        AppLocale.fr => nameFr,
      };

  String noteFor(AppLocale locale) => switch (locale) {
        AppLocale.ar => noteAr,
        AppLocale.en => noteEn,
        AppLocale.fr => noteFr,
      };
}

/// الطرق المتاحة — القيم مأخوذة من نفس الجدول المستخدم في البروتوتايب
/// (`prototype/assets/js/calc.js` → `METHODS`) ومطابقة لمراجع Adhan/PrayTimes.
abstract final class PrayerMethods {
  static const egyptian = PrayerMethodSpec(
    id: 'egyptian',
    fajrAngle: 19.5,
    ishaAngle: 17.5,
    nameAr: 'الهيئة المصرية العامة للمساحة',
    nameEn: 'Egyptian General Authority of Survey',
    nameFr: 'Autorité égyptienne des levés',
    noteAr: 'المعتمد رسميًا في مصر · فجر ١٩.٥° · عشاء ١٧.٥°',
    noteEn: 'Official in Egypt · Fajr 19.5° · Isha 17.5°',
    noteFr: 'Officielle en Égypte · Fajr 19.5° · Isha 17.5°',
  );

  static const ummAlQura = PrayerMethodSpec(
    id: 'umm_alqura',
    fajrAngle: 18.5,
    ishaAngle: 0,
    ishaInterval: 90,
    nameAr: 'أم القرى (مكة المكرمة)',
    nameEn: 'Umm Al-Qura (Makkah)',
    nameFr: 'Umm Al-Qura (La Mecque)',
    noteAr: 'المعتمد في السعودية · فجر ١٨.٥° · عشاء = المغرب + ٩٠ دقيقة',
    noteEn: 'Used in Saudi Arabia · Fajr 18.5° · Isha = Maghrib + 90 min',
    noteFr: 'Utilisée en Arabie saoudite · Fajr 18.5° · Icha = Maghreb + 90 min',
  );

  static const muslimWorldLeague = PrayerMethodSpec(
    id: 'mwl',
    fajrAngle: 18,
    ishaAngle: 17,
    nameAr: 'رابطة العالم الإسلامي',
    nameEn: 'Muslim World League',
    nameFr: 'Ligue islamique mondiale',
    noteAr: 'الأكثر انتشارًا عالميًا · فجر ١٨° · عشاء ١٧°',
    noteEn: 'Most widely used · Fajr 18° · Isha 17°',
    noteFr: 'La plus répandue · Fajr 18° · Icha 17°',
  );

  static const isna = PrayerMethodSpec(
    id: 'isna',
    fajrAngle: 15,
    ishaAngle: 15,
    nameAr: 'ISNA (أمريكا الشمالية)',
    nameEn: 'ISNA (North America)',
    nameFr: 'ISNA (Amérique du Nord)',
    noteAr: 'المنتشر في أمريكا الشمالية · فجر ١٥° · عشاء ١٥°',
    noteEn: 'Common in North America · Fajr 15° · Isha 15°',
    noteFr: 'Courante en Amérique du Nord · Fajr 15° · Icha 15°',
  );

  static const karachi = PrayerMethodSpec(
    id: 'karachi',
    fajrAngle: 18,
    ishaAngle: 18,
    nameAr: 'جامعة العلوم الإسلامية (كراتشي)',
    nameEn: 'University of Islamic Sciences, Karachi',
    nameFr: 'Université des sciences islamiques, Karachi',
    noteAr: 'المنتشر في باكستان والهند · فجر ١٨° · عشاء ١٨°',
    noteEn: 'Common in Pakistan & India · Fajr 18° · Isha 18°',
    noteFr: 'Courante au Pakistan et en Inde · Fajr 18° · Icha 18°',
  );

  static const gulf = PrayerMethodSpec(
    id: 'gulf',
    fajrAngle: 19.5,
    ishaAngle: 0,
    ishaInterval: 90,
    nameAr: 'دول الخليج العربي',
    nameEn: 'Gulf Region',
    nameFr: 'Région du Golfe',
    noteAr: 'فجر ١٩.٥° · عشاء = المغرب + ٩٠ دقيقة',
    noteEn: 'Fajr 19.5° · Isha = Maghrib + 90 min',
    noteFr: 'Fajr 19.5° · Icha = Maghreb + 90 min',
  );

  static const turkey = PrayerMethodSpec(
    id: 'turkey',
    fajrAngle: 18,
    ishaAngle: 17,
    nameAr: 'تركيا (رئاسة الشؤون الدينية)',
    nameEn: 'Turkey (Diyanet)',
    nameFr: 'Turquie (Diyanet)',
    noteAr: 'المعتمد في تركيا · فجر ١٨° · عشاء ١٧°',
    noteEn: 'Official in Türkiye · Fajr 18° · Isha 17°',
    noteFr: 'Officielle en Turquie · Fajr 18° · Icha 17°',
  );

  static const france12 = PrayerMethodSpec(
    id: 'france12',
    fajrAngle: 12,
    ishaAngle: 12,
    nameAr: 'فرنسا ١٢° (UOIF)',
    nameEn: 'France 12° (UOIF)',
    nameFr: 'France 12° (UOIF)',
    noteAr: 'اتحاد المنظمات الإسلامية في فرنسا · فجر ١٢° · عشاء ١٢°',
    noteEn: 'Union of Islamic Organisations of France · Fajr 12° · Isha 12°',
    noteFr: 'Union des organisations islamiques de France · Fajr 12° · Icha 12°',
  );

  static const france15 = PrayerMethodSpec(
    id: 'france15',
    fajrAngle: 15,
    ishaAngle: 15,
    nameAr: 'فرنسا ١٥° (UOIF)',
    nameEn: 'France 15° (UOIF)',
    nameFr: 'France 15° (UOIF)',
    noteAr: 'الأكثر استخدامًا في فرنسا · فجر ١٥° · عشاء ١٥°',
    noteEn: 'Most used in France · Fajr 15° · Isha 15°',
    noteFr: 'La plus utilisée en France · Fajr 15° · Icha 15°',
  );

  static const france18 = PrayerMethodSpec(
    id: 'france18',
    fajrAngle: 18,
    ishaAngle: 18,
    nameAr: 'فرنسا ١٨°',
    nameEn: 'France 18°',
    nameFr: 'France 18°',
    noteAr: 'الأقرب لرابطة العالم الإسلامي · فجر ١٨° · عشاء ١٨°',
    noteEn: 'Closest to Muslim World League · Fajr 18° · Isha 18°',
    noteFr: 'La plus proche de la Ligue islamique mondiale · Fajr 18° · Icha 18°',
  );

  static const singapore = PrayerMethodSpec(
    id: 'singapore',
    fajrAngle: 20,
    ishaAngle: 18,
    nameAr: 'سنغافورة (MUIS)',
    nameEn: 'Singapore (MUIS)',
    nameFr: 'Singapour (MUIS)',
    noteAr: 'المعتمد في سنغافورة · فجر ٢٠° · عشاء ١٨°',
    noteEn: 'Official in Singapore · Fajr 20° · Isha 18°',
    noteFr: 'Officielle à Singapour · Fajr 20° · Icha 18°',
  );

  static const tehran = PrayerMethodSpec(
    id: 'tehran',
    fajrAngle: 17.7,
    ishaAngle: 14,
    maghribAngle: 4.5,
    nameAr: 'طهران / الجعفري',
    nameEn: 'Tehran / Ja’fari',
    nameFr: 'Téhéran / Ja’farite',
    noteAr: 'المذهب الجعفري · فجر ١٧.٧° · مغرب ٤.٥° · عشاء ١٤°',
    noteEn: 'Ja’fari school · Fajr 17.7° · Maghrib 4.5° · Isha 14°',
    noteFr: 'École ja’farite · Fajr 17.7° · Maghreb 4.5° · Icha 14°',
  );

  static const moonsighting = PrayerMethodSpec(
    id: 'moonsighting',
    fajrAngle: 18,
    ishaAngle: 18,
    nameAr: 'لجنة تحري الأهلة العالمية',
    nameEn: 'Moonsighting Committee',
    nameFr: 'Comité d’observation lunaire',
    noteAr: 'فجر ١٨° · عشاء ١٨° مع مراعاة الشفق الموسمي',
    noteEn: 'Fajr 18° · Isha 18° with seasonal shafaq',
    noteFr: 'Fajr 18° · Icha 18° avec chafaq saisonnier',
  );

  static const qatar = PrayerMethodSpec(
    id: 'qatar',
    fajrAngle: 18,
    ishaAngle: 0,
    ishaInterval: 90,
    nameAr: 'قطر (وزارة الأوقاف)',
    nameEn: 'Qatar (Ministry of Awqaf)',
    nameFr: 'Qatar (Ministère des Awqaf)',
    noteAr: 'فجر ١٨° · عشاء = المغرب + ٩٠ دقيقة',
    noteEn: 'Fajr 18° · Isha = Maghrib + 90 min',
    noteFr: 'Fajr 18° · Icha = Maghreb + 90 min',
  );

  static const kuwait = PrayerMethodSpec(
    id: 'kuwait',
    fajrAngle: 18,
    ishaAngle: 17.5,
    nameAr: 'الكويت',
    nameEn: 'Kuwait',
    nameFr: 'Koweït',
    noteAr: 'فجر ١٨° · عشاء ١٧.٥°',
    noteEn: 'Fajr 18° · Isha 17.5°',
    noteFr: 'Fajr 18° · Icha 17.5°',
  );

  static const dubai = PrayerMethodSpec(
    id: 'dubai',
    fajrAngle: 18.2,
    ishaAngle: 18.2,
    nameAr: 'دبي',
    nameEn: 'Dubai',
    nameFr: 'Dubaï',
    noteAr: 'المعتمد في إمارة دبي · فجر ١٨.٢° · عشاء ١٨.٢°',
    noteEn: 'Used in Dubai · Fajr 18.2° · Isha 18.2°',
    noteFr: 'Utilisée à Dubaï · Fajr 18.2° · Icha 18.2°',
  );

  /// كل الطرق بالترتيب المعروض في الواجهة.
  static const List<PrayerMethodSpec> all = [
    egyptian,
    ummAlQura,
    muslimWorldLeague,
    isna,
    karachi,
    gulf,
    turkey,
    france15,
    france12,
    france18,
    singapore,
    tehran,
    moonsighting,
    qatar,
    kuwait,
    dubai,
  ];

  static PrayerMethodSpec byId(String? id) => all.firstWhere(
        (m) => m.id == id,
        orElse: () => egyptian,
      );
}

/// المذهب — يؤثر على وقت العصر فقط (طول الظل).
enum MadhabId {
  shafi, // شافعي/مالكي/حنبلي: الظل = مثل الشيء
  hanafi; // حنفي: الظل = مثلا الشيء

  String nameFor(AppLocale locale) => switch (this) {
        MadhabId.shafi => switch (locale) {
            AppLocale.ar => 'شافعي / مالكي / حنبلي',
            AppLocale.en => 'Shafi’i / Maliki / Hanbali',
            AppLocale.fr => 'Chafi’ite / Malikite / Hanbalite',
          },
        MadhabId.hanafi => switch (locale) {
            AppLocale.ar => 'حنفي',
            AppLocale.en => 'Hanafi',
            AppLocale.fr => 'Hanafite',
          },
      };
}

/// قاعدة خطوط العرض العالية (A5) — مهمة لأوروبا وأمريكا الشمالية.
enum HighLatitudeRuleId {
  none,
  middleOfTheNight,
  seventhOfTheNight,
  twilightAngle;

  String nameFor(AppLocale locale) => switch (this) {
        HighLatitudeRuleId.none => switch (locale) {
            AppLocale.ar => 'بدون (الحساب المباشر)',
            AppLocale.en => 'None (direct calculation)',
            AppLocale.fr => 'Aucune (calcul direct)',
          },
        HighLatitudeRuleId.middleOfTheNight => switch (locale) {
            AppLocale.ar => 'منتصف الليل',
            AppLocale.en => 'Middle of the night',
            AppLocale.fr => 'Milieu de la nuit',
          },
        HighLatitudeRuleId.seventhOfTheNight => switch (locale) {
            AppLocale.ar => 'سُبع الليل',
            AppLocale.en => 'Seventh of the night',
            AppLocale.fr => 'Septième de la nuit',
          },
        HighLatitudeRuleId.twilightAngle => switch (locale) {
            AppLocale.ar => 'زاوية الشفق',
            AppLocale.en => 'Twilight angle',
            AppLocale.fr => 'Angle du crépuscule',
          },
      };
}

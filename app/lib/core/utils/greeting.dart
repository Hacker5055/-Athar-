import '../l10n/strings.dart';

/// تحية حسب وقت اليوم — نفس منطق البروتوتايب.
///
/// الفواصل: صباح < 12 · بعد الظهر < 17 · مساء < 21 · ليل بعد كده.
String greetingFor(DateTime time, Strings s) {
  final hour = time.hour;
  if (hour < 12) return s.hiMorning;
  if (hour < 17) return s.hiAfternoon;
  if (hour < 21) return s.hiEvening;
  return s.hiNight;
}

/// مفاتيح أوقات الصلاة — مرجع مشترك بين الجدول والعدّاد والإشعارات.
enum PrayerNameKey { fajr, sunrise, dhuhr, asr, maghrib, isha }

/// أوقات الصلاة بالترتيب — بدون الشروق (مش صلاة).
const List<PrayerNameKey> prayerOrder = [
  PrayerNameKey.fajr,
  PrayerNameKey.dhuhr,
  PrayerNameKey.asr,
  PrayerNameKey.maghrib,
  PrayerNameKey.isha,
];

/// كل الأوقات بالترتيب (للجدول اليومي الكامل).
const List<PrayerNameKey> prayerOrderWithSunrise = [
  PrayerNameKey.fajr,
  PrayerNameKey.sunrise,
  PrayerNameKey.dhuhr,
  PrayerNameKey.asr,
  PrayerNameKey.maghrib,
  PrayerNameKey.isha,
];

extension PrayerNameKeyX on PrayerNameKey {
  /// معرّف ثابت (يُستخدم كمفتاح في قاعدة البيانات والإشعارات).
  String get id => name;

  String label(Strings s) {
    switch (this) {
      case PrayerNameKey.fajr:
        return s.fajr;
      case PrayerNameKey.sunrise:
        return s.sunrise;
      case PrayerNameKey.dhuhr:
        return s.dhuhr;
      case PrayerNameKey.asr:
        return s.asr;
      case PrayerNameKey.maghrib:
        return s.maghrib;
      case PrayerNameKey.isha:
        return s.isha;
    }
  }

  /// الشروق مش صلاة — لا يتبع ولا يُؤذّن له.
  bool get isPrayer => this != PrayerNameKey.sunrise;
}

/// يرجع المفتاح من معرّف نصي (من قاعدة البيانات أو إشعار).
PrayerNameKey prayerKeyFromId(String id) {
  switch (id) {
    case 'fajr':
      return PrayerNameKey.fajr;
    case 'sunrise':
      return PrayerNameKey.sunrise;
    case 'dhuhr':
      return PrayerNameKey.dhuhr;
    case 'asr':
      return PrayerNameKey.asr;
    case 'maghrib':
      return PrayerNameKey.maghrib;
    case 'isha':
      return PrayerNameKey.isha;
    default:
      return PrayerNameKey.fajr;
  }
}

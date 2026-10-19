import '../../../core/utils/greeting.dart' show PrayerNameKey;

/// أنواع التنبيهات — كل نوع له قناة إشعارات مستقلة على أندرويد.
enum AlertKind {
  /// أذان الصلاة في وقتها.
  athan,

  /// تذكير قبل الصلاة بدقائق.
  reminder,

  /// تذكير الجمعة (أسبوعي متكرر).
  jumuah;

  /// فهرس ثابت يُستخدم في بناء معرّف الإشعار.
  int get indexValue => switch (this) {
        AlertKind.athan => 0,
        AlertKind.reminder => 1,
        AlertKind.jumuah => 2,
      };
}

/// معرّف إشعار **حتمي** (deterministic).
///
/// ليه مش أرقام عشوائية؟ لأننا محتاجين نلغي/نحدّث إشعارًا بعينه من غير
/// ما نحتفظ بخريطة في التخزين — نفس المدخلات = نفس المعرّف دائمًا.
///
/// الصيغة: `kind | dayEpoch | prayerIndex`
/// - dayEpoch = عدد الأيام من ٢٠٠٠-٠١-٠١ (يكفي حتى سنة ٢٢٧٣)
/// - الأقصى ≈ ٣٬٠٠٠٬٠٠٠ — بعيد جدًا عن حدود `int` في أندرويد (٢٫١ مليار).
abstract final class AlertId {
  /// أساس العدّ: ٢٠٠٠-٠١-٠١ بالتوقيت العالمي.
  static final DateTime epoch = DateTime.utc(2000);

  static int dayEpoch(DateTime date) =>
      DateTime.utc(date.year, date.month, date.day).difference(epoch).inDays;

  static int encode({
    required AlertKind kind,
    required DateTime date,
    PrayerNameKey? prayer,
  }) {
    final day = dayEpoch(date);
    final p = prayer == null ? 0 : PrayerNameKey.values.indexOf(prayer) + 1;
    return kind.indexValue * 1000000 + day * 10 + p;
  }

  /// فك الترميز — مفيد في الاختبارات ولتشخيص الأخطاء.
  ///
  /// `prayer == null` يعني «بلا صلاة مرتبطة» (تذكير الجمعة) — الخانة = ٠.
  static ({
    AlertKind kind,
    DateTime date,
    PrayerNameKey? prayer,
    int dayEpoch,
    int prayerSlot,
  }) decode(int id) {
    final kindIndex = id ~/ 1000000;
    final rest = id % 1000000;
    final day = rest ~/ 10;
    final slot = rest % 10;
    return (
      kind: AlertKind.values[kindIndex.clamp(0, AlertKind.values.length - 1)],
      date: epoch.add(Duration(days: day)),
      prayer: (slot >= 1 && slot <= PrayerNameKey.values.length)
          ? PrayerNameKey.values[slot - 1]
          : null,
      dayEpoch: day,
      prayerSlot: slot,
    );
  }
}

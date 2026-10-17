import 'habit.dart';

/// مستوى كثافة اليوم — يُستخدم لتلوين شبكة الثلاثين يوم.
///
/// ⚠️ **اللون مش الإشارة الوحيدة أبدًا**: الشبكة دائمًا مصحوبة بعدّاد
/// نصّي ورقيقة توضيح، احترامًا لإمكانية الوصول (`docs/04` §أ).
enum DayLevel {
  /// لا شيء.
  none,

  /// ١–٢.
  low,

  /// ٣–٤.
  mid,

  /// ٥–٦.
  high,

  /// ٧–٨ (كل شيء).
  full;

  /// ٠ → ٤ — مناسب للاستخدام في فهرس مصفوفة الألوان.
  int get intensity => index;
}

/// يوم واحد في المتتبع.
///
/// ⚠️ **«اليوم» هنا يوم مدني محلي** (من منتصف الليل لمنتصف الليل بتوقيت
/// الجهاز)، **مش** اليوم العبادي اللي يبدأ من المغرب. هذا تبسيط مقصود
/// لـ v1: اليوم العبادي يفتح باب أسئلة صعبة (صيام، قيام، صلاة الفجر
/// قبل المغرب ولا بعده؟)، وهو مُسجّل كمرشّح بعد الإطلاق.
class DailyRecord {
  const DailyRecord({
    required this.day,
    this.done = const <HabitId>{},
  });

  /// اليوم المدني (y-m-d والباقي أصفار) **بالتوقيت المحلي**.
  final DateTime day;

  /// العبادات المكتملة فيه.
  final Set<HabitId> done;

  int get count => done.length;

  bool get isEmpty => done.isEmpty;

  bool has(HabitId habit) => done.contains(habit);

  DayLevel get level => levelFor(count);

  /// مفتاح التخزين: `٢٠٢٦-١٠-١٨`.
  String get key => dayKey(day);

  DailyRecord toggle(HabitId habit) {
    final next = Set<HabitId>.from(done);
    if (!next.add(habit)) next.remove(habit);
    return DailyRecord(day: day, done: next);
  }

  DailyRecord copyWithDone(Set<HabitId> done) =>
      DailyRecord(day: day, done: done);

  static DayLevel levelFor(int count) => switch (count) {
        0 => DayLevel.none,
        1 || 2 => DayLevel.low,
        3 || 4 => DayLevel.mid,
        5 || 6 => DayLevel.high,
        _ => DayLevel.full,
      };

  Map<String, dynamic> toJson() => {
        'd': dayKey(day),
        'h': done.map((h) => h.id).toList()..sort(),
      };

  @override
  String toString() => 'DailyRecord($key, ${done.length})';
}

/// يوم مدني محلي من أي لحظة (منتصف الليل هو الحدّ).
DateTime civilDay(DateTime t) => DateTime(t.year, t.month, t.day);

/// مفتاح اليوم بصيغة `YYYY-MM-DD` — ثابت الطول، يُرتّب معجميًا زمنيًا.
String dayKey(DateTime t) =>
    '${t.year.toString().padLeft(4, '0')}-'
    '${t.month.toString().padLeft(2, '0')}-'
    '${t.day.toString().padLeft(2, '0')}';

/// اليوم السابق — ببناء `DateTime(y, m, d-1)` مش `subtract(Duration)`.
///
/// السبب: `subtract(Duration(days:1))` يحسب ٢٤ ساعة بالضبط، وده بيفضّل
/// عن يوم مدني في أيام التوقيت الصيفي (٢٣ أو ٢٥ ساعة). البناء بالتاريخ
/// المدني يتعامل مع dart كـ normalization صح.
DateTime previousDay(DateTime t) => DateTime(t.year, t.month, t.day - 1);

/// ترميز `YYYY-MM-DD` → يوم مدني. `null` لو الصيغة غلط.
DateTime? dayFromKey(String key) {
  final parts = key.split('-');
  if (parts.length != 3) return null;
  final y = int.tryParse(parts[0]);
  final m = int.tryParse(parts[1]);
  final d = int.tryParse(parts[2]);
  if (y == null || m == null || d == null) return null;
  if (m < 1 || m > 12 || d < 1 || d > 31) return null;
  return DateTime(y, m, d);
}

/// يبني سجلًا من JSON — يتجاهل المعرّفات غير المعروفة بدل ما يفشل.
DailyRecord? recordFromKey(String key, Object? raw) {
  final day = dayFromKey(key);
  if (day == null) return null;
  if (raw is! List) return DailyRecord(day: day);
  final done = <HabitId>{};
  for (final entry in raw) {
    final habit = habitFromId(entry is String ? entry : null);
    if (habit != null) done.add(habit);
  }
  return DailyRecord(day: day, done: done);
}

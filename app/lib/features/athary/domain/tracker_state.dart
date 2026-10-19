import 'dart:convert';

import '../../../core/utils/greeting.dart' show PrayerNameKey;
import 'daily_record.dart';
import 'habit.dart';

/// حالة المتتبع كاملة — **محضة بالكامل** (لا SharedPreferences ولا منصّة).
///
/// هذا هو قلب «أثري»: كل الحسابات اللي تهمّ المستخدم (السلسلة، الشبكة،
/// النسبة) موجودة هنا وحدها، وبتُختبر بدون أي محاكاة.
class TrackerState {
  const TrackerState({this.records = const <String, DailyRecord>{}});

  /// السجلات مفهرسة بمفتاح اليوم (`YYYY-MM-DD`).
  final Map<String, DailyRecord> records;

  /// حجم الشبكة المعروضة (مساهمات ٣٠ يوم).
  static const int windowDays = 30;

  /// كم يومًا نحتفظ به في التخزين.
  ///
  /// سنة + شهر يكفي لأي إحصاء معقول، وفي الوقت نفسه يمنع ملف
  /// `SharedPreferences` من النمو بلا حدّ على جهاز المستخدم.
  static const int retentionDays = 400;

  static const TrackerState empty = TrackerState();

  // ── القراءة ────────────────────────────────────────────
  DailyRecord recordFor(DateTime day) =>
      records[dayKey(day)] ?? DailyRecord(day: civilDay(day));

  int countFor(DateTime day) => recordFor(day).count;

  bool has(DateTime day, HabitId habit) => recordFor(day).has(habit);

  // ── الكتابة ────────────────────────────────────────────
  TrackerState toggle(DateTime day, HabitId habit) {
    final key = dayKey(day);
    final current = records[key] ?? DailyRecord(day: civilDay(day));
    final next = current.toggle(habit);

    final map = Map<String, DailyRecord>.from(records);
    if (next.isEmpty) {
      // لا نُخزّن أيامًا فارغة — أوضح في التصحيح وأصغر في التخزين.
      map.remove(key);
    } else {
      map[key] = next;
    }
    return TrackerState(records: map);
  }

  /// يحذف السجلات الأقدم من [retentionDays].
  TrackerState pruned(DateTime today) {
    if (records.isEmpty) return this;
    final oldest = civilDay(today).subtract(
      // `Duration` هنا مقبول: التقليم لا يحتاج دقة يوم مدني.
      const Duration(days: retentionDays),
    );
    final cutoff = dayKey(oldest);
    final map = Map<String, DailyRecord>.from(records)
      ..removeWhere((key, _) => key.compareTo(cutoff) < 0);
    return TrackerState(records: map);
  }

  // ── السلسلة (Streak) ───────────────────────────────────
  /// عدد الأيام المتصلة اللي فيها عبادة واحدة على الأقل.
  ///
  /// ⚠️ **قاعدة التسامح:** لو اليوم الحالي لسه فاضي، نعدّ من إمبارح.
  /// بدون القاعدة دي، السلسلة بتظهر «صفر» كل صباح — وده بالضبط
  /// السلوك اللي يخلي المستخدم يسيب المتتبع.
  int currentStreak(DateTime today) {
    var cursor = civilDay(today);
    if (countFor(cursor) == 0) cursor = previousDay(cursor);

    var streak = 0;
    while (countFor(cursor) > 0) {
      streak++;
      cursor = previousDay(cursor);
    }
    return streak;
  }

  /// أطول سلسلة في التاريخ المخزّن.
  int bestStreak() {
    if (records.isEmpty) return 0;
    final days = records.keys.toList()..sort();
    var best = 0;
    var run = 0;
    DateTime? previous;
    for (final key in days) {
      final day = dayFromKey(key);
      if (day == null) continue;
      if (previous != null && dayKey(previousDay(day)) == dayKey(previous)) {
        run++;
      } else {
        run = 1;
      }
      if (run > best) best = run;
      previous = day;
    }
    return best;
  }

  /// هل اليوم الحالي «منجَز بالكامل»؟
  bool isComplete(DateTime today) =>
      recordFor(today).count >= HabitId.values.length;

  // ── الشبكة ─────────────────────────────────────────────
  /// آخر [days] يومًا منتهية باليوم المعطى (ترتيب تصاعدي).
  List<DailyRecord> window(DateTime today, {int days = windowDays}) {
    final result = <DailyRecord>[];
    var cursor = civilDay(today);
    for (var i = 0; i < days; i++) {
      result.insert(0, recordFor(cursor));
      cursor = previousDay(cursor);
    }
    return result;
  }

  /// إجمالي العبادات المنجزة في نافذة [days] يومًا (للإحصاء).
  int totalInWindow(DateTime today, {int days = windowDays}) =>
      window(today, days: days).fold(0, (sum, r) => sum + r.count);

  /// نسبة الإنجاز في اليوم (٠–١).
  double ratioFor(DateTime today) =>
      recordFor(today).count / HabitId.values.length;

  /// العبادة التالية المقترحة — أكثر واحدة يفوّتها المستخدم.
  ///
  /// تُستخدم في بطاقة الشاشة الرئيسية عشان نقترح **فعلًا واحدًا**،
  /// مش ثمانية (قاعدة «فِعل واحد» من `docs/03`).
  HabitId? nextSuggestion(DateTime today) {
    final record = recordFor(today);
    for (final habit in habitOrder) {
      if (!record.has(habit)) return habit;
    }
    return null;
  }

  /// عبادة مرتبطة بوقت صلاة (لربط المتتبع بالمواقيت لاحقًا).
  static const Map<HabitId, PrayerNameKey> prayerLinks = {
    HabitId.fajr: PrayerNameKey.fajr,
    HabitId.duha: PrayerNameKey.sunrise,
  };

  // ── الترميز ────────────────────────────────────────────
  String encode() => jsonEncode(<String, Object>{
        'v': 1,
        'days': <String, Object>{
          for (final entry in records.entries) entry.key: entry.value.toJson()['h'],
        },
      });

  static TrackerState decode(String? raw) {
    if (raw == null || raw.isEmpty) return empty;
    Object? data;
    try {
      data = jsonDecode(raw);
    } catch (_) {
      return empty;
    }
    if (data is! Map) return empty;

    final daysRaw = data['days'];
    if (daysRaw is! Map) return empty;

    final records = <String, DailyRecord>{};
    for (final entry in daysRaw.entries) {
      final key = entry.key;
      if (key is! String) continue;
      final record = recordFromKey(key, entry.value);
      if (record != null && !record.isEmpty) records[key] = record;
    }
    return TrackerState(records: records);
  }
}

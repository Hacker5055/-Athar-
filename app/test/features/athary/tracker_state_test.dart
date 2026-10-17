import 'package:flutter_test/flutter_test.dart';

import 'package:athar/features/athary/domain/daily_record.dart';
import 'package:athar/features/athary/domain/habit.dart';
import 'package:athar/features/athary/domain/tracker_state.dart';

void main() {
  DateTime d(int y, int m, int day) => DateTime(y, m, day);

  /// يبني سلسلة من الأيام: `n` يوم منتهية في `end`، كلها بعبادة واحدة.
  TrackerState streakOf(DateTime end, int n, {HabitId habit = HabitId.fajr}) {
    var state = TrackerState.empty;
    var cursor = end;
    for (var i = 0; i < n; i++) {
      state = state.toggle(cursor, habit);
      cursor = previousDay(cursor);
    }
    return state;
  }

  group('DailyRecord', () {
    test('التبديل يضيف ثم يزيل', () {
      var record = DailyRecord(day: d(2026, 10, 18));
      expect(record.count, 0);
      record = record.toggle(HabitId.fajr);
      expect(record.count, 1);
      expect(record.has(HabitId.fajr), isTrue);
      record = record.toggle(HabitId.fajr);
      expect(record.count, 0);
      expect(record.has(HabitId.fajr), isFalse);
    });

    test('مستويات الكثافة', () {
      expect(DailyRecord.levelFor(0), DayLevel.none);
      expect(DailyRecord.levelFor(1), DayLevel.low);
      expect(DailyRecord.levelFor(2), DayLevel.low);
      expect(DailyRecord.levelFor(3), DayLevel.mid);
      expect(DailyRecord.levelFor(4), DayLevel.mid);
      expect(DailyRecord.levelFor(5), DayLevel.high);
      expect(DailyRecord.levelFor(6), DayLevel.high);
      expect(DailyRecord.levelFor(7), DayLevel.full);
      expect(DailyRecord.levelFor(8), DayLevel.full);
      // أكثر من عدد العبادات (دفاعًا ضد إضافة عبادة بدون تحديث الحدود)
      expect(DailyRecord.levelFor(99), DayLevel.full);
    });

    test('مفتاح اليوم بصفر بادئ (يُرتّب معجميًا)', () {
      expect(dayKey(d(2026, 1, 5)), '2026-01-05');
      expect(dayKey(d(2026, 12, 31)), '2026-12-31');
    });
  });

  group('اليوم المدني', () {
    test('civilDay يقطع الوقت', () {
      expect(civilDay(DateTime(2026, 10, 18, 23, 59, 59)), d(2026, 10, 18));
    });

    test('previousDay يعبر حدود الشهر والسنة', () {
      expect(previousDay(d(2026, 1, 1)), d(2025, 12, 31));
      expect(previousDay(d(2026, 3, 1)), d(2026, 2, 28));
      expect(previousDay(d(2024, 3, 1)), d(2024, 2, 29)); // سنة كبيسة
      expect(previousDay(d(2026, 10, 18)), d(2026, 10, 17));
    });

    test('dayFromKey ذهابًا وإيابًا', () {
      for (final key in ['2026-01-01', '2026-10-18', '2024-02-29']) {
        expect(dayKey(dayFromKey(key)!), key);
      }
    });

    test('dayFromKey يرفض الصيغ الغلط', () {
      expect(dayFromKey(''), isNull);
      expect(dayFromKey('2026'), isNull);
      expect(dayFromKey('2026-10'), isNull);
      expect(dayFromKey('2026-13-01'), isNull);
      expect(dayFromKey('2026-00-01'), isNull);
      expect(dayFromKey('س-ص-ش'), isNull);
    });
  });

  group('TrackerState — التبديل', () {
    test('يوم فارغ لا يُخزّن', () {
      var state = TrackerState.empty.toggle(d(2026, 10, 18), HabitId.fajr);
      expect(state.records.length, 1);
      state = state.toggle(d(2026, 10, 18), HabitId.fajr);
      expect(state.records.isEmpty, isTrue);
    });

    test('التبديل لا يؤثر على يوم آخر', () {
      final state = TrackerState.empty
          .toggle(d(2026, 10, 18), HabitId.fajr)
          .toggle(d(2026, 10, 17), HabitId.quran);
      expect(state.has(d(2026, 10, 18), HabitId.fajr), isTrue);
      expect(state.has(d(2026, 10, 18), HabitId.quran), isFalse);
      expect(state.has(d(2026, 10, 17), HabitId.quran), isTrue);
    });

    test('recordFor يوم غير مسجّل يرجّع سجلًا فارغًا (مش null)', () {
      expect(TrackerState.empty.countFor(d(2026, 10, 18)), 0);
      expect(TrackerState.empty.recordFor(d(2026, 10, 18)).isEmpty, isTrue);
    });
  });

  group('TrackerState — السلسلة', () {
    final today = d(2026, 10, 18);

    test('سلسلة متصلة', () {
      expect(streakOf(today, 5).currentStreak(today), 5);
    });

    test('حالة فارغة → صفر', () {
      expect(TrackerState.empty.currentStreak(today), 0);
    });

    test('⚠️ قاعدة التسامح: اليوم فاضي ← نعدّ من إمبارح', () {
      // بدون هذه القاعدة تظهر السلسلة «صفر» كل صباح قبل أي تأشير
      final state = streakOf(previousDay(today), 7);
      expect(state.currentStreak(today), 7);
    });

    test('انقطاع يوم يوقف العدّ', () {
      // ٣ أيام متصلة + فجوة + يوم معزول → السلسلة ٣
      var state = streakOf(today, 2); // اليوم وإمبارح
      final third = previousDay(previousDay(today));
      state = state.toggle(third, HabitId.fajr);
      expect(state.currentStreak(today), 3);

      final isolated = previousDay(previousDay(third)); // بعد فجوة
      state = state.toggle(isolated, HabitId.fajr);
      expect(state.currentStreak(today), 3);
    });

    test('أفضل سلسلة تتذكّر الماضي', () {
      var state = streakOf(d(2026, 1, 10), 30);
      // نكسر السلسلة الحالية
      state = state.toggle(d(2026, 10, 18), HabitId.fajr);
      expect(state.bestStreak(), 30);
      expect(state.currentStreak(d(2026, 10, 18)), 1);
    });

    test('أفضل سلسلة في تاريخ فارغ = صفر', () {
      expect(TrackerState.empty.bestStreak(), 0);
    });

    test('يوم كامل', () {
      var state = TrackerState.empty;
      for (final habit in HabitId.values) {
        state = state.toggle(today, habit);
      }
      expect(state.isComplete(today), isTrue);
      expect(state.ratioFor(today), 1.0);
      expect(state.nextSuggestion(today), isNull);
    });

    test('الاقتراح يرجّع أول عبادة ناقصة بالترتيب', () {
      final state = TrackerState.empty.toggle(today, HabitId.fajr);
      expect(state.nextSuggestion(today), HabitId.fivePrayers);
    });
  });

  group('TrackerState — الشبكة', () {
    final today = d(2026, 10, 18);

    test('٣٠ يومًا بترتيب تصاعدي ينتهي باليوم', () {
      final window = TrackerState.empty.window(today);
      expect(window.length, TrackerState.windowDays);
      expect(window.last.day, today);
      for (var i = 1; i < window.length; i++) {
        expect(
          window[i].day.isAfter(window[i - 1].day),
          isTrue,
          reason: 'الشبكة غير مرتبة عند $i',
        );
      }
    });

    test('الشبكة تعبر حدود الشهر والسنة بلا فراغات', () {
      final window = TrackerState.empty.window(d(2026, 1, 3), days: 10);
      expect(window.first.day, d(2025, 12, 25));
      expect(window.last.day, d(2026, 1, 3));
    });

    test('إجمالي النافذة', () {
      final state = streakOf(today, 3);
      expect(state.totalInWindow(today, days: 30), 3);
    });
  });

  group('TrackerState — الترميز', () {
    test('دورة كاملة', () {
      final state = TrackerState.empty
          .toggle(d(2026, 10, 18), HabitId.fajr)
          .toggle(d(2026, 10, 18), HabitId.quran)
          .toggle(d(2026, 10, 17), HabitId.fasting);

      final decoded = TrackerState.decode(state.encode());
      expect(decoded.records.length, 2);
      expect(decoded.has(d(2026, 10, 18), HabitId.fajr), isTrue);
      expect(decoded.has(d(2026, 10, 18), HabitId.quran), isTrue);
      expect(decoded.has(d(2026, 10, 18), HabitId.fasting), isFalse);
      expect(decoded.has(d(2026, 10, 17), HabitId.fasting), isTrue);
    });

    test('يقبل التالف ويرجّع فارغًا', () {
      for (final bad in ['', 'not json', '[]', 'null', '{"days": "yes"}']) {
        expect(TrackerState.decode(bad).records, isEmpty, reason: bad);
      }
      expect(TrackerState.decode(null).records, isEmpty);
    });

    test('يتجاهل المفاتيح والمعرّفات غير المعروفة', () {
      final decoded = TrackerState.decode(
        '{"v":1,"days":{"2026-10-18":["fajr","غلط",42],'
        '"تاريخ-غلط":["fajr"],"2026-13-40":["fajr"]}}',
      );
      expect(decoded.records.length, 1);
      expect(decoded.has(d(2026, 10, 18), HabitId.fajr), isTrue);
      expect(decoded.countFor(d(2026, 10, 18)), 1);
    });

    test('لا يُخزّن أيامًا فارغة بعد الفك', () {
      final decoded = TrackerState.decode('{"days":{"2026-10-18":[]}}');
      expect(decoded.records, isEmpty);
    });
  });

  group('TrackerState — التقليم', () {
    test('يحذف الأقدم من ٤٠٠ يوم ويحتفظ بالجديد', () {
      var state = TrackerState.empty
          .toggle(d(2026, 10, 18), HabitId.fajr)
          .toggle(d(2024, 1, 1), HabitId.fajr);

      final pruned = state.pruned(d(2026, 10, 18));
      expect(pruned.has(d(2026, 10, 18), HabitId.fajr), isTrue);
      expect(pruned.has(d(2024, 1, 1), HabitId.fajr), isFalse);
    });

    test('التقليم على تاريخ فارغ لا يكراش', () {
      expect(TrackerState.empty.pruned(d(2026, 10, 18)).records, isEmpty);
    });
  });

  group('HabitId', () {
    test('٨ عبادات بمعرّفات فريدة', () {
      expect(HabitId.values.length, 8);
      expect(HabitId.values.map((h) => h.id).toSet().length, 8);
    });

    test('habitFromId', () {
      expect(habitFromId('fajr'), HabitId.fajr);
      expect(habitFromId('fivePrayers'), HabitId.fivePrayers);
      expect(habitFromId('غير-موجود'), isNull);
      expect(habitFromId(null), isNull);
    });
  });
}

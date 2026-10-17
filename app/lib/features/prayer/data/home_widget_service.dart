import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:home_widget/home_widget.dart';

import '../../../core/l10n/strings.dart';
import '../../../core/settings/settings_controller.dart';
import '../../../core/utils/arabic_numbers.dart';
import '../../../core/utils/greeting.dart' show prayerOrderWithSunrise;
import '../../../core/utils/prayer_time_format.dart';
import '../application/prayer_providers.dart';
import 'timezone_service.dart';

/// ودجت الشاشة الرئيسية — الجانب الدارتي فقط.
///
/// `home_widget` **لا** يرسم الودجت: الرسم كود أصلي (Kotlin/Swift)، وهذه
/// الحزمة توفّر قناة واحدة لكتابة البيانات وطلب تحديثه. الكود الأصلي
/// موجود في `native/android/` ويُنسخ بـ `tool/setup_android.sh`.
abstract final class AtharWidget {
  /// اسم الـ `AppWidgetProvider` في أندرويد — **لازم يطابق** الاسم في
  /// `native/android/kotlin/.../AtharWidgetProvider.kt` تمامًا.
  static const String androidProvider = 'AtharWidgetProvider';

  /// اسم الودجت في iOS (WidgetKit `kind`).
  static const String iOSName = 'AtharWidget';

  // مفاتيح البيانات المشتركة بين دارت والكود الأصلي
  static const String keyNextName = 'next_name';
  static const String keyNextTime = 'next_time';
  static const String keyNextIn = 'next_in';
  static const String keyTimes = 'times';
  static const String keyPlace = 'place';
  static const String keyUpdated = 'updated_at';
}

/// يكتب بيانات اليوم في الودجت ويطلب تحديثه.
final homeWidgetServiceProvider = Provider<HomeWidgetService>((ref) {
  return HomeWidgetService(ref);
});

class HomeWidgetService {
  const HomeWidgetService(this._ref);

  final Ref _ref;

  /// يحدّث الودجت بما يراه المستخدم: الصلاة القادمة + مواقيت اليوم.
  ///
  /// ⚠️ كل شيء داخل try/catch: الودجت تحسين، **مش** سبب لانهيار التطبيق
  /// على جهاز ما يدعمه (بعض مشغّلات الأندرويد الصينية تمنعه تمامًا).
  Future<bool> sync() async {
    try {
      final s = _ref.read(stringsProvider);
      final locale = _ref.read(currentLocaleProvider);
      final digits = _ref.read(useArabicDigitsProvider);
      final place = _ref.read(activePlaceProvider);

      final day = _ref.read(todayPrayerProvider);
      if (day == null) {
        await HomeWidget.saveWidgetData<String>(
          AtharWidget.keyNextName,
          s.widgetNoData,
        );
        await HomeWidget.saveWidgetData<String>(AtharWidget.keyTimes, '');
        await _requestUpdate();
        return false;
      }

      final tomorrow = _ref.read(tomorrowPrayerProvider);
      final next = day.nextPrayer(DateTime.now(), tomorrow: tomorrow);

      if (next == null) {
        await HomeWidget.saveWidgetData<String>(
          AtharWidget.keyNextName,
          s.widgetNoData,
        );
      } else {
        final localNext =
            TimezoneService.toLocal(place, next.at);
        await HomeWidget.saveWidgetData<String>(
          AtharWidget.keyNextName,
          next.key.label(s),
        );
        await HomeWidget.saveWidgetData<String>(
          AtharWidget.keyNextTime,
          AtharTimeFormat.hm12(
            localNext,
            s: s,
            useArabicDigits: digits,
          ),
        );
        await HomeWidget.saveWidgetData<String>(
          AtharWidget.keyNextIn,
          ArabicNumbers.formatDuration(
            next.remaining,
            useArabicDigits: digits,
            showHours: true,
          ),
        );
      }

      // كل المواقيت في سطر واحد مفصول بـ `|` — أسهل كتير من ست مفاتيح
      // على الجانب الأصلي (ودجت أندرويد عندها عدد محدود من الحقول).
      final times = <String>[];
      for (final prayer in prayerOrderWithSunrise) {
        final instant = day.timeFor(prayer);
        if (instant == null) continue;
        final local = TimezoneService.toLocal(place, instant);
        times.add(
          '${prayer.name}|${AtharTimeFormat.hm(local, useArabicDigits: digits)}',
        );
      }
      await HomeWidget.saveWidgetData<String>(
        AtharWidget.keyTimes,
        times.join(';'),
      );
      await HomeWidget.saveWidgetData<String>(
        AtharWidget.keyPlace,
        place.nameFor(locale),
      );
      await HomeWidget.saveWidgetData<int>(
        AtharWidget.keyUpdated,
        DateTime.now().millisecondsSinceEpoch,
      );

      await _requestUpdate();
      return true;
    } catch (_) {
      return false;
    }
  }

  Future<void> _requestUpdate() => HomeWidget.updateWidget(
        name: AtharWidget.androidProvider,
        iOSName: AtharWidget.iOSName,
      );
}

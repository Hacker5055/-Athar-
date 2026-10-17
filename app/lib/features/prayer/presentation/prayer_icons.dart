import 'package:flutter/material.dart';

import '../../../core/utils/greeting.dart' show PrayerNameKey;

/// أيقونة كل صلاة — محتفظ بيها في مكان واحد عشان الواجهة تبقى متسقة
/// بين الشاشة الرئيسية وشاشة المواقيت والإشعارات (أسبوع ٣).
extension PrayerIconX on PrayerNameKey {
  IconData get icon => switch (this) {
        PrayerNameKey.fajr => Icons.nights_stay_outlined,
        PrayerNameKey.sunrise => Icons.wb_sunny_outlined,
        PrayerNameKey.dhuhr => Icons.light_mode_outlined,
        PrayerNameKey.asr => Icons.wb_twilight,
        PrayerNameKey.maghrib => Icons.dark_mode_outlined,
        PrayerNameKey.isha => Icons.nightlight_outlined,
      };
}

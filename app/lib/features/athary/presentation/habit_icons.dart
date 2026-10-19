import 'package:flutter/material.dart';

import '../domain/habit.dart';

/// أيقونة كل عبادة — مكان واحد عشان الشاشة الرئيسية و«أثري» والودجت
/// يستعملوا نفس الرمز.
extension HabitIconX on HabitId {
  IconData get icon => switch (this) {
        HabitId.fajr => Icons.nights_stay_outlined,
        HabitId.fivePrayers => Icons.mosque_outlined,
        HabitId.quran => Icons.menu_book_outlined,
        HabitId.morningAdhkar => Icons.wb_sunny_outlined,
        HabitId.eveningAdhkar => Icons.nightlight_outlined,
        HabitId.duha => Icons.wb_twilight,
        HabitId.sadaqa => Icons.volunteer_activism_outlined,
        HabitId.fasting => Icons.bedtime_outlined,
      };
}

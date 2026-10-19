import 'package:flutter/material.dart';

import '../../../../core/l10n/app_locale.dart';
import '../../../../core/l10n/strings.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/widgets/widgets.dart';
import '../../domain/daily_record.dart';
import '../../domain/habit.dart';
import '../habit_icons.dart';
import '../../../../core/theme/app_colors.dart';

/// بطاقة السلسلة — أكبر رقم في الشاشة لأنه أقوى محفّز سلوكي.
class StreakCard extends StatelessWidget {
  const StreakCard({
    super.key,
    required this.streak,
    required this.best,
    required this.doneToday,
    required this.totalHabits,
    required this.s,
    required this.useArabicDigits,
  });

  final int streak;
  final int best;
  final int doneToday;
  final int totalHabits;
  final Strings s;
  final bool useArabicDigits;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return AtharCard(
      child: Row(
        children: [
          const Text('🔥', style: TextStyle(fontSize: 26)),
          const SizedBox(width: AppSpacing.md),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  _n(streak),
                  style: context.textStyles.displayLarge?.copyWith(
                    color: c.primary,
                    fontWeight: FontWeight.w700,
                  ),
                ),
                Text(
                  s.streakDays,
                  style: context.textStyles.bodySmall
                      ?.copyWith(color: c.ink60),
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                '${_n(doneToday)}/${_n(totalHabits)}',
                style: context.textStyles.titleLarge
                    ?.copyWith(fontWeight: FontWeight.w700),
              ),
              Text(
                s.today,
                style:
                    context.textStyles.bodySmall?.copyWith(color: c.ink60),
              ),
              if (best > streak) ...[
                const SizedBox(height: 2),
                Text(
                  '${s.atharyBest} ${_n(best)}',
                  style: context.textStyles.bodySmall
                      ?.copyWith(color: c.ink40),
                ),
              ],
            ],
          ),
        ],
      ),
    );
  }

  String _n(int value) =>
      ArabicNumbers.format(value, useArabicDigits: useArabicDigits);
}

/// شبكة ٣٠ يوم — تكثيف لوني حسب عدد العبادات (مثل مساهمات GitHub).
///
/// ⚠️ اللون **مش** الإشارة الوحيدة: كل خلية لها رقيقة (`tooltip`) فيها
/// التاريخ والعدد، والعدد الإجمالي مكتوب فوق — احترامًا لإمكانية الوصول.
class DaysGrid extends StatelessWidget {
  const DaysGrid({
    super.key,
    required this.days,
    required this.s,
    required this.useArabicDigits,
    this.columns = 10,
  });

  final List<DailyRecord> days;
  final Strings s;
  final bool useArabicDigits;
  final int columns;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    final cell = 26.0;

    return Column(
      children: [
        Wrap(
          spacing: 5,
          runSpacing: 5,
          children: [
            for (final day in days)
              Tooltip(
                message: '${day.key} · ${ArabicNumbers.format(
                  day.count,
                  useArabicDigits: useArabicDigits,
                )}',
                child: Container(
                  width: cell,
                  height: cell,
                  decoration: BoxDecoration(
                    color: _colorFor(day.level, c),
                    borderRadius: BorderRadius.circular(6),
                    border: Border.all(
                      color: c.border,
                      width: day.level == DayLevel.none ? 1 : 0,
                    ),
                  ),
                ),
              ),
          ],
        ),
        const SizedBox(height: AppSpacing.sm),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              s.atharyLess,
              style: context.textStyles.bodySmall?.copyWith(color: c.ink40),
            ),
            Row(
              children: [
                for (final level in DayLevel.values)
                  Padding(
                    padding: const EdgeInsets.only(left: 4),
                    child: Container(
                      width: 12,
                      height: 12,
                      decoration: BoxDecoration(
                        color: _colorFor(level, c),
                        borderRadius: BorderRadius.circular(3),
                        border: level == DayLevel.none
                            ? Border.all(color: c.border)
                            : null,
                      ),
                    ),
                  ),
              ],
            ),
            Text(
              s.atharyMore,
              style: context.textStyles.bodySmall?.copyWith(color: c.ink40),
            ),
          ],
        ),
      ],
    );
  }

  Color _colorFor(DayLevel level, AppColorSet c) => switch (level) {
        DayLevel.none => c.surface2,
        DayLevel.low => c.primary200,
        DayLevel.mid => c.primary.fade(0.55),
        DayLevel.high => c.primary.fade(0.8),
        DayLevel.full => c.primary,
      };
}

/// صف عبادة قابلة للتبديل.
class HabitTile extends StatelessWidget {
  const HabitTile({
    super.key,
    required this.habit,
    required this.done,
    required this.locale,
    required this.onToggle,
  });

  final HabitId habit;
  final bool done;
  final AppLocale locale;
  final ValueChanged<bool> onToggle;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return InkWell(
      onTap: () => onToggle(!done),
      child: Padding(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.lg,
          vertical: AppSpacing.md,
        ),
        child: Row(
          children: [
            AnimatedContainer(
              duration: AppDuration.state,
              width: 26,
              height: 26,
              decoration: BoxDecoration(
                color: done ? c.primary : Colors.transparent,
                border: Border.all(
                  color: done ? c.primary : c.borderStrong,
                  width: 2,
                ),
                borderRadius: BorderRadius.circular(8),
              ),
              child: done
                  ? Icon(Icons.check, size: 16, color: c.onPrimary)
                  : null,
            ),
            const SizedBox(width: AppSpacing.md),
            Icon(
              habit.icon,
              size: 20,
              color: done ? c.primary : c.ink60,
            ),
            const SizedBox(width: AppSpacing.md),
            Expanded(
              child: Text(
                habit.nameFor(locale),
                style: context.textStyles.titleMedium?.copyWith(
                  color: done ? c.ink40 : null,
                  decoration: done ? TextDecoration.lineThrough : null,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

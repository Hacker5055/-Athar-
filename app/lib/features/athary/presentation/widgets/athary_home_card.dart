import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/l10n/app_locale.dart';
import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/widgets/widgets.dart';
import '../../application/tracker_controller.dart';
import '../../domain/habit.dart';
import '../habit_icons.dart';

/// بطاقة «أثري» المدمجة على الشاشة الرئيسية.
///
/// الهدف منها **فِعل واحد سريع** (تأشير عبادة) + لمحة عن السلسلة،
/// مش شاشة كاملة — الشاشة الكاملة لها طريقها من هنا.
class AtharyHomeCard extends ConsumerWidget {
  const AtharyHomeCard({super.key, required this.onOpen});

  final VoidCallback onOpen;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final locale = ref.watch(currentLocaleProvider);
    final arabicDigits = ref.watch(useArabicDigitsProvider);
    final state = ref.watch(trackerProvider);
    final today = ref.watch(todayProvider);
    final c = context.colors;

    final doneToday = state.countFor(today);
    final streak = state.currentStreak(today);
    final n = (int v) => ArabicNumbers.format(v, useArabicDigits: arabicDigits);

    return AtharCard(
      onTap: onOpen,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            children: [
              const Text('🔥', style: TextStyle(fontSize: 18)),
              const SizedBox(width: AppSpacing.sm),
              Expanded(
                child: Text.rich(
                  TextSpan(
                    children: [
                      TextSpan(
                        text: n(streak),
                        style: context.textStyles.titleLarge?.copyWith(
                          color: c.primary,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      TextSpan(
                        text: ' ${s.streakDays}',
                        style: context.textStyles.bodySmall
                            ?.copyWith(color: c.ink60),
                      ),
                    ],
                  ),
                ),
              ),
              Text(
                '${n(doneToday)}/${n(HabitId.values.length)}',
                style: context.textStyles.titleMedium?.copyWith(
                  color: c.ink70,
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.md),

          // شرائح العبادات — نقرة واحدة تأشّر
          Wrap(
            spacing: AppSpacing.sm,
            runSpacing: AppSpacing.sm,
            children: [
              for (final habit in habitOrder)
                AtharChip(
                  label: habit.nameFor(locale),
                  icon: habit.icon,
                  selected: state.has(today, habit),
                  onTap: (_) => ref
                      .read(trackerProvider.notifier)
                      .toggle(habit, at: today),
                ),
            ],
          ),
        ],
      ),
    );
  }
}

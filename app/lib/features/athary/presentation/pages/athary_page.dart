import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/widgets/widgets.dart';
import '../../application/tracker_controller.dart';
import '../../domain/daily_record.dart';
import '../../domain/habit.dart';
import '../../domain/tracker_state.dart';
import '../habit_icons.dart';
import '../widgets/athary_widgets.dart';

/// شاشة «أثري» — متتبع العبادات اليومية (أسبوع ٤، v1).
///
/// تتكوّن من ثلاثة أقسام بالترتيب اللي يقرأها المستخدم:
/// ١. السلسلة (أقوى محفّز) · ٢. شبكة ٣٠ يومًا · ٣. عبادات اليوم.
class AtharyPage extends ConsumerWidget {
  const AtharyPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final locale = ref.watch(currentLocaleProvider);
    final arabicDigits = ref.watch(useArabicDigitsProvider);
    final state = ref.watch(trackerProvider);
    final today = ref.watch(todayProvider);
    final c = context.colors;

    final doneToday = state.countFor(today);
    final window = state.window(today);
    final suggestion = state.nextSuggestion(today);

    return Scaffold(
      appBar: AppBar(
        title: Text(s.trackerTitle),
        actions: [
          if (state.records.isNotEmpty)
            IconButton(
              icon: const Icon(Icons.delete_outline),
              tooltip: s.atharyReset,
              onPressed: () => _confirmReset(context, ref),
            ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(AppSpacing.lg),
        children: [
          StreakCard(
            streak: state.currentStreak(today),
            best: state.bestStreak(),
            doneToday: doneToday,
            totalHabits: HabitId.values.length,
            s: s,
            useArabicDigits: arabicDigits,
          ),

          if (suggestion != null) ...[
            const SizedBox(height: AppSpacing.md),
            _SuggestionChip(
              habit: suggestion,
              locale: locale,
              s: s,
              onTap: () => ref
                  .read(trackerProvider.notifier)
                  .toggle(suggestion, at: today),
            ),
          ],

          const SizedBox(height: AppSpacing.lg),
          SectionHeader(title: s.last30),
          const SizedBox(height: AppSpacing.sm),
          AtharCard(
            child: DaysGrid(
              days: window,
              s: s,
              useArabicDigits: arabicDigits,
            ),
          ),

          const SizedBox(height: AppSpacing.lg),
          SectionHeader(title: s.habits),
          const SizedBox(height: AppSpacing.sm),
          AtharCard(
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
            child: Column(
              children: [
                for (final habit in habitOrder) ...[
                  if (habit != habitOrder.first)
                    Divider(color: c.border, height: 1),
                  HabitTile(
                    habit: habit,
                    done: state.has(today, habit),
                    locale: locale,
                    onToggle: (_) => ref
                        .read(trackerProvider.notifier)
                        .toggle(habit, at: today),
                  ),
                ],
              ],
            ),
          ),

          const SizedBox(height: AppSpacing.lg),
          _FutureNote(s: s),

          const SizedBox(height: AppSpacing.xxl),
        ],
      ),
    );
  }

  Future<void> _confirmReset(BuildContext context, WidgetRef ref) async {
    final s = ref.read(stringsProvider);
    final ok = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(s.atharyReset),
        content: Text(s.atharyResetConfirm),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text(s.cancel),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(true),
            child: Text(s.atharyReset),
          ),
        ],
      ),
    );
    if (ok != true) return;
    await ref.read(trackerProvider.notifier).clearAll();
  }
}

/// «اللي فاضلك النهاردة» — فِعل واحد مقترح، مش ثمانية.
class _SuggestionChip extends StatelessWidget {
  const _SuggestionChip({
    required this.habit,
    required this.locale,
    required this.s,
    required this.onTap,
  });

  final HabitId habit;
  final AppLocale locale;
  final Strings s;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return InkWell(
      onTap: onTap,
      borderRadius: AppRadius.cardRadius,
      child: Container(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.md,
          vertical: AppSpacing.sm,
        ),
        decoration: BoxDecoration(
          color: c.primary100,
          borderRadius: AppRadius.cardRadius,
        ),
        child: Row(
          children: [
            Icon(habit.icon, size: 18, color: c.primary),
            const SizedBox(width: AppSpacing.sm),
            Expanded(
              child: Text(
                '${s.atharyNext} · ${habit.nameFor(locale)}',
                style: context.textStyles.bodyMedium,
              ),
            ),
            Icon(Icons.add_circle_outline, size: 18, color: c.primary),
          ],
        ),
      ),
    );
  }
}

/// ملاحظة «القادم» بأسلوب البروتوتايب — توضّح حدود v1 بصراحة.
class _FutureNote extends StatelessWidget {
  const _FutureNote({required this.s});

  final Strings s;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: c.surface2,
        borderRadius: AppRadius.cardRadius,
      ),
      child: Row(
        children: [
          Icon(Icons.lightbulb_outline, size: 18, color: c.ink60),
          const SizedBox(width: AppSpacing.sm),
          Expanded(
            child: Text(
              s.atharyFutureNote,
              style: context.textStyles.bodySmall?.copyWith(color: c.ink60),
            ),
          ),
        ],
      ),
    );
  }
}

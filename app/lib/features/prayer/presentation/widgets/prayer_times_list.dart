import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/greeting.dart';
import '../../../../core/utils/prayer_time_format.dart';
import '../../application/prayer_providers.dart';
import '../../domain/entities/prayer_day.dart';
import '../prayer_icons.dart';

/// جدول المواقيت الستة — يبرز الصلاة الحالية والجاية.
///
/// يُستخدم في الشاشة الرئيسية (مختصر) وفي شاشة المواقيت (كامل).
class PrayerTimesList extends ConsumerWidget {
  const PrayerTimesList({
    super.key,
    required this.day,
    this.onPrayerTap,
  });

  final PrayerDay day;

  /// عند النقر على صلاة (لتفعيل/إلغاء الإشعار في أسبوع ٣).
  final void Function(PrayerNameKey key)? onPrayerTap;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final arabicDigits = ref.watch(useArabicDigitsProvider);
    final next = ref.watch(nextPrayerProvider);
    final current = ref.watch(currentPrayerProvider);

    return Column(
      children: [
        for (final prayer in prayerOrderWithSunrise)
          _PrayerRow(
            key: ValueKey(prayer),
            day: day,
            prayer: prayer,
            label: prayer.label(s),
            isCurrent: prayer == current,
            isNext: prayer == next?.key && !next!.isTomorrow,
            useArabicDigits: arabicDigits,
            s: s,
            onTap: onPrayerTap == null ? null : () => onPrayerTap!(prayer),
          ),
      ],
    );
  }
}

class _PrayerRow extends StatelessWidget {
  const _PrayerRow({
    required this.day,
    required this.prayer,
    required this.label,
    required this.isCurrent,
    required this.isNext,
    required this.useArabicDigits,
    required this.s,
    this.onTap,
    super.key,
  });

  final PrayerDay day;
  final PrayerNameKey prayer;
  final String label;
  final bool isCurrent;
  final bool isNext;
  final bool useArabicDigits;
  final Strings s;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    final local = localTimeFor(day, prayer);
    final isSunrise = prayer == PrayerNameKey.sunrise;

    final highlight = isNext && !isSunrise;
    final bg = highlight ? c.primary.alpha(0.08) : Colors.transparent;
    final fg = highlight ? c.primary : c.ink;

    return InkWell(
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: bg,
          border: Border(
            bottom: BorderSide(color: c.border, width: 1),
          ),
        ),
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.lg,
          vertical: AppSpacing.md,
        ),
        child: Row(
          children: [
            Icon(prayer.icon, size: 18, color: highlight ? c.primary : c.ink60),
            const SizedBox(width: AppSpacing.md),
            Expanded(
              child: Text(
                label,
                style: context.textStyles.bodyLarge?.copyWith(
                  color: fg,
                  fontWeight:
                      highlight ? FontWeight.w600 : FontWeight.normal,
                ),
              ),
            ),
            if (isCurrent && !isSunrise)
              Padding(
                padding: const EdgeInsetsDirectional.only(end: AppSpacing.sm),
                child: Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppSpacing.sm,
                    vertical: 2,
                  ),
                  decoration: BoxDecoration(
                    color: c.accent.alpha(0.15),
                    borderRadius: AppRadius.chipRadius,
                  ),
                  child: Text(
                    s.prayerNow,
                    style: context.textStyles.labelSmall?.copyWith(
                      color: c.accent,
                    ),
                  ),
                ),
              ),
            if (isNext && !isSunrise)
              Padding(
                padding: const EdgeInsetsDirectional.only(end: AppSpacing.sm),
                child: Text(
                  s.prayerUpcoming,
                  style: context.textStyles.labelSmall?.copyWith(
                    color: c.primary,
                  ),
                ),
              ),
            Text(
              local == null
                  ? '--:--'
                  : AtharTimeFormat.hm12(
                      local,
                      s: s,
                      useArabicDigits: useArabicDigits,
                    ),
              style: context.textStyles.titleMedium?.copyWith(
                color: fg,
                fontFeatures: const [FontFeature.tabularFigures()],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

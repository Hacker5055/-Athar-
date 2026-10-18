import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/utils/prayer_time_format.dart';
import '../../application/prayer_providers.dart';

/// بطاقة الصلاة القادمة — العدّ التنازلي الحيّ في أعلى الشاشة الرئيسية.
///
/// بتسمع [clockTickProvider] فتتحدّث كل ثانية، وبتراقب
/// [todayPrayerProvider] فتتحدّث فور تغيير المدينة أو الطريقة.
class NextPrayerCard extends ConsumerWidget {
  const NextPrayerCard({super.key, this.onTap});

  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final locale = ref.watch(currentLocaleProvider);
    final arabicDigits = ref.watch(useArabicDigitsProvider);
    final day = ref.watch(todayPrayerProvider);
    final next = ref.watch(nextPrayerProvider);

    final c = context.colors;

    if (day == null || next == null) {
      return Container(
        width: double.infinity,
        padding: const EdgeInsets.all(AppSpacing.lg),
        decoration: BoxDecoration(
          color: c.surface,
          borderRadius: AppRadius.cardRadius,
          border: Border.all(color: c.border),
        ),
        child: Row(
          children: [
            const Icon(Icons.schedule_outlined),
            const SizedBox(width: AppSpacing.md),
            Expanded(child: Text(s.prayerUnavailable)),
          ],
        ),
      );
    }

    final localNext = localTimeFor(day, next.key);
    final gradient = LinearGradient(
      begin: Alignment.topRight,
      end: Alignment.bottomLeft,
      colors: [c.primary, c.primary600],
    );

    return InkWell(
      onTap: onTap,
      borderRadius: AppRadius.cardRadius,
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.all(AppSpacing.lg),
        decoration: BoxDecoration(
          gradient: gradient,
          borderRadius: AppRadius.cardRadius,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              next.isTomorrow ? s.prayerTomorrowFajr : s.prayerNextLabel,
              style: context.textStyles.bodySmall?.copyWith(
                color: c.onPrimary.fade(0.8),
              ),
            ),
            const SizedBox(height: AppSpacing.xs),
            Row(
              children: [
                Expanded(
                  child: Text(
                    next.key.label(s),
                    style: context.textStyles.headlineSmall?.copyWith(
                      color: c.onPrimary,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                if (localNext != null)
                  Text(
                    AtharTimeFormat.hm12(
                      localNext,
                      s: s,
                      useArabicDigits: arabicDigits,
                    ),
                    style: context.textStyles.titleLarge?.copyWith(
                      color: c.onPrimary,
                    ),
                  ),
              ],
            ),
            const SizedBox(height: AppSpacing.md),
            Row(
              children: [
                Icon(
                  Icons.hourglass_bottom,
                  size: 16,
                  color: c.onPrimary.fade(0.75),
                ),
                const SizedBox(width: AppSpacing.sm),
                Text(
                  ArabicNumbers.formatDuration(
                    next.remaining,
                    useArabicDigits: arabicDigits,
                  ),
                  style: context.textStyles.titleMedium?.copyWith(
                    color: c.onPrimary,
                    fontFeatures: const [FontFeature.tabularFigures()],
                  ),
                ),
                const Spacer(),
                Icon(
                  Icons.location_on_outlined,
                  size: 14,
                  color: c.onPrimary.fade(0.7),
                ),
                const SizedBox(width: 4),
                Flexible(
                  child: Text(
                    day.place.nameFor(locale),
                    style: context.textStyles.bodySmall?.copyWith(
                      color: c.onPrimary.fade(0.8),
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

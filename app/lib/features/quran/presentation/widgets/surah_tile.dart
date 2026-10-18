import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/settings/settings_controller.dart'
    show stringsProvider, useArabicDigitsProvider;
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/widgets/widgets.dart';
import '../../domain/entities/surah.dart';

/// صف سورة في الفهرس.
///
/// الترتيب البصري ثابت: رقم السورة (دائرة) · الاسم العربي + النوع ·
/// عدد الآيات ورقم الصفحة. الرقم داخل دائرة لأنه **مفتاح تنقّل** في
/// المصحف («اقرأ سورة ٣٦») لا مجرد ترتيب في قائمة.
class SurahTile extends ConsumerWidget {
  const SurahTile({
    super.key,
    required this.surah,
    required this.onTap,
  });

  final Surah surah;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final c = context.colors;
    final arabic = ref.watch(useArabicDigitsProvider);

    return AtharCard(
      margin: const EdgeInsets.only(bottom: AppSpacing.sm),
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.md,
        vertical: AppSpacing.md,
      ),
      onTap: onTap,
      child: Row(
        children: [
          Container(
            width: 40,
            height: 40,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: c.primary100,
              borderRadius: BorderRadius.circular(AppRadius.chip),
            ),
            child: Text(
              ArabicNumbers.format(surah.id, useArabicDigits: arabic),
              style: context.textStyles.labelLarge?.copyWith(
                color: c.primary600,
                fontWeight: FontWeight.w700,
              ),
            ),
          ),
          const HSpace(AppSpacing.md),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  surah.nameAr,
                  style: context.textStyles.titleMedium?.copyWith(
                    color: c.ink,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const VSpace(2),
                Text(
                  '${surah.revelation == RevelationType.meccan ? s.meccan : s.medinan}'
                  ' · ${ArabicNumbers.format(surah.ayahCount, useArabicDigits: arabic)}'
                  ' ${s.verses}'
                  ' · ${s.quranPageWord}'
                  ' ${ArabicNumbers.format(surah.startPage, useArabicDigits: arabic)}',
                  style: context.textStyles.bodySmall?.copyWith(color: c.ink60),
                ),
              ],
            ),
          ),
          Icon(Icons.chevron_left_rounded, size: 20, color: c.ink40),
        ],
      ),
    );
  }
}

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/settings/settings_controller.dart'
    show stringsProvider, useArabicDigitsProvider;
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/theme/quran_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/widgets/widgets.dart';
import '../../domain/entities/ayah.dart';
import '../../domain/quran_settings.dart';

/// ⚠️ **لا يوجد عنوان بسملة منفصل.**
/// النص العثماني (Tanzil) يجعل البسملة جزءًا من الآية الأولى في كل
/// سورة عدا التوبة — عرضها كعنوان يعني تكرارها. والتوبة تُعرض بلا
/// بسملة تلقائيًا لأن نصها لا يحتويها، بلا أي استثناء في الكود.

/// آية واحدة في وضع القائمة.
///
/// ### قرار التصميم: النص متّصل والرقم في النهاية
/// الرسم العثماني لا يُقطَّع بين الأسطر في وضع القائمة — الآية تُعرض
/// كفقرة واحدة، ورقمها في دائرة بنهايتها كما في المصحف. هذا يحافظ على
/// الإيقاع البصري المعتاد لقارئ القرآن.
class AyahView extends ConsumerWidget {
  const AyahView({
    super.key,
    required this.ayah,
    required this.settings,
    this.surahName,
    this.onLongPress,
  });

  final Ayah ayah;
  final QuranSettings settings;

  /// يُعرض كعنوان عند أول آية من كل سورة (في عرض الجزء/الصفحة).
  final String? surahName;

  final VoidCallback? onLongPress;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final arabic = ref.watch(useArabicDigitsProvider);
    final colors = settings.pageTheme.colors;

    return InkWell(
      onLongPress: onLongPress,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(
          AppSpacing.lg,
          AppSpacing.md,
          AppSpacing.lg,
          AppSpacing.md,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (surahName != null) ...[
              _SurahBanner(name: surahName!, colors: colors),
              const VSpace(AppSpacing.md),
            ],

            // ── النص العثماني + رقم الآية ──
            RichText(
              textAlign: TextAlign.justify,
              textDirection: TextDirection.rtl,
              text: TextSpan(
                style: AppTypography.quranStyle(
                  color: colors.text,
                  fontSize: AppTypography.quranFontSize * settings.fontScale,
                ),
                children: [
                  TextSpan(text: ayah.textUthmani),
                  const TextSpan(text: ' '),
                  WidgetSpan(
                    alignment: PlaceholderAlignment.middle,
                    child: _AyahNumberMarker(
                      number: ArabicNumbers.format(
                        ayah.ayah,
                        useArabicDigits: arabic,
                      ),
                      colors: colors,
                    ),
                  ),
                ],
              ),
            ),

            if (ayah.isSajda) ...[
              const VSpace(AppSpacing.sm),
              _SajdaBadge(label: s.quranSajda, colors: colors),
            ],

            if (settings.showTranslation && ayah.translation != null) ...[
              const VSpace(AppSpacing.md),
              Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: AppSpacing.md,
                  vertical: AppSpacing.sm,
                ),
                decoration: BoxDecoration(
                  color: colors.markerBorder.fade(0.35),
                  borderRadius: BorderRadius.circular(AppRadius.chip),
                ),
                child: Text(
                  ayah.translation!,
                  // الترجمات لاتينية الاتجاه؛ العربي يُفرض RTL دائمًا.
                  textDirection: _direction(ayah.translation!),
                  style: context.textStyles.bodyMedium?.copyWith(
                    color: colors.text.fade(0.75),
                    height: 1.6,
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  static TextDirection _direction(String text) {
    for (final rune in text.runes) {
      if (rune >= 0x0600 && rune <= 0x06FF) return TextDirection.rtl;
      if (rune > 0x07FF) break;
    }
    return TextDirection.ltr;
  }
}

class _AyahNumberMarker extends StatelessWidget {
  const _AyahNumberMarker({required this.number, required this.colors});

  static const double _size = 26;

  final String number;
  final QuranThemeColors colors;

  @override
  Widget build(BuildContext context) => Container(
        width: _size,
        height: _size,
        alignment: Alignment.center,
        decoration: BoxDecoration(
          color: colors.markerBorder.fade(0.5),
          borderRadius: BorderRadius.circular(_size / 2),
          border: Border.all(color: colors.accent.fade(0.35), width: 1),
        ),
        child: FittedBox(
          child: Text(
            number,
            style: TextStyle(
              color: colors.accent,
              fontSize: 12,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      );
}

class _SajdaBadge extends StatelessWidget {
  const _SajdaBadge({required this.label, required this.colors});

  final String label;
  final QuranThemeColors colors;

  @override
  Widget build(BuildContext context) => Align(
        alignment: AlignmentDirectional.centerStart,
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
          decoration: BoxDecoration(
            color: colors.accent.fade(0.16),
            borderRadius: BorderRadius.circular(AppRadius.chip),
            border: Border.all(color: colors.accent.fade(0.4)),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(Icons.keyboard_double_arrow_down_rounded,
                  size: 13, color: colors.accent),
              const HSpace(4),
              Text(
                label,
                style: context.textStyles.labelSmall?.copyWith(
                  color: colors.accent,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ),
      );
}

/// عنوان السورة — يظهر في عرض الجزء/الصفحة عند عبور حد سورة.
class SurahBanner extends StatelessWidget {
  const SurahBanner({super.key, required this.name, required this.colors});

  final String name;
  final QuranThemeColors colors;

  @override
  Widget build(BuildContext context) => _SurahBanner(name: name, colors: colors);
}

class _SurahBanner extends StatelessWidget {
  const _SurahBanner({required this.name, required this.colors});

  final String name;
  final QuranThemeColors colors;

  @override
  Widget build(BuildContext context) => Container(
        padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
        decoration: BoxDecoration(
          color: colors.markerBorder.fade(0.4),
          borderRadius: BorderRadius.circular(AppRadius.chip),
        ),
        child: Center(
          child: Text(
            name,
            style: AppTypography.quranStyle(
              color: colors.text,
              fontSize: 18,
              height: 1.6,
            ),
          ),
        ),
      );
}

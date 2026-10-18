import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart' show stringsProvider;
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/theme/quran_theme.dart';
import '../../../../core/widgets/widgets.dart';
import '../../application/quran_providers.dart';
import '../../application/quran_settings_controller.dart';

/// ورقة إعدادات القراءة: حجم الخط · الثيم · الترجمة.
///
/// يأخذ `ref` لأن العنوان مترجَم، والورقة تُفتح من شاشات `Consumer`.
Future<void> showReadingSettingsSheet(BuildContext context, WidgetRef ref) async {
  await showAtharSheet<void>(
    context: context,
    title: ref.read(stringsProvider).quranReaderSettings,
    body: const _ReadingSettingsBody(),
  );
}

class _ReadingSettingsBody extends ConsumerWidget {
  const _ReadingSettingsBody();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final settings = ref.watch(quranSettingsProvider);
    final controller = ref.read(quranSettingsProvider.notifier);
    final editions = ref.watch(availableEditionsProvider);
    final c = context.colors;

    return Padding(
      padding: const EdgeInsets.fromLTRB(
        AppSpacing.screenPaddingH,
        0,
        AppSpacing.screenPaddingH,
        AppSpacing.xl,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        mainAxisSize: MainAxisSize.min,
        children: [
          // ── حجم الخط ──
          Row(
            children: [
              Expanded(
                child: Text(
                  s.quranFontSize,
                  style: context.textStyles.titleSmall?.copyWith(color: c.ink),
                ),
              ),
              IconButton.filledTonal(
                visualDensity: VisualDensity.compact,
                onPressed: settings.fontScale > QuranSettings.minFontScale
                    ? () => controller.nudgeFontScale(
                        -QuranSettings.fontScaleStep)
                    : null,
                icon: const Icon(Icons.remove_rounded),
                tooltip: s.quranFontSmaller,
              ),
              SizedBox(
                width: 52,
                child: Center(
                  child: Text(
                    '${(settings.fontScale * 100).round()}%',
                    style: context.textStyles.labelLarge?.copyWith(
                      color: c.primary600,
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                ),
              ),
              IconButton.filledTonal(
                visualDensity: VisualDensity.compact,
                onPressed: settings.fontScale < QuranSettings.maxFontScale
                    ? () =>
                        controller.nudgeFontScale(QuranSettings.fontScaleStep)
                    : null,
                icon: const Icon(Icons.add_rounded),
                tooltip: s.quranFontBigger,
              ),
            ],
          ),
          Slider(
            value: settings.fontScale,
            min: QuranSettings.minFontScale,
            max: QuranSettings.maxFontScale,
            onChanged: controller.setFontScale,
          ),

          // ── عيّنة حيّة ──
          Container(
            padding: const EdgeInsets.all(AppSpacing.md),
            decoration: BoxDecoration(
              color: settings.pageTheme.colors.background,
              borderRadius: BorderRadius.circular(AppRadius.chip),
              border: Border.all(color: c.border),
            ),
            child: Text(
              'بِسْمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ',
              textDirection: TextDirection.rtl,
              textAlign: TextAlign.center,
              style: AppTypography.quranStyle(
                color: settings.pageTheme.colors.text,
                fontSize: AppTypography.quranFontSize * settings.fontScale * 0.8,
              ),
            ),
          ),
          const VSpace(AppSpacing.lg),

          // ── الثيم ──
          Text(
            s.quranReadingTheme,
            style: context.textStyles.titleSmall?.copyWith(color: c.ink),
          ),
          const VSpace(AppSpacing.sm),
          Wrap(
            spacing: AppSpacing.sm,
            children: [
              for (final theme in QuranPageTheme.values)
                AtharChip(
                  label: _themeLabel(s, theme),
                  selected: settings.pageTheme == theme,
                  onTap: (_) => controller.setPageTheme(theme),
                ),
            ],
          ),
          const VSpace(AppSpacing.lg),

          // ── الترجمة ──
          SwitchListTile.adaptive(
            contentPadding: EdgeInsets.zero,
            title: Text(
              s.quranShowTranslation,
              style: context.textStyles.bodyLarge?.copyWith(color: c.ink),
            ),
            value: settings.showTranslation,
            onChanged: editions.isEmpty ? null : controller.setShowTranslation,
          ),
          if (editions.isEmpty)
            Padding(
              padding: const EdgeInsets.only(bottom: AppSpacing.sm),
              child: Text(
                s.quranNoTranslation,
                style: context.textStyles.bodySmall?.copyWith(color: c.ink60),
              ),
            ),

          if (editions.length > 1) ...[
            const VSpace(AppSpacing.sm),
            Text(
              s.quranEdition,
              style: context.textStyles.titleSmall?.copyWith(color: c.ink),
            ),
            const VSpace(AppSpacing.sm),
            Wrap(
              spacing: AppSpacing.sm,
              children: [
                for (final edition in editions)
                  AtharChip(
                    label: edition,
                    selected: (settings.edition ?? editions.first) == edition,
                    onTap: (_) => controller.setEdition(edition),
                  ),
              ],
            ),
          ],
        ],
      ),
    );
  }

  String _themeLabel(Strings s, QuranPageTheme theme) => switch (theme) {
        QuranPageTheme.paper => s.quranThemePaper,
        QuranPageTheme.sepia => s.quranThemeSepia,
        QuranPageTheme.night => s.quranThemeNight,
      };
}

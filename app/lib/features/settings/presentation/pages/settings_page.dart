import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/l10n/app_locale.dart';
import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/widgets/widgets.dart';

/// شاشة الإعدادات.
///
/// كل إعداد يوضّح **قيمته الحالية** على الطرف الآخر بدون فتح شاشة تانية
/// (قرار تصميم موثّق في `docs/04` §8).
class SettingsPage extends ConsumerWidget {
  const SettingsPage({super.key});

  /// قيم مقياس النص: صغير · متوسط · كبير · كبير جدًا.
  static const List<double> textScaleValues = [0.85, 1, 1.15, 1.3];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final settings = ref.watch(settingsProvider);
    final c = context.colors;

    return Scaffold(
      appBar: AppBar(title: Text(s.settingsTitle)),
      body: ListView(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.screenPaddingH,
          vertical: AppSpacing.sm,
        ),
        children: [
          // ── اللغة ───────────────────────────────────────
          _SectionLabel(title: s.language),
          AtharCard(
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
            child: Column(
              children: AppLocale.values.map((locale) {
                return AtharSettingsTile(
                  icon: Icons.translate,
                  title: locale.nativeName,
                  subtitle: locale.englishName,
                  onTap: () =>
                      ref.read(settingsProvider.notifier).setLocale(locale),
                  trailing: settings.locale == locale
                      ? Icon(Icons.check_circle, color: c.primary, size: 20)
                      : null,
                );
              }).toList(),
            ),
          ),

          const VSpace(AppSpacing.lg),

          // ── المظهر ──────────────────────────────────────
          _SectionLabel(title: s.theme),
          AtharCard(
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
            child: Column(
              children: [
                AtharSettingsTile(
                  icon: Icons.light_mode_outlined,
                  title: s.themeLight,
                  onTap: () => ref
                      .read(settingsProvider.notifier)
                      .setThemeMode(ThemeMode.light),
                  trailing: settings.themeMode == ThemeMode.light
                      ? Icon(Icons.check_circle, color: c.primary, size: 20)
                      : null,
                ),
                AtharSettingsTile(
                  icon: Icons.dark_mode_outlined,
                  title: s.themeDark,
                  onTap: () => ref
                      .read(settingsProvider.notifier)
                      .setThemeMode(ThemeMode.dark),
                  trailing: settings.themeMode == ThemeMode.dark
                      ? Icon(Icons.check_circle, color: c.primary, size: 20)
                      : null,
                ),
                AtharSettingsTile(
                  icon: Icons.brightness_auto_outlined,
                  title: s.themeAuto,
                  subtitle: s.systemDefault,
                  onTap: () => ref
                      .read(settingsProvider.notifier)
                      .setThemeMode(ThemeMode.system),
                  trailing: settings.themeMode == ThemeMode.system
                      ? Icon(Icons.check_circle, color: c.primary, size: 20)
                      : null,
                ),
              ],
            ),
          ),

          const VSpace(AppSpacing.lg),

          // ── حجم النص ────────────────────────────────────
          _SectionLabel(title: s.textSize),
          AtharCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Wrap(
                  spacing: AppSpacing.sm,
                  children: List<Widget>.generate(4, (index) {
                    final value = textScaleValues[index];
                    return AtharChip(
                      label: _textScaleLabel(s, index),
                      selected: settings.textScale == value,
                      icon: _textScaleIcon(index),
                      onTap: (_) => ref
                          .read(settingsProvider.notifier)
                          .setTextScale(value),
                    );
                  }),
                ),
                const VSpace(AppSpacing.md),
                Text(
                  '${s.fontSize}: ${ArabicNumbers.format(22, useArabicDigits: settings.useArabicDigits)}',
                  style: context.textStyles.bodySmall?.copyWith(
                    color: c.ink60,
                  ),
                ),
              ],
            ),
          ),

          const VSpace(AppSpacing.lg),

          // ── الأرقام العربية ─────────────────────────────
          AtharCard(
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.xs),
            child: SwitchListTile.adaptive(
              value: settings.useArabicDigits,
              onChanged: (value) => ref
                  .read(settingsProvider.notifier)
                  .setUseArabicDigits(value),
              title: Text(s.arabicDigits),
              subtitle: Text(
                ArabicNumbers.format(255, useArabicDigits: true),
                style: context.textStyles.bodySmall?.copyWith(
                  color: c.ink60,
                ),
              ),
              contentPadding:
                  const EdgeInsets.symmetric(horizontal: AppSpacing.lg),
            ),
          ),

          const VSpace(AppSpacing.lg),

          // ── حول ─────────────────────────────────────────
          _SectionLabel(title: s.about),
          AtharCard(
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
            child: Column(
              children: [
                AtharSettingsTile(
                  icon: Icons.menu_book_outlined,
                  title: s.sources,
                  subtitle: 'docs/05 · §3',
                  trailing: AtharBadge(label: s.comingSoon),
                ),
                AtharSettingsTile(
                  icon: Icons.lock_outline,
                  title: s.privacy,
                  subtitle: s.privacy,
                  trailing: AtharBadge(label: s.comingSoon),
                ),
              ],
            ),
          ),

          const VSpace(AppSpacing.xxl),
        ],
      ),
    );
  }

  IconData _textScaleIcon(int index) {
    switch (index) {
      case 0:
        return Icons.text_decrease;
      case 1:
        return Icons.text_fields;
      case 2:
        return Icons.text_increase;
      default:
        return Icons.format_size;
    }
  }

  String _textScaleLabel(Strings s, int index) {
    switch (index) {
      case 0:
        return s.sizeSmall;
      case 1:
        return s.sizeNormal;
      case 2:
        return s.sizeLarge;
      default:
        return s.sizeXlarge;
    }
  }
}

class _SectionLabel extends StatelessWidget {
  const _SectionLabel({required this.title});

  final String title;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(
        left: AppSpacing.xs,
        right: AppSpacing.xs,
        bottom: AppSpacing.sm,
        top: AppSpacing.md,
      ),
      child: Text(
        title,
        style: context.textStyles.titleMedium?.copyWith(
          fontSize: 13,
          fontWeight: FontWeight.w600,
          color: context.colors.ink70,
        ),
      ),
    );
  }
}

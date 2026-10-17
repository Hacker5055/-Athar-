import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../app/router.dart';
import '../../../../core/l10n/app_locale.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/widgets.dart';

/// تبويب «المزيد» — الأدوات الثانوية والإعدادات.
///
/// السبب: الشريط السفلي يفضل ٥ عناصر فقط (قاعدة الاستخدام بيد واحدة)،
/// فكل ما هو أقل استخدامًا بيتجمع هنا. راجع `docs/03` §1.
class MorePage extends ConsumerWidget {
  const MorePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final settings = ref.watch(settingsProvider);
    final c = context.colors;

    return Scaffold(
      appBar: AppBar(title: Text(s.moreTitle)),
      body: ListView(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.screenPaddingH,
          vertical: AppSpacing.sm,
        ),
        children: [
          // ── المظهر واللغة (أكتر إعدادين مطلوبين) ─────────
          AtharCard(
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppSpacing.lg,
                    vertical: AppSpacing.sm,
                  ),
                  child: Text(
                    s.appearance,
                    style: context.textStyles.titleMedium?.copyWith(
                      fontSize: 13,
                      color: c.ink70,
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.fromLTRB(
                    AppSpacing.lg,
                    0,
                    AppSpacing.lg,
                    AppSpacing.md,
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Wrap(
                        spacing: AppSpacing.sm,
                        children: AppLocale.values
                            .map(
                              (locale) => AtharChip(
                                label: locale.nativeName,
                                selected: settings.locale == locale,
                                onTap: (_) => ref
                                    .read(settingsProvider.notifier)
                                    .setLocale(locale),
                              ),
                            )
                            .toList(),
                      ),
                      const VSpace(AppSpacing.md),
                      Wrap(
                        spacing: AppSpacing.sm,
                        children: [
                          AtharChip(
                            label: s.themeLight,
                            icon: Icons.light_mode_outlined,
                            selected: settings.themeMode == ThemeMode.light,
                            onTap: (_) => _setTheme(ref, ThemeMode.light),
                          ),
                          AtharChip(
                            label: s.themeDark,
                            icon: Icons.dark_mode_outlined,
                            selected: settings.themeMode == ThemeMode.dark,
                            onTap: (_) => _setTheme(ref, ThemeMode.dark),
                          ),
                          AtharChip(
                            label: s.themeAuto,
                            icon: Icons.brightness_auto_outlined,
                            selected: settings.themeMode == ThemeMode.system,
                            onTap: (_) => _setTheme(ref, ThemeMode.system),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),

          const VSpace(AppSpacing.lg),

          // ── الأدوات ────────────────────────────────────
          AtharCard(
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
            child: Column(
              children: [
                AtharSettingsTile(
                  icon: Icons.settings_outlined,
                  title: s.settings,
                  subtitle: s.language,
                  onTap: () => context.push(Routes.settings),
                ),
                AtharSettingsTile(
                  icon: Icons.mosque_outlined,
                  title: s.prayerSettingsOpen,
                  subtitle: s.prayerMethod,
                  onTap: () => context.push(Routes.prayerSettings),
                ),
                AtharSettingsTile(
                  icon: Icons.palette_outlined,
                  title: s.designSystem,
                  subtitle: s.dsComponents,
                  onTap: () => context.push(Routes.designSystem),
                ),
                AtharSettingsTile(
                  icon: Icons.spa_outlined,
                  title: s.trackerTitle,
                  subtitle: s.trackerSub,
                  trailing: AtharBadge(label: s.comingSoon),
                ),
                AtharSettingsTile(
                  icon: Icons.loop,
                  title: s.tasbihTitle,
                  trailing: AtharBadge(label: s.comingSoon),
                ),
                AtharSettingsTile(
                  icon: Icons.calendar_month_outlined,
                  title: s.calendarTitle,
                  subtitle: s.hijri,
                  trailing: AtharBadge(label: s.comingSoon),
                ),
              ],
            ),
          ),

          const VSpace(AppSpacing.lg),

          AtharCard(
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
            child: Column(
              children: [
                AtharSettingsTile(
                  icon: Icons.info_outline,
                  title: s.about,
                  subtitle: s.tagline,
                  onTap: () => context.push(Routes.settings),
                ),
                AtharSettingsTile(
                  icon: Icons.share_outlined,
                  title: s.shareApp,
                  trailing: AtharBadge(label: s.comingSoon),
                ),
              ],
            ),
          ),

          const VSpace(AppSpacing.xl),
          Center(
            child: Text(
              s.tagline,
              style: context.textStyles.bodySmall?.copyWith(
                color: c.ink40,
                fontSize: 11,
              ),
            ),
          ),
          const VSpace(AppSpacing.xxl),
        ],
      ),
    );
  }

  void _setTheme(WidgetRef ref, ThemeMode mode) {
    ref.read(settingsProvider.notifier).setThemeMode(mode);
  }
}

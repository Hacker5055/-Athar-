import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../app/router.dart';
import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/greeting.dart';
import '../../../../core/utils/prayer_time_format.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../athary/presentation/widgets/athary_home_card.dart';
import '../../../prayer/application/prayer_providers.dart';
import '../../../prayer/presentation/prayer_icons.dart';
import '../../../prayer/presentation/widgets/next_prayer_card.dart';

/// الشاشة الرئيسية.
///
/// **أسبوع ٢:** المواقيت بقت **حقيقية** (حساب فلكي بـ `adhan_dart` +
/// الموقع + التوقيت المحلي عبر حزمة `timezone`).
///
/// ما زال placeholder: الورد القرآني والأذكار (أسبوع ٣) و«أثري» (أسبوع ٤).
class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final locale = ref.watch(currentLocaleProvider);
    final arabicDigits = ref.watch(useArabicDigitsProvider);
    final c = context.colors;
    final now = DateTime.now();

    return Scaffold(
      appBar: AppBar(
        toolbarHeight: 64,
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(greetingFor(now, s)),
            Text(
              AtharTimeFormat.longDate(
                now,
                locale: locale,
                useArabicDigits: arabicDigits,
              ),
              style: context.textStyles.bodySmall?.copyWith(
                color: c.ink60,
                fontSize: 11.5,
                fontWeight: FontWeight.w400,
              ),
            ),
          ],
        ),
        actions: [
          AtharIconButton(
            icon: Icons.tune_outlined,
            tooltip: s.prayerSettingsOpen,
            onPressed: () => context.push(Routes.prayerSettings),
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.screenPaddingH,
        ),
        children: [
          const VSpace(AppSpacing.sm),

          // ─── الصلاة القادمة (حيّة — عدّ تنازلي كل ثانية) ───
          NextPrayerCard(
            onTap: () => context.push(Routes.prayerSettings),
          ),

          SectionHeader(
            title: s.today,
            actionLabel: s.monthlyCalendar,
            onAction: () => _showComingSoon(context, s),
          ),
          const _PrayerGrid(),

          SectionHeader(title: s.quickActions),
          _QuickActions(onUnavailable: () => _showComingSoon(context, s)),

          SectionHeader(title: s.wirdTitle),
          AtharCard(
            child: Column(
              children: [
                WirdRow(
                  title: s.wirdTitle,
                  subtitle: s.wirdSub,
                  progress: 0.25,
                  ringLabel: '١/٤',
                  actionLabel: s.done,
                  onAction: () => _showComingSoon(context, s),
                ),
                Divider(color: c.border, height: 1),
                WirdRow(
                  title: s.adhkarToday,
                  subtitle: '${s.morning} · ${s.evening}',
                  progress: 0.4,
                  ringLabel: '٤٠٪',
                  accentColor: c.accent,
                  actionLabel: s.tabAdhkar,
                  onAction: () => context.go(Routes.adhkar),
                ),
              ],
            ),
          ),

          SectionHeader(
            title: s.trackerTitle,
            actionLabel: s.atharyOpen,
            onAction: () => context.push(Routes.athary),
          ),
          AtharyHomeCard(onOpen: () => context.push(Routes.athary)),

          const VSpace(AppSpacing.md),
          AtharCard(
            tinted: true,
            child: Row(
              children: [
                Icon(Icons.info_outline, size: 18, color: c.primary600),
                const HSpace(AppSpacing.md),
                Expanded(
                  child: Text(
                    s.homeWeek4Note,
                    style: context.textStyles.bodySmall?.copyWith(
                      color: c.primary600,
                      height: 1.7,
                    ),
                  ),
                ),
              ],
            ),
          ),
          const VSpace(AppSpacing.xxl),
        ],
      ),
    );
  }

  void _showComingSoon(BuildContext context, Strings s) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(s.comingSoon),
        duration: const Duration(seconds: 2),
      ),
    );
  }
}

/// شبكة مواقيت ٣×٢ — كل الوقت في شاشة واحدة بدون تمرير.
///
/// تبرز الصلاة الجاية وتقرأ الأوقات من [todayPrayerProvider]
/// بعد تحويلها للتوقيت المحلي للمكان.
class _PrayerGrid extends ConsumerWidget {
  const _PrayerGrid();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final arabicDigits = ref.watch(useArabicDigitsProvider);
    final c = context.colors;
    final day = ref.watch(todayPrayerProvider);
    final next = ref.watch(nextPrayerProvider);

    return GridView.count(
      crossAxisCount: 3,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      mainAxisSpacing: AppSpacing.sm,
      crossAxisSpacing: AppSpacing.sm,
      childAspectRatio: 1.35,
      children: [
        for (final prayer in prayerOrderWithSunrise)
          Builder(
            key: ValueKey(prayer),
            builder: (context) {
              final isSunrise = prayer == PrayerNameKey.sunrise;
              final isNext = prayer == next?.key && !isSunrise;
              final local = day == null ? null : localTimeFor(day, prayer);

              final bg = isNext ? c.primary : c.surface;
              final fg = isNext ? Colors.white : c.ink;

              return Container(
                decoration: BoxDecoration(
                  color: bg,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: isNext ? c.primary : c.border),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      prayer.icon,
                      size: 14,
                      color: isNext
                          ? Colors.white70
                          : (isSunrise ? c.ink40 : c.ink60),
                    ),
                    const VSpace(3),
                    Text(
                      prayer.label(s),
                      style: context.textStyles.bodySmall?.copyWith(
                        fontSize: 11.5,
                        color: isNext ? Colors.white70 : c.ink60,
                      ),
                    ),
                    const VSpace(3),
                    Text(
                      local == null
                          ? '--:--'
                          : AtharTimeFormat.hm12(
                              local,
                              s: s,
                              useArabicDigits: arabicDigits,
                            ),
                      style: context.textStyles.titleMedium?.copyWith(
                        fontSize: 14.5,
                        fontWeight: FontWeight.w600,
                        color: fg,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
              );
            },
          ),
      ],
    );
  }
}

/// شبكة الإجراءات السريعة — كلها في النصف السفلي (استخدام بيد واحدة).
class _QuickActions extends ConsumerWidget {
  const _QuickActions({required this.onUnavailable});

  final VoidCallback onUnavailable;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final c = context.colors;

    final items = [
      _QuickAction(
        icon: Icons.menu_book_outlined,
        label: s.qaQuran,
        route: Routes.quran,
      ),
      _QuickAction(
        icon: Icons.list_alt_outlined,
        label: s.qaAdhkar,
        route: Routes.adhkar,
      ),
      _QuickAction(
        icon: Icons.explore_outlined,
        label: s.qaQibla,
        route: Routes.qibla,
      ),
      _QuickAction(icon: Icons.loop, label: s.qaTasbih),
      _QuickAction(icon: Icons.auto_stories_outlined, label: s.qaDuas),
      _QuickAction(icon: Icons.grid_view_outlined, label: s.qaNames),
    ];

    return GridView.count(
      crossAxisCount: 3,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      mainAxisSpacing: AppSpacing.md,
      crossAxisSpacing: AppSpacing.md,
      childAspectRatio: 0.92,
      children: items.map((item) {
        return InkWell(
          onTap: () {
            if (item.route != null) {
              context.go(item.route!);
            } else {
              onUnavailable();
            }
          },
          borderRadius: BorderRadius.circular(18),
          child: Container(
            decoration: BoxDecoration(
              color: c.surface,
              borderRadius: BorderRadius.circular(18),
              border: Border.all(color: c.border),
            ),
            padding: const EdgeInsets.symmetric(vertical: AppSpacing.md),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  width: 42,
                  height: 42,
                  decoration: BoxDecoration(
                    color: c.primary100,
                    borderRadius: BorderRadius.circular(13),
                  ),
                  child: Icon(item.icon, size: 21, color: c.primary),
                ),
                const VSpace(AppSpacing.sm),
                Text(
                  item.label,
                  textAlign: TextAlign.center,
                  style: context.textStyles.bodySmall?.copyWith(
                    fontSize: 11,
                    color: c.ink70,
                    height: 1.4,
                  ),
                ),
              ],
            ),
          ),
        );
      }).toList(),
    );
  }
}

class _QuickAction {
  const _QuickAction({
    required this.icon,
    required this.label,
    this.route,
  });

  final IconData icon;
  final String label;
  final String? route;
}

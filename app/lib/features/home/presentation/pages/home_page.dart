import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../app/router.dart';
import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/greeting.dart';
import '../../../../core/widgets/widgets.dart';

/// الشاشة الرئيسية.
///
/// ⚠️ أسبوع ١: البطاقات دي **هياكل ثابتة** — البيانات الحقيقية
/// (المواقيت بالحساب الفلكي، آخر موضع قراءة، الختمة) بتيجي في أسبوع ٢ و٣.
/// التصميم والـ tokens نهائية ومطابقة للبروتوتايب.
class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
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
              MaterialLocalizations.of(context).formatMediumDate(now),
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
            icon: Icons.translate,
            tooltip: s.language,
            onPressed: () => context.push(Routes.settings),
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.screenPaddingH,
        ),
        children: [
          const VSpace(AppSpacing.sm),
          const _NextPrayerCard(),

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

          const VSpace(AppSpacing.md),
          AtharCard(
            tinted: true,
            child: Row(
              children: [
                Icon(Icons.info_outline, size: 18, color: c.primary600),
                const HSpace(AppSpacing.md),
                Expanded(
                  child: Text(
                    s.week1Note,
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

/// بطاقة الصلاة القادمة — أكبر عنصر في الشاشة (قرار تصميم موثّق في docs/04).
class _NextPrayerCard extends ConsumerWidget {
  const _NextPrayerCard();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final c = context.colors;

    return Container(
      margin: const EdgeInsets.only(bottom: AppSpacing.md),
      padding: const EdgeInsets.all(AppSpacing.xl),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [c.primary, c.primary600],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: AppRadius.cardRadius,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            s.nextPrayer,
            style: context.textStyles.bodySmall?.copyWith(
              color: const Color(0xCCFFFFFF),
              fontSize: 12,
            ),
          ),
          const VSpace(AppSpacing.xs),
          Text(
            s.maghrib,
            style: context.textStyles.displayLarge?.copyWith(
              color: Colors.white,
              fontSize: 26,
            ),
          ),
          const VSpace(AppSpacing.md),
          Text(
            '--:--:--',
            style: context.textStyles.displayLarge?.copyWith(
              color: Colors.white,
              fontSize: 40,
              fontWeight: FontWeight.w300,
              letterSpacing: 1,
            ),
          ),
          const VSpace(AppSpacing.xs),
          Row(
            children: [
              const Icon(
                Icons.location_on_outlined,
                size: 13,
                color: Color(0xC7FFFFFF),
              ),
              const HSpace(AppSpacing.xs),
              Text(
                s.comingWeek2,
                style: context.textStyles.bodySmall?.copyWith(
                  color: const Color(0xC7FFFFFF),
                  fontSize: 11.5,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

/// شبكة مواقيت ٣×٢ — كل الوقت في شاشة واحدة بدون تمرير.
class _PrayerGrid extends ConsumerWidget {
  const _PrayerGrid();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final c = context.colors;

    return GridView.count(
      crossAxisCount: 3,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      mainAxisSpacing: AppSpacing.sm,
      crossAxisSpacing: AppSpacing.sm,
      childAspectRatio: 1.35,
      children: prayerOrderWithSunrise.map((key) {
        final isMaghrib = key == PrayerNameKey.maghrib;
        return Container(
          decoration: BoxDecoration(
            color: isMaghrib ? c.primary : c.surface,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: isMaghrib ? c.primary : c.border),
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                key.label(s),
                style: context.textStyles.bodySmall?.copyWith(
                  fontSize: 11.5,
                  color: isMaghrib ? Colors.white70 : c.ink60,
                ),
              ),
              const VSpace(3),
              Text(
                '--:--',
                style: context.textStyles.titleMedium?.copyWith(
                  fontSize: 14.5,
                  fontWeight: FontWeight.w600,
                  color: isMaghrib ? Colors.white : c.ink,
                ),
              ),
            ],
          ),
        );
      }).toList(),
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

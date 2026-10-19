import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/l10n/app_locale.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/widgets.dart';

/// شاشة أول تشغيل — خطوة واحدة فقط: اختيار اللغة.
///
/// القرار المتعمّد: **لا نطلب أي إذن هنا**.
/// الموقع والإشعارات بتتطلب في سياقها الحقيقي (شاشة المواقيت /
/// الإشعارات)، ودي قاعدة موثّقة في `docs/03` §3.1.
class OnboardingPage extends ConsumerWidget {
  const OnboardingPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final c = context.colors;
    final currentLocale = ref.watch(currentLocaleProvider);

    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(AppSpacing.xl),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Spacer(),

              // ── الشعار ──────────────────────────────────
              Center(
                child: Container(
                  width: 88,
                  height: 88,
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      colors: [c.primary, c.primary600],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: BorderRadius.circular(26),
                  ),
                  child: const Center(
                    child: Text(
                      'أ',
                      style: TextStyle(
                        fontFamily: 'Amiri',
                        fontSize: 42,
                        fontWeight: FontWeight.w700,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ),
              ),
              const VSpace(AppSpacing.xxl),

              Text(
                s.onboardingTitle,
                textAlign: TextAlign.center,
                style: context.textStyles.displayLarge,
              ),
              const VSpace(AppSpacing.sm),
              Text(
                s.onboardingSubtitle,
                textAlign: TextAlign.center,
                style: context.textStyles.bodyMedium?.copyWith(
                  color: c.ink60,
                  height: 1.9,
                ),
              ),

              const Spacer(),

              // ── اختيار اللغة ────────────────────────────
              Text(
                s.chooseLanguage,
                style: context.textStyles.titleMedium?.copyWith(fontSize: 15),
              ),
              const VSpace(AppSpacing.md),
              ...AppLocale.values.map(
                (locale) => Padding(
                  padding: const EdgeInsets.only(bottom: AppSpacing.sm),
                  child: AtharCard(
                    onTap: () =>
                        ref.read(settingsProvider.notifier).setLocale(locale),
                    tinted: locale == currentLocale,
                    padding: const EdgeInsets.symmetric(
                      horizontal: AppSpacing.lg,
                      vertical: AppSpacing.md,
                    ),
                    child: Row(
                      children: [
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                locale.nativeName,
                                style: context.textStyles.bodyLarge?.copyWith(
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                              Text(
                                locale.englishName,
                                style: context.textStyles.bodySmall?.copyWith(
                                  color: c.ink60,
                                  fontSize: 11,
                                ),
                              ),
                            ],
                          ),
                        ),
                        if (locale == currentLocale)
                          Icon(Icons.check_circle, color: c.primary, size: 22),
                      ],
                    ),
                  ),
                ),
              ),

              const VSpace(AppSpacing.lg),

              AtharButton(
                label: s.start,
                expand: true,
                icon: Icons.arrow_forward,
                onPressed: () async {
                  await ref
                      .read(settingsProvider.notifier)
                      .completeOnboarding();
                },
              ),

              const VSpace(AppSpacing.md),
              Center(
                child: Text(
                  s.tagline,
                  style: context.textStyles.bodySmall?.copyWith(
                    color: c.ink40,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

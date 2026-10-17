import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../core/l10n/app_locale.dart';
import '../core/settings/settings_controller.dart';
import '../core/theme/app_theme.dart';
import 'router.dart';

/// جذر التطبيق.
///
/// كل حاجة ديناميكية (اللغة، الاتجاه، الثيم، مقياس النص) جاية من
/// `SettingsController` — فتغيير اللغة بيعيد بناء الشجرة بالـ RTL فورًا.
class AtharApp extends ConsumerWidget {
  const AtharApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final router = ref.watch(routerProvider);
    final locale = ref.watch(currentLocaleProvider);
    final themeMode = ref.watch(themeModeProvider);
    final textScale = ref.watch(textScaleProvider);

    return MaterialApp.router(
      title: 'أثر · Athar',
      debugShowCheckedModeBanner: false,
      routerConfig: router,

      // ── التدويل ────────────────────────────────────────
      locale: locale.locale,
      supportedLocales: AppLocale.supportedLocales,

      // ── الثيم ──────────────────────────────────────────
      theme: AppTheme.light(),
      darkTheme: AppTheme.dark(),
      themeMode: themeMode,

      // ── مقياس النص (إعداد المستخدم × إعداد النظام) ─────
      // نضرب مقياس النظام في اختيار المستخدم، ثم نقيّد الناتج بين
      // 0.85× و2× — لا يكسر التصميم صغيرًا، ولا يمنع التكبير كبيرًا.
      builder: (context, child) {
        final media = MediaQuery.of(context);
        final scaled = media.textScaler.scale(textScale).clamp(0.85, 2.0);
        return MediaQuery(
          data: media.copyWith(textScaler: TextScaler.linear(scaled.toDouble())),
          child: child ?? const SizedBox.shrink(),
        );
      },
    );
  }
}

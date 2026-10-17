import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../core/l10n/app_locale.dart';
import '../core/settings/settings_controller.dart';
import '../core/theme/app_theme.dart';
import '../features/prayer/application/notification_controller.dart';
import 'router.dart';

/// جذر التطبيق.
///
/// كل حاجة ديناميكية (اللغة، الاتجاه، الثيم، مقياس النص) جاية من
/// `SettingsController` — فتغيير اللغة بيعيد بناء الشجرة بالـ RTL فورًا.
///
/// **أسبوع ٣:** مسؤول كمان عن إعادة جدولة الإشعارات عند أول تشغيل وعند
/// رجوع التطبيق للمقدمة (لأن أندرويد يقدر يسحب صلاحية المنبّه الدقيق،
/// والسفر يغيّر المكان).
class AtharApp extends ConsumerStatefulWidget {
  const AtharApp({super.key});

  @override
  ConsumerState<AtharApp> createState() => _AtharAppState();
}

class _AtharAppState extends ConsumerState<AtharApp> with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _syncNotifications();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) _syncNotifications();
  }

  void _syncNotifications() {
    // لا نطلب أي شيء قبل ما يخلّص المستخدم أول تشغيل.
    if (!ref.read(settingsProvider).onboardingCompleted) return;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      ref.read(notificationStatusProvider.notifier).sync();
    });
  }

  @override
  Widget build(BuildContext context) {
    final router = ref.watch(routerProvider);

    // أول تشغيل خلص الآن ← نبدأ الجدولة (ما طلبناش إذن قبل كده).
    ref.listen(settingsProvider, (previous, next) {
      final wasDone = previous?.onboardingCompleted ?? true;
      if (!wasDone && next.onboardingCompleted) _syncNotifications();
    });

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

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../core/settings/settings_controller.dart';
import '../features/adhkar/presentation/pages/adhkar_page.dart';
import '../features/home/presentation/pages/home_page.dart';
import '../features/prayer/presentation/pages/city_picker_page.dart';
import '../features/athary/presentation/pages/athary_page.dart';
import '../features/prayer/presentation/pages/notifications_page.dart';
import '../features/prayer/presentation/pages/prayer_settings_page.dart';
import '../features/more/presentation/pages/design_system_page.dart';
import '../features/more/presentation/pages/more_page.dart';
import '../features/onboarding/presentation/pages/onboarding_page.dart';
import '../features/qibla/presentation/pages/qibla_page.dart';
import '../features/quran/presentation/pages/quran_page.dart';
import '../features/quran/presentation/pages/quran_search_page.dart';
import '../features/quran/presentation/pages/reader_page.dart';
import '../features/settings/presentation/pages/settings_page.dart';
import '../features/shell/app_shell.dart';

/// أسماء المسارات — مكان واحد بدل سلاسل نصية مبعثرة.
abstract final class Routes {
  static const onboarding = '/onboarding';
  static const home = '/home';
  static const quran = '/quran';
  static const adhkar = '/adhkar';
  static const qibla = '/qibla';
  static const more = '/more';
  static const settings = '/settings';
  static const designSystem = '/design-system';
  static const prayerSettings = '/prayer-settings';
  static const cityPicker = '/city-picker';
  static const notifications = '/notifications';
  static const athary = '/athary';
  static const surah = '/quran/surah/:id';
  static const juz = '/quran/juz/:number';
  static const mushafPage = '/quran/page/:number';
  static const quranSearch = '/quran/search';

  /// يبني مسار سورة برقمها.
  static String surahOf(int id) => '/quran/surah/$id';
  static String juzOf(int n) => '/quran/juz/$n';
  static String pageOf(int n) => '/quran/page/$n';
}

final routerProvider = Provider<GoRouter>((ref) {
  // إعادة بناء الراوتر عند إكمال أول تشغيل (بيحصل مرة واحدة).
  final onboardingCompleted = ref.watch(settingsProvider).onboardingCompleted;

  return GoRouter(
    initialLocation: onboardingCompleted ? Routes.home : Routes.onboarding,
    debugLogDiagnostics: false,
    redirect: (context, state) {
      final atOnboarding = state.matchedLocation == Routes.onboarding;
      if (!onboardingCompleted && !atOnboarding) return Routes.onboarding;
      if (onboardingCompleted && atOnboarding) return Routes.home;
      return null;
    },
    routes: [
      GoRoute(
        path: Routes.onboarding,
        name: 'onboarding',
        builder: (context, state) => const OnboardingPage(),
      ),
      StatefulShellRoute.indexedStack(
        builder: (context, state, navigationShell) =>
            AppShell(navigationShell: navigationShell),
        branches: [
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: Routes.home,
                name: 'home',
                builder: (context, state) => const HomePage(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: Routes.quran,
                name: 'quran',
                builder: (context, state) => const QuranPage(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: Routes.adhkar,
                name: 'adhkar',
                builder: (context, state) => const AdhkarPage(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: Routes.qibla,
                name: 'qibla',
                builder: (context, state) => const QiblaPage(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: Routes.more,
                name: 'more',
                builder: (context, state) => const MorePage(),
              ),
            ],
          ),
        ],
      ),
      GoRoute(
        path: Routes.settings,
        name: 'settings',
        builder: (context, state) => const SettingsPage(),
      ),
      GoRoute(
        path: Routes.designSystem,
        name: 'design-system',
        builder: (context, state) => const DesignSystemPage(),
      ),
      GoRoute(
        path: Routes.prayerSettings,
        name: 'prayer-settings',
        builder: (context, state) => const PrayerSettingsPage(),
      ),
      GoRoute(
        path: Routes.cityPicker,
        name: 'city-picker',
        builder: (context, state) => const CityPickerPage(),
      ),
      GoRoute(
        path: Routes.notifications,
        name: 'notifications',
        builder: (context, state) => const NotificationsPage(),
      ),
      GoRoute(
        path: Routes.athary,
        name: 'athary',
        builder: (context, state) => const AtharyPage(),
      ),

      // ── القرآن (أسبوع ٦) ──────────────────────────────
      GoRoute(
        path: Routes.surah,
        name: 'surah',
        builder: (context, state) => ReaderPage(
          args: ReaderArgs.surah(int.parse(state.pathParameters['id']!)),
        ),
      ),
      GoRoute(
        path: Routes.juz,
        name: 'juz',
        builder: (context, state) => ReaderPage(
          args: ReaderArgs.juz(int.parse(state.pathParameters['number']!)),
        ),
      ),
      GoRoute(
        path: Routes.mushafPage,
        name: 'mushaf-page',
        builder: (context, state) => ReaderPage(
          args: ReaderArgs.page(int.parse(state.pathParameters['number']!)),
        ),
      ),
      GoRoute(
        path: Routes.quranSearch,
        name: 'quran-search',
        builder: (context, state) => const QuranSearchPage(),
      ),
    ],
  );
});

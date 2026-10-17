import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../core/l10n/app_locale.dart';
import '../core/settings/settings_controller.dart';
import 'app.dart';

/// نقطة دخول موحّدة — كل إعداد قبل `runApp` يتعمل هنا.
Future<void> bootstrap() async {
  WidgetsFlutterBinding.ensureInitialized();

  // TODO(week-4): اربط بـ Sentry مع تعطيل إرسال أي نص قرآني.
  FlutterError.onError = (details) {
    FlutterError.presentError(details);
  };

  final prefs = await SharedPreferences.getInstance();
  final initialSettings = SettingsController.initial(
    prefs,
    // نحترم لغة الجهاز في أول تشغيل فقط.
    PlatformDispatcher.instance.locales,
  );

  runApp(
    ProviderScope(
      overrides: [
        sharedPreferencesProvider.overrideWithValue(prefs),
        settingsProvider.overrideWith(
          (ref) => SettingsController(prefs, initialSettings),
        ),
      ],
      child: const AtharApp(),
    ),
  );
}

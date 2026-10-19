import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

import '../../../core/theme/quran_theme.dart';
import '../domain/quran_settings.dart';

/// يقرأ ويكتب تفضيلات القراءة في `SharedPreferences`.
///
/// **لماذا JSON كامل بدل مفاتيح منفردة؟**
/// الإعدادات تُقرأ كوحدة واحدة دائمًا. تخزينها ككائن واحد يجعل
/// «الكل أو لا شيء» طبيعيًا: لو فسد جزء، نرجع للافتراضي كاملًا بدل
/// حالة هجين (حجم خط محفوظ + ثيم مفقود).
class QuranSettingsRepository {
  const QuranSettingsRepository(this._prefs);

  final SharedPreferences _prefs;

  static const String storageKey = 'athar.quran.v1';

  QuranSettings load() {
    final raw = _prefs.getString(storageKey);
    if (raw == null || raw.isEmpty) return const QuranSettings();
    try {
      final map = jsonDecode(raw);
      if (map is! Map) return const QuranSettings();
      return _fromMap(map);
    } catch (_) {
      // ملف تالف → الافتراضي. لا نرمي استثناءً: الإعدادات ليست حرجة
      // بما يكفي لإعاقة تشغيل التطبيق.
      return const QuranSettings();
    }
  }

  Future<void> save(QuranSettings value) =>
      _prefs.setString(storageKey, jsonEncode(_toMap(value)));

  static QuranSettings _fromMap(Map<Object?, Object?> map) {
    const defaults = QuranSettings();
    final scale = (map['fontScale'] as num?)?.toDouble();
    final themeName = map['pageTheme'] as String?;
    return QuranSettings(
      fontScale: scale == null
          ? defaults.fontScale
          : defaults.clampScale(scale),
      pageTheme: QuranPageTheme.values.firstWhere(
        (t) => t.name == themeName,
        orElse: () => defaults.pageTheme,
      ),
      showTranslation: map['showTranslation'] == true ||
          (map['showTranslation'] == null && defaults.showTranslation),
      edition: map['edition'] as String?,
    );
  }

  static Map<String, Object?> _toMap(QuranSettings value) => {
        'fontScale': value.fontScale,
        'pageTheme': value.pageTheme.name,
        'showTranslation': value.showTranslation,
        if (value.edition != null) 'edition': value.edition,
      };
}

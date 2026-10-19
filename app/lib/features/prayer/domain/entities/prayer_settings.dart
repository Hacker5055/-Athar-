import 'dart:convert';

import '../../../../core/utils/greeting.dart' show PrayerNameKey;
import '../prayer_methods.dart' show HighLatitudeRuleId, MadhabId;
import 'place.dart';

/// من يحدّد المكان: الـ GPS ولا مدينة مختارة يدويًا؟
enum LocationMode { auto, manual }

/// إعدادات الصلاة — تُحفظ كـ JSON مستقل عن `AppSettings` تحت مفتاح
/// `athar.prayer.v1` (لأنها أكبر بكثير وتتغيّر بمعدل مختلف).
class PrayerSettings {
  const PrayerSettings({
    this.methodId = PrayerSettings.defaultMethodId,
    this.madhab = MadhabId.shafi,
    this.highLatitudeRule = HighLatitudeRuleId.none,
    this.adjustments = const <PrayerNameKey, int>{},
    this.locationMode = LocationMode.manual,
    this.manualCityId = PrayerSettings.defaultCityId,
    this.lastKnown,
  });

  static const String defaultMethodId = 'egyptian';
  static const String defaultCityId = 'cairo';

  final String methodId;
  final MadhabId madhab;
  final HighLatitudeRuleId highLatitudeRule;

  /// تعديل يدوي بالدقائق لكل صلاة (A4) — موجب = تأخير، سالب = تقديم.
  final Map<PrayerNameKey, int> adjustments;

  final LocationMode locationMode;

  /// معرّف المدينة المختارة يدويًا (من `Cities.all`).
  final String manualCityId;

  /// آخر إحداثيات معروفة من الـ GPS — تُستخدم كـ fallback أوفلاين،
  /// وتخلّي التطبيق يعرض مواقيت صح حتى من غير صلاحية الموقع.
  final Place? lastKnown;

  int adjustmentFor(PrayerNameKey key) => adjustments[key] ?? 0;

  /// هل هناك أي تعديل يدوي مطبّق؟ (الواجهة تعرض تنبيهًا للمستخدم).
  bool get hasAdjustments =>
      adjustments.values.any((minutes) => minutes != 0);

  PrayerSettings copyWith({
    String? methodId,
    MadhabId? madhab,
    HighLatitudeRuleId? highLatitudeRule,
    Map<PrayerNameKey, int>? adjustments,
    LocationMode? locationMode,
    String? manualCityId,
    Place? lastKnown,
    bool clearLastKnown = false,
  }) =>
      PrayerSettings(
        methodId: methodId ?? this.methodId,
        madhab: madhab ?? this.madhab,
        highLatitudeRule: highLatitudeRule ?? this.highLatitudeRule,
        adjustments: adjustments ?? this.adjustments,
        locationMode: locationMode ?? this.locationMode,
        manualCityId: manualCityId ?? this.manualCityId,
        lastKnown: clearLastKnown ? null : (lastKnown ?? this.lastKnown),
      );

  PrayerSettings copyWithAdjustment(PrayerNameKey key, int minutes) {
    final next = Map<PrayerNameKey, int>.from(adjustments);
    if (minutes == 0) {
      next.remove(key);
    } else {
      next[key] = minutes;
    }
    return copyWith(adjustments: next);
  }

  // ── الترميز ────────────────────────────────────────────
  static const int _version = 1;

  String encode() => jsonEncode({
        'v': _version,
        'method': methodId,
        'madhab': madhab.name,
        'hlr': highLatitudeRule.name,
        'adj': {
          for (final e in adjustments.entries)
            if (e.value != 0) e.key.name: e.value,
        },
        'loc': locationMode.name,
        'city': manualCityId,
        if (lastKnown != null) 'last': lastKnown!.toJson(),
      });

  /// يتحمل أي بيانات تالفة: يرجّع الافتراضي بدل ما يوقّع التطبيق.
  static PrayerSettings decode(String? raw) {
    if (raw == null || raw.isEmpty) return const PrayerSettings();
    Object? data;
    try {
      data = jsonDecode(raw);
    } catch (_) {
      return const PrayerSettings();
    }
    if (data is! Map) return const PrayerSettings();

    final adjRaw = data['adj'];
    final adj = <PrayerNameKey, int>{};
    if (adjRaw is Map) {
      for (final entry in adjRaw.entries) {
        final key = PrayerNameKey.values.asNameMap()[entry.key];
        final minutes = entry.value;
        if (key != null && minutes is int && minutes != 0) {
          adj[key] = minutes.clamp(-60, 60);
        }
      }
    }

    final madhab = MadhabId.values.asNameMap()[data['madhab']];
    final hlr = HighLatitudeRuleId.values.asNameMap()[data['hlr']];
    final loc = LocationMode.values.asNameMap()[data['loc']];
    final city = data['city'];

    return PrayerSettings(
      methodId: (data['method'] is String)
          ? data['method'] as String
          : defaultMethodId,
      madhab: madhab ?? MadhabId.shafi,
      highLatitudeRule: hlr ?? HighLatitudeRuleId.none,
      adjustments: adj,
      locationMode: loc ?? LocationMode.manual,
      manualCityId: (city is String && city.isNotEmpty)
          ? city
          : defaultCityId,
      lastKnown: Place.fromJson(data['last']),
    );
  }
}

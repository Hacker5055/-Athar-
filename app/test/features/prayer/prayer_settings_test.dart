import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/l10n/app_locale.dart';
import 'package:athar/core/utils/greeting.dart';
import 'package:athar/features/prayer/domain/entities/place.dart';
import 'package:athar/features/prayer/domain/entities/prayer_settings.dart';
import 'package:athar/features/prayer/domain/prayer_methods.dart';

void main() {
  group('PrayerSettings', () {
    test('الافتراضي: مصرية · شافعي · بلا قاعدة خطوط عرض · القاهرة', () {
      const settings = PrayerSettings();
      expect(settings.methodId, 'egyptian');
      expect(settings.madhab, MadhabId.shafi);
      expect(settings.highLatitudeRule, HighLatitudeRuleId.none);
      expect(settings.locationMode, LocationMode.manual);
      expect(settings.manualCityId, 'cairo');
      expect(settings.adjustments, isEmpty);
      expect(settings.hasAdjustments, isFalse);
    });

    test('ترميز وفك ترميز يرجّع نفس الكائن', () {
      const settings = PrayerSettings(
        methodId: 'umm_alqura',
        madhab: MadhabId.hanafi,
        highLatitudeRule: HighLatitudeRuleId.seventhOfTheNight,
        adjustments: {
          PrayerNameKey.fajr: -5,
          PrayerNameKey.isha: 10,
        },
        locationMode: LocationMode.auto,
        manualCityId: 'makkah',
        lastKnown: Place(
          id: 'auto',
          latitude: 21.4225,
          longitude: 39.8262,
          timeZoneId: 'Asia/Riyadh',
          nameAr: 'موقعي الحالي',
          nameEn: 'My current location',
          nameFr: 'Ma position actuelle',
          isAuto: true,
        ),
      );

      final decoded = PrayerSettings.decode(settings.encode());

      expect(decoded.methodId, settings.methodId);
      expect(decoded.madhab, settings.madhab);
      expect(decoded.highLatitudeRule, settings.highLatitudeRule);
      expect(decoded.adjustments, settings.adjustments);
      expect(decoded.locationMode, settings.locationMode);
      expect(decoded.lastKnown?.latitude, 21.4225);
      expect(decoded.lastKnown?.timeZoneId, 'Asia/Riyadh');
    });

    test('JSON بايظ يرجّع الافتراضي بدل ما يوقّع التطبيق', () {
      for (final raw in [
        null,
        '',
        '{not json',
        '"just a string"',
        '[]',
        '{}',
      ]) {
        final settings = PrayerSettings.decode(raw);
        expect(settings.methodId, PrayerSettings.defaultMethodId,
            reason: 'raw=$raw');
        expect(settings.madhab, MadhabId.shafi, reason: 'raw=$raw');
      }
    });

    test('قيم غريبة تُتجاهل فرديًا بدل ما تبوّظ الكل', () {
      final settings = PrayerSettings.decode(
        '{"method":123,"madhab":"زيطة","hlr":"none","adj":{"nonexistent":9},'
        '"loc":"","city":45}',
      );
      expect(settings.methodId, PrayerSettings.defaultMethodId);
      expect(settings.madhab, MadhabId.shafi);
      expect(settings.highLatitudeRule, HighLatitudeRuleId.none);
      expect(settings.adjustments, isEmpty);
      expect(settings.locationMode, LocationMode.manual);
      expect(settings.manualCityId, PrayerSettings.defaultCityId);
    });

    test('التعديلات تتقيّد بين -60 و+60', () {
      final settings = PrayerSettings.decode(
        '{"adj":{"fajr":900,"dhuhr":-900,"asr":0}}',
      );
      expect(settings.adjustmentFor(PrayerNameKey.fajr), 60);
      expect(settings.adjustmentFor(PrayerNameKey.dhuhr), -60);
      expect(settings.adjustmentFor(PrayerNameKey.asr), 0);
      expect(settings.hasAdjustments, isTrue);
    });

    test('copyWithAdjustment: الصفر يمسح المفتاح', () {
      var settings = const PrayerSettings()
          .copyWithAdjustment(PrayerNameKey.fajr, 7);
      expect(settings.adjustmentFor(PrayerNameKey.fajr), 7);

      settings = settings.copyWithAdjustment(PrayerNameKey.fajr, 0);
      expect(settings.adjustmentFor(PrayerNameKey.fajr), 0);
      expect(settings.adjustments.containsKey(PrayerNameKey.fajr), isFalse);
    });

    test('clearLastKnown يمسح الموقع المحفوظ', () {
      const settings = PrayerSettings(
        locationMode: LocationMode.auto,
        lastKnown: Place.fallback,
      );
      expect(settings.lastKnown, isNotNull);
      expect(settings.copyWith(clearLastKnown: true).lastKnown, isNull);
    });
  });

  group('PrayerMethods', () {
    test('كل الطرق لها معرّف فريد وأسماء بالثلاث لغات', () {
      final ids = <String>{};
      for (final method in PrayerMethods.all) {
        expect(ids.add(method.id), isTrue,
            reason: 'معرّف مكرر: ${method.id}');
        expect(method.nameAr.trim(), isNotEmpty, reason: method.id);
        expect(method.nameEn.trim(), isNotEmpty, reason: method.id);
        expect(method.nameFr.trim(), isNotEmpty, reason: method.id);
      }
    });

    test('كل طريقة إما زاوية عشاء أو فاصل دقائق — مش الاتنين', () {
      for (final method in PrayerMethods.all) {
        if (method.ishaInterval > 0) {
          expect(method.ishaAngle, 0,
              reason: '${method.id}: فاصل + زاوية معًا');
        } else {
          expect(method.ishaAngle, greaterThan(0),
              reason: '${method.id}: لا فاصل ولا زاوية');
        }
      }
    });

    test('byId يرجع المصرية لأي معرّف مجهول', () {
      expect(PrayerMethods.byId('unknown').id, 'egyptian');
      expect(PrayerMethods.byId(null).id, 'egyptian');
      expect(PrayerMethods.byId('mwl').id, 'mwl');
    });

    test('كل المذاهب وقواعد خطوط العرض لها أسماء بالثلاث لغات', () {
      for (final locale in AppLocale.values) {
        for (final madhab in MadhabId.values) {
          expect(madhab.nameFor(locale).trim(), isNotEmpty);
        }
        for (final rule in HighLatitudeRuleId.values) {
          expect(rule.nameFor(locale).trim(), isNotEmpty);
        }
      }
    });
  });
}

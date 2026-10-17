import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/l10n/app_locale.dart';
import 'package:athar/features/prayer/data/cities.dart';
import 'package:athar/features/prayer/domain/entities/place.dart';

void main() {
  group('Cities', () {
    test('كل الصفوف تتحلّل ومفيش بيانات ناقصة', () {
      expect(Cities.all, isNotEmpty);
      final ids = <String>{};
      for (final city in Cities.all) {
        expect(ids.add(city.id), isTrue, reason: 'معرّف مكرر: ${city.id}');
        expect(city.latitude, inInclusiveRange(-90, 90), reason: city.id);
        expect(city.longitude, inInclusiveRange(-180, 180), reason: city.id);
        expect(city.timeZoneId, contains('/'), reason: city.id);
        expect(city.nameAr.trim(), isNotEmpty, reason: city.id);
        expect(city.nameEn.trim(), isNotEmpty, reason: city.id);
        expect(city.nameFr.trim(), isNotEmpty, reason: city.id);
      }
    });

    test('القاهرة موجودة كافتراضي', () {
      expect(Cities.byId('cairo'), isNotNull);
      expect(Cities.byId('غير_موجود'), isNull);
      expect(Cities.byId(null), isNull);
    });

    test('البحث بالعربية يتجاهل التشكيل والتاء المربوطة', () {
      expect(Cities.search('القاهرة').map((c) => c.id), contains('cairo'));
      expect(Cities.search('القاهره').map((c) => c.id), contains('cairo'));
      expect(Cities.search('القـاهرة').map((c) => c.id), contains('cairo'));
    });

    test('البحث يشتغل بالإنجليزية وبالفرنسية', () {
      expect(Cities.search('cairo').map((c) => c.id), contains('cairo'));
      expect(Cities.search('CAIRO').map((c) => c.id), contains('cairo'));
      expect(Cities.search('Le Caire').map((c) => c.id), contains('cairo'));
      expect(Cities.search('Lyon').map((c) => c.id), contains('lyon'));
      expect(Cities.search('lyon').map((c) => c.id), contains('lyon'));
    });

    test('بحث فاضي يرجّع كل المدن (محدودة بالحد)', () {
      expect(Cities.search('').length, lessThanOrEqualTo(40));
      expect(Cities.search('   ').length, lessThanOrEqualTo(40));
    });

    test('بحث مفيش منه نتائج يرجّع قائمة فاضية', () {
      expect(Cities.search('zzzqqq'), isEmpty);
    });

    test('أقرب مدينة لإحداثيات القاهرة هي القاهرة', () {
      final nearest = Cities.nearestTo(Place.fallback.point);
      expect(nearest, isNotNull);
      expect(nearest!.id, 'cairo');
    });

    test('المسافة والاتجاه: القاهرة → مكة', () {
      final cairo = Place.fallback.point;
      final makkah = Cities.byId('makkah')!.point;

      final km = cairo.distanceKmTo(makkah);
      // المسافة الفعلية ~١٢٠٠ كم
      expect(km, inInclusiveRange(1150, 1300));

      // زاوية القبلة من القاهرة ≈ ١٣٦° (من الشمال الحقيقي)
      final bearing = cairo.bearingToDeg(makkah);
      expect(bearing, inInclusiveRange(130, 142));
    });
  });

  group('Place', () {
    test('الاسم يتغيّر حسب اللغة', () {
      const place = Place.fallback;
      expect(place.nameFor(AppLocale.ar), 'القاهرة');
      expect(place.nameFor(AppLocale.en), 'Cairo');
      expect(place.nameFor(AppLocale.fr), 'Le Caire');
    });

    test('المكان التلقائي يستخدم «موقعي الحالي» بكل اللغات', () {
      final auto = Place.auto(
        latitude: 30.0,
        longitude: 31.0,
        timeZoneId: 'Africa/Cairo',
      );
      expect(auto.isAuto, isTrue);
      expect(auto.nameFor(AppLocale.ar), contains('موقعي'));
      expect(auto.nameFor(AppLocale.en), contains('current'));
      expect(auto.nameFor(AppLocale.fr).trim(), isNotEmpty);
    });

    test('fromJson يتحمّل البيانات الناقصة', () {
      expect(Place.fromJson(null), isNull);
      expect(Place.fromJson('not a map'), isNull);
      expect(Place.fromJson({'lat': 1}), isNull); // ناقص lng/tz
      expect(Place.fromJson({'lat': 1, 'lng': 2}), isNull); // ناقص tz
      expect(
        Place.fromJson({'lat': 'x', 'lng': 2, 'tz': 'Africa/Cairo'}),
        isNull,
      ); // lat نص
    });

    test('ترميز وفك ترميز', () {
      final json = Place.fallback.toJson();
      final back = Place.fromJson(json);
      expect(back, isNotNull);
      expect(back!.id, Place.fallback.id);
      expect(back.latitude, Place.fallback.latitude);
      expect(back.timeZoneId, Place.fallback.timeZoneId);
    });
  });
}

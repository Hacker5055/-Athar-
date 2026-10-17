import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/l10n/app_locale.dart';
import 'package:athar/core/l10n/bundles/strings_ar.dart';
import 'package:athar/core/l10n/bundles/strings_en.dart';
import 'package:athar/core/l10n/bundles/strings_fr.dart';
import 'package:athar/core/l10n/strings.dart';

void main() {
  group('Strings', () {
    test('كل الحزم فيها نفس المفاتيح بالظبط', () {
      final ar = kStringsAr.keys.toSet();
      final en = kStringsEn.keys.toSet();
      final fr = kStringsFr.keys.toSet();

      expect(en.difference(ar), isEmpty,
          reason: 'مفاتيح موجودة في الإنجليزية وناقصة في العربية');
      expect(fr.difference(ar), isEmpty,
          reason: 'مفاتيح موجودة في الفرنسية وناقصة في العربية');
      expect(ar.difference(en), isEmpty,
          reason: 'مفاتيح ناقصة في الإنجليزية');
      expect(ar.difference(fr), isEmpty,
          reason: 'مفاتيح ناقصة في الفرنسية');
    });

    test('مفيش قيمة فاضية في أي حزمة', () {
      for (final bundle in [
        (name: 'ar', data: kStringsAr),
        (name: 'en', data: kStringsEn),
        (name: 'fr', data: kStringsFr),
      ]) {
        for (final entry in bundle.data.entries) {
          expect(entry.value.trim(), isNotEmpty,
              reason: '${bundle.name}.${entry.key} فاضي');
        }
      }
    });

    test('الاتجاه: العربية RTL والباقي LTR', () {
      expect(AppLocale.ar.isRtl, isTrue);
      expect(AppLocale.en.isRtl, isFalse);
      expect(AppLocale.fr.isRtl, isFalse);
    });

    test('fromCode يرجع العربية لأي كود غير مدعوم', () {
      expect(AppLocale.fromCode('ar'), AppLocale.ar);
      expect(AppLocale.fromCode('fr'), AppLocale.fr);
      expect(AppLocale.fromCode('de'), AppLocale.ar);
      expect(AppLocale.fromCode(null), AppLocale.ar);
    });

    test('fromPlatformLocales يختار أول لغة مدعومة', () {
      expect(
        AppLocale.fromPlatformLocales(const [Locale('de'), Locale('fr')]),
        AppLocale.fr,
      );
      expect(
        AppLocale.fromPlatformLocales(const [Locale('ar')]),
        AppLocale.ar,
      );
      expect(
        AppLocale.fromPlatformLocales(const [Locale('tr')]),
        AppLocale.ar,
      );
    });

    test('الترجمة ترجع للعربية عند نقص مفتاح', () {
      final english = Strings(AppLocale.en);
      expect(english.tabHome, 'Home');
      // مفتاح مش موجود خالص → يرجع المفتاح نفسه (سهل اكتشافه في المراجعة)
      expect(english.t('key_that_does_not_exist'), 'key_that_does_not_exist');
    });

    test('tArgs يستبدل المتغيّرات', () {
      final s = Strings(AppLocale.ar);
      expect(s.tArgs('x {n}', {'n': 5}), 'x 5');
      expect(
        s.tArgs('{a} / {b}', {'a': 1, 'b': 4}),
        '1 / 4',
      );
    });

    test('كل اللغات ترجّع اسم التطبيق', () {
      for (final locale in AppLocale.values) {
        expect(Strings(locale).appName, isNotEmpty);
      }
    });
  });
}

import 'package:flutter_test/flutter_test.dart';

import 'package:athar/features/quran/data/audio/quran_audio_source.dart';
import 'package:athar/features/quran/data/audio/reciter_catalog.dart';
import 'package:athar/features/quran/domain/audio/audio_license.dart';
import 'package:athar/features/quran/domain/audio/reciter.dart';

/// عدد آيات كل سورة (حفص) — من المصدر بعد التحقق: المجموع ٦٢٣٦.
const List<int> kCounts = <int>[
  7, 286, 200, 176, 120, 165, 206, 75, 129, 109, 123, 111, 43, 52, 99, 128, //
  111, 110, 98, 135, 112, 78, 118, 64, 77, 227, 93, 88, 69, 60, 34, 30, 73, //
  54, 45, 83, 182, 88, 75, 85, 54, 53, 89, 59, 37, 35, 38, 29, 18, 45, 60, //
  49, 62, 55, 78, 96, 29, 22, 24, 13, 14, 11, 11, 18, 12, 12, 30, 52, 52, //
  44, 28, 28, 20, 56, 40, 31, 50, 40, 46, 42, 29, 19, 36, 25, 22, 17, 19, //
  26, 30, 20, 15, 21, 11, 8, 8, 19, 5, 8, 8, 11, 11, 8, 3, 9, 5, 4, 7, 3, //
  6, 3, 5, 4, 5, 6, //
];

void main() {
  group('QuranAyahIndex — الرقم العالمي', () {
    test('المصدر: ١١٤ سورة ومجموعها ٦٢٣٦', () {
      expect(kCounts.length, 114);
      expect(kCounts.reduce((a, b) => a + b), 6236);
    });

    test('بدايات معروفة', () {
      expect(QuranAyahIndex.globalOf(1, 1, kCounts), 1);
      expect(QuranAyahIndex.globalOf(1, 7, kCounts), 7);
      expect(QuranAyahIndex.globalOf(2, 1, kCounts), 8);
      expect(QuranAyahIndex.globalOf(114, 6, kCounts), 6236);
    });

    test('آية الكرسي ٢:٢٥٥ → ٢٦٢', () {
      expect(QuranAyahIndex.globalOf(2, 255, kCounts), 262);
    });

    test('العكس يرجع نفس الموضع', () {
      for (final (surah, ayah) in [
        (1, 1),
        (1, 7),
        (2, 1),
        (2, 255),
        (9, 1),
        (18, 10),
        (78, 1),
        (114, 6),
      ]) {
        final global = QuranAyahIndex.globalOf(surah, ayah, kCounts);
        final back = QuranAyahIndex.fromGlobal(global, kCounts);
        expect(back.surah, surah, reason: '$global');
        expect(back.ayah, ayah, reason: '$global');
      }
    });

    test('⚠️ تناظر كامل على كل الـ٦٢٣٦ — لا تكرار ولا فراغ', () {
      final seen = <int>{};
      for (var s = 1; s <= 114; s++) {
        for (var a = 1; a <= kCounts[s - 1]; a++) {
          seen.add(QuranAyahIndex.globalOf(s, a, kCounts));
        }
      }
      expect(seen.length, 6236);
      expect(seen.reduce((a, b) => a > b ? a : b), 6236);
      expect(seen.reduce((a, b) => a < b ? a : b), 1);
    });

    test('خارج النطاق → خطأ صريح لا رقم خاطئ', () {
      expect(() => QuranAyahIndex.globalOf(0, 1, kCounts), throwsArgumentError);
      expect(
        () => QuranAyahIndex.globalOf(115, 1, kCounts),
        throwsArgumentError,
      );
      expect(
        () => QuranAyahIndex.fromGlobal(0, kCounts),
        throwsArgumentError,
      );
      expect(
        () => QuranAyahIndex.fromGlobal(6237, kCounts),
        throwsArgumentError,
      );
    });
  });

  group('QuranAudioSource — أنماط العناوين', () {
    test('ملف آية: الرقم العالمي لا {سورة}{آية}', () {
      expect(
        QuranAudioSource.ayahUrl(reciterId: 'ar.alafasy', globalAyah: 262),
        'https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3',
      );
    });

    test('⚠️ مسار السورة مختلف (audio-surah)', () {
      expect(
        QuranAudioSource.surahUrl(reciterId: 'ar.alafasy', surah: 114),
        'https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/114.mp3',
      );
      expect(
        QuranAudioSource.surahUrl(reciterId: 'ar.alafasy', surah: 1),
        contains('/quran/audio-surah/'),
      );
    });

    test('معدّل البِتّ يتغيّر في المسار', () {
      expect(
        QuranAudioSource.ayahUrl(
          reciterId: 'ar.alafasy',
          globalAyah: 1,
          bitrate: 64,
        ),
        endsWith('/64/ar.alafasy/1.mp3'),
      );
    });

    test('اسم الملف المحلي ثابت', () {
      expect(
        QuranAudioSource.localFileName(
          reciterId: 'ar.alafasy',
          globalAyah: 262,
        ),
        'ar.alafasy_262.mp3',
      );
    });
  });

  group('AudioLicensePolicy — الحارس', () {
    const granted = Reciter(
      id: 'x.granted',
      nameAr: 'ا',
      nameEn: 'A',
      sourceName: 's',
      license: 'l',
      licenseUrl: 'u',
      status: AudioLicenseStatus.granted,
    );
    const conditional = Reciter(
      id: 'x.conditional',
      nameAr: 'ب',
      nameEn: 'B',
      sourceName: 's',
      license: 'l',
      licenseUrl: 'u',
      status: AudioLicenseStatus.conditional,
    );
    const unknown = Reciter(
      id: 'x.unknown',
      nameAr: 'ج',
      nameEn: 'C',
      sourceName: 's',
      license: 'l',
      licenseUrl: 'u',
      status: AudioLicenseStatus.unknown,
    );
    const incomplete = Reciter(
      id: 'x.incomplete',
      nameAr: 'د',
      nameEn: 'D',
      sourceName: 's',
      license: 'l',
      licenseUrl: 'u',
      status: AudioLicenseStatus.granted,
      ayahCount: 6235,
    );

    test('granted يُعرض', () {
      expect(AudioLicensePolicy.canOffer(granted), isTrue);
    });

    test('conditional يُعرض لأن التطبيق غير تجاري', () {
      expect(AudioLicensePolicy.appIsNonCommercial, isTrue);
      expect(AudioLicensePolicy.canOffer(conditional), isTrue);
    });

    test('⛔ unknown لا يُعرض — عدم ذكر الرخصة ليس إذنًا', () {
      expect(AudioLicensePolicy.canOffer(unknown), isFalse);
    });

    test('⛔ prohibited لا يُعرض', () {
      expect(
        AudioLicensePolicy.canOffer(
          unknown.copyWith(status: AudioLicenseStatus.prohibited),
        ),
        isFalse,
      );
    });

    test('⛔ غير المكتمل (٦٢٣٥) لا يُعرض — وإلا صمت التطبيق عند آية', () {
      expect(AudioLicensePolicy.canOffer(incomplete), isFalse);
    });

    test('⛔ المنسحب لا يُعرض', () {
      final withdrawn = AudioLicensePolicy.applyTakedown(granted);
      expect(
        AudioLicensePolicy.canOffer(
          granted.copyWith(status: AudioLicenseStatus.withdrawn),
        ),
        isFalse,
      );
      expect(withdrawn.status, granted.status, reason: 'ليس في قائمة الانسحاب');
    });

    test('التنزيل يحتاج معدّل بِتّ متاح عند المصدر', () {
      const narrow = Reciter(
        id: 'x.narrow',
        nameAr: 'ه',
        nameEn: 'E',
        sourceName: 's',
        license: 'l',
        licenseUrl: 'u',
        status: AudioLicenseStatus.granted,
        bitrates: <int>[128],
      );
      expect(AudioLicensePolicy.canDownload(narrow, bitrate: 128), isTrue);
      expect(AudioLicensePolicy.canDownload(narrow, bitrate: 320), isFalse);
    });

    test('offerable يفلتر', () {
      final result = AudioLicensePolicy.offerable(<Reciter>[
        granted,
        conditional,
        unknown,
        incomplete,
      ]);
      expect(result.map((r) => r.id),
          <String>['x.granted', 'x.conditional']);
    });
  });

  group('الكتالوج الفعلي', () {
    test('كل قرّاء الكتالوج قابلون للعرض', () {
      for (final reciter in kReciterCatalog) {
        expect(
          AudioLicensePolicy.canOffer(reciter),
          isTrue,
          reason: '${reciter.id} لا يحقق السياسة',
        );
      }
    });

    test('لا معرّف مكرّر', () {
      final ids = kReciterCatalog.map((r) => r.id).toList();
      expect(ids.toSet().length, ids.length);
    });

    test('⚖️ كل قارئ له رخصة ورابط ونص نسبة', () {
      for (final reciter in kReciterCatalog) {
        expect(reciter.license.isEmpty, isFalse);
        expect(reciter.licenseUrl.startsWith('https://'), isTrue);
        expect(reciter.sourceName.isEmpty, isFalse);
        expect(reciter.attributionFor('ar'), contains(reciter.nameAr));
        expect(reciter.attributionFor('en'), contains(reciter.nameEn));
        expect(reciter.attributionFor('fr'), isNotEmpty);
      }
    });

    test('كلهم مكتملون (٦٢٣٦) ومعدّل ١٢٨ متاح', () {
      for (final reciter in kReciterCatalog) {
        expect(reciter.isComplete, isTrue);
        expect(reciter.bitrates.contains(128), isTrue);
      }
    });

    test('المستبعدون عمدًا ليسوا في الكتالوج', () {
      final ids = kReciterCatalog.map((r) => r.id).toSet();
      for (final excluded in <String>[
        'ar.husary', // ٦٢٣٥ — ينقصه آية
        'ar.minshawi', // ٦٢٣٥
        'zh.chinese', // ترجمة لا تلاوة
        'ru.kuliev-audio', // ترجمة لا تلاوة
      ]) {
        expect(ids.contains(excluded), isFalse, reason: excluded);
      }
    });

    test('offerableReciters يطبّق السياسة', () {
      expect(offerableReciters, isNotEmpty);
      for (final reciter in offerableReciters) {
        expect(AudioLicensePolicy.canOffer(reciter), isTrue);
      }
    });
  });
}

extension on Reciter {
  Reciter copyWith({AudioLicenseStatus? status}) => Reciter(
        id: id,
        nameAr: nameAr,
        nameEn: nameEn,
        sourceName: sourceName,
        license: license,
        licenseUrl: licenseUrl,
        status: status ?? this.status,
        ayahCount: ayahCount,
        bitrates: bitrates,
        notes: notes,
      );
}

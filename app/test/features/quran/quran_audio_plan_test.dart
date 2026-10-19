import 'package:flutter_test/flutter_test.dart';

import 'package:athar/features/quran/domain/audio/audio_track.dart';
import 'package:athar/features/quran/domain/audio/quran_audio_plan.dart';
import 'package:athar/features/quran/domain/audio/reciter.dart';

/// عدد آيات كل سورة (حفص) — المجموع ٦٢٣٦ (مصدر متحقق).
const List<int> kCounts = <int>[
  7, 286, 200, 176, 120, 165, 206, 75, 129, 109, 123, 111, 43, 52, 99, 128, //
  111, 110, 98, 135, 112, 78, 118, 64, 77, 227, 93, 88, 69, 60, 34, 30, 73, //
  54, 45, 83, 182, 88, 75, 85, 54, 53, 89, 59, 37, 35, 38, 29, 18, 45, 60, //
  49, 62, 55, 78, 96, 29, 22, 24, 13, 14, 11, 11, 18, 12, 12, 30, 52, 52, //
  44, 28, 28, 20, 56, 40, 31, 50, 40, 46, 42, 29, 19, 36, 25, 22, 17, 19, //
  26, 30, 20, 15, 21, 11, 8, 8, 19, 5, 8, 8, 11, 11, 8, 3, 9, 5, 4, 7, 3, //
  6, 3, 5, 4, 5, 6, //
];

const Reciter kReciter = Reciter(
  id: 'ar.alafasy',
  nameAr: 'مشاري العفاسي',
  nameEn: 'Alafasy',
  sourceName: 'Islamic Network CDN',
  license: 'غير تجاري — الحقوق للقارئ',
  licenseUrl: 'https://alquran.cloud/terms-and-conditions',
  status: AudioLicenseStatus.conditional,
  bitrates: <int>[32, 40, 48, 64, 128, 192, 320],
);

void main() {
  group('QuranAudioPlanner.surah', () {
    test('الفاتحة: ٧ مسارات من ١ إلى ٧', () {
      final plan = QuranAudioPlanner.surah(
        reciter: kReciter,
        surah: 1,
        surahAyahCounts: kCounts,
      );
      expect(plan.length, 7);
      expect(plan.tracks.map((t) => t.globalAyah),
          <int>[1, 2, 3, 4, 5, 6, 7]);
      expect(plan.tracks.first.surah, 1);
      expect(plan.tracks.first.ayah, 1);
    });

    test('⚠️ البقرة تبدأ عند ٨ لا ١ — أول اختبار يكشف انزياحًا كاملًا', () {
      final plan = QuranAudioPlanner.surah(
        reciter: kReciter,
        surah: 2,
        surahAyahCounts: kCounts,
      );
      expect(plan.length, 286);
      expect(plan.tracks.first.globalAyah, 8);
      expect(plan.tracks.last.globalAyah, 293);
      expect(plan.tracks.last.ayah, 286);
    });

    test('الناس: آخر مسار هو ٦٢٣٦', () {
      final plan = QuranAudioPlanner.surah(
        reciter: kReciter,
        surah: 114,
        surahAyahCounts: kCounts,
      );
      expect(plan.length, 6);
      expect(plan.tracks.last.globalAyah, 6236);
    });

    test('العنوان يتركّب من اسم السورة ورقم الآية', () {
      final plan = QuranAudioPlanner.surah(
        reciter: kReciter,
        surah: 1,
        surahAyahCounts: kCounts,
        surahNames: <int, String>{1: 'الفاتحة'},
      );
      expect(plan.tracks.first.title, 'الفاتحة 1');
      expect(plan.tracks.last.title, 'الفاتحة 7');
    });

    test('بدون أسماء سور: العنوان فارغ لا مكسور', () {
      final plan = QuranAudioPlanner.surah(
        reciter: kReciter,
        surah: 1,
        surahAyahCounts: kCounts,
      );
      expect(plan.tracks.first.title, isNull);
    });
  });

  group('QuranAudioPlanner.singleAyah', () {
    test('⚠️ آية الكرسي ٢:٢٥٥ → ٢٦٢', () {
      final plan = QuranAudioPlanner.singleAyah(
        reciter: kReciter,
        surah: 2,
        ayah: 255,
        surahAyahCounts: kCounts,
      );
      expect(plan.length, 1);
      expect(plan.tracks.single.globalAyah, 262);
      expect(
        plan.tracks.single.remoteUrl,
        'https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3',
      );
      expect(
        plan.tracks.single.localFileName,
        'ar.alafasy_262.mp3',
      );
    });
  });

  group('QuranAudioPlanner.byAyahRange', () {
    test('نطاق شامل الطرفين', () {
      final plan = QuranAudioPlanner.byAyahRange(
        reciter: kReciter,
        fromGlobalAyah: 10,
        toGlobalAyah: 12,
        surahAyahCounts: kCounts,
      );
      expect(plan.tracks.map((t) => t.globalAyah), <int>[10, 11, 12]);
    });

    test('⚠️ كل مسار يحمل رقم السورة والآية الصحيحين لا العالمي فقط', () {
      final plan = QuranAudioPlanner.byAyahRange(
        reciter: kReciter,
        fromGlobalAyah: 7,
        toGlobalAyah: 9,
        surahAyahCounts: kCounts,
      );
      // ٧ = آخر الفاتحة، ٨ و٩ = أول آيتين من البقرة
      expect(plan.tracks[0].surah, 1);
      expect(plan.tracks[0].ayah, 7);
      expect(plan.tracks[1].surah, 2);
      expect(plan.tracks[1].ayah, 1);
      expect(plan.tracks[2].surah, 2);
      expect(plan.tracks[2].ayah, 2);
    });

    test('معدّل البِتّ يظهر في كل رابط', () {
      final plan = QuranAudioPlanner.byAyahRange(
        reciter: kReciter,
        fromGlobalAyah: 1,
        toGlobalAyah: 2,
        surahAyahCounts: kCounts,
        bitrate: 64,
      );
      for (final track in plan.tracks) {
        expect(track.remoteUrl, contains('/64/'));
      }
    });

    test('خارج النطاق أو نطاق مقلوب → خطأ صريح', () {
      expect(
        () => QuranAudioPlanner.byAyahRange(
          reciter: kReciter,
          fromGlobalAyah: 0,
          toGlobalAyah: 5,
          surahAyahCounts: kCounts,
        ),
        throwsArgumentError,
      );
      expect(
        () => QuranAudioPlanner.byAyahRange(
          reciter: kReciter,
          fromGlobalAyah: 1,
          toGlobalAyah: 6237,
          surahAyahCounts: kCounts,
        ),
        throwsArgumentError,
      );
      expect(
        () => QuranAudioPlanner.byAyahRange(
          reciter: kReciter,
          fromGlobalAyah: 20,
          toGlobalAyah: 10,
          surahAyahCounts: kCounts,
        ),
        throwsArgumentError,
      );
    });
  });

  group('QuranAudioPlanner.surahFile — الوضع الآخر', () {
    test('⚠️ مسار مختلف تمامًا (audio-surah)', () {
      final plan = QuranAudioPlanner.surahFile(
        reciter: kReciter,
        surah: 2,
      );
      expect(plan.length, 1);
      expect(plan.mode, QuranAudioMode.surahFile);
      expect(
        plan.tracks.single.remoteUrl,
        'https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/2.mp3',
      );
      expect(plan.tracks.single.localFileName, 'ar.alafasy_surah_2.mp3');
    });
  });

  group('تقدير الحجم', () {
    test('البقرة ≈ ٤٥.٧٦ ميجابايت', () {
      final plan = QuranAudioPlanner.surah(
        reciter: kReciter,
        surah: 2,
        surahAyahCounts: kCounts,
      );
      expect(plan.estimatedBytes, 286 * 10 * 128000 ~/ 8);
      expect(plan.estimatedBytes, 45760000);
    });

    test('⚠️ القرآن كاملًا ≈ ٩٥٢ ميجابايت — سبب التحذير في الواجهة', () {
      final plan = QuranAudioPlanner.byAyahRange(
        reciter: kReciter,
        fromGlobalAyah: 1,
        toGlobalAyah: 6236,
        surahAyahCounts: kCounts,
      );
      expect(plan.estimatedBytes, 997760000);
      expect(plan.estimatedBytes, greaterThan(900 * 1000 * 1000));
    });

    test('٦٤ كيلوبِت نصف حجم ١٢٨', () {
      final at128 = QuranAudioPlanner.surah(
        reciter: kReciter,
        surah: 2,
        surahAyahCounts: kCounts,
        bitrate: 128,
      );
      final at64 = QuranAudioPlanner.surah(
        reciter: kReciter,
        surah: 2,
        surahAyahCounts: kCounts,
        bitrate: 64,
      );
      // ~/ يقتطع الكسر؛ نقارن بالتقريب
      expect((at64.estimatedBytes * 2 - at128.estimatedBytes).abs(),
          lessThanOrEqualTo(1));
    });
  });

  group('AudioTrack', () {
    test('المساواة بالرقم العالمي واسم الملف', () {
      const a = AudioTrack(
        globalAyah: 1,
        surah: 1,
        ayah: 1,
        remoteUrl: 'u',
        localFileName: 'f',
      );
      const b = AudioTrack(
        globalAyah: 1,
        surah: 1,
        ayah: 1,
        remoteUrl: 'u2',
        localFileName: 'f',
      );
      const c = AudioTrack(
        globalAyah: 2,
        surah: 1,
        ayah: 2,
        remoteUrl: 'u',
        localFileName: 'f',
      );
      expect(a, b, reason: 'نفس الآية ونفس الملف');
      expect(a == c, isFalse);
      expect(a.hashCode, b.hashCode);
    });
  });
}

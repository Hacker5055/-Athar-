import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/l10n/app_locale.dart';
import 'package:athar/core/utils/greeting.dart' show PrayerNameKey, prayerOrder;
import 'package:athar/features/prayer/domain/notification_settings.dart';

void main() {
  group('PrayerAlertMode', () {
    test('off هو الوضع الوحيد المعطّل', () {
      for (final mode in PrayerAlertMode.values) {
        expect(mode.isEnabled, mode != PrayerAlertMode.off);
      }
    });

    test('hasSound / hasVibration', () {
      expect(PrayerAlertMode.adhan.hasSound, isTrue);
      expect(PrayerAlertMode.adhan.hasVibration, isTrue);
      expect(PrayerAlertMode.beep.hasSound, isTrue);
      expect(PrayerAlertMode.beep.hasVibration, isFalse);
      expect(PrayerAlertMode.vibrate.hasSound, isFalse);
      expect(PrayerAlertMode.vibrate.hasVibration, isTrue);
      expect(PrayerAlertMode.silent.hasSound, isFalse);
      expect(PrayerAlertMode.silent.hasVibration, isFalse);
    });

    test('الأذان فقط هو المحتاج ملفًا صوتيًا مرفقًا', () {
      expect(PrayerAlertMode.adhan.needsBundledAudio, isTrue);
      expect(PrayerAlertMode.shortAdhan.needsBundledAudio, isTrue);
      expect(PrayerAlertMode.beep.needsBundledAudio, isFalse);
      expect(PrayerAlertMode.vibrate.needsBundledAudio, isFalse);
      expect(PrayerAlertMode.silent.needsBundledAudio, isFalse);
    });
  });

  group('AdhanSounds — ⚠️ لا ملفات صوتية في المستودع', () {
    test('كل الأصوات غير مرفقة (bundled = false)', () {
      for (final sound in AdhanSounds.all) {
        expect(
          sound.bundled,
          isFalse,
          reason: '${sound.id}: لا يجوز إرفاق ملف صوتي قبل التحقق '
              'من الترخيص (راجع docs/05 §3.3)',
        );
      }
    });

    test('لا صوت قابل للتشغيل من الإشعار الآن', () {
      for (final sound in AdhanSounds.all) {
        expect(sound.canPlayFromNotification, isFalse);
      }
    });

    test('المعرّفات فريدة', () {
      expect(
        AdhanSounds.all.map((s) => s.id).toSet().length,
        AdhanSounds.all.length,
      );
    });

    test('byId يسقط على صوت النظام', () {
      expect(AdhanSounds.byId('makkah').id, 'makkah');
      expect(AdhanSounds.byId('غير-موجود').id, AdhanSoundOption.systemId);
      expect(AdhanSounds.byId(null).id, AdhanSoundOption.systemId);
    });

    test('لكل صوت اسم بالثلاث لغات', () {
      for (final sound in AdhanSounds.all) {
        for (final locale in AppLocale.values) {
          expect(sound.nameFor(locale).isNotEmpty, isTrue);
        }
      }
    });

    test('الصوت الافتراضي هو نغمة النظام', () {
      expect(
        const NotificationSettings().adhanSoundId,
        AdhanSoundOption.systemId,
      );
      expect(const NotificationSettings().sound.isSystemSound, isTrue);
    });
  });

  group('NotificationSettings — الافتراضيات', () {
    test('الافتراضي: أذان لكل صلاة + تذكير جمعة ٩٠', () {
      const settings = NotificationSettings();
      expect(settings.masterEnabled, isTrue);
      expect(settings.jumuahReminder, isTrue);
      expect(settings.jumuahMinutesBefore, 90);
      for (final prayer in prayerOrder) {
        expect(settings.modeFor(prayer), PrayerAlertMode.adhan);
        expect(settings.reminderFor(prayer), 0);
      }
    });

    test('الشروق مقفول دائمًا (مش صلاة)', () {
      expect(
        const NotificationSettings().modeFor(PrayerNameKey.sunrise),
        PrayerAlertMode.off,
      );
    });
  });

  group('NotificationSettings — الترميز', () {
    test('دورة كاملة: encode → decode', () {
      final settings = NotificationSettings(
        masterEnabled: false,
        modes: const {
          PrayerNameKey.fajr: PrayerAlertMode.vibrate,
          PrayerNameKey.isha: PrayerAlertMode.off,
        },
        reminderMinutes: const {
          PrayerNameKey.fajr: 15,
          PrayerNameKey.maghrib: 30,
        },
        jumuahReminder: false,
        jumuahMinutesBefore: 120,
        adhanSoundId: 'makkah',
      );
      final decoded = NotificationSettings.decode(settings.encode());

      expect(decoded.masterEnabled, isFalse);
      expect(decoded.modeFor(PrayerNameKey.fajr), PrayerAlertMode.vibrate);
      expect(decoded.modeFor(PrayerNameKey.isha), PrayerAlertMode.off);
      expect(decoded.modeFor(PrayerNameKey.dhuhr), PrayerAlertMode.adhan);
      expect(decoded.reminderFor(PrayerNameKey.fajr), 15);
      expect(decoded.reminderFor(PrayerNameKey.maghrib), 30);
      expect(decoded.reminderFor(PrayerNameKey.asr), 0);
      expect(decoded.jumuahReminder, isFalse);
      expect(decoded.jumuahMinutesBefore, 120);
      expect(decoded.adhanSoundId, 'makkah');
    });

    test('يقبل التالف ويرجّع الافتراضي', () {
      for (final bad in ['', 'not json', '[]', 'null', '{"on": "yes"}']) {
        final decoded = NotificationSettings.decode(bad);
        expect(decoded.masterEnabled, isTrue, reason: bad);
        expect(decoded.jumuahMinutesBefore, 90, reason: bad);
      }
      expect(NotificationSettings.decode(null), const NotificationSettings());
    });

    test('يتجاهل القيم غير المعروفة', () {
      final decoded = NotificationSettings.decode(
        '{"on":true,"modes":{"fajr":"غلط","asr":"silent"},'
        '"rem":{"fajr":999,"dhuhr":10},"jumMin":5,"sound":42}',
      );
      expect(decoded.modeFor(PrayerNameKey.fajr), PrayerAlertMode.adhan);
      expect(decoded.modeFor(PrayerNameKey.asr), PrayerAlertMode.silent);
      expect(decoded.reminderFor(PrayerNameKey.fajr), 0, reason: '٩٩٩ مش خيار');
      expect(decoded.reminderFor(PrayerNameKey.dhuhr), 10);
      expect(decoded.jumuahMinutesBefore, 30, reason: '٥ تُقيَّد لأدنى حد');
      expect(decoded.adhanSoundId, AdhanSoundOption.systemId);
    });

    test('jumuahMinutesBefore يُقيَّد بين ٣٠ و٢٤٠', () {
      expect(NotificationSettings.decode('{"jumMin":5}').jumuahMinutesBefore, 30);
      expect(
        NotificationSettings.decode('{"jumMin":9999}').jumuahMinutesBefore,
        240,
      );
      expect(
        NotificationSettings.decode('{"jumMin":90}').jumuahMinutesBefore,
        90,
      );
    });

    test('لا يُخزّن تذكيرًا بقيمته صفر', () {
      final encoded = NotificationSettings(
        reminderMinutes: const {PrayerNameKey.fajr: 0},
      ).encode();
      expect(encoded.contains('"rem":{"fajr":0}'), isFalse);
    });
  });

  group('NotificationSettings — copyWith', () {
    test('copyWithMode يعدّل صلاة واحدة فقط', () {
      final next = const NotificationSettings().copyWithMode(
        PrayerNameKey.asr,
        PrayerAlertMode.silent,
      );
      expect(next.modeFor(PrayerNameKey.asr), PrayerAlertMode.silent);
      expect(next.modeFor(PrayerNameKey.dhuhr), PrayerAlertMode.adhan);
    });

    test('copyWithReminder يحذف الصفر بدل تخزينه', () {
      final withReminder = const NotificationSettings().copyWithReminder(
        PrayerNameKey.fajr,
        15,
      );
      expect(withReminder.reminderFor(PrayerNameKey.fajr), 15);

      final cleared = withReminder.copyWithReminder(PrayerNameKey.fajr, 0);
      expect(cleared.reminderFor(PrayerNameKey.fajr), 0);
      expect(cleared.reminderMinutes.containsKey(PrayerNameKey.fajr), isFalse);
    });

    test('copyWithReminder يرفض قيمة خارج الخيارات', () {
      final next = const NotificationSettings().copyWithReminder(
        PrayerNameKey.fajr,
        7,
      );
      expect(next.reminderFor(PrayerNameKey.fajr), 0);
    });

    test('كل خيارات التذكير قابلة للتخزين', () {
      for (final choice in NotificationSettings.reminderChoices) {
        final next = const NotificationSettings().copyWithReminder(
          PrayerNameKey.dhuhr,
          choice,
        );
        expect(next.reminderFor(PrayerNameKey.dhuhr), choice);
      }
    });

    test('setModeForAll لا يلمس الشروق', () {
      const controller = NotificationSettings();
      final all = controller.copyWith(
        modes: {
          for (final prayer in PrayerNameKey.values)
            if (prayer != PrayerNameKey.sunrise)
              prayer: PrayerAlertMode.vibrate,
        },
      );
      expect(all.modeFor(PrayerNameKey.sunrise), PrayerAlertMode.off);
      expect(all.modeFor(PrayerNameKey.isha), PrayerAlertMode.vibrate);
    });
  });

  group('NotificationSettings — الأصوات المفقودة', () {
    test('audioUnavailable = true لأن الملفات غير مرفقة', () {
      expect(const NotificationSettings().audioUnavailable, isTrue);
    });

    test('audioUnavailable = false لو كل الصلوات صامتة', () {
      final silent = const NotificationSettings().copyWith(
        modes: {
          for (final prayer in prayerOrder) prayer: PrayerAlertMode.silent,
        },
      );
      expect(silent.wantsBundledAudio, isFalse);
      expect(silent.audioUnavailable, isFalse);
    });

    test('audioUnavailable = false مع المفتاح الرئيسي مقفولًا', () {
      expect(
        const NotificationSettings(masterEnabled: false).audioUnavailable,
        isFalse,
      );
    });
  });
}

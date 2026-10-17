import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/utils/greeting.dart';
import '../../../../core/widgets/widgets.dart';
import '../../application/notification_controller.dart';
import '../../application/notification_settings_controller.dart';
import '../../domain/notification_settings.dart';

/// شاشة الإشعارات والأذان (أسبوع ٣).
///
/// تغطّي: A8 أذان لكل صلاة · A9 اختيار الصوت · A10 تذكير قبل الصلاة ·
/// A13 تذكير الجمعة.
class NotificationsPage extends ConsumerStatefulWidget {
  const NotificationsPage({super.key});

  @override
  ConsumerState<NotificationsPage> createState() => _NotificationsPageState();
}

class _NotificationsPageState extends ConsumerState<NotificationsPage> {
  @override
  void initState() {
    super.initState();
    // نتأكد إن الحالة (الصلاحيات + عدد المجدول) محدّثة عند فتح الشاشة.
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      ref.read(notificationStatusProvider.notifier).sync();
    });
  }

  /// بعد أي تغيير إعداد: نحفظ ثم نعيد الجدولة فورًا.
  Future<void> _resync() =>
      ref.read(notificationStatusProvider.notifier).sync();

  @override
  Widget build(BuildContext context) {
    final s = ref.watch(stringsProvider);
    final locale = ref.watch(currentLocaleProvider);
    final arabicDigits = ref.watch(useArabicDigitsProvider);
    final settings = ref.watch(notificationSettingsProvider);
    final status = ref.watch(notificationStatusProvider);
    final c = context.colors;

    return Scaffold(
      appBar: AppBar(title: Text(s.notifications)),
      body: ListView(
        padding: const EdgeInsets.all(AppSpacing.lg),
        children: [
          // ─── المفتاح الرئيسي ───────────────────────────
          AtharCard(
            child: Row(
              children: [
                Container(
                  width: 34,
                  height: 34,
                  decoration: BoxDecoration(
                    color: c.primary100,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Icon(
                    Icons.notifications_active_outlined,
                    size: 18,
                    color: c.primary,
                  ),
                ),
                const SizedBox(width: AppSpacing.md),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Text(
                        s.notificationsMaster,
                        style: context.textStyles.titleMedium,
                      ),
                      const SizedBox(height: 2),
                      Text(
                        s.notificationsMasterDesc,
                        style: context.textStyles.bodySmall?.copyWith(
                          color: c.ink60,
                        ),
                      ),
                    ],
                  ),
                ),
                Switch(
                  value: settings.masterEnabled,
                  onChanged: (value) async {
                    await ref
                        .read(notificationSettingsProvider.notifier)
                        .setMasterEnabled(value);
                    await _resync();
                  },
                ),
              ],
            ),
          ),

          if (!settings.masterEnabled) ...[
            const SizedBox(height: AppSpacing.md),
            _Hint(icon: Icons.info_outline, text: s.notificationsOffHint),
          ],

          // ─── صلاحيات النظام ────────────────────────────
          if (settings.masterEnabled && status.hasPermissionIssue) ...[
            const SizedBox(height: AppSpacing.md),
            _ActionBanner(
              icon: Icons.notifications_off_outlined,
              text: s.notificationsPermissionNeeded,
              actionLabel: s.notificationsPermissionAction,
              onPressed: () async {
                await ref
                    .read(notificationStatusProvider.notifier)
                    .requestPermission();
                await _resync();
              },
            ),
          ],

          if (settings.masterEnabled &&
              status.permissions != null &&
              status.permissions!.needsInexactFallback) ...[
            const SizedBox(height: AppSpacing.md),
            _ActionBanner(
              icon: Icons.alarm_outlined,
              text: s.notificationsExactNeededShort,
              actionLabel: s.notificationsExactAction,
              onPressed: () async {
                await ref
                    .read(notificationStatusProvider.notifier)
                    .requestExactAlarms();
                await _resync();
              },
            ),
          ],

          const SizedBox(height: AppSpacing.lg),

          // ─── الأذان لكل صلاة ──────────────────────────
          Opacity(
            opacity: settings.masterEnabled ? 1 : 0.45,
            child: IgnorePointer(
              ignoring: !settings.masterEnabled,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  SectionHeader(title: s.notificationsPerPrayer),
                  const SizedBox(height: AppSpacing.sm),
                  Text(
                    s.notificationsPerPrayerHint,
                    style: context.textStyles.bodySmall
                        ?.copyWith(color: c.ink60),
                  ),
                  const SizedBox(height: AppSpacing.md),
                  AtharCard(
                    child: Column(
                      children: [
                        for (final prayer in _athanPrayers) ...[
                          if (prayer != _athanPrayers.first)
                            Divider(color: c.border, height: 1),
                          AtharSettingsTile(
                            icon: prayer.icon,
                            title: prayer.label(s),
                            value: _modeLabel(settings.modeFor(prayer), s),
                            onTap: () => _pickMode(context, ref, prayer),
                          ),
                        ],
                      ],
                    ),
                  ),

                  const SizedBox(height: AppSpacing.lg),

                  // ─── التذكير قبل الأذان ──────────────────
                  SectionHeader(title: s.notificationsReminder),
                  const SizedBox(height: AppSpacing.sm),
                  Text(
                    s.notificationsReminderDesc,
                    style:
                        context.textStyles.bodySmall?.copyWith(color: c.ink60),
                  ),
                  const SizedBox(height: AppSpacing.md),
                  AtharCard(
                    child: Column(
                      children: [
                        for (final prayer in _athanPrayers) ...[
                          if (prayer != _athanPrayers.first)
                            Divider(color: c.border, height: 1),
                          _ReminderRow(
                            prayer: prayer,
                            label: prayer.label(s),
                            minutes: settings.reminderFor(prayer),
                            arabicDigits: arabicDigits,
                            s: s,
                            onChanged: (minutes) async {
                              await ref
                                  .read(notificationSettingsProvider.notifier)
                                  .setReminder(prayer, minutes);
                              await _resync();
                            },
                          ),
                        ],
                      ],
                    ),
                  ),

                  const SizedBox(height: AppSpacing.lg),

                  // ─── تذكير الجمعة ────────────────────────
                  SectionHeader(title: s.notificationsJumuah),
                  const SizedBox(height: AppSpacing.sm),
                  AtharCard(
                    child: Column(
                      children: [
                        Row(
                          children: [
                            Container(
                              width: 34,
                              height: 34,
                              decoration: BoxDecoration(
                                color: c.primary100,
                                borderRadius: BorderRadius.circular(10),
                              ),
                              child: Icon(
                                Icons.mosque_outlined,
                                size: 18,
                                color: c.primary,
                              ),
                            ),
                            const SizedBox(width: AppSpacing.md),
                            Expanded(
                              child: Text(
                                s.notificationsJumuahDesc,
                                style: context.textStyles.bodySmall
                                    ?.copyWith(color: c.ink60),
                              ),
                            ),
                            Switch(
                              value: settings.jumuahReminder,
                              onChanged: (value) async {
                                await ref
                                    .read(
                                      notificationSettingsProvider.notifier,
                                    )
                                    .setJumuahReminder(value);
                                await _resync();
                              },
                            ),
                          ],
                        ),
                        if (settings.jumuahReminder) ...[
                          Divider(color: c.border, height: 1),
                          _JumuahMinutesRow(
                            minutes: settings.jumuahMinutesBefore,
                            arabicDigits: arabicDigits,
                            s: s,
                            onChanged: (minutes) async {
                              await ref
                                  .read(
                                    notificationSettingsProvider.notifier,
                                  )
                                  .setJumuahMinutesBefore(minutes);
                              await _resync();
                            },
                          ),
                        ],
                      ],
                    ),
                  ),

                  const SizedBox(height: AppSpacing.lg),

                  // ─── صوت الأذان ──────────────────────────
                  SectionHeader(title: s.notificationsSound),
                  const SizedBox(height: AppSpacing.sm),
                  AtharCard(
                    child: Column(
                      children: [
                        AtharSettingsTile(
                          icon: Icons.music_note_outlined,
                          title: settings.sound.nameFor(locale),
                          value: settings.sound.bundled ? null : s.comingSoon,
                          onTap: () => _pickSound(context, ref),
                        ),
                        if (settings.audioUnavailable)
                          Padding(
                            padding: const EdgeInsets.all(AppSpacing.md),
                            child: _Hint(
                              icon: Icons.lock_outline,
                              text: s.notificationsSoundDesc,
                            ),
                          ),
                      ],
                    ),
                  ),

                  const SizedBox(height: AppSpacing.lg),

                  // ─── تجربة + عدد المجدول ─────────────────
                  AtharButton(
                    label: s.notificationsTest,
                    variant: AtharButtonVariant.outline,
                    onPressed: () async {
                      final service =
                          await ref.read(notificationServiceProvider.future);
                      await service.showTestNotification(s);
                      if (!context.mounted) return;
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text(s.notificationsTestQueued),
                        ),
                      );
                    },
                  ),

                  const SizedBox(height: AppSpacing.md),
                  Center(
                    child: Text(
                      _scheduledSummary(status, s, arabicDigits),
                      style: context.textStyles.bodySmall
                          ?.copyWith(color: c.ink60),
                      textAlign: TextAlign.center,
                    ),
                  ),
                  if (status.lastError != null) ...[
                    const SizedBox(height: AppSpacing.sm),
                    _Hint(icon: Icons.error_outline, text: status.lastError!),
                  ],
                ],
              ),
            ),
          ),

          const SizedBox(height: AppSpacing.xxl),
        ],
      ),
    );
  }

  /// كل الصلوات عدا الشروق (ليست صلاة).

  static const List<PrayerNameKey> _athanPrayers = [
    PrayerNameKey.fajr,
    PrayerNameKey.dhuhr,
    PrayerNameKey.asr,
    PrayerNameKey.maghrib,
    PrayerNameKey.isha,
  ];

  String _scheduledSummary(
    NotificationStatus status,
    Strings s,
    bool arabicDigits,
  ) {
    if (status.syncing) return '…';
    final count = status.scheduledCount;
    if (count == 0) return s.notificationsScheduledNone;
    final n = ArabicNumbers.format(count, useArabicDigits: arabicDigits);
    return '$n ${count == 1 ? s.notificationsScheduled : s.notificationsScheduledMany}';
  }

  String _modeLabel(PrayerAlertMode mode, Strings s) => switch (mode) {
        PrayerAlertMode.adhan => s.notificationsModeAdhan,
        PrayerAlertMode.shortAdhan => s.notificationsModeShortAdhan,
        PrayerAlertMode.beep => s.notificationsModeBeep,
        PrayerAlertMode.vibrate => s.notificationsModeVibrate,
        PrayerAlertMode.silent => s.notificationsModeSilent,
        PrayerAlertMode.off => s.notificationsModeOff,
      };

  Future<void> _pickMode(
    BuildContext context,
    WidgetRef ref,
    PrayerNameKey prayer,
  ) async {
    final s = ref.read(stringsProvider);
    final settings = ref.read(notificationSettingsProvider);
    final current = settings.modeFor(prayer);

    await showAtharSheet<void>(
      context: context,
      title: prayer.label(s),
      body: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          for (final mode in PrayerAlertMode.values)
            RadioListTile<PrayerAlertMode>(
              value: mode,
              groupValue: current,
              title: Text(_modeLabel(mode, s)),
              onChanged: (value) async {
                if (value == null) return;
                await ref
                    .read(notificationSettingsProvider.notifier)
                    .setMode(prayer, value);
                if (context.mounted) Navigator.of(context).pop();
                await _resync();
              },
            ),
        ],
      ),
    );
  }

  Future<void> _pickSound(BuildContext context, WidgetRef ref) async {
    final s = ref.read(stringsProvider);
    final locale = ref.read(currentLocaleProvider);
    final settings = ref.read(notificationSettingsProvider);

    await showAtharSheet<void>(
      context: context,
      title: s.notificationsSound,
      body: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: AppSpacing.lg),
            child: Text(
              s.notificationsSoundDesc,
              style: context.textStyles.bodySmall
                  ?.copyWith(color: context.colors.ink60),
            ),
          ),
          const SizedBox(height: AppSpacing.sm),
          for (final sound in AdhanSounds.all)
            RadioListTile<String>(
              value: sound.id,
              groupValue: settings.adhanSoundId,
              title: Text(sound.nameFor(locale)),
              // ⚠️ الأصوات غير المرفقة تُعرض لكنها تعمل كنغمة النظام
              // حتى تُضاف الملفات ويُتحقّق من تراخيصها.
              subtitle: sound.bundled
                  ? null
                  : Text(s.comingSoon, style: context.textStyles.bodySmall),
              onChanged: (value) async {
                if (value == null) return;
                await ref
                    .read(notificationSettingsProvider.notifier)
                    .setSound(value);
                if (context.mounted) Navigator.of(context).pop();
                await _resync();
              },
            ),
        ],
      ),
    );
  }
}

class _Hint extends StatelessWidget {
  const _Hint({required this.icon, required this.text});

  final IconData icon;
  final String text;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: c.surface2,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Icon(icon, size: 18, color: c.ink60),
          const SizedBox(width: AppSpacing.sm),
          Expanded(
            child: Text(
              text,
              style:
                  context.textStyles.bodySmall?.copyWith(color: c.ink60),
            ),
          ),
        ],
      ),
    );
  }
}

class _ActionBanner extends StatelessWidget {
  const _ActionBanner({
    required this.icon,
    required this.text,
    required this.actionLabel,
    required this.onPressed,
  });

  final IconData icon;
  final String text;
  final String actionLabel;
  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: c.primary100,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            children: [
              Icon(icon, size: 18, color: c.primary),
              const SizedBox(width: AppSpacing.sm),
              Expanded(
                child: Text(
                  text,
                  style: context.textStyles.bodyMedium,
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          AtharButton(
            label: actionLabel,
            variant: AtharButtonVariant.primary,
            onPressed: onPressed,
          ),
        ],
      ),
    );
  }
}

/// صفّ التذكير: عنوان الصلاة + شرائح الدقائق.
class _ReminderRow extends StatelessWidget {
  const _ReminderRow({
    required this.prayer,
    required this.label,
    required this.minutes,
    required this.arabicDigits,
    required this.s,
    required this.onChanged,
  });

  final PrayerNameKey prayer;
  final String label;
  final int minutes;
  final bool arabicDigits;
  final Strings s;
  final ValueChanged<int> onChanged;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.lg,
        vertical: AppSpacing.md,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            children: [
              Icon(
                prayer.icon,
                size: 18,
                color: context.colors.primary,
              ),
              const SizedBox(width: AppSpacing.sm),
              Expanded(
                child: Text(label, style: context.textStyles.titleMedium),
              ),
              Text(
                minutes == 0
                    ? s.notificationsMinutesOff
                    : s.tArgs('notifications_minutes_value', {
                        'n': ArabicNumbers.format(
                          minutes,
                          useArabicDigits: arabicDigits,
                        ),
                      }),
                style: context.textStyles.bodySmall
                    ?.copyWith(color: context.colors.ink60),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          Wrap(
            spacing: AppSpacing.sm,
            runSpacing: AppSpacing.sm,
            children: [
              for (final choice in NotificationSettings.reminderChoices)
                AtharChip(
                  label: choice == 0
                      ? s.notificationsMinutesOff
                      : ArabicNumbers.format(
                          choice,
                          useArabicDigits: arabicDigits,
                        ),
                  selected: choice == minutes,
                  onTap: (_) => onChanged(choice),
                ),
            ],
          ),
        ],
      ),
    );
  }
}

/// اختيار دقائق تذكير الجمعة (٣٠–٢٤٠).
class _JumuahMinutesRow extends StatelessWidget {
  const _JumuahMinutesRow({
    required this.minutes,
    required this.arabicDigits,
    required this.s,
    required this.onChanged,
  });

  final int minutes;
  final bool arabicDigits;
  final Strings s;
  final ValueChanged<int> onChanged;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.lg,
        vertical: AppSpacing.md,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  s.notificationsJumuahBefore,
                  style: context.textStyles.titleMedium,
                ),
              ),
              Text(
                s.tArgs('notifications_minutes_value', {
                  'n': ArabicNumbers.format(
                    minutes,
                    useArabicDigits: arabicDigits,
                  ),
                }),
                style: context.textStyles.bodySmall
                    ?.copyWith(color: context.colors.ink60),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          Wrap(
            spacing: AppSpacing.sm,
            runSpacing: AppSpacing.sm,
            children: [
              for (final choice in const [30, 60, 90, 120, 180, 240])
                AtharChip(
                  label: ArabicNumbers.format(
                    choice,
                    useArabicDigits: arabicDigits,
                  ),
                  selected: choice == minutes,
                  onTap: (_) => onChanged(choice),
                ),
            ],
          ),
        ],
      ),
    );
  }
}

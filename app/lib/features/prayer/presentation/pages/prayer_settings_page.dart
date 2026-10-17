import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/utils/greeting.dart';
import '../../../../core/widgets/widgets.dart';
import '../../application/location_controller.dart';
import '../../application/prayer_settings_controller.dart';
import '../../data/cities.dart';
import '../../domain/entities/place.dart';
import '../../domain/prayer_methods.dart';

/// شاشة إعدادات الصلاة: الموقع · الطريقة · المذهب · خطوط العرض · التعديل اليدوي.
///
/// كل تغيير يُحفظ فورًا ويعيد حساب المواقيت (لأن الموفّرين تسمع
/// [prayerSettingsProvider]).
class PrayerSettingsPage extends ConsumerWidget {
  const PrayerSettingsPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final locale = ref.watch(currentLocaleProvider);
    final arabicDigits = ref.watch(useArabicDigitsProvider);
    final settings = ref.watch(prayerSettingsProvider);
    final location = ref.watch(locationControllerProvider);
    final method = PrayerMethods.byId(settings.methodId);

    final Place place =
        (settings.locationMode == LocationMode.auto && settings.lastKnown != null)
            ? settings.lastKnown!
            : (Cities.byId(settings.manualCityId) ?? Place.fallback);

    return Scaffold(
      appBar: AppBar(title: Text(s.prayerSettingsTitle)),
      body: ListView(
        padding: const EdgeInsets.all(AppSpacing.lg),
        children: [
          // ─── الموقع ────────────────────────────────────
          SectionHeader(title: s.prayerLocation),
          const SizedBox(height: AppSpacing.sm),
          AtharCard(
            child: Column(
              children: [
                AtharSettingsTile(
                  icon: Icons.location_city_outlined,
                  title: s.prayerChooseCity,
                  value: place.nameFor(locale),
                  onTap: () => context.push('/city-picker'),
                ),
                Divider(color: context.colors.border, height: 1),
                AtharSettingsTile(
                  icon: Icons.my_location,
                  title: s.prayerUseMyLocation,
                  subtitle: s.prayerLocationRationale,
                  iconColor: context.colors.primary,
                  trailing: location.isBusy
                      ? const SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : null,
                  onTap: location.isBusy
                      ? null
                      : () => _useMyLocation(context, ref),
                ),
                if (location.failure != null)
                  Padding(
                    padding: const EdgeInsets.only(top: AppSpacing.md),
                    child: _LocationError(
                      failure: location.failure!,
                      s: s,
                      onOpenSettings: () =>
                          ref.read(locationControllerProvider.notifier)
                              .openSettings(),
                    ),
                  ),
              ],
            ),
          ),

          const SizedBox(height: AppSpacing.lg),

          // ─── طريقة الحساب ──────────────────────────────
          SectionHeader(title: s.prayerMethod),
          const SizedBox(height: AppSpacing.sm),
          AtharCard(
            child: Column(
              children: [
                AtharSettingsTile(
                  icon: Icons.calculate_outlined,
                  title: method.nameFor(locale),
                  subtitle: method.noteFor(locale),
                  onTap: () => _pickMethod(context, ref),
                ),
              ],
            ),
          ),

          const SizedBox(height: AppSpacing.lg),

          // ─── المذهب ────────────────────────────────────
          SectionHeader(title: s.prayerMadhab),
          const SizedBox(height: AppSpacing.sm),
          AtharCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  s.prayerMadhabSub,
                  style: context.textStyles.bodySmall?.copyWith(
                    color: context.colors.ink60,
                  ),
                ),
                const SizedBox(height: AppSpacing.md),
                Wrap(
                  spacing: AppSpacing.sm,
                  children: [
                    for (final option in MadhabId.values)
                      AtharChip(
                        label: option.nameFor(locale),
                        selected: option == settings.madhab,
                        onTap: () => ref
                            .read(prayerSettingsProvider.notifier)
                            .setMadhab(option),
                      ),
                  ],
                ),
              ],
            ),
          ),

          const SizedBox(height: AppSpacing.lg),

          // ─── خطوط العرض العالية ────────────────────────
          SectionHeader(title: s.prayerHighLatitude),
          const SizedBox(height: AppSpacing.sm),
          AtharCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  s.prayerHighLatitudeSub,
                  style: context.textStyles.bodySmall?.copyWith(
                    color: context.colors.ink60,
                  ),
                ),
                const SizedBox(height: AppSpacing.md),
                Wrap(
                  spacing: AppSpacing.sm,
                  runSpacing: AppSpacing.sm,
                  children: [
                    for (final option in HighLatitudeRuleId.values)
                      AtharChip(
                        label: option.nameFor(locale),
                        selected: option == settings.highLatitudeRule,
                        onTap: () => ref
                            .read(prayerSettingsProvider.notifier)
                            .setHighLatitudeRule(option),
                      ),
                  ],
                ),
              ],
            ),
          ),

          const SizedBox(height: AppSpacing.lg),

          // ─── التعديل اليدوي ────────────────────────────
          SectionHeader(title: s.prayerAdjustments),
          const SizedBox(height: AppSpacing.sm),
          AtharCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  s.prayerAdjustmentsSub,
                  style: context.textStyles.bodySmall?.copyWith(
                    color: context.colors.ink60,
                  ),
                ),
                if (settings.hasAdjustments) ...[
                  const SizedBox(height: AppSpacing.md),
                  Container(
                    padding: const EdgeInsets.all(AppSpacing.md),
                    decoration: BoxDecoration(
                      color: context.colors.warningBg,
                      borderRadius: AppRadius.chipRadius,
                    ),
                    child: Row(
                      children: [
                        Icon(Icons.info_outline,
                            size: 16, color: context.colors.warning),
                        const SizedBox(width: AppSpacing.sm),
                        Expanded(
                          child: Text(
                            s.prayerAdjustedNotice,
                            style: context.textStyles.bodySmall?.copyWith(
                              color: context.colors.warning,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
                const SizedBox(height: AppSpacing.sm),
                for (final prayer in prayerOrder)
                  _AdjustmentRow(
                    label: prayer.label(s),
                    minutes: settings.adjustmentFor(prayer),
                    useArabicDigits: arabicDigits,
                    onChanged: (value) => ref
                        .read(prayerSettingsProvider.notifier)
                        .setAdjustment(prayer, value),
                  ),
                if (settings.hasAdjustments)
                  Align(
                    alignment: AlignmentDirectional.centerStart,
                    child: TextButton(
                      onPressed: () => ref
                          .read(prayerSettingsProvider.notifier)
                          .clearAdjustments(),
                      child: Text(s.prayerClearAdjustments),
                    ),
                  ),
              ],
            ),
          ),

          const SizedBox(height: AppSpacing.xxl),
        ],
      ),
    );
  }

  Future<void> _useMyLocation(BuildContext context, WidgetRef ref) async {
    final ok = await ref
        .read(locationControllerProvider.notifier)
        .useMyLocation();
    if (!context.mounted) return;
    final s = ref.read(stringsProvider);
    if (ok) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(s.prayerLocationUpdated)),
      );
    }
  }

  Future<void> _pickMethod(BuildContext context, WidgetRef ref) async {
    final s = ref.read(stringsProvider);
    final locale = ref.read(currentLocaleProvider);
    final settings = ref.read(prayerSettingsProvider);

    await showAtharSheet<void>(
      context: context,
      title: s.prayerMethod,
      body: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          for (final method in PrayerMethods.all)
            RadioListTile<String>(
              value: method.id,
              groupValue: settings.methodId,
              title: Text(method.nameFor(locale)),
              subtitle: Text(
                method.noteFor(locale),
                style: context.textStyles.bodySmall,
              ),
              onChanged: (value) {
                if (value == null) return;
                ref.read(prayerSettingsProvider.notifier).setMethod(value);
                Navigator.of(context).pop();
              },
            ),
        ],
      ),
    );
  }
}

class _LocationError extends StatelessWidget {
  const _LocationError({
    required this.failure,
    required this.s,
    required this.onOpenSettings,
  });

  final LocationFailure failure;
  final Strings s;
  final VoidCallback onOpenSettings;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    final (message, canOpen) = switch (failure) {
      LocationFailure.serviceDisabled =>
        (s.prayerLocationServiceOff, true),
      LocationFailure.permissionDenied =>
        (s.prayerLocationDenied, false),
      LocationFailure.permissionDeniedForever =>
        (s.prayerLocationDeniedForever, true),
      LocationFailure.timeout => (s.prayerLocationTimeout, false),
      LocationFailure.unknown => (s.prayerLocationUnknown, false),
    };

    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: c.warningBg,
        borderRadius: AppRadius.chipRadius,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            children: [
              Icon(Icons.location_off_outlined, size: 16, color: c.warning),
              const SizedBox(width: AppSpacing.sm),
              Expanded(
                child: Text(
                  message,
                  style: context.textStyles.bodySmall?.copyWith(
                    color: c.warning,
                  ),
                ),
              ),
            ],
          ),
          if (canOpen)
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: TextButton(
                onPressed: onOpenSettings,
                child: Text(s.prayerOpenSettings),
              ),
            ),
        ],
      ),
    );
  }
}

class _AdjustmentRow extends StatelessWidget {
  const _AdjustmentRow({
    required this.label,
    required this.minutes,
    required this.useArabicDigits,
    required this.onChanged,
  });

  final String label;
  final int minutes;
  final bool useArabicDigits;
  final ValueChanged<int> onChanged;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    final text = minutes == 0
        ? '—'
        : '${minutes > 0 ? '+' : '−'}${ArabicNumbers.format(
            minutes.abs().toString(),
            useArabicDigits: useArabicDigits,
          )}';

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppSpacing.xs),
      child: Row(
        children: [
          Expanded(
            child: Text(label, style: context.textStyles.bodyLarge),
          ),
          IconButton(
            icon: const Icon(Icons.remove_circle_outline, size: 20),
            tooltip: '−1',
            onPressed: minutes <= -60 ? null : () => onChanged(minutes - 1),
          ),
          SizedBox(
            width: 56,
            child: Center(
              child: Text(
                text,
                style: context.textStyles.titleMedium?.copyWith(
                  color: minutes == 0 ? c.ink40 : c.ink,
                  fontFeatures: const [FontFeature.tabularFigures()],
                ),
              ),
            ),
          ),
          IconButton(
            icon: const Icon(Icons.add_circle_outline, size: 20),
            tooltip: '+1',
            onPressed: minutes >= 60 ? null : () => onChanged(minutes + 1),
          ),
        ],
      ),
    );
  }
}

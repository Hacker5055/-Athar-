import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/l10n/strings.dart';
import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/widgets.dart';
import '../../application/prayer_settings_controller.dart';
import '../../data/cities.dart';
import '../../domain/entities/place.dart';

/// شاشة اختيار المدينة — بحث بالعربية أو الإنجليزية أو الفرنسية.
///
/// البحث متجاهل للتشكيل والتطويل (`SearchText.normalizeArabic`):
/// «القاهرة» == «القاهره» == «القـاهرة».
class CityPickerPage extends ConsumerStatefulWidget {
  const CityPickerPage({super.key});

  @override
  ConsumerState<CityPickerPage> createState() => _CityPickerPageState();
}

class _CityPickerPageState extends ConsumerState<CityPickerPage> {
  String _query = '';

  @override
  Widget build(BuildContext context) {
    final s = ref.watch(stringsProvider);
    final locale = ref.watch(currentLocaleProvider);
    final selectedId = ref.watch(prayerSettingsProvider).manualCityId;

    final results = Cities.search(_query);

    return Scaffold(
      appBar: AppBar(title: Text(s.prayerChooseCity)),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(AppSpacing.lg),
            child: AtharTextField(
              hint: s.citySearchHint,
              prefixIcon: Icons.search,
              onChanged: (value) => setState(() => _query = value),
            ),
          ),
          Expanded(
            child: results.isEmpty
                ? AtharEmptyState(
                    icon: Icons.location_city_outlined,
                    title: s.cityNoResults,
                    message: s.cityNoResultsHint,
                  )
                : ListView.separated(
                    itemCount: results.length,
                    separatorBuilder: (_, __) => Divider(
                      color: context.colors.border,
                      height: 1,
                    ),
                    itemBuilder: (context, index) {
                      final city = results[index];
                      return _CityRow(
                        city: city,
                        name: city.nameFor(locale),
                        isSelected: city.id == selectedId,
                        onTap: () => _select(city),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }

  Future<void> _select(Place city) async {
    await ref.read(prayerSettingsProvider.notifier).selectCity(city.id);
    if (mounted) context.pop();
  }
}

class _CityRow extends StatelessWidget {
  const _CityRow({
    required this.city,
    required this.name,
    required this.isSelected,
    required this.onTap,
  });

  final Place city;
  final String name;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.lg,
          vertical: AppSpacing.md,
        ),
        child: Row(
          children: [
            Icon(
              isSelected
                  ? Icons.radio_button_checked
                  : Icons.radio_button_unchecked,
              size: 20,
              color: isSelected ? c.primary : c.ink40,
            ),
            const SizedBox(width: AppSpacing.md),
            Expanded(
              child: Text(
                name,
                style: context.textStyles.bodyLarge?.copyWith(
                  fontWeight:
                      isSelected ? FontWeight.w600 : FontWeight.normal,
                ),
              ),
            ),
            Text(
              city.countryCode,
              style: context.textStyles.labelSmall?.copyWith(
                color: c.ink40,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

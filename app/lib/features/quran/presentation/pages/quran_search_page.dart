import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/settings/settings_controller.dart'
    show stringsProvider, useArabicDigitsProvider;
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../../../core/widgets/widgets.dart';
import '../../application/quran_providers.dart';
import '../../domain/entities/ayah.dart';
import '../../domain/search_query.dart';

/// البحث في القرآن (`docs/02` B8 — P0).
///
/// ثلاثة أنواع مدعومة في حقل واحد:
/// • نص عربي متجاهل للتشكيل → فهرس `ayahs_fts`
/// • نص لاتيني (EN/FR) → فهرس `translations_fts`
/// • مرجع: `2:255` · «ص ٤٢» · «ج ٣» · «حزب ٧» → قفزة مباشرة
class QuranSearchPage extends ConsumerStatefulWidget {
  const QuranSearchPage({super.key});

  @override
  ConsumerState<QuranSearchPage> createState() => _QuranSearchPageState();
}

class _QuranSearchPageState extends ConsumerState<QuranSearchPage> {
  final _controller = TextEditingController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final s = ref.watch(stringsProvider);
    final raw = ref.watch(quranSearchQueryProvider);
    final query = QuranSearchParser.parse(raw);
    final results = ref.watch(quranSearchResultsProvider(query));

    return Scaffold(
      appBar: AppBar(
        title: Text(s.quranSearchTitle),
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(
              horizontal: AppSpacing.screenPaddingH,
              vertical: AppSpacing.sm,
            ),
            child: AtharTextField(
              controller: _controller,
              hint: s.quranSearchHint,
              prefixIcon: Icons.search_rounded,
              autofocus: true,
              textInputAction: TextInputAction.search,
              onChanged: (value) =>
                  ref.read(quranSearchQueryProvider.notifier).state = value,
            ),
          ),
          Expanded(child: _Results(query: query, results: results)),
        ],
      ),
    );
  }
}

class _Results extends ConsumerWidget {
  const _Results({required this.query, required this.results});

  final QuranSearchQuery query;
  final AsyncValue<List<AyahMatch>> results;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final arabic = ref.watch(useArabicDigitsProvider);
    final c = context.colors;

    if (query.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(AppSpacing.screenPaddingH),
          child: Text(
            s.quranSearchHint,
            textAlign: TextAlign.center,
            style: context.textStyles.bodyMedium?.copyWith(color: c.ink60),
          ),
        ),
      );
    }

    return results.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, _) => Center(
        child: Padding(
          padding: const EdgeInsets.all(AppSpacing.screenPaddingH),
          child: Text(
            '$error',
            textAlign: TextAlign.center,
            style: context.textStyles.bodyMedium?.copyWith(color: c.danger),
          ),
        ),
      ),
      data: (matches) {
        if (matches.isEmpty) {
          return Center(
            child: Text(
              s.quranSearchEmpty,
              style: context.textStyles.bodyMedium?.copyWith(color: c.ink60),
            ),
          );
        }
        return Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: AppSpacing.screenPaddingH,
                vertical: AppSpacing.sm,
              ),
              child: Text(
                s.tArgs(
                  'quran_results_count',
                  {'n': ArabicNumbers.format(matches.length,
                      useArabicDigits: arabic)},
                ),
                style: context.textStyles.labelMedium?.copyWith(color: c.ink60),
              ),
            ),
            Expanded(
              child: ListView.separated(
                padding: const EdgeInsets.only(bottom: AppSpacing.huge),
                itemCount: matches.length,
                separatorBuilder: (_, __) => const Divider(height: 1),
                itemBuilder: (context, index) =>
                    _MatchTile(match: matches[index]),
              ),
            ),
          ],
        );
      },
    );
  }
}

class _MatchTile extends ConsumerWidget {
  const _MatchTile({required this.match});

  final AyahMatch match;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final arabic = ref.watch(useArabicDigitsProvider);
    final c = context.colors;
    final ayah = match.ayah;
    final name = match.surah?.nameAr;

    return ListTile(
      contentPadding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.screenPaddingH,
        vertical: AppSpacing.xs,
      ),
      title: Text(
        ayah.textUthmani,
        textDirection: TextDirection.rtl,
        maxLines: 2,
        overflow: TextOverflow.ellipsis,
        style: context.textStyles.bodyLarge?.copyWith(
          color: c.ink,
          height: 1.9,
        ),
      ),
      subtitle: Padding(
        padding: const EdgeInsets.only(top: AppSpacing.xs),
        child: Text(
          '${name == null ? '' : '$name · '}'
          '${ArabicNumbers.format(ayah.surahId, useArabicDigits: arabic)}:'
          '${ArabicNumbers.format(ayah.ayah, useArabicDigits: arabic)}',
          style: context.textStyles.bodySmall?.copyWith(color: c.ink60),
        ),
      ),
      onTap: () => context.push('/quran/surah/${ayah.surahId}'),
    );
  }
}

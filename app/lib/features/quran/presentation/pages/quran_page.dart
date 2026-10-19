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
import '../../domain/entities/surah.dart';
import '../widgets/quran_empty_state.dart';
import '../widgets/reading_settings_sheet.dart';
import '../widgets/surah_tile.dart';

/// تبويب القرآن — فهرس بثلاثة أبواب: **سورة · جزء · صفحة**.
///
/// ### لماذا ثلاثة أبواب؟
/// `docs/02` B2 يطلب التنقّل بالمصحف، والناس تحفظ القرآن بإحدى ثلاث
/// طرق: بالسورة، أو بالجزء (ورد الحزب اليومي)، أو برقم الصفحة (حفظة
/// المصحف). باب واحد يضطر قارئ الجزء للبحث عن أول آية فيه.
class QuranPage extends ConsumerStatefulWidget {
  const QuranPage({super.key});

  @override
  ConsumerState<QuranPage> createState() => _QuranPageState();
}

class _QuranPageState extends ConsumerState<QuranPage> {
  @override
  Widget build(BuildContext context) {
    final s = ref.watch(stringsProvider);
    final status = ref.watch(quranStatusProvider);
    final surahs = ref.watch(surahsProvider);

    final ready = status.valueOrNull?.isComplete ?? false;

    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: Text(s.quranIndex),
          actions: [
            if (ready)
              IconButton(
                tooltip: s.quranSearchTitle,
                icon: const Icon(Icons.search_rounded),
                onPressed: () => context.push('/quran/search'),
              ),
            if (ready)
              IconButton(
                tooltip: s.quranReaderSettings,
                icon: const Icon(Icons.text_format_rounded),
                onPressed: () => showReadingSettingsSheet(context, ref),
              ),
          ],
          bottom: ready
              ? TabBar(
                  tabs: [
                    Tab(text: s.quranTabSurah),
                    Tab(text: s.quranTabJuz),
                    Tab(text: s.quranTabPage),
                  ],
                )
              : null,
        ),
        body: !ready
            ? const QuranEmptyState()
            : TabBarView(
                children: [
                  _SurahTab(surahs: surahs),
                  const _JuzTab(),
                  const _PageTab(),
                ],
              ),
      ),
    );
  }
}

// ── باب السور ────────────────────────────────────────────
class _SurahTab extends ConsumerWidget {
  const _SurahTab({required this.surahs});

  final List<Surah> surahs;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final c = context.colors;

    if (surahs.isEmpty) {
      return Center(
        child: Text(
          s.quranEmptyBody,
          style: context.textStyles.bodyMedium?.copyWith(color: c.ink60),
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.fromLTRB(
        AppSpacing.screenPaddingH,
        AppSpacing.md,
        AppSpacing.screenPaddingH,
        AppSpacing.huge,
      ),
      itemCount: surahs.length,
      itemBuilder: (context, index) => SurahTile(
        surah: surahs[index],
        onTap: () => context.push('/quran/surah/${surahs[index].id}'),
      ),
    );
  }
}

// ── شبكة الأجزاء ─────────────────────────────────────────
class _JuzTab extends ConsumerWidget {
  const _JuzTab();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final arabic = ref.watch(useArabicDigitsProvider);
    final c = context.colors;

    return GridView.builder(
      padding: const EdgeInsets.fromLTRB(
        AppSpacing.screenPaddingH,
        AppSpacing.md,
        AppSpacing.screenPaddingH,
        AppSpacing.huge,
      ),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 3,
        crossAxisSpacing: AppSpacing.sm,
        mainAxisSpacing: AppSpacing.sm,
        childAspectRatio: 1.15,
      ),
      itemCount: 30,
      itemBuilder: (context, index) {
        final juz = index + 1;
        return AtharCard(
          padding: EdgeInsets.zero,
          onTap: () => context.push('/quran/juz/$juz'),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                ArabicNumbers.format(juz, useArabicDigits: arabic),
                style: context.textStyles.headlineSmall?.copyWith(
                  color: c.primary600,
                  fontWeight: FontWeight.w700,
                ),
              ),
              const VSpace(2),
              Text(
                s.juz,
                style: context.textStyles.bodySmall?.copyWith(color: c.ink60),
              ),
            ],
          ),
        );
      },
    );
  }
}

// ── شبكة الصفحات ─────────────────────────────────────────
class _PageTab extends ConsumerWidget {
  const _PageTab();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final arabic = ref.watch(useArabicDigitsProvider);
    final c = context.colors;

    return GridView.builder(
      padding: const EdgeInsets.fromLTRB(
        AppSpacing.screenPaddingH,
        AppSpacing.md,
        AppSpacing.screenPaddingH,
        AppSpacing.huge,
      ),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 4,
        crossAxisSpacing: AppSpacing.sm,
        mainAxisSpacing: AppSpacing.sm,
        childAspectRatio: 1,
      ),
      itemCount: 604,
      itemBuilder: (context, index) {
        final page = index + 1;
        return AtharCard(
          padding: EdgeInsets.zero,
          onTap: () => context.push('/quran/page/$page'),
          child: Center(
            child: Text(
              ArabicNumbers.format(page, useArabicDigits: arabic),
              style: context.textStyles.titleMedium?.copyWith(
                color: c.ink,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        );
      },
    );
  }
}

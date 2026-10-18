import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/settings/settings_controller.dart'
    show stringsProvider;
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/theme/quran_theme.dart';
import '../../../../core/utils/arabic_numbers.dart';
import '../../application/quran_providers.dart';
import '../../application/quran_settings_controller.dart';
import '../../domain/entities/ayah.dart';
import '../widgets/ayah_view.dart';
import '../widgets/reading_settings_sheet.dart';

/// ما يعرضه القارئ — سورة كاملة، أو جزء، أو صفحة مصحف.
///
/// الشاشة **واحدة** والفرق في مصدر الآيات فقط. السبب: تجربة القراءة
/// (الترقيم، السجدة، الترجمة، الثيم) يجب أن تكون متطابقة في الحالات
/// الثلاث؛ ثلاث شاشات منفصلة تعني ثلاث نسخ تتعارض مع الوقت.
sealed class ReaderArgs {
  const ReaderArgs();

  const factory ReaderArgs.surah(int id) = _SurahArgs;
  const factory ReaderArgs.juz(int number) = _JuzArgs;
  const factory ReaderArgs.page(int number) = _PageArgs;
}

final class _SurahArgs extends ReaderArgs {
  const _SurahArgs(this.id);
  final int id;
}

final class _JuzArgs extends ReaderArgs {
  const _JuzArgs(this.number);
  final int number;
}

final class _PageArgs extends ReaderArgs {
  const _PageArgs(this.number);
  final int number;
}

/// شاشة القارئ — **وضع القائمة** (`docs/02` B2).
///
/// ### لماذا لا بسملة منفصلة؟
/// النص العثماني (Tanzil) يجعل البسملة **جزءًا من الآية الأولى** في كل
/// سورة عدا التوبة. عرضها كعنوان يعني تكرارها. والتوبة لا تُفتح بها —
/// تمامًا كما في المصحف، ودون أي استثناء في الكود.
///
/// ### وضع المصحف (الصفحات)
/// لم يُبنَ بعد: يحتاج صور صفحات أو محرّك تخطيط ثابت، وكلاهما مؤجّل —
/// راجع `docs/09` §6½.
class ReaderPage extends ConsumerWidget {
  const ReaderPage({super.key, required this.args});

  final ReaderArgs args;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final settings = ref.watch(quranSettingsProvider);
    final surahs = ref.watch(surahsProvider);
    final colors = settings.pageTheme.colors;

    final ayahsAsync = switch (args) {
      _SurahArgs(:final id) => ref.watch(surahAyahsProvider(id)),
      _JuzArgs(:final number) => ref.watch(juzAyahsProvider(number)),
      _PageArgs(:final number) => ref.watch(pageAyahsProvider(number)),
    };

    final namesById = <int, String>{
      for (final surah in surahs) surah.id: surah.nameAr,
    };

    final title = switch (args) {
      _SurahArgs(:final id) => namesById[id] ?? s.quranIndex,
      _JuzArgs(:final number) => '${s.juz} ${ArabicNumbers.format(number)}',
      _PageArgs(:final number) =>
        '${s.quranPageWord} ${ArabicNumbers.format(number)}',
    };

    return Scaffold(
      backgroundColor: colors.background,
      appBar: AppBar(
        backgroundColor: colors.background,
        foregroundColor: colors.text,
        title: Text(title),
        actions: [
          IconButton(
            tooltip: s.quranReaderSettings,
            icon: const Icon(Icons.text_format_rounded),
            onPressed: () => showReadingSettingsSheet(context, ref),
          ),
        ],
      ),
      body: ayahsAsync.when(
        loading: () => Center(
          child: CircularProgressIndicator(color: colors.accent),
        ),
        error: (error, _) => Center(
          child: Padding(
            padding: const EdgeInsets.all(AppSpacing.screenPaddingH),
            child: Text(
              '$error',
              textAlign: TextAlign.center,
              style:
                  context.textStyles.bodyMedium?.copyWith(color: colors.text),
            ),
          ),
        ),
        data: (ayahs) => _ReaderBody(
          ayahs: ayahs,
          settings: settings,
          colors: colors,
          namesById: namesById,
        ),
      ),
    );
  }
}

class _ReaderBody extends ConsumerWidget {
  const _ReaderBody({
    required this.ayahs,
    required this.settings,
    required this.colors,
    required this.namesById,
  });

  final List<Ayah> ayahs;
  final QuranSettings settings;
  final QuranThemeColors colors;
  final Map<int, String> namesById;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);

    if (ayahs.isEmpty) {
      return Center(
        child: Text(
          s.quranNoAyahs,
          style: context.textStyles.bodyMedium?.copyWith(color: colors.text),
        ),
      );
    }

    return ListView.separated(
      padding: const EdgeInsets.fromLTRB(
        AppSpacing.sm,
        AppSpacing.sm,
        AppSpacing.sm,
        AppSpacing.huge,
      ),
      itemCount: ayahs.length,
      separatorBuilder: (_, __) => Divider(
        height: 1,
        color: colors.markerBorder.fade(0.6),
      ),
      itemBuilder: (context, index) {
        final ayah = ayahs[index];
        // عنوان السورة يظهر عند عبور حد سورة (عرض الجزء/الصفحة فقط —
        // في عرض السورة كل الآيات لسورة واحدة فلا داعي).
        final startsNewSurah = index > 0 && ayah.ayah == 1;

        return AyahView(
          ayah: ayah,
          settings: settings,
          surahName: startsNewSurah ? namesById[ayah.surahId] : null,
        );
      },
    );
  }
}

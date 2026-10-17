import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../shared/presentation/widgets/coming_soon_view.dart';

/// تبويب القرآن.
///
/// أسبوع ٢ و٣: فهرس ١١٤ سورة + القارئ (نص عثماني، تفسير، ترجمة،
/// تلاوة، بحث FTS5) — راجع `docs/02` قسم (ب) و`docs/06`.
class QuranPage extends ConsumerWidget {
  const QuranPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);

    return Scaffold(
      appBar: AppBar(title: Text(s.quranIndex)),
      body: Column(
        children: [
          // حقل البحث جاهز — هيتوصّل بالفهرس في أسبوع ٢.
          Padding(
            padding: const EdgeInsets.symmetric(
              horizontal: AppSpacing.screenPaddingH,
              vertical: AppSpacing.sm,
            ),
            child: AtharTextField(
              hint: s.searchPh,
              prefixIcon: Icons.search,
              readOnly: true,
            ),
          ),
          Expanded(
            child: ComingSoonView(
              icon: Icons.menu_book_outlined,
              title: s.quranSoonTitle,
              message: s.quranSoonBody,
              week: 2,
            ),
          ),
        ],
      ),
    );
  }
}

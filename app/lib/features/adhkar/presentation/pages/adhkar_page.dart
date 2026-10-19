import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/settings/settings_controller.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../shared/presentation/widgets/coming_soon_view.dart';

/// تبويب الأذكار.
///
/// أسبوع ٣: ١٠ تصنيفات بعدّاد لكل ذكر + مكتبة أدعية + أسماء الله الحسنى.
/// كل ذكر لازم يكون عليه **تخريج** — راجع `docs/02` قسم (ج).
class AdhkarPage extends ConsumerWidget {
  const AdhkarPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);

    return Scaffold(
      appBar: AppBar(title: Text(s.adhkarCat)),
      body: ComingSoonView(
        icon: Icons.list_alt_outlined,
        title: s.adhkarSoonTitle,
        message: s.adhkarSoonBody,
        week: 3,
      ),
    );
  }
}

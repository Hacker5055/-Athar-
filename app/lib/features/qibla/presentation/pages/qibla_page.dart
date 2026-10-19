import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/settings/settings_controller.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../shared/presentation/widgets/coming_soon_view.dart';

/// تبويب القبلة.
///
/// أسبوع ٣: بوصلة بدمج المستشعرات + **تحويل الميل المغناطيسي**
/// (البوصلة تقرأ الشمال المغناطيسي والقبلة محسوبة على الشمال الحقيقي)
/// + تنبيه المعايرة + بديل الشمس. راجع `docs/05` §5.
class QiblaPage extends ConsumerWidget {
  const QiblaPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);

    return Scaffold(
      appBar: AppBar(title: Text(s.qiblaTitle)),
      body: ComingSoonView(
        icon: Icons.explore_outlined,
        title: s.qiblaSoonTitle,
        message: s.qiblaSoonBody,
        week: 3,
      ),
    );
  }
}

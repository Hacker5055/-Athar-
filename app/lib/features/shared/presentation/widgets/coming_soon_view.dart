import 'package:flutter/material.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/widgets.dart';

/// شاشة «قريبًا» الموحّدة لمرحلة البناء.
///
/// بدل شاشات فاضية غامضة: بنوضح إيه الجاي وإمتى (أي أسبوع من الخطة)
/// عشان أي حد يفتح المشروع يفهم حالة كل وحدة.
class ComingSoonView extends StatelessWidget {
  const ComingSoonView({
    super.key,
    required this.icon,
    required this.title,
    required this.message,
    this.week,
  });

  final IconData icon;
  final String title;
  final String message;
  final int? week;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;

    return Center(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(AppSpacing.xl),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 88,
              height: 88,
              decoration: BoxDecoration(
                color: c.surface2,
                shape: BoxShape.circle,
              ),
              child: Icon(icon, size: 40, color: c.ink40),
            ),
            const VSpace(AppSpacing.xl),
            Text(
              title,
              textAlign: TextAlign.center,
              style: context.textStyles.titleLarge,
            ),
            if (week != null) ...[
              const VSpace(AppSpacing.md),
              AtharBadge(label: _weekLabel(context, week!)),
            ],
            const VSpace(AppSpacing.md),
            Text(
              message,
              textAlign: TextAlign.center,
              style: context.textStyles.bodyMedium?.copyWith(
                color: c.ink60,
                height: 1.9,
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _weekLabel(BuildContext context, int week) {
    final s = Localizations.localeOf(context).languageCode;
    switch (s) {
      case 'fr':
        return 'Semaine $week';
      case 'en':
        return 'Week $week';
      default:
        return 'أسبوع ${week == 1 ? "١" : week == 2 ? "٢" : week == 3 ? "٣" : "$week"}';
    }
  }
}

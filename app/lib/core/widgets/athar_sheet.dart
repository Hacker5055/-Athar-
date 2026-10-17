import 'package:flutter/material.dart';

import '../theme/app_spacing.dart';
import '../theme/app_theme.dart';
import 'gap.dart';

/// الورقة السفلية الموحّدة.
///
/// كل الأوراق في التطبيق بتمر من هنا عشان:
/// - الشكل والحشو متسق.
/// - لووحة المفاتيح مرفوعة (viewInsets) المحتوى يفضل ظاهر.
/// - المحتوى يقدر يوصل لـ 90% من الشاشة ويتمرر عند اللزوم.
Future<T?> showAtharSheet<T>({
  required BuildContext context,
  required String title,
  required Widget body,
  String? subtitle,
  bool isScrollControlled = true,
}) {
  return showModalBottomSheet<T>(
    context: context,
    isScrollControlled: isScrollControlled,
    backgroundColor: context.colors.bg,
    showDragHandle: true,
    builder: (sheetContext) {
      return SafeArea(
        child: Padding(
          padding: EdgeInsets.only(
            bottom: MediaQuery.of(sheetContext).viewInsets.bottom,
          ),
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Padding(
                  padding: const EdgeInsets.fromLTRB(
                    AppSpacing.xl,
                    AppSpacing.sm,
                    AppSpacing.xl,
                    AppSpacing.lg,
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: context.textStyles.headlineSmall?.copyWith(
                          fontSize: 18,
                        ),
                      ),
                      if (subtitle != null) ...[
                        const VSpace(AppSpacing.xs),
                        Text(
                          subtitle!,
                          style: context.textStyles.bodySmall?.copyWith(
                            color: context.colors.ink60,
                            fontSize: 11.5,
                          ),
                        ),
                      ],
                    ],
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.fromLTRB(
                    AppSpacing.xl,
                    0,
                    AppSpacing.xl,
                    AppSpacing.xl,
                  ),
                  child: body,
                ),
              ],
            ),
          ),
        ),
      );
    },
  );
}

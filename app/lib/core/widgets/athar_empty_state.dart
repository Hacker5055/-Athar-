import 'package:flutter/material.dart';

import '../theme/app_spacing.dart';
import '../theme/app_theme.dart';
import 'athar_button.dart';
import 'gap.dart';

/// حالة فارغة — كل شاشة محتاجة واحدة.
///
/// مفيش شاشة فاضية من غير: أيقونة، عنوان، سبب، وإجراء واضح.
class AtharEmptyState extends StatelessWidget {
  const AtharEmptyState({
    super.key,
    required this.icon,
    required this.title,
    this.message,
    this.actionLabel,
    this.onAction,
    this.compact = false,
  });

  final IconData icon;
  final String title;
  final String? message;
  final String? actionLabel;
  final VoidCallback? onAction;

  /// نسخة مصغّرة داخل بطاقة بدل شاشة كاملة.
  final bool compact;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;

    return Padding(
      padding: EdgeInsets.symmetric(
        horizontal: AppSpacing.xl,
        vertical: compact ? AppSpacing.lg : AppSpacing.huge,
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 72,
            height: 72,
            decoration: BoxDecoration(
              color: c.surface2,
              shape: BoxShape.circle,
            ),
            child: Icon(icon, size: 32, color: c.ink40),
          ),
          const VSpace(AppSpacing.lg),
          Text(
            title,
            textAlign: TextAlign.center,
            style: context.textStyles.titleMedium?.copyWith(
              fontSize: 16,
              color: c.ink,
            ),
          ),
          if (message != null) ...[
            const VSpace(AppSpacing.sm),
            Text(
              message!,
              textAlign: TextAlign.center,
              style: context.textStyles.bodySmall?.copyWith(
                color: c.ink60,
                height: 1.8,
              ),
            ),
          ],
          if (actionLabel != null && onAction != null) ...[
            const VSpace(AppSpacing.xl),
            AtharButton(
              label: actionLabel!,
              onPressed: onAction,
              variant: AtharButtonVariant.outline,
              icon: Icons.arrow_forward,
            ),
          ],
        ],
      ),
    );
  }

  /// حالة «قريبًا» الجاهزة — بتستخدم كتير في مرحلة البناء.
  factory AtharEmptyState.comingSoon({
    Key? key,
    required String title,
    required String message,
  }) {
    return AtharEmptyState(
      key: key,
      icon: Icons.construction,
      title: title,
      message: message,
    );
  }
}

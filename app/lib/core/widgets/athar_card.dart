import 'package:flutter/material.dart';

import '../theme/app_theme.dart';
import '../theme/app_spacing.dart';
import 'gap.dart';

/// بطاقة أثر — حواف 20، حدود 1px، بدون ظل (تصميم مسطّح وهادئ).
class AtharCard extends StatelessWidget {
  const AtharCard({
    super.key,
    required this.child,
    this.onTap,
    this.padding = const EdgeInsets.all(AppSpacing.lg),
    this.color,
    this.tinted = false,
    this.showBorder = true,
    this.margin,
  });

  final Widget child;
  final VoidCallback? onTap;
  final EdgeInsetsGeometry padding;
  final Color? color;

  /// خلفية ملوّنة بلون primary الفاتح (للبطاقات المميزة).
  final bool tinted;

  /// `false` للبطاقات المدمجة في قوائم.
  final bool showBorder;

  final EdgeInsetsGeometry? margin;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;

    final decoration = BoxDecoration(
      color: color ?? (tinted ? c.primary100 : c.surface),
      borderRadius: AppRadius.cardRadius,
      border: showBorder ? Border.all(color: c.border) : null,
    );

    final content = Padding(padding: padding, child: child);

    return Padding(
      padding: margin ?? EdgeInsets.zero,
      child: onTap == null
          ? DecoratedBox(decoration: decoration, child: content)
          : Ink(
              decoration: decoration,
              child: InkWell(
                onTap: onTap,
                borderRadius: AppRadius.cardRadius,
                child: content,
              ),
            ),
    );
  }
}

/// عنوان قسم — بيجمع عنوان صغير + إجراء اختياري على الطرف الآخر.
class SectionHeader extends StatelessWidget {
  const SectionHeader({
    super.key,
    required this.title,
    this.actionLabel,
    this.onAction,
  });

  final String title;
  final String? actionLabel;
  final VoidCallback? onAction;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return Padding(
      padding: const EdgeInsets.only(
        top: AppSpacing.xl,
        bottom: AppSpacing.md,
      ),
      child: Row(
        children: [
          Expanded(
            child: Text(
              title,
              style: context.textStyles.titleMedium?.copyWith(
                fontSize: 13,
                fontWeight: FontWeight.w600,
                color: c.ink70,
              ),
            ),
          ),
          if (actionLabel != null && onAction != null)
            TextButton(
              onPressed: onAction,
              style: TextButton.styleFrom(
                minimumSize: const Size(0, 36),
                padding: EdgeInsets.zero,
                textStyle: context.textStyles.labelMedium,
              ),
              child: Text(actionLabel!),
            ),
        ],
      ),
    );
  }
}

/// سهم «للأمام» يتقلب تلقائيًا في RTL.
///
/// أيقونات Material لا تنعكس تلقائيًا مع الاتجاه، فبنلفّها ١٨٠ درجة.
class ForwardChevron extends StatelessWidget {
  const ForwardChevron({
    super.key,
    this.color,
    this.size = 20,
  });

  final Color? color;
  final double size;

  @override
  Widget build(BuildContext context) {
    final isRtl = Directionality.of(context) == TextDirection.rtl;
    return RotatedBox(
      quarterTurns: isRtl ? 2 : 0,
      child: Icon(Icons.chevron_right, size: size, color: color),
    );
  }
}

/// صف إعدادات — أيقونة + عنوان + (وصف/قيمة) + سهم.
class AtharSettingsTile extends StatelessWidget {
  const AtharSettingsTile({
    super.key,
    required this.icon,
    required this.title,
    this.subtitle,
    this.value,
    this.onTap,
    this.trailing,
    this.iconColor,
  });

  final IconData icon;
  final String title;
  final String? subtitle;

  /// القيمة الحالية (تُعرض على الطرف الآخر قبل السهم).
  final String? value;

  final VoidCallback? onTap;
  final Widget? trailing;
  final Color? iconColor;

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
            Container(
              width: 34,
              height: 34,
              decoration: BoxDecoration(
                color: c.primary100,
                borderRadius: BorderRadius.circular(10),
              ),
              child: Icon(icon, size: 18, color: iconColor ?? c.primary),
            ),
            const HSpace(AppSpacing.md),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: context.textStyles.bodyLarge?.copyWith(
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  if (subtitle != null) ...[
                    const VSpace(2),
                    Text(
                      subtitle!,
                      style: context.textStyles.bodySmall?.copyWith(
                        color: c.ink60,
                        fontSize: 11,
                      ),
                    ),
                  ],
                ],
              ),
            ),
            if (value != null) ...[
              const HSpace(AppSpacing.sm),
              Text(
                value!,
                style: context.textStyles.bodyMedium?.copyWith(
                  color: c.ink60,
                ),
              ),
            ],
            if (trailing != null) ...[
              const HSpace(AppSpacing.sm),
              trailing!,
            ] else if (onTap != null) ...[
              const HSpace(AppSpacing.sm),
              ForwardChevron(color: c.ink40, size: 20),
            ],
          ],
        ),
      ),
    );
  }
}

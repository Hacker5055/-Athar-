import 'package:flutter/material.dart';

import '../theme/app_spacing.dart';
import '../theme/app_theme.dart';
import 'gap.dart';

/// شريحة (chip) قابلة للتحديد — تصميم مخصّص بدل `FilterChip`
/// عشان نتحكم كامل في الشكل (الحدود، الحشو، الحركة).
class AtharChip extends StatelessWidget {
  const AtharChip({
    super.key,
    required this.label,
    this.selected = false,
    this.onTap,
    this.icon,
  });

  final String label;
  final bool selected;
  final ValueChanged<bool>? onTap;
  final IconData? icon;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    final foreground = selected ? c.onPrimary : c.ink70;

    return InkWell(
      onTap: onTap == null ? null : () => onTap!(!selected),
      borderRadius: AppRadius.chipRadius,
      child: AnimatedContainer(
        duration: AppDuration.state,
        curve: AppCurve.standard,
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.md,
          vertical: AppSpacing.sm,
        ),
        decoration: BoxDecoration(
          color: selected ? c.primary : c.surface2,
          borderRadius: AppRadius.chipRadius,
          border: Border.all(color: selected ? c.primary : c.border),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (icon != null) ...[
              Icon(icon, size: 16, color: foreground),
              const HSpace(6),
            ],
            Text(
              label,
              style: context.textStyles.labelMedium?.copyWith(
                color: foreground,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// شارة صغيرة دائرية (تُستخدم للمسافات والزوايا والشارات).
class AtharBadge extends StatelessWidget {
  const AtharBadge({
    super.key,
    required this.label,
    this.color,
    this.textColor,
  });

  final String label;
  final Color? color;
  final Color? textColor;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: color ?? c.accent100,
        borderRadius: AppRadius.chipRadius,
      ),
      child: Text(
        label,
        style: context.textStyles.labelSmall?.copyWith(
          color: textColor ?? c.warning,
          fontSize: 11,
        ),
      ),
    );
  }
}

import 'package:flutter/material.dart';

import '../theme/app_spacing.dart';
import 'gap.dart';

/// أشكال زر أثر.
enum AtharButtonVariant {
  /// الإجراء الأساسي — زمردي مصمت.
  primary,

  /// إجراء ثانوي مؤكد — زمردي فاتح.
  soft,

  /// إجراء ثانوي — حدود فقط.
  outline,

  /// إجراء هامشي — نص فقط.
  text,
}

/// زر موحّد — كل أزرار التطبيق بتمر من هنا.
///
/// القواعد اللي بيضمنها:
/// - حد أدنى 48px للهدف القابل للنقر (راحة الإبهام).
/// - حواف 14px.
/// - حالة تحميل موحّدة.
class AtharButton extends StatelessWidget {
  const AtharButton({
    super.key,
    required this.label,
    this.onPressed,
    this.icon,
    this.variant = AtharButtonVariant.primary,
    this.expand = false,
    this.isLoading = false,
    this.isCompact = false,
  });

  final String label;
  final VoidCallback? onPressed;
  final IconData? icon;
  final AtharButtonVariant variant;
  final bool expand;
  final bool isLoading;
  final bool isCompact;

  @override
  Widget build(BuildContext context) {
    final enabled = !isLoading && onPressed != null;
    final content = _buildContent(context);

    final effectiveHeight = isCompact ? 38.0 : AppSize.minTouchTarget;

    final Widget button;
    switch (variant) {
      case AtharButtonVariant.primary:
        button = ElevatedButton(
          onPressed: enabled ? onPressed : null,
          child: content,
        );
        break;
      case AtharButtonVariant.soft:
        button = FilledButton(
          onPressed: enabled ? onPressed : null,
          child: content,
        );
        break;
      case AtharButtonVariant.outline:
        button = OutlinedButton(
          onPressed: enabled ? onPressed : null,
          child: content,
        );
        break;
      case AtharButtonVariant.text:
        button = TextButton(
          onPressed: enabled ? onPressed : null,
          child: content,
        );
        break;
    }

    final sized = SizedBox(
      height: effectiveHeight,
      width: expand ? double.infinity : null,
      child: button,
    );
    return sized;
  }

  Widget _buildContent(BuildContext context) {
    if (isLoading) {
      return const SizedBox(
        width: 20,
        height: 20,
        child: CircularProgressIndicator(strokeWidth: 2.2),
      );
    }

    final style = Theme.of(context).textTheme.labelLarge;
    final text = Text(label, style: style);

    if (icon == null) return text;

    return Row(
      mainAxisSize: MainAxisSize.min,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(icon, size: 18),
        const HSpace(AppSpacing.sm),
        Flexible(child: text),
      ],
    );
  }
}

/// زر أيقونة مربّع — الحد الأدنى 48×48 افتراضيًا.
class AtharIconButton extends StatelessWidget {
  const AtharIconButton({
    super.key,
    required this.icon,
    required this.tooltip,
    this.onPressed,
    this.size = AppSize.iconButton,
  });

  final IconData icon;
  final String tooltip;
  final VoidCallback? onPressed;
  final double size;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: size,
      height: size,
      child: IconButton(
        onPressed: onPressed,
        tooltip: tooltip,
        icon: Icon(icon, size: 20),
      ),
    );
  }
}

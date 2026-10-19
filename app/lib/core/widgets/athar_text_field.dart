import 'package:flutter/material.dart';

import '../theme/app_spacing.dart';
import '../theme/app_theme.dart';

/// حقل إدخال موحّد — ارتفاع 46، حواف 12، خلفية `surface2`.
class AtharTextField extends StatelessWidget {
  const AtharTextField({
    super.key,
    this.controller,
    this.hint,
    this.prefixIcon,
    this.suffix,
    this.onChanged,
    this.onSubmitted,
    this.keyboardType,
    this.textInputAction,
    this.autofocus = false,
    this.readOnly = false,
    this.onTap,
  });

  final TextEditingController? controller;
  final String? hint;
  final IconData? prefixIcon;
  final Widget? suffix;
  final ValueChanged<String>? onChanged;
  final ValueChanged<String>? onSubmitted;
  final TextInputType? keyboardType;
  final TextInputAction? textInputAction;
  final bool autofocus;
  final bool readOnly;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 46,
      child: TextField(
        controller: controller,
        onChanged: onChanged,
        onSubmitted: onSubmitted,
        onTap: onTap,
        readOnly: readOnly,
        autofocus: autofocus,
        keyboardType: keyboardType,
        textInputAction: textInputAction,
        style: context.textStyles.bodyLarge,
        cursorColor: context.colors.primary,
        textAlignVertical: TextAlignVertical.center,
        decoration: InputDecoration(
          hintText: hint,
          hintStyle: context.textStyles.bodyMedium?.copyWith(
            color: context.colors.ink40,
          ),
          prefixIcon: prefixIcon == null
              ? null
              : Icon(prefixIcon, size: 18, color: context.colors.ink40),
          suffixIcon: suffix,
          isCollapsed: true,
          contentPadding: const EdgeInsets.symmetric(
            horizontal: AppSpacing.md,
            vertical: AppSpacing.md,
          ),
        ),
      ),
    );
  }
}

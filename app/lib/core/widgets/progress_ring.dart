import 'dart:math' as math;

import 'package:flutter/material.dart';

import '../theme/app_spacing.dart';
import '../theme/app_theme.dart';
import 'athar_button.dart';
import 'gap.dart';

/// حلقة تقدم دائرية — بتستخدم في «وردي اليوم» والأذكار.
///
/// [progress] من 0.0 إلى 1.0 (بيتم تثبيته تلقائيًا).
class ProgressRing extends StatelessWidget {
  const ProgressRing({
    super.key,
    required this.progress,
    this.size = 46,
    this.strokeWidth = 4,
    this.color,
    this.trackColor,
    this.child,
  });

  final double progress;
  final double size;
  final double strokeWidth;
  final Color? color;
  final Color? trackColor;
  final Widget? child;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return SizedBox(
      width: size,
      height: size,
      child: CustomPaint(
        painter: _RingPainter(
          progress: progress.clamp(0, 1),
          color: color ?? c.primary,
          trackColor: trackColor ?? c.surface3,
          strokeWidth: strokeWidth,
        ),
        child: Center(child: child),
      ),
    );
  }
}

class _RingPainter extends CustomPainter {
  const _RingPainter({
    required this.progress,
    required this.color,
    required this.trackColor,
    required this.strokeWidth,
  });

  final double progress;
  final Color color;
  final Color trackColor;
  final double strokeWidth;

  @override
  void paint(Canvas canvas, Size size) {
    final center = size.center(Offset.zero);
    final radius = (math.min(size.width, size.height) - strokeWidth) / 2;
    final rect = Rect.fromCircle(center: center, radius: radius);

    const startAngle = -math.pi / 2; // ١٢ اتجاه الساعة

    canvas.drawArc(
      rect,
      startAngle,
      2 * math.pi,
      false,
      Paint()
        ..color = trackColor
        ..style = PaintingStyle.stroke
        ..strokeWidth = strokeWidth
        ..strokeCap = StrokeCap.round,
    );

    if (progress <= 0) return;

    canvas.drawArc(
      rect,
      startAngle,
      2 * math.pi * progress,
      false,
      Paint()
        ..color = color
        ..style = PaintingStyle.stroke
        ..strokeWidth = strokeWidth
        ..strokeCap = StrokeCap.round,
    );
  }

  @override
  bool shouldRepaint(covariant _RingPainter oldDelegate) =>
      oldDelegate.progress != progress ||
      oldDelegate.color != color ||
      oldDelegate.trackColor != trackColor ||
      oldDelegate.strokeWidth != strokeWidth;
}

/// شريط تقدم خطي — للأذكار والختمة.
class ProgressBar extends StatelessWidget {
  const ProgressBar({
    super.key,
    required this.progress,
    this.height = 5,
    this.color,
    this.trackColor,
  });

  final double progress;
  final double height;
  final Color? color;
  final Color? trackColor;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return ClipRRect(
      borderRadius: BorderRadius.circular(height / 2),
      child: SizedBox(
        height: height,
        child: LinearProgressIndicator(
          value: progress.clamp(0, 1),
          backgroundColor: trackColor ?? c.surface3,
          valueColor: AlwaysStoppedAnimation<Color>(color ?? c.primary),
          minHeight: height,
        ),
      ),
    );
  }
}

/// بطاقة «وردي اليوم / أذكاري» — حلقة + عنوان + زر.
class WirdRow extends StatelessWidget {
  const WirdRow({
    super.key,
    required this.title,
    required this.subtitle,
    required this.progress,
    required this.ringLabel,
    this.actionLabel,
    this.onAction,
    this.accentColor,
  });

  final String title;
  final String subtitle;
  final double progress;
  final String ringLabel;
  final String? actionLabel;
  final VoidCallback? onAction;
  final Color? accentColor;

  @override
  Widget build(BuildContext context) {
    final c = context.colors;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppSpacing.md),
      child: Row(
        children: [
          ProgressRing(
            progress: progress,
            color: accentColor ?? c.primary,
            child: Text(
              ringLabel,
              style: context.textStyles.labelSmall?.copyWith(
                fontSize: 10.5,
                fontWeight: FontWeight.w700,
                color: c.ink70,
              ),
            ),
          ),
          const HSpace(AppSpacing.md),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: context.textStyles.bodyLarge?.copyWith(
                    fontWeight: FontWeight.w600,
                    fontSize: 14,
                  ),
                ),
                const VSpace(2),
                Text(
                  subtitle,
                  style: context.textStyles.bodySmall?.copyWith(
                    fontSize: 11.5,
                    color: c.ink60,
                  ),
                ),
              ],
            ),
          ),
          if (actionLabel != null)
            AtharButton(
              label: actionLabel!,
              onPressed: onAction,
              variant: AtharButtonVariant.soft,
              isCompact: true,
            ),
        ],
      ),
    );
  }
}

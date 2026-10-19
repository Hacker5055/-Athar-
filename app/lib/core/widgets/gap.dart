import 'package:flutter/material.dart';

/// مسافة فارغة بسيطة — مفيش `SizedBox` مبعثرة في الشاشات.
class Gap extends StatelessWidget {
  const Gap(this.size, {super.key});

  /// لا مسافة (يُستخدم كفاصل مرن في بعض الحالات).
  const Gap.zero({super.key}) : size = 0;

  /// مسافة حسب [AppSpacing].
  factory Gap.xs({Key? key}) => Gap(4, key: key);
  factory Gap.sm({Key? key}) => Gap(8, key: key);
  factory Gap.md({Key? key}) => Gap(12, key: key);
  factory Gap.lg({Key? key}) => Gap(16, key: key);
  factory Gap.xl({Key? key}) => Gap(20, key: key);
  factory Gap.xxl({Key? key}) => Gap(24, key: key);

  final double size;

  @override
  Widget build(BuildContext context) => SizedBox(width: size, height: size);
}

/// مسافة رأسية فقط.
class VSpace extends StatelessWidget {
  const VSpace(this.size, {super.key});

  final double size;

  @override
  Widget build(BuildContext context) => SizedBox(height: size);
}

/// مسافة أفقية فقط.
class HSpace extends StatelessWidget {
  const HSpace(this.size, {super.key});

  final double size;

  @override
  Widget build(BuildContext context) => SizedBox(width: size);
}

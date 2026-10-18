import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/settings/settings_controller.dart' show stringsProvider;
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/widgets.dart';
import '../../application/quran_import_controller.dart';

/// الحالة الفارغة لتبويب القرآن.
///
/// ### لماذا «فارغ» مش «قريبًا»؟
/// قاعدة البيانات تبدأ فارغة **عن قصد** — نص القرآن لا يُرفق بالتطبيق
/// ولا بالمستودع (قرار ترخيصي، `docs/09` §1). فالشاشة تشرح للمستخدم
/// أن الاستيراد خطوة مقصودة، لا عطلًا ولا ميزة مؤجّلة.
class QuranEmptyState extends ConsumerWidget {
  const QuranEmptyState({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);
    final import = ref.watch(quranImportProvider);
    final controller = ref.read(quranImportProvider.notifier);
    final c = context.colors;

    return Center(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(AppSpacing.screenPaddingH),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 72,
              height: 72,
              decoration: BoxDecoration(
                color: c.primary100,
                borderRadius: BorderRadius.circular(22),
              ),
              child: Icon(Icons.menu_book_outlined, size: 34, color: c.primary600),
            ),
            const VSpace(AppSpacing.lg),
            Text(
              s.quranEmptyTitle,
              textAlign: TextAlign.center,
              style: context.textStyles.titleLarge?.copyWith(color: c.ink),
            ),
            const VSpace(AppSpacing.sm),
            Text(
              s.quranEmptyBody,
              textAlign: TextAlign.center,
              style: context.textStyles.bodyMedium?.copyWith(color: c.ink70),
            ),
            const VSpace(AppSpacing.xl),
            AtharButton(
              label: import.busy ? s.quranImporting : s.quranImportAction,
              onPressed: import.busy ? null : controller.pickAndImport,
              isLoading: import.busy,
            ),

            // ── أخطاء التحقّق (تُعرض كلها، لا أول خطأ فقط) ──
            if (import.errors.isNotEmpty) ...[
              const VSpace(AppSpacing.lg),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(AppSpacing.md),
                decoration: BoxDecoration(
                  color: c.danger.alpha(0.10),
                  borderRadius: BorderRadius.circular(AppSpacing.chip),
                  border: Border.all(color: c.danger.alpha(0.35)),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(Icons.error_outline_rounded,
                            size: 16, color: c.danger),
                        const HSpace(6),
                        Text(
                          s.quranImportFailed,
                          style: context.textStyles.labelLarge?.copyWith(
                            color: c.danger,
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                      ],
                    ),
                    const VSpace(AppSpacing.sm),
                    for (final error in import.errors)
                      Padding(
                        padding: const EdgeInsets.only(bottom: 4),
                        child: Text(
                          '• $error',
                          style: context.textStyles.bodySmall
                              ?.copyWith(color: c.danger),
                        ),
                      ),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

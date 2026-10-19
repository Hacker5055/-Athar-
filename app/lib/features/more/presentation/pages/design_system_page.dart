import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/settings/settings_controller.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/theme/app_typography.dart';
import '../../../../core/theme/quran_theme.dart';
import '../../../../core/widgets/widgets.dart';

/// معرض نظام التصميم.
///
/// الهدف: أي تغيير في الـ tokens يتأكد بصريًا في مكان واحد بدل
/// الفتح في عشر شاشات. مش جزء من التطبيق النهائي (مخفي في «المزيد»).
class DesignSystemPage extends ConsumerWidget {
  const DesignSystemPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final s = ref.watch(stringsProvider);

    return Scaffold(
      appBar: AppBar(title: Text(s.designSystem)),
      body: ListView(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.screenPaddingH,
          vertical: AppSpacing.sm,
        ),
        children: [
          _colorsSection(context, s.dsColors),
          _typographySection(context, s.dsTypography),
          _quranSection(context),
          _spacingSection(context, s.dsSpacing),
          _radiiSection(context, s.dsRadii),
          _buttonsSection(context, s.dsButtons),
          _inputsSection(context, s.dsInputs),
          _feedbackSection(context, s.dsFeedback),
          const VSpace(AppSpacing.huge),
        ],
      ),
    );
  }

  // ── الألوان ─────────────────────────────────────────────
  Widget _colorsSection(BuildContext context, String title) {
    final c = context.colors;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: title),
        Wrap(
          spacing: AppSpacing.sm,
          runSpacing: AppSpacing.sm,
          children: [
            _ColorSwatch('bg', c.bg),
            _ColorSwatch('surface', c.surface),
            _ColorSwatch('surface2', c.surface2),
            _ColorSwatch('surface3', c.surface3),
            _ColorSwatch('primary', c.primary),
            _ColorSwatch('primary600', c.primary600),
            _ColorSwatch('primary100', c.primary100),
            _ColorSwatch('primary200', c.primary200),
            _ColorSwatch('accent', c.accent),
            _ColorSwatch('accent100', c.accent100),
            _ColorSwatch('ink', c.ink),
            _ColorSwatch('ink70', c.ink70),
            _ColorSwatch('ink60', c.ink60),
            _ColorSwatch('ink40', c.ink40),
            _ColorSwatch('border', c.border),
            _ColorSwatch('success', c.success),
            _ColorSwatch('warning', c.warning),
            _ColorSwatch('danger', c.danger),
          ],
        ),
      ],
    );
  }

  // ── الخطوط ──────────────────────────────────────────────
  Widget _typographySection(BuildContext context, String title) {
    final styles = context.textStyles;

    final samples = <(String, TextStyle?)>[
      ('displayLarge 32', styles.displayLarge),
      ('headlineSmall 24', styles.headlineSmall),
      ('titleLarge 20', styles.titleLarge),
      ('titleMedium 17', styles.titleMedium),
      ('bodyLarge 15', styles.bodyLarge),
      ('bodyMedium 14', styles.bodyMedium),
      ('bodySmall 13', styles.bodySmall),
      ('labelSmall 11', styles.labelSmall),
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: title),
        ...samples.map(
          (sample) => Padding(
            padding: const EdgeInsets.only(bottom: AppSpacing.sm),
            child: Text(
              '${sample.$1} — أثر Athar',
              style: sample.$2,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ),
      ],
    );
  }

  // ── النص القرآني ────────────────────────────────────────
  Widget _quranSection(BuildContext context) {
    const sample =
        'بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ';
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: 'Quran text'),
        Row(
          children: QuranPageTheme.values.map((theme) {
            final colors = theme.colors;
            return Expanded(
              child: Container(
                margin: const EdgeInsetsDirectional.only(end: AppSpacing.sm),
                padding: const EdgeInsets.all(AppSpacing.md),
                decoration: BoxDecoration(
                  color: colors.background,
                  borderRadius: AppRadius.chipRadius,
                  border: Border.all(color: context.colors.border),
                ),
                child: Column(
                  children: [
                    Text(
                      sample,
                      textAlign: TextAlign.center,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: AppTypography.quranStyle(
                        color: colors.text,
                        fontSize: 15,
                      ),
                    ),
                    const VSpace(AppSpacing.sm),
                    Text(
                      theme.name,
                      style: context.textStyles.labelSmall?.copyWith(
                        color: colors.accent,
                        fontSize: 10,
                      ),
                    ),
                  ],
                ),
              ),
            );
          }).toList(),
        ),
      ],
    );
  }

  // ── المسافات ────────────────────────────────────────────
  Widget _spacingSection(BuildContext context, String title) {
    const values = <double>[4, 8, 12, 16, 20, 24, 32, 48];
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: title),
        ...values.map(
          (v) => Padding(
            padding: const EdgeInsets.only(bottom: AppSpacing.xs),
            child: Row(
              children: [
                SizedBox(
                  width: 28,
                  child: Text(
                    '${v.toInt()}',
                    style: context.textStyles.labelSmall,
                  ),
                ),
                Container(
                  width: v,
                  height: 12,
                  color: context.colors.primary,
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  // ── الحواف ──────────────────────────────────────────────
  Widget _radiiSection(BuildContext context, String title) {
    const values = <double>[12, 14, 20, 24];
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: title),
        Row(
          children: values
              .map(
                (r) => Padding(
                  padding: const EdgeInsetsDirectional.only(end: AppSpacing.md),
                  child: Container(
                    width: 56,
                    height: 56,
                    decoration: BoxDecoration(
                      color: context.colors.surface3,
                      borderRadius: BorderRadius.circular(r),
                      border: Border.all(color: context.colors.border),
                    ),
                    child: Center(
                      child: Text(
                        '${r.toInt()}',
                        style: context.textStyles.labelSmall,
                      ),
                    ),
                  ),
                ),
              )
              .toList(),
        ),
      ],
    );
  }

  // ── الأزرار ─────────────────────────────────────────────
  Widget _buttonsSection(BuildContext context, String title) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: title),
        Wrap(
          spacing: AppSpacing.md,
          runSpacing: AppSpacing.md,
          children: const [
            AtharButton(
              label: 'أساسي',
              onPressed: null,
              variant: AtharButtonVariant.primary,
            ),
            AtharButton(
              label: 'ثانوي',
              onPressed: null,
              variant: AtharButtonVariant.soft,
            ),
            AtharButton(
              label: 'بحدود',
              onPressed: null,
              variant: AtharButtonVariant.outline,
            ),
            AtharButton(
              label: 'نصي',
              onPressed: null,
              variant: AtharButtonVariant.text,
            ),
            AtharButton(
              label: 'تحميل',
              onPressed: null,
              isLoading: true,
            ),
            AtharButton(
              label: 'مع أيقونة',
              onPressed: null,
              icon: Icons.bookmark_outline,
            ),
          ],
        ),
        const VSpace(AppSpacing.md),
        const AtharButton(
          label: 'بعرض الشاشة',
          onPressed: null,
          expand: true,
        ),
      ],
    );
  }

  // ── حقول الإدخال والشرائح ───────────────────────────────
  Widget _inputsSection(BuildContext context, String title) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: title),
        AtharTextField(
          hint: 'ابحث…',
          prefixIcon: Icons.search,
          readOnly: true,
        ),
        const VSpace(AppSpacing.md),
        Wrap(
          spacing: AppSpacing.sm,
          children: const [
            AtharChip(label: 'الكل', selected: true),
            AtharChip(label: 'مكية'),
            AtharChip(label: 'مدنية'),
            AtharChip(label: 'بأيقونة', icon: Icons.star_border),
          ],
        ),
        const VSpace(AppSpacing.md),
        Row(
          children: const [
            AtharBadge(label: '١٢٨٧ كم'),
            HSpace(AppSpacing.sm),
            AtharBadge(label: 'أسبوع ٢'),
          ],
        ),
      ],
    );
  }

  // ── التغذية الراجعة والتقدم ─────────────────────────────
  Widget _feedbackSection(BuildContext context, String title) {
    final c = context.colors;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: title),
        Row(
          children: [
            ProgressRing(
              progress: 0.25,
              child: Text(
                '١/٤',
                style: context.textStyles.labelSmall?.copyWith(
                  fontSize: 10.5,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ),
            const HSpace(AppSpacing.md),
            ProgressRing(
              progress: 0.7,
              color: c.accent,
              child: Text(
                '٧٠٪',
                style: context.textStyles.labelSmall?.copyWith(
                  fontSize: 10.5,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ),
            const HSpace(AppSpacing.lg),
            Expanded(
              child: Column(
                children: const [
                  ProgressBar(progress: 0.4),
                  VSpace(AppSpacing.sm),
                  ProgressBar(progress: 0.85, color: null),
                ],
              ),
            ),
          ],
        ),
        const VSpace(AppSpacing.lg),
        AtharCard(
          child: AtharEmptyState(
            compact: true,
            icon: Icons.bookmark_outline,
            title: 'لا توجد علامات بعد',
            message: 'اضغط مطوّلًا على أي آية لحفظها هنا',
          ),
        ),
        const VSpace(AppSpacing.md),
        Wrap(
          spacing: AppSpacing.md,
          children: [
            AtharButton(
              label: 'ورقة سفلية',
              variant: AtharButtonVariant.outline,
              onPressed: () => showAtharSheet<void>(
                context: context,
                title: 'عنوان الورقة',
                subtitle: 'وصف فرعي توضيحي',
                body: const Text('محتوى الورقة السفلية'),
              ),
            ),
            AtharButton(
              label: 'رسالة',
              variant: AtharButtonVariant.outline,
              onPressed: () => ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('تم الحفظ')),
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class _ColorSwatch extends StatelessWidget {
  const _ColorSwatch(this.name, this.color);

  final String name;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 74,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            height: 44,
            decoration: BoxDecoration(
              color: color,
              borderRadius: AppRadius.chipRadius,
              border: Border.all(color: context.colors.border),
            ),
          ),
          const VSpace(4),
          Text(
            name,
            style: context.textStyles.labelSmall?.copyWith(fontSize: 9.5),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }
}

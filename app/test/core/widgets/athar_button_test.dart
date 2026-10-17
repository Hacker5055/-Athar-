import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/theme/app_theme.dart';
import 'package:athar/core/widgets/widgets.dart';

void main() {
  Widget buildApp(Widget child) {
    return MaterialApp(
      theme: AppTheme.light(),
      darkTheme: AppTheme.dark(),
      home: Directionality(
        textDirection: TextDirection.rtl,
        child: Scaffold(body: Center(child: child)),
      ),
    );
  }

  group('AtharButton', () {
    testWidgets('يعرض النص ويستجيب للنقر', (tester) async {
      var taps = 0;

      await tester.pumpWidget(
        buildApp(
          AtharButton(label: 'أساسي', onPressed: () => taps++),
        ),
      );

      expect(find.text('أساسي'), findsOneWidget);
      await tester.tap(find.text('أساسي'));
      await tester.pumpAndSettle();

      expect(taps, 1);
    });

    testWidgets('الزر المعطّل لا يستجيب', (tester) async {
      var taps = 0;

      await tester.pumpWidget(
        buildApp(
          AtharButton(label: 'معطّل', onPressed: () => taps++),
        ),
      );

      await tester.pumpWidget(
        buildApp(
          const AtharButton(label: 'معطّل', onPressed: null),
        ),
      );

      await tester.tap(find.text('معطّل'));
      await tester.pumpAndSettle();

      expect(taps, 0);
    });

    testWidgets('حالة التحميل تخفي النص', (tester) async {
      await tester.pumpWidget(
        buildApp(
          const AtharButton(
            label: 'مخفي',
            onPressed: null,
            isLoading: true,
          ),
        ),
      );

      expect(find.text('مخفي'), findsNothing);
      expect(find.byType(CircularProgressIndicator), findsOneWidget);
    });

    testWidgets('الهدف القابل للنقر لا يقل عن 48px', (tester) async {
      await tester.pumpWidget(
        buildApp(
          AtharButton(label: 'حجم', onPressed: () {}),
        ),
      );

      final size = tester.getSize(find.byType(AtharButton));
      expect(size.height, greaterThanOrEqualTo(48));
    });
  });

  group('ProgressRing', () {
    testWidgets('يرسم بدون أخطاء عند progress = 0 و 1', (tester) async {
      for (final value in [0.0, 0.5, 1.0]) {
        await tester.pumpWidget(
          buildApp(ProgressRing(progress: value)),
        );
        expect(tester.takeException(), isNull);
      }
    });

    testWidgets('يثبّت القيم خارج النطاق', (tester) async {
      await tester.pumpWidget(
        buildApp(const ProgressRing(progress: 2.5)),
      );
      expect(tester.takeException(), isNull);
    });
  });
}

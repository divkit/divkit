import 'dart:async';

import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/div_visibility_emitter.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:visibility_detector/visibility_detector.dart';

void main() {
  group(
    'DivVisibilityEmitter test divVisibility != visible',
    () {
      testWidgets(
        'If divVisibility != visible action will not be executed',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          bool visible = false;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {
                visible = isVisible;
              },
              DivVisibility.invisible,
            ),
          );
          expect(visible, false);
        },
      );
      testWidgets(
        'If divVisibility != visible then VisibilityDetector not in the tree',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {},
              DivVisibility.invisible,
            ),
          );
          expect(find.byType(VisibilityDetector), findsNothing);
        },
      );
      testWidgets(
        'If divVisibility != visible then text "Visible" not shown',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {},
              DivVisibility.invisible,
            ),
          );
          expect(find.text('Visible'), findsNothing);
        },
      );
    },
  );

  group(
    'DivVisibilityEmitter test divVisibility == visible',
    () {
      testWidgets(
        'If divVisibility == visible and waited visibilityDuration - action will be executed',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          bool visible = false;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {
                visible = isVisible;
              },
              DivVisibility.visible,
            ),
          );
          await tester.pump(
            const Duration(seconds: 1),
          );
          expect(visible, true);
        },
      );
      testWidgets(
        'If divVisibility == visible and not waited visibilityDuration - action will be executed',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          bool visible = false;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {
                visible = isVisible;
              },
              DivVisibility.visible,
            ),
          );
          expect(visible, false);
        },
      );
      testWidgets(
        'If divVisibility == visible and waited visibilityDuration and current visibilityPercentage < expected visibilityPercentage - action will not be executed',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          bool visible = false;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {
                visible = isVisible;
              },
              DivVisibility.visible,
              paddingTop: 550,
            ),
          );
          await tester.pump(
            const Duration(seconds: 1),
          );
          expect(visible, false);
        },
      );
      testWidgets(
        'If divVisibility == visible then VisibilityDetector in the tree',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {},
              DivVisibility.visible,
            ),
          );
          expect(find.byType(VisibilityDetector), findsOneWidget);
        },
      );
      testWidgets(
        'If divVisibility == visible then text "Visible" shown',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {},
              DivVisibility.visible,
            ),
          );
          expect(find.text('Visible'), findsOneWidget);
        },
      );
    },
  );
  group(
    'DivVisibilityEmitter test action list empty',
    () {
      testWidgets(
        'If action list empty action will not be executed',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          bool visible = false;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {
                visible = isVisible;
              },
              DivVisibility.visible,
              actions: const [],
            ),
          );
          await tester.pump(
            const Duration(seconds: 1),
          );
          expect(visible, false);
        },
      );
      testWidgets(
        'If action list empty then VisibilityDetector not in the tree',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {},
              actions: const [],
              DivVisibility.visible,
            ),
          );
          await tester.pump(
            const Duration(seconds: 1),
          );
          expect(find.byType(VisibilityDetector), findsNothing);
        },
      );
      testWidgets(
        'If action list empty then text "Visible" shown',
        (WidgetTester tester) async {
          VisibilityDetectorController.instance.updateInterval = Duration.zero;
          await tester.pumpWidget(
            _MyApp(
              (isVisible) {},
              actions: const [],
              DivVisibility.visible,
            ),
          );
          await tester.pump(
            const Duration(seconds: 1),
          );
          expect(find.text('Visible'), findsOneWidget);
        },
      );
    },
  );
}

class _MyApp extends StatelessWidget {
  final Function(bool isVisible) updateVisible;
  final DivVisibility divVisibility;
  final double paddingTop;
  final List<DivVisibilityActionModel>? actions;

  const _MyApp(
    this.updateVisible,
    this.divVisibility, {
    this.actions,
    this.paddingTop = 0,
  });

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: DivKitProvider<DivContext>(
        value: DivRootContext(context)
          ..visibilityActionManager = DefaultDivVisibilityActionManager()
          ..actionHandler = _TestDivActionHandler(updateVisible),
        child: Scaffold(
          body: ListView(
            children: <Widget>[
              SizedBox(
                height: paddingTop,
              ),
              DivVisibilityEmitter(
                divVisibility: divVisibility,
                id: "id",
                visibilityActions: actions ??
                    [
                      const DivVisibilityActionModel(
                        divAction: DivActionModel(
                          enabled: true,
                          logId: "id",
                        ),
                        visibilityPercentage: 50,
                        visibilityDuration: 1000,
                      ),
                    ],
                child: const SizedBox(
                  height: 200,
                  child: Center(
                    child: Text('Visible'),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _TestDivActionHandler implements DivActionHandler {
  final Function(bool isVisible) isVisible;

  const _TestDivActionHandler(
    this.isVisible,
  );

  @override
  bool canHandle(DivContext context, DivActionModel action) => true;

  @override
  FutureOr<bool> handleAction(DivContext context, DivActionModel action) {
    isVisible(true);
    return true;
  }
}

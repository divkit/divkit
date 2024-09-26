import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:golden_toolkit/golden_toolkit.dart';
import 'package:meta/meta.dart';

import 'screen_matches_golden.dart';

@isTest
Future<void> goldenTestForCI({
  required String description,
  required String testFileName,
  required Device device,
  required ValueGetter<Widget> builder,
}) async {
  testGoldens(
    description,
    (WidgetTester tester) async {
      final constraints = BoxConstraints(
        maxHeight: device.size.height,
        maxWidth: device.size.width,
      );

      final childKey = UniqueKey();
      await tester.pumpWidgetBuilder(
        Center(
          child: OverflowBox(
            minWidth: constraints.minWidth,
            minHeight: constraints.minHeight,
            maxWidth: constraints.maxWidth,
            maxHeight: constraints.maxHeight,
            child: Center(
              child: KeyedSubtree(
                key: childKey,
                child: builder(),
              ),
            ),
          ),
        ),
        wrapper: (child) => MaterialApp(
          supportedLocales: const [Locale('en')],
          debugShowCheckedModeBanner: false,
          home: child,
        ),
      );
      await tester.pumpAndSettle();

      final childSize = tester.getSize(
        find.firstDescendant(of: find.byKey(childKey)),
      );

      try {
        await tester.binding.setSurfaceSize(childSize);
        tester.binding.window.physicalSizeTestValue = childSize;
        tester.binding.window.devicePixelRatioTestValue =
            device.devicePixelRatio;

        await enhancedScreenMatchesGolden(
          tester,
          testFileName,
        );
      } finally {
        await tester.binding.setSurfaceSize(null);
        tester.binding.window.clearPhysicalSizeTestValue();
        tester.binding.window.clearDevicePixelRatioTestValue();
      }
    },
  );
}

extension on CommonFinders {
  Finder firstDescendant({
    required Finder of,
    bool skipOffstage = true,
  }) {
    final acenstor = of;
    final Iterable<Element> ancestorElements = acenstor.evaluate();
    final List<Element> candidates = ancestorElements
        .expand<Element>(
          (Element element) =>
              collectAllElementsFrom(element, skipOffstage: skipOffstage),
        )
        .toSet()
        .toList();
    final builtChild = candidates.first;
    return find.byElementPredicate((element) => element == builtChild);
  }
}

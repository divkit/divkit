import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:golden_toolkit/golden_toolkit.dart';

extension AutoSizeWidgetTesterExtension on WidgetTester {
  /// Pumps a widget and adjusts screen size to its size.
  Future<void> pumpAutoSizeWidgetBuilder({
    required ValueGetter<Widget> builder,
    WidgetWrapper? wrapper,
    Device? device,
  }) async {
    final constraints = device == null
        ? null
        : BoxConstraints(
            maxHeight: device.size.height,
            maxWidth: device.size.width,
          );

    final childKey = UniqueKey();

    await pumpWidgetBuilder(
      Center(
        child: OverflowBox(
          minWidth: constraints?.minWidth,
          minHeight: constraints?.minHeight,
          maxWidth: constraints?.maxWidth,
          maxHeight: constraints?.maxHeight,
          child: Center(
            child: KeyedSubtree(
              key: childKey,
              child: builder(),
            ),
          ),
        ),
      ),
      wrapper: (child) => Builder(
        builder: (context) {
          WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
            // A hack, until https://github.com/eBay/flutter_glove_box/pull/193 is merged
            binding.window.devicePixelRatioTestValue =
                device?.devicePixelRatio ?? 1;
          });

          return wrapper?.call(child) ?? materialAppWrapper()(child);
        },
      ),
    );
    await pumpAndSettle();

    final childSize = getSize(
      find.firstDescendant(of: find.byKey(childKey)),
    );

    addTearDown(() async {
      await binding.setSurfaceSize(null);
      binding.window.clearPhysicalSizeTestValue();
      binding.window.clearDevicePixelRatioTestValue();
    });

    await binding.setSurfaceSize(childSize);
    binding.window.physicalSizeTestValue = childSize;
  }
}

extension AutoSizeGoldenBuilderExtension on GoldenBuilder {
  Widget buildForAutoSize([Color? bgColor]) => Container(
        padding: const EdgeInsets.all(8),
        color: bgColor ?? this.bgColor ?? const Color(0xFFEEEEEE),
        child: columns == 1 ? _column() : _grid(),
      );

  GridView _grid() => GridView.count(
        childAspectRatio: widthToHeightRatio,
        crossAxisSpacing: 16,
        mainAxisSpacing: 16,
        shrinkWrap: true,
        crossAxisCount: columns,
        children: scenarios,
      );

  Column _column() =>
      Column(mainAxisSize: MainAxisSize.min, children: scenarios);
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
          (element) =>
              collectAllElementsFrom(element, skipOffstage: skipOffstage),
        )
        .toSet()
        .toList();
    final builtChild = candidates.first;
    return find.byElementPredicate((element) => element == builtChild);
  }
}

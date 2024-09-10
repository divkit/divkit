import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

const _defaultCrossAxisAlignment = CrossAxisAlignment.center;
const _defaultMainAxisAlignment = MainAxisAlignment.start;
const _defaultWrapAlignment = WrapAlignment.start;

extension PassContentAlignmentVertical on DivContentAlignmentVertical {
  MainAxisAlignment get asMainAxisAlignment => map(
        top: () => MainAxisAlignment.start,
        center: () => MainAxisAlignment.center,
        bottom: () => MainAxisAlignment.end,
        spaceBetween: () => MainAxisAlignment.spaceBetween,
        spaceAround: () => MainAxisAlignment.spaceAround,
        spaceEvenly: () => MainAxisAlignment.spaceEvenly,
        baseline: () => _defaultMainAxisAlignment,
      );

  CrossAxisAlignment get asCrossAxisAlignment => map(
        top: () => CrossAxisAlignment.start,
        center: () => CrossAxisAlignment.center,
        bottom: () => CrossAxisAlignment.end,
        spaceBetween: () => _defaultCrossAxisAlignment,
        spaceAround: () => _defaultCrossAxisAlignment,
        spaceEvenly: () => _defaultCrossAxisAlignment,
        baseline: () => CrossAxisAlignment.baseline,
      );

  WrapAlignment get asWrapAlignment => map(
        top: () => WrapAlignment.start,
        center: () => WrapAlignment.center,
        bottom: () => WrapAlignment.end,
        spaceBetween: () => WrapAlignment.spaceBetween,
        spaceAround: () => WrapAlignment.spaceAround,
        spaceEvenly: () => WrapAlignment.spaceEvenly,
        baseline: () => _defaultWrapAlignment,
      );
}

extension PassContentAlignmentHorizontal on DivContentAlignmentHorizontal {
  MainAxisAlignment get asMainAxisAlignment => map(
        // TODO: support baseline alignment in flutter
        left: () => MainAxisAlignment.start,
        right: () => MainAxisAlignment.end,
        start: () => MainAxisAlignment.start,
        end: () => MainAxisAlignment.end,
        center: () => MainAxisAlignment.center,
        spaceBetween: () => MainAxisAlignment.spaceBetween,
        spaceAround: () => MainAxisAlignment.spaceAround,
        spaceEvenly: () => MainAxisAlignment.spaceEvenly,
      );

  CrossAxisAlignment get asCrossAxisAlignment => map(
        // TODO: support baseline alignment in flutter
        left: () => CrossAxisAlignment.start,
        center: () => CrossAxisAlignment.center,
        right: () => CrossAxisAlignment.end,
        start: () => CrossAxisAlignment.start,
        end: () => CrossAxisAlignment.end,
        spaceBetween: () => _defaultCrossAxisAlignment,
        spaceAround: () => _defaultCrossAxisAlignment,
        spaceEvenly: () => _defaultCrossAxisAlignment,
      );

  WrapAlignment get asWrapAlignment => map(
        left: () => WrapAlignment.start,
        center: () => WrapAlignment.center,
        right: () => WrapAlignment.end,
        start: () => WrapAlignment.start,
        end: () => WrapAlignment.end,
        spaceBetween: () => WrapAlignment.spaceBetween,
        spaceAround: () => WrapAlignment.spaceAround,
        spaceEvenly: () => WrapAlignment.spaceEvenly,
      );
}

abstract class ContentAlignment {
  const ContentAlignment();

  T map<T>({
    required T Function(FlexContentAlignment data) flex,
    required T Function(WrapContentAlignment data) wrap,
    required T Function(StackContentAlignment data) stack,
  });
}

class FlexContentAlignment extends ContentAlignment with EquatableMixin {
  final Axis direction;

  final CrossAxisAlignment crossAxisAlignment;
  final MainAxisAlignment mainAxisAlignment;

  const FlexContentAlignment({
    required this.direction,
    required this.crossAxisAlignment,
    required this.mainAxisAlignment,
  });

  @override
  T map<T>({
    required T Function(FlexContentAlignment data) flex,
    required T Function(WrapContentAlignment data) wrap,
    required T Function(StackContentAlignment data) stack,
  }) =>
      flex(this);

  @override
  List<Object?> get props => [direction, crossAxisAlignment, mainAxisAlignment];
}

class WrapContentAlignment extends ContentAlignment with EquatableMixin {
  final Axis direction;

  final WrapAlignment wrapAlignment;
  final WrapAlignment runAlignment;

  const WrapContentAlignment({
    required this.direction,
    required this.wrapAlignment,
    required this.runAlignment,
  });

  @override
  T map<T>({
    required T Function(FlexContentAlignment data) flex,
    required T Function(WrapContentAlignment data) wrap,
    required T Function(StackContentAlignment data) stack,
  }) =>
      wrap(this);

  @override
  List<Object?> get props => [direction, wrapAlignment, runAlignment];
}

class StackContentAlignment extends ContentAlignment with EquatableMixin {
  final AlignmentGeometry? contentAlignment;

  const StackContentAlignment({
    required this.contentAlignment,
  });

  @override
  T map<T>({
    required T Function(FlexContentAlignment data) flex,
    required T Function(WrapContentAlignment data) wrap,
    required T Function(StackContentAlignment data) stack,
  }) =>
      stack(this);

  @override
  List<Object?> get props => [contentAlignment];
}

class PassDivContentAlignment {
  final Expression<DivContainerOrientation> orientation;
  final Expression<DivContentAlignmentVertical> vertical;
  final Expression<DivContentAlignmentHorizontal> horizontal;
  final Expression<DivContainerLayoutMode> layoutMode;

  const PassDivContentAlignment(
    this.orientation,
    this.vertical,
    this.horizontal,
    this.layoutMode,
  );

  Future<ContentAlignment> resolve({
    required DivVariableContext context,
  }) async {
    final safeVertical = vertical;
    final safeHorizontal = horizontal;
    final resolvedOrientation =
        await orientation.resolveValue(context: context);

    final divVertical = await safeVertical.resolveValue(context: context);
    final divHorizontal = await safeHorizontal.resolveValue(context: context);
    final isWrap = await layoutMode.resolveValue(context: context) ==
        DivContainerLayoutMode.wrap;

    return resolvedOrientation.map(
      vertical: () {
        if (isWrap) {
          return WrapContentAlignment(
            direction: Axis.vertical,
            wrapAlignment: divVertical.asWrapAlignment,
            runAlignment: divHorizontal.asWrapAlignment,
          );
        }
        return FlexContentAlignment(
          direction: Axis.vertical,
          mainAxisAlignment: divVertical.asMainAxisAlignment,
          crossAxisAlignment: divHorizontal.asCrossAxisAlignment,
        );
      },
      horizontal: () {
        if (isWrap) {
          return WrapContentAlignment(
            direction: Axis.horizontal,
            wrapAlignment: divHorizontal.asWrapAlignment,
            runAlignment: divVertical.asWrapAlignment,
          );
        }
        return FlexContentAlignment(
          direction: Axis.horizontal,
          mainAxisAlignment: divHorizontal.asMainAxisAlignment,
          crossAxisAlignment: divVertical.asCrossAxisAlignment,
        );
      },
      overlap: () => StackContentAlignment(
        contentAlignment: divHorizontal.map(
          left: () => divVertical.map(
            top: () => Alignment.topLeft,
            center: () => Alignment.centerLeft,
            bottom: () => Alignment.bottomLeft,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          center: () => divVertical.map(
            top: () => Alignment.topCenter,
            center: () => Alignment.center,
            bottom: () => Alignment.bottomCenter,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          right: () => divVertical.map(
            top: () => Alignment.topRight,
            center: () => Alignment.centerLeft,
            bottom: () => Alignment.bottomRight,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          start: () => divVertical.map(
            top: () => AlignmentDirectional.topStart,
            center: () => AlignmentDirectional.centerStart,
            bottom: () => AlignmentDirectional.bottomStart,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          end: () => divVertical.map(
            top: () => AlignmentDirectional.topEnd,
            center: () => AlignmentDirectional.centerEnd,
            bottom: () => AlignmentDirectional.bottomEnd,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          spaceBetween: () => null,
          spaceAround: () => null,
          spaceEvenly: () => null,
        ),
      ),
    );
  }

  ContentAlignment get requireValue {
    final safeVertical = vertical;
    final safeHorizontal = horizontal;
    final resolvedOrientation = orientation.requireValue;

    final divVertical = safeVertical.requireValue;
    final divHorizontal = safeHorizontal.requireValue;

    final isWrap = layoutMode.requireValue == DivContainerLayoutMode.wrap;

    return resolvedOrientation.map(
      vertical: () {
        if (isWrap) {
          return WrapContentAlignment(
            direction: Axis.vertical,
            wrapAlignment: divVertical.asWrapAlignment,
            runAlignment: divHorizontal.asWrapAlignment,
          );
        }
        return FlexContentAlignment(
          direction: Axis.vertical,
          mainAxisAlignment: divVertical.asMainAxisAlignment,
          crossAxisAlignment: divHorizontal.asCrossAxisAlignment,
        );
      },
      horizontal: () {
        if (isWrap) {
          return WrapContentAlignment(
            direction: Axis.horizontal,
            wrapAlignment: divHorizontal.asWrapAlignment,
            runAlignment: divVertical.asWrapAlignment,
          );
        }
        return FlexContentAlignment(
          direction: Axis.horizontal,
          mainAxisAlignment: divHorizontal.asMainAxisAlignment,
          crossAxisAlignment: divVertical.asCrossAxisAlignment,
        );
      },
      overlap: () => StackContentAlignment(
        contentAlignment: divHorizontal.map(
          left: () => divVertical.map(
            top: () => Alignment.topLeft,
            center: () => Alignment.centerLeft,
            bottom: () => Alignment.bottomLeft,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          center: () => divVertical.map(
            top: () => Alignment.topCenter,
            center: () => Alignment.center,
            bottom: () => Alignment.bottomCenter,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          right: () => divVertical.map(
            top: () => Alignment.topRight,
            center: () => Alignment.centerLeft,
            bottom: () => Alignment.bottomRight,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          start: () => divVertical.map(
            top: () => AlignmentDirectional.topStart,
            center: () => AlignmentDirectional.centerStart,
            bottom: () => AlignmentDirectional.bottomStart,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          end: () => divVertical.map(
            top: () => AlignmentDirectional.topEnd,
            center: () => AlignmentDirectional.centerEnd,
            bottom: () => AlignmentDirectional.bottomEnd,
            baseline: () => null,
            spaceBetween: () => null,
            spaceAround: () => null,
            spaceEvenly: () => null,
          ),
          spaceBetween: () => null,
          spaceAround: () => null,
          spaceEvenly: () => null,
        ),
      ),
    );
  }
}

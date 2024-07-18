import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/generated_sources/div_size.dart';
import 'package:divkit/src/utils/content_alignment_converters.dart';
import 'package:divkit/src/utils/converters.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';

abstract class PassDivSize {
  const PassDivSize();

  T map<T>({
    required T Function(FixedDivSize) fixed,
    required T Function(FlexDivSize) flex,
    required T Function(WrapDivSize) wrapped,
  }) {
    final value = this;
    if (value is FixedDivSize) {
      return fixed(value);
    }
    if (value is FlexDivSize) {
      return flex(value);
    }
    if (value is WrapDivSize) {
      return wrapped(value);
    }
    throw Exception("Unsupported ContentAlignment type");
  }

  double? get maxConstraint => map(
        fixed: (fixed) => fixed.size,
        flex: (_) => null,
        wrapped: (wrapped) => wrapped.maxSize?.size ?? double.infinity,
      );

  double? get minConstraint => map(
        fixed: (fixed) => fixed.size,
        flex: (_) => null,
        wrapped: (wrapped) => wrapped.minSize?.size ?? 0,
      );
}

class FixedDivSize extends PassDivSize {
  final double size;

  const FixedDivSize(this.size);
}

class FlexDivSize extends PassDivSize {
  final int? weight;

  const FlexDivSize(this.weight);
}

class WrapDivSize extends PassDivSize {
  final bool? constrained;
  final FixedDivSize? maxSize;
  final FixedDivSize? minSize;

  const WrapDivSize(
    this.constrained, {
    required this.maxSize,
    required this.minSize,
  });
}

extension PassDivSizeImpl on DivSize {
  Future<PassDivSize> resolve({
    required DivVariableContext context,
    required double viewScale,
    double extension = 0,
  }) async =>
      map(
        divFixedSize: (fixed) async => FixedDivSize(
          await fixed.resolveDimension(
                context: context,
                viewScale: viewScale,
              ) +
              extension * viewScale,
        ),
        divMatchParentSize: (flex) async => FlexDivSize(
          (await flex.weight?.resolveValue(context: context))?.toInt(),
        ),
        divWrapContentSize: (wrapped) async {
          final FixedDivSize? parsedMax, parsedMin;
          final maxSize = wrapped.maxSize;
          final minSize = wrapped.minSize;
          if (maxSize != null) {
            parsedMax = FixedDivSize(
              await maxSize.resolveDimension(
                    context: context,
                    viewScale: viewScale,
                  ) +
                  extension * viewScale,
            );
          } else {
            parsedMax = null;
          }
          if (minSize != null) {
            parsedMin = FixedDivSize(
              await minSize.resolveDimension(
                    context: context,
                    viewScale: viewScale,
                  ) +
                  extension * viewScale,
            );
          } else {
            parsedMin = null;
          }

          return WrapDivSize(
            await wrapped.constrained?.resolveValue(context: context),
            maxSize: parsedMax,
            minSize: parsedMin,
          );
        },
      );
}

abstract class _AxisWrapper extends StatelessWidget {
  Axis get primaryAxis;

  DivAxisAlignment? get alignment;

  ContentAlignment? get contentAlignment;

  PassDivSize get sizeOnCrossAxis;

  bool get canExpand {
    final contentAlignment = this.contentAlignment;
    return alignment != null &&
        (contentAlignment is! FlexContentAlignment ||
            contentAlignment.direction != primaryAxis);
  }

  int? get flex {
    final contentAlignment = this.contentAlignment;
    if (contentAlignment is FlexContentAlignment) {
      return contentAlignment.direction == primaryAxis ||
              sizeOnCrossAxis is FixedDivSize
          ? 0
          : 1;
    }
    return null;
  }

  const _AxisWrapper();
}

class _HorizontalWrapper extends _AxisWrapper {
  @override
  final primaryAxis = Axis.horizontal;
  @override
  final DivAxisAlignment? alignment;
  @override
  final ContentAlignment? contentAlignment;
  @override
  final PassDivSize sizeOnCrossAxis;

  final Widget child;

  const _HorizontalWrapper({
    required this.child,
    required this.alignment,
    required this.contentAlignment,
    required this.sizeOnCrossAxis,
  });

  @override
  Widget build(BuildContext context) {
    final row = Row(
      mainAxisAlignment: alignment?.asMainAxis ?? MainAxisAlignment.start,
      mainAxisSize: canExpand ? MainAxisSize.max : MainAxisSize.min,
      children: [child],
    );

    final flex = super.flex;
    if (flex != null) {
      return Flexible(
        flex: flex,
        child: row,
      );
    }

    return row;
  }
}

class _VerticalWrapper extends _AxisWrapper {
  @override
  final primaryAxis = Axis.vertical;
  @override
  final DivAxisAlignment? alignment;
  @override
  final ContentAlignment? contentAlignment;
  @override
  final PassDivSize sizeOnCrossAxis;

  final Widget child;

  const _VerticalWrapper({
    required this.child,
    required this.alignment,
    required this.contentAlignment,
    required this.sizeOnCrossAxis,
  });

  @override
  Widget build(BuildContext context) {
    final column = Column(
      mainAxisAlignment: alignment?.asMainAxis ?? MainAxisAlignment.start,
      mainAxisSize: canExpand ? MainAxisSize.max : MainAxisSize.min,
      children: [child],
    );

    final flex = super.flex;
    if (flex != null) {
      return Flexible(
        flex: flex,
        child: column,
      );
    }

    return column;
  }
}

class DivSizeWrapper extends StatelessWidget {
  final PassDivSize width;
  final PassDivSize height;
  final Widget child;
  final DivAlignment alignment;
  final ParentData? parentData;

  const DivSizeWrapper({
    required this.child,
    required this.height,
    required this.width,
    required this.alignment,
    this.parentData,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final safeWidth = width;
    final safeHeight = height;
    final safeAlignment = alignment;

    final maxWidth = safeWidth.maxConstraint;
    final minWidth = safeWidth.minConstraint;
    final maxHeight = safeHeight.maxConstraint;
    final minHeight = safeHeight.minConstraint;

    final contentAlignment = DivKitProvider.watch<ContentAlignment>(context);

    final isStack = parentData is StackParentData;
    final isFlex = parentData is FlexParentData;

    final Widget wrapper;

    /// Used for stack when layouting complicated objects (Column or Row inside stack with custom position)
    final bool dropStackLayout;

    if (minHeight != null &&
        maxHeight != null &&
        minWidth != null &&
        maxWidth != null) {
      dropStackLayout = false;
      final canBeWrappedHorizontal = safeWidth is! WrapDivSize;
      final canBeWrappedVertical = safeHeight is! WrapDivSize;

      final isFlexibleOnVerticalAxis =
          contentAlignment is FlexContentAlignment &&
              contentAlignment.direction == Axis.vertical;
      final isFlexibleOnHorizontalAxis =
          contentAlignment is FlexContentAlignment &&
              contentAlignment.direction == Axis.horizontal;

      if (canBeWrappedHorizontal && canBeWrappedVertical) {
        wrapper = _VerticalWrapper(
          sizeOnCrossAxis: safeWidth,
          contentAlignment: contentAlignment,
          alignment: safeAlignment.vertical,
          child: _HorizontalWrapper(
            sizeOnCrossAxis: safeHeight,
            contentAlignment: contentAlignment,
            alignment: safeAlignment.horizontal,
            child: ConstrainedBox(
              constraints: BoxConstraints(
                minHeight: minHeight,
                maxWidth: maxWidth,
                maxHeight: maxHeight,
                minWidth: minWidth,
              ),
              child: child,
            ),
          ),
        );
      } else if (canBeWrappedHorizontal || isFlexibleOnHorizontalAxis) {
        wrapper = _VerticalWrapper(
          sizeOnCrossAxis: safeWidth,
          contentAlignment: null,
          alignment: safeAlignment.vertical,
          child: _HorizontalWrapper(
            sizeOnCrossAxis: safeHeight,
            contentAlignment: contentAlignment,
            alignment: safeAlignment.horizontal,
            child: ConstrainedBox(
              constraints: BoxConstraints(
                minHeight: minHeight,
                maxWidth: maxWidth,
                maxHeight: maxHeight,
                minWidth: minWidth,
              ),
              child: child,
            ),
          ),
        );
      } else if (canBeWrappedVertical || isFlexibleOnVerticalAxis) {
        wrapper = _VerticalWrapper(
          sizeOnCrossAxis: safeWidth,
          contentAlignment: contentAlignment,
          alignment: safeAlignment.vertical,
          child: _HorizontalWrapper(
            sizeOnCrossAxis: safeHeight,
            contentAlignment: null,
            alignment: safeAlignment.horizontal,
            child: ConstrainedBox(
              constraints: BoxConstraints(
                minHeight: minHeight,
                maxWidth: maxWidth,
                maxHeight: maxHeight,
                minWidth: minWidth,
              ),
              child: child,
            ),
          ),
        );
      } else {
        wrapper = ConstrainedBox(
          constraints: BoxConstraints(
            minHeight: minHeight,
            maxWidth: maxWidth,
            maxHeight: maxHeight,
            minWidth: minWidth,
          ),
          child: child,
        );
      }
    } else if (minHeight != null && maxHeight != null) {
      final Widget localWrapper;
      if (safeWidth is FlexDivSize) {
        localWrapper = _HorizontalWrapper(
          sizeOnCrossAxis: safeHeight,
          contentAlignment: isFlex ? contentAlignment : null,
          alignment: safeAlignment.horizontal,
          child: Expanded(
            flex: safeWidth.weight ?? 1,
            child: ConstrainedBox(
              constraints: BoxConstraints(
                minHeight: minHeight,
                maxHeight: maxHeight,
              ),
              child: child,
            ),
          ),
        );
      } else {
        localWrapper = ConstrainedBox(
          constraints: BoxConstraints(
            minHeight: minHeight,
            maxHeight: maxHeight,
          ),
          child: child,
        );
      }
      if (safeAlignment.vertical != null) {
        dropStackLayout = true;
      } else {
        dropStackLayout = false;
      }
      wrapper = _VerticalWrapper(
        sizeOnCrossAxis: safeWidth,
        contentAlignment: isFlex ? contentAlignment : null,
        alignment: safeAlignment.vertical,
        child: localWrapper,
      );
    } else if (minWidth != null && maxWidth != null) {
      final Widget localWrapper;
      if (safeHeight is FlexDivSize) {
        localWrapper = _VerticalWrapper(
          sizeOnCrossAxis: safeWidth,
          contentAlignment: isFlex ? contentAlignment : null,
          alignment: safeAlignment.vertical,
          child: Expanded(
            flex: safeHeight.weight ?? 1,
            child: ConstrainedBox(
              constraints: BoxConstraints(
                maxWidth: maxWidth,
                minWidth: minWidth,
              ),
              child: child,
            ),
          ),
        );
      } else {
        localWrapper = ConstrainedBox(
          constraints: BoxConstraints(
            maxWidth: maxWidth,
            minWidth: minWidth,
          ),
          child: child,
        );
      }
      if (safeAlignment.horizontal != null) {
        dropStackLayout = true;
      } else {
        dropStackLayout = false;
      }
      wrapper = _HorizontalWrapper(
        sizeOnCrossAxis: safeHeight,
        contentAlignment: isFlex ? contentAlignment : null,
        alignment: safeAlignment.horizontal,
        child: localWrapper,
      );
    } else if (safeHeight is FlexDivSize &&
        safeWidth is FlexDivSize &&
        (isFlex || isStack)) {
      dropStackLayout = false;
      if (isFlex) {
        wrapper = Expanded(
          flex: safeHeight.weight ?? safeWidth.weight ?? 1,
          child: SizedBox(
            height: double.infinity,
            width: double.infinity,
            child: child,
          ),
        );
      } else {
        wrapper = SizedBox(
          height: double.infinity,
          width: double.infinity,
          child: child,
        );
      }
    } else {
      dropStackLayout = false;
      wrapper = child;
    }

    if (isStack) {
      if (dropStackLayout) {
        return Positioned.fill(
          child: Align(
            alignment: safeAlignment.asAlignment ?? Alignment.center,
            child: wrapper,
          ),
        );
      }
      final alignment = safeAlignment.asAlignment;
      return Positioned.fill(
        left: safeAlignment.horizontal == DivAxisAlignment.start ? 0 : null,
        right: safeAlignment.horizontal == DivAxisAlignment.end ? 0 : null,
        top: safeAlignment.vertical == DivAxisAlignment.start ? 0 : null,
        bottom: safeAlignment.vertical == DivAxisAlignment.end ? 0 : null,
        child: alignment != null
            ? Align(
                alignment: alignment,
                child: wrapper,
              )
            : wrapper,
      );
    }
    return wrapper;
  }
}

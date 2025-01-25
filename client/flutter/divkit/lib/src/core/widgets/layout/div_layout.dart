import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';

/// Print logs with information about constraints and div sizes.
bool debugPrintDivPerformLayout = false;

void _log(String message) {
  if (debugPrintDivPerformLayout) {
    loggerUse(const DefaultDivLoggerContext('onLayout')).debug(message);
  }
}

/// Layout size parameters.
class DivLayoutParam {
  final DivSizeValue width;
  final DivSizeValue height;

  const DivLayoutParam({
    this.width = const DivMatchParent(),
    this.height = const DivWrapContent(),
  });

  DivLayoutParam copyWith({
    DivSizeValue? width,
    DivSizeValue? height,
  }) =>
      DivLayoutParam(
        width: width ?? this.width,
        height: height ?? this.height,
      );

  @override
  String toString() => 'DivLayoutParam($width, $height)';
}

/// The type of the parent container.
enum DivParentData {
  none,
  wrap,
  column,
  row,
  stack,
  pager;

  bool get isWrap => this == DivParentData.wrap;

  bool get isColumn => this == DivParentData.column;

  bool get isRow => this == DivParentData.row;

  bool get isStack => this == DivParentData.stack;

  bool get isPager => this == DivParentData.pager;
}

/// Generalization of divkit sizes.
abstract class DivSizeValue {
  const DivSizeValue();

  T map<T>({
    required Function(double value) fixed,
    required Function(double? min, double? max) wrapContent,
    required Function(int weight) matchParent,
    required Function(double? min, double? max) constrained,
  });

  bool get isFixed => false;

  bool get isWrapContent => false;

  bool get isMatchParent => false;

  bool get isConstrained => false;
}

/// Force setting the size.
class DivFixed extends DivSizeValue {
  final double value;

  const DivFixed(this.value);

  @override
  T map<T>({
    required Function(double value) fixed,
    required Function(double? min, double? max) wrapContent,
    required Function(int weight) matchParent,
    required Function(double? min, double? max) constrained,
  }) =>
      fixed(value);

  @override
  bool get isFixed => true;

  @override
  String toString() => 'fixed($value)';
}

/// Allow the child to be the size it wants and
/// specify the constants within which expects it.
/// Unlike [DivConstrained] allows overflows!
class DivWrapContent extends DivSizeValue {
  final double? min;
  final double? max;

  const DivWrapContent({
    this.min,
    this.max,
  });

  @override
  T map<T>({
    required Function(double value) fixed,
    required Function(double? min, double? max) wrapContent,
    required Function(int weight) matchParent,
    required Function(double? min, double? max) constrained,
  }) =>
      wrapContent(min, max);

  @override
  bool get isWrapContent => true;

  @override
  String toString() => 'wrapContent';
}

/// Force the maximum size of the parent for the child,
/// but we can still specify the weight in [Flex].
class DivMatchParent extends DivSizeValue {
  final int? weight;

  const DivMatchParent([this.weight = 1]);

  @override
  T map<T>({
    required Function(double value) fixed,
    required Function(double? min, double? max) wrapContent,
    required Function(int weight) matchParent,
    required Function(double? min, double? max) constrained,
  }) =>
      matchParent(weight ?? 1);

  @override
  bool get isMatchParent => true;

  @override
  String toString() => 'matchParent(${weight ?? 1})';
}

/// Like that [DivWrapContent], but doesn't allow overflowing at all.
class DivConstrained extends DivSizeValue {
  final double? min;
  final double? max;

  const DivConstrained({
    this.min,
    this.max,
  });

  @override
  T map<T>({
    required Function(double value) fixed,
    required Function(double? min, double? max) wrapContent,
    required Function(int weight) matchParent,
    required Function(double? min, double? max) constrained,
  }) =>
      constrained(min, max);

  @override
  bool get isConstrained => true;

  @override
  String toString() => 'constrained($min, $max))';
}

/// A unit of a divkit layout system that tries to integrate into an existing one in Flutter.
///
/// All the necessary data about the parent and child are collected here
/// and it can forcibly change the standard [performLayout].
///
/// Note: to debug the divkit layout system, enable logging [debugPrintDivPerformLayout].
class DivLayout extends StatelessWidget {
  final DivSizeValue width;
  final DivSizeValue height;
  final double? aspect;
  final EdgeInsetsGeometry? margin;
  final AlignmentGeometry? alignment;

  final Widget child;

  const DivLayout({
    super.key,
    required this.child,
    this.width = const DivMatchParent(),
    this.height = const DivWrapContent(),
    this.alignment,
    this.margin,
    this.aspect,
  });

  DivLayoutParam modifySize(DivParentData? parent, DivLayoutParam? parentSize) {
    final parentWidth = parentSize?.width;
    final parentHeight = parentSize?.height;

    var res = DivLayoutParam(
      width: width,
      height: height,
    );

    // When the wrapContent/constrained is specified for the parent, then we force it on children.
    if (width.isMatchParent && (parentHeight?.isWrapContent ?? false) ||
        (parentHeight?.isConstrained ?? false)) {
      res = res.copyWith(
        width: parentWidth,
      );
    }

    // When the wrapContent/constrained is specified for the parent, then we force it on children.
    if (height.isMatchParent && (parentHeight?.isWrapContent ?? false) ||
        (parentHeight?.isConstrained ?? false)) {
      res = res.copyWith(
        height: parentHeight,
      );
    }

    return res;
  }

  @override
  Widget build(BuildContext context) {
    final parent = watch<DivParentData>(context);
    final parentSize = watch<DivLayoutParam>(context);
    final modified = modifySize(parent, parentSize);
    final biggerParent = (parentSize?.height.isMatchParent ?? false) ||
        (parentSize?.width.isMatchParent ?? false);

    Widget it = Padding(
      padding: margin ?? EdgeInsets.zero,
      child: DivSizeLayout(
        width: modified.width,
        height: modified.height,
        aspect: aspect,
        child: provide(
          modified,
          child: child,
        ),
      ),
    );

    if (parent == DivParentData.stack && alignment != null && biggerParent) {
      return Positioned.fill(
        child: Align(
          widthFactor: 1,
          heightFactor: 1,
          alignment: alignment!,
          child: it,
        ),
      );
    }

    if (alignment != null) {
      it = Align(
        alignment: alignment!,
        child: it,
      );
    }

    if (parent == DivParentData.column && modified.height is DivMatchParent) {
      final weight = (modified.height as DivMatchParent).weight ?? 1;
      return Flexible(flex: weight, child: it);
    } else if (parent == DivParentData.row &&
        modified.width is DivMatchParent) {
      final weight = (modified.width as DivMatchParent).weight ?? 1;
      return Flexible(flex: weight, child: it);
    }

    return it;
  }

  @override
  void debugFillProperties(DiagnosticPropertiesBuilder properties) {
    super.debugFillProperties(properties);
    properties.add(
      DiagnosticsProperty<AlignmentGeometry?>(
        'alignment',
        alignment,
        defaultValue: null,
      ),
    );
    properties.add(
      DiagnosticsProperty<DivSizeValue>(
        'width',
        width,
        defaultValue: const DivMatchParent(),
      ),
    );
    properties.add(
      DiagnosticsProperty<DivSizeValue>(
        'height',
        height,
        defaultValue: const DivWrapContent(),
      ),
    );
    properties.add(
      DiagnosticsProperty<double?>(
        'aspect',
        aspect,
        defaultValue: null,
      ),
    );
  }
}

class DivSizeLayout extends SingleChildRenderObjectWidget {
  final DivSizeValue width;
  final DivSizeValue height;
  final double? aspect;

  const DivSizeLayout({
    super.key,
    required Widget child,
    this.width = const DivWrapContent(),
    this.height = const DivWrapContent(),
    this.aspect,
  }) : super(child: child);

  @override
  RenderObject createRenderObject(BuildContext context) => DivSizeLayoutRender(
        width,
        height,
        aspect,
      );

  @override
  void updateRenderObject(
    BuildContext context,
    covariant DivSizeLayoutRender renderObject,
  ) =>
      renderObject
        ..width = width
        ..height = height
        ..aspect = aspect;
}

class DivSizeLayoutRender extends RenderBox
    with RenderObjectWithChildMixin<RenderBox> {
  DivSizeValue _width;

  set width(DivSizeValue value) {
    if (value != _width) {
      _width = value;
      markNeedsLayout();
    }
  }

  DivSizeValue _height;

  set height(DivSizeValue value) {
    if (value != _height) {
      _height = value;
      markNeedsLayout();
    }
  }

  double? _aspect;

  set aspect(double? value) {
    if (value != _aspect) {
      _aspect = value;
      markNeedsLayout();
    }
  }

  DivSizeLayoutRender(
    DivSizeValue width,
    DivSizeValue height,
    double? aspect,
  )   : _width = width,
        _height = height,
        _aspect = aspect;

  @override
  bool hitTestSelf(Offset position) => true;

  @override
  bool hitTestChildren(
    BoxHitTestResult result, {
    required Offset position,
  }) {
    if (child != null) {
      return child!.hitTest(
        result,
        position: position,
      );
    }
    return false;
  }

  @override
  void performLayout() {
    if (child == null) {
      size = constraints.smallest;
      return;
    }

    _log("[begin #$hashCode]");

    var it = constraints;
    _width.map(
      fixed: (value) {
        _log("width.fixed($value)");
        it = it.copyWith(
          minWidth: value,
          maxWidth: value,
        );
      },
      wrapContent: (min, max) {
        _log("width.wrapContent($min, $max)");
        it = it.copyWith(minWidth: min ?? 0, maxWidth: max);
      },
      matchParent: (weight) {
        _log("width.matchParent($weight)");
        it = it.copyWith(
          minWidth: constraints.hasBoundedWidth ? constraints.maxWidth : 0.0,
          maxWidth: constraints.maxWidth,
        );
      },
      constrained: (min, max) {
        _log("width.constrained($min, $max)");
        it = it.copyWith(
          minWidth: constraints.constrainWidth(min ?? constraints.minWidth),
          maxWidth: constraints.constrainWidth(max ?? constraints.maxWidth),
        );
      },
    );

    if (_aspect == null) {
      _height.map(
        fixed: (value) {
          _log("height.fixed($value)");
          it = it.copyWith(
            minHeight: value,
            maxHeight: value,
          );
        },
        wrapContent: (min, max) {
          _log("height.wrapContent($min, $max)");
          it = it.copyWith(minHeight: min ?? 0, maxHeight: max);
        },
        matchParent: (weight) {
          _log("height.matchParent($weight)");
          it = it.copyWith(
            minHeight:
                constraints.hasBoundedHeight ? constraints.maxHeight : 0.0,
            maxHeight: constraints.maxHeight,
          );
        },
        constrained: (min, max) {
          _log("height.constrained($min, $max)");
          it = it.copyWith(
            minHeight:
                constraints.constrainHeight(min ?? constraints.minHeight),
            maxHeight:
                constraints.constrainHeight(max ?? constraints.maxHeight),
          );
        },
      );
    } else {
      // This layout passage is needed in order to calculate the width.
      child!.layout(it, parentUsesSize: true);
      final value = child!.size.width / _aspect!;
      _log("Aspect size: $value");
      it = it.copyWith(
        minHeight: value,
        maxHeight: value,
      );
    }

    _log(
      "[constraints]\n"
      "in: $constraints\n"
      "out: $it",
    );

    child!.layout(it, parentUsesSize: true);

    final childWidth = child!.size.width;
    final calcWidth = _width is DivMatchParent
        ? constraints.hasBoundedWidth
            ? constraints.maxWidth
            : childWidth
        : _width is DivWrapContent
            ? childWidth
            : constraints.constrainWidth(childWidth);

    final childHeight = child!.size.height;
    final calcHeight = _height is DivMatchParent
        ? constraints.hasBoundedHeight
            ? constraints.maxHeight
            : childHeight
        : _height is DivWrapContent
            ? childHeight
            : constraints.constrainHeight(childHeight);

    _log(
      "[size]\n"
      "in: ${child!.size}\n"
      "out: ${constraints.constrain(Size(calcWidth, calcHeight))}",
    );

    size = constraints.constrain(Size(calcWidth, calcHeight));

    _log("[end #$hashCode]");
  }

  @override
  void paint(PaintingContext context, Offset offset) {
    if (child != null) {
      context.paintChild(child!, offset);
    }
  }
}

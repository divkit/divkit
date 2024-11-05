// Generated code. Do not modify.

import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// The size of an element adjusts to its contents.
class DivWrapContentSize extends Resolvable with EquatableMixin {
  const DivWrapContentSize({
    this.constrained,
    this.maxSize,
    this.minSize,
  });

  static const type = "wrap_content";

  /// The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
  final Expression<bool>? constrained;

  /// Maximum size of an element.
  final DivWrapContentSizeConstraintSize? maxSize;

  /// Minimum size of an element.
  final DivWrapContentSizeConstraintSize? minSize;

  @override
  List<Object?> get props => [
        constrained,
        maxSize,
        minSize,
      ];

  DivWrapContentSize copyWith({
    Expression<bool>? Function()? constrained,
    DivWrapContentSizeConstraintSize? Function()? maxSize,
    DivWrapContentSizeConstraintSize? Function()? minSize,
  }) =>
      DivWrapContentSize(
        constrained:
            constrained != null ? constrained.call() : this.constrained,
        maxSize: maxSize != null ? maxSize.call() : this.maxSize,
        minSize: minSize != null ? minSize.call() : this.minSize,
      );

  static DivWrapContentSize? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivWrapContentSize(
        constrained: safeParseBoolExpr(
          json['constrained'],
        ),
        maxSize: safeParseObj(
          DivWrapContentSizeConstraintSize.fromJson(json['max_size']),
        ),
        minSize: safeParseObj(
          DivWrapContentSizeConstraintSize.fromJson(json['min_size']),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivWrapContentSize resolve(DivVariableContext context) {
    constrained?.resolve(context);
    maxSize?.resolve(context);
    minSize?.resolve(context);
    return this;
  }
}

class DivWrapContentSizeConstraintSize extends Resolvable with EquatableMixin {
  const DivWrapContentSizeConstraintSize({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    required this.value,
  });

  /// Unit of measurement:
  /// • `px` — a physical pixel.
  /// • `dp` — a logical pixel that doesn't depend on screen density.
  /// • `sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.
  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;
  // constraint: number >= 0
  final Expression<int> value;

  @override
  List<Object?> get props => [
        unit,
        value,
      ];

  DivWrapContentSizeConstraintSize copyWith({
    Expression<DivSizeUnit>? unit,
    Expression<int>? value,
  }) =>
      DivWrapContentSizeConstraintSize(
        unit: unit ?? this.unit,
        value: value ?? this.value,
      );

  static DivWrapContentSizeConstraintSize? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivWrapContentSizeConstraintSize(
        unit: safeParseStrEnumExpr(
          json['unit'],
          parse: DivSizeUnit.fromJson,
          fallback: DivSizeUnit.dp,
        )!,
        value: safeParseIntExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivWrapContentSizeConstraintSize resolve(DivVariableContext context) {
    unit.resolve(context);
    value.resolve(context);
    return this;
  }
}

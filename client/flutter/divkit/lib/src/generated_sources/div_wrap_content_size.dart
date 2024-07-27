// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_size_unit.dart';

class DivWrapContentSize with EquatableMixin {
  const DivWrapContentSize({
    this.constrained,
    this.maxSize,
    this.minSize,
  });

  static const type = "wrap_content";

  final Expression<bool>? constrained;

  final DivWrapContentSizeConstraintSize? maxSize;

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

  static DivWrapContentSize? fromJson(Map<String, dynamic>? json) {
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
}

class DivWrapContentSizeConstraintSize with EquatableMixin {
  const DivWrapContentSizeConstraintSize({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    required this.value,
  });

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
}

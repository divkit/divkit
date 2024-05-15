// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_size_unit.dart';

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

  static DivWrapContentSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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

  static DivWrapContentSizeConstraintSize? fromJson(
      Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
  }
}

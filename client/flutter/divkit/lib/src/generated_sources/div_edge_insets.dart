// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_size_unit.dart';

class DivEdgeInsets with EquatableMixin {
  const DivEdgeInsets({
    this.bottom = const ValueExpression(0),
    this.end,
    this.left = const ValueExpression(0),
    this.right = const ValueExpression(0),
    this.start,
    this.top = const ValueExpression(0),
    this.unit = const ValueExpression(DivSizeUnit.dp),
  });

  // constraint: number >= 0; default value: 0
  final Expression<int> bottom;
  // constraint: number >= 0
  final Expression<int>? end;
  // constraint: number >= 0; default value: 0
  final Expression<int> left;
  // constraint: number >= 0; default value: 0
  final Expression<int> right;
  // constraint: number >= 0
  final Expression<int>? start;
  // constraint: number >= 0; default value: 0
  final Expression<int> top;
  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;

  @override
  List<Object?> get props => [
        bottom,
        end,
        left,
        right,
        start,
        top,
        unit,
      ];

  DivEdgeInsets copyWith({
    Expression<int>? bottom,
    Expression<int>? Function()? end,
    Expression<int>? left,
    Expression<int>? right,
    Expression<int>? Function()? start,
    Expression<int>? top,
    Expression<DivSizeUnit>? unit,
  }) =>
      DivEdgeInsets(
        bottom: bottom ?? this.bottom,
        end: end != null ? end.call() : this.end,
        left: left ?? this.left,
        right: right ?? this.right,
        start: start != null ? start.call() : this.start,
        top: top ?? this.top,
        unit: unit ?? this.unit,
      );

  static DivEdgeInsets? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivEdgeInsets(
        bottom: safeParseIntExpr(
          json['bottom'],
          fallback: 0,
        )!,
        end: safeParseIntExpr(
          json['end'],
        ),
        left: safeParseIntExpr(
          json['left'],
          fallback: 0,
        )!,
        right: safeParseIntExpr(
          json['right'],
          fallback: 0,
        )!,
        start: safeParseIntExpr(
          json['start'],
        ),
        top: safeParseIntExpr(
          json['top'],
          fallback: 0,
        )!,
        unit: safeParseStrEnumExpr(
          json['unit'],
          parse: DivSizeUnit.fromJson,
          fallback: DivSizeUnit.dp,
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}

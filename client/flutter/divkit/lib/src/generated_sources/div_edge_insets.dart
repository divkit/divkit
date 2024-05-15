// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_size_unit.dart';

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

  static DivEdgeInsets? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
  }
}

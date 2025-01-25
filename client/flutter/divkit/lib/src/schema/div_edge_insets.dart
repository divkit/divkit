// Generated code. Do not modify.

import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// It sets margins.
class DivEdgeInsets extends Resolvable with EquatableMixin {
  const DivEdgeInsets({
    this.bottom = const ValueExpression(0),
    this.end,
    this.left = const ValueExpression(0),
    this.right = const ValueExpression(0),
    this.start,
    this.top = const ValueExpression(0),
    this.unit = const ValueExpression(DivSizeUnit.dp),
  });

  /// Bottom margin.
  // constraint: number >= 0; default value: 0
  final Expression<int> bottom;

  /// End margin. Margin position depends on the interface orientation. Has higher priority than the left and right margins.
  // constraint: number >= 0
  final Expression<int>? end;

  /// Left margin.
  // constraint: number >= 0; default value: 0
  final Expression<int> left;

  /// Right margin.
  // constraint: number >= 0; default value: 0
  final Expression<int> right;

  /// Start margin. Margin position depends on the interface orientation. Has higher priority than the left and right margins.
  // constraint: number >= 0
  final Expression<int>? start;

  /// Top margin.
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

  static DivEdgeInsets? fromJson(
    Map<String, dynamic>? json,
  ) {
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

  @override
  DivEdgeInsets resolve(DivVariableContext context) {
    bottom.resolve(context);
    end?.resolve(context);
    left.resolve(context);
    right.resolve(context);
    start?.resolve(context);
    top.resolve(context);
    unit.resolve(context);
    return this;
  }
}

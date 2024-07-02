// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivAbsoluteEdgeInsets with EquatableMixin {
  const DivAbsoluteEdgeInsets({
    this.bottom = const ValueExpression(0),
    this.left = const ValueExpression(0),
    this.right = const ValueExpression(0),
    this.top = const ValueExpression(0),
  });

  // constraint: number >= 0; default value: 0
  final Expression<int> bottom;
  // constraint: number >= 0; default value: 0
  final Expression<int> left;
  // constraint: number >= 0; default value: 0
  final Expression<int> right;
  // constraint: number >= 0; default value: 0
  final Expression<int> top;

  @override
  List<Object?> get props => [
        bottom,
        left,
        right,
        top,
      ];

  DivAbsoluteEdgeInsets copyWith({
    Expression<int>? bottom,
    Expression<int>? left,
    Expression<int>? right,
    Expression<int>? top,
  }) =>
      DivAbsoluteEdgeInsets(
        bottom: bottom ?? this.bottom,
        left: left ?? this.left,
        right: right ?? this.right,
        top: top ?? this.top,
      );

  static DivAbsoluteEdgeInsets? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivAbsoluteEdgeInsets(
      bottom: safeParseIntExpr(
        json['bottom'],
        fallback: 0,
      )!,
      left: safeParseIntExpr(
        json['left'],
        fallback: 0,
      )!,
      right: safeParseIntExpr(
        json['right'],
        fallback: 0,
      )!,
      top: safeParseIntExpr(
        json['top'],
        fallback: 0,
      )!,
    );
  }
}

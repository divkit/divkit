// Generated code. Do not modify.

import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// It sets margins.
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
        bottom: reqVProp<int>(
          safeParseIntExpr(
            json['bottom'],
            fallback: 0,
          ),
          name: 'bottom',
        ),
        end: safeParseIntExpr(
          json['end'],
        ),
        left: reqVProp<int>(
          safeParseIntExpr(
            json['left'],
            fallback: 0,
          ),
          name: 'left',
        ),
        right: reqVProp<int>(
          safeParseIntExpr(
            json['right'],
            fallback: 0,
          ),
          name: 'right',
        ),
        start: safeParseIntExpr(
          json['start'],
        ),
        top: reqVProp<int>(
          safeParseIntExpr(
            json['top'],
            fallback: 0,
          ),
          name: 'top',
        ),
        unit: reqVProp<DivSizeUnit>(
          safeParseStrEnumExpr(
            json['unit'],
            parse: DivSizeUnit.fromJson,
            fallback: DivSizeUnit.dp,
          ),
          name: 'unit',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

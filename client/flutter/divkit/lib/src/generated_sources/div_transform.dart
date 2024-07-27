// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_pivot.dart';
import 'package:divkit/src/generated_sources/div_pivot_percentage.dart';

class DivTransform with EquatableMixin {
  const DivTransform({
    this.pivotX = const DivPivot.divPivotPercentage(
      DivPivotPercentage(
        value: ValueExpression(50),
      ),
    ),
    this.pivotY = const DivPivot.divPivotPercentage(
      DivPivotPercentage(
        value: ValueExpression(50),
      ),
    ),
    this.rotation,
  });

  // default value: const DivPivot.divPivotPercentage(const DivPivotPercentage(value: ValueExpression(50),))
  final DivPivot pivotX;
  // default value: const DivPivot.divPivotPercentage(const DivPivotPercentage(value: ValueExpression(50),))
  final DivPivot pivotY;

  final Expression<double>? rotation;

  @override
  List<Object?> get props => [
        pivotX,
        pivotY,
        rotation,
      ];

  DivTransform copyWith({
    DivPivot? pivotX,
    DivPivot? pivotY,
    Expression<double>? Function()? rotation,
  }) =>
      DivTransform(
        pivotX: pivotX ?? this.pivotX,
        pivotY: pivotY ?? this.pivotY,
        rotation: rotation != null ? rotation.call() : this.rotation,
      );

  static DivTransform? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivTransform(
        pivotX: safeParseObj(
          DivPivot.fromJson(json['pivot_x']),
          fallback: const DivPivot.divPivotPercentage(
            DivPivotPercentage(
              value: ValueExpression(50),
            ),
          ),
        )!,
        pivotY: safeParseObj(
          DivPivot.fromJson(json['pivot_y']),
          fallback: const DivPivot.divPivotPercentage(
            DivPivotPercentage(
              value: ValueExpression(50),
            ),
          ),
        )!,
        rotation: safeParseDoubleExpr(
          json['rotation'],
        ),
      );
    } catch (e) {
      return null;
    }
  }
}

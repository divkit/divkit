// Generated code. Do not modify.

import 'package:divkit/src/schema/div_pivot.dart';
import 'package:divkit/src/schema/div_pivot_percentage.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Transformation of the element.
class DivTransform with EquatableMixin {
  const DivTransform({
    this.pivotX = const DivPivot.divPivotPercentage(
      DivPivotPercentage(
        value: ValueExpression(
          50,
        ),
      ),
    ),
    this.pivotY = const DivPivot.divPivotPercentage(
      DivPivotPercentage(
        value: ValueExpression(
          50,
        ),
      ),
    ),
    this.rotation,
  });

  /// X coordinate of the rotation axis.
  // default value: const DivPivot.divPivotPercentage(const DivPivotPercentage(value: ValueExpression(50,),),)
  final DivPivot pivotX;

  /// Y coordinate of the rotation axis.
  // default value: const DivPivot.divPivotPercentage(const DivPivotPercentage(value: ValueExpression(50,),),)
  final DivPivot pivotY;

  /// Degrees of the element rotation. Positive values used for clockwise rotation.
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

  static DivTransform? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTransform(
        pivotX: reqProp<DivPivot>(
          safeParseObject(
            json['pivot_x'],
            parse: DivPivot.fromJson,
            fallback: const DivPivot.divPivotPercentage(
              DivPivotPercentage(
                value: ValueExpression(
                  50,
                ),
              ),
            ),
          ),
          name: 'pivot_x',
        ),
        pivotY: reqProp<DivPivot>(
          safeParseObject(
            json['pivot_y'],
            parse: DivPivot.fromJson,
            fallback: const DivPivot.divPivotPercentage(
              DivPivotPercentage(
                value: ValueExpression(
                  50,
                ),
              ),
            ),
          ),
          name: 'pivot_y',
        ),
        rotation: safeParseDoubleExpr(
          json['rotation'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

// Generated code. Do not modify.

import 'package:divkit/src/schema/div_pivot.dart';
import 'package:divkit/src/schema/div_pivot_percentage.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Transformation of the element.
class DivTransform extends Preloadable with EquatableMixin {
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
        pivotX: safeParseObj(
          DivPivot.fromJson(json['pivot_x']),
          fallback: const DivPivot.divPivotPercentage(
            DivPivotPercentage(
              value: ValueExpression(
                50,
              ),
            ),
          ),
        )!,
        pivotY: safeParseObj(
          DivPivot.fromJson(json['pivot_y']),
          fallback: const DivPivot.divPivotPercentage(
            DivPivotPercentage(
              value: ValueExpression(
                50,
              ),
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

  static Future<DivTransform?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTransform(
        pivotX: (await safeParseObjAsync(
          DivPivot.fromJson(json['pivot_x']),
          fallback: const DivPivot.divPivotPercentage(
            DivPivotPercentage(
              value: ValueExpression(
                50,
              ),
            ),
          ),
        ))!,
        pivotY: (await safeParseObjAsync(
          DivPivot.fromJson(json['pivot_y']),
          fallback: const DivPivot.divPivotPercentage(
            DivPivotPercentage(
              value: ValueExpression(
                50,
              ),
            ),
          ),
        ))!,
        rotation: await safeParseDoubleExprAsync(
          json['rotation'],
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await pivotX.preload(context);
      await pivotY.preload(context);
      await rotation?.preload(context);
    } catch (e) {
      return;
    }
  }
}

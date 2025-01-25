// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Linear gradient.
class DivLinearGradient extends Resolvable with EquatableMixin {
  const DivLinearGradient({
    this.angle = const ValueExpression(0),
    required this.colors,
  });

  static const type = "gradient";

  /// Angle of gradient direction.
  // constraint: number >= 0 && number <= 360; default value: 0
  final Expression<int> angle;

  /// Colors. Gradient points are located at an equal distance from each other.
  // at least 2 elements
  final Expression<List<Color>> colors;

  @override
  List<Object?> get props => [
        angle,
        colors,
      ];

  DivLinearGradient copyWith({
    Expression<int>? angle,
    Expression<List<Color>>? colors,
  }) =>
      DivLinearGradient(
        angle: angle ?? this.angle,
        colors: colors ?? this.colors,
      );

  static DivLinearGradient? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivLinearGradient(
        angle: safeParseIntExpr(
          json['angle'],
          fallback: 0,
        )!,
        colors: safeParseObjExpr(
          safeListMap(
            json['colors'],
            (v) => safeParseColor(
              v,
            )!,
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivLinearGradient resolve(DivVariableContext context) {
    angle.resolve(context);
    colors.resolve(context);
    return this;
  }
}

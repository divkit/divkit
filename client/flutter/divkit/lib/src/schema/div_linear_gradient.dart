// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
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
  final Expression<Arr<Color>> colors;

  @override
  List<Object?> get props => [
        angle,
        colors,
      ];

  DivLinearGradient copyWith({
    Expression<int>? angle,
    Expression<Arr<Color>>? colors,
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
        angle: reqVProp<int>(
          safeParseIntExpr(
            json['angle'],
            fallback: 0,
          ),
          name: 'angle',
        ),
        colors: reqVProp<Arr<Color>>(
          safeParseObjectsExpr(
            json['colors'],
            (v) => reqProp<Color>(
              safeParseColor(v),
            ),
          ),
          name: 'colors',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
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

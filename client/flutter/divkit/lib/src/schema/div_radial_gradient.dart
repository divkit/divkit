// Generated code. Do not modify.

import 'package:divkit/src/schema/div_radial_gradient_center.dart';
import 'package:divkit/src/schema/div_radial_gradient_radius.dart';
import 'package:divkit/src/schema/div_radial_gradient_relative_center.dart';
import 'package:divkit/src/schema/div_radial_gradient_relative_radius.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Radial gradient.
class DivRadialGradient with EquatableMixin {
  const DivRadialGradient({
    this.centerX =
        const DivRadialGradientCenter.divRadialGradientRelativeCenter(
      DivRadialGradientRelativeCenter(
        value: ValueExpression(
          0.5,
        ),
      ),
    ),
    this.centerY =
        const DivRadialGradientCenter.divRadialGradientRelativeCenter(
      DivRadialGradientRelativeCenter(
        value: ValueExpression(
          0.5,
        ),
      ),
    ),
    required this.colors,
    this.radius = const DivRadialGradientRadius.divRadialGradientRelativeRadius(
      DivRadialGradientRelativeRadius(
        value: ValueExpression(
          DivRadialGradientRelativeRadiusValue.farthestCorner,
        ),
      ),
    ),
  });

  static const type = "radial_gradient";

  /// Shift of the central point of the gradient relative to the left edge along the X axis.
  // default value: const DivRadialGradientCenter.divRadialGradientRelativeCenter(const DivRadialGradientRelativeCenter(value: ValueExpression(0.5,),),)
  final DivRadialGradientCenter centerX;

  /// Shift of the central point of the gradient relative to the top edge along the Y axis.
  // default value: const DivRadialGradientCenter.divRadialGradientRelativeCenter(const DivRadialGradientRelativeCenter(value: ValueExpression(0.5,),),)
  final DivRadialGradientCenter centerY;

  /// Colors. Gradient points are located at an equal distance from each other.
  // at least 2 elements
  final Expression<Arr<Color>> colors;

  /// Radius of the gradient transition.
  // default value: const DivRadialGradientRadius.divRadialGradientRelativeRadius(const DivRadialGradientRelativeRadius(value: ValueExpression(DivRadialGradientRelativeRadiusValue.farthestCorner,),),)
  final DivRadialGradientRadius radius;

  @override
  List<Object?> get props => [
        centerX,
        centerY,
        colors,
        radius,
      ];

  DivRadialGradient copyWith({
    DivRadialGradientCenter? centerX,
    DivRadialGradientCenter? centerY,
    Expression<Arr<Color>>? colors,
    DivRadialGradientRadius? radius,
  }) =>
      DivRadialGradient(
        centerX: centerX ?? this.centerX,
        centerY: centerY ?? this.centerY,
        colors: colors ?? this.colors,
        radius: radius ?? this.radius,
      );

  static DivRadialGradient? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradient(
        centerX: reqProp<DivRadialGradientCenter>(
          safeParseObject(
            json['center_x'],
            parse: DivRadialGradientCenter.fromJson,
            fallback:
                const DivRadialGradientCenter.divRadialGradientRelativeCenter(
              DivRadialGradientRelativeCenter(
                value: ValueExpression(
                  0.5,
                ),
              ),
            ),
          ),
          name: 'center_x',
        ),
        centerY: reqProp<DivRadialGradientCenter>(
          safeParseObject(
            json['center_y'],
            parse: DivRadialGradientCenter.fromJson,
            fallback:
                const DivRadialGradientCenter.divRadialGradientRelativeCenter(
              DivRadialGradientRelativeCenter(
                value: ValueExpression(
                  0.5,
                ),
              ),
            ),
          ),
          name: 'center_y',
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
        radius: reqProp<DivRadialGradientRadius>(
          safeParseObject(
            json['radius'],
            parse: DivRadialGradientRadius.fromJson,
            fallback:
                const DivRadialGradientRadius.divRadialGradientRelativeRadius(
              DivRadialGradientRelativeRadius(
                value: ValueExpression(
                  DivRadialGradientRelativeRadiusValue.farthestCorner,
                ),
              ),
            ),
          ),
          name: 'radius',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

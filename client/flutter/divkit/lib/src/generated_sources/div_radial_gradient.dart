// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_radial_gradient_center.dart';
import 'div_radial_gradient_radius.dart';
import 'div_radial_gradient_relative_center.dart';
import 'div_radial_gradient_relative_center.dart';
import 'div_radial_gradient_relative_radius.dart';

class DivRadialGradient with EquatableMixin {
  const DivRadialGradient({
    this.centerX =
        const DivRadialGradientCenter(const DivRadialGradientRelativeCenter(
      value: ValueExpression(0.5),
    )),
    this.centerY =
        const DivRadialGradientCenter(const DivRadialGradientRelativeCenter(
      value: ValueExpression(0.5),
    )),
    required this.colors,
    this.radius =
        const DivRadialGradientRadius(const DivRadialGradientRelativeRadius(
      value:
          ValueExpression(DivRadialGradientRelativeRadiusValue.farthestCorner),
    )),
  });

  static const type = "radial_gradient";
  // default value: const DivRadialGradientCenter(const DivRadialGradientRelativeCenter(value: ValueExpression(0.5),))
  final DivRadialGradientCenter centerX;
  // default value: const DivRadialGradientCenter(const DivRadialGradientRelativeCenter(value: ValueExpression(0.5),))
  final DivRadialGradientCenter centerY;
  // at least 2 elements
  final Expression<List<Color>> colors;
  // default value: const DivRadialGradientRadius(const DivRadialGradientRelativeRadius(value: ValueExpression(DivRadialGradientRelativeRadiusValue.farthestCorner),))
  final DivRadialGradientRadius radius;

  @override
  List<Object?> get props => [
        centerX,
        centerY,
        colors,
        radius,
      ];

  static DivRadialGradient? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivRadialGradient(
      centerX: safeParseObj(
        DivRadialGradientCenter.fromJson(json['center_x']),
        fallback:
            const DivRadialGradientCenter(const DivRadialGradientRelativeCenter(
          value: ValueExpression(0.5),
        )),
      )!,
      centerY: safeParseObj(
        DivRadialGradientCenter.fromJson(json['center_y']),
        fallback:
            const DivRadialGradientCenter(const DivRadialGradientRelativeCenter(
          value: ValueExpression(0.5),
        )),
      )!,
      colors: safeParseObjExpr(
        (json['colors'] as List<dynamic>)
            .map(
              (v) => safeParseColor(
                v,
              )!,
            )
            .toList(),
      )!,
      radius: safeParseObj(
        DivRadialGradientRadius.fromJson(json['radius']),
        fallback:
            const DivRadialGradientRadius(const DivRadialGradientRelativeRadius(
          value: ValueExpression(
              DivRadialGradientRelativeRadiusValue.farthestCorner),
        )),
      )!,
    );
  }
}

// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_radial_gradient_center.dart';
import 'package:divkit/src/generated_sources/div_radial_gradient_radius.dart';
import 'package:divkit/src/generated_sources/div_radial_gradient_relative_center.dart';
import 'package:divkit/src/generated_sources/div_radial_gradient_relative_radius.dart';

class DivRadialGradient with EquatableMixin {
  const DivRadialGradient({
    this.centerX =
        const DivRadialGradientCenter.divRadialGradientRelativeCenter(
      DivRadialGradientRelativeCenter(
        value: ValueExpression(0.5),
      ),
    ),
    this.centerY =
        const DivRadialGradientCenter.divRadialGradientRelativeCenter(
      DivRadialGradientRelativeCenter(
        value: ValueExpression(0.5),
      ),
    ),
    required this.colors,
    this.radius = const DivRadialGradientRadius.divRadialGradientRelativeRadius(
      DivRadialGradientRelativeRadius(
        value: ValueExpression(
            DivRadialGradientRelativeRadiusValue.farthestCorner),
      ),
    ),
  });

  static const type = "radial_gradient";
  // default value: const DivRadialGradientCenter.divRadialGradientRelativeCenter(const DivRadialGradientRelativeCenter(value: ValueExpression(0.5),))
  final DivRadialGradientCenter centerX;
  // default value: const DivRadialGradientCenter.divRadialGradientRelativeCenter(const DivRadialGradientRelativeCenter(value: ValueExpression(0.5),))
  final DivRadialGradientCenter centerY;
  // at least 2 elements
  final Expression<List<Color>> colors;
  // default value: const DivRadialGradientRadius.divRadialGradientRelativeRadius(const DivRadialGradientRelativeRadius(value: ValueExpression(DivRadialGradientRelativeRadiusValue.farthestCorner),))
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
    Expression<List<Color>>? colors,
    DivRadialGradientRadius? radius,
  }) =>
      DivRadialGradient(
        centerX: centerX ?? this.centerX,
        centerY: centerY ?? this.centerY,
        colors: colors ?? this.colors,
        radius: radius ?? this.radius,
      );

  static DivRadialGradient? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradient(
        centerX: safeParseObj(
          DivRadialGradientCenter.fromJson(json['center_x']),
          fallback:
              const DivRadialGradientCenter.divRadialGradientRelativeCenter(
            DivRadialGradientRelativeCenter(
              value: ValueExpression(0.5),
            ),
          ),
        )!,
        centerY: safeParseObj(
          DivRadialGradientCenter.fromJson(json['center_y']),
          fallback:
              const DivRadialGradientCenter.divRadialGradientRelativeCenter(
            DivRadialGradientRelativeCenter(
              value: ValueExpression(0.5),
            ),
          ),
        )!,
        colors: safeParseObjExpr(
          safeListMap(
            json['colors'],
            (v) => safeParseColor(
              v,
            )!,
          ),
        )!,
        radius: safeParseObj(
          DivRadialGradientRadius.fromJson(json['radius']),
          fallback:
              const DivRadialGradientRadius.divRadialGradientRelativeRadius(
            DivRadialGradientRelativeRadius(
              value: ValueExpression(
                DivRadialGradientRelativeRadiusValue.farthestCorner,
              ),
            ),
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}

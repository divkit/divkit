// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_fixed_size.dart';
import 'div_stroke.dart';

class DivCircleShape with EquatableMixin {
  const DivCircleShape({
    this.backgroundColor,
    this.radius = const DivFixedSize(
      value: ValueExpression(10),
    ),
    this.stroke,
  });

  static const type = "circle";

  final Expression<Color>? backgroundColor;
  // default value: const DivFixedSize(value: ValueExpression(10),)
  final DivFixedSize radius;

  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        backgroundColor,
        radius,
        stroke,
      ];

  static DivCircleShape? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivCircleShape(
      backgroundColor: safeParseColorExpr(
        json['background_color'],
      ),
      radius: safeParseObj(
        DivFixedSize.fromJson(json['radius']),
        fallback: const DivFixedSize(
          value: ValueExpression(10),
        ),
      )!,
      stroke: safeParseObj(
        DivStroke.fromJson(json['stroke']),
      ),
    );
  }
}

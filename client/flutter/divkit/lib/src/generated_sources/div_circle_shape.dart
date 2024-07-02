// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_fixed_size.dart';
import 'package:divkit/src/generated_sources/div_stroke.dart';

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

  DivCircleShape copyWith({
    Expression<Color>? Function()? backgroundColor,
    DivFixedSize? radius,
    DivStroke? Function()? stroke,
  }) =>
      DivCircleShape(
        backgroundColor: backgroundColor != null
            ? backgroundColor.call()
            : this.backgroundColor,
        radius: radius ?? this.radius,
        stroke: stroke != null ? stroke.call() : this.stroke,
      );

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

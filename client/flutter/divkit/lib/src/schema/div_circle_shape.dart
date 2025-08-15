// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/schema/div_stroke.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Circle.
class DivCircleShape with EquatableMixin {
  const DivCircleShape({
    this.backgroundColor,
    this.radius = const DivFixedSize(
      value: ValueExpression(
        10,
      ),
    ),
    this.stroke,
  });

  static const type = "circle";

  /// Fill color.
  final Expression<Color>? backgroundColor;

  /// Radius.
  // default value: const DivFixedSize(value: ValueExpression(10,),)
  final DivFixedSize radius;

  /// Stroke style.
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

  static DivCircleShape? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivCircleShape(
        backgroundColor: safeParseColorExpr(
          json['background_color'],
        ),
        radius: reqProp<DivFixedSize>(
          safeParseObject(
            json['radius'],
            parse: DivFixedSize.fromJson,
            fallback: const DivFixedSize(
              value: ValueExpression(
                10,
              ),
            ),
          ),
          name: 'radius',
        ),
        stroke: safeParseObject(
          json['stroke'],
          parse: DivStroke.fromJson,
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

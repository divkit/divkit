// Generated code. Do not modify.

import 'package:divkit/src/schema/div_corners_radius.dart';
import 'package:divkit/src/schema/div_shadow.dart';
import 'package:divkit/src/schema/div_stroke.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Stroke around the element.
class DivBorder with EquatableMixin {
  const DivBorder({
    this.cornerRadius,
    this.cornersRadius,
    this.hasShadow = const ValueExpression(false),
    this.shadow,
    this.stroke,
  });

  /// One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
  // constraint: number >= 0
  final Expression<int>? cornerRadius;

  /// Multiple radii of element and stroke corner rounding.
  final DivCornersRadius? cornersRadius;

  /// Adding shadow.
  // default value: false
  final Expression<bool> hasShadow;

  /// Parameters of the shadow applied to the element stroke.
  final DivShadow? shadow;

  /// Stroke style.
  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        cornerRadius,
        cornersRadius,
        hasShadow,
        shadow,
        stroke,
      ];

  DivBorder copyWith({
    Expression<int>? Function()? cornerRadius,
    DivCornersRadius? Function()? cornersRadius,
    Expression<bool>? hasShadow,
    DivShadow? Function()? shadow,
    DivStroke? Function()? stroke,
  }) =>
      DivBorder(
        cornerRadius:
            cornerRadius != null ? cornerRadius.call() : this.cornerRadius,
        cornersRadius:
            cornersRadius != null ? cornersRadius.call() : this.cornersRadius,
        hasShadow: hasShadow ?? this.hasShadow,
        shadow: shadow != null ? shadow.call() : this.shadow,
        stroke: stroke != null ? stroke.call() : this.stroke,
      );

  static DivBorder? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivBorder(
        cornerRadius: safeParseIntExpr(
          json['corner_radius'],
        ),
        cornersRadius: safeParseObject(
          json['corners_radius'],
          parse: DivCornersRadius.fromJson,
        ),
        hasShadow: reqVProp<bool>(
          safeParseBoolExpr(
            json['has_shadow'],
            fallback: false,
          ),
          name: 'has_shadow',
        ),
        shadow: safeParseObject(
          json['shadow'],
          parse: DivShadow.fromJson,
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

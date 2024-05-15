// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_corners_radius.dart';
import 'div_shadow.dart';
import 'div_stroke.dart';

class DivBorder with EquatableMixin {
  const DivBorder({
    this.cornerRadius,
    this.cornersRadius,
    this.hasShadow = const ValueExpression(false),
    this.shadow,
    this.stroke,
  });

  // constraint: number >= 0
  final Expression<int>? cornerRadius;

  final DivCornersRadius? cornersRadius;
  // default value: false
  final Expression<bool> hasShadow;

  final DivShadow? shadow;

  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        cornerRadius,
        cornersRadius,
        hasShadow,
        shadow,
        stroke,
      ];

  static DivBorder? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivBorder(
      cornerRadius: safeParseIntExpr(
        json['corner_radius'],
      ),
      cornersRadius: safeParseObj(
        DivCornersRadius.fromJson(json['corners_radius']),
      ),
      hasShadow: safeParseBoolExpr(
        json['has_shadow'],
        fallback: false,
      )!,
      shadow: safeParseObj(
        DivShadow.fromJson(json['shadow']),
      ),
      stroke: safeParseObj(
        DivStroke.fromJson(json['stroke']),
      ),
    );
  }
}

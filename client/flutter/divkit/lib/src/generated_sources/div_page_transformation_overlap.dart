// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_animation_interpolator.dart';

class DivPageTransformationOverlap with EquatableMixin {
  const DivPageTransformationOverlap({
    this.interpolator =
        const ValueExpression(DivAnimationInterpolator.easeInOut),
    this.nextPageAlpha = const ValueExpression(1.0),
    this.nextPageScale = const ValueExpression(1.0),
    this.previousPageAlpha = const ValueExpression(1.0),
    this.previousPageScale = const ValueExpression(1.0),
    this.reversedStackingOrder = const ValueExpression(false),
  });

  static const type = "overlap";
  // default value: DivAnimationInterpolator.easeInOut
  final Expression<DivAnimationInterpolator> interpolator;
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  final Expression<double> nextPageAlpha;
  // constraint: number >= 0.0; default value: 1.0
  final Expression<double> nextPageScale;
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  final Expression<double> previousPageAlpha;
  // constraint: number >= 0.0; default value: 1.0
  final Expression<double> previousPageScale;
  // default value: false
  final Expression<bool> reversedStackingOrder;

  @override
  List<Object?> get props => [
        interpolator,
        nextPageAlpha,
        nextPageScale,
        previousPageAlpha,
        previousPageScale,
        reversedStackingOrder,
      ];

  static DivPageTransformationOverlap? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivPageTransformationOverlap(
      interpolator: safeParseStrEnumExpr(
        json['interpolator'],
        parse: DivAnimationInterpolator.fromJson,
        fallback: DivAnimationInterpolator.easeInOut,
      )!,
      nextPageAlpha: safeParseDoubleExpr(
        json['next_page_alpha'],
        fallback: 1.0,
      )!,
      nextPageScale: safeParseDoubleExpr(
        json['next_page_scale'],
        fallback: 1.0,
      )!,
      previousPageAlpha: safeParseDoubleExpr(
        json['previous_page_alpha'],
        fallback: 1.0,
      )!,
      previousPageScale: safeParseDoubleExpr(
        json['previous_page_scale'],
        fallback: 1.0,
      )!,
      reversedStackingOrder: safeParseBoolExpr(
        json['reversed_stacking_order'],
        fallback: false,
      )!,
    );
  }
}

// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_animation_interpolator.dart';
import 'package:divkit/src/generated_sources/div_transition_base.dart';

class DivScaleTransition with EquatableMixin implements DivTransitionBase {
  const DivScaleTransition({
    this.duration = const ValueExpression(200),
    this.interpolator =
        const ValueExpression(DivAnimationInterpolator.easeInOut),
    this.pivotX = const ValueExpression(0.5),
    this.pivotY = const ValueExpression(0.5),
    this.scale = const ValueExpression(0.0),
    this.startDelay = const ValueExpression(0),
  });

  static const type = "scale";
  // constraint: number >= 0; default value: 200
  @override
  final Expression<int> duration;
  // default value: DivAnimationInterpolator.easeInOut
  @override
  final Expression<DivAnimationInterpolator> interpolator;
  // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  final Expression<double> pivotX;
  // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  final Expression<double> pivotY;
  // constraint: number >= 0.0; default value: 0.0
  final Expression<double> scale;
  // constraint: number >= 0; default value: 0
  @override
  final Expression<int> startDelay;

  @override
  List<Object?> get props => [
        duration,
        interpolator,
        pivotX,
        pivotY,
        scale,
        startDelay,
      ];

  DivScaleTransition copyWith({
    Expression<int>? duration,
    Expression<DivAnimationInterpolator>? interpolator,
    Expression<double>? pivotX,
    Expression<double>? pivotY,
    Expression<double>? scale,
    Expression<int>? startDelay,
  }) =>
      DivScaleTransition(
        duration: duration ?? this.duration,
        interpolator: interpolator ?? this.interpolator,
        pivotX: pivotX ?? this.pivotX,
        pivotY: pivotY ?? this.pivotY,
        scale: scale ?? this.scale,
        startDelay: startDelay ?? this.startDelay,
      );

  static DivScaleTransition? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivScaleTransition(
      duration: safeParseIntExpr(
        json['duration'],
        fallback: 200,
      )!,
      interpolator: safeParseStrEnumExpr(
        json['interpolator'],
        parse: DivAnimationInterpolator.fromJson,
        fallback: DivAnimationInterpolator.easeInOut,
      )!,
      pivotX: safeParseDoubleExpr(
        json['pivot_x'],
        fallback: 0.5,
      )!,
      pivotY: safeParseDoubleExpr(
        json['pivot_y'],
        fallback: 0.5,
      )!,
      scale: safeParseDoubleExpr(
        json['scale'],
        fallback: 0.0,
      )!,
      startDelay: safeParseIntExpr(
        json['start_delay'],
        fallback: 0,
      )!,
    );
  }
}

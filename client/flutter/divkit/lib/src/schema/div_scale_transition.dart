// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_transition_base.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Scale animation.
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

  /// Animation duration in milliseconds.
  // constraint: number >= 0; default value: 200
  @override
  final Expression<int> duration;

  /// Transition speed nature.
  // default value: DivAnimationInterpolator.easeInOut
  @override
  final Expression<DivAnimationInterpolator> interpolator;

  /// Relative coordinate `X` of the point that won't change its position in case of scaling.
  // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  final Expression<double> pivotX;

  /// Relative coordinate `Y` of the point that won't change its position in case of scaling.
  // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  final Expression<double> pivotY;

  /// Value of the scale  from which the element starts appearing or at which it finishes disappearing.
  // constraint: number >= 0.0; default value: 0.0
  final Expression<double> scale;

  /// Delay in milliseconds before animation starts.
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

  static DivScaleTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivScaleTransition(
        duration: reqVProp<int>(
          safeParseIntExpr(
            json['duration'],
            fallback: 200,
          ),
          name: 'duration',
        ),
        interpolator: reqVProp<DivAnimationInterpolator>(
          safeParseStrEnumExpr(
            json['interpolator'],
            parse: DivAnimationInterpolator.fromJson,
            fallback: DivAnimationInterpolator.easeInOut,
          ),
          name: 'interpolator',
        ),
        pivotX: reqVProp<double>(
          safeParseDoubleExpr(
            json['pivot_x'],
            fallback: 0.5,
          ),
          name: 'pivot_x',
        ),
        pivotY: reqVProp<double>(
          safeParseDoubleExpr(
            json['pivot_y'],
            fallback: 0.5,
          ),
          name: 'pivot_y',
        ),
        scale: reqVProp<double>(
          safeParseDoubleExpr(
            json['scale'],
            fallback: 0.0,
          ),
          name: 'scale',
        ),
        startDelay: reqVProp<int>(
          safeParseIntExpr(
            json['start_delay'],
            fallback: 0,
          ),
          name: 'start_delay',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

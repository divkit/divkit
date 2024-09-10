// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_transition_base.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Scale animation.
class DivScaleTransition extends Preloadable
    with EquatableMixin
    implements DivTransitionBase {
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
    } catch (e) {
      return null;
    }
  }

  static Future<DivScaleTransition?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivScaleTransition(
        duration: (await safeParseIntExprAsync(
          json['duration'],
          fallback: 200,
        ))!,
        interpolator: (await safeParseStrEnumExprAsync(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
          fallback: DivAnimationInterpolator.easeInOut,
        ))!,
        pivotX: (await safeParseDoubleExprAsync(
          json['pivot_x'],
          fallback: 0.5,
        ))!,
        pivotY: (await safeParseDoubleExprAsync(
          json['pivot_y'],
          fallback: 0.5,
        ))!,
        scale: (await safeParseDoubleExprAsync(
          json['scale'],
          fallback: 0.0,
        ))!,
        startDelay: (await safeParseIntExprAsync(
          json['start_delay'],
          fallback: 0,
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await duration.preload(context);
      await interpolator.preload(context);
      await pivotX.preload(context);
      await pivotY.preload(context);
      await scale.preload(context);
      await startDelay.preload(context);
    } catch (e) {
      return;
    }
  }
}

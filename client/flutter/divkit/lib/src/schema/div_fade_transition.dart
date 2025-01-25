// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_transition_base.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Transparency animation.
class DivFadeTransition extends Resolvable
    with EquatableMixin
    implements DivTransitionBase {
  const DivFadeTransition({
    this.alpha = const ValueExpression(0.0),
    this.duration = const ValueExpression(200),
    this.interpolator =
        const ValueExpression(DivAnimationInterpolator.easeInOut),
    this.startDelay = const ValueExpression(0),
  });

  static const type = "fade";

  /// Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
  // constraint: number >= 0.0 && number <= 1.0; default value: 0.0
  final Expression<double> alpha;

  /// Animation duration in milliseconds.
  // constraint: number >= 0; default value: 200
  @override
  final Expression<int> duration;

  /// Transition speed nature.
  // default value: DivAnimationInterpolator.easeInOut
  @override
  final Expression<DivAnimationInterpolator> interpolator;

  /// Delay in milliseconds before animation starts.
  // constraint: number >= 0; default value: 0
  @override
  final Expression<int> startDelay;

  @override
  List<Object?> get props => [
        alpha,
        duration,
        interpolator,
        startDelay,
      ];

  DivFadeTransition copyWith({
    Expression<double>? alpha,
    Expression<int>? duration,
    Expression<DivAnimationInterpolator>? interpolator,
    Expression<int>? startDelay,
  }) =>
      DivFadeTransition(
        alpha: alpha ?? this.alpha,
        duration: duration ?? this.duration,
        interpolator: interpolator ?? this.interpolator,
        startDelay: startDelay ?? this.startDelay,
      );

  static DivFadeTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivFadeTransition(
        alpha: safeParseDoubleExpr(
          json['alpha'],
          fallback: 0.0,
        )!,
        duration: safeParseIntExpr(
          json['duration'],
          fallback: 200,
        )!,
        interpolator: safeParseStrEnumExpr(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
          fallback: DivAnimationInterpolator.easeInOut,
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

  @override
  DivFadeTransition resolve(DivVariableContext context) {
    alpha.resolve(context);
    duration.resolve(context);
    interpolator.resolve(context);
    startDelay.resolve(context);
    return this;
  }
}

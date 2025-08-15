// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_transition_base.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Element position and size change animation.
class DivChangeBoundsTransition
    with EquatableMixin
    implements DivTransitionBase {
  const DivChangeBoundsTransition({
    this.duration = const ValueExpression(200),
    this.interpolator =
        const ValueExpression(DivAnimationInterpolator.easeInOut),
    this.startDelay = const ValueExpression(0),
  });

  static const type = "change_bounds";

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
        duration,
        interpolator,
        startDelay,
      ];

  DivChangeBoundsTransition copyWith({
    Expression<int>? duration,
    Expression<DivAnimationInterpolator>? interpolator,
    Expression<int>? startDelay,
  }) =>
      DivChangeBoundsTransition(
        duration: duration ?? this.duration,
        interpolator: interpolator ?? this.interpolator,
        startDelay: startDelay ?? this.startDelay,
      );

  static DivChangeBoundsTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivChangeBoundsTransition(
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

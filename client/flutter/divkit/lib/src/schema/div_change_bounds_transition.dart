// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_transition_base.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Element position and size change animation.
class DivChangeBoundsTransition extends Preloadable
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

  static Future<DivChangeBoundsTransition?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivChangeBoundsTransition(
        duration: (await safeParseIntExprAsync(
          json['duration'],
          fallback: 200,
        ))!,
        interpolator: (await safeParseStrEnumExprAsync(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
          fallback: DivAnimationInterpolator.easeInOut,
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
      await startDelay.preload(context);
    } catch (e) {
      return;
    }
  }
}

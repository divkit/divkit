// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_animation_direction.dart';
import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Number animator.
class DivNumberAnimator extends Preloadable with EquatableMixin {
  const DivNumberAnimator({
    this.cancelActions,
    this.direction = const ValueExpression(DivAnimationDirection.normal),
    required this.duration,
    this.endActions,
    required this.endValue,
    required this.id,
    this.interpolator = const ValueExpression(DivAnimationInterpolator.linear),
    this.repeatCount = const ValueExpression(1),
    this.startDelay = const ValueExpression(0),
    this.startValue,
    required this.variableName,
  });

  static const type = "number_animator";

  /// Actions performed when the animator is cancelled. For example, when an action with `animator_stop` type is received
  final List<DivAction>? cancelActions;

  /// Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
  // default value: DivAnimationDirection.normal
  final Expression<DivAnimationDirection> direction;

  /// Animation duration in milliseconds.
  // constraint: number >= 0
  final Expression<int> duration;

  /// Actions performed when the animator completes animation.
  final List<DivAction>? endActions;

  /// Value that will be set at the end of animation.
  final Expression<double> endValue;

  /// Animator identificator
  final String id;

  /// Interpolation function.
  // default value: DivAnimationInterpolator.linear
  final Expression<DivAnimationInterpolator> interpolator;

  /// The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
  // constraint: number >= 0; default value: 1
  final Expression<int> repeatCount;

  /// Animation start delay in milliseconds.
  // constraint: number >= 0; default value: 0
  final Expression<int> startDelay;

  /// Value that will be set at the start of animation. Can be omitted, in which case current value of the variable will be used.
  final Expression<double>? startValue;

  /// Name of the variable being animated.
  final String variableName;

  @override
  List<Object?> get props => [
        cancelActions,
        direction,
        duration,
        endActions,
        endValue,
        id,
        interpolator,
        repeatCount,
        startDelay,
        startValue,
        variableName,
      ];

  DivNumberAnimator copyWith({
    List<DivAction>? Function()? cancelActions,
    Expression<DivAnimationDirection>? direction,
    Expression<int>? duration,
    List<DivAction>? Function()? endActions,
    Expression<double>? endValue,
    String? id,
    Expression<DivAnimationInterpolator>? interpolator,
    Expression<int>? repeatCount,
    Expression<int>? startDelay,
    Expression<double>? Function()? startValue,
    String? variableName,
  }) =>
      DivNumberAnimator(
        cancelActions:
            cancelActions != null ? cancelActions.call() : this.cancelActions,
        direction: direction ?? this.direction,
        duration: duration ?? this.duration,
        endActions: endActions != null ? endActions.call() : this.endActions,
        endValue: endValue ?? this.endValue,
        id: id ?? this.id,
        interpolator: interpolator ?? this.interpolator,
        repeatCount: repeatCount ?? this.repeatCount,
        startDelay: startDelay ?? this.startDelay,
        startValue: startValue != null ? startValue.call() : this.startValue,
        variableName: variableName ?? this.variableName,
      );

  static DivNumberAnimator? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivNumberAnimator(
        cancelActions: safeParseObj(
          safeListMap(
            json['cancel_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        direction: safeParseStrEnumExpr(
          json['direction'],
          parse: DivAnimationDirection.fromJson,
          fallback: DivAnimationDirection.normal,
        )!,
        duration: safeParseIntExpr(
          json['duration'],
        )!,
        endActions: safeParseObj(
          safeListMap(
            json['end_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        endValue: safeParseDoubleExpr(
          json['end_value'],
        )!,
        id: safeParseStr(
          json['id']?.toString(),
        )!,
        interpolator: safeParseStrEnumExpr(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
          fallback: DivAnimationInterpolator.linear,
        )!,
        repeatCount: safeParseIntExpr(
          json['repeat_count'],
          fallback: 1,
        )!,
        startDelay: safeParseIntExpr(
          json['start_delay'],
          fallback: 0,
        )!,
        startValue: safeParseDoubleExpr(
          json['start_value'],
        ),
        variableName: safeParseStr(
          json['variable_name']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivNumberAnimator?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivNumberAnimator(
        cancelActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['cancel_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        direction: (await safeParseStrEnumExprAsync(
          json['direction'],
          parse: DivAnimationDirection.fromJson,
          fallback: DivAnimationDirection.normal,
        ))!,
        duration: (await safeParseIntExprAsync(
          json['duration'],
        ))!,
        endActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['end_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        endValue: (await safeParseDoubleExprAsync(
          json['end_value'],
        ))!,
        id: (await safeParseStrAsync(
          json['id']?.toString(),
        ))!,
        interpolator: (await safeParseStrEnumExprAsync(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
          fallback: DivAnimationInterpolator.linear,
        ))!,
        repeatCount: (await safeParseIntExprAsync(
          json['repeat_count'],
          fallback: 1,
        ))!,
        startDelay: (await safeParseIntExprAsync(
          json['start_delay'],
          fallback: 0,
        ))!,
        startValue: await safeParseDoubleExprAsync(
          json['start_value'],
        ),
        variableName: (await safeParseStrAsync(
          json['variable_name']?.toString(),
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
      await safeFuturesWait(cancelActions, (v) => v.preload(context));
      await direction.preload(context);
      await duration.preload(context);
      await safeFuturesWait(endActions, (v) => v.preload(context));
      await endValue.preload(context);
      await interpolator.preload(context);
      await repeatCount.preload(context);
      await startDelay.preload(context);
      await startValue?.preload(context);
    } catch (e) {
      return;
    }
  }
}

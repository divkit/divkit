// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_animation_direction.dart';
import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_animator_base.dart';
import 'package:divkit/src/schema/div_count.dart';
import 'package:divkit/src/schema/div_fixed_count.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Color animator.
class DivColorAnimator extends Resolvable
    with EquatableMixin
    implements DivAnimatorBase {
  const DivColorAnimator({
    this.cancelActions,
    this.direction = const ValueExpression(DivAnimationDirection.normal),
    required this.duration,
    this.endActions,
    required this.endValue,
    required this.id,
    this.interpolator = const ValueExpression(DivAnimationInterpolator.linear),
    this.repeatCount = const DivCount.divFixedCount(
      DivFixedCount(
        value: ValueExpression(
          1,
        ),
      ),
    ),
    this.startDelay = const ValueExpression(0),
    this.startValue,
    required this.variableName,
  });

  static const type = "color_animator";

  /// Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received.
  @override
  final Arr<DivAction>? cancelActions;

  /// Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
  // default value: DivAnimationDirection.normal
  @override
  final Expression<DivAnimationDirection> direction;

  /// Animation duration in milliseconds.
  // constraint: number >= 0
  @override
  final Expression<int> duration;

  /// Actions when the animation is completed.
  @override
  final Arr<DivAction>? endActions;

  /// The value the variable will have when the animation ends.
  final Expression<Color> endValue;

  /// Animator ID.
  @override
  final String id;

  /// Animated value interpolation function.
  // default value: DivAnimationInterpolator.linear
  @override
  final Expression<DivAnimationInterpolator> interpolator;

  /// Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
  // default value: const DivCount.divFixedCount(const DivFixedCount(value: ValueExpression(1,),),)
  @override
  final DivCount repeatCount;

  /// Delay before the animation is launched in milliseconds.
  // constraint: number >= 0; default value: 0
  @override
  final Expression<int> startDelay;

  /// The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
  final Expression<Color>? startValue;

  /// Name of the variable being animated.
  @override
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

  DivColorAnimator copyWith({
    Arr<DivAction>? Function()? cancelActions,
    Expression<DivAnimationDirection>? direction,
    Expression<int>? duration,
    Arr<DivAction>? Function()? endActions,
    Expression<Color>? endValue,
    String? id,
    Expression<DivAnimationInterpolator>? interpolator,
    DivCount? repeatCount,
    Expression<int>? startDelay,
    Expression<Color>? Function()? startValue,
    String? variableName,
  }) =>
      DivColorAnimator(
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

  static DivColorAnimator? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivColorAnimator(
        cancelActions: safeParseObjects(
          json['cancel_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        direction: reqVProp<DivAnimationDirection>(
          safeParseStrEnumExpr(
            json['direction'],
            parse: DivAnimationDirection.fromJson,
            fallback: DivAnimationDirection.normal,
          ),
          name: 'direction',
        ),
        duration: reqVProp<int>(
          safeParseIntExpr(
            json['duration'],
          ),
          name: 'duration',
        ),
        endActions: safeParseObjects(
          json['end_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        endValue: reqVProp<Color>(
          safeParseColorExpr(
            json['end_value'],
          ),
          name: 'end_value',
        ),
        id: reqProp<String>(
          safeParseStr(
            json['id'],
          ),
          name: 'id',
        ),
        interpolator: reqVProp<DivAnimationInterpolator>(
          safeParseStrEnumExpr(
            json['interpolator'],
            parse: DivAnimationInterpolator.fromJson,
            fallback: DivAnimationInterpolator.linear,
          ),
          name: 'interpolator',
        ),
        repeatCount: reqProp<DivCount>(
          safeParseObject(
            json['repeat_count'],
            parse: DivCount.fromJson,
            fallback: const DivCount.divFixedCount(
              DivFixedCount(
                value: ValueExpression(
                  1,
                ),
              ),
            ),
          ),
          name: 'repeat_count',
        ),
        startDelay: reqVProp<int>(
          safeParseIntExpr(
            json['start_delay'],
            fallback: 0,
          ),
          name: 'start_delay',
        ),
        startValue: safeParseColorExpr(
          json['start_value'],
        ),
        variableName: reqProp<String>(
          safeParseStr(
            json['variable_name'],
          ),
          name: 'variable_name',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivColorAnimator resolve(DivVariableContext context) {
    tryResolveList(cancelActions, (v) => v.resolve(context));
    direction.resolve(context);
    duration.resolve(context);
    tryResolveList(endActions, (v) => v.resolve(context));
    endValue.resolve(context);
    interpolator.resolve(context);
    repeatCount.resolve(context);
    startDelay.resolve(context);
    startValue?.resolve(context);
    return this;
  }
}

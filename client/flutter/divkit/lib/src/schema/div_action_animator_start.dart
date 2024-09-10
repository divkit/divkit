// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_direction.dart';
import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Starts specified animator
class DivActionAnimatorStart extends Preloadable with EquatableMixin {
  const DivActionAnimatorStart({
    required this.animatorId,
    this.direction,
    this.duration,
    this.endValue,
    this.interpolator,
    this.repeatCount,
    this.startDelay,
    this.startValue,
  });

  static const type = "animator_start";

  /// The identifier of the animator being started.
  final Expression<String> animatorId;

  /// Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
  final Expression<DivAnimationDirection>? direction;

  /// Animation duration in milliseconds.
  // constraint: number >= 0
  final Expression<int>? duration;

  /// Overrides value that will be set at the end of animation.
  final DivTypedValue? endValue;

  /// Interpolation function.
  final Expression<DivAnimationInterpolator>? interpolator;

  /// The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
  // constraint: number >= 0
  final Expression<int>? repeatCount;

  /// Animation start delay in milliseconds.
  // constraint: number >= 0
  final Expression<int>? startDelay;

  /// Overrides value that will be set at the start of animation.
  final DivTypedValue? startValue;

  @override
  List<Object?> get props => [
        animatorId,
        direction,
        duration,
        endValue,
        interpolator,
        repeatCount,
        startDelay,
        startValue,
      ];

  DivActionAnimatorStart copyWith({
    Expression<String>? animatorId,
    Expression<DivAnimationDirection>? Function()? direction,
    Expression<int>? Function()? duration,
    DivTypedValue? Function()? endValue,
    Expression<DivAnimationInterpolator>? Function()? interpolator,
    Expression<int>? Function()? repeatCount,
    Expression<int>? Function()? startDelay,
    DivTypedValue? Function()? startValue,
  }) =>
      DivActionAnimatorStart(
        animatorId: animatorId ?? this.animatorId,
        direction: direction != null ? direction.call() : this.direction,
        duration: duration != null ? duration.call() : this.duration,
        endValue: endValue != null ? endValue.call() : this.endValue,
        interpolator:
            interpolator != null ? interpolator.call() : this.interpolator,
        repeatCount:
            repeatCount != null ? repeatCount.call() : this.repeatCount,
        startDelay: startDelay != null ? startDelay.call() : this.startDelay,
        startValue: startValue != null ? startValue.call() : this.startValue,
      );

  static DivActionAnimatorStart? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionAnimatorStart(
        animatorId: safeParseStrExpr(
          json['animator_id']?.toString(),
        )!,
        direction: safeParseStrEnumExpr(
          json['direction'],
          parse: DivAnimationDirection.fromJson,
        ),
        duration: safeParseIntExpr(
          json['duration'],
        ),
        endValue: safeParseObj(
          DivTypedValue.fromJson(json['end_value']),
        ),
        interpolator: safeParseStrEnumExpr(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
        ),
        repeatCount: safeParseIntExpr(
          json['repeat_count'],
        ),
        startDelay: safeParseIntExpr(
          json['start_delay'],
        ),
        startValue: safeParseObj(
          DivTypedValue.fromJson(json['start_value']),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionAnimatorStart?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionAnimatorStart(
        animatorId: (await safeParseStrExprAsync(
          json['animator_id']?.toString(),
        ))!,
        direction: await safeParseStrEnumExprAsync(
          json['direction'],
          parse: DivAnimationDirection.fromJson,
        ),
        duration: await safeParseIntExprAsync(
          json['duration'],
        ),
        endValue: await safeParseObjAsync(
          DivTypedValue.fromJson(json['end_value']),
        ),
        interpolator: await safeParseStrEnumExprAsync(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
        ),
        repeatCount: await safeParseIntExprAsync(
          json['repeat_count'],
        ),
        startDelay: await safeParseIntExprAsync(
          json['start_delay'],
        ),
        startValue: await safeParseObjAsync(
          DivTypedValue.fromJson(json['start_value']),
        ),
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
      await animatorId.preload(context);
      await direction?.preload(context);
      await duration?.preload(context);
      await endValue?.preload(context);
      await interpolator?.preload(context);
      await repeatCount?.preload(context);
      await startDelay?.preload(context);
      await startValue?.preload(context);
    } catch (e) {
      return;
    }
  }
}

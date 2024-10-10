// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_direction.dart';
import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_count.dart';
import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Launches the specified animator.
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

  /// ID of the animator launched.
  final String animatorId;

  /// Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
  final Expression<DivAnimationDirection>? direction;

  /// Animation duration in milliseconds.
  // constraint: number >= 0
  final Expression<int>? duration;

  /// Overrides the value that will be set after the animation finishes.
  final DivTypedValue? endValue;

  /// Animated value interpolation function.
  final Expression<DivAnimationInterpolator>? interpolator;

  /// Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
  final DivCount? repeatCount;

  /// Delay before the animation is launched in milliseconds.
  // constraint: number >= 0
  final Expression<int>? startDelay;

  /// Overrides the value that will be set before the animation begins.
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
    String? animatorId,
    Expression<DivAnimationDirection>? Function()? direction,
    Expression<int>? Function()? duration,
    DivTypedValue? Function()? endValue,
    Expression<DivAnimationInterpolator>? Function()? interpolator,
    DivCount? Function()? repeatCount,
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
        animatorId: safeParseStr(
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
        repeatCount: safeParseObj(
          DivCount.fromJson(json['repeat_count']),
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
        animatorId: (await safeParseStrAsync(
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
        repeatCount: await safeParseObjAsync(
          DivCount.fromJson(json['repeat_count']),
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

// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_count.dart';
import 'package:divkit/src/schema/div_infinity_count.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Element animation parameters.
class DivAnimation extends Resolvable with EquatableMixin {
  const DivAnimation({
    this.duration = const ValueExpression(300),
    this.endValue,
    this.interpolator = const ValueExpression(DivAnimationInterpolator.spring),
    this.items,
    required this.name,
    this.repeat = const DivCount.divInfinityCount(
      DivInfinityCount(),
    ),
    this.startDelay = const ValueExpression(0),
    this.startValue,
  });

  /// Animation duration in milliseconds.
  // constraint: number >= 0; default value: 300
  final Expression<int> duration;

  /// Final value of an animation.
  final Expression<double>? endValue;

  /// Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:
  /// • `linear` — cubic-bezier(0, 0, 1, 1);
  /// • `ease` — cubic-bezier(0.25, 0.1, 0.25, 1);
  /// • `ease_in` — cubic-bezier(0.42, 0, 1, 1);
  /// • `ease_out` — cubic-bezier(0, 0, 0.58, 1);
  /// • `ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).
  // default value: DivAnimationInterpolator.spring
  final Expression<DivAnimationInterpolator> interpolator;

  /// Animation elements.
  final Arr<DivAnimation>? items;

  /// Animation type.
  final Expression<DivAnimationName> name;

  /// Number of animation repetitions.
  // default value: const DivCount.divInfinityCount(DivInfinityCount(),)
  final DivCount repeat;

  /// Delay in milliseconds before animation starts.
  // constraint: number >= 0; default value: 0
  final Expression<int> startDelay;

  /// Starting value of an animation.
  final Expression<double>? startValue;

  @override
  List<Object?> get props => [
        duration,
        endValue,
        interpolator,
        items,
        name,
        repeat,
        startDelay,
        startValue,
      ];

  DivAnimation copyWith({
    Expression<int>? duration,
    Expression<double>? Function()? endValue,
    Expression<DivAnimationInterpolator>? interpolator,
    Arr<DivAnimation>? Function()? items,
    Expression<DivAnimationName>? name,
    DivCount? repeat,
    Expression<int>? startDelay,
    Expression<double>? Function()? startValue,
  }) =>
      DivAnimation(
        duration: duration ?? this.duration,
        endValue: endValue != null ? endValue.call() : this.endValue,
        interpolator: interpolator ?? this.interpolator,
        items: items != null ? items.call() : this.items,
        name: name ?? this.name,
        repeat: repeat ?? this.repeat,
        startDelay: startDelay ?? this.startDelay,
        startValue: startValue != null ? startValue.call() : this.startValue,
      );

  static DivAnimation? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivAnimation(
        duration: reqVProp<int>(
          safeParseIntExpr(
            json['duration'],
            fallback: 300,
          ),
          name: 'duration',
        ),
        endValue: safeParseDoubleExpr(
          json['end_value'],
        ),
        interpolator: reqVProp<DivAnimationInterpolator>(
          safeParseStrEnumExpr(
            json['interpolator'],
            parse: DivAnimationInterpolator.fromJson,
            fallback: DivAnimationInterpolator.spring,
          ),
          name: 'interpolator',
        ),
        items: safeParseObjects(
          json['items'],
          (v) => reqProp<DivAnimation>(
            safeParseObject(
              v,
              parse: DivAnimation.fromJson,
            ),
          ),
        ),
        name: reqVProp<DivAnimationName>(
          safeParseStrEnumExpr(
            json['name'],
            parse: DivAnimationName.fromJson,
          ),
          name: 'name',
        ),
        repeat: reqProp<DivCount>(
          safeParseObject(
            json['repeat'],
            parse: DivCount.fromJson,
            fallback: const DivCount.divInfinityCount(
              DivInfinityCount(),
            ),
          ),
          name: 'repeat',
        ),
        startDelay: reqVProp<int>(
          safeParseIntExpr(
            json['start_delay'],
            fallback: 0,
          ),
          name: 'start_delay',
        ),
        startValue: safeParseDoubleExpr(
          json['start_value'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivAnimation resolve(DivVariableContext context) {
    duration.resolve(context);
    endValue?.resolve(context);
    interpolator.resolve(context);
    name.resolve(context);
    repeat.resolve(context);
    startDelay.resolve(context);
    startValue?.resolve(context);
    return this;
  }
}

enum DivAnimationName implements Resolvable {
  fade('fade'),
  translate('translate'),
  scale('scale'),
  native('native'),
  set('set'),
  noAnimation('no_animation');

  final String value;

  const DivAnimationName(this.value);
  bool get isFade => this == fade;

  bool get isTranslate => this == translate;

  bool get isScale => this == scale;

  bool get isNative => this == native;

  bool get isSet => this == set;

  bool get isNoAnimation => this == noAnimation;

  T map<T>({
    required T Function() fade,
    required T Function() translate,
    required T Function() scale,
    required T Function() native,
    required T Function() set,
    required T Function() noAnimation,
  }) {
    switch (this) {
      case DivAnimationName.fade:
        return fade();
      case DivAnimationName.translate:
        return translate();
      case DivAnimationName.scale:
        return scale();
      case DivAnimationName.native:
        return native();
      case DivAnimationName.set:
        return set();
      case DivAnimationName.noAnimation:
        return noAnimation();
    }
  }

  T maybeMap<T>({
    T Function()? fade,
    T Function()? translate,
    T Function()? scale,
    T Function()? native,
    T Function()? set,
    T Function()? noAnimation,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivAnimationName.fade:
        return fade?.call() ?? orElse();
      case DivAnimationName.translate:
        return translate?.call() ?? orElse();
      case DivAnimationName.scale:
        return scale?.call() ?? orElse();
      case DivAnimationName.native:
        return native?.call() ?? orElse();
      case DivAnimationName.set:
        return set?.call() ?? orElse();
      case DivAnimationName.noAnimation:
        return noAnimation?.call() ?? orElse();
    }
  }

  static DivAnimationName? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'fade':
          return DivAnimationName.fade;
        case 'translate':
          return DivAnimationName.translate;
        case 'scale':
          return DivAnimationName.scale;
        case 'native':
          return DivAnimationName.native;
        case 'set':
          return DivAnimationName.set;
        case 'no_animation':
          return DivAnimationName.noAnimation;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivAnimationName: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivAnimationName resolve(DivVariableContext context) => this;
}

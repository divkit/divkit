// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_animation_interpolator.dart';
import 'div_count.dart';
import 'div_infinity_count.dart';

class DivAnimation with EquatableMixin {
  const DivAnimation({
    this.duration = const ValueExpression(300),
    this.endValue,
    this.interpolator = const ValueExpression(DivAnimationInterpolator.spring),
    this.items,
    required this.name,
    this.repeat = const DivCount(DivInfinityCount()),
    this.startDelay = const ValueExpression(0),
    this.startValue,
  });

  // constraint: number >= 0; default value: 300
  final Expression<int> duration;

  final Expression<double>? endValue;
  // default value: DivAnimationInterpolator.spring
  final Expression<DivAnimationInterpolator> interpolator;

  final List<DivAnimation>? items;

  final Expression<DivAnimationName> name;
  // default value: const DivCount(DivInfinityCount())
  final DivCount repeat;
  // constraint: number >= 0; default value: 0
  final Expression<int> startDelay;

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

  static DivAnimation? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivAnimation(
      duration: safeParseIntExpr(
        json['duration'],
        fallback: 300,
      )!,
      endValue: safeParseDoubleExpr(
        json['end_value'],
      ),
      interpolator: safeParseStrEnumExpr(
        json['interpolator'],
        parse: DivAnimationInterpolator.fromJson,
        fallback: DivAnimationInterpolator.spring,
      )!,
      items: safeParseObj(
        (json['items'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAnimation.fromJson(v),
              )!,
            )
            .toList(),
      ),
      name: safeParseStrEnumExpr(
        json['name'],
        parse: DivAnimationName.fromJson,
      )!,
      repeat: safeParseObj(
        DivCount.fromJson(json['repeat']),
        fallback: const DivCount(DivInfinityCount()),
      )!,
      startDelay: safeParseIntExpr(
        json['start_delay'],
        fallback: 0,
      )!,
      startValue: safeParseDoubleExpr(
        json['start_value'],
      ),
    );
  }
}

enum DivAnimationName {
  fade('fade'),
  translate('translate'),
  scale('scale'),
  native('native'),
  set('set'),
  noAnimation('no_animation');

  final String value;

  const DivAnimationName(this.value);

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

  static DivAnimationName? fromJson(String? json) {
    if (json == null) {
      return null;
    }
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
  }
}

// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_count.dart';
import 'package:divkit/src/schema/div_infinity_count.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivAnimation extends Preloadable with EquatableMixin {
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

  // constraint: number >= 0; default value: 300
  final Expression<int> duration;

  final Expression<double>? endValue;
  // default value: DivAnimationInterpolator.spring
  final Expression<DivAnimationInterpolator> interpolator;

  final List<DivAnimation>? items;

  final Expression<DivAnimationName> name;
  // default value: const DivCount.divInfinityCount(DivInfinityCount(),)
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

  DivAnimation copyWith({
    Expression<int>? duration,
    Expression<double>? Function()? endValue,
    Expression<DivAnimationInterpolator>? interpolator,
    List<DivAnimation>? Function()? items,
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
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              DivAnimation.fromJson(v),
            )!,
          ),
        ),
        name: safeParseStrEnumExpr(
          json['name'],
          parse: DivAnimationName.fromJson,
        )!,
        repeat: safeParseObj(
          DivCount.fromJson(json['repeat']),
          fallback: const DivCount.divInfinityCount(
            DivInfinityCount(),
          ),
        )!,
        startDelay: safeParseIntExpr(
          json['start_delay'],
          fallback: 0,
        )!,
        startValue: safeParseDoubleExpr(
          json['start_value'],
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivAnimation?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivAnimation(
        duration: (await safeParseIntExprAsync(
          json['duration'],
          fallback: 300,
        ))!,
        endValue: await safeParseDoubleExprAsync(
          json['end_value'],
        ),
        interpolator: (await safeParseStrEnumExprAsync(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
          fallback: DivAnimationInterpolator.spring,
        ))!,
        items: await safeParseObjAsync(
          await safeListMapAsync(
            json['items'],
            (v) => safeParseObj(
              DivAnimation.fromJson(v),
            )!,
          ),
        ),
        name: (await safeParseStrEnumExprAsync(
          json['name'],
          parse: DivAnimationName.fromJson,
        ))!,
        repeat: (await safeParseObjAsync(
          DivCount.fromJson(json['repeat']),
          fallback: const DivCount.divInfinityCount(
            DivInfinityCount(),
          ),
        ))!,
        startDelay: (await safeParseIntExprAsync(
          json['start_delay'],
          fallback: 0,
        ))!,
        startValue: await safeParseDoubleExprAsync(
          json['start_value'],
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
      await duration.preload(context);
      await endValue?.preload(context);
      await interpolator.preload(context);
      await safeFuturesWait(items, (v) => v.preload(context));
      await name.preload(context);
      await repeat.preload(context);
      await startDelay.preload(context);
      await startValue?.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivAnimationName implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivAnimationName?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}

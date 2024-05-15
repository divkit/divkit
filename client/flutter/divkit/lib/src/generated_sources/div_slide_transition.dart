// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_animation_interpolator.dart';
import 'div_dimension.dart';
import 'div_transition_base.dart';

class DivSlideTransition with EquatableMixin implements DivTransitionBase {
  const DivSlideTransition({
    this.distance,
    this.duration = const ValueExpression(200),
    this.edge = const ValueExpression(DivSlideTransitionEdge.bottom),
    this.interpolator =
        const ValueExpression(DivAnimationInterpolator.easeInOut),
    this.startDelay = const ValueExpression(0),
  });

  static const type = "slide";

  final DivDimension? distance;
  // constraint: number >= 0; default value: 200
  @override
  final Expression<int> duration;
  // default value: DivSlideTransitionEdge.bottom
  final Expression<DivSlideTransitionEdge> edge;
  // default value: DivAnimationInterpolator.easeInOut
  @override
  final Expression<DivAnimationInterpolator> interpolator;
  // constraint: number >= 0; default value: 0
  @override
  final Expression<int> startDelay;

  @override
  List<Object?> get props => [
        distance,
        duration,
        edge,
        interpolator,
        startDelay,
      ];

  static DivSlideTransition? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivSlideTransition(
      distance: safeParseObj(
        DivDimension.fromJson(json['distance']),
      ),
      duration: safeParseIntExpr(
        json['duration'],
        fallback: 200,
      )!,
      edge: safeParseStrEnumExpr(
        json['edge'],
        parse: DivSlideTransitionEdge.fromJson,
        fallback: DivSlideTransitionEdge.bottom,
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
  }
}

enum DivSlideTransitionEdge {
  left('left'),
  top('top'),
  right('right'),
  bottom('bottom');

  final String value;

  const DivSlideTransitionEdge(this.value);

  T map<T>({
    required T Function() left,
    required T Function() top,
    required T Function() right,
    required T Function() bottom,
  }) {
    switch (this) {
      case DivSlideTransitionEdge.left:
        return left();
      case DivSlideTransitionEdge.top:
        return top();
      case DivSlideTransitionEdge.right:
        return right();
      case DivSlideTransitionEdge.bottom:
        return bottom();
    }
  }

  T maybeMap<T>({
    T Function()? left,
    T Function()? top,
    T Function()? right,
    T Function()? bottom,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivSlideTransitionEdge.left:
        return left?.call() ?? orElse();
      case DivSlideTransitionEdge.top:
        return top?.call() ?? orElse();
      case DivSlideTransitionEdge.right:
        return right?.call() ?? orElse();
      case DivSlideTransitionEdge.bottom:
        return bottom?.call() ?? orElse();
    }
  }

  static DivSlideTransitionEdge? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'left':
        return DivSlideTransitionEdge.left;
      case 'top':
        return DivSlideTransitionEdge.top;
      case 'right':
        return DivSlideTransitionEdge.right;
      case 'bottom':
        return DivSlideTransitionEdge.bottom;
    }
    return null;
  }
}

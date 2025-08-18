// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_dimension.dart';
import 'package:divkit/src/schema/div_transition_base.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Slide animation.
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

  /// A fixed value of an offset which the element starts appearing from or at which it finishes disappearing. If no value is specified, the distance to the selected edge of a parent element is used.
  final DivDimension? distance;

  /// Animation duration in milliseconds.
  // constraint: number >= 0; default value: 200
  @override
  final Expression<int> duration;

  /// Edge of a parent element for one of the action types:
  /// • where the element will move from when appearing;
  /// • where the element will move to when disappearing.
  // default value: DivSlideTransitionEdge.bottom
  final Expression<DivSlideTransitionEdge> edge;

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
        distance,
        duration,
        edge,
        interpolator,
        startDelay,
      ];

  DivSlideTransition copyWith({
    DivDimension? Function()? distance,
    Expression<int>? duration,
    Expression<DivSlideTransitionEdge>? edge,
    Expression<DivAnimationInterpolator>? interpolator,
    Expression<int>? startDelay,
  }) =>
      DivSlideTransition(
        distance: distance != null ? distance.call() : this.distance,
        duration: duration ?? this.duration,
        edge: edge ?? this.edge,
        interpolator: interpolator ?? this.interpolator,
        startDelay: startDelay ?? this.startDelay,
      );

  static DivSlideTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivSlideTransition(
        distance: safeParseObject(
          json['distance'],
          parse: DivDimension.fromJson,
        ),
        duration: reqVProp<int>(
          safeParseIntExpr(
            json['duration'],
            fallback: 200,
          ),
          name: 'duration',
        ),
        edge: reqVProp<DivSlideTransitionEdge>(
          safeParseStrEnumExpr(
            json['edge'],
            parse: DivSlideTransitionEdge.fromJson,
            fallback: DivSlideTransitionEdge.bottom,
          ),
          name: 'edge',
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

enum DivSlideTransitionEdge {
  left('left'),
  top('top'),
  right('right'),
  bottom('bottom');

  final String value;

  const DivSlideTransitionEdge(this.value);
  bool get isLeft => this == left;

  bool get isTop => this == top;

  bool get isRight => this == right;

  bool get isBottom => this == bottom;

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

  static DivSlideTransitionEdge? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivSlideTransitionEdge: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }
}

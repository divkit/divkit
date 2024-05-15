// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_animation_interpolator.dart';
import 'div_transition_base.dart';

class DivChangeBoundsTransition
    with EquatableMixin
    implements DivTransitionBase {
  const DivChangeBoundsTransition({
    this.duration = const ValueExpression(200),
    this.interpolator =
        const ValueExpression(DivAnimationInterpolator.easeInOut),
    this.startDelay = const ValueExpression(0),
  });

  static const type = "change_bounds";
  // constraint: number >= 0; default value: 200
  @override
  final Expression<int> duration;
  // default value: DivAnimationInterpolator.easeInOut
  @override
  final Expression<DivAnimationInterpolator> interpolator;
  // constraint: number >= 0; default value: 0
  @override
  final Expression<int> startDelay;

  @override
  List<Object?> get props => [
        duration,
        interpolator,
        startDelay,
      ];

  static DivChangeBoundsTransition? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
  }
}

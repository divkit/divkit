// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_animation_direction.dart';
import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/schema/div_count.dart';
import 'package:divkit/src/utils/parsing.dart';

abstract class DivAnimatorBase {
  Arr<DivAction>? get cancelActions;

  // default value: DivAnimationDirection.normal
  Expression<DivAnimationDirection> get direction;

  // constraint: number >= 0
  Expression<int> get duration;

  Arr<DivAction>? get endActions;

  String get id;

  // default value: DivAnimationInterpolator.linear
  Expression<DivAnimationInterpolator> get interpolator;

  // default value: const DivCount.divFixedCount(const DivFixedCount(value: ValueExpression(1,),),)
  DivCount get repeatCount;

  // constraint: number >= 0; default value: 0
  Expression<int> get startDelay;

  String get variableName;
}

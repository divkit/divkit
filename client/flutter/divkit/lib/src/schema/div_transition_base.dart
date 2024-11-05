// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/utils/parsing_utils.dart';

abstract class DivTransitionBase extends Resolvable {
  // constraint: number >= 0; default value: 200
  Expression<int> get duration;

  // default value: DivAnimationInterpolator.easeInOut
  Expression<DivAnimationInterpolator> get interpolator;

  // constraint: number >= 0; default value: 0
  Expression<int> get startDelay;
  @override
  DivTransitionBase resolve(DivVariableContext context);
}

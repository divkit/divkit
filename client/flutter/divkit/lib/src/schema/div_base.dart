// Generated code. Do not modify.

import 'package:divkit/src/schema/div_accessibility.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_animator.dart';
import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_function.dart';
import 'package:divkit/src/schema/div_layout_provider.dart';
import 'package:divkit/src/schema/div_size.dart';
import 'package:divkit/src/schema/div_tooltip.dart';
import 'package:divkit/src/schema/div_transform.dart';
import 'package:divkit/src/schema/div_transition_trigger.dart';
import 'package:divkit/src/schema/div_trigger.dart';
import 'package:divkit/src/schema/div_variable.dart';
import 'package:divkit/src/schema/div_visibility.dart';
import 'package:divkit/src/schema/div_visibility_action.dart';
import 'package:divkit/src/utils/parsing.dart';

abstract class DivBase {
  DivAccessibility get accessibility;

  Expression<DivAlignmentHorizontal>? get alignmentHorizontal;

  Expression<DivAlignmentVertical>? get alignmentVertical;

  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  Expression<double> get alpha;

  Arr<DivAnimator>? get animators;

  Arr<DivBackground>? get background;

  DivBorder get border;

  // constraint: number >= 0
  Expression<int>? get columnSpan;

  Arr<DivDisappearAction>? get disappearActions;

  Arr<DivExtension>? get extensions;

  DivFocus? get focus;

  Arr<DivFunction>? get functions;

  // default value: const DivSize.divWrapContentSize(DivWrapContentSize(),)
  DivSize get height;

  String? get id;

  DivLayoutProvider? get layoutProvider;

  DivEdgeInsets get margins;

  DivEdgeInsets get paddings;

  Expression<String>? get reuseId;

  // constraint: number >= 0
  Expression<int>? get rowSpan;

  Arr<DivAction>? get selectedActions;

  Arr<DivTooltip>? get tooltips;

  DivTransform get transform;

  DivChangeTransition? get transitionChange;

  DivAppearanceTransition? get transitionIn;

  DivAppearanceTransition? get transitionOut;

  // at least 1 elements
  Arr<DivTransitionTrigger>? get transitionTriggers;

  Arr<DivTrigger>? get variableTriggers;

  Arr<DivVariable>? get variables;

  // default value: DivVisibility.visible
  Expression<DivVisibility> get visibility;

  DivVisibilityAction? get visibilityAction;

  Arr<DivVisibilityAction>? get visibilityActions;

  // default value: const DivSize.divMatchParentSize(DivMatchParentSize(),)
  DivSize get width;
}

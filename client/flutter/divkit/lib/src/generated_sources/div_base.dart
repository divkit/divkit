// Generated code. Do not modify.

import '../utils/parsing_utils.dart';
import 'div_accessibility.dart';
import 'div_action.dart';
import 'div_alignment_horizontal.dart';
import 'div_alignment_vertical.dart';
import 'div_appearance_transition.dart';
import 'div_background.dart';
import 'div_border.dart';
import 'div_change_transition.dart';
import 'div_disappear_action.dart';
import 'div_edge_insets.dart';
import 'div_extension.dart';
import 'div_focus.dart';
import 'div_match_parent_size.dart';
import 'div_size.dart';
import 'div_tooltip.dart';
import 'div_transform.dart';
import 'div_transition_trigger.dart';
import 'div_visibility.dart';
import 'div_visibility_action.dart';
import 'div_wrap_content_size.dart';

abstract class DivBase {
  DivAccessibility get accessibility;

  Expression<DivAlignmentHorizontal>? get alignmentHorizontal;

  Expression<DivAlignmentVertical>? get alignmentVertical;

  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  Expression<double> get alpha;

  List<DivBackground>? get background;

  DivBorder get border;

  // constraint: number >= 0
  Expression<int>? get columnSpan;

  List<DivDisappearAction>? get disappearActions;

  List<DivExtension>? get extensions;

  DivFocus? get focus;

  // default value: const DivSize(DivWrapContentSize())
  DivSize get height;

  String? get id;

  DivEdgeInsets get margins;

  DivEdgeInsets get paddings;

  // constraint: number >= 0
  Expression<int>? get rowSpan;

  List<DivAction>? get selectedActions;

  List<DivTooltip>? get tooltips;

  DivTransform get transform;

  DivChangeTransition? get transitionChange;

  DivAppearanceTransition? get transitionIn;

  DivAppearanceTransition? get transitionOut;

  // at least 1 elements
  List<DivTransitionTrigger>? get transitionTriggers;

  // default value: DivVisibility.visible
  Expression<DivVisibility> get visibility;

  DivVisibilityAction? get visibilityAction;

  List<DivVisibilityAction>? get visibilityActions;

  // default value: const DivSize(DivMatchParentSize())
  DivSize get width;
}

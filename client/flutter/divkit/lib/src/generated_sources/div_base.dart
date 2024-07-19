// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_accessibility.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_alignment_horizontal.dart';
import 'package:divkit/src/generated_sources/div_alignment_vertical.dart';
import 'package:divkit/src/generated_sources/div_appearance_transition.dart';
import 'package:divkit/src/generated_sources/div_background.dart';
import 'package:divkit/src/generated_sources/div_border.dart';
import 'package:divkit/src/generated_sources/div_change_transition.dart';
import 'package:divkit/src/generated_sources/div_disappear_action.dart';
import 'package:divkit/src/generated_sources/div_edge_insets.dart';
import 'package:divkit/src/generated_sources/div_extension.dart';
import 'package:divkit/src/generated_sources/div_focus.dart';
import 'package:divkit/src/generated_sources/div_layout_provider.dart';
import 'package:divkit/src/generated_sources/div_size.dart';
import 'package:divkit/src/generated_sources/div_tooltip.dart';
import 'package:divkit/src/generated_sources/div_transform.dart';
import 'package:divkit/src/generated_sources/div_transition_trigger.dart';
import 'package:divkit/src/generated_sources/div_variable.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/generated_sources/div_visibility_action.dart';

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

  // default value: const DivSize.divWrapContentSize(DivWrapContentSize())
  DivSize get height;

  String? get id;

  DivLayoutProvider? get layoutProvider;

  DivEdgeInsets get margins;

  DivEdgeInsets get paddings;

  Expression<String>? get reuseId;

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

  List<DivVariable>? get variables;

  // default value: DivVisibility.visible
  Expression<DivVisibility> get visibility;

  DivVisibilityAction? get visibilityAction;

  List<DivVisibilityAction>? get visibilityActions;

  // default value: const DivSize.divMatchParentSize(DivMatchParentSize())
  DivSize get width;
}

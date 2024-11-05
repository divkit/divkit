// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_accessibility.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_animation.dart';
import 'package:divkit/src/schema/div_animator.dart';
import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_base.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_function.dart';
import 'package:divkit/src/schema/div_layout_provider.dart';
import 'package:divkit/src/schema/div_match_parent_size.dart';
import 'package:divkit/src/schema/div_size.dart';
import 'package:divkit/src/schema/div_tooltip.dart';
import 'package:divkit/src/schema/div_transform.dart';
import 'package:divkit/src/schema/div_transition_selector.dart';
import 'package:divkit/src/schema/div_transition_trigger.dart';
import 'package:divkit/src/schema/div_trigger.dart';
import 'package:divkit/src/schema/div_variable.dart';
import 'package:divkit/src/schema/div_visibility.dart';
import 'package:divkit/src/schema/div_visibility_action.dart';
import 'package:divkit/src/schema/div_wrap_content_size.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// It contains sets of states for visual elements and switches between them.
class DivState extends Resolvable with EquatableMixin implements DivBase {
  const DivState({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.animators,
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.defaultStateId,
    this.disappearActions,
    this.divId,
    this.extensions,
    this.focus,
    this.functions,
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.id,
    this.layoutProvider,
    this.margins = const DivEdgeInsets(),
    this.paddings = const DivEdgeInsets(),
    this.reuseId,
    this.rowSpan,
    this.selectedActions,
    this.stateIdVariable,
    required this.states,
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionAnimationSelector =
        const ValueExpression(DivTransitionSelector.stateChange),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    this.variableTriggers,
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(
      DivMatchParentSize(),
    ),
  });

  static const type = "state";

  /// Accessibility settings.
  @override
  final DivAccessibility accessibility;

  /// Horizontal alignment of an element inside the parent element.
  @override
  final Expression<DivAlignmentHorizontal>? alignmentHorizontal;

  /// Vertical alignment of an element inside the parent element.
  @override
  final Expression<DivAlignmentVertical>? alignmentVertical;

  /// Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  @override
  final Expression<double> alpha;

  /// Declaration of animators that change variable values over time.
  @override
  final List<DivAnimator>? animators;

  /// Element background. It can contain multiple layers.
  @override
  final List<DivBackground>? background;

  /// Element stroke.
  @override
  final DivBorder border;

  /// Merges cells in a column of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;

  /// ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
  final Expression<String>? defaultStateId;

  /// Actions when an element disappears from the screen.
  @override
  final List<DivDisappearAction>? disappearActions;

  /// ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
  final String? divId;

  /// Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](https://divkit.tech/docs/en/concepts/extensions).
  @override
  final List<DivExtension>? extensions;

  /// Parameters when focusing on an element or losing focus.
  @override
  final DivFocus? focus;

  /// User functions.
  @override
  final List<DivFunction>? functions;

  /// Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](https://divkit.tech/docs/en/concepts/layout).
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize(),)
  @override
  final DivSize height;

  /// Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
  @override
  final String? id;

  /// Provides data on the actual size of the element.
  @override
  final DivLayoutProvider? layoutProvider;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Internal margins from the element stroke.
  @override
  final DivEdgeInsets paddings;

  /// ID for the div object structure. Used to optimize block reuse. See [block reuse](https://divkit.tech/docs/en/concepts/reuse/reuse.md).
  @override
  final Expression<String>? reuseId;

  /// Merges cells in a string of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  /// List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
  @override
  final List<DivAction>? selectedActions;

  /// The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
  final String? stateIdVariable;

  /// States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](https://divkit.tech/docs/en/concepts/interaction) of the [action](div-action.md) element.
  // at least 1 elements
  final List<DivStateState> states;

  /// Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
  @override
  final List<DivTooltip>? tooltips;

  /// Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
  @override
  final DivTransform transform;

  /// It determines which events trigger transition animations.
  // default value: DivTransitionSelector.stateChange
  final Expression<DivTransitionSelector> transitionAnimationSelector;

  /// Change animation. It is played when the position or size of an element changes in the new layout.
  @override
  final DivChangeTransition? transitionChange;

  /// Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](https://divkit.tech/docs/en/concepts/interaction#animation/transition-animation).
  @override
  final DivAppearanceTransition? transitionIn;

  /// Disappearance animation. It is played when an element disappears in the new layout.
  @override
  final DivAppearanceTransition? transitionOut;

  /// Animation starting triggers. Default value: `[state_change, visibility_change]`.
  // at least 1 elements
  @override
  final List<DivTransitionTrigger>? transitionTriggers;

  /// Triggers for changing variables within an element.
  @override
  final List<DivTrigger>? variableTriggers;

  /// Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
  @override
  final List<DivVariable>? variables;

  /// Element visibility.
  // default value: DivVisibility.visible
  @override
  final Expression<DivVisibility> visibility;

  /// Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
  @override
  final DivVisibilityAction? visibilityAction;

  /// Actions when an element appears on the screen.
  @override
  final List<DivVisibilityAction>? visibilityActions;

  /// Element width.
  // default value: const DivSize.divMatchParentSize(DivMatchParentSize(),)
  @override
  final DivSize width;

  @override
  List<Object?> get props => [
        accessibility,
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        animators,
        background,
        border,
        columnSpan,
        defaultStateId,
        disappearActions,
        divId,
        extensions,
        focus,
        functions,
        height,
        id,
        layoutProvider,
        margins,
        paddings,
        reuseId,
        rowSpan,
        selectedActions,
        stateIdVariable,
        states,
        tooltips,
        transform,
        transitionAnimationSelector,
        transitionChange,
        transitionIn,
        transitionOut,
        transitionTriggers,
        variableTriggers,
        variables,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  DivState copyWith({
    DivAccessibility? accessibility,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivAnimator>? Function()? animators,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    Expression<String>? Function()? defaultStateId,
    List<DivDisappearAction>? Function()? disappearActions,
    String? Function()? divId,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    List<DivFunction>? Function()? functions,
    DivSize? height,
    String? Function()? id,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    DivEdgeInsets? paddings,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    List<DivAction>? Function()? selectedActions,
    String? Function()? stateIdVariable,
    List<DivStateState>? states,
    List<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    Expression<DivTransitionSelector>? transitionAnimationSelector,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    List<DivTransitionTrigger>? Function()? transitionTriggers,
    List<DivTrigger>? Function()? variableTriggers,
    List<DivVariable>? Function()? variables,
    Expression<DivVisibility>? visibility,
    DivVisibilityAction? Function()? visibilityAction,
    List<DivVisibilityAction>? Function()? visibilityActions,
    DivSize? width,
  }) =>
      DivState(
        accessibility: accessibility ?? this.accessibility,
        alignmentHorizontal: alignmentHorizontal != null
            ? alignmentHorizontal.call()
            : this.alignmentHorizontal,
        alignmentVertical: alignmentVertical != null
            ? alignmentVertical.call()
            : this.alignmentVertical,
        alpha: alpha ?? this.alpha,
        animators: animators != null ? animators.call() : this.animators,
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        defaultStateId: defaultStateId != null
            ? defaultStateId.call()
            : this.defaultStateId,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        divId: divId != null ? divId.call() : this.divId,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        functions: functions != null ? functions.call() : this.functions,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        margins: margins ?? this.margins,
        paddings: paddings ?? this.paddings,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        stateIdVariable: stateIdVariable != null
            ? stateIdVariable.call()
            : this.stateIdVariable,
        states: states ?? this.states,
        tooltips: tooltips != null ? tooltips.call() : this.tooltips,
        transform: transform ?? this.transform,
        transitionAnimationSelector:
            transitionAnimationSelector ?? this.transitionAnimationSelector,
        transitionChange: transitionChange != null
            ? transitionChange.call()
            : this.transitionChange,
        transitionIn:
            transitionIn != null ? transitionIn.call() : this.transitionIn,
        transitionOut:
            transitionOut != null ? transitionOut.call() : this.transitionOut,
        transitionTriggers: transitionTriggers != null
            ? transitionTriggers.call()
            : this.transitionTriggers,
        variableTriggers: variableTriggers != null
            ? variableTriggers.call()
            : this.variableTriggers,
        variables: variables != null ? variables.call() : this.variables,
        visibility: visibility ?? this.visibility,
        visibilityAction: visibilityAction != null
            ? visibilityAction.call()
            : this.visibilityAction,
        visibilityActions: visibilityActions != null
            ? visibilityActions.call()
            : this.visibilityActions,
        width: width ?? this.width,
      );

  static DivState? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivState(
        accessibility: safeParseObj(
          DivAccessibility.fromJson(json['accessibility']),
          fallback: const DivAccessibility(),
        )!,
        alignmentHorizontal: safeParseStrEnumExpr(
          json['alignment_horizontal'],
          parse: DivAlignmentHorizontal.fromJson,
        ),
        alignmentVertical: safeParseStrEnumExpr(
          json['alignment_vertical'],
          parse: DivAlignmentVertical.fromJson,
        ),
        alpha: safeParseDoubleExpr(
          json['alpha'],
          fallback: 1.0,
        )!,
        animators: safeParseObj(
          safeListMap(
            json['animators'],
            (v) => safeParseObj(
              DivAnimator.fromJson(v),
            )!,
          ),
        ),
        background: safeParseObj(
          safeListMap(
            json['background'],
            (v) => safeParseObj(
              DivBackground.fromJson(v),
            )!,
          ),
        ),
        border: safeParseObj(
          DivBorder.fromJson(json['border']),
          fallback: const DivBorder(),
        )!,
        columnSpan: safeParseIntExpr(
          json['column_span'],
        ),
        defaultStateId: safeParseStrExpr(
          json['default_state_id']?.toString(),
        ),
        disappearActions: safeParseObj(
          safeListMap(
            json['disappear_actions'],
            (v) => safeParseObj(
              DivDisappearAction.fromJson(v),
            )!,
          ),
        ),
        divId: safeParseStr(
          json['div_id']?.toString(),
        ),
        extensions: safeParseObj(
          safeListMap(
            json['extensions'],
            (v) => safeParseObj(
              DivExtension.fromJson(v),
            )!,
          ),
        ),
        focus: safeParseObj(
          DivFocus.fromJson(json['focus']),
        ),
        functions: safeParseObj(
          safeListMap(
            json['functions'],
            (v) => safeParseObj(
              DivFunction.fromJson(v),
            )!,
          ),
        ),
        height: safeParseObj(
          DivSize.fromJson(json['height']),
          fallback: const DivSize.divWrapContentSize(
            DivWrapContentSize(),
          ),
        )!,
        id: safeParseStr(
          json['id']?.toString(),
        ),
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        margins: safeParseObj(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        )!,
        paddings: safeParseObj(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        )!,
        reuseId: safeParseStrExpr(
          json['reuse_id']?.toString(),
        ),
        rowSpan: safeParseIntExpr(
          json['row_span'],
        ),
        selectedActions: safeParseObj(
          safeListMap(
            json['selected_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        stateIdVariable: safeParseStr(
          json['state_id_variable']?.toString(),
        ),
        states: safeParseObj(
          safeListMap(
            json['states'],
            (v) => safeParseObj(
              DivStateState.fromJson(v),
            )!,
          ),
        )!,
        tooltips: safeParseObj(
          safeListMap(
            json['tooltips'],
            (v) => safeParseObj(
              DivTooltip.fromJson(v),
            )!,
          ),
        ),
        transform: safeParseObj(
          DivTransform.fromJson(json['transform']),
          fallback: const DivTransform(),
        )!,
        transitionAnimationSelector: safeParseStrEnumExpr(
          json['transition_animation_selector'],
          parse: DivTransitionSelector.fromJson,
          fallback: DivTransitionSelector.stateChange,
        )!,
        transitionChange: safeParseObj(
          DivChangeTransition.fromJson(json['transition_change']),
        ),
        transitionIn: safeParseObj(
          DivAppearanceTransition.fromJson(json['transition_in']),
        ),
        transitionOut: safeParseObj(
          DivAppearanceTransition.fromJson(json['transition_out']),
        ),
        transitionTriggers: safeParseObj(
          safeListMap(
            json['transition_triggers'],
            (v) => safeParseStrEnum(
              v,
              parse: DivTransitionTrigger.fromJson,
            )!,
          ),
        ),
        variableTriggers: safeParseObj(
          safeListMap(
            json['variable_triggers'],
            (v) => safeParseObj(
              DivTrigger.fromJson(v),
            )!,
          ),
        ),
        variables: safeParseObj(
          safeListMap(
            json['variables'],
            (v) => safeParseObj(
              DivVariable.fromJson(v),
            )!,
          ),
        ),
        visibility: safeParseStrEnumExpr(
          json['visibility'],
          parse: DivVisibility.fromJson,
          fallback: DivVisibility.visible,
        )!,
        visibilityAction: safeParseObj(
          DivVisibilityAction.fromJson(json['visibility_action']),
        ),
        visibilityActions: safeParseObj(
          safeListMap(
            json['visibility_actions'],
            (v) => safeParseObj(
              DivVisibilityAction.fromJson(v),
            )!,
          ),
        ),
        width: safeParseObj(
          DivSize.fromJson(json['width']),
          fallback: const DivSize.divMatchParentSize(
            DivMatchParentSize(),
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivState resolve(DivVariableContext context) {
    accessibility.resolve(context);
    alignmentHorizontal?.resolve(context);
    alignmentVertical?.resolve(context);
    alpha.resolve(context);
    safeListResolve(animators, (v) => v.resolve(context));
    safeListResolve(background, (v) => v.resolve(context));
    border.resolve(context);
    columnSpan?.resolve(context);
    defaultStateId?.resolve(context);
    safeListResolve(disappearActions, (v) => v.resolve(context));
    safeListResolve(extensions, (v) => v.resolve(context));
    focus?.resolve(context);
    safeListResolve(functions, (v) => v.resolve(context));
    height.resolve(context);
    layoutProvider?.resolve(context);
    margins.resolve(context);
    paddings.resolve(context);
    reuseId?.resolve(context);
    rowSpan?.resolve(context);
    safeListResolve(selectedActions, (v) => v.resolve(context));
    safeListResolve(states, (v) => v.resolve(context));
    safeListResolve(tooltips, (v) => v.resolve(context));
    transform.resolve(context);
    transitionAnimationSelector.resolve(context);
    transitionChange?.resolve(context);
    transitionIn?.resolve(context);
    transitionOut?.resolve(context);
    safeListResolve(transitionTriggers, (v) => v.resolve(context));
    safeListResolve(variableTriggers, (v) => v.resolve(context));
    safeListResolve(variables, (v) => v.resolve(context));
    visibility.resolve(context);
    visibilityAction?.resolve(context);
    safeListResolve(visibilityActions, (v) => v.resolve(context));
    width.resolve(context);
    return this;
  }
}

class DivStateState extends Resolvable with EquatableMixin {
  const DivStateState({
    this.animationIn,
    this.animationOut,
    this.div,
    required this.stateId,
    this.swipeOutActions,
  });

  /// State appearance animation. Use `transition_in` instead.
  final DivAnimation? animationIn;

  /// State disappearance animation. Use `transition_out` instead.
  final DivAnimation? animationOut;

  /// Contents. If the parameter is missing, the state won't be displayed.
  final Div? div;

  /// State ID. It must be unique at one hierarchy level.
  final String stateId;

  /// Actions when swiping the state horizontally.
  final List<DivAction>? swipeOutActions;

  @override
  List<Object?> get props => [
        animationIn,
        animationOut,
        div,
        stateId,
        swipeOutActions,
      ];

  DivStateState copyWith({
    DivAnimation? Function()? animationIn,
    DivAnimation? Function()? animationOut,
    Div? Function()? div,
    String? stateId,
    List<DivAction>? Function()? swipeOutActions,
  }) =>
      DivStateState(
        animationIn:
            animationIn != null ? animationIn.call() : this.animationIn,
        animationOut:
            animationOut != null ? animationOut.call() : this.animationOut,
        div: div != null ? div.call() : this.div,
        stateId: stateId ?? this.stateId,
        swipeOutActions: swipeOutActions != null
            ? swipeOutActions.call()
            : this.swipeOutActions,
      );

  static DivStateState? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivStateState(
        animationIn: safeParseObj(
          DivAnimation.fromJson(json['animation_in']),
        ),
        animationOut: safeParseObj(
          DivAnimation.fromJson(json['animation_out']),
        ),
        div: safeParseObj(
          Div.fromJson(json['div']),
        ),
        stateId: safeParseStr(
          json['state_id']?.toString(),
        )!,
        swipeOutActions: safeParseObj(
          safeListMap(
            json['swipe_out_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivStateState resolve(DivVariableContext context) {
    animationIn?.resolve(context);
    animationOut?.resolve(context);
    safeListResolve(swipeOutActions, (v) => v.resolve(context));
    return this;
  }
}

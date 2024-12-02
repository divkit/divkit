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
import 'package:divkit/src/utils/parsing.dart';
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
    this.clipToBounds = const ValueExpression(true),
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
  final Arr<DivAnimator>? animators;

  /// Element background. It can contain multiple layers.
  @override
  final Arr<DivBackground>? background;

  /// Element stroke.
  @override
  final DivBorder border;

  /// Enables the bounding of child elements by the parent's borders.
  // default value: true
  final Expression<bool> clipToBounds;

  /// Merges cells in a column of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;

  /// ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
  final Expression<String>? defaultStateId;

  /// Actions when an element disappears from the screen.
  @override
  final Arr<DivDisappearAction>? disappearActions;

  /// ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
  final String? divId;

  /// Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](https://divkit.tech/docs/en/concepts/extensions).
  @override
  final Arr<DivExtension>? extensions;

  /// Parameters when focusing on an element or losing focus.
  @override
  final DivFocus? focus;

  /// User functions.
  @override
  final Arr<DivFunction>? functions;

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
  final Arr<DivAction>? selectedActions;

  /// The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
  final String? stateIdVariable;

  /// States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](https://divkit.tech/docs/en/concepts/interaction) of the [action](div-action.md) element.
  // at least 1 elements
  final Arr<DivStateState> states;

  /// Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
  @override
  final Arr<DivTooltip>? tooltips;

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
  final Arr<DivTransitionTrigger>? transitionTriggers;

  /// Triggers for changing variables within an element.
  @override
  final Arr<DivTrigger>? variableTriggers;

  /// Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
  @override
  final Arr<DivVariable>? variables;

  /// Element visibility.
  // default value: DivVisibility.visible
  @override
  final Expression<DivVisibility> visibility;

  /// Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
  @override
  final DivVisibilityAction? visibilityAction;

  /// Actions when an element appears on the screen.
  @override
  final Arr<DivVisibilityAction>? visibilityActions;

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
        clipToBounds,
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
    Arr<DivAnimator>? Function()? animators,
    Arr<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<bool>? clipToBounds,
    Expression<int>? Function()? columnSpan,
    Expression<String>? Function()? defaultStateId,
    Arr<DivDisappearAction>? Function()? disappearActions,
    String? Function()? divId,
    Arr<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    Arr<DivFunction>? Function()? functions,
    DivSize? height,
    String? Function()? id,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    DivEdgeInsets? paddings,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Arr<DivAction>? Function()? selectedActions,
    String? Function()? stateIdVariable,
    Arr<DivStateState>? states,
    Arr<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    Expression<DivTransitionSelector>? transitionAnimationSelector,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    Arr<DivTransitionTrigger>? Function()? transitionTriggers,
    Arr<DivTrigger>? Function()? variableTriggers,
    Arr<DivVariable>? Function()? variables,
    Expression<DivVisibility>? visibility,
    DivVisibilityAction? Function()? visibilityAction,
    Arr<DivVisibilityAction>? Function()? visibilityActions,
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
        clipToBounds: clipToBounds ?? this.clipToBounds,
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
        accessibility: reqProp<DivAccessibility>(
          safeParseObject(
            json['accessibility'],
            parse: DivAccessibility.fromJson,
            fallback: const DivAccessibility(),
          ),
          name: 'accessibility',
        ),
        alignmentHorizontal: safeParseStrEnumExpr(
          json['alignment_horizontal'],
          parse: DivAlignmentHorizontal.fromJson,
        ),
        alignmentVertical: safeParseStrEnumExpr(
          json['alignment_vertical'],
          parse: DivAlignmentVertical.fromJson,
        ),
        alpha: reqVProp<double>(
          safeParseDoubleExpr(
            json['alpha'],
            fallback: 1.0,
          ),
          name: 'alpha',
        ),
        animators: safeParseObjects(
          json['animators'],
          (v) => reqProp<DivAnimator>(
            safeParseObject(
              v,
              parse: DivAnimator.fromJson,
            ),
          ),
        ),
        background: safeParseObjects(
          json['background'],
          (v) => reqProp<DivBackground>(
            safeParseObject(
              v,
              parse: DivBackground.fromJson,
            ),
          ),
        ),
        border: reqProp<DivBorder>(
          safeParseObject(
            json['border'],
            parse: DivBorder.fromJson,
            fallback: const DivBorder(),
          ),
          name: 'border',
        ),
        clipToBounds: reqVProp<bool>(
          safeParseBoolExpr(
            json['clip_to_bounds'],
            fallback: true,
          ),
          name: 'clip_to_bounds',
        ),
        columnSpan: safeParseIntExpr(
          json['column_span'],
        ),
        defaultStateId: safeParseStrExpr(
          json['default_state_id'],
        ),
        disappearActions: safeParseObjects(
          json['disappear_actions'],
          (v) => reqProp<DivDisappearAction>(
            safeParseObject(
              v,
              parse: DivDisappearAction.fromJson,
            ),
          ),
        ),
        divId: safeParseStr(
          json['div_id'],
        ),
        extensions: safeParseObjects(
          json['extensions'],
          (v) => reqProp<DivExtension>(
            safeParseObject(
              v,
              parse: DivExtension.fromJson,
            ),
          ),
        ),
        focus: safeParseObject(
          json['focus'],
          parse: DivFocus.fromJson,
        ),
        functions: safeParseObjects(
          json['functions'],
          (v) => reqProp<DivFunction>(
            safeParseObject(
              v,
              parse: DivFunction.fromJson,
            ),
          ),
        ),
        height: reqProp<DivSize>(
          safeParseObject(
            json['height'],
            parse: DivSize.fromJson,
            fallback: const DivSize.divWrapContentSize(
              DivWrapContentSize(),
            ),
          ),
          name: 'height',
        ),
        id: safeParseStr(
          json['id'],
        ),
        layoutProvider: safeParseObject(
          json['layout_provider'],
          parse: DivLayoutProvider.fromJson,
        ),
        margins: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['margins'],
            parse: DivEdgeInsets.fromJson,
            fallback: const DivEdgeInsets(),
          ),
          name: 'margins',
        ),
        paddings: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['paddings'],
            parse: DivEdgeInsets.fromJson,
            fallback: const DivEdgeInsets(),
          ),
          name: 'paddings',
        ),
        reuseId: safeParseStrExpr(
          json['reuse_id'],
        ),
        rowSpan: safeParseIntExpr(
          json['row_span'],
        ),
        selectedActions: safeParseObjects(
          json['selected_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        stateIdVariable: safeParseStr(
          json['state_id_variable'],
        ),
        states: reqProp<Arr<DivStateState>>(
          safeParseObjects(
            json['states'],
            (v) => reqProp<DivStateState>(
              safeParseObject(
                v,
                parse: DivStateState.fromJson,
              ),
            ),
          ),
          name: 'states',
        ),
        tooltips: safeParseObjects(
          json['tooltips'],
          (v) => reqProp<DivTooltip>(
            safeParseObject(
              v,
              parse: DivTooltip.fromJson,
            ),
          ),
        ),
        transform: reqProp<DivTransform>(
          safeParseObject(
            json['transform'],
            parse: DivTransform.fromJson,
            fallback: const DivTransform(),
          ),
          name: 'transform',
        ),
        transitionAnimationSelector: reqVProp<DivTransitionSelector>(
          safeParseStrEnumExpr(
            json['transition_animation_selector'],
            parse: DivTransitionSelector.fromJson,
            fallback: DivTransitionSelector.stateChange,
          ),
          name: 'transition_animation_selector',
        ),
        transitionChange: safeParseObject(
          json['transition_change'],
          parse: DivChangeTransition.fromJson,
        ),
        transitionIn: safeParseObject(
          json['transition_in'],
          parse: DivAppearanceTransition.fromJson,
        ),
        transitionOut: safeParseObject(
          json['transition_out'],
          parse: DivAppearanceTransition.fromJson,
        ),
        transitionTriggers: safeParseObjects(
          json['transition_triggers'],
          (v) => reqProp<DivTransitionTrigger>(
            safeParseStrEnum(
              v,
              parse: DivTransitionTrigger.fromJson,
            ),
          ),
        ),
        variableTriggers: safeParseObjects(
          json['variable_triggers'],
          (v) => reqProp<DivTrigger>(
            safeParseObject(
              v,
              parse: DivTrigger.fromJson,
            ),
          ),
        ),
        variables: safeParseObjects(
          json['variables'],
          (v) => reqProp<DivVariable>(
            safeParseObject(
              v,
              parse: DivVariable.fromJson,
            ),
          ),
        ),
        visibility: reqVProp<DivVisibility>(
          safeParseStrEnumExpr(
            json['visibility'],
            parse: DivVisibility.fromJson,
            fallback: DivVisibility.visible,
          ),
          name: 'visibility',
        ),
        visibilityAction: safeParseObject(
          json['visibility_action'],
          parse: DivVisibilityAction.fromJson,
        ),
        visibilityActions: safeParseObjects(
          json['visibility_actions'],
          (v) => reqProp<DivVisibilityAction>(
            safeParseObject(
              v,
              parse: DivVisibilityAction.fromJson,
            ),
          ),
        ),
        width: reqProp<DivSize>(
          safeParseObject(
            json['width'],
            parse: DivSize.fromJson,
            fallback: const DivSize.divMatchParentSize(
              DivMatchParentSize(),
            ),
          ),
          name: 'width',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivState resolve(DivVariableContext context) {
    accessibility.resolve(context);
    alignmentHorizontal?.resolve(context);
    alignmentVertical?.resolve(context);
    alpha.resolve(context);
    tryResolveList(animators, (v) => v.resolve(context));
    tryResolveList(background, (v) => v.resolve(context));
    border.resolve(context);
    clipToBounds.resolve(context);
    columnSpan?.resolve(context);
    defaultStateId?.resolve(context);
    tryResolveList(disappearActions, (v) => v.resolve(context));
    tryResolveList(extensions, (v) => v.resolve(context));
    focus?.resolve(context);
    tryResolveList(functions, (v) => v.resolve(context));
    height.resolve(context);
    layoutProvider?.resolve(context);
    margins.resolve(context);
    paddings.resolve(context);
    reuseId?.resolve(context);
    rowSpan?.resolve(context);
    tryResolveList(selectedActions, (v) => v.resolve(context));
    tryResolveList(states, (v) => v.resolve(context));
    tryResolveList(tooltips, (v) => v.resolve(context));
    transform.resolve(context);
    transitionAnimationSelector.resolve(context);
    transitionChange?.resolve(context);
    transitionIn?.resolve(context);
    transitionOut?.resolve(context);
    tryResolveList(transitionTriggers, (v) => v.resolve(context));
    tryResolveList(variableTriggers, (v) => v.resolve(context));
    tryResolveList(variables, (v) => v.resolve(context));
    visibility.resolve(context);
    visibilityAction?.resolve(context);
    tryResolveList(visibilityActions, (v) => v.resolve(context));
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
  final Arr<DivAction>? swipeOutActions;

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
    Arr<DivAction>? Function()? swipeOutActions,
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
        animationIn: safeParseObject(
          json['animation_in'],
          parse: DivAnimation.fromJson,
        ),
        animationOut: safeParseObject(
          json['animation_out'],
          parse: DivAnimation.fromJson,
        ),
        div: safeParseObject(
          json['div'],
          parse: Div.fromJson,
        ),
        stateId: reqProp<String>(
          safeParseStr(
            json['state_id'],
          ),
          name: 'state_id',
        ),
        swipeOutActions: safeParseObjects(
          json['swipe_out_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivStateState resolve(DivVariableContext context) {
    animationIn?.resolve(context);
    animationOut?.resolve(context);
    tryResolveList(swipeOutActions, (v) => v.resolve(context));
    return this;
  }
}

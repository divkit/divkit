// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_accessibility.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_animation.dart';
import 'package:divkit/src/schema/div_animator.dart';
import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/schema/div_aspect.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_base.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/schema/div_collection_item_builder.dart';
import 'package:divkit/src/schema/div_content_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_content_alignment_vertical.dart';
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_drawable.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_function.dart';
import 'package:divkit/src/schema/div_layout_provider.dart';
import 'package:divkit/src/schema/div_match_parent_size.dart';
import 'package:divkit/src/schema/div_size.dart';
import 'package:divkit/src/schema/div_tooltip.dart';
import 'package:divkit/src/schema/div_transform.dart';
import 'package:divkit/src/schema/div_transition_trigger.dart';
import 'package:divkit/src/schema/div_trigger.dart';
import 'package:divkit/src/schema/div_variable.dart';
import 'package:divkit/src/schema/div_visibility.dart';
import 'package:divkit/src/schema/div_visibility_action.dart';
import 'package:divkit/src/schema/div_wrap_content_size.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Container. It contains other elements and is responsible for their location. It is used to arrange elements vertically, horizontally, and with an overlay in a certain order, simulating the third dimension.
class DivContainer extends Resolvable with EquatableMixin implements DivBase {
  const DivContainer({
    this.accessibility = const DivAccessibility(),
    this.action,
    this.actionAnimation = const DivAnimation(
      duration: ValueExpression(
        100,
      ),
      endValue: ValueExpression(
        0.6,
      ),
      name: ValueExpression(
        DivAnimationName.fade,
      ),
      startValue: ValueExpression(
        1,
      ),
    ),
    this.actions,
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.animators,
    this.aspect,
    this.background,
    this.border = const DivBorder(),
    this.clipToBounds = const ValueExpression(true),
    this.columnSpan,
    this.contentAlignmentHorizontal =
        const ValueExpression(DivContentAlignmentHorizontal.start),
    this.contentAlignmentVertical =
        const ValueExpression(DivContentAlignmentVertical.top),
    this.disappearActions,
    this.doubletapActions,
    this.extensions,
    this.focus,
    this.functions,
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.hoverEndActions,
    this.hoverStartActions,
    this.id,
    this.itemBuilder,
    this.items,
    this.layoutMode = const ValueExpression(DivContainerLayoutMode.noWrap),
    this.layoutProvider,
    this.lineSeparator,
    this.longtapActions,
    this.margins = const DivEdgeInsets(),
    this.orientation = const ValueExpression(DivContainerOrientation.vertical),
    this.paddings = const DivEdgeInsets(),
    this.pressEndActions,
    this.pressStartActions,
    this.reuseId,
    this.rowSpan,
    this.selectedActions,
    this.separator,
    this.tooltips,
    this.transform = const DivTransform(),
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

  static const type = "container";

  /// Accessibility settings.
  @override
  final DivAccessibility accessibility;

  /// One action when clicking on an element. Not used if the `actions` parameter is set.
  final DivAction? action;

  /// Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
  // default value: const DivAnimation(duration: ValueExpression(100,), endValue: ValueExpression(0.6,), name: ValueExpression(DivAnimationName.fade,), startValue: ValueExpression(1,),)
  final DivAnimation actionAnimation;

  /// Multiple actions when clicking on an element.
  final Arr<DivAction>? actions;

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

  /// Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value.
  /// On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
  final DivAspect? aspect;

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

  /// Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
  // default value: DivContentAlignmentHorizontal.start
  final Expression<DivContentAlignmentHorizontal> contentAlignmentHorizontal;

  /// Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
  // default value: DivContentAlignmentVertical.top
  final Expression<DivContentAlignmentVertical> contentAlignmentVertical;

  /// Actions when an element disappears from the screen.
  @override
  final Arr<DivDisappearAction>? disappearActions;

  /// Action when double-clicking on an element.
  final Arr<DivAction>? doubletapActions;

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

  /// Actions performed when hovering over an element ends. Available on platforms with pointing device support (mouse, stylus, etc).
  final Arr<DivAction>? hoverEndActions;

  /// Actions performed when hovering over an element. Available on platforms with pointing device support (mouse, stylus, etc).
  final Arr<DivAction>? hoverStartActions;

  /// Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
  @override
  final String? id;

  /// Sets collection elements dynamically using `data` and `prototypes`.
  final DivCollectionItemBuilder? itemBuilder;

  /// Nested elements.
  final Arr<Div>? items;

  /// Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:
  /// • A separate line is allocated for each element along the main axis with the size value set to `match_parent`.
  /// • Elements along the cross axis with the size value `match_parent` are ignored.
  // default value: DivContainerLayoutMode.noWrap
  final Expression<DivContainerLayoutMode> layoutMode;

  /// Provides data on the actual size of the element.
  @override
  final DivLayoutProvider? layoutProvider;

  /// Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
  final DivContainerSeparator? lineSeparator;

  /// Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
  final Arr<DivAction>? longtapActions;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
  // default value: DivContainerOrientation.vertical
  final Expression<DivContainerOrientation> orientation;

  /// Internal margins from the element stroke.
  @override
  final DivEdgeInsets paddings;

  /// Actions performed when an element is released.
  final Arr<DivAction>? pressEndActions;

  /// Actions performed when an element is pressed.
  final Arr<DivAction>? pressStartActions;

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

  /// Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
  final DivContainerSeparator? separator;

  /// Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
  @override
  final Arr<DivTooltip>? tooltips;

  /// Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
  @override
  final DivTransform transform;

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
        action,
        actionAnimation,
        actions,
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        animators,
        aspect,
        background,
        border,
        clipToBounds,
        columnSpan,
        contentAlignmentHorizontal,
        contentAlignmentVertical,
        disappearActions,
        doubletapActions,
        extensions,
        focus,
        functions,
        height,
        hoverEndActions,
        hoverStartActions,
        id,
        itemBuilder,
        items,
        layoutMode,
        layoutProvider,
        lineSeparator,
        longtapActions,
        margins,
        orientation,
        paddings,
        pressEndActions,
        pressStartActions,
        reuseId,
        rowSpan,
        selectedActions,
        separator,
        tooltips,
        transform,
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

  DivContainer copyWith({
    DivAccessibility? accessibility,
    DivAction? Function()? action,
    DivAnimation? actionAnimation,
    Arr<DivAction>? Function()? actions,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    Arr<DivAnimator>? Function()? animators,
    DivAspect? Function()? aspect,
    Arr<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<bool>? clipToBounds,
    Expression<int>? Function()? columnSpan,
    Expression<DivContentAlignmentHorizontal>? contentAlignmentHorizontal,
    Expression<DivContentAlignmentVertical>? contentAlignmentVertical,
    Arr<DivDisappearAction>? Function()? disappearActions,
    Arr<DivAction>? Function()? doubletapActions,
    Arr<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    Arr<DivFunction>? Function()? functions,
    DivSize? height,
    Arr<DivAction>? Function()? hoverEndActions,
    Arr<DivAction>? Function()? hoverStartActions,
    String? Function()? id,
    DivCollectionItemBuilder? Function()? itemBuilder,
    Arr<Div>? Function()? items,
    Expression<DivContainerLayoutMode>? layoutMode,
    DivLayoutProvider? Function()? layoutProvider,
    DivContainerSeparator? Function()? lineSeparator,
    Arr<DivAction>? Function()? longtapActions,
    DivEdgeInsets? margins,
    Expression<DivContainerOrientation>? orientation,
    DivEdgeInsets? paddings,
    Arr<DivAction>? Function()? pressEndActions,
    Arr<DivAction>? Function()? pressStartActions,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Arr<DivAction>? Function()? selectedActions,
    DivContainerSeparator? Function()? separator,
    Arr<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
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
      DivContainer(
        accessibility: accessibility ?? this.accessibility,
        action: action != null ? action.call() : this.action,
        actionAnimation: actionAnimation ?? this.actionAnimation,
        actions: actions != null ? actions.call() : this.actions,
        alignmentHorizontal: alignmentHorizontal != null
            ? alignmentHorizontal.call()
            : this.alignmentHorizontal,
        alignmentVertical: alignmentVertical != null
            ? alignmentVertical.call()
            : this.alignmentVertical,
        alpha: alpha ?? this.alpha,
        animators: animators != null ? animators.call() : this.animators,
        aspect: aspect != null ? aspect.call() : this.aspect,
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        clipToBounds: clipToBounds ?? this.clipToBounds,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        contentAlignmentHorizontal:
            contentAlignmentHorizontal ?? this.contentAlignmentHorizontal,
        contentAlignmentVertical:
            contentAlignmentVertical ?? this.contentAlignmentVertical,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        doubletapActions: doubletapActions != null
            ? doubletapActions.call()
            : this.doubletapActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        functions: functions != null ? functions.call() : this.functions,
        height: height ?? this.height,
        hoverEndActions: hoverEndActions != null
            ? hoverEndActions.call()
            : this.hoverEndActions,
        hoverStartActions: hoverStartActions != null
            ? hoverStartActions.call()
            : this.hoverStartActions,
        id: id != null ? id.call() : this.id,
        itemBuilder:
            itemBuilder != null ? itemBuilder.call() : this.itemBuilder,
        items: items != null ? items.call() : this.items,
        layoutMode: layoutMode ?? this.layoutMode,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        lineSeparator:
            lineSeparator != null ? lineSeparator.call() : this.lineSeparator,
        longtapActions: longtapActions != null
            ? longtapActions.call()
            : this.longtapActions,
        margins: margins ?? this.margins,
        orientation: orientation ?? this.orientation,
        paddings: paddings ?? this.paddings,
        pressEndActions: pressEndActions != null
            ? pressEndActions.call()
            : this.pressEndActions,
        pressStartActions: pressStartActions != null
            ? pressStartActions.call()
            : this.pressStartActions,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        separator: separator != null ? separator.call() : this.separator,
        tooltips: tooltips != null ? tooltips.call() : this.tooltips,
        transform: transform ?? this.transform,
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

  static DivContainer? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivContainer(
        accessibility: reqProp<DivAccessibility>(
          safeParseObject(
            json['accessibility'],
            parse: DivAccessibility.fromJson,
            fallback: const DivAccessibility(),
          ),
          name: 'accessibility',
        ),
        action: safeParseObject(
          json['action'],
          parse: DivAction.fromJson,
        ),
        actionAnimation: reqProp<DivAnimation>(
          safeParseObject(
            json['action_animation'],
            parse: DivAnimation.fromJson,
            fallback: const DivAnimation(
              duration: ValueExpression(
                100,
              ),
              endValue: ValueExpression(
                0.6,
              ),
              name: ValueExpression(
                DivAnimationName.fade,
              ),
              startValue: ValueExpression(
                1,
              ),
            ),
          ),
          name: 'action_animation',
        ),
        actions: safeParseObjects(
          json['actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
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
        aspect: safeParseObject(
          json['aspect'],
          parse: DivAspect.fromJson,
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
        contentAlignmentHorizontal: reqVProp<DivContentAlignmentHorizontal>(
          safeParseStrEnumExpr(
            json['content_alignment_horizontal'],
            parse: DivContentAlignmentHorizontal.fromJson,
            fallback: DivContentAlignmentHorizontal.start,
          ),
          name: 'content_alignment_horizontal',
        ),
        contentAlignmentVertical: reqVProp<DivContentAlignmentVertical>(
          safeParseStrEnumExpr(
            json['content_alignment_vertical'],
            parse: DivContentAlignmentVertical.fromJson,
            fallback: DivContentAlignmentVertical.top,
          ),
          name: 'content_alignment_vertical',
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
        doubletapActions: safeParseObjects(
          json['doubletap_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
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
        hoverEndActions: safeParseObjects(
          json['hover_end_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        hoverStartActions: safeParseObjects(
          json['hover_start_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        id: safeParseStr(
          json['id'],
        ),
        itemBuilder: safeParseObject(
          json['item_builder'],
          parse: DivCollectionItemBuilder.fromJson,
        ),
        items: safeParseObjects(
          json['items'],
          (v) => reqProp<Div>(
            safeParseObject(
              v,
              parse: Div.fromJson,
            ),
          ),
        ),
        layoutMode: reqVProp<DivContainerLayoutMode>(
          safeParseStrEnumExpr(
            json['layout_mode'],
            parse: DivContainerLayoutMode.fromJson,
            fallback: DivContainerLayoutMode.noWrap,
          ),
          name: 'layout_mode',
        ),
        layoutProvider: safeParseObject(
          json['layout_provider'],
          parse: DivLayoutProvider.fromJson,
        ),
        lineSeparator: safeParseObject(
          json['line_separator'],
          parse: DivContainerSeparator.fromJson,
        ),
        longtapActions: safeParseObjects(
          json['longtap_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        margins: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['margins'],
            parse: DivEdgeInsets.fromJson,
            fallback: const DivEdgeInsets(),
          ),
          name: 'margins',
        ),
        orientation: reqVProp<DivContainerOrientation>(
          safeParseStrEnumExpr(
            json['orientation'],
            parse: DivContainerOrientation.fromJson,
            fallback: DivContainerOrientation.vertical,
          ),
          name: 'orientation',
        ),
        paddings: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['paddings'],
            parse: DivEdgeInsets.fromJson,
            fallback: const DivEdgeInsets(),
          ),
          name: 'paddings',
        ),
        pressEndActions: safeParseObjects(
          json['press_end_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        pressStartActions: safeParseObjects(
          json['press_start_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
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
        separator: safeParseObject(
          json['separator'],
          parse: DivContainerSeparator.fromJson,
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
  DivContainer resolve(DivVariableContext context) {
    accessibility.resolve(context);
    action?.resolve(context);
    actionAnimation.resolve(context);
    tryResolveList(actions, (v) => v.resolve(context));
    alignmentHorizontal?.resolve(context);
    alignmentVertical?.resolve(context);
    alpha.resolve(context);
    tryResolveList(animators, (v) => v.resolve(context));
    aspect?.resolve(context);
    tryResolveList(background, (v) => v.resolve(context));
    border.resolve(context);
    clipToBounds.resolve(context);
    columnSpan?.resolve(context);
    contentAlignmentHorizontal.resolve(context);
    contentAlignmentVertical.resolve(context);
    tryResolveList(disappearActions, (v) => v.resolve(context));
    tryResolveList(doubletapActions, (v) => v.resolve(context));
    tryResolveList(extensions, (v) => v.resolve(context));
    focus?.resolve(context);
    tryResolveList(functions, (v) => v.resolve(context));
    height.resolve(context);
    tryResolveList(hoverEndActions, (v) => v.resolve(context));
    tryResolveList(hoverStartActions, (v) => v.resolve(context));
    itemBuilder?.resolve(context);
    layoutMode.resolve(context);
    layoutProvider?.resolve(context);
    lineSeparator?.resolve(context);
    tryResolveList(longtapActions, (v) => v.resolve(context));
    margins.resolve(context);
    orientation.resolve(context);
    paddings.resolve(context);
    tryResolveList(pressEndActions, (v) => v.resolve(context));
    tryResolveList(pressStartActions, (v) => v.resolve(context));
    reuseId?.resolve(context);
    rowSpan?.resolve(context);
    tryResolveList(selectedActions, (v) => v.resolve(context));
    separator?.resolve(context);
    tryResolveList(tooltips, (v) => v.resolve(context));
    transform.resolve(context);
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

class DivContainerSeparator extends Resolvable with EquatableMixin {
  const DivContainerSeparator({
    this.margins = const DivEdgeInsets(),
    this.showAtEnd = const ValueExpression(false),
    this.showAtStart = const ValueExpression(false),
    this.showBetween = const ValueExpression(true),
    required this.style,
  });

  /// External margins from the element stroke.
  final DivEdgeInsets margins;

  /// Enables displaying the separator after the last item.
  // default value: false
  final Expression<bool> showAtEnd;

  /// Enables displaying the separator before the first item.
  // default value: false
  final Expression<bool> showAtStart;

  /// Enables displaying the separator between items.
  // default value: true
  final Expression<bool> showBetween;

  /// Separator style.
  final DivDrawable style;

  @override
  List<Object?> get props => [
        margins,
        showAtEnd,
        showAtStart,
        showBetween,
        style,
      ];

  DivContainerSeparator copyWith({
    DivEdgeInsets? margins,
    Expression<bool>? showAtEnd,
    Expression<bool>? showAtStart,
    Expression<bool>? showBetween,
    DivDrawable? style,
  }) =>
      DivContainerSeparator(
        margins: margins ?? this.margins,
        showAtEnd: showAtEnd ?? this.showAtEnd,
        showAtStart: showAtStart ?? this.showAtStart,
        showBetween: showBetween ?? this.showBetween,
        style: style ?? this.style,
      );

  static DivContainerSeparator? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivContainerSeparator(
        margins: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['margins'],
            parse: DivEdgeInsets.fromJson,
            fallback: const DivEdgeInsets(),
          ),
          name: 'margins',
        ),
        showAtEnd: reqVProp<bool>(
          safeParseBoolExpr(
            json['show_at_end'],
            fallback: false,
          ),
          name: 'show_at_end',
        ),
        showAtStart: reqVProp<bool>(
          safeParseBoolExpr(
            json['show_at_start'],
            fallback: false,
          ),
          name: 'show_at_start',
        ),
        showBetween: reqVProp<bool>(
          safeParseBoolExpr(
            json['show_between'],
            fallback: true,
          ),
          name: 'show_between',
        ),
        style: reqProp<DivDrawable>(
          safeParseObject(
            json['style'],
            parse: DivDrawable.fromJson,
          ),
          name: 'style',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivContainerSeparator resolve(DivVariableContext context) {
    margins.resolve(context);
    showAtEnd.resolve(context);
    showAtStart.resolve(context);
    showBetween.resolve(context);
    style.resolve(context);
    return this;
  }
}

enum DivContainerOrientation implements Resolvable {
  vertical('vertical'),
  horizontal('horizontal'),
  overlap('overlap');

  final String value;

  const DivContainerOrientation(this.value);
  bool get isVertical => this == vertical;

  bool get isHorizontal => this == horizontal;

  bool get isOverlap => this == overlap;

  T map<T>({
    required T Function() vertical,
    required T Function() horizontal,
    required T Function() overlap,
  }) {
    switch (this) {
      case DivContainerOrientation.vertical:
        return vertical();
      case DivContainerOrientation.horizontal:
        return horizontal();
      case DivContainerOrientation.overlap:
        return overlap();
    }
  }

  T maybeMap<T>({
    T Function()? vertical,
    T Function()? horizontal,
    T Function()? overlap,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivContainerOrientation.vertical:
        return vertical?.call() ?? orElse();
      case DivContainerOrientation.horizontal:
        return horizontal?.call() ?? orElse();
      case DivContainerOrientation.overlap:
        return overlap?.call() ?? orElse();
    }
  }

  static DivContainerOrientation? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'vertical':
          return DivContainerOrientation.vertical;
        case 'horizontal':
          return DivContainerOrientation.horizontal;
        case 'overlap':
          return DivContainerOrientation.overlap;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivContainerOrientation: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivContainerOrientation resolve(DivVariableContext context) => this;
}

enum DivContainerLayoutMode implements Resolvable {
  noWrap('no_wrap'),
  wrap('wrap');

  final String value;

  const DivContainerLayoutMode(this.value);
  bool get isNoWrap => this == noWrap;

  bool get isWrap => this == wrap;

  T map<T>({
    required T Function() noWrap,
    required T Function() wrap,
  }) {
    switch (this) {
      case DivContainerLayoutMode.noWrap:
        return noWrap();
      case DivContainerLayoutMode.wrap:
        return wrap();
    }
  }

  T maybeMap<T>({
    T Function()? noWrap,
    T Function()? wrap,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivContainerLayoutMode.noWrap:
        return noWrap?.call() ?? orElse();
      case DivContainerLayoutMode.wrap:
        return wrap?.call() ?? orElse();
    }
  }

  static DivContainerLayoutMode? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'no_wrap':
          return DivContainerLayoutMode.noWrap;
        case 'wrap':
          return DivContainerLayoutMode.wrap;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivContainerLayoutMode: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivContainerLayoutMode resolve(DivVariableContext context) => this;
}

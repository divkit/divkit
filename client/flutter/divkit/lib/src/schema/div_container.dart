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
import 'package:divkit/src/utils/parsing_utils.dart';
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
  final List<DivAction>? actions;

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

  /// Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value.
  /// On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
  final DivAspect? aspect;

  /// Element background. It can contain multiple layers.
  @override
  final List<DivBackground>? background;

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
  final List<DivDisappearAction>? disappearActions;

  /// Action when double-clicking on an element.
  final List<DivAction>? doubletapActions;

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

  /// Sets collection elements dynamically using `data` and `prototypes`.
  final DivCollectionItemBuilder? itemBuilder;

  /// Nested elements.
  final List<Div>? items;

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
  final List<DivAction>? longtapActions;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
  // default value: DivContainerOrientation.vertical
  final Expression<DivContainerOrientation> orientation;

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

  /// Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
  final DivContainerSeparator? separator;

  /// Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
  @override
  final List<DivTooltip>? tooltips;

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
    List<DivAction>? Function()? actions,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivAnimator>? Function()? animators,
    DivAspect? Function()? aspect,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<bool>? clipToBounds,
    Expression<int>? Function()? columnSpan,
    Expression<DivContentAlignmentHorizontal>? contentAlignmentHorizontal,
    Expression<DivContentAlignmentVertical>? contentAlignmentVertical,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivAction>? Function()? doubletapActions,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    List<DivFunction>? Function()? functions,
    DivSize? height,
    String? Function()? id,
    DivCollectionItemBuilder? Function()? itemBuilder,
    List<Div>? Function()? items,
    Expression<DivContainerLayoutMode>? layoutMode,
    DivLayoutProvider? Function()? layoutProvider,
    DivContainerSeparator? Function()? lineSeparator,
    List<DivAction>? Function()? longtapActions,
    DivEdgeInsets? margins,
    Expression<DivContainerOrientation>? orientation,
    DivEdgeInsets? paddings,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    List<DivAction>? Function()? selectedActions,
    DivContainerSeparator? Function()? separator,
    List<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
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
        accessibility: safeParseObj(
          DivAccessibility.fromJson(json['accessibility']),
          fallback: const DivAccessibility(),
        )!,
        action: safeParseObj(
          DivAction.fromJson(json['action']),
        ),
        actionAnimation: safeParseObj(
          DivAnimation.fromJson(json['action_animation']),
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
        )!,
        actions: safeParseObj(
          safeListMap(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
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
        aspect: safeParseObj(
          DivAspect.fromJson(json['aspect']),
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
        clipToBounds: safeParseBoolExpr(
          json['clip_to_bounds'],
          fallback: true,
        )!,
        columnSpan: safeParseIntExpr(
          json['column_span'],
        ),
        contentAlignmentHorizontal: safeParseStrEnumExpr(
          json['content_alignment_horizontal'],
          parse: DivContentAlignmentHorizontal.fromJson,
          fallback: DivContentAlignmentHorizontal.start,
        )!,
        contentAlignmentVertical: safeParseStrEnumExpr(
          json['content_alignment_vertical'],
          parse: DivContentAlignmentVertical.fromJson,
          fallback: DivContentAlignmentVertical.top,
        )!,
        disappearActions: safeParseObj(
          safeListMap(
            json['disappear_actions'],
            (v) => safeParseObj(
              DivDisappearAction.fromJson(v),
            )!,
          ),
        ),
        doubletapActions: safeParseObj(
          safeListMap(
            json['doubletap_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
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
        itemBuilder: safeParseObj(
          DivCollectionItemBuilder.fromJson(json['item_builder']),
        ),
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              Div.fromJson(v),
            )!,
          ),
        ),
        layoutMode: safeParseStrEnumExpr(
          json['layout_mode'],
          parse: DivContainerLayoutMode.fromJson,
          fallback: DivContainerLayoutMode.noWrap,
        )!,
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        lineSeparator: safeParseObj(
          DivContainerSeparator.fromJson(json['line_separator']),
        ),
        longtapActions: safeParseObj(
          safeListMap(
            json['longtap_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        margins: safeParseObj(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        )!,
        orientation: safeParseStrEnumExpr(
          json['orientation'],
          parse: DivContainerOrientation.fromJson,
          fallback: DivContainerOrientation.vertical,
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
        separator: safeParseObj(
          DivContainerSeparator.fromJson(json['separator']),
        ),
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
  DivContainer resolve(DivVariableContext context) {
    accessibility.resolve(context);
    action?.resolve(context);
    actionAnimation.resolve(context);
    safeListResolve(actions, (v) => v.resolve(context));
    alignmentHorizontal?.resolve(context);
    alignmentVertical?.resolve(context);
    alpha.resolve(context);
    safeListResolve(animators, (v) => v.resolve(context));
    aspect?.resolve(context);
    safeListResolve(background, (v) => v.resolve(context));
    border.resolve(context);
    clipToBounds.resolve(context);
    columnSpan?.resolve(context);
    contentAlignmentHorizontal.resolve(context);
    contentAlignmentVertical.resolve(context);
    safeListResolve(disappearActions, (v) => v.resolve(context));
    safeListResolve(doubletapActions, (v) => v.resolve(context));
    safeListResolve(extensions, (v) => v.resolve(context));
    focus?.resolve(context);
    safeListResolve(functions, (v) => v.resolve(context));
    height.resolve(context);
    itemBuilder?.resolve(context);
    layoutMode.resolve(context);
    layoutProvider?.resolve(context);
    lineSeparator?.resolve(context);
    safeListResolve(longtapActions, (v) => v.resolve(context));
    margins.resolve(context);
    orientation.resolve(context);
    paddings.resolve(context);
    reuseId?.resolve(context);
    rowSpan?.resolve(context);
    safeListResolve(selectedActions, (v) => v.resolve(context));
    separator?.resolve(context);
    safeListResolve(tooltips, (v) => v.resolve(context));
    transform.resolve(context);
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
        margins: safeParseObj(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        )!,
        showAtEnd: safeParseBoolExpr(
          json['show_at_end'],
          fallback: false,
        )!,
        showAtStart: safeParseBoolExpr(
          json['show_at_start'],
          fallback: false,
        )!,
        showBetween: safeParseBoolExpr(
          json['show_between'],
          fallback: true,
        )!,
        style: safeParseObj(
          DivDrawable.fromJson(json['style']),
        )!,
      );
    } catch (e) {
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
    } catch (e) {
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
    } catch (e) {
      return null;
    }
  }

  @override
  DivContainerLayoutMode resolve(DivVariableContext context) => this;
}

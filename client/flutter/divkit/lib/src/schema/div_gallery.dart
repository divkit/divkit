// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_accessibility.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_animator.dart';
import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_base.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/schema/div_collection_item_builder.dart';
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
import 'package:divkit/src/schema/div_transition_trigger.dart';
import 'package:divkit/src/schema/div_trigger.dart';
import 'package:divkit/src/schema/div_variable.dart';
import 'package:divkit/src/schema/div_visibility.dart';
import 'package:divkit/src/schema/div_visibility_action.dart';
import 'package:divkit/src/schema/div_wrap_content_size.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Gallery. It contains a horizontal or vertical set of cards that can be scrolled.
class DivGallery extends Resolvable with EquatableMixin implements DivBase {
  const DivGallery({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.animators,
    this.background,
    this.border = const DivBorder(),
    this.columnCount,
    this.columnSpan,
    this.crossContentAlignment =
        const ValueExpression(DivGalleryCrossContentAlignment.start),
    this.crossSpacing,
    this.defaultItem = const ValueExpression(0),
    this.disappearActions,
    this.extensions,
    this.focus,
    this.functions,
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.id,
    this.itemBuilder,
    this.itemSpacing = const ValueExpression(8),
    this.items,
    this.layoutProvider,
    this.margins = const DivEdgeInsets(),
    this.orientation = const ValueExpression(DivGalleryOrientation.horizontal),
    this.paddings = const DivEdgeInsets(),
    this.restrictParentScroll = const ValueExpression(false),
    this.reuseId,
    this.rowSpan,
    this.scrollMode = const ValueExpression(DivGalleryScrollMode.default_),
    this.scrollbar = const ValueExpression(DivGalleryScrollbar.none),
    this.selectedActions,
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

  static const type = "gallery";

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

  /// Number of columns for block layout.
  // constraint: number > 0
  final Expression<int>? columnCount;

  /// Merges cells in a column of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;

  /// Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:
  /// • `start` — alignment to the top of the card;
  /// • `center` — to the center;
  /// • `end` — to the bottom.</p><p>In vertical galleries:
  /// • `start` — alignment to the left of the card;
  /// • `center` — to the center;
  /// • `end` — to the right.
  // default value: DivGalleryCrossContentAlignment.start
  final Expression<DivGalleryCrossContentAlignment> crossContentAlignment;

  /// Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
  // constraint: number >= 0
  final Expression<int>? crossSpacing;

  /// Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:
  /// • `default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;
  /// • `paging` — the scroll position is set to the center of the element.
  // constraint: number >= 0; default value: 0
  final Expression<int> defaultItem;

  /// Actions when an element disappears from the screen.
  @override
  final Arr<DivDisappearAction>? disappearActions;

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

  /// Sets collection elements dynamically using `data` and `prototypes`.
  final DivCollectionItemBuilder? itemBuilder;

  /// Spacing between elements.
  // constraint: number >= 0; default value: 8
  final Expression<int> itemSpacing;

  /// Gallery elements. Scrolling to elements can be implemented using:
  /// • `div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;
  /// • `div-action://set_next_item?id=[&overflow={clamp\|ring}]` — scrolling to the next element inside an element, with the specified `id`;
  /// • `div-action://set_previous_item?id=[&overflow={clamp\|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:
  /// • `clamp` — transition will stop at the border element;
  /// • `ring` — go to the beginning or end, depending on the current element.</p><p>By default, `clamp`.
  final Arr<Div>? items;

  /// Provides data on the actual size of the element.
  @override
  final DivLayoutProvider? layoutProvider;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Gallery orientation.
  // default value: DivGalleryOrientation.horizontal
  final Expression<DivGalleryOrientation> orientation;

  /// Internal margins from the element stroke.
  @override
  final DivEdgeInsets paddings;

  /// If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
  // default value: false
  final Expression<bool> restrictParentScroll;

  /// ID for the div object structure. Used to optimize block reuse. See [block reuse](https://divkit.tech/docs/en/concepts/reuse/reuse.md).
  @override
  final Expression<String>? reuseId;

  /// Merges cells in a string of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  /// Scroll type: `default` — continuous, `paging` — page-by-page.
  // default value: DivGalleryScrollMode.default_
  final Expression<DivGalleryScrollMode> scrollMode;

  /// Scrollbar behavior. Hidden by default. When choosing a gallery size, keep in mind that the scrollbar may have a different height and width depending on the platform and user settings.
  /// • `none` — the scrollbar is hidden.
  /// • `auto` — the scrollbar is shown if there isn't enough space and it needs to be displayed on the current platform.
  // default value: DivGalleryScrollbar.none
  final Expression<DivGalleryScrollbar> scrollbar;

  /// List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
  @override
  final Arr<DivAction>? selectedActions;

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
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        animators,
        background,
        border,
        columnCount,
        columnSpan,
        crossContentAlignment,
        crossSpacing,
        defaultItem,
        disappearActions,
        extensions,
        focus,
        functions,
        height,
        id,
        itemBuilder,
        itemSpacing,
        items,
        layoutProvider,
        margins,
        orientation,
        paddings,
        restrictParentScroll,
        reuseId,
        rowSpan,
        scrollMode,
        scrollbar,
        selectedActions,
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

  DivGallery copyWith({
    DivAccessibility? accessibility,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    Arr<DivAnimator>? Function()? animators,
    Arr<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnCount,
    Expression<int>? Function()? columnSpan,
    Expression<DivGalleryCrossContentAlignment>? crossContentAlignment,
    Expression<int>? Function()? crossSpacing,
    Expression<int>? defaultItem,
    Arr<DivDisappearAction>? Function()? disappearActions,
    Arr<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    Arr<DivFunction>? Function()? functions,
    DivSize? height,
    String? Function()? id,
    DivCollectionItemBuilder? Function()? itemBuilder,
    Expression<int>? itemSpacing,
    Arr<Div>? Function()? items,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    Expression<DivGalleryOrientation>? orientation,
    DivEdgeInsets? paddings,
    Expression<bool>? restrictParentScroll,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Expression<DivGalleryScrollMode>? scrollMode,
    Expression<DivGalleryScrollbar>? scrollbar,
    Arr<DivAction>? Function()? selectedActions,
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
      DivGallery(
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
        columnCount:
            columnCount != null ? columnCount.call() : this.columnCount,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        crossContentAlignment:
            crossContentAlignment ?? this.crossContentAlignment,
        crossSpacing:
            crossSpacing != null ? crossSpacing.call() : this.crossSpacing,
        defaultItem: defaultItem ?? this.defaultItem,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        functions: functions != null ? functions.call() : this.functions,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        itemBuilder:
            itemBuilder != null ? itemBuilder.call() : this.itemBuilder,
        itemSpacing: itemSpacing ?? this.itemSpacing,
        items: items != null ? items.call() : this.items,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        margins: margins ?? this.margins,
        orientation: orientation ?? this.orientation,
        paddings: paddings ?? this.paddings,
        restrictParentScroll: restrictParentScroll ?? this.restrictParentScroll,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        scrollMode: scrollMode ?? this.scrollMode,
        scrollbar: scrollbar ?? this.scrollbar,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
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

  static DivGallery? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivGallery(
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
        columnCount: safeParseIntExpr(
          json['column_count'],
        ),
        columnSpan: safeParseIntExpr(
          json['column_span'],
        ),
        crossContentAlignment: reqVProp<DivGalleryCrossContentAlignment>(
          safeParseStrEnumExpr(
            json['cross_content_alignment'],
            parse: DivGalleryCrossContentAlignment.fromJson,
            fallback: DivGalleryCrossContentAlignment.start,
          ),
          name: 'cross_content_alignment',
        ),
        crossSpacing: safeParseIntExpr(
          json['cross_spacing'],
        ),
        defaultItem: reqVProp<int>(
          safeParseIntExpr(
            json['default_item'],
            fallback: 0,
          ),
          name: 'default_item',
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
        itemBuilder: safeParseObject(
          json['item_builder'],
          parse: DivCollectionItemBuilder.fromJson,
        ),
        itemSpacing: reqVProp<int>(
          safeParseIntExpr(
            json['item_spacing'],
            fallback: 8,
          ),
          name: 'item_spacing',
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
        orientation: reqVProp<DivGalleryOrientation>(
          safeParseStrEnumExpr(
            json['orientation'],
            parse: DivGalleryOrientation.fromJson,
            fallback: DivGalleryOrientation.horizontal,
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
        restrictParentScroll: reqVProp<bool>(
          safeParseBoolExpr(
            json['restrict_parent_scroll'],
            fallback: false,
          ),
          name: 'restrict_parent_scroll',
        ),
        reuseId: safeParseStrExpr(
          json['reuse_id'],
        ),
        rowSpan: safeParseIntExpr(
          json['row_span'],
        ),
        scrollMode: reqVProp<DivGalleryScrollMode>(
          safeParseStrEnumExpr(
            json['scroll_mode'],
            parse: DivGalleryScrollMode.fromJson,
            fallback: DivGalleryScrollMode.default_,
          ),
          name: 'scroll_mode',
        ),
        scrollbar: reqVProp<DivGalleryScrollbar>(
          safeParseStrEnumExpr(
            json['scrollbar'],
            parse: DivGalleryScrollbar.fromJson,
            fallback: DivGalleryScrollbar.none,
          ),
          name: 'scrollbar',
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
  DivGallery resolve(DivVariableContext context) {
    accessibility.resolve(context);
    alignmentHorizontal?.resolve(context);
    alignmentVertical?.resolve(context);
    alpha.resolve(context);
    tryResolveList(animators, (v) => v.resolve(context));
    tryResolveList(background, (v) => v.resolve(context));
    border.resolve(context);
    columnCount?.resolve(context);
    columnSpan?.resolve(context);
    crossContentAlignment.resolve(context);
    crossSpacing?.resolve(context);
    defaultItem.resolve(context);
    tryResolveList(disappearActions, (v) => v.resolve(context));
    tryResolveList(extensions, (v) => v.resolve(context));
    focus?.resolve(context);
    tryResolveList(functions, (v) => v.resolve(context));
    height.resolve(context);
    itemBuilder?.resolve(context);
    itemSpacing.resolve(context);
    layoutProvider?.resolve(context);
    margins.resolve(context);
    orientation.resolve(context);
    paddings.resolve(context);
    restrictParentScroll.resolve(context);
    reuseId?.resolve(context);
    rowSpan?.resolve(context);
    scrollMode.resolve(context);
    scrollbar.resolve(context);
    tryResolveList(selectedActions, (v) => v.resolve(context));
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

enum DivGalleryCrossContentAlignment implements Resolvable {
  start('start'),
  center('center'),
  end('end');

  final String value;

  const DivGalleryCrossContentAlignment(this.value);
  bool get isStart => this == start;

  bool get isCenter => this == center;

  bool get isEnd => this == end;

  T map<T>({
    required T Function() start,
    required T Function() center,
    required T Function() end,
  }) {
    switch (this) {
      case DivGalleryCrossContentAlignment.start:
        return start();
      case DivGalleryCrossContentAlignment.center:
        return center();
      case DivGalleryCrossContentAlignment.end:
        return end();
    }
  }

  T maybeMap<T>({
    T Function()? start,
    T Function()? center,
    T Function()? end,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivGalleryCrossContentAlignment.start:
        return start?.call() ?? orElse();
      case DivGalleryCrossContentAlignment.center:
        return center?.call() ?? orElse();
      case DivGalleryCrossContentAlignment.end:
        return end?.call() ?? orElse();
    }
  }

  static DivGalleryCrossContentAlignment? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'start':
          return DivGalleryCrossContentAlignment.start;
        case 'center':
          return DivGalleryCrossContentAlignment.center;
        case 'end':
          return DivGalleryCrossContentAlignment.end;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivGalleryCrossContentAlignment: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivGalleryCrossContentAlignment resolve(DivVariableContext context) => this;
}

enum DivGalleryScrollMode implements Resolvable {
  paging('paging'),
  default_('default');

  final String value;

  const DivGalleryScrollMode(this.value);
  bool get isPaging => this == paging;

  bool get isDefault => this == default_;

  T map<T>({
    required T Function() paging,
    required T Function() default_,
  }) {
    switch (this) {
      case DivGalleryScrollMode.paging:
        return paging();
      case DivGalleryScrollMode.default_:
        return default_();
    }
  }

  T maybeMap<T>({
    T Function()? paging,
    T Function()? default_,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivGalleryScrollMode.paging:
        return paging?.call() ?? orElse();
      case DivGalleryScrollMode.default_:
        return default_?.call() ?? orElse();
    }
  }

  static DivGalleryScrollMode? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'paging':
          return DivGalleryScrollMode.paging;
        case 'default':
          return DivGalleryScrollMode.default_;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivGalleryScrollMode: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivGalleryScrollMode resolve(DivVariableContext context) => this;
}

enum DivGalleryOrientation implements Resolvable {
  horizontal('horizontal'),
  vertical('vertical');

  final String value;

  const DivGalleryOrientation(this.value);
  bool get isHorizontal => this == horizontal;

  bool get isVertical => this == vertical;

  T map<T>({
    required T Function() horizontal,
    required T Function() vertical,
  }) {
    switch (this) {
      case DivGalleryOrientation.horizontal:
        return horizontal();
      case DivGalleryOrientation.vertical:
        return vertical();
    }
  }

  T maybeMap<T>({
    T Function()? horizontal,
    T Function()? vertical,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivGalleryOrientation.horizontal:
        return horizontal?.call() ?? orElse();
      case DivGalleryOrientation.vertical:
        return vertical?.call() ?? orElse();
    }
  }

  static DivGalleryOrientation? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'horizontal':
          return DivGalleryOrientation.horizontal;
        case 'vertical':
          return DivGalleryOrientation.vertical;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivGalleryOrientation: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivGalleryOrientation resolve(DivVariableContext context) => this;
}

enum DivGalleryScrollbar implements Resolvable {
  none('none'),
  auto('auto');

  final String value;

  const DivGalleryScrollbar(this.value);
  bool get isNone => this == none;

  bool get isAuto => this == auto;

  T map<T>({
    required T Function() none,
    required T Function() auto,
  }) {
    switch (this) {
      case DivGalleryScrollbar.none:
        return none();
      case DivGalleryScrollbar.auto:
        return auto();
    }
  }

  T maybeMap<T>({
    T Function()? none,
    T Function()? auto,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivGalleryScrollbar.none:
        return none?.call() ?? orElse();
      case DivGalleryScrollbar.auto:
        return auto?.call() ?? orElse();
    }
  }

  static DivGalleryScrollbar? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivGalleryScrollbar.none;
        case 'auto':
          return DivGalleryScrollbar.auto;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivGalleryScrollbar: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivGalleryScrollbar resolve(DivVariableContext context) => this;
}

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
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Gallery. It contains a horizontal or vertical set of cards that can be scrolled.
class DivGallery extends Preloadable with EquatableMixin implements DivBase {
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
  final List<DivAnimator>? animators;

  /// Element background. It can contain multiple layers.
  @override
  final List<DivBackground>? background;

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
  final List<DivDisappearAction>? disappearActions;

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

  /// Spacing between elements.
  // constraint: number >= 0; default value: 8
  final Expression<int> itemSpacing;

  /// Gallery elements. Scrolling to elements can be implemented using:
  /// • `div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;
  /// • `div-action://set_next_item?id=[&overflow={clamp\|ring}]` — scrolling to the next element inside an element, with the specified `id`;
  /// • `div-action://set_previous_item?id=[&overflow={clamp\|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:
  /// • `clamp` — transition will stop at the border element;
  /// • `ring` — go to the beginning or end, depending on the current element.</p><p>By default, `clamp`.
  final List<Div>? items;

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
  final List<DivAction>? selectedActions;

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
    List<DivAnimator>? Function()? animators,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnCount,
    Expression<int>? Function()? columnSpan,
    Expression<DivGalleryCrossContentAlignment>? crossContentAlignment,
    Expression<int>? Function()? crossSpacing,
    Expression<int>? defaultItem,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    List<DivFunction>? Function()? functions,
    DivSize? height,
    String? Function()? id,
    DivCollectionItemBuilder? Function()? itemBuilder,
    Expression<int>? itemSpacing,
    List<Div>? Function()? items,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    Expression<DivGalleryOrientation>? orientation,
    DivEdgeInsets? paddings,
    Expression<bool>? restrictParentScroll,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Expression<DivGalleryScrollMode>? scrollMode,
    Expression<DivGalleryScrollbar>? scrollbar,
    List<DivAction>? Function()? selectedActions,
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
        columnCount: safeParseIntExpr(
          json['column_count'],
        ),
        columnSpan: safeParseIntExpr(
          json['column_span'],
        ),
        crossContentAlignment: safeParseStrEnumExpr(
          json['cross_content_alignment'],
          parse: DivGalleryCrossContentAlignment.fromJson,
          fallback: DivGalleryCrossContentAlignment.start,
        )!,
        crossSpacing: safeParseIntExpr(
          json['cross_spacing'],
        ),
        defaultItem: safeParseIntExpr(
          json['default_item'],
          fallback: 0,
        )!,
        disappearActions: safeParseObj(
          safeListMap(
            json['disappear_actions'],
            (v) => safeParseObj(
              DivDisappearAction.fromJson(v),
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
        itemSpacing: safeParseIntExpr(
          json['item_spacing'],
          fallback: 8,
        )!,
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              Div.fromJson(v),
            )!,
          ),
        ),
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        margins: safeParseObj(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        )!,
        orientation: safeParseStrEnumExpr(
          json['orientation'],
          parse: DivGalleryOrientation.fromJson,
          fallback: DivGalleryOrientation.horizontal,
        )!,
        paddings: safeParseObj(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        )!,
        restrictParentScroll: safeParseBoolExpr(
          json['restrict_parent_scroll'],
          fallback: false,
        )!,
        reuseId: safeParseStrExpr(
          json['reuse_id']?.toString(),
        ),
        rowSpan: safeParseIntExpr(
          json['row_span'],
        ),
        scrollMode: safeParseStrEnumExpr(
          json['scroll_mode'],
          parse: DivGalleryScrollMode.fromJson,
          fallback: DivGalleryScrollMode.default_,
        )!,
        scrollbar: safeParseStrEnumExpr(
          json['scrollbar'],
          parse: DivGalleryScrollbar.fromJson,
          fallback: DivGalleryScrollbar.none,
        )!,
        selectedActions: safeParseObj(
          safeListMap(
            json['selected_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
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

  static Future<DivGallery?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivGallery(
        accessibility: (await safeParseObjAsync(
          DivAccessibility.fromJson(json['accessibility']),
          fallback: const DivAccessibility(),
        ))!,
        alignmentHorizontal: await safeParseStrEnumExprAsync(
          json['alignment_horizontal'],
          parse: DivAlignmentHorizontal.fromJson,
        ),
        alignmentVertical: await safeParseStrEnumExprAsync(
          json['alignment_vertical'],
          parse: DivAlignmentVertical.fromJson,
        ),
        alpha: (await safeParseDoubleExprAsync(
          json['alpha'],
          fallback: 1.0,
        ))!,
        animators: await safeParseObjAsync(
          await safeListMapAsync(
            json['animators'],
            (v) => safeParseObj(
              DivAnimator.fromJson(v),
            )!,
          ),
        ),
        background: await safeParseObjAsync(
          await safeListMapAsync(
            json['background'],
            (v) => safeParseObj(
              DivBackground.fromJson(v),
            )!,
          ),
        ),
        border: (await safeParseObjAsync(
          DivBorder.fromJson(json['border']),
          fallback: const DivBorder(),
        ))!,
        columnCount: await safeParseIntExprAsync(
          json['column_count'],
        ),
        columnSpan: await safeParseIntExprAsync(
          json['column_span'],
        ),
        crossContentAlignment: (await safeParseStrEnumExprAsync(
          json['cross_content_alignment'],
          parse: DivGalleryCrossContentAlignment.fromJson,
          fallback: DivGalleryCrossContentAlignment.start,
        ))!,
        crossSpacing: await safeParseIntExprAsync(
          json['cross_spacing'],
        ),
        defaultItem: (await safeParseIntExprAsync(
          json['default_item'],
          fallback: 0,
        ))!,
        disappearActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['disappear_actions'],
            (v) => safeParseObj(
              DivDisappearAction.fromJson(v),
            )!,
          ),
        ),
        extensions: await safeParseObjAsync(
          await safeListMapAsync(
            json['extensions'],
            (v) => safeParseObj(
              DivExtension.fromJson(v),
            )!,
          ),
        ),
        focus: await safeParseObjAsync(
          DivFocus.fromJson(json['focus']),
        ),
        functions: await safeParseObjAsync(
          await safeListMapAsync(
            json['functions'],
            (v) => safeParseObj(
              DivFunction.fromJson(v),
            )!,
          ),
        ),
        height: (await safeParseObjAsync(
          DivSize.fromJson(json['height']),
          fallback: const DivSize.divWrapContentSize(
            DivWrapContentSize(),
          ),
        ))!,
        id: await safeParseStrAsync(
          json['id']?.toString(),
        ),
        itemBuilder: await safeParseObjAsync(
          DivCollectionItemBuilder.fromJson(json['item_builder']),
        ),
        itemSpacing: (await safeParseIntExprAsync(
          json['item_spacing'],
          fallback: 8,
        ))!,
        items: await safeParseObjAsync(
          await safeListMapAsync(
            json['items'],
            (v) => safeParseObj(
              Div.fromJson(v),
            )!,
          ),
        ),
        layoutProvider: await safeParseObjAsync(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        margins: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        ))!,
        orientation: (await safeParseStrEnumExprAsync(
          json['orientation'],
          parse: DivGalleryOrientation.fromJson,
          fallback: DivGalleryOrientation.horizontal,
        ))!,
        paddings: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        ))!,
        restrictParentScroll: (await safeParseBoolExprAsync(
          json['restrict_parent_scroll'],
          fallback: false,
        ))!,
        reuseId: await safeParseStrExprAsync(
          json['reuse_id']?.toString(),
        ),
        rowSpan: await safeParseIntExprAsync(
          json['row_span'],
        ),
        scrollMode: (await safeParseStrEnumExprAsync(
          json['scroll_mode'],
          parse: DivGalleryScrollMode.fromJson,
          fallback: DivGalleryScrollMode.default_,
        ))!,
        scrollbar: (await safeParseStrEnumExprAsync(
          json['scrollbar'],
          parse: DivGalleryScrollbar.fromJson,
          fallback: DivGalleryScrollbar.none,
        ))!,
        selectedActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['selected_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        tooltips: await safeParseObjAsync(
          await safeListMapAsync(
            json['tooltips'],
            (v) => safeParseObj(
              DivTooltip.fromJson(v),
            )!,
          ),
        ),
        transform: (await safeParseObjAsync(
          DivTransform.fromJson(json['transform']),
          fallback: const DivTransform(),
        ))!,
        transitionChange: await safeParseObjAsync(
          DivChangeTransition.fromJson(json['transition_change']),
        ),
        transitionIn: await safeParseObjAsync(
          DivAppearanceTransition.fromJson(json['transition_in']),
        ),
        transitionOut: await safeParseObjAsync(
          DivAppearanceTransition.fromJson(json['transition_out']),
        ),
        transitionTriggers: await safeParseObjAsync(
          await safeListMapAsync(
            json['transition_triggers'],
            (v) => safeParseStrEnum(
              v,
              parse: DivTransitionTrigger.fromJson,
            )!,
          ),
        ),
        variableTriggers: await safeParseObjAsync(
          await safeListMapAsync(
            json['variable_triggers'],
            (v) => safeParseObj(
              DivTrigger.fromJson(v),
            )!,
          ),
        ),
        variables: await safeParseObjAsync(
          await safeListMapAsync(
            json['variables'],
            (v) => safeParseObj(
              DivVariable.fromJson(v),
            )!,
          ),
        ),
        visibility: (await safeParseStrEnumExprAsync(
          json['visibility'],
          parse: DivVisibility.fromJson,
          fallback: DivVisibility.visible,
        ))!,
        visibilityAction: await safeParseObjAsync(
          DivVisibilityAction.fromJson(json['visibility_action']),
        ),
        visibilityActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['visibility_actions'],
            (v) => safeParseObj(
              DivVisibilityAction.fromJson(v),
            )!,
          ),
        ),
        width: (await safeParseObjAsync(
          DivSize.fromJson(json['width']),
          fallback: const DivSize.divMatchParentSize(
            DivMatchParentSize(),
          ),
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await accessibility.preload(context);
      await alignmentHorizontal?.preload(context);
      await alignmentVertical?.preload(context);
      await alpha.preload(context);
      await safeFuturesWait(animators, (v) => v.preload(context));
      await safeFuturesWait(background, (v) => v.preload(context));
      await border.preload(context);
      await columnCount?.preload(context);
      await columnSpan?.preload(context);
      await crossContentAlignment.preload(context);
      await crossSpacing?.preload(context);
      await defaultItem.preload(context);
      await safeFuturesWait(disappearActions, (v) => v.preload(context));
      await safeFuturesWait(extensions, (v) => v.preload(context));
      await focus?.preload(context);
      await safeFuturesWait(functions, (v) => v.preload(context));
      await height.preload(context);
      await itemBuilder?.preload(context);
      await itemSpacing.preload(context);
      await safeFuturesWait(items, (v) => v.preload(context));
      await layoutProvider?.preload(context);
      await margins.preload(context);
      await orientation.preload(context);
      await paddings.preload(context);
      await restrictParentScroll.preload(context);
      await reuseId?.preload(context);
      await rowSpan?.preload(context);
      await scrollMode.preload(context);
      await scrollbar.preload(context);
      await safeFuturesWait(selectedActions, (v) => v.preload(context));
      await safeFuturesWait(tooltips, (v) => v.preload(context));
      await transform.preload(context);
      await transitionChange?.preload(context);
      await transitionIn?.preload(context);
      await transitionOut?.preload(context);
      await safeFuturesWait(transitionTriggers, (v) => v.preload(context));
      await safeFuturesWait(variableTriggers, (v) => v.preload(context));
      await safeFuturesWait(variables, (v) => v.preload(context));
      await visibility.preload(context);
      await visibilityAction?.preload(context);
      await safeFuturesWait(visibilityActions, (v) => v.preload(context));
      await width.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivGalleryCrossContentAlignment implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivGalleryCrossContentAlignment?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}

enum DivGalleryScrollMode implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivGalleryScrollMode?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}

enum DivGalleryOrientation implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivGalleryOrientation?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}

enum DivGalleryScrollbar implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivGalleryScrollbar?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}

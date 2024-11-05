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
import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_function.dart';
import 'package:divkit/src/schema/div_layout_provider.dart';
import 'package:divkit/src/schema/div_match_parent_size.dart';
import 'package:divkit/src/schema/div_page_transformation.dart';
import 'package:divkit/src/schema/div_pager_layout_mode.dart';
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

/// Pager. It contains a horizontal set of cards that can be scrolled page by page. It shows the main page and the beginning of the next one.
class DivPager extends Resolvable with EquatableMixin implements DivBase {
  const DivPager({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.animators,
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.defaultItem = const ValueExpression(0),
    this.disappearActions,
    this.extensions,
    this.focus,
    this.functions,
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.id,
    this.infiniteScroll = const ValueExpression(false),
    this.itemBuilder,
    this.itemSpacing = const DivFixedSize(
      value: ValueExpression(
        0,
      ),
    ),
    this.items,
    required this.layoutMode,
    this.layoutProvider,
    this.margins = const DivEdgeInsets(),
    this.orientation = const ValueExpression(DivPagerOrientation.horizontal),
    this.paddings = const DivEdgeInsets(),
    this.pageTransformation,
    this.restrictParentScroll = const ValueExpression(false),
    this.reuseId,
    this.rowSpan,
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

  static const type = "pager";

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

  /// Ordinal number of the pager element that will be opened by default.
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

  /// Enables infinite scrolling of cards. Scrolling is looped: after the last card is displayed, it starts over again.
  // default value: false
  final Expression<bool> infiniteScroll;

  /// Sets collection elements dynamically using `data` and `prototypes`.
  final DivCollectionItemBuilder? itemBuilder;

  /// Spacing between elements.
  // default value: const DivFixedSize(value: ValueExpression(0,),)
  final DivFixedSize itemSpacing;

  /// Pager elements. Page-by-page transition options can be implemented using:
  /// • `div-action://set_current_item?id=&item=` — set the current page with an ordinal number `item` inside an element, with the specified `id`;
  /// • `div-action://set_next_item?id=[&overflow={clamp\|ring}]` — go to the next page inside an element, with the specified `id`;
  /// • `div-action://set_previous_item?id=[&overflow={clamp\|ring}]` — go to the previous page inside an element, with the specified `id`.</p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:
  /// • `clamp` — transition will stop at the border element;
  /// • `ring` — go to the beginning or end, depending on the current element.</p><p>By default, `clamp`.
  final List<Div>? items;

  /// Type of calculation of the main page width:
  /// • `fixed` — from the fixed width of the next page `neighbour_page_width`;
  /// • `percentage` — from the percentage value `page_width`.
  final DivPagerLayoutMode layoutMode;

  /// Provides data on the actual size of the element.
  @override
  final DivLayoutProvider? layoutProvider;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Pager orientation.
  // default value: DivPagerOrientation.horizontal
  final Expression<DivPagerOrientation> orientation;

  /// Internal margins from the element stroke.
  @override
  final DivEdgeInsets paddings;

  /// Page transformation during pager scrolling.
  final DivPageTransformation? pageTransformation;

  /// If the parameter is enabled, the pager won't transmit the scroll gesture to the parent element.
  // default value: false
  final Expression<bool> restrictParentScroll;

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
        columnSpan,
        defaultItem,
        disappearActions,
        extensions,
        focus,
        functions,
        height,
        id,
        infiniteScroll,
        itemBuilder,
        itemSpacing,
        items,
        layoutMode,
        layoutProvider,
        margins,
        orientation,
        paddings,
        pageTransformation,
        restrictParentScroll,
        reuseId,
        rowSpan,
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

  DivPager copyWith({
    DivAccessibility? accessibility,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivAnimator>? Function()? animators,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    Expression<int>? defaultItem,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    List<DivFunction>? Function()? functions,
    DivSize? height,
    String? Function()? id,
    Expression<bool>? infiniteScroll,
    DivCollectionItemBuilder? Function()? itemBuilder,
    DivFixedSize? itemSpacing,
    List<Div>? Function()? items,
    DivPagerLayoutMode? layoutMode,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    Expression<DivPagerOrientation>? orientation,
    DivEdgeInsets? paddings,
    DivPageTransformation? Function()? pageTransformation,
    Expression<bool>? restrictParentScroll,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
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
      DivPager(
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
        defaultItem: defaultItem ?? this.defaultItem,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        functions: functions != null ? functions.call() : this.functions,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        infiniteScroll: infiniteScroll ?? this.infiniteScroll,
        itemBuilder:
            itemBuilder != null ? itemBuilder.call() : this.itemBuilder,
        itemSpacing: itemSpacing ?? this.itemSpacing,
        items: items != null ? items.call() : this.items,
        layoutMode: layoutMode ?? this.layoutMode,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        margins: margins ?? this.margins,
        orientation: orientation ?? this.orientation,
        paddings: paddings ?? this.paddings,
        pageTransformation: pageTransformation != null
            ? pageTransformation.call()
            : this.pageTransformation,
        restrictParentScroll: restrictParentScroll ?? this.restrictParentScroll,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
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

  static DivPager? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPager(
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
        infiniteScroll: safeParseBoolExpr(
          json['infinite_scroll'],
          fallback: false,
        )!,
        itemBuilder: safeParseObj(
          DivCollectionItemBuilder.fromJson(json['item_builder']),
        ),
        itemSpacing: safeParseObj(
          DivFixedSize.fromJson(json['item_spacing']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              0,
            ),
          ),
        )!,
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              Div.fromJson(v),
            )!,
          ),
        ),
        layoutMode: safeParseObj(
          DivPagerLayoutMode.fromJson(json['layout_mode']),
        )!,
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        margins: safeParseObj(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        )!,
        orientation: safeParseStrEnumExpr(
          json['orientation'],
          parse: DivPagerOrientation.fromJson,
          fallback: DivPagerOrientation.horizontal,
        )!,
        paddings: safeParseObj(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        )!,
        pageTransformation: safeParseObj(
          DivPageTransformation.fromJson(json['page_transformation']),
        ),
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

  @override
  DivPager resolve(DivVariableContext context) {
    accessibility.resolve(context);
    alignmentHorizontal?.resolve(context);
    alignmentVertical?.resolve(context);
    alpha.resolve(context);
    safeListResolve(animators, (v) => v.resolve(context));
    safeListResolve(background, (v) => v.resolve(context));
    border.resolve(context);
    columnSpan?.resolve(context);
    defaultItem.resolve(context);
    safeListResolve(disappearActions, (v) => v.resolve(context));
    safeListResolve(extensions, (v) => v.resolve(context));
    focus?.resolve(context);
    safeListResolve(functions, (v) => v.resolve(context));
    height.resolve(context);
    infiniteScroll.resolve(context);
    itemBuilder?.resolve(context);
    itemSpacing.resolve(context);
    layoutMode.resolve(context);
    layoutProvider?.resolve(context);
    margins.resolve(context);
    orientation.resolve(context);
    paddings.resolve(context);
    pageTransformation?.resolve(context);
    restrictParentScroll.resolve(context);
    reuseId?.resolve(context);
    rowSpan?.resolve(context);
    safeListResolve(selectedActions, (v) => v.resolve(context));
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

enum DivPagerOrientation implements Resolvable {
  horizontal('horizontal'),
  vertical('vertical');

  final String value;

  const DivPagerOrientation(this.value);
  bool get isHorizontal => this == horizontal;

  bool get isVertical => this == vertical;

  T map<T>({
    required T Function() horizontal,
    required T Function() vertical,
  }) {
    switch (this) {
      case DivPagerOrientation.horizontal:
        return horizontal();
      case DivPagerOrientation.vertical:
        return vertical();
    }
  }

  T maybeMap<T>({
    T Function()? horizontal,
    T Function()? vertical,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivPagerOrientation.horizontal:
        return horizontal?.call() ?? orElse();
      case DivPagerOrientation.vertical:
        return vertical?.call() ?? orElse();
    }
  }

  static DivPagerOrientation? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'horizontal':
          return DivPagerOrientation.horizontal;
        case 'vertical':
          return DivPagerOrientation.vertical;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  @override
  DivPagerOrientation resolve(DivVariableContext context) => this;
}

// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div.dart';
import 'package:divkit/src/generated_sources/div_accessibility.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_alignment_horizontal.dart';
import 'package:divkit/src/generated_sources/div_alignment_vertical.dart';
import 'package:divkit/src/generated_sources/div_appearance_transition.dart';
import 'package:divkit/src/generated_sources/div_background.dart';
import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/generated_sources/div_border.dart';
import 'package:divkit/src/generated_sources/div_change_transition.dart';
import 'package:divkit/src/generated_sources/div_collection_item_builder.dart';
import 'package:divkit/src/generated_sources/div_disappear_action.dart';
import 'package:divkit/src/generated_sources/div_edge_insets.dart';
import 'package:divkit/src/generated_sources/div_extension.dart';
import 'package:divkit/src/generated_sources/div_fixed_size.dart';
import 'package:divkit/src/generated_sources/div_focus.dart';
import 'package:divkit/src/generated_sources/div_match_parent_size.dart';
import 'package:divkit/src/generated_sources/div_page_transformation.dart';
import 'package:divkit/src/generated_sources/div_pager_layout_mode.dart';
import 'package:divkit/src/generated_sources/div_size.dart';
import 'package:divkit/src/generated_sources/div_tooltip.dart';
import 'package:divkit/src/generated_sources/div_transform.dart';
import 'package:divkit/src/generated_sources/div_transition_trigger.dart';
import 'package:divkit/src/generated_sources/div_variable.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/generated_sources/div_visibility_action.dart';
import 'package:divkit/src/generated_sources/div_wrap_content_size.dart';

class DivPager with EquatableMixin implements DivBase {
  const DivPager({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.defaultItem = const ValueExpression(0),
    this.disappearActions,
    this.extensions,
    this.focus,
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
    this.id,
    this.infiniteScroll = const ValueExpression(false),
    this.itemBuilder,
    this.itemSpacing = const DivFixedSize(
      value: ValueExpression(0),
    ),
    this.items,
    required this.layoutMode,
    this.margins = const DivEdgeInsets(),
    this.orientation = const ValueExpression(DivPagerOrientation.horizontal),
    this.paddings = const DivEdgeInsets(),
    this.pageTransformation,
    this.restrictParentScroll = const ValueExpression(false),
    this.rowSpan,
    this.selectedActions,
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(DivMatchParentSize()),
  });

  static const type = "pager";

  @override
  final DivAccessibility accessibility;

  @override
  final Expression<DivAlignmentHorizontal>? alignmentHorizontal;

  @override
  final Expression<DivAlignmentVertical>? alignmentVertical;
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  @override
  final Expression<double> alpha;

  @override
  final List<DivBackground>? background;

  @override
  final DivBorder border;
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;
  // constraint: number >= 0; default value: 0
  final Expression<int> defaultItem;

  @override
  final List<DivDisappearAction>? disappearActions;

  @override
  final List<DivExtension>? extensions;

  @override
  final DivFocus? focus;
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize())
  @override
  final DivSize height;

  @override
  final String? id;
  // default value: false
  final Expression<bool> infiniteScroll;

  final DivCollectionItemBuilder? itemBuilder;
  // default value: const DivFixedSize(value: ValueExpression(0),)
  final DivFixedSize itemSpacing;

  final List<Div>? items;

  final DivPagerLayoutMode layoutMode;

  @override
  final DivEdgeInsets margins;
  // default value: DivPagerOrientation.horizontal
  final Expression<DivPagerOrientation> orientation;

  @override
  final DivEdgeInsets paddings;

  final DivPageTransformation? pageTransformation;
  // default value: false
  final Expression<bool> restrictParentScroll;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  @override
  final List<DivAction>? selectedActions;

  @override
  final List<DivTooltip>? tooltips;

  @override
  final DivTransform transform;

  @override
  final DivChangeTransition? transitionChange;

  @override
  final DivAppearanceTransition? transitionIn;

  @override
  final DivAppearanceTransition? transitionOut;
  // at least 1 elements
  @override
  final List<DivTransitionTrigger>? transitionTriggers;

  @override
  final List<DivVariable>? variables;
  // default value: DivVisibility.visible
  @override
  final Expression<DivVisibility> visibility;

  @override
  final DivVisibilityAction? visibilityAction;

  @override
  final List<DivVisibilityAction>? visibilityActions;
  // default value: const DivSize.divMatchParentSize(DivMatchParentSize())
  @override
  final DivSize width;

  @override
  List<Object?> get props => [
        accessibility,
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        background,
        border,
        columnSpan,
        defaultItem,
        disappearActions,
        extensions,
        focus,
        height,
        id,
        infiniteScroll,
        itemBuilder,
        itemSpacing,
        items,
        layoutMode,
        margins,
        orientation,
        paddings,
        pageTransformation,
        restrictParentScroll,
        rowSpan,
        selectedActions,
        tooltips,
        transform,
        transitionChange,
        transitionIn,
        transitionOut,
        transitionTriggers,
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
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    Expression<int>? defaultItem,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    DivSize? height,
    String? Function()? id,
    Expression<bool>? infiniteScroll,
    DivCollectionItemBuilder? Function()? itemBuilder,
    DivFixedSize? itemSpacing,
    List<Div>? Function()? items,
    DivPagerLayoutMode? layoutMode,
    DivEdgeInsets? margins,
    Expression<DivPagerOrientation>? orientation,
    DivEdgeInsets? paddings,
    DivPageTransformation? Function()? pageTransformation,
    Expression<bool>? restrictParentScroll,
    Expression<int>? Function()? rowSpan,
    List<DivAction>? Function()? selectedActions,
    List<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    List<DivTransitionTrigger>? Function()? transitionTriggers,
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
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        defaultItem: defaultItem ?? this.defaultItem,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        infiniteScroll: infiniteScroll ?? this.infiniteScroll,
        itemBuilder:
            itemBuilder != null ? itemBuilder.call() : this.itemBuilder,
        itemSpacing: itemSpacing ?? this.itemSpacing,
        items: items != null ? items.call() : this.items,
        layoutMode: layoutMode ?? this.layoutMode,
        margins: margins ?? this.margins,
        orientation: orientation ?? this.orientation,
        paddings: paddings ?? this.paddings,
        pageTransformation: pageTransformation != null
            ? pageTransformation.call()
            : this.pageTransformation,
        restrictParentScroll: restrictParentScroll ?? this.restrictParentScroll,
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

  static DivPager? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
      background: safeParseObj(
        safeListMap(
            json['background'],
            (v) => safeParseObj(
                  DivBackground.fromJson(v),
                )!),
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
                )!),
      ),
      extensions: safeParseObj(
        safeListMap(
            json['extensions'],
            (v) => safeParseObj(
                  DivExtension.fromJson(v),
                )!),
      ),
      focus: safeParseObj(
        DivFocus.fromJson(json['focus']),
      ),
      height: safeParseObj(
        DivSize.fromJson(json['height']),
        fallback: const DivSize.divWrapContentSize(DivWrapContentSize()),
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
          value: ValueExpression(0),
        ),
      )!,
      items: safeParseObj(
        safeListMap(
            json['items'],
            (v) => safeParseObj(
                  Div.fromJson(v),
                )!),
      ),
      layoutMode: safeParseObj(
        DivPagerLayoutMode.fromJson(json['layout_mode']),
      )!,
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
      rowSpan: safeParseIntExpr(
        json['row_span'],
      ),
      selectedActions: safeParseObj(
        safeListMap(
            json['selected_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      tooltips: safeParseObj(
        safeListMap(
            json['tooltips'],
            (v) => safeParseObj(
                  DivTooltip.fromJson(v),
                )!),
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
                )!),
      ),
      variables: safeParseObj(
        safeListMap(
            json['variables'],
            (v) => safeParseObj(
                  DivVariable.fromJson(v),
                )!),
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
                )!),
      ),
      width: safeParseObj(
        DivSize.fromJson(json['width']),
        fallback: const DivSize.divMatchParentSize(DivMatchParentSize()),
      )!,
    );
  }
}

enum DivPagerOrientation {
  horizontal('horizontal'),
  vertical('vertical');

  final String value;

  const DivPagerOrientation(this.value);

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

  static DivPagerOrientation? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'horizontal':
        return DivPagerOrientation.horizontal;
      case 'vertical':
        return DivPagerOrientation.vertical;
    }
    return null;
  }
}

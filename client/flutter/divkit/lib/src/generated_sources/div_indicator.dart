// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_accessibility.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_alignment_horizontal.dart';
import 'package:divkit/src/generated_sources/div_alignment_vertical.dart';
import 'package:divkit/src/generated_sources/div_appearance_transition.dart';
import 'package:divkit/src/generated_sources/div_background.dart';
import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/generated_sources/div_border.dart';
import 'package:divkit/src/generated_sources/div_change_transition.dart';
import 'package:divkit/src/generated_sources/div_disappear_action.dart';
import 'package:divkit/src/generated_sources/div_edge_insets.dart';
import 'package:divkit/src/generated_sources/div_extension.dart';
import 'package:divkit/src/generated_sources/div_fixed_size.dart';
import 'package:divkit/src/generated_sources/div_focus.dart';
import 'package:divkit/src/generated_sources/div_indicator_item_placement.dart';
import 'package:divkit/src/generated_sources/div_layout_provider.dart';
import 'package:divkit/src/generated_sources/div_match_parent_size.dart';
import 'package:divkit/src/generated_sources/div_rounded_rectangle_shape.dart';
import 'package:divkit/src/generated_sources/div_shape.dart';
import 'package:divkit/src/generated_sources/div_size.dart';
import 'package:divkit/src/generated_sources/div_tooltip.dart';
import 'package:divkit/src/generated_sources/div_transform.dart';
import 'package:divkit/src/generated_sources/div_transition_trigger.dart';
import 'package:divkit/src/generated_sources/div_variable.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/generated_sources/div_visibility_action.dart';
import 'package:divkit/src/generated_sources/div_wrap_content_size.dart';

class DivIndicator with EquatableMixin implements DivBase {
  const DivIndicator({
    this.accessibility = const DivAccessibility(),
    this.activeItemColor = const ValueExpression(Color(0xFFFFDC60)),
    this.activeItemSize = const ValueExpression(1.3),
    this.activeShape,
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.animation = const ValueExpression(DivIndicatorAnimation.scale),
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.disappearActions,
    this.extensions,
    this.focus,
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
    this.id,
    this.inactiveItemColor = const ValueExpression(Color(0x33919CB5)),
    this.inactiveMinimumShape,
    this.inactiveShape,
    this.itemsPlacement,
    this.layoutProvider,
    this.margins = const DivEdgeInsets(),
    this.minimumItemSize = const ValueExpression(0.5),
    this.paddings = const DivEdgeInsets(),
    this.pagerId,
    this.reuseId,
    this.rowSpan,
    this.selectedActions,
    this.shape =
        const DivShape.divRoundedRectangleShape(DivRoundedRectangleShape()),
    this.spaceBetweenCenters = const DivFixedSize(
      value: ValueExpression(15),
    ),
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

  static const type = "indicator";

  @override
  final DivAccessibility accessibility;
  // default value: const Color(0xFFFFDC60)
  final Expression<Color> activeItemColor;
  // constraint: number > 0; default value: 1.3
  final Expression<double> activeItemSize;

  final DivRoundedRectangleShape? activeShape;

  @override
  final Expression<DivAlignmentHorizontal>? alignmentHorizontal;

  @override
  final Expression<DivAlignmentVertical>? alignmentVertical;
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  @override
  final Expression<double> alpha;
  // default value: DivIndicatorAnimation.scale
  final Expression<DivIndicatorAnimation> animation;

  @override
  final List<DivBackground>? background;

  @override
  final DivBorder border;
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;

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
  // default value: const Color(0x33919CB5)
  final Expression<Color> inactiveItemColor;

  final DivRoundedRectangleShape? inactiveMinimumShape;

  final DivRoundedRectangleShape? inactiveShape;

  final DivIndicatorItemPlacement? itemsPlacement;

  @override
  final DivLayoutProvider? layoutProvider;

  @override
  final DivEdgeInsets margins;
  // constraint: number > 0; default value: 0.5
  final Expression<double> minimumItemSize;

  @override
  final DivEdgeInsets paddings;

  final String? pagerId;

  @override
  final Expression<String>? reuseId;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  @override
  final List<DivAction>? selectedActions;
  // default value: const DivShape.divRoundedRectangleShape(DivRoundedRectangleShape())
  final DivShape shape;
  // default value: const DivFixedSize(value: ValueExpression(15),)
  final DivFixedSize spaceBetweenCenters;

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
        activeItemColor,
        activeItemSize,
        activeShape,
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        animation,
        background,
        border,
        columnSpan,
        disappearActions,
        extensions,
        focus,
        height,
        id,
        inactiveItemColor,
        inactiveMinimumShape,
        inactiveShape,
        itemsPlacement,
        layoutProvider,
        margins,
        minimumItemSize,
        paddings,
        pagerId,
        reuseId,
        rowSpan,
        selectedActions,
        shape,
        spaceBetweenCenters,
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

  DivIndicator copyWith({
    DivAccessibility? accessibility,
    Expression<Color>? activeItemColor,
    Expression<double>? activeItemSize,
    DivRoundedRectangleShape? Function()? activeShape,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    Expression<DivIndicatorAnimation>? animation,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    DivSize? height,
    String? Function()? id,
    Expression<Color>? inactiveItemColor,
    DivRoundedRectangleShape? Function()? inactiveMinimumShape,
    DivRoundedRectangleShape? Function()? inactiveShape,
    DivIndicatorItemPlacement? Function()? itemsPlacement,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    Expression<double>? minimumItemSize,
    DivEdgeInsets? paddings,
    String? Function()? pagerId,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    List<DivAction>? Function()? selectedActions,
    DivShape? shape,
    DivFixedSize? spaceBetweenCenters,
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
      DivIndicator(
        accessibility: accessibility ?? this.accessibility,
        activeItemColor: activeItemColor ?? this.activeItemColor,
        activeItemSize: activeItemSize ?? this.activeItemSize,
        activeShape:
            activeShape != null ? activeShape.call() : this.activeShape,
        alignmentHorizontal: alignmentHorizontal != null
            ? alignmentHorizontal.call()
            : this.alignmentHorizontal,
        alignmentVertical: alignmentVertical != null
            ? alignmentVertical.call()
            : this.alignmentVertical,
        alpha: alpha ?? this.alpha,
        animation: animation ?? this.animation,
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        inactiveItemColor: inactiveItemColor ?? this.inactiveItemColor,
        inactiveMinimumShape: inactiveMinimumShape != null
            ? inactiveMinimumShape.call()
            : this.inactiveMinimumShape,
        inactiveShape:
            inactiveShape != null ? inactiveShape.call() : this.inactiveShape,
        itemsPlacement: itemsPlacement != null
            ? itemsPlacement.call()
            : this.itemsPlacement,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        margins: margins ?? this.margins,
        minimumItemSize: minimumItemSize ?? this.minimumItemSize,
        paddings: paddings ?? this.paddings,
        pagerId: pagerId != null ? pagerId.call() : this.pagerId,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        shape: shape ?? this.shape,
        spaceBetweenCenters: spaceBetweenCenters ?? this.spaceBetweenCenters,
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

  static DivIndicator? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivIndicator(
        accessibility: safeParseObj(
          DivAccessibility.fromJson(json['accessibility']),
          fallback: const DivAccessibility(),
        )!,
        activeItemColor: safeParseColorExpr(
          json['active_item_color'],
          fallback: const Color(0xFFFFDC60),
        )!,
        activeItemSize: safeParseDoubleExpr(
          json['active_item_size'],
          fallback: 1.3,
        )!,
        activeShape: safeParseObj(
          DivRoundedRectangleShape.fromJson(json['active_shape']),
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
        animation: safeParseStrEnumExpr(
          json['animation'],
          parse: DivIndicatorAnimation.fromJson,
          fallback: DivIndicatorAnimation.scale,
        )!,
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
        height: safeParseObj(
          DivSize.fromJson(json['height']),
          fallback: const DivSize.divWrapContentSize(DivWrapContentSize()),
        )!,
        id: safeParseStr(
          json['id']?.toString(),
        ),
        inactiveItemColor: safeParseColorExpr(
          json['inactive_item_color'],
          fallback: const Color(0x33919CB5),
        )!,
        inactiveMinimumShape: safeParseObj(
          DivRoundedRectangleShape.fromJson(json['inactive_minimum_shape']),
        ),
        inactiveShape: safeParseObj(
          DivRoundedRectangleShape.fromJson(json['inactive_shape']),
        ),
        itemsPlacement: safeParseObj(
          DivIndicatorItemPlacement.fromJson(json['items_placement']),
        ),
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        margins: safeParseObj(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        )!,
        minimumItemSize: safeParseDoubleExpr(
          json['minimum_item_size'],
          fallback: 0.5,
        )!,
        paddings: safeParseObj(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        )!,
        pagerId: safeParseStr(
          json['pager_id']?.toString(),
        ),
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
        shape: safeParseObj(
          DivShape.fromJson(json['shape']),
          fallback: const DivShape.divRoundedRectangleShape(
            DivRoundedRectangleShape(),
          ),
        )!,
        spaceBetweenCenters: safeParseObj(
          DivFixedSize.fromJson(json['space_between_centers']),
          fallback: const DivFixedSize(
            value: ValueExpression(15),
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
          fallback: const DivSize.divMatchParentSize(DivMatchParentSize()),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}

enum DivIndicatorAnimation {
  scale('scale'),
  worm('worm'),
  slider('slider');

  final String value;

  const DivIndicatorAnimation(this.value);

  T map<T>({
    required T Function() scale,
    required T Function() worm,
    required T Function() slider,
  }) {
    switch (this) {
      case DivIndicatorAnimation.scale:
        return scale();
      case DivIndicatorAnimation.worm:
        return worm();
      case DivIndicatorAnimation.slider:
        return slider();
    }
  }

  T maybeMap<T>({
    T Function()? scale,
    T Function()? worm,
    T Function()? slider,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivIndicatorAnimation.scale:
        return scale?.call() ?? orElse();
      case DivIndicatorAnimation.worm:
        return worm?.call() ?? orElse();
      case DivIndicatorAnimation.slider:
        return slider?.call() ?? orElse();
    }
  }

  static DivIndicatorAnimation? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'scale':
          return DivIndicatorAnimation.scale;
        case 'worm':
          return DivIndicatorAnimation.worm;
        case 'slider':
          return DivIndicatorAnimation.slider;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

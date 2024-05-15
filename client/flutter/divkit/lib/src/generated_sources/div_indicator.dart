// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_accessibility.dart';
import 'div_action.dart';
import 'div_alignment_horizontal.dart';
import 'div_alignment_vertical.dart';
import 'div_appearance_transition.dart';
import 'div_background.dart';
import 'div_base.dart';
import 'div_border.dart';
import 'div_change_transition.dart';
import 'div_disappear_action.dart';
import 'div_edge_insets.dart';
import 'div_extension.dart';
import 'div_fixed_size.dart';
import 'div_focus.dart';
import 'div_indicator_item_placement.dart';
import 'div_match_parent_size.dart';
import 'div_rounded_rectangle_shape.dart';
import 'div_rounded_rectangle_shape.dart';
import 'div_shape.dart';
import 'div_size.dart';
import 'div_tooltip.dart';
import 'div_transform.dart';
import 'div_transition_trigger.dart';
import 'div_visibility.dart';
import 'div_visibility_action.dart';
import 'div_wrap_content_size.dart';

class DivIndicator with EquatableMixin implements DivBase {
  const DivIndicator({
    this.accessibility = const DivAccessibility(),
    this.activeItemColor = const ValueExpression(const Color(0xFFFFDC60)),
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
    this.height = const DivSize(DivWrapContentSize()),
    this.id,
    this.inactiveItemColor = const ValueExpression(const Color(0x33919CB5)),
    this.inactiveMinimumShape,
    this.inactiveShape,
    this.itemsPlacement,
    this.margins = const DivEdgeInsets(),
    this.minimumItemSize = const ValueExpression(0.5),
    this.paddings = const DivEdgeInsets(),
    this.pagerId,
    this.rowSpan,
    this.selectedActions,
    this.shape = const DivShape(DivRoundedRectangleShape()),
    this.spaceBetweenCenters = const DivFixedSize(
      value: ValueExpression(15),
    ),
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize(DivMatchParentSize()),
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
  // default value: const DivSize(DivWrapContentSize())
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
  final DivEdgeInsets margins;
  // constraint: number > 0; default value: 0.5
  final Expression<double> minimumItemSize;

  @override
  final DivEdgeInsets paddings;

  final String? pagerId;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  @override
  final List<DivAction>? selectedActions;
  // default value: const DivShape(DivRoundedRectangleShape())
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
  // default value: DivVisibility.visible
  @override
  final Expression<DivVisibility> visibility;

  @override
  final DivVisibilityAction? visibilityAction;

  @override
  final List<DivVisibilityAction>? visibilityActions;
  // default value: const DivSize(DivMatchParentSize())
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
        margins,
        minimumItemSize,
        paddings,
        pagerId,
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
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  static DivIndicator? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
        (json['background'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivBackground.fromJson(v),
              )!,
            )
            .toList(),
      ),
      border: safeParseObj(
        DivBorder.fromJson(json['border']),
        fallback: const DivBorder(),
      )!,
      columnSpan: safeParseIntExpr(
        json['column_span'],
      ),
      disappearActions: safeParseObj(
        (json['disappear_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivDisappearAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      extensions: safeParseObj(
        (json['extensions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivExtension.fromJson(v),
              )!,
            )
            .toList(),
      ),
      focus: safeParseObj(
        DivFocus.fromJson(json['focus']),
      ),
      height: safeParseObj(
        DivSize.fromJson(json['height']),
        fallback: const DivSize(DivWrapContentSize()),
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
      rowSpan: safeParseIntExpr(
        json['row_span'],
      ),
      selectedActions: safeParseObj(
        (json['selected_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      shape: safeParseObj(
        DivShape.fromJson(json['shape']),
        fallback: const DivShape(DivRoundedRectangleShape()),
      )!,
      spaceBetweenCenters: safeParseObj(
        DivFixedSize.fromJson(json['space_between_centers']),
        fallback: const DivFixedSize(
          value: ValueExpression(15),
        ),
      )!,
      tooltips: safeParseObj(
        (json['tooltips'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivTooltip.fromJson(v),
              )!,
            )
            .toList(),
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
        (json['transition_triggers'] as List<dynamic>?)
            ?.map(
              (v) => safeParseStrEnum(
                v,
                parse: DivTransitionTrigger.fromJson,
              )!,
            )
            .toList(),
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
        (json['visibility_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivVisibilityAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      width: safeParseObj(
        DivSize.fromJson(json['width']),
        fallback: const DivSize(DivMatchParentSize()),
      )!,
    );
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
    switch (json) {
      case 'scale':
        return DivIndicatorAnimation.scale;
      case 'worm':
        return DivIndicatorAnimation.worm;
      case 'slider':
        return DivIndicatorAnimation.slider;
    }
    return null;
  }
}

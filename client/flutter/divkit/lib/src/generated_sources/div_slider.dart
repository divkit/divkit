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
import 'package:divkit/src/generated_sources/div_drawable.dart';
import 'package:divkit/src/generated_sources/div_edge_insets.dart';
import 'package:divkit/src/generated_sources/div_extension.dart';
import 'package:divkit/src/generated_sources/div_focus.dart';
import 'package:divkit/src/generated_sources/div_font_weight.dart';
import 'package:divkit/src/generated_sources/div_match_parent_size.dart';
import 'package:divkit/src/generated_sources/div_point.dart';
import 'package:divkit/src/generated_sources/div_size.dart';
import 'package:divkit/src/generated_sources/div_size_unit.dart';
import 'package:divkit/src/generated_sources/div_tooltip.dart';
import 'package:divkit/src/generated_sources/div_transform.dart';
import 'package:divkit/src/generated_sources/div_transition_trigger.dart';
import 'package:divkit/src/generated_sources/div_variable.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/generated_sources/div_visibility_action.dart';
import 'package:divkit/src/generated_sources/div_wrap_content_size.dart';

class DivSlider with EquatableMixin implements DivBase {
  const DivSlider({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.disappearActions,
    this.extensions,
    this.focus,
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
    this.id,
    this.margins = const DivEdgeInsets(),
    this.maxValue = const ValueExpression(100),
    this.minValue = const ValueExpression(0),
    this.paddings = const DivEdgeInsets(),
    this.ranges,
    this.rowSpan,
    this.secondaryValueAccessibility = const DivAccessibility(),
    this.selectedActions,
    this.thumbSecondaryStyle,
    this.thumbSecondaryTextStyle,
    this.thumbSecondaryValueVariable,
    required this.thumbStyle,
    this.thumbTextStyle,
    this.thumbValueVariable,
    this.tickMarkActiveStyle,
    this.tickMarkInactiveStyle,
    this.tooltips,
    required this.trackActiveStyle,
    required this.trackInactiveStyle,
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

  static const type = "slider";

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

  @override
  final DivEdgeInsets margins;
  // default value: 100
  final Expression<int> maxValue;
  // default value: 0
  final Expression<int> minValue;

  @override
  final DivEdgeInsets paddings;

  final List<DivSliderRange>? ranges;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  final DivAccessibility secondaryValueAccessibility;

  @override
  final List<DivAction>? selectedActions;

  final DivDrawable? thumbSecondaryStyle;

  final DivSliderTextStyle? thumbSecondaryTextStyle;

  final String? thumbSecondaryValueVariable;

  final DivDrawable thumbStyle;

  final DivSliderTextStyle? thumbTextStyle;

  final String? thumbValueVariable;

  final DivDrawable? tickMarkActiveStyle;

  final DivDrawable? tickMarkInactiveStyle;

  @override
  final List<DivTooltip>? tooltips;

  final DivDrawable trackActiveStyle;

  final DivDrawable trackInactiveStyle;

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
        disappearActions,
        extensions,
        focus,
        height,
        id,
        margins,
        maxValue,
        minValue,
        paddings,
        ranges,
        rowSpan,
        secondaryValueAccessibility,
        selectedActions,
        thumbSecondaryStyle,
        thumbSecondaryTextStyle,
        thumbSecondaryValueVariable,
        thumbStyle,
        thumbTextStyle,
        thumbValueVariable,
        tickMarkActiveStyle,
        tickMarkInactiveStyle,
        tooltips,
        trackActiveStyle,
        trackInactiveStyle,
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

  DivSlider copyWith({
    DivAccessibility? accessibility,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    DivSize? height,
    String? Function()? id,
    DivEdgeInsets? margins,
    Expression<int>? maxValue,
    Expression<int>? minValue,
    DivEdgeInsets? paddings,
    List<DivSliderRange>? Function()? ranges,
    Expression<int>? Function()? rowSpan,
    DivAccessibility? secondaryValueAccessibility,
    List<DivAction>? Function()? selectedActions,
    DivDrawable? Function()? thumbSecondaryStyle,
    DivSliderTextStyle? Function()? thumbSecondaryTextStyle,
    String? Function()? thumbSecondaryValueVariable,
    DivDrawable? thumbStyle,
    DivSliderTextStyle? Function()? thumbTextStyle,
    String? Function()? thumbValueVariable,
    DivDrawable? Function()? tickMarkActiveStyle,
    DivDrawable? Function()? tickMarkInactiveStyle,
    List<DivTooltip>? Function()? tooltips,
    DivDrawable? trackActiveStyle,
    DivDrawable? trackInactiveStyle,
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
      DivSlider(
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
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        margins: margins ?? this.margins,
        maxValue: maxValue ?? this.maxValue,
        minValue: minValue ?? this.minValue,
        paddings: paddings ?? this.paddings,
        ranges: ranges != null ? ranges.call() : this.ranges,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        secondaryValueAccessibility:
            secondaryValueAccessibility ?? this.secondaryValueAccessibility,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        thumbSecondaryStyle: thumbSecondaryStyle != null
            ? thumbSecondaryStyle.call()
            : this.thumbSecondaryStyle,
        thumbSecondaryTextStyle: thumbSecondaryTextStyle != null
            ? thumbSecondaryTextStyle.call()
            : this.thumbSecondaryTextStyle,
        thumbSecondaryValueVariable: thumbSecondaryValueVariable != null
            ? thumbSecondaryValueVariable.call()
            : this.thumbSecondaryValueVariable,
        thumbStyle: thumbStyle ?? this.thumbStyle,
        thumbTextStyle: thumbTextStyle != null
            ? thumbTextStyle.call()
            : this.thumbTextStyle,
        thumbValueVariable: thumbValueVariable != null
            ? thumbValueVariable.call()
            : this.thumbValueVariable,
        tickMarkActiveStyle: tickMarkActiveStyle != null
            ? tickMarkActiveStyle.call()
            : this.tickMarkActiveStyle,
        tickMarkInactiveStyle: tickMarkInactiveStyle != null
            ? tickMarkInactiveStyle.call()
            : this.tickMarkInactiveStyle,
        tooltips: tooltips != null ? tooltips.call() : this.tooltips,
        trackActiveStyle: trackActiveStyle ?? this.trackActiveStyle,
        trackInactiveStyle: trackInactiveStyle ?? this.trackInactiveStyle,
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

  static DivSlider? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivSlider(
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
      margins: safeParseObj(
        DivEdgeInsets.fromJson(json['margins']),
        fallback: const DivEdgeInsets(),
      )!,
      maxValue: safeParseIntExpr(
        json['max_value'],
        fallback: 100,
      )!,
      minValue: safeParseIntExpr(
        json['min_value'],
        fallback: 0,
      )!,
      paddings: safeParseObj(
        DivEdgeInsets.fromJson(json['paddings']),
        fallback: const DivEdgeInsets(),
      )!,
      ranges: safeParseObj(
        safeListMap(
            json['ranges'],
            (v) => safeParseObj(
                  DivSliderRange.fromJson(v),
                )!),
      ),
      rowSpan: safeParseIntExpr(
        json['row_span'],
      ),
      secondaryValueAccessibility: safeParseObj(
        DivAccessibility.fromJson(json['secondary_value_accessibility']),
        fallback: const DivAccessibility(),
      )!,
      selectedActions: safeParseObj(
        safeListMap(
            json['selected_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      thumbSecondaryStyle: safeParseObj(
        DivDrawable.fromJson(json['thumb_secondary_style']),
      ),
      thumbSecondaryTextStyle: safeParseObj(
        DivSliderTextStyle.fromJson(json['thumb_secondary_text_style']),
      ),
      thumbSecondaryValueVariable: safeParseStr(
        json['thumb_secondary_value_variable']?.toString(),
      ),
      thumbStyle: safeParseObj(
        DivDrawable.fromJson(json['thumb_style']),
      )!,
      thumbTextStyle: safeParseObj(
        DivSliderTextStyle.fromJson(json['thumb_text_style']),
      ),
      thumbValueVariable: safeParseStr(
        json['thumb_value_variable']?.toString(),
      ),
      tickMarkActiveStyle: safeParseObj(
        DivDrawable.fromJson(json['tick_mark_active_style']),
      ),
      tickMarkInactiveStyle: safeParseObj(
        DivDrawable.fromJson(json['tick_mark_inactive_style']),
      ),
      tooltips: safeParseObj(
        safeListMap(
            json['tooltips'],
            (v) => safeParseObj(
                  DivTooltip.fromJson(v),
                )!),
      ),
      trackActiveStyle: safeParseObj(
        DivDrawable.fromJson(json['track_active_style']),
      )!,
      trackInactiveStyle: safeParseObj(
        DivDrawable.fromJson(json['track_inactive_style']),
      )!,
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

class DivSliderTextStyle with EquatableMixin {
  const DivSliderTextStyle({
    required this.fontSize,
    this.fontSizeUnit = const ValueExpression(DivSizeUnit.sp),
    this.fontWeight = const ValueExpression(DivFontWeight.regular),
    this.fontWeightValue,
    this.offset,
    this.textColor = const ValueExpression(Color(0xFF000000)),
  });

  // constraint: number >= 0
  final Expression<int> fontSize;
  // default value: DivSizeUnit.sp
  final Expression<DivSizeUnit> fontSizeUnit;
  // default value: DivFontWeight.regular
  final Expression<DivFontWeight> fontWeight;
  // constraint: number > 0
  final Expression<int>? fontWeightValue;

  final DivPoint? offset;
  // default value: const Color(0xFF000000)
  final Expression<Color> textColor;

  @override
  List<Object?> get props => [
        fontSize,
        fontSizeUnit,
        fontWeight,
        fontWeightValue,
        offset,
        textColor,
      ];

  DivSliderTextStyle copyWith({
    Expression<int>? fontSize,
    Expression<DivSizeUnit>? fontSizeUnit,
    Expression<DivFontWeight>? fontWeight,
    Expression<int>? Function()? fontWeightValue,
    DivPoint? Function()? offset,
    Expression<Color>? textColor,
  }) =>
      DivSliderTextStyle(
        fontSize: fontSize ?? this.fontSize,
        fontSizeUnit: fontSizeUnit ?? this.fontSizeUnit,
        fontWeight: fontWeight ?? this.fontWeight,
        fontWeightValue: fontWeightValue != null
            ? fontWeightValue.call()
            : this.fontWeightValue,
        offset: offset != null ? offset.call() : this.offset,
        textColor: textColor ?? this.textColor,
      );

  static DivSliderTextStyle? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivSliderTextStyle(
      fontSize: safeParseIntExpr(
        json['font_size'],
      )!,
      fontSizeUnit: safeParseStrEnumExpr(
        json['font_size_unit'],
        parse: DivSizeUnit.fromJson,
        fallback: DivSizeUnit.sp,
      )!,
      fontWeight: safeParseStrEnumExpr(
        json['font_weight'],
        parse: DivFontWeight.fromJson,
        fallback: DivFontWeight.regular,
      )!,
      fontWeightValue: safeParseIntExpr(
        json['font_weight_value'],
      ),
      offset: safeParseObj(
        DivPoint.fromJson(json['offset']),
      ),
      textColor: safeParseColorExpr(
        json['text_color'],
        fallback: const Color(0xFF000000),
      )!,
    );
  }
}

class DivSliderRange with EquatableMixin {
  const DivSliderRange({
    this.end,
    this.margins = const DivEdgeInsets(),
    this.start,
    this.trackActiveStyle,
    this.trackInactiveStyle,
  });

  final Expression<int>? end;

  final DivEdgeInsets margins;

  final Expression<int>? start;

  final DivDrawable? trackActiveStyle;

  final DivDrawable? trackInactiveStyle;

  @override
  List<Object?> get props => [
        end,
        margins,
        start,
        trackActiveStyle,
        trackInactiveStyle,
      ];

  DivSliderRange copyWith({
    Expression<int>? Function()? end,
    DivEdgeInsets? margins,
    Expression<int>? Function()? start,
    DivDrawable? Function()? trackActiveStyle,
    DivDrawable? Function()? trackInactiveStyle,
  }) =>
      DivSliderRange(
        end: end != null ? end.call() : this.end,
        margins: margins ?? this.margins,
        start: start != null ? start.call() : this.start,
        trackActiveStyle: trackActiveStyle != null
            ? trackActiveStyle.call()
            : this.trackActiveStyle,
        trackInactiveStyle: trackInactiveStyle != null
            ? trackInactiveStyle.call()
            : this.trackInactiveStyle,
      );

  static DivSliderRange? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivSliderRange(
      end: safeParseIntExpr(
        json['end'],
      ),
      margins: safeParseObj(
        DivEdgeInsets.fromJson(json['margins']),
        fallback: const DivEdgeInsets(),
      )!,
      start: safeParseIntExpr(
        json['start'],
      ),
      trackActiveStyle: safeParseObj(
        DivDrawable.fromJson(json['track_active_style']),
      ),
      trackInactiveStyle: safeParseObj(
        DivDrawable.fromJson(json['track_inactive_style']),
      ),
    );
  }
}

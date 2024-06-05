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
import 'div_focus.dart';
import 'div_font_weight.dart';
import 'div_match_parent_size.dart';
import 'div_size.dart';
import 'div_size_unit.dart';
import 'div_tooltip.dart';
import 'div_transform.dart';
import 'div_transition_trigger.dart';
import 'div_variable.dart';
import 'div_visibility.dart';
import 'div_visibility_action.dart';
import 'div_wrap_content_size.dart';

class DivSelect with EquatableMixin implements DivBase {
  const DivSelect({
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
    this.fontFamily,
    this.fontSize = const ValueExpression(12),
    this.fontSizeUnit = const ValueExpression(DivSizeUnit.sp),
    this.fontWeight = const ValueExpression(DivFontWeight.regular),
    this.height = const DivSize(DivWrapContentSize()),
    this.hintColor = const ValueExpression(const Color(0x73000000)),
    this.hintText,
    this.id,
    this.letterSpacing = const ValueExpression(0),
    this.lineHeight,
    this.margins = const DivEdgeInsets(),
    required this.options,
    this.paddings = const DivEdgeInsets(),
    this.rowSpan,
    this.selectedActions,
    this.textColor = const ValueExpression(const Color(0xFF000000)),
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    required this.valueVariable,
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize(DivMatchParentSize()),
  });

  static const type = "select";

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

  final Expression<String>? fontFamily;
  // constraint: number >= 0; default value: 12
  final Expression<int> fontSize;
  // default value: DivSizeUnit.sp
  final Expression<DivSizeUnit> fontSizeUnit;
  // default value: DivFontWeight.regular
  final Expression<DivFontWeight> fontWeight;
  // default value: const DivSize(DivWrapContentSize())
  @override
  final DivSize height;
  // default value: const Color(0x73000000)
  final Expression<Color> hintColor;

  final Expression<String>? hintText;

  @override
  final String? id;
  // default value: 0
  final Expression<double> letterSpacing;
  // constraint: number >= 0
  final Expression<int>? lineHeight;

  @override
  final DivEdgeInsets margins;
  // at least 1 elements
  final List<DivSelectOption> options;

  @override
  final DivEdgeInsets paddings;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  @override
  final List<DivAction>? selectedActions;
  // default value: const Color(0xFF000000)
  final Expression<Color> textColor;

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

  final String valueVariable;

  @override
  final List<DivVariable>? variables;
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
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        background,
        border,
        columnSpan,
        disappearActions,
        extensions,
        focus,
        fontFamily,
        fontSize,
        fontSizeUnit,
        fontWeight,
        height,
        hintColor,
        hintText,
        id,
        letterSpacing,
        lineHeight,
        margins,
        options,
        paddings,
        rowSpan,
        selectedActions,
        textColor,
        tooltips,
        transform,
        transitionChange,
        transitionIn,
        transitionOut,
        transitionTriggers,
        valueVariable,
        variables,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  static DivSelect? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivSelect(
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
      fontFamily: safeParseStrExpr(
        json['font_family']?.toString(),
      ),
      fontSize: safeParseIntExpr(
        json['font_size'],
        fallback: 12,
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
      height: safeParseObj(
        DivSize.fromJson(json['height']),
        fallback: const DivSize(DivWrapContentSize()),
      )!,
      hintColor: safeParseColorExpr(
        json['hint_color'],
        fallback: const Color(0x73000000),
      )!,
      hintText: safeParseStrExpr(
        json['hint_text']?.toString(),
      ),
      id: safeParseStr(
        json['id']?.toString(),
      ),
      letterSpacing: safeParseDoubleExpr(
        json['letter_spacing'],
        fallback: 0,
      )!,
      lineHeight: safeParseIntExpr(
        json['line_height'],
      ),
      margins: safeParseObj(
        DivEdgeInsets.fromJson(json['margins']),
        fallback: const DivEdgeInsets(),
      )!,
      options: safeParseObj(
        (json['options'] as List<dynamic>)
            .map(
              (v) => safeParseObj(
                DivSelectOption.fromJson(v),
              )!,
            )
            .toList(),
      )!,
      paddings: safeParseObj(
        DivEdgeInsets.fromJson(json['paddings']),
        fallback: const DivEdgeInsets(),
      )!,
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
      textColor: safeParseColorExpr(
        json['text_color'],
        fallback: const Color(0xFF000000),
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
      valueVariable: safeParseStr(
        json['value_variable']?.toString(),
      )!,
      variables: safeParseObj(
        (json['variables'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivVariable.fromJson(v),
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

class DivSelectOption with EquatableMixin {
  const DivSelectOption({
    this.text,
    required this.value,
  });

  final Expression<String>? text;

  final Expression<String> value;

  @override
  List<Object?> get props => [
        text,
        value,
      ];

  static DivSelectOption? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivSelectOption(
      text: safeParseStrExpr(
        json['text']?.toString(),
      ),
      value: safeParseStrExpr(
        json['value']?.toString(),
      )!,
    );
  }
}

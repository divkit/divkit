// Generated code. Do not modify.

import 'package:divkit/src/schema/div_accessibility.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_base.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_font_weight.dart';
import 'package:divkit/src/schema/div_layout_provider.dart';
import 'package:divkit/src/schema/div_match_parent_size.dart';
import 'package:divkit/src/schema/div_size.dart';
import 'package:divkit/src/schema/div_size_unit.dart';
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

class DivSelect extends Preloadable with EquatableMixin implements DivBase {
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
    this.fontWeightValue,
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.hintColor = const ValueExpression(Color(0x73000000)),
    this.hintText,
    this.id,
    this.layoutProvider,
    this.letterSpacing = const ValueExpression(0),
    this.lineHeight,
    this.margins = const DivEdgeInsets(),
    required this.options,
    this.paddings = const DivEdgeInsets(),
    this.reuseId,
    this.rowSpan,
    this.selectedActions,
    this.textColor = const ValueExpression(Color(0xFF000000)),
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    required this.valueVariable,
    this.variableTriggers,
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(
      DivMatchParentSize(),
    ),
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
  // constraint: number > 0
  final Expression<int>? fontWeightValue;
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize(),)
  @override
  final DivSize height;
  // default value: const Color(0x73000000)
  final Expression<Color> hintColor;

  final Expression<String>? hintText;

  @override
  final String? id;

  @override
  final DivLayoutProvider? layoutProvider;
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

  @override
  final Expression<String>? reuseId;
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
  final List<DivTrigger>? variableTriggers;

  @override
  final List<DivVariable>? variables;
  // default value: DivVisibility.visible
  @override
  final Expression<DivVisibility> visibility;

  @override
  final DivVisibilityAction? visibilityAction;

  @override
  final List<DivVisibilityAction>? visibilityActions;
  // default value: const DivSize.divMatchParentSize(DivMatchParentSize(),)
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
        fontWeightValue,
        height,
        hintColor,
        hintText,
        id,
        layoutProvider,
        letterSpacing,
        lineHeight,
        margins,
        options,
        paddings,
        reuseId,
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
        variableTriggers,
        variables,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  DivSelect copyWith({
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
    Expression<String>? Function()? fontFamily,
    Expression<int>? fontSize,
    Expression<DivSizeUnit>? fontSizeUnit,
    Expression<DivFontWeight>? fontWeight,
    Expression<int>? Function()? fontWeightValue,
    DivSize? height,
    Expression<Color>? hintColor,
    Expression<String>? Function()? hintText,
    String? Function()? id,
    DivLayoutProvider? Function()? layoutProvider,
    Expression<double>? letterSpacing,
    Expression<int>? Function()? lineHeight,
    DivEdgeInsets? margins,
    List<DivSelectOption>? options,
    DivEdgeInsets? paddings,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    List<DivAction>? Function()? selectedActions,
    Expression<Color>? textColor,
    List<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    List<DivTransitionTrigger>? Function()? transitionTriggers,
    String? valueVariable,
    List<DivTrigger>? Function()? variableTriggers,
    List<DivVariable>? Function()? variables,
    Expression<DivVisibility>? visibility,
    DivVisibilityAction? Function()? visibilityAction,
    List<DivVisibilityAction>? Function()? visibilityActions,
    DivSize? width,
  }) =>
      DivSelect(
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
        fontFamily: fontFamily != null ? fontFamily.call() : this.fontFamily,
        fontSize: fontSize ?? this.fontSize,
        fontSizeUnit: fontSizeUnit ?? this.fontSizeUnit,
        fontWeight: fontWeight ?? this.fontWeight,
        fontWeightValue: fontWeightValue != null
            ? fontWeightValue.call()
            : this.fontWeightValue,
        height: height ?? this.height,
        hintColor: hintColor ?? this.hintColor,
        hintText: hintText != null ? hintText.call() : this.hintText,
        id: id != null ? id.call() : this.id,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        letterSpacing: letterSpacing ?? this.letterSpacing,
        lineHeight: lineHeight != null ? lineHeight.call() : this.lineHeight,
        margins: margins ?? this.margins,
        options: options ?? this.options,
        paddings: paddings ?? this.paddings,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        textColor: textColor ?? this.textColor,
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
        valueVariable: valueVariable ?? this.valueVariable,
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

  static DivSelect? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
        fontWeightValue: safeParseIntExpr(
          json['font_weight_value'],
        ),
        height: safeParseObj(
          DivSize.fromJson(json['height']),
          fallback: const DivSize.divWrapContentSize(
            DivWrapContentSize(),
          ),
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
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
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
          safeListMap(
            json['options'],
            (v) => safeParseObj(
              DivSelectOption.fromJson(v),
            )!,
          ),
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
        textColor: safeParseColorExpr(
          json['text_color'],
          fallback: const Color(0xFF000000),
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
        valueVariable: safeParseStr(
          json['value_variable']?.toString(),
        )!,
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

  static Future<DivSelect?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivSelect(
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
        columnSpan: await safeParseIntExprAsync(
          json['column_span'],
        ),
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
        fontFamily: await safeParseStrExprAsync(
          json['font_family']?.toString(),
        ),
        fontSize: (await safeParseIntExprAsync(
          json['font_size'],
          fallback: 12,
        ))!,
        fontSizeUnit: (await safeParseStrEnumExprAsync(
          json['font_size_unit'],
          parse: DivSizeUnit.fromJson,
          fallback: DivSizeUnit.sp,
        ))!,
        fontWeight: (await safeParseStrEnumExprAsync(
          json['font_weight'],
          parse: DivFontWeight.fromJson,
          fallback: DivFontWeight.regular,
        ))!,
        fontWeightValue: await safeParseIntExprAsync(
          json['font_weight_value'],
        ),
        height: (await safeParseObjAsync(
          DivSize.fromJson(json['height']),
          fallback: const DivSize.divWrapContentSize(
            DivWrapContentSize(),
          ),
        ))!,
        hintColor: (await safeParseColorExprAsync(
          json['hint_color'],
          fallback: const Color(0x73000000),
        ))!,
        hintText: await safeParseStrExprAsync(
          json['hint_text']?.toString(),
        ),
        id: await safeParseStrAsync(
          json['id']?.toString(),
        ),
        layoutProvider: await safeParseObjAsync(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        letterSpacing: (await safeParseDoubleExprAsync(
          json['letter_spacing'],
          fallback: 0,
        ))!,
        lineHeight: await safeParseIntExprAsync(
          json['line_height'],
        ),
        margins: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        ))!,
        options: (await safeParseObjAsync(
          await safeListMapAsync(
            json['options'],
            (v) => safeParseObj(
              DivSelectOption.fromJson(v),
            )!,
          ),
        ))!,
        paddings: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        ))!,
        reuseId: await safeParseStrExprAsync(
          json['reuse_id']?.toString(),
        ),
        rowSpan: await safeParseIntExprAsync(
          json['row_span'],
        ),
        selectedActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['selected_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        textColor: (await safeParseColorExprAsync(
          json['text_color'],
          fallback: const Color(0xFF000000),
        ))!,
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
        valueVariable: (await safeParseStrAsync(
          json['value_variable']?.toString(),
        ))!,
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
      await safeFuturesWait(background, (v) => v.preload(context));
      await border.preload(context);
      await columnSpan?.preload(context);
      await safeFuturesWait(disappearActions, (v) => v.preload(context));
      await safeFuturesWait(extensions, (v) => v.preload(context));
      await focus?.preload(context);
      await fontFamily?.preload(context);
      await fontSize.preload(context);
      await fontSizeUnit.preload(context);
      await fontWeight.preload(context);
      await fontWeightValue?.preload(context);
      await height.preload(context);
      await hintColor.preload(context);
      await hintText?.preload(context);
      await layoutProvider?.preload(context);
      await letterSpacing.preload(context);
      await lineHeight?.preload(context);
      await margins.preload(context);
      await safeFuturesWait(options, (v) => v.preload(context));
      await paddings.preload(context);
      await reuseId?.preload(context);
      await rowSpan?.preload(context);
      await safeFuturesWait(selectedActions, (v) => v.preload(context));
      await textColor.preload(context);
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

class DivSelectOption extends Preloadable with EquatableMixin {
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

  DivSelectOption copyWith({
    Expression<String>? Function()? text,
    Expression<String>? value,
  }) =>
      DivSelectOption(
        text: text != null ? text.call() : this.text,
        value: value ?? this.value,
      );

  static DivSelectOption? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivSelectOption(
        text: safeParseStrExpr(
          json['text']?.toString(),
        ),
        value: safeParseStrExpr(
          json['value']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivSelectOption?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivSelectOption(
        text: await safeParseStrExprAsync(
          json['text']?.toString(),
        ),
        value: (await safeParseStrExprAsync(
          json['value']?.toString(),
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
      await text?.preload(context);
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}

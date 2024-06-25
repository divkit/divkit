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
import 'div_input_mask.dart';
import 'div_input_validator.dart';
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

class DivInput with EquatableMixin implements DivBase {
  const DivInput({
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
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
    this.highlightColor,
    this.hintColor = const ValueExpression(const Color(0x73000000)),
    this.hintText,
    this.id,
    this.isEnabled = const ValueExpression(true),
    this.keyboardType =
        const ValueExpression(DivInputKeyboardType.multiLineText),
    this.letterSpacing = const ValueExpression(0),
    this.lineHeight,
    this.margins = const DivEdgeInsets(),
    this.mask,
    this.maxLength,
    this.maxVisibleLines,
    this.nativeInterface,
    this.paddings = const DivEdgeInsets(),
    this.rowSpan,
    this.selectAllOnFocus = const ValueExpression(false),
    this.selectedActions,
    this.textAlignmentHorizontal =
        const ValueExpression(DivAlignmentHorizontal.start),
    this.textAlignmentVertical =
        const ValueExpression(DivAlignmentVertical.center),
    this.textColor = const ValueExpression(const Color(0xFF000000)),
    required this.textVariable,
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    this.validators,
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(DivMatchParentSize()),
  });

  static const type = "input";

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
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize())
  @override
  final DivSize height;

  final Expression<Color>? highlightColor;
  // default value: const Color(0x73000000)
  final Expression<Color> hintColor;

  final Expression<String>? hintText;

  @override
  final String? id;
  // default value: true
  final Expression<bool> isEnabled;
  // default value: DivInputKeyboardType.multiLineText
  final Expression<DivInputKeyboardType> keyboardType;
  // default value: 0
  final Expression<double> letterSpacing;
  // constraint: number >= 0
  final Expression<int>? lineHeight;

  @override
  final DivEdgeInsets margins;

  final DivInputMask? mask;
  // constraint: number > 0
  final Expression<int>? maxLength;
  // constraint: number > 0
  final Expression<int>? maxVisibleLines;

  final DivInputNativeInterface? nativeInterface;

  @override
  final DivEdgeInsets paddings;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;
  // default value: false
  final Expression<bool> selectAllOnFocus;

  @override
  final List<DivAction>? selectedActions;
  // default value: DivAlignmentHorizontal.start
  final Expression<DivAlignmentHorizontal> textAlignmentHorizontal;
  // default value: DivAlignmentVertical.center
  final Expression<DivAlignmentVertical> textAlignmentVertical;
  // default value: const Color(0xFF000000)
  final Expression<Color> textColor;

  final String textVariable;

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

  final List<DivInputValidator>? validators;

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
        fontFamily,
        fontSize,
        fontSizeUnit,
        fontWeight,
        fontWeightValue,
        height,
        highlightColor,
        hintColor,
        hintText,
        id,
        isEnabled,
        keyboardType,
        letterSpacing,
        lineHeight,
        margins,
        mask,
        maxLength,
        maxVisibleLines,
        nativeInterface,
        paddings,
        rowSpan,
        selectAllOnFocus,
        selectedActions,
        textAlignmentHorizontal,
        textAlignmentVertical,
        textColor,
        textVariable,
        tooltips,
        transform,
        transitionChange,
        transitionIn,
        transitionOut,
        transitionTriggers,
        validators,
        variables,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  static DivInput? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivInput(
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
        fallback: const DivSize.divWrapContentSize(DivWrapContentSize()),
      )!,
      highlightColor: safeParseColorExpr(
        json['highlight_color'],
      ),
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
      isEnabled: safeParseBoolExpr(
        json['is_enabled'],
        fallback: true,
      )!,
      keyboardType: safeParseStrEnumExpr(
        json['keyboard_type'],
        parse: DivInputKeyboardType.fromJson,
        fallback: DivInputKeyboardType.multiLineText,
      )!,
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
      mask: safeParseObj(
        DivInputMask.fromJson(json['mask']),
      ),
      maxLength: safeParseIntExpr(
        json['max_length'],
      ),
      maxVisibleLines: safeParseIntExpr(
        json['max_visible_lines'],
      ),
      nativeInterface: safeParseObj(
        DivInputNativeInterface.fromJson(json['native_interface']),
      ),
      paddings: safeParseObj(
        DivEdgeInsets.fromJson(json['paddings']),
        fallback: const DivEdgeInsets(),
      )!,
      rowSpan: safeParseIntExpr(
        json['row_span'],
      ),
      selectAllOnFocus: safeParseBoolExpr(
        json['select_all_on_focus'],
        fallback: false,
      )!,
      selectedActions: safeParseObj(
        safeListMap(
            json['selected_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      textAlignmentHorizontal: safeParseStrEnumExpr(
        json['text_alignment_horizontal'],
        parse: DivAlignmentHorizontal.fromJson,
        fallback: DivAlignmentHorizontal.start,
      )!,
      textAlignmentVertical: safeParseStrEnumExpr(
        json['text_alignment_vertical'],
        parse: DivAlignmentVertical.fromJson,
        fallback: DivAlignmentVertical.center,
      )!,
      textColor: safeParseColorExpr(
        json['text_color'],
        fallback: const Color(0xFF000000),
      )!,
      textVariable: safeParseStr(
        json['text_variable']?.toString(),
      )!,
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
      validators: safeParseObj(
        safeListMap(
            json['validators'],
            (v) => safeParseObj(
                  DivInputValidator.fromJson(v),
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

class DivInputNativeInterface with EquatableMixin {
  const DivInputNativeInterface({
    required this.color,
  });

  final Expression<Color> color;

  @override
  List<Object?> get props => [
        color,
      ];

  static DivInputNativeInterface? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivInputNativeInterface(
      color: safeParseColorExpr(
        json['color'],
      )!,
    );
  }
}

enum DivInputKeyboardType {
  singleLineText('single_line_text'),
  multiLineText('multi_line_text'),
  phone('phone'),
  number('number'),
  email('email'),
  uri('uri'),
  password('password');

  final String value;

  const DivInputKeyboardType(this.value);

  T map<T>({
    required T Function() singleLineText,
    required T Function() multiLineText,
    required T Function() phone,
    required T Function() number,
    required T Function() email,
    required T Function() uri,
    required T Function() password,
  }) {
    switch (this) {
      case DivInputKeyboardType.singleLineText:
        return singleLineText();
      case DivInputKeyboardType.multiLineText:
        return multiLineText();
      case DivInputKeyboardType.phone:
        return phone();
      case DivInputKeyboardType.number:
        return number();
      case DivInputKeyboardType.email:
        return email();
      case DivInputKeyboardType.uri:
        return uri();
      case DivInputKeyboardType.password:
        return password();
    }
  }

  T maybeMap<T>({
    T Function()? singleLineText,
    T Function()? multiLineText,
    T Function()? phone,
    T Function()? number,
    T Function()? email,
    T Function()? uri,
    T Function()? password,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivInputKeyboardType.singleLineText:
        return singleLineText?.call() ?? orElse();
      case DivInputKeyboardType.multiLineText:
        return multiLineText?.call() ?? orElse();
      case DivInputKeyboardType.phone:
        return phone?.call() ?? orElse();
      case DivInputKeyboardType.number:
        return number?.call() ?? orElse();
      case DivInputKeyboardType.email:
        return email?.call() ?? orElse();
      case DivInputKeyboardType.uri:
        return uri?.call() ?? orElse();
      case DivInputKeyboardType.password:
        return password?.call() ?? orElse();
    }
  }

  static DivInputKeyboardType? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'single_line_text':
        return DivInputKeyboardType.singleLineText;
      case 'multi_line_text':
        return DivInputKeyboardType.multiLineText;
      case 'phone':
        return DivInputKeyboardType.phone;
      case 'number':
        return DivInputKeyboardType.number;
      case 'email':
        return DivInputKeyboardType.email;
      case 'uri':
        return DivInputKeyboardType.uri;
      case 'password':
        return DivInputKeyboardType.password;
    }
    return null;
  }
}

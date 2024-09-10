// Generated code. Do not modify.

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
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_font_weight.dart';
import 'package:divkit/src/schema/div_input_filter.dart';
import 'package:divkit/src/schema/div_input_mask.dart';
import 'package:divkit/src/schema/div_input_validator.dart';
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

/// Text input element.
class DivInput extends Preloadable with EquatableMixin implements DivBase {
  const DivInput({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.animators,
    this.autocapitalization =
        const ValueExpression(DivInputAutocapitalization.auto),
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.disappearActions,
    this.extensions,
    this.filters,
    this.focus,
    this.fontFamily,
    this.fontSize = const ValueExpression(12),
    this.fontSizeUnit = const ValueExpression(DivSizeUnit.sp),
    this.fontWeight = const ValueExpression(DivFontWeight.regular),
    this.fontWeightValue,
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.highlightColor,
    this.hintColor = const ValueExpression(Color(0x73000000)),
    this.hintText,
    this.id,
    this.isEnabled = const ValueExpression(true),
    this.keyboardType =
        const ValueExpression(DivInputKeyboardType.multiLineText),
    this.layoutProvider,
    this.letterSpacing = const ValueExpression(0),
    this.lineHeight,
    this.margins = const DivEdgeInsets(),
    this.mask,
    this.maxLength,
    this.maxVisibleLines,
    this.nativeInterface,
    this.paddings = const DivEdgeInsets(),
    this.reuseId,
    this.rowSpan,
    this.selectAllOnFocus = const ValueExpression(false),
    this.selectedActions,
    this.textAlignmentHorizontal =
        const ValueExpression(DivAlignmentHorizontal.start),
    this.textAlignmentVertical =
        const ValueExpression(DivAlignmentVertical.center),
    this.textColor = const ValueExpression(Color(0xFF000000)),
    required this.textVariable,
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    this.validators,
    this.variableTriggers,
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(
      DivMatchParentSize(),
    ),
  });

  static const type = "input";

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

  /// Declaration of animators that can be used to change the value of variables over time.
  @override
  final List<DivAnimator>? animators;

  /// Automatic text capitalization type.
  // default value: DivInputAutocapitalization.auto
  final Expression<DivInputAutocapitalization> autocapitalization;

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

  /// Actions when an element disappears from the screen.
  @override
  final List<DivDisappearAction>? disappearActions;

  /// Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](https://divkit.tech/docs/en/concepts/extensions).
  @override
  final List<DivExtension>? extensions;

  /// Filtering that prevents the entry of text that does not meet specified conditions.
  final List<DivInputFilter>? filters;

  /// Parameters when focusing on an element or losing focus.
  @override
  final DivFocus? focus;

  /// Font family:
  /// • `text` — a standard text font;
  /// • `display` — a family of fonts with a large font size.
  final Expression<String>? fontFamily;

  /// Font size.
  // constraint: number >= 0; default value: 12
  final Expression<int> fontSize;

  /// Unit of measurement:
  /// • `px` — a physical pixel.
  /// • `dp` — a logical pixel that doesn't depend on screen density.
  /// • `sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.
  // default value: DivSizeUnit.sp
  final Expression<DivSizeUnit> fontSizeUnit;

  /// Style.
  // default value: DivFontWeight.regular
  final Expression<DivFontWeight> fontWeight;

  /// Style. Numeric value.
  // constraint: number > 0
  final Expression<int>? fontWeightValue;

  /// Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](https://divkit.tech/docs/en/concepts/layout).
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize(),)
  @override
  final DivSize height;

  /// Text highlight color. If the value isn't set, the color set in the client will be used instead.
  final Expression<Color>? highlightColor;

  /// Text color.
  // default value: const Color(0x73000000)
  final Expression<Color> hintColor;

  /// Tooltip text.
  final Expression<String>? hintText;

  /// Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
  @override
  final String? id;

  /// Indicates if the text editing is enabled.
  // default value: true
  final Expression<bool> isEnabled;

  /// Keyboard type.
  // default value: DivInputKeyboardType.multiLineText
  final Expression<DivInputKeyboardType> keyboardType;

  /// Provides element real size values after a layout cycle.
  @override
  final DivLayoutProvider? layoutProvider;

  /// Spacing between characters.
  // default value: 0
  final Expression<double> letterSpacing;

  /// Line spacing of the text. Units specified in `font_size_unit`.
  // constraint: number >= 0
  final Expression<int>? lineHeight;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Mask for entering text based on the specified template.
  final DivInputMask? mask;

  /// Maximum number of characters that can be entered in the input field.
  // constraint: number > 0
  final Expression<int>? maxLength;

  /// Maximum number of lines to be displayed in the input field.
  // constraint: number > 0
  final Expression<int>? maxVisibleLines;

  /// Text input line used in the native interface.
  final DivInputNativeInterface? nativeInterface;

  /// Internal margins from the element stroke.
  @override
  final DivEdgeInsets paddings;

  /// Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](https://divkit.tech/docs/en/concepts/reuse/reuse.md)
  @override
  final Expression<String>? reuseId;

  /// Merges cells in a string of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  /// Highlighting input text when focused.
  // default value: false
  final Expression<bool> selectAllOnFocus;

  /// List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
  @override
  final List<DivAction>? selectedActions;

  /// Horizontal text alignment.
  // default value: DivAlignmentHorizontal.start
  final Expression<DivAlignmentHorizontal> textAlignmentHorizontal;

  /// Vertical text alignment.
  // default value: DivAlignmentVertical.center
  final Expression<DivAlignmentVertical> textAlignmentVertical;

  /// Text color.
  // default value: const Color(0xFF000000)
  final Expression<Color> textColor;

  /// Name of text storage variable.
  final String textVariable;

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

  /// Validator that checks that the field value meets the specified conditions.
  final List<DivInputValidator>? validators;

  /// Triggers for changing variables within an element.
  @override
  final List<DivTrigger>? variableTriggers;

  /// Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
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
        autocapitalization,
        background,
        border,
        columnSpan,
        disappearActions,
        extensions,
        filters,
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
        layoutProvider,
        letterSpacing,
        lineHeight,
        margins,
        mask,
        maxLength,
        maxVisibleLines,
        nativeInterface,
        paddings,
        reuseId,
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
        variableTriggers,
        variables,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  DivInput copyWith({
    DivAccessibility? accessibility,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivAnimator>? Function()? animators,
    Expression<DivInputAutocapitalization>? autocapitalization,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivExtension>? Function()? extensions,
    List<DivInputFilter>? Function()? filters,
    DivFocus? Function()? focus,
    Expression<String>? Function()? fontFamily,
    Expression<int>? fontSize,
    Expression<DivSizeUnit>? fontSizeUnit,
    Expression<DivFontWeight>? fontWeight,
    Expression<int>? Function()? fontWeightValue,
    DivSize? height,
    Expression<Color>? Function()? highlightColor,
    Expression<Color>? hintColor,
    Expression<String>? Function()? hintText,
    String? Function()? id,
    Expression<bool>? isEnabled,
    Expression<DivInputKeyboardType>? keyboardType,
    DivLayoutProvider? Function()? layoutProvider,
    Expression<double>? letterSpacing,
    Expression<int>? Function()? lineHeight,
    DivEdgeInsets? margins,
    DivInputMask? Function()? mask,
    Expression<int>? Function()? maxLength,
    Expression<int>? Function()? maxVisibleLines,
    DivInputNativeInterface? Function()? nativeInterface,
    DivEdgeInsets? paddings,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Expression<bool>? selectAllOnFocus,
    List<DivAction>? Function()? selectedActions,
    Expression<DivAlignmentHorizontal>? textAlignmentHorizontal,
    Expression<DivAlignmentVertical>? textAlignmentVertical,
    Expression<Color>? textColor,
    String? textVariable,
    List<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    List<DivTransitionTrigger>? Function()? transitionTriggers,
    List<DivInputValidator>? Function()? validators,
    List<DivTrigger>? Function()? variableTriggers,
    List<DivVariable>? Function()? variables,
    Expression<DivVisibility>? visibility,
    DivVisibilityAction? Function()? visibilityAction,
    List<DivVisibilityAction>? Function()? visibilityActions,
    DivSize? width,
  }) =>
      DivInput(
        accessibility: accessibility ?? this.accessibility,
        alignmentHorizontal: alignmentHorizontal != null
            ? alignmentHorizontal.call()
            : this.alignmentHorizontal,
        alignmentVertical: alignmentVertical != null
            ? alignmentVertical.call()
            : this.alignmentVertical,
        alpha: alpha ?? this.alpha,
        animators: animators != null ? animators.call() : this.animators,
        autocapitalization: autocapitalization ?? this.autocapitalization,
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        filters: filters != null ? filters.call() : this.filters,
        focus: focus != null ? focus.call() : this.focus,
        fontFamily: fontFamily != null ? fontFamily.call() : this.fontFamily,
        fontSize: fontSize ?? this.fontSize,
        fontSizeUnit: fontSizeUnit ?? this.fontSizeUnit,
        fontWeight: fontWeight ?? this.fontWeight,
        fontWeightValue: fontWeightValue != null
            ? fontWeightValue.call()
            : this.fontWeightValue,
        height: height ?? this.height,
        highlightColor: highlightColor != null
            ? highlightColor.call()
            : this.highlightColor,
        hintColor: hintColor ?? this.hintColor,
        hintText: hintText != null ? hintText.call() : this.hintText,
        id: id != null ? id.call() : this.id,
        isEnabled: isEnabled ?? this.isEnabled,
        keyboardType: keyboardType ?? this.keyboardType,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        letterSpacing: letterSpacing ?? this.letterSpacing,
        lineHeight: lineHeight != null ? lineHeight.call() : this.lineHeight,
        margins: margins ?? this.margins,
        mask: mask != null ? mask.call() : this.mask,
        maxLength: maxLength != null ? maxLength.call() : this.maxLength,
        maxVisibleLines: maxVisibleLines != null
            ? maxVisibleLines.call()
            : this.maxVisibleLines,
        nativeInterface: nativeInterface != null
            ? nativeInterface.call()
            : this.nativeInterface,
        paddings: paddings ?? this.paddings,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        selectAllOnFocus: selectAllOnFocus ?? this.selectAllOnFocus,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        textAlignmentHorizontal:
            textAlignmentHorizontal ?? this.textAlignmentHorizontal,
        textAlignmentVertical:
            textAlignmentVertical ?? this.textAlignmentVertical,
        textColor: textColor ?? this.textColor,
        textVariable: textVariable ?? this.textVariable,
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
        validators: validators != null ? validators.call() : this.validators,
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

  static DivInput? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
        animators: safeParseObj(
          safeListMap(
            json['animators'],
            (v) => safeParseObj(
              DivAnimator.fromJson(v),
            )!,
          ),
        ),
        autocapitalization: safeParseStrEnumExpr(
          json['autocapitalization'],
          parse: DivInputAutocapitalization.fromJson,
          fallback: DivInputAutocapitalization.auto,
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
        filters: safeParseObj(
          safeListMap(
            json['filters'],
            (v) => safeParseObj(
              DivInputFilter.fromJson(v),
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
        reuseId: safeParseStrExpr(
          json['reuse_id']?.toString(),
        ),
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
            )!,
          ),
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
        validators: safeParseObj(
          safeListMap(
            json['validators'],
            (v) => safeParseObj(
              DivInputValidator.fromJson(v),
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

  static Future<DivInput?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivInput(
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
        autocapitalization: (await safeParseStrEnumExprAsync(
          json['autocapitalization'],
          parse: DivInputAutocapitalization.fromJson,
          fallback: DivInputAutocapitalization.auto,
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
        filters: await safeParseObjAsync(
          await safeListMapAsync(
            json['filters'],
            (v) => safeParseObj(
              DivInputFilter.fromJson(v),
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
        highlightColor: await safeParseColorExprAsync(
          json['highlight_color'],
        ),
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
        isEnabled: (await safeParseBoolExprAsync(
          json['is_enabled'],
          fallback: true,
        ))!,
        keyboardType: (await safeParseStrEnumExprAsync(
          json['keyboard_type'],
          parse: DivInputKeyboardType.fromJson,
          fallback: DivInputKeyboardType.multiLineText,
        ))!,
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
        mask: await safeParseObjAsync(
          DivInputMask.fromJson(json['mask']),
        ),
        maxLength: await safeParseIntExprAsync(
          json['max_length'],
        ),
        maxVisibleLines: await safeParseIntExprAsync(
          json['max_visible_lines'],
        ),
        nativeInterface: await safeParseObjAsync(
          DivInputNativeInterface.fromJson(json['native_interface']),
        ),
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
        selectAllOnFocus: (await safeParseBoolExprAsync(
          json['select_all_on_focus'],
          fallback: false,
        ))!,
        selectedActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['selected_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        textAlignmentHorizontal: (await safeParseStrEnumExprAsync(
          json['text_alignment_horizontal'],
          parse: DivAlignmentHorizontal.fromJson,
          fallback: DivAlignmentHorizontal.start,
        ))!,
        textAlignmentVertical: (await safeParseStrEnumExprAsync(
          json['text_alignment_vertical'],
          parse: DivAlignmentVertical.fromJson,
          fallback: DivAlignmentVertical.center,
        ))!,
        textColor: (await safeParseColorExprAsync(
          json['text_color'],
          fallback: const Color(0xFF000000),
        ))!,
        textVariable: (await safeParseStrAsync(
          json['text_variable']?.toString(),
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
        validators: await safeParseObjAsync(
          await safeListMapAsync(
            json['validators'],
            (v) => safeParseObj(
              DivInputValidator.fromJson(v),
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
      await autocapitalization.preload(context);
      await safeFuturesWait(background, (v) => v.preload(context));
      await border.preload(context);
      await columnSpan?.preload(context);
      await safeFuturesWait(disappearActions, (v) => v.preload(context));
      await safeFuturesWait(extensions, (v) => v.preload(context));
      await safeFuturesWait(filters, (v) => v.preload(context));
      await focus?.preload(context);
      await fontFamily?.preload(context);
      await fontSize.preload(context);
      await fontSizeUnit.preload(context);
      await fontWeight.preload(context);
      await fontWeightValue?.preload(context);
      await height.preload(context);
      await highlightColor?.preload(context);
      await hintColor.preload(context);
      await hintText?.preload(context);
      await isEnabled.preload(context);
      await keyboardType.preload(context);
      await layoutProvider?.preload(context);
      await letterSpacing.preload(context);
      await lineHeight?.preload(context);
      await margins.preload(context);
      await mask?.preload(context);
      await maxLength?.preload(context);
      await maxVisibleLines?.preload(context);
      await nativeInterface?.preload(context);
      await paddings.preload(context);
      await reuseId?.preload(context);
      await rowSpan?.preload(context);
      await selectAllOnFocus.preload(context);
      await safeFuturesWait(selectedActions, (v) => v.preload(context));
      await textAlignmentHorizontal.preload(context);
      await textAlignmentVertical.preload(context);
      await textColor.preload(context);
      await safeFuturesWait(tooltips, (v) => v.preload(context));
      await transform.preload(context);
      await transitionChange?.preload(context);
      await transitionIn?.preload(context);
      await transitionOut?.preload(context);
      await safeFuturesWait(transitionTriggers, (v) => v.preload(context));
      await safeFuturesWait(validators, (v) => v.preload(context));
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

/// Text input line used in the native interface.
class DivInputNativeInterface extends Preloadable with EquatableMixin {
  const DivInputNativeInterface({
    required this.color,
  });

  /// Text input line color.
  final Expression<Color> color;

  @override
  List<Object?> get props => [
        color,
      ];

  DivInputNativeInterface copyWith({
    Expression<Color>? color,
  }) =>
      DivInputNativeInterface(
        color: color ?? this.color,
      );

  static DivInputNativeInterface? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivInputNativeInterface(
        color: safeParseColorExpr(
          json['color'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivInputNativeInterface?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivInputNativeInterface(
        color: (await safeParseColorExprAsync(
          json['color'],
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
      await color.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivInputAutocapitalization implements Preloadable {
  auto('auto'),
  none('none'),
  words('words'),
  sentences('sentences'),
  allCharacters('all_characters');

  final String value;

  const DivInputAutocapitalization(this.value);
  bool get isAuto => this == auto;

  bool get isNone => this == none;

  bool get isWords => this == words;

  bool get isSentences => this == sentences;

  bool get isAllCharacters => this == allCharacters;

  T map<T>({
    required T Function() auto,
    required T Function() none,
    required T Function() words,
    required T Function() sentences,
    required T Function() allCharacters,
  }) {
    switch (this) {
      case DivInputAutocapitalization.auto:
        return auto();
      case DivInputAutocapitalization.none:
        return none();
      case DivInputAutocapitalization.words:
        return words();
      case DivInputAutocapitalization.sentences:
        return sentences();
      case DivInputAutocapitalization.allCharacters:
        return allCharacters();
    }
  }

  T maybeMap<T>({
    T Function()? auto,
    T Function()? none,
    T Function()? words,
    T Function()? sentences,
    T Function()? allCharacters,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivInputAutocapitalization.auto:
        return auto?.call() ?? orElse();
      case DivInputAutocapitalization.none:
        return none?.call() ?? orElse();
      case DivInputAutocapitalization.words:
        return words?.call() ?? orElse();
      case DivInputAutocapitalization.sentences:
        return sentences?.call() ?? orElse();
      case DivInputAutocapitalization.allCharacters:
        return allCharacters?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivInputAutocapitalization? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'auto':
          return DivInputAutocapitalization.auto;
        case 'none':
          return DivInputAutocapitalization.none;
        case 'words':
          return DivInputAutocapitalization.words;
        case 'sentences':
          return DivInputAutocapitalization.sentences;
        case 'all_characters':
          return DivInputAutocapitalization.allCharacters;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivInputAutocapitalization?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'auto':
          return DivInputAutocapitalization.auto;
        case 'none':
          return DivInputAutocapitalization.none;
        case 'words':
          return DivInputAutocapitalization.words;
        case 'sentences':
          return DivInputAutocapitalization.sentences;
        case 'all_characters':
          return DivInputAutocapitalization.allCharacters;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

enum DivInputKeyboardType implements Preloadable {
  singleLineText('single_line_text'),
  multiLineText('multi_line_text'),
  phone('phone'),
  number('number'),
  email('email'),
  uri('uri'),
  password('password');

  final String value;

  const DivInputKeyboardType(this.value);
  bool get isSingleLineText => this == singleLineText;

  bool get isMultiLineText => this == multiLineText;

  bool get isPhone => this == phone;

  bool get isNumber => this == number;

  bool get isEmail => this == email;

  bool get isUri => this == uri;

  bool get isPassword => this == password;

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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivInputKeyboardType? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e) {
      return null;
    }
  }

  static Future<DivInputKeyboardType?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e) {
      return null;
    }
  }
}

// Generated code. Do not modify.

import 'package:divkit/src/schema/div_accessibility.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_animation.dart';
import 'package:divkit/src/schema/div_animator.dart';
import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_base.dart';
import 'package:divkit/src/schema/div_blend_mode.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_font_weight.dart';
import 'package:divkit/src/schema/div_function.dart';
import 'package:divkit/src/schema/div_layout_provider.dart';
import 'package:divkit/src/schema/div_line_style.dart';
import 'package:divkit/src/schema/div_match_parent_size.dart';
import 'package:divkit/src/schema/div_shadow.dart';
import 'package:divkit/src/schema/div_size.dart';
import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/schema/div_text_alignment_vertical.dart';
import 'package:divkit/src/schema/div_text_gradient.dart';
import 'package:divkit/src/schema/div_text_range_background.dart';
import 'package:divkit/src/schema/div_text_range_border.dart';
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

/// Text.
class DivText extends Preloadable with EquatableMixin implements DivBase {
  const DivText({
    this.accessibility = const DivAccessibility(),
    this.action,
    this.actionAnimation = const DivAnimation(
      duration: ValueExpression(
        100,
      ),
      endValue: ValueExpression(
        0.6,
      ),
      name: ValueExpression(
        DivAnimationName.fade,
      ),
      startValue: ValueExpression(
        1,
      ),
    ),
    this.actions,
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.animators,
    this.autoEllipsize,
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.disappearActions,
    this.doubletapActions,
    this.ellipsis,
    this.extensions,
    this.focus,
    this.focusedTextColor,
    this.fontFamily,
    this.fontFeatureSettings,
    this.fontSize = const ValueExpression(12),
    this.fontSizeUnit = const ValueExpression(DivSizeUnit.sp),
    this.fontWeight = const ValueExpression(DivFontWeight.regular),
    this.fontWeightValue,
    this.functions,
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.id,
    this.images,
    this.layoutProvider,
    this.letterSpacing = const ValueExpression(0),
    this.lineHeight,
    this.longtapActions,
    this.margins = const DivEdgeInsets(),
    this.maxLines,
    this.minHiddenLines,
    this.paddings = const DivEdgeInsets(),
    this.ranges,
    this.reuseId,
    this.rowSpan,
    this.selectable = const ValueExpression(false),
    this.selectedActions,
    this.strike = const ValueExpression(DivLineStyle.none),
    required this.text,
    this.textAlignmentHorizontal =
        const ValueExpression(DivAlignmentHorizontal.start),
    this.textAlignmentVertical =
        const ValueExpression(DivAlignmentVertical.top),
    this.textColor = const ValueExpression(Color(0xFF000000)),
    this.textGradient,
    this.textShadow,
    this.tightenWidth = const ValueExpression(false),
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    this.truncate = const ValueExpression(DivTextTruncate.end),
    this.underline = const ValueExpression(DivLineStyle.none),
    this.variableTriggers,
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(
      DivMatchParentSize(),
    ),
  });

  static const type = "text";

  /// Accessibility settings.
  @override
  final DivAccessibility accessibility;

  /// One action when clicking on an element. Not used if the `actions` parameter is set.
  final DivAction? action;

  /// Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
  // default value: const DivAnimation(duration: ValueExpression(100,), endValue: ValueExpression(0.6,), name: ValueExpression(DivAnimationName.fade,), startValue: ValueExpression(1,),)
  final DivAnimation actionAnimation;

  /// Multiple actions when clicking on an element.
  final List<DivAction>? actions;

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

  /// Automatic text cropping to fit the container size.
  final Expression<bool>? autoEllipsize;

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

  /// Action when double-clicking on an element.
  final List<DivAction>? doubletapActions;

  /// Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
  final DivTextEllipsis? ellipsis;

  /// Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](https://divkit.tech/docs/en/concepts/extensions).
  @override
  final List<DivExtension>? extensions;

  /// Parameters when focusing on an element or losing focus.
  @override
  final DivFocus? focus;

  /// Text color when focusing on the element.
  final Expression<Color>? focusedTextColor;

  /// Font family:
  /// • `text` — a standard text font;
  /// • `display` — a family of fonts with a large font size.
  final Expression<String>? fontFamily;

  /// List of OpenType font features. The format matches the CSS attribute "font-feature-settings". Learn more: https://www.w3.org/TR/css-fonts-3/#font-feature-settings-prop
  final Expression<String>? fontFeatureSettings;

  /// Font size.
  // constraint: number >= 0; default value: 12
  final Expression<int> fontSize;
  // default value: DivSizeUnit.sp
  final Expression<DivSizeUnit> fontSizeUnit;

  /// Style.
  // default value: DivFontWeight.regular
  final Expression<DivFontWeight> fontWeight;

  /// Style. Numeric value.
  // constraint: number > 0
  final Expression<int>? fontWeightValue;

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

  /// Images embedded in text.
  final List<DivTextImage>? images;

  /// Provides data on the actual size of the element.
  @override
  final DivLayoutProvider? layoutProvider;

  /// Spacing between characters.
  // default value: 0
  final Expression<double> letterSpacing;

  /// Line spacing of the text.
  // constraint: number >= 0
  final Expression<int>? lineHeight;

  /// Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
  final List<DivAction>? longtapActions;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Maximum number of lines not to be cropped when breaking the limits.
  // constraint: number >= 0
  final Expression<int>? maxLines;

  /// Minimum number of cropped lines when breaking the limits.
  // constraint: number >= 0
  final Expression<int>? minHiddenLines;

  /// Internal margins from the element stroke.
  @override
  final DivEdgeInsets paddings;

  /// A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
  final List<DivTextRange>? ranges;

  /// ID for the div object structure. Used to optimize block reuse. See [block reuse](https://divkit.tech/docs/en/concepts/reuse/reuse.md).
  @override
  final Expression<String>? reuseId;

  /// Merges cells in a string of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  /// Ability to select and copy text.
  // default value: false
  final Expression<bool> selectable;

  /// List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
  @override
  final List<DivAction>? selectedActions;

  /// Strikethrough.
  // default value: DivLineStyle.none
  final Expression<DivLineStyle> strike;

  /// Text.
  final Expression<String> text;

  /// Horizontal text alignment.
  // default value: DivAlignmentHorizontal.start
  final Expression<DivAlignmentHorizontal> textAlignmentHorizontal;

  /// Vertical text alignment.
  // default value: DivAlignmentVertical.top
  final Expression<DivAlignmentVertical> textAlignmentVertical;

  /// Text color. Not used if the `text_gradient` parameter is set.
  // default value: const Color(0xFF000000)
  final Expression<Color> textColor;

  /// Gradient text color.
  final DivTextGradient? textGradient;

  /// Parameters of the shadow applied to the text.
  final DivShadow? textShadow;

  /// Set text width to maximal line width, works only with wrap_content width with constrained=true and max_size set
  // default value: false
  final Expression<bool> tightenWidth;

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

  /// Text cropping method. Use `ellipsis` instead.
  // default value: DivTextTruncate.end
  final Expression<DivTextTruncate> truncate;

  /// Underline.
  // default value: DivLineStyle.none
  final Expression<DivLineStyle> underline;

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
        action,
        actionAnimation,
        actions,
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        animators,
        autoEllipsize,
        background,
        border,
        columnSpan,
        disappearActions,
        doubletapActions,
        ellipsis,
        extensions,
        focus,
        focusedTextColor,
        fontFamily,
        fontFeatureSettings,
        fontSize,
        fontSizeUnit,
        fontWeight,
        fontWeightValue,
        functions,
        height,
        id,
        images,
        layoutProvider,
        letterSpacing,
        lineHeight,
        longtapActions,
        margins,
        maxLines,
        minHiddenLines,
        paddings,
        ranges,
        reuseId,
        rowSpan,
        selectable,
        selectedActions,
        strike,
        text,
        textAlignmentHorizontal,
        textAlignmentVertical,
        textColor,
        textGradient,
        textShadow,
        tightenWidth,
        tooltips,
        transform,
        transitionChange,
        transitionIn,
        transitionOut,
        transitionTriggers,
        truncate,
        underline,
        variableTriggers,
        variables,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  DivText copyWith({
    DivAccessibility? accessibility,
    DivAction? Function()? action,
    DivAnimation? actionAnimation,
    List<DivAction>? Function()? actions,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivAnimator>? Function()? animators,
    Expression<bool>? Function()? autoEllipsize,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivAction>? Function()? doubletapActions,
    DivTextEllipsis? Function()? ellipsis,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    Expression<Color>? Function()? focusedTextColor,
    Expression<String>? Function()? fontFamily,
    Expression<String>? Function()? fontFeatureSettings,
    Expression<int>? fontSize,
    Expression<DivSizeUnit>? fontSizeUnit,
    Expression<DivFontWeight>? fontWeight,
    Expression<int>? Function()? fontWeightValue,
    List<DivFunction>? Function()? functions,
    DivSize? height,
    String? Function()? id,
    List<DivTextImage>? Function()? images,
    DivLayoutProvider? Function()? layoutProvider,
    Expression<double>? letterSpacing,
    Expression<int>? Function()? lineHeight,
    List<DivAction>? Function()? longtapActions,
    DivEdgeInsets? margins,
    Expression<int>? Function()? maxLines,
    Expression<int>? Function()? minHiddenLines,
    DivEdgeInsets? paddings,
    List<DivTextRange>? Function()? ranges,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Expression<bool>? selectable,
    List<DivAction>? Function()? selectedActions,
    Expression<DivLineStyle>? strike,
    Expression<String>? text,
    Expression<DivAlignmentHorizontal>? textAlignmentHorizontal,
    Expression<DivAlignmentVertical>? textAlignmentVertical,
    Expression<Color>? textColor,
    DivTextGradient? Function()? textGradient,
    DivShadow? Function()? textShadow,
    Expression<bool>? tightenWidth,
    List<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    List<DivTransitionTrigger>? Function()? transitionTriggers,
    Expression<DivTextTruncate>? truncate,
    Expression<DivLineStyle>? underline,
    List<DivTrigger>? Function()? variableTriggers,
    List<DivVariable>? Function()? variables,
    Expression<DivVisibility>? visibility,
    DivVisibilityAction? Function()? visibilityAction,
    List<DivVisibilityAction>? Function()? visibilityActions,
    DivSize? width,
  }) =>
      DivText(
        accessibility: accessibility ?? this.accessibility,
        action: action != null ? action.call() : this.action,
        actionAnimation: actionAnimation ?? this.actionAnimation,
        actions: actions != null ? actions.call() : this.actions,
        alignmentHorizontal: alignmentHorizontal != null
            ? alignmentHorizontal.call()
            : this.alignmentHorizontal,
        alignmentVertical: alignmentVertical != null
            ? alignmentVertical.call()
            : this.alignmentVertical,
        alpha: alpha ?? this.alpha,
        animators: animators != null ? animators.call() : this.animators,
        autoEllipsize:
            autoEllipsize != null ? autoEllipsize.call() : this.autoEllipsize,
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        doubletapActions: doubletapActions != null
            ? doubletapActions.call()
            : this.doubletapActions,
        ellipsis: ellipsis != null ? ellipsis.call() : this.ellipsis,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        focusedTextColor: focusedTextColor != null
            ? focusedTextColor.call()
            : this.focusedTextColor,
        fontFamily: fontFamily != null ? fontFamily.call() : this.fontFamily,
        fontFeatureSettings: fontFeatureSettings != null
            ? fontFeatureSettings.call()
            : this.fontFeatureSettings,
        fontSize: fontSize ?? this.fontSize,
        fontSizeUnit: fontSizeUnit ?? this.fontSizeUnit,
        fontWeight: fontWeight ?? this.fontWeight,
        fontWeightValue: fontWeightValue != null
            ? fontWeightValue.call()
            : this.fontWeightValue,
        functions: functions != null ? functions.call() : this.functions,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        images: images != null ? images.call() : this.images,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        letterSpacing: letterSpacing ?? this.letterSpacing,
        lineHeight: lineHeight != null ? lineHeight.call() : this.lineHeight,
        longtapActions: longtapActions != null
            ? longtapActions.call()
            : this.longtapActions,
        margins: margins ?? this.margins,
        maxLines: maxLines != null ? maxLines.call() : this.maxLines,
        minHiddenLines: minHiddenLines != null
            ? minHiddenLines.call()
            : this.minHiddenLines,
        paddings: paddings ?? this.paddings,
        ranges: ranges != null ? ranges.call() : this.ranges,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        selectable: selectable ?? this.selectable,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        strike: strike ?? this.strike,
        text: text ?? this.text,
        textAlignmentHorizontal:
            textAlignmentHorizontal ?? this.textAlignmentHorizontal,
        textAlignmentVertical:
            textAlignmentVertical ?? this.textAlignmentVertical,
        textColor: textColor ?? this.textColor,
        textGradient:
            textGradient != null ? textGradient.call() : this.textGradient,
        textShadow: textShadow != null ? textShadow.call() : this.textShadow,
        tightenWidth: tightenWidth ?? this.tightenWidth,
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
        truncate: truncate ?? this.truncate,
        underline: underline ?? this.underline,
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

  static DivText? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivText(
        accessibility: safeParseObj(
          DivAccessibility.fromJson(json['accessibility']),
          fallback: const DivAccessibility(),
        )!,
        action: safeParseObj(
          DivAction.fromJson(json['action']),
        ),
        actionAnimation: safeParseObj(
          DivAnimation.fromJson(json['action_animation']),
          fallback: const DivAnimation(
            duration: ValueExpression(
              100,
            ),
            endValue: ValueExpression(
              0.6,
            ),
            name: ValueExpression(
              DivAnimationName.fade,
            ),
            startValue: ValueExpression(
              1,
            ),
          ),
        )!,
        actions: safeParseObj(
          safeListMap(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
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
        animators: safeParseObj(
          safeListMap(
            json['animators'],
            (v) => safeParseObj(
              DivAnimator.fromJson(v),
            )!,
          ),
        ),
        autoEllipsize: safeParseBoolExpr(
          json['auto_ellipsize'],
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
        disappearActions: safeParseObj(
          safeListMap(
            json['disappear_actions'],
            (v) => safeParseObj(
              DivDisappearAction.fromJson(v),
            )!,
          ),
        ),
        doubletapActions: safeParseObj(
          safeListMap(
            json['doubletap_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        ellipsis: safeParseObj(
          DivTextEllipsis.fromJson(json['ellipsis']),
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
        focusedTextColor: safeParseColorExpr(
          json['focused_text_color'],
        ),
        fontFamily: safeParseStrExpr(
          json['font_family']?.toString(),
        ),
        fontFeatureSettings: safeParseStrExpr(
          json['font_feature_settings']?.toString(),
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
        images: safeParseObj(
          safeListMap(
            json['images'],
            (v) => safeParseObj(
              DivTextImage.fromJson(v),
            )!,
          ),
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
        longtapActions: safeParseObj(
          safeListMap(
            json['longtap_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        margins: safeParseObj(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        )!,
        maxLines: safeParseIntExpr(
          json['max_lines'],
        ),
        minHiddenLines: safeParseIntExpr(
          json['min_hidden_lines'],
        ),
        paddings: safeParseObj(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        )!,
        ranges: safeParseObj(
          safeListMap(
            json['ranges'],
            (v) => safeParseObj(
              DivTextRange.fromJson(v),
            )!,
          ),
        ),
        reuseId: safeParseStrExpr(
          json['reuse_id']?.toString(),
        ),
        rowSpan: safeParseIntExpr(
          json['row_span'],
        ),
        selectable: safeParseBoolExpr(
          json['selectable'],
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
        strike: safeParseStrEnumExpr(
          json['strike'],
          parse: DivLineStyle.fromJson,
          fallback: DivLineStyle.none,
        )!,
        text: safeParseStrExpr(
          json['text']?.toString(),
        )!,
        textAlignmentHorizontal: safeParseStrEnumExpr(
          json['text_alignment_horizontal'],
          parse: DivAlignmentHorizontal.fromJson,
          fallback: DivAlignmentHorizontal.start,
        )!,
        textAlignmentVertical: safeParseStrEnumExpr(
          json['text_alignment_vertical'],
          parse: DivAlignmentVertical.fromJson,
          fallback: DivAlignmentVertical.top,
        )!,
        textColor: safeParseColorExpr(
          json['text_color'],
          fallback: const Color(0xFF000000),
        )!,
        textGradient: safeParseObj(
          DivTextGradient.fromJson(json['text_gradient']),
        ),
        textShadow: safeParseObj(
          DivShadow.fromJson(json['text_shadow']),
        ),
        tightenWidth: safeParseBoolExpr(
          json['tighten_width'],
          fallback: false,
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
        truncate: safeParseStrEnumExpr(
          json['truncate'],
          parse: DivTextTruncate.fromJson,
          fallback: DivTextTruncate.end,
        )!,
        underline: safeParseStrEnumExpr(
          json['underline'],
          parse: DivLineStyle.fromJson,
          fallback: DivLineStyle.none,
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

  static Future<DivText?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivText(
        accessibility: (await safeParseObjAsync(
          DivAccessibility.fromJson(json['accessibility']),
          fallback: const DivAccessibility(),
        ))!,
        action: await safeParseObjAsync(
          DivAction.fromJson(json['action']),
        ),
        actionAnimation: (await safeParseObjAsync(
          DivAnimation.fromJson(json['action_animation']),
          fallback: const DivAnimation(
            duration: ValueExpression(
              100,
            ),
            endValue: ValueExpression(
              0.6,
            ),
            name: ValueExpression(
              DivAnimationName.fade,
            ),
            startValue: ValueExpression(
              1,
            ),
          ),
        ))!,
        actions: await safeParseObjAsync(
          await safeListMapAsync(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
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
        autoEllipsize: await safeParseBoolExprAsync(
          json['auto_ellipsize'],
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
        doubletapActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['doubletap_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        ellipsis: await safeParseObjAsync(
          DivTextEllipsis.fromJson(json['ellipsis']),
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
        focusedTextColor: await safeParseColorExprAsync(
          json['focused_text_color'],
        ),
        fontFamily: await safeParseStrExprAsync(
          json['font_family']?.toString(),
        ),
        fontFeatureSettings: await safeParseStrExprAsync(
          json['font_feature_settings']?.toString(),
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
        images: await safeParseObjAsync(
          await safeListMapAsync(
            json['images'],
            (v) => safeParseObj(
              DivTextImage.fromJson(v),
            )!,
          ),
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
        longtapActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['longtap_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        margins: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        ))!,
        maxLines: await safeParseIntExprAsync(
          json['max_lines'],
        ),
        minHiddenLines: await safeParseIntExprAsync(
          json['min_hidden_lines'],
        ),
        paddings: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        ))!,
        ranges: await safeParseObjAsync(
          await safeListMapAsync(
            json['ranges'],
            (v) => safeParseObj(
              DivTextRange.fromJson(v),
            )!,
          ),
        ),
        reuseId: await safeParseStrExprAsync(
          json['reuse_id']?.toString(),
        ),
        rowSpan: await safeParseIntExprAsync(
          json['row_span'],
        ),
        selectable: (await safeParseBoolExprAsync(
          json['selectable'],
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
        strike: (await safeParseStrEnumExprAsync(
          json['strike'],
          parse: DivLineStyle.fromJson,
          fallback: DivLineStyle.none,
        ))!,
        text: (await safeParseStrExprAsync(
          json['text']?.toString(),
        ))!,
        textAlignmentHorizontal: (await safeParseStrEnumExprAsync(
          json['text_alignment_horizontal'],
          parse: DivAlignmentHorizontal.fromJson,
          fallback: DivAlignmentHorizontal.start,
        ))!,
        textAlignmentVertical: (await safeParseStrEnumExprAsync(
          json['text_alignment_vertical'],
          parse: DivAlignmentVertical.fromJson,
          fallback: DivAlignmentVertical.top,
        ))!,
        textColor: (await safeParseColorExprAsync(
          json['text_color'],
          fallback: const Color(0xFF000000),
        ))!,
        textGradient: await safeParseObjAsync(
          DivTextGradient.fromJson(json['text_gradient']),
        ),
        textShadow: await safeParseObjAsync(
          DivShadow.fromJson(json['text_shadow']),
        ),
        tightenWidth: (await safeParseBoolExprAsync(
          json['tighten_width'],
          fallback: false,
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
        truncate: (await safeParseStrEnumExprAsync(
          json['truncate'],
          parse: DivTextTruncate.fromJson,
          fallback: DivTextTruncate.end,
        ))!,
        underline: (await safeParseStrEnumExprAsync(
          json['underline'],
          parse: DivLineStyle.fromJson,
          fallback: DivLineStyle.none,
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
      await action?.preload(context);
      await actionAnimation.preload(context);
      await safeFuturesWait(actions, (v) => v.preload(context));
      await alignmentHorizontal?.preload(context);
      await alignmentVertical?.preload(context);
      await alpha.preload(context);
      await safeFuturesWait(animators, (v) => v.preload(context));
      await autoEllipsize?.preload(context);
      await safeFuturesWait(background, (v) => v.preload(context));
      await border.preload(context);
      await columnSpan?.preload(context);
      await safeFuturesWait(disappearActions, (v) => v.preload(context));
      await safeFuturesWait(doubletapActions, (v) => v.preload(context));
      await ellipsis?.preload(context);
      await safeFuturesWait(extensions, (v) => v.preload(context));
      await focus?.preload(context);
      await focusedTextColor?.preload(context);
      await fontFamily?.preload(context);
      await fontFeatureSettings?.preload(context);
      await fontSize.preload(context);
      await fontSizeUnit.preload(context);
      await fontWeight.preload(context);
      await fontWeightValue?.preload(context);
      await safeFuturesWait(functions, (v) => v.preload(context));
      await height.preload(context);
      await safeFuturesWait(images, (v) => v.preload(context));
      await layoutProvider?.preload(context);
      await letterSpacing.preload(context);
      await lineHeight?.preload(context);
      await safeFuturesWait(longtapActions, (v) => v.preload(context));
      await margins.preload(context);
      await maxLines?.preload(context);
      await minHiddenLines?.preload(context);
      await paddings.preload(context);
      await safeFuturesWait(ranges, (v) => v.preload(context));
      await reuseId?.preload(context);
      await rowSpan?.preload(context);
      await selectable.preload(context);
      await safeFuturesWait(selectedActions, (v) => v.preload(context));
      await strike.preload(context);
      await text.preload(context);
      await textAlignmentHorizontal.preload(context);
      await textAlignmentVertical.preload(context);
      await textColor.preload(context);
      await textGradient?.preload(context);
      await textShadow?.preload(context);
      await tightenWidth.preload(context);
      await safeFuturesWait(tooltips, (v) => v.preload(context));
      await transform.preload(context);
      await transitionChange?.preload(context);
      await transitionIn?.preload(context);
      await transitionOut?.preload(context);
      await safeFuturesWait(transitionTriggers, (v) => v.preload(context));
      await truncate.preload(context);
      await underline.preload(context);
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

/// Additional parameters of the character range.
class DivTextRange extends Preloadable with EquatableMixin {
  const DivTextRange({
    this.actions,
    this.alignmentVertical,
    this.background,
    this.border,
    this.end,
    this.fontFamily,
    this.fontFeatureSettings,
    this.fontSize,
    this.fontSizeUnit = const ValueExpression(DivSizeUnit.sp),
    this.fontWeight,
    this.fontWeightValue,
    this.letterSpacing,
    this.lineHeight,
    this.start = const ValueExpression(0),
    this.strike,
    this.textColor,
    this.textShadow,
    this.topOffset,
    this.underline,
  });

  /// Action when clicking on text.
  final List<DivAction>? actions;

  /// Vertical text alignment within the row.
  final Expression<DivTextAlignmentVertical>? alignmentVertical;

  /// Character range background.
  final DivTextRangeBackground? background;

  /// Character range border.
  final DivTextRangeBorder? border;

  /// Ordinal number of the last character to be included in the range.
  // constraint: number > 0
  final Expression<int>? end;

  /// Font family:
  /// • `text` — a standard text font;
  /// • `display` — a family of fonts with a large font size.
  final Expression<String>? fontFamily;

  /// List of OpenType font features. The format matches the CSS attribute "font-feature-settings". Learn more: https://www.w3.org/TR/css-fonts-3/#font-feature-settings-prop
  final Expression<String>? fontFeatureSettings;

  /// Font size.
  // constraint: number >= 0
  final Expression<int>? fontSize;

  /// Unit of measurement:
  /// • `px` — a physical pixel.
  /// • `dp` — a logical pixel that doesn't depend on screen density.
  /// • `sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.
  // default value: DivSizeUnit.sp
  final Expression<DivSizeUnit> fontSizeUnit;

  /// Style.
  final Expression<DivFontWeight>? fontWeight;

  /// Style. Numeric value.
  // constraint: number > 0
  final Expression<int>? fontWeightValue;

  /// Spacing between characters.
  final Expression<double>? letterSpacing;

  /// Line spacing of the text. Units specified in `font_size_unit`.
  // constraint: number >= 0
  final Expression<int>? lineHeight;

  /// Ordinal number of a character which the range begins from. The first character has a number `0`.
  // constraint: number >= 0; default value: 0
  final Expression<int> start;

  /// Strikethrough.
  final Expression<DivLineStyle>? strike;

  /// Text color.
  final Expression<Color>? textColor;

  /// Parameters of the shadow applied to the character range.
  final DivShadow? textShadow;

  /// Top margin of the character range. Units specified in `font_size_unit`.
  // constraint: number >= 0
  final Expression<int>? topOffset;

  /// Underline.
  final Expression<DivLineStyle>? underline;

  @override
  List<Object?> get props => [
        actions,
        alignmentVertical,
        background,
        border,
        end,
        fontFamily,
        fontFeatureSettings,
        fontSize,
        fontSizeUnit,
        fontWeight,
        fontWeightValue,
        letterSpacing,
        lineHeight,
        start,
        strike,
        textColor,
        textShadow,
        topOffset,
        underline,
      ];

  DivTextRange copyWith({
    List<DivAction>? Function()? actions,
    Expression<DivTextAlignmentVertical>? Function()? alignmentVertical,
    DivTextRangeBackground? Function()? background,
    DivTextRangeBorder? Function()? border,
    Expression<int>? Function()? end,
    Expression<String>? Function()? fontFamily,
    Expression<String>? Function()? fontFeatureSettings,
    Expression<int>? Function()? fontSize,
    Expression<DivSizeUnit>? fontSizeUnit,
    Expression<DivFontWeight>? Function()? fontWeight,
    Expression<int>? Function()? fontWeightValue,
    Expression<double>? Function()? letterSpacing,
    Expression<int>? Function()? lineHeight,
    Expression<int>? start,
    Expression<DivLineStyle>? Function()? strike,
    Expression<Color>? Function()? textColor,
    DivShadow? Function()? textShadow,
    Expression<int>? Function()? topOffset,
    Expression<DivLineStyle>? Function()? underline,
  }) =>
      DivTextRange(
        actions: actions != null ? actions.call() : this.actions,
        alignmentVertical: alignmentVertical != null
            ? alignmentVertical.call()
            : this.alignmentVertical,
        background: background != null ? background.call() : this.background,
        border: border != null ? border.call() : this.border,
        end: end != null ? end.call() : this.end,
        fontFamily: fontFamily != null ? fontFamily.call() : this.fontFamily,
        fontFeatureSettings: fontFeatureSettings != null
            ? fontFeatureSettings.call()
            : this.fontFeatureSettings,
        fontSize: fontSize != null ? fontSize.call() : this.fontSize,
        fontSizeUnit: fontSizeUnit ?? this.fontSizeUnit,
        fontWeight: fontWeight != null ? fontWeight.call() : this.fontWeight,
        fontWeightValue: fontWeightValue != null
            ? fontWeightValue.call()
            : this.fontWeightValue,
        letterSpacing:
            letterSpacing != null ? letterSpacing.call() : this.letterSpacing,
        lineHeight: lineHeight != null ? lineHeight.call() : this.lineHeight,
        start: start ?? this.start,
        strike: strike != null ? strike.call() : this.strike,
        textColor: textColor != null ? textColor.call() : this.textColor,
        textShadow: textShadow != null ? textShadow.call() : this.textShadow,
        topOffset: topOffset != null ? topOffset.call() : this.topOffset,
        underline: underline != null ? underline.call() : this.underline,
      );

  static DivTextRange? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTextRange(
        actions: safeParseObj(
          safeListMap(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        alignmentVertical: safeParseStrEnumExpr(
          json['alignment_vertical'],
          parse: DivTextAlignmentVertical.fromJson,
        ),
        background: safeParseObj(
          DivTextRangeBackground.fromJson(json['background']),
        ),
        border: safeParseObj(
          DivTextRangeBorder.fromJson(json['border']),
        ),
        end: safeParseIntExpr(
          json['end'],
        ),
        fontFamily: safeParseStrExpr(
          json['font_family']?.toString(),
        ),
        fontFeatureSettings: safeParseStrExpr(
          json['font_feature_settings']?.toString(),
        ),
        fontSize: safeParseIntExpr(
          json['font_size'],
        ),
        fontSizeUnit: safeParseStrEnumExpr(
          json['font_size_unit'],
          parse: DivSizeUnit.fromJson,
          fallback: DivSizeUnit.sp,
        )!,
        fontWeight: safeParseStrEnumExpr(
          json['font_weight'],
          parse: DivFontWeight.fromJson,
        ),
        fontWeightValue: safeParseIntExpr(
          json['font_weight_value'],
        ),
        letterSpacing: safeParseDoubleExpr(
          json['letter_spacing'],
        ),
        lineHeight: safeParseIntExpr(
          json['line_height'],
        ),
        start: safeParseIntExpr(
          json['start'],
          fallback: 0,
        )!,
        strike: safeParseStrEnumExpr(
          json['strike'],
          parse: DivLineStyle.fromJson,
        ),
        textColor: safeParseColorExpr(
          json['text_color'],
        ),
        textShadow: safeParseObj(
          DivShadow.fromJson(json['text_shadow']),
        ),
        topOffset: safeParseIntExpr(
          json['top_offset'],
        ),
        underline: safeParseStrEnumExpr(
          json['underline'],
          parse: DivLineStyle.fromJson,
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivTextRange?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTextRange(
        actions: await safeParseObjAsync(
          await safeListMapAsync(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        alignmentVertical: await safeParseStrEnumExprAsync(
          json['alignment_vertical'],
          parse: DivTextAlignmentVertical.fromJson,
        ),
        background: await safeParseObjAsync(
          DivTextRangeBackground.fromJson(json['background']),
        ),
        border: await safeParseObjAsync(
          DivTextRangeBorder.fromJson(json['border']),
        ),
        end: await safeParseIntExprAsync(
          json['end'],
        ),
        fontFamily: await safeParseStrExprAsync(
          json['font_family']?.toString(),
        ),
        fontFeatureSettings: await safeParseStrExprAsync(
          json['font_feature_settings']?.toString(),
        ),
        fontSize: await safeParseIntExprAsync(
          json['font_size'],
        ),
        fontSizeUnit: (await safeParseStrEnumExprAsync(
          json['font_size_unit'],
          parse: DivSizeUnit.fromJson,
          fallback: DivSizeUnit.sp,
        ))!,
        fontWeight: await safeParseStrEnumExprAsync(
          json['font_weight'],
          parse: DivFontWeight.fromJson,
        ),
        fontWeightValue: await safeParseIntExprAsync(
          json['font_weight_value'],
        ),
        letterSpacing: await safeParseDoubleExprAsync(
          json['letter_spacing'],
        ),
        lineHeight: await safeParseIntExprAsync(
          json['line_height'],
        ),
        start: (await safeParseIntExprAsync(
          json['start'],
          fallback: 0,
        ))!,
        strike: await safeParseStrEnumExprAsync(
          json['strike'],
          parse: DivLineStyle.fromJson,
        ),
        textColor: await safeParseColorExprAsync(
          json['text_color'],
        ),
        textShadow: await safeParseObjAsync(
          DivShadow.fromJson(json['text_shadow']),
        ),
        topOffset: await safeParseIntExprAsync(
          json['top_offset'],
        ),
        underline: await safeParseStrEnumExprAsync(
          json['underline'],
          parse: DivLineStyle.fromJson,
        ),
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
      await safeFuturesWait(actions, (v) => v.preload(context));
      await alignmentVertical?.preload(context);
      await background?.preload(context);
      await border?.preload(context);
      await end?.preload(context);
      await fontFamily?.preload(context);
      await fontFeatureSettings?.preload(context);
      await fontSize?.preload(context);
      await fontSizeUnit.preload(context);
      await fontWeight?.preload(context);
      await fontWeightValue?.preload(context);
      await letterSpacing?.preload(context);
      await lineHeight?.preload(context);
      await start.preload(context);
      await strike?.preload(context);
      await textColor?.preload(context);
      await textShadow?.preload(context);
      await topOffset?.preload(context);
      await underline?.preload(context);
    } catch (e) {
      return;
    }
  }
}

/// Image.
class DivTextImage extends Preloadable with EquatableMixin {
  const DivTextImage({
    this.accessibility = const DivTextImageAccessibility(),
    this.alignmentVertical =
        const ValueExpression(DivTextAlignmentVertical.center),
    this.height = const DivFixedSize(
      value: ValueExpression(
        20,
      ),
    ),
    this.preloadRequired = const ValueExpression(false),
    required this.start,
    this.tintColor,
    this.tintMode = const ValueExpression(DivBlendMode.sourceIn),
    required this.url,
    this.width = const DivFixedSize(
      value: ValueExpression(
        20,
      ),
    ),
  });

  final DivTextImageAccessibility accessibility;

  /// Vertical image alignment within the row.
  // default value: DivTextAlignmentVertical.center
  final Expression<DivTextAlignmentVertical> alignmentVertical;

  /// Image height.
  // default value: const DivFixedSize(value: ValueExpression(20,),)
  final DivFixedSize height;

  /// Background image must be loaded before the display.
  // default value: false
  final Expression<bool> preloadRequired;

  /// A symbol to insert prior to an image. To insert an image at the end of the text, specify the number of the last character plus one.
  // constraint: number >= 0
  final Expression<int> start;

  /// New color of a contour image.
  final Expression<Color>? tintColor;

  /// Blend mode of the color specified in `tint_color`.
  // default value: DivBlendMode.sourceIn
  final Expression<DivBlendMode> tintMode;

  /// Image URL.
  final Expression<Uri> url;

  /// Image width.
  // default value: const DivFixedSize(value: ValueExpression(20,),)
  final DivFixedSize width;

  @override
  List<Object?> get props => [
        accessibility,
        alignmentVertical,
        height,
        preloadRequired,
        start,
        tintColor,
        tintMode,
        url,
        width,
      ];

  DivTextImage copyWith({
    DivTextImageAccessibility? accessibility,
    Expression<DivTextAlignmentVertical>? alignmentVertical,
    DivFixedSize? height,
    Expression<bool>? preloadRequired,
    Expression<int>? start,
    Expression<Color>? Function()? tintColor,
    Expression<DivBlendMode>? tintMode,
    Expression<Uri>? url,
    DivFixedSize? width,
  }) =>
      DivTextImage(
        accessibility: accessibility ?? this.accessibility,
        alignmentVertical: alignmentVertical ?? this.alignmentVertical,
        height: height ?? this.height,
        preloadRequired: preloadRequired ?? this.preloadRequired,
        start: start ?? this.start,
        tintColor: tintColor != null ? tintColor.call() : this.tintColor,
        tintMode: tintMode ?? this.tintMode,
        url: url ?? this.url,
        width: width ?? this.width,
      );

  static DivTextImage? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTextImage(
        accessibility: safeParseObj(
          DivTextImageAccessibility.fromJson(json['accessibility']),
          fallback: const DivTextImageAccessibility(),
        )!,
        alignmentVertical: safeParseStrEnumExpr(
          json['alignment_vertical'],
          parse: DivTextAlignmentVertical.fromJson,
          fallback: DivTextAlignmentVertical.center,
        )!,
        height: safeParseObj(
          DivFixedSize.fromJson(json['height']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              20,
            ),
          ),
        )!,
        preloadRequired: safeParseBoolExpr(
          json['preload_required'],
          fallback: false,
        )!,
        start: safeParseIntExpr(
          json['start'],
        )!,
        tintColor: safeParseColorExpr(
          json['tint_color'],
        ),
        tintMode: safeParseStrEnumExpr(
          json['tint_mode'],
          parse: DivBlendMode.fromJson,
          fallback: DivBlendMode.sourceIn,
        )!,
        url: safeParseUriExpr(json['url'])!,
        width: safeParseObj(
          DivFixedSize.fromJson(json['width']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              20,
            ),
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivTextImage?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTextImage(
        accessibility: (await safeParseObjAsync(
          DivTextImageAccessibility.fromJson(json['accessibility']),
          fallback: const DivTextImageAccessibility(),
        ))!,
        alignmentVertical: (await safeParseStrEnumExprAsync(
          json['alignment_vertical'],
          parse: DivTextAlignmentVertical.fromJson,
          fallback: DivTextAlignmentVertical.center,
        ))!,
        height: (await safeParseObjAsync(
          DivFixedSize.fromJson(json['height']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              20,
            ),
          ),
        ))!,
        preloadRequired: (await safeParseBoolExprAsync(
          json['preload_required'],
          fallback: false,
        ))!,
        start: (await safeParseIntExprAsync(
          json['start'],
        ))!,
        tintColor: await safeParseColorExprAsync(
          json['tint_color'],
        ),
        tintMode: (await safeParseStrEnumExprAsync(
          json['tint_mode'],
          parse: DivBlendMode.fromJson,
          fallback: DivBlendMode.sourceIn,
        ))!,
        url: (await safeParseUriExprAsync(json['url']))!,
        width: (await safeParseObjAsync(
          DivFixedSize.fromJson(json['width']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              20,
            ),
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
      await alignmentVertical.preload(context);
      await height.preload(context);
      await preloadRequired.preload(context);
      await start.preload(context);
      await tintColor?.preload(context);
      await tintMode.preload(context);
      await url.preload(context);
      await width.preload(context);
    } catch (e) {
      return;
    }
  }
}

class DivTextImageAccessibility extends Preloadable with EquatableMixin {
  const DivTextImageAccessibility({
    this.description,
    this.type = DivTextImageAccessibilityType.auto,
  });

  /// Element description. It is used as the main description for screen reading applications.
  final Expression<String>? description;

  /// Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
  // default value: DivTextImageAccessibilityType.auto
  final DivTextImageAccessibilityType type;

  @override
  List<Object?> get props => [
        description,
        type,
      ];

  DivTextImageAccessibility copyWith({
    Expression<String>? Function()? description,
    DivTextImageAccessibilityType? type,
  }) =>
      DivTextImageAccessibility(
        description:
            description != null ? description.call() : this.description,
        type: type ?? this.type,
      );

  static DivTextImageAccessibility? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTextImageAccessibility(
        description: safeParseStrExpr(
          json['description']?.toString(),
        ),
        type: safeParseStrEnum(
          json['type'],
          parse: DivTextImageAccessibilityType.fromJson,
          fallback: DivTextImageAccessibilityType.auto,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivTextImageAccessibility?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTextImageAccessibility(
        description: await safeParseStrExprAsync(
          json['description']?.toString(),
        ),
        type: (await safeParseStrEnumAsync(
          json['type'],
          parse: DivTextImageAccessibilityType.fromJson,
          fallback: DivTextImageAccessibilityType.auto,
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
      await description?.preload(context);
      await type.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivTextImageAccessibilityType implements Preloadable {
  none('none'),
  button('button'),
  image('image'),
  text('text'),
  auto('auto');

  final String value;

  const DivTextImageAccessibilityType(this.value);
  bool get isNone => this == none;

  bool get isButton => this == button;

  bool get isImage => this == image;

  bool get isText => this == text;

  bool get isAuto => this == auto;

  T map<T>({
    required T Function() none,
    required T Function() button,
    required T Function() image,
    required T Function() text,
    required T Function() auto,
  }) {
    switch (this) {
      case DivTextImageAccessibilityType.none:
        return none();
      case DivTextImageAccessibilityType.button:
        return button();
      case DivTextImageAccessibilityType.image:
        return image();
      case DivTextImageAccessibilityType.text:
        return text();
      case DivTextImageAccessibilityType.auto:
        return auto();
    }
  }

  T maybeMap<T>({
    T Function()? none,
    T Function()? button,
    T Function()? image,
    T Function()? text,
    T Function()? auto,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivTextImageAccessibilityType.none:
        return none?.call() ?? orElse();
      case DivTextImageAccessibilityType.button:
        return button?.call() ?? orElse();
      case DivTextImageAccessibilityType.image:
        return image?.call() ?? orElse();
      case DivTextImageAccessibilityType.text:
        return text?.call() ?? orElse();
      case DivTextImageAccessibilityType.auto:
        return auto?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivTextImageAccessibilityType? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivTextImageAccessibilityType.none;
        case 'button':
          return DivTextImageAccessibilityType.button;
        case 'image':
          return DivTextImageAccessibilityType.image;
        case 'text':
          return DivTextImageAccessibilityType.text;
        case 'auto':
          return DivTextImageAccessibilityType.auto;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivTextImageAccessibilityType?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivTextImageAccessibilityType.none;
        case 'button':
          return DivTextImageAccessibilityType.button;
        case 'image':
          return DivTextImageAccessibilityType.image;
        case 'text':
          return DivTextImageAccessibilityType.text;
        case 'auto':
          return DivTextImageAccessibilityType.auto;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

/// Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
class DivTextEllipsis extends Preloadable with EquatableMixin {
  const DivTextEllipsis({
    this.actions,
    this.images,
    this.ranges,
    required this.text,
  });

  /// Actions when clicking on a crop marker.
  final List<DivAction>? actions;

  /// Images embedded in a crop marker.
  final List<DivTextImage>? images;

  /// Character ranges inside a crop marker with different text styles.
  final List<DivTextRange>? ranges;

  /// Marker text.
  final Expression<String> text;

  @override
  List<Object?> get props => [
        actions,
        images,
        ranges,
        text,
      ];

  DivTextEllipsis copyWith({
    List<DivAction>? Function()? actions,
    List<DivTextImage>? Function()? images,
    List<DivTextRange>? Function()? ranges,
    Expression<String>? text,
  }) =>
      DivTextEllipsis(
        actions: actions != null ? actions.call() : this.actions,
        images: images != null ? images.call() : this.images,
        ranges: ranges != null ? ranges.call() : this.ranges,
        text: text ?? this.text,
      );

  static DivTextEllipsis? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTextEllipsis(
        actions: safeParseObj(
          safeListMap(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        images: safeParseObj(
          safeListMap(
            json['images'],
            (v) => safeParseObj(
              DivTextImage.fromJson(v),
            )!,
          ),
        ),
        ranges: safeParseObj(
          safeListMap(
            json['ranges'],
            (v) => safeParseObj(
              DivTextRange.fromJson(v),
            )!,
          ),
        ),
        text: safeParseStrExpr(
          json['text']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivTextEllipsis?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTextEllipsis(
        actions: await safeParseObjAsync(
          await safeListMapAsync(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        images: await safeParseObjAsync(
          await safeListMapAsync(
            json['images'],
            (v) => safeParseObj(
              DivTextImage.fromJson(v),
            )!,
          ),
        ),
        ranges: await safeParseObjAsync(
          await safeListMapAsync(
            json['ranges'],
            (v) => safeParseObj(
              DivTextRange.fromJson(v),
            )!,
          ),
        ),
        text: (await safeParseStrExprAsync(
          json['text']?.toString(),
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
      await safeFuturesWait(actions, (v) => v.preload(context));
      await safeFuturesWait(images, (v) => v.preload(context));
      await safeFuturesWait(ranges, (v) => v.preload(context));
      await text.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivTextTruncate implements Preloadable {
  none('none'),
  start('start'),
  end('end'),
  middle('middle');

  final String value;

  const DivTextTruncate(this.value);
  bool get isNone => this == none;

  bool get isStart => this == start;

  bool get isEnd => this == end;

  bool get isMiddle => this == middle;

  T map<T>({
    required T Function() none,
    required T Function() start,
    required T Function() end,
    required T Function() middle,
  }) {
    switch (this) {
      case DivTextTruncate.none:
        return none();
      case DivTextTruncate.start:
        return start();
      case DivTextTruncate.end:
        return end();
      case DivTextTruncate.middle:
        return middle();
    }
  }

  T maybeMap<T>({
    T Function()? none,
    T Function()? start,
    T Function()? end,
    T Function()? middle,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivTextTruncate.none:
        return none?.call() ?? orElse();
      case DivTextTruncate.start:
        return start?.call() ?? orElse();
      case DivTextTruncate.end:
        return end?.call() ?? orElse();
      case DivTextTruncate.middle:
        return middle?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivTextTruncate? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivTextTruncate.none;
        case 'start':
          return DivTextTruncate.start;
        case 'end':
          return DivTextTruncate.end;
        case 'middle':
          return DivTextTruncate.middle;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivTextTruncate?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivTextTruncate.none;
        case 'start':
          return DivTextTruncate.start;
        case 'end':
          return DivTextTruncate.end;
        case 'middle':
          return DivTextTruncate.middle;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

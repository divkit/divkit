// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_accessibility.dart';
import 'div_action.dart';
import 'div_alignment_horizontal.dart';
import 'div_alignment_vertical.dart';
import 'div_animation.dart';
import 'div_appearance_transition.dart';
import 'div_background.dart';
import 'div_base.dart';
import 'div_blend_mode.dart';
import 'div_border.dart';
import 'div_change_transition.dart';
import 'div_disappear_action.dart';
import 'div_edge_insets.dart';
import 'div_extension.dart';
import 'div_fixed_size.dart';
import 'div_focus.dart';
import 'div_font_weight.dart';
import 'div_line_style.dart';
import 'div_match_parent_size.dart';
import 'div_shadow.dart';
import 'div_size.dart';
import 'div_size_unit.dart';
import 'div_text_gradient.dart';
import 'div_text_range_background.dart';
import 'div_text_range_border.dart';
import 'div_tooltip.dart';
import 'div_transform.dart';
import 'div_transition_trigger.dart';
import 'div_variable.dart';
import 'div_visibility.dart';
import 'div_visibility_action.dart';
import 'div_wrap_content_size.dart';

class DivText with EquatableMixin implements DivBase {
  const DivText({
    this.accessibility = const DivAccessibility(),
    this.action,
    this.actionAnimation = const DivAnimation(
      duration: ValueExpression(100),
      endValue: ValueExpression(0.6),
      name: ValueExpression(DivAnimationName.fade),
      startValue: ValueExpression(1),
    ),
    this.actions,
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
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
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
    this.id,
    this.images,
    this.letterSpacing = const ValueExpression(0),
    this.lineHeight,
    this.longtapActions,
    this.margins = const DivEdgeInsets(),
    this.maxLines,
    this.minHiddenLines,
    this.paddings = const DivEdgeInsets(),
    this.ranges,
    this.rowSpan,
    this.selectable = const ValueExpression(false),
    this.selectedActions,
    this.strike = const ValueExpression(DivLineStyle.none),
    required this.text,
    this.textAlignmentHorizontal =
        const ValueExpression(DivAlignmentHorizontal.start),
    this.textAlignmentVertical =
        const ValueExpression(DivAlignmentVertical.top),
    this.textColor = const ValueExpression(const Color(0xFF000000)),
    this.textGradient,
    this.textShadow,
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    this.truncate = const ValueExpression(DivTextTruncate.end),
    this.underline = const ValueExpression(DivLineStyle.none),
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(DivMatchParentSize()),
  });

  static const type = "text";

  @override
  final DivAccessibility accessibility;

  final DivAction? action;
  // default value: const DivAnimation(duration: ValueExpression(100), endValue: ValueExpression(0.6), name: ValueExpression(DivAnimationName.fade), startValue: ValueExpression(1),)
  final DivAnimation actionAnimation;

  final List<DivAction>? actions;

  @override
  final Expression<DivAlignmentHorizontal>? alignmentHorizontal;

  @override
  final Expression<DivAlignmentVertical>? alignmentVertical;
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  @override
  final Expression<double> alpha;

  final Expression<bool>? autoEllipsize;

  @override
  final List<DivBackground>? background;

  @override
  final DivBorder border;
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;

  @override
  final List<DivDisappearAction>? disappearActions;

  final List<DivAction>? doubletapActions;

  final DivTextEllipsis? ellipsis;

  @override
  final List<DivExtension>? extensions;

  @override
  final DivFocus? focus;

  final Expression<Color>? focusedTextColor;

  final Expression<String>? fontFamily;

  final Expression<String>? fontFeatureSettings;
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

  @override
  final String? id;

  final List<DivTextImage>? images;
  // default value: 0
  final Expression<double> letterSpacing;
  // constraint: number >= 0
  final Expression<int>? lineHeight;

  final List<DivAction>? longtapActions;

  @override
  final DivEdgeInsets margins;
  // constraint: number >= 0
  final Expression<int>? maxLines;
  // constraint: number >= 0
  final Expression<int>? minHiddenLines;

  @override
  final DivEdgeInsets paddings;

  final List<DivTextRange>? ranges;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;
  // default value: false
  final Expression<bool> selectable;

  @override
  final List<DivAction>? selectedActions;
  // default value: DivLineStyle.none
  final Expression<DivLineStyle> strike;

  final Expression<String> text;
  // default value: DivAlignmentHorizontal.start
  final Expression<DivAlignmentHorizontal> textAlignmentHorizontal;
  // default value: DivAlignmentVertical.top
  final Expression<DivAlignmentVertical> textAlignmentVertical;
  // default value: const Color(0xFF000000)
  final Expression<Color> textColor;

  final DivTextGradient? textGradient;

  final DivShadow? textShadow;

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
  // default value: DivTextTruncate.end
  final Expression<DivTextTruncate> truncate;
  // default value: DivLineStyle.none
  final Expression<DivLineStyle> underline;

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
        action,
        actionAnimation,
        actions,
        alignmentHorizontal,
        alignmentVertical,
        alpha,
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
        height,
        id,
        images,
        letterSpacing,
        lineHeight,
        longtapActions,
        margins,
        maxLines,
        minHiddenLines,
        paddings,
        ranges,
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
        tooltips,
        transform,
        transitionChange,
        transitionIn,
        transitionOut,
        transitionTriggers,
        truncate,
        underline,
        variables,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  static DivText? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
          duration: ValueExpression(100),
          endValue: ValueExpression(0.6),
          name: ValueExpression(DivAnimationName.fade),
          startValue: ValueExpression(1),
        ),
      )!,
      actions: safeParseObj(
        safeListMap(
            json['actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
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
      autoEllipsize: safeParseBoolExpr(
        json['auto_ellipsize'],
      ),
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
      doubletapActions: safeParseObj(
        safeListMap(
            json['doubletap_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      ellipsis: safeParseObj(
        DivTextEllipsis.fromJson(json['ellipsis']),
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
      height: safeParseObj(
        DivSize.fromJson(json['height']),
        fallback: const DivSize.divWrapContentSize(DivWrapContentSize()),
      )!,
      id: safeParseStr(
        json['id']?.toString(),
      ),
      images: safeParseObj(
        safeListMap(
            json['images'],
            (v) => safeParseObj(
                  DivTextImage.fromJson(v),
                )!),
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
                )!),
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
                )!),
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
                )!),
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

class DivTextRange with EquatableMixin {
  const DivTextRange({
    this.actions,
    this.background,
    this.border,
    required this.end,
    this.fontFamily,
    this.fontFeatureSettings,
    this.fontSize,
    this.fontSizeUnit = const ValueExpression(DivSizeUnit.sp),
    this.fontWeight,
    this.fontWeightValue,
    this.letterSpacing,
    this.lineHeight,
    required this.start,
    this.strike,
    this.textColor,
    this.textShadow,
    this.topOffset,
    this.underline,
  });

  final List<DivAction>? actions;

  final DivTextRangeBackground? background;

  final DivTextRangeBorder? border;
  // constraint: number > 0
  final Expression<int> end;

  final Expression<String>? fontFamily;

  final Expression<String>? fontFeatureSettings;
  // constraint: number >= 0
  final Expression<int>? fontSize;
  // default value: DivSizeUnit.sp
  final Expression<DivSizeUnit> fontSizeUnit;

  final Expression<DivFontWeight>? fontWeight;
  // constraint: number > 0
  final Expression<int>? fontWeightValue;

  final Expression<double>? letterSpacing;
  // constraint: number >= 0
  final Expression<int>? lineHeight;
  // constraint: number >= 0
  final Expression<int> start;

  final Expression<DivLineStyle>? strike;

  final Expression<Color>? textColor;

  final DivShadow? textShadow;
  // constraint: number >= 0
  final Expression<int>? topOffset;

  final Expression<DivLineStyle>? underline;

  @override
  List<Object?> get props => [
        actions,
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

  static DivTextRange? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTextRange(
      actions: safeParseObj(
        safeListMap(
            json['actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      background: safeParseObj(
        DivTextRangeBackground.fromJson(json['background']),
      ),
      border: safeParseObj(
        DivTextRangeBorder.fromJson(json['border']),
      ),
      end: safeParseIntExpr(
        json['end'],
      )!,
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
  }
}

class DivTextImage with EquatableMixin {
  const DivTextImage({
    this.height = const DivFixedSize(
      value: ValueExpression(20),
    ),
    this.preloadRequired = const ValueExpression(false),
    required this.start,
    this.tintColor,
    this.tintMode = const ValueExpression(DivBlendMode.sourceIn),
    required this.url,
    this.width = const DivFixedSize(
      value: ValueExpression(20),
    ),
  });

  // default value: const DivFixedSize(value: ValueExpression(20),)
  final DivFixedSize height;
  // default value: false
  final Expression<bool> preloadRequired;
  // constraint: number >= 0
  final Expression<int> start;

  final Expression<Color>? tintColor;
  // default value: DivBlendMode.sourceIn
  final Expression<DivBlendMode> tintMode;

  final Expression<Uri> url;
  // default value: const DivFixedSize(value: ValueExpression(20),)
  final DivFixedSize width;

  @override
  List<Object?> get props => [
        height,
        preloadRequired,
        start,
        tintColor,
        tintMode,
        url,
        width,
      ];

  static DivTextImage? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTextImage(
      height: safeParseObj(
        DivFixedSize.fromJson(json['height']),
        fallback: const DivFixedSize(
          value: ValueExpression(20),
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
          value: ValueExpression(20),
        ),
      )!,
    );
  }
}

class DivTextEllipsis with EquatableMixin {
  const DivTextEllipsis({
    this.actions,
    this.images,
    this.ranges,
    required this.text,
  });

  final List<DivAction>? actions;

  final List<DivTextImage>? images;

  final List<DivTextRange>? ranges;

  final Expression<String> text;

  @override
  List<Object?> get props => [
        actions,
        images,
        ranges,
        text,
      ];

  static DivTextEllipsis? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTextEllipsis(
      actions: safeParseObj(
        safeListMap(
            json['actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      images: safeParseObj(
        safeListMap(
            json['images'],
            (v) => safeParseObj(
                  DivTextImage.fromJson(v),
                )!),
      ),
      ranges: safeParseObj(
        safeListMap(
            json['ranges'],
            (v) => safeParseObj(
                  DivTextRange.fromJson(v),
                )!),
      ),
      text: safeParseStrExpr(
        json['text']?.toString(),
      )!,
    );
  }
}

enum DivTextTruncate {
  none('none'),
  start('start'),
  end('end'),
  middle('middle');

  final String value;

  const DivTextTruncate(this.value);

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

  static DivTextTruncate? fromJson(String? json) {
    if (json == null) {
      return null;
    }
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
  }
}

// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div.dart';
import 'div_accessibility.dart';
import 'div_action.dart';
import 'div_alignment_horizontal.dart';
import 'div_alignment_vertical.dart';
import 'div_appearance_transition.dart';
import 'div_background.dart';
import 'div_base.dart';
import 'div_border.dart';
import 'div_change_transition.dart';
import 'div_corners_radius.dart';
import 'div_disappear_action.dart';
import 'div_edge_insets.dart';
import 'div_extension.dart';
import 'div_fixed_size.dart';
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

class DivTabs with EquatableMixin implements DivBase {
  const DivTabs({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.disappearActions,
    this.dynamicHeight = const ValueExpression(false),
    this.extensions,
    this.focus,
    this.hasSeparator = const ValueExpression(false),
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
    this.id,
    required this.items,
    this.margins = const DivEdgeInsets(),
    this.paddings = const DivEdgeInsets(),
    this.restrictParentScroll = const ValueExpression(false),
    this.rowSpan,
    this.selectedActions,
    this.selectedTab = const ValueExpression(0),
    this.separatorColor = const ValueExpression(const Color(0x14000000)),
    this.separatorPaddings = const DivEdgeInsets(
      bottom: ValueExpression(0),
      left: ValueExpression(12),
      right: ValueExpression(12),
      top: ValueExpression(0),
    ),
    this.switchTabsByContentSwipeEnabled = const ValueExpression(true),
    this.tabTitleDelimiter,
    this.tabTitleStyle = const DivTabsTabTitleStyle(),
    this.titlePaddings = const DivEdgeInsets(
      bottom: ValueExpression(8),
      left: ValueExpression(12),
      right: ValueExpression(12),
      top: ValueExpression(0),
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

  static const type = "tabs";

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
  // default value: false
  final Expression<bool> dynamicHeight;

  @override
  final List<DivExtension>? extensions;

  @override
  final DivFocus? focus;
  // default value: false
  final Expression<bool> hasSeparator;
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize())
  @override
  final DivSize height;

  @override
  final String? id;
  // at least 1 elements
  final List<DivTabsItem> items;

  @override
  final DivEdgeInsets margins;

  @override
  final DivEdgeInsets paddings;
  // default value: false
  final Expression<bool> restrictParentScroll;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  @override
  final List<DivAction>? selectedActions;
  // constraint: number >= 0; default value: 0
  final Expression<int> selectedTab;
  // default value: const Color(0x14000000)
  final Expression<Color> separatorColor;
  // default value: const DivEdgeInsets(bottom: ValueExpression(0), left: ValueExpression(12), right: ValueExpression(12), top: ValueExpression(0),)
  final DivEdgeInsets separatorPaddings;
  // default value: true
  final Expression<bool> switchTabsByContentSwipeEnabled;

  final DivTabsTabTitleDelimiter? tabTitleDelimiter;

  final DivTabsTabTitleStyle tabTitleStyle;
  // default value: const DivEdgeInsets(bottom: ValueExpression(8), left: ValueExpression(12), right: ValueExpression(12), top: ValueExpression(0),)
  final DivEdgeInsets titlePaddings;

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
        disappearActions,
        dynamicHeight,
        extensions,
        focus,
        hasSeparator,
        height,
        id,
        items,
        margins,
        paddings,
        restrictParentScroll,
        rowSpan,
        selectedActions,
        selectedTab,
        separatorColor,
        separatorPaddings,
        switchTabsByContentSwipeEnabled,
        tabTitleDelimiter,
        tabTitleStyle,
        titlePaddings,
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

  static DivTabs? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTabs(
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
      dynamicHeight: safeParseBoolExpr(
        json['dynamic_height'],
        fallback: false,
      )!,
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
      hasSeparator: safeParseBoolExpr(
        json['has_separator'],
        fallback: false,
      )!,
      height: safeParseObj(
        DivSize.fromJson(json['height']),
        fallback: const DivSize.divWrapContentSize(DivWrapContentSize()),
      )!,
      id: safeParseStr(
        json['id']?.toString(),
      ),
      items: safeParseObj(
        safeListMap(
            json['items'],
            (v) => safeParseObj(
                  DivTabsItem.fromJson(v),
                )!),
      )!,
      margins: safeParseObj(
        DivEdgeInsets.fromJson(json['margins']),
        fallback: const DivEdgeInsets(),
      )!,
      paddings: safeParseObj(
        DivEdgeInsets.fromJson(json['paddings']),
        fallback: const DivEdgeInsets(),
      )!,
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
      selectedTab: safeParseIntExpr(
        json['selected_tab'],
        fallback: 0,
      )!,
      separatorColor: safeParseColorExpr(
        json['separator_color'],
        fallback: const Color(0x14000000),
      )!,
      separatorPaddings: safeParseObj(
        DivEdgeInsets.fromJson(json['separator_paddings']),
        fallback: const DivEdgeInsets(
          bottom: ValueExpression(0),
          left: ValueExpression(12),
          right: ValueExpression(12),
          top: ValueExpression(0),
        ),
      )!,
      switchTabsByContentSwipeEnabled: safeParseBoolExpr(
        json['switch_tabs_by_content_swipe_enabled'],
        fallback: true,
      )!,
      tabTitleDelimiter: safeParseObj(
        DivTabsTabTitleDelimiter.fromJson(json['tab_title_delimiter']),
      ),
      tabTitleStyle: safeParseObj(
        DivTabsTabTitleStyle.fromJson(json['tab_title_style']),
        fallback: const DivTabsTabTitleStyle(),
      )!,
      titlePaddings: safeParseObj(
        DivEdgeInsets.fromJson(json['title_paddings']),
        fallback: const DivEdgeInsets(
          bottom: ValueExpression(8),
          left: ValueExpression(12),
          right: ValueExpression(12),
          top: ValueExpression(0),
        ),
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

class DivTabsTabTitleStyle with EquatableMixin {
  const DivTabsTabTitleStyle({
    this.activeBackgroundColor = const ValueExpression(const Color(0xFFFFDC60)),
    this.activeFontWeight,
    this.activeTextColor = const ValueExpression(const Color(0xCC000000)),
    this.animationDuration = const ValueExpression(300),
    this.animationType =
        const ValueExpression(DivTabsTabTitleStyleAnimationType.slide),
    this.cornerRadius,
    this.cornersRadius,
    this.fontFamily,
    this.fontSize = const ValueExpression(12),
    this.fontSizeUnit = const ValueExpression(DivSizeUnit.sp),
    this.fontWeight = const ValueExpression(DivFontWeight.regular),
    this.inactiveBackgroundColor,
    this.inactiveFontWeight,
    this.inactiveTextColor = const ValueExpression(const Color(0x80000000)),
    this.itemSpacing = const ValueExpression(0),
    this.letterSpacing = const ValueExpression(0),
    this.lineHeight,
    this.paddings = const DivEdgeInsets(
      bottom: ValueExpression(6),
      left: ValueExpression(8),
      right: ValueExpression(8),
      top: ValueExpression(6),
    ),
  });

  // default value: const Color(0xFFFFDC60)
  final Expression<Color> activeBackgroundColor;

  final Expression<DivFontWeight>? activeFontWeight;
  // default value: const Color(0xCC000000)
  final Expression<Color> activeTextColor;
  // constraint: number >= 0; default value: 300
  final Expression<int> animationDuration;
  // default value: DivTabsTabTitleStyleAnimationType.slide
  final Expression<DivTabsTabTitleStyleAnimationType> animationType;
  // constraint: number >= 0
  final Expression<int>? cornerRadius;

  final DivCornersRadius? cornersRadius;

  final Expression<String>? fontFamily;
  // constraint: number >= 0; default value: 12
  final Expression<int> fontSize;
  // default value: DivSizeUnit.sp
  final Expression<DivSizeUnit> fontSizeUnit;
  // default value: DivFontWeight.regular
  final Expression<DivFontWeight> fontWeight;

  final Expression<Color>? inactiveBackgroundColor;

  final Expression<DivFontWeight>? inactiveFontWeight;
  // default value: const Color(0x80000000)
  final Expression<Color> inactiveTextColor;
  // constraint: number >= 0; default value: 0
  final Expression<int> itemSpacing;
  // default value: 0
  final Expression<double> letterSpacing;
  // constraint: number >= 0
  final Expression<int>? lineHeight;
  // default value: const DivEdgeInsets(bottom: ValueExpression(6), left: ValueExpression(8), right: ValueExpression(8), top: ValueExpression(6),)
  final DivEdgeInsets paddings;

  @override
  List<Object?> get props => [
        activeBackgroundColor,
        activeFontWeight,
        activeTextColor,
        animationDuration,
        animationType,
        cornerRadius,
        cornersRadius,
        fontFamily,
        fontSize,
        fontSizeUnit,
        fontWeight,
        inactiveBackgroundColor,
        inactiveFontWeight,
        inactiveTextColor,
        itemSpacing,
        letterSpacing,
        lineHeight,
        paddings,
      ];

  static DivTabsTabTitleStyle? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTabsTabTitleStyle(
      activeBackgroundColor: safeParseColorExpr(
        json['active_background_color'],
        fallback: const Color(0xFFFFDC60),
      )!,
      activeFontWeight: safeParseStrEnumExpr(
        json['active_font_weight'],
        parse: DivFontWeight.fromJson,
      ),
      activeTextColor: safeParseColorExpr(
        json['active_text_color'],
        fallback: const Color(0xCC000000),
      )!,
      animationDuration: safeParseIntExpr(
        json['animation_duration'],
        fallback: 300,
      )!,
      animationType: safeParseStrEnumExpr(
        json['animation_type'],
        parse: DivTabsTabTitleStyleAnimationType.fromJson,
        fallback: DivTabsTabTitleStyleAnimationType.slide,
      )!,
      cornerRadius: safeParseIntExpr(
        json['corner_radius'],
      ),
      cornersRadius: safeParseObj(
        DivCornersRadius.fromJson(json['corners_radius']),
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
      inactiveBackgroundColor: safeParseColorExpr(
        json['inactive_background_color'],
      ),
      inactiveFontWeight: safeParseStrEnumExpr(
        json['inactive_font_weight'],
        parse: DivFontWeight.fromJson,
      ),
      inactiveTextColor: safeParseColorExpr(
        json['inactive_text_color'],
        fallback: const Color(0x80000000),
      )!,
      itemSpacing: safeParseIntExpr(
        json['item_spacing'],
        fallback: 0,
      )!,
      letterSpacing: safeParseDoubleExpr(
        json['letter_spacing'],
        fallback: 0,
      )!,
      lineHeight: safeParseIntExpr(
        json['line_height'],
      ),
      paddings: safeParseObj(
        DivEdgeInsets.fromJson(json['paddings']),
        fallback: const DivEdgeInsets(
          bottom: ValueExpression(6),
          left: ValueExpression(8),
          right: ValueExpression(8),
          top: ValueExpression(6),
        ),
      )!,
    );
  }
}

enum DivTabsTabTitleStyleAnimationType {
  slide('slide'),
  fade('fade'),
  none('none');

  final String value;

  const DivTabsTabTitleStyleAnimationType(this.value);

  T map<T>({
    required T Function() slide,
    required T Function() fade,
    required T Function() none,
  }) {
    switch (this) {
      case DivTabsTabTitleStyleAnimationType.slide:
        return slide();
      case DivTabsTabTitleStyleAnimationType.fade:
        return fade();
      case DivTabsTabTitleStyleAnimationType.none:
        return none();
    }
  }

  T maybeMap<T>({
    T Function()? slide,
    T Function()? fade,
    T Function()? none,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivTabsTabTitleStyleAnimationType.slide:
        return slide?.call() ?? orElse();
      case DivTabsTabTitleStyleAnimationType.fade:
        return fade?.call() ?? orElse();
      case DivTabsTabTitleStyleAnimationType.none:
        return none?.call() ?? orElse();
    }
  }

  static DivTabsTabTitleStyleAnimationType? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'slide':
        return DivTabsTabTitleStyleAnimationType.slide;
      case 'fade':
        return DivTabsTabTitleStyleAnimationType.fade;
      case 'none':
        return DivTabsTabTitleStyleAnimationType.none;
    }
    return null;
  }
}

class DivTabsTabTitleDelimiter with EquatableMixin {
  const DivTabsTabTitleDelimiter({
    this.height = const DivFixedSize(
      value: ValueExpression(12),
    ),
    required this.imageUrl,
    this.width = const DivFixedSize(
      value: ValueExpression(12),
    ),
  });

  // default value: const DivFixedSize(value: ValueExpression(12),)
  final DivFixedSize height;

  final Expression<Uri> imageUrl;
  // default value: const DivFixedSize(value: ValueExpression(12),)
  final DivFixedSize width;

  @override
  List<Object?> get props => [
        height,
        imageUrl,
        width,
      ];

  static DivTabsTabTitleDelimiter? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTabsTabTitleDelimiter(
      height: safeParseObj(
        DivFixedSize.fromJson(json['height']),
        fallback: const DivFixedSize(
          value: ValueExpression(12),
        ),
      )!,
      imageUrl: safeParseUriExpr(json['image_url'])!,
      width: safeParseObj(
        DivFixedSize.fromJson(json['width']),
        fallback: const DivFixedSize(
          value: ValueExpression(12),
        ),
      )!,
    );
  }
}

class DivTabsItem with EquatableMixin {
  const DivTabsItem({
    required this.div,
    required this.title,
    this.titleClickAction,
  });

  final Div div;

  final Expression<String> title;

  final DivAction? titleClickAction;

  @override
  List<Object?> get props => [
        div,
        title,
        titleClickAction,
      ];

  static DivTabsItem? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTabsItem(
      div: safeParseObj(
        Div.fromJson(json['div']),
      )!,
      title: safeParseStrExpr(
        json['title']?.toString(),
      )!,
      titleClickAction: safeParseObj(
        DivAction.fromJson(json['title_click_action']),
      ),
    );
  }
}

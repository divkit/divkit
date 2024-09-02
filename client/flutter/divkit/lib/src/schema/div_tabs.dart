// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_accessibility.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_base.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/schema/div_corners_radius.dart';
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_fixed_size.dart';
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

class DivTabs extends Preloadable with EquatableMixin implements DivBase {
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
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.id,
    required this.items,
    this.layoutProvider,
    this.margins = const DivEdgeInsets(),
    this.paddings = const DivEdgeInsets(),
    this.restrictParentScroll = const ValueExpression(false),
    this.reuseId,
    this.rowSpan,
    this.selectedActions,
    this.selectedTab = const ValueExpression(0),
    this.separatorColor = const ValueExpression(Color(0x14000000)),
    this.separatorPaddings = const DivEdgeInsets(
      bottom: ValueExpression(
        0,
      ),
      left: ValueExpression(
        12,
      ),
      right: ValueExpression(
        12,
      ),
      top: ValueExpression(
        0,
      ),
    ),
    this.switchTabsByContentSwipeEnabled = const ValueExpression(true),
    this.tabTitleDelimiter,
    this.tabTitleStyle = const DivTabsTabTitleStyle(),
    this.titlePaddings = const DivEdgeInsets(
      bottom: ValueExpression(
        8,
      ),
      left: ValueExpression(
        12,
      ),
      right: ValueExpression(
        12,
      ),
      top: ValueExpression(
        0,
      ),
    ),
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    this.variableTriggers,
    this.variables,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(
      DivMatchParentSize(),
    ),
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
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize(),)
  @override
  final DivSize height;

  @override
  final String? id;
  // at least 1 elements
  final List<DivTabsItem> items;

  @override
  final DivLayoutProvider? layoutProvider;

  @override
  final DivEdgeInsets margins;

  @override
  final DivEdgeInsets paddings;
  // default value: false
  final Expression<bool> restrictParentScroll;

  @override
  final Expression<String>? reuseId;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  @override
  final List<DivAction>? selectedActions;
  // constraint: number >= 0; default value: 0
  final Expression<int> selectedTab;
  // default value: const Color(0x14000000)
  final Expression<Color> separatorColor;
  // default value: const DivEdgeInsets(bottom: ValueExpression(0,), left: ValueExpression(12,), right: ValueExpression(12,), top: ValueExpression(0,),)
  final DivEdgeInsets separatorPaddings;
  // default value: true
  final Expression<bool> switchTabsByContentSwipeEnabled;

  final DivTabsTabTitleDelimiter? tabTitleDelimiter;

  final DivTabsTabTitleStyle tabTitleStyle;
  // default value: const DivEdgeInsets(bottom: ValueExpression(8,), left: ValueExpression(12,), right: ValueExpression(12,), top: ValueExpression(0,),)
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
        dynamicHeight,
        extensions,
        focus,
        hasSeparator,
        height,
        id,
        items,
        layoutProvider,
        margins,
        paddings,
        restrictParentScroll,
        reuseId,
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
        variableTriggers,
        variables,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  DivTabs copyWith({
    DivAccessibility? accessibility,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    List<DivDisappearAction>? Function()? disappearActions,
    Expression<bool>? dynamicHeight,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    Expression<bool>? hasSeparator,
    DivSize? height,
    String? Function()? id,
    List<DivTabsItem>? items,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    DivEdgeInsets? paddings,
    Expression<bool>? restrictParentScroll,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    List<DivAction>? Function()? selectedActions,
    Expression<int>? selectedTab,
    Expression<Color>? separatorColor,
    DivEdgeInsets? separatorPaddings,
    Expression<bool>? switchTabsByContentSwipeEnabled,
    DivTabsTabTitleDelimiter? Function()? tabTitleDelimiter,
    DivTabsTabTitleStyle? tabTitleStyle,
    DivEdgeInsets? titlePaddings,
    List<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    List<DivTransitionTrigger>? Function()? transitionTriggers,
    List<DivTrigger>? Function()? variableTriggers,
    List<DivVariable>? Function()? variables,
    Expression<DivVisibility>? visibility,
    DivVisibilityAction? Function()? visibilityAction,
    List<DivVisibilityAction>? Function()? visibilityActions,
    DivSize? width,
  }) =>
      DivTabs(
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
        dynamicHeight: dynamicHeight ?? this.dynamicHeight,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        hasSeparator: hasSeparator ?? this.hasSeparator,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        items: items ?? this.items,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        margins: margins ?? this.margins,
        paddings: paddings ?? this.paddings,
        restrictParentScroll: restrictParentScroll ?? this.restrictParentScroll,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        selectedTab: selectedTab ?? this.selectedTab,
        separatorColor: separatorColor ?? this.separatorColor,
        separatorPaddings: separatorPaddings ?? this.separatorPaddings,
        switchTabsByContentSwipeEnabled: switchTabsByContentSwipeEnabled ??
            this.switchTabsByContentSwipeEnabled,
        tabTitleDelimiter: tabTitleDelimiter != null
            ? tabTitleDelimiter.call()
            : this.tabTitleDelimiter,
        tabTitleStyle: tabTitleStyle ?? this.tabTitleStyle,
        titlePaddings: titlePaddings ?? this.titlePaddings,
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

  static DivTabs? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
        dynamicHeight: safeParseBoolExpr(
          json['dynamic_height'],
          fallback: false,
        )!,
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
        hasSeparator: safeParseBoolExpr(
          json['has_separator'],
          fallback: false,
        )!,
        height: safeParseObj(
          DivSize.fromJson(json['height']),
          fallback: const DivSize.divWrapContentSize(
            DivWrapContentSize(),
          ),
        )!,
        id: safeParseStr(
          json['id']?.toString(),
        ),
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              DivTabsItem.fromJson(v),
            )!,
          ),
        )!,
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
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
            bottom: ValueExpression(
              0,
            ),
            left: ValueExpression(
              12,
            ),
            right: ValueExpression(
              12,
            ),
            top: ValueExpression(
              0,
            ),
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
            bottom: ValueExpression(
              8,
            ),
            left: ValueExpression(
              12,
            ),
            right: ValueExpression(
              12,
            ),
            top: ValueExpression(
              0,
            ),
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

  static Future<DivTabs?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTabs(
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
        dynamicHeight: (await safeParseBoolExprAsync(
          json['dynamic_height'],
          fallback: false,
        ))!,
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
        hasSeparator: (await safeParseBoolExprAsync(
          json['has_separator'],
          fallback: false,
        ))!,
        height: (await safeParseObjAsync(
          DivSize.fromJson(json['height']),
          fallback: const DivSize.divWrapContentSize(
            DivWrapContentSize(),
          ),
        ))!,
        id: await safeParseStrAsync(
          json['id']?.toString(),
        ),
        items: (await safeParseObjAsync(
          await safeListMapAsync(
            json['items'],
            (v) => safeParseObj(
              DivTabsItem.fromJson(v),
            )!,
          ),
        ))!,
        layoutProvider: await safeParseObjAsync(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        margins: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        ))!,
        paddings: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        ))!,
        restrictParentScroll: (await safeParseBoolExprAsync(
          json['restrict_parent_scroll'],
          fallback: false,
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
        selectedTab: (await safeParseIntExprAsync(
          json['selected_tab'],
          fallback: 0,
        ))!,
        separatorColor: (await safeParseColorExprAsync(
          json['separator_color'],
          fallback: const Color(0x14000000),
        ))!,
        separatorPaddings: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['separator_paddings']),
          fallback: const DivEdgeInsets(
            bottom: ValueExpression(
              0,
            ),
            left: ValueExpression(
              12,
            ),
            right: ValueExpression(
              12,
            ),
            top: ValueExpression(
              0,
            ),
          ),
        ))!,
        switchTabsByContentSwipeEnabled: (await safeParseBoolExprAsync(
          json['switch_tabs_by_content_swipe_enabled'],
          fallback: true,
        ))!,
        tabTitleDelimiter: await safeParseObjAsync(
          DivTabsTabTitleDelimiter.fromJson(json['tab_title_delimiter']),
        ),
        tabTitleStyle: (await safeParseObjAsync(
          DivTabsTabTitleStyle.fromJson(json['tab_title_style']),
          fallback: const DivTabsTabTitleStyle(),
        ))!,
        titlePaddings: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['title_paddings']),
          fallback: const DivEdgeInsets(
            bottom: ValueExpression(
              8,
            ),
            left: ValueExpression(
              12,
            ),
            right: ValueExpression(
              12,
            ),
            top: ValueExpression(
              0,
            ),
          ),
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
      await dynamicHeight.preload(context);
      await safeFuturesWait(extensions, (v) => v.preload(context));
      await focus?.preload(context);
      await hasSeparator.preload(context);
      await height.preload(context);
      await safeFuturesWait(items, (v) => v.preload(context));
      await layoutProvider?.preload(context);
      await margins.preload(context);
      await paddings.preload(context);
      await restrictParentScroll.preload(context);
      await reuseId?.preload(context);
      await rowSpan?.preload(context);
      await safeFuturesWait(selectedActions, (v) => v.preload(context));
      await selectedTab.preload(context);
      await separatorColor.preload(context);
      await separatorPaddings.preload(context);
      await switchTabsByContentSwipeEnabled.preload(context);
      await tabTitleDelimiter?.preload(context);
      await tabTitleStyle.preload(context);
      await titlePaddings.preload(context);
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

class DivTabsTabTitleStyle extends Preloadable with EquatableMixin {
  const DivTabsTabTitleStyle({
    this.activeBackgroundColor = const ValueExpression(Color(0xFFFFDC60)),
    this.activeFontWeight,
    this.activeTextColor = const ValueExpression(Color(0xCC000000)),
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
    this.inactiveTextColor = const ValueExpression(Color(0x80000000)),
    this.itemSpacing = const ValueExpression(0),
    this.letterSpacing = const ValueExpression(0),
    this.lineHeight,
    this.paddings = const DivEdgeInsets(
      bottom: ValueExpression(
        6,
      ),
      left: ValueExpression(
        8,
      ),
      right: ValueExpression(
        8,
      ),
      top: ValueExpression(
        6,
      ),
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
  // default value: const DivEdgeInsets(bottom: ValueExpression(6,), left: ValueExpression(8,), right: ValueExpression(8,), top: ValueExpression(6,),)
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

  DivTabsTabTitleStyle copyWith({
    Expression<Color>? activeBackgroundColor,
    Expression<DivFontWeight>? Function()? activeFontWeight,
    Expression<Color>? activeTextColor,
    Expression<int>? animationDuration,
    Expression<DivTabsTabTitleStyleAnimationType>? animationType,
    Expression<int>? Function()? cornerRadius,
    DivCornersRadius? Function()? cornersRadius,
    Expression<String>? Function()? fontFamily,
    Expression<int>? fontSize,
    Expression<DivSizeUnit>? fontSizeUnit,
    Expression<DivFontWeight>? fontWeight,
    Expression<Color>? Function()? inactiveBackgroundColor,
    Expression<DivFontWeight>? Function()? inactiveFontWeight,
    Expression<Color>? inactiveTextColor,
    Expression<int>? itemSpacing,
    Expression<double>? letterSpacing,
    Expression<int>? Function()? lineHeight,
    DivEdgeInsets? paddings,
  }) =>
      DivTabsTabTitleStyle(
        activeBackgroundColor:
            activeBackgroundColor ?? this.activeBackgroundColor,
        activeFontWeight: activeFontWeight != null
            ? activeFontWeight.call()
            : this.activeFontWeight,
        activeTextColor: activeTextColor ?? this.activeTextColor,
        animationDuration: animationDuration ?? this.animationDuration,
        animationType: animationType ?? this.animationType,
        cornerRadius:
            cornerRadius != null ? cornerRadius.call() : this.cornerRadius,
        cornersRadius:
            cornersRadius != null ? cornersRadius.call() : this.cornersRadius,
        fontFamily: fontFamily != null ? fontFamily.call() : this.fontFamily,
        fontSize: fontSize ?? this.fontSize,
        fontSizeUnit: fontSizeUnit ?? this.fontSizeUnit,
        fontWeight: fontWeight ?? this.fontWeight,
        inactiveBackgroundColor: inactiveBackgroundColor != null
            ? inactiveBackgroundColor.call()
            : this.inactiveBackgroundColor,
        inactiveFontWeight: inactiveFontWeight != null
            ? inactiveFontWeight.call()
            : this.inactiveFontWeight,
        inactiveTextColor: inactiveTextColor ?? this.inactiveTextColor,
        itemSpacing: itemSpacing ?? this.itemSpacing,
        letterSpacing: letterSpacing ?? this.letterSpacing,
        lineHeight: lineHeight != null ? lineHeight.call() : this.lineHeight,
        paddings: paddings ?? this.paddings,
      );

  static DivTabsTabTitleStyle? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
            bottom: ValueExpression(
              6,
            ),
            left: ValueExpression(
              8,
            ),
            right: ValueExpression(
              8,
            ),
            top: ValueExpression(
              6,
            ),
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivTabsTabTitleStyle?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTabsTabTitleStyle(
        activeBackgroundColor: (await safeParseColorExprAsync(
          json['active_background_color'],
          fallback: const Color(0xFFFFDC60),
        ))!,
        activeFontWeight: await safeParseStrEnumExprAsync(
          json['active_font_weight'],
          parse: DivFontWeight.fromJson,
        ),
        activeTextColor: (await safeParseColorExprAsync(
          json['active_text_color'],
          fallback: const Color(0xCC000000),
        ))!,
        animationDuration: (await safeParseIntExprAsync(
          json['animation_duration'],
          fallback: 300,
        ))!,
        animationType: (await safeParseStrEnumExprAsync(
          json['animation_type'],
          parse: DivTabsTabTitleStyleAnimationType.fromJson,
          fallback: DivTabsTabTitleStyleAnimationType.slide,
        ))!,
        cornerRadius: await safeParseIntExprAsync(
          json['corner_radius'],
        ),
        cornersRadius: await safeParseObjAsync(
          DivCornersRadius.fromJson(json['corners_radius']),
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
        inactiveBackgroundColor: await safeParseColorExprAsync(
          json['inactive_background_color'],
        ),
        inactiveFontWeight: await safeParseStrEnumExprAsync(
          json['inactive_font_weight'],
          parse: DivFontWeight.fromJson,
        ),
        inactiveTextColor: (await safeParseColorExprAsync(
          json['inactive_text_color'],
          fallback: const Color(0x80000000),
        ))!,
        itemSpacing: (await safeParseIntExprAsync(
          json['item_spacing'],
          fallback: 0,
        ))!,
        letterSpacing: (await safeParseDoubleExprAsync(
          json['letter_spacing'],
          fallback: 0,
        ))!,
        lineHeight: await safeParseIntExprAsync(
          json['line_height'],
        ),
        paddings: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(
            bottom: ValueExpression(
              6,
            ),
            left: ValueExpression(
              8,
            ),
            right: ValueExpression(
              8,
            ),
            top: ValueExpression(
              6,
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
      await activeBackgroundColor.preload(context);
      await activeFontWeight?.preload(context);
      await activeTextColor.preload(context);
      await animationDuration.preload(context);
      await animationType.preload(context);
      await cornerRadius?.preload(context);
      await cornersRadius?.preload(context);
      await fontFamily?.preload(context);
      await fontSize.preload(context);
      await fontSizeUnit.preload(context);
      await fontWeight.preload(context);
      await inactiveBackgroundColor?.preload(context);
      await inactiveFontWeight?.preload(context);
      await inactiveTextColor.preload(context);
      await itemSpacing.preload(context);
      await letterSpacing.preload(context);
      await lineHeight?.preload(context);
      await paddings.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivTabsTabTitleStyleAnimationType implements Preloadable {
  slide('slide'),
  fade('fade'),
  none('none');

  final String value;

  const DivTabsTabTitleStyleAnimationType(this.value);
  bool get isSlide => this == slide;

  bool get isFade => this == fade;

  bool get isNone => this == none;

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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivTabsTabTitleStyleAnimationType? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'slide':
          return DivTabsTabTitleStyleAnimationType.slide;
        case 'fade':
          return DivTabsTabTitleStyleAnimationType.fade;
        case 'none':
          return DivTabsTabTitleStyleAnimationType.none;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivTabsTabTitleStyleAnimationType?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'slide':
          return DivTabsTabTitleStyleAnimationType.slide;
        case 'fade':
          return DivTabsTabTitleStyleAnimationType.fade;
        case 'none':
          return DivTabsTabTitleStyleAnimationType.none;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

class DivTabsTabTitleDelimiter extends Preloadable with EquatableMixin {
  const DivTabsTabTitleDelimiter({
    this.height = const DivFixedSize(
      value: ValueExpression(
        12,
      ),
    ),
    required this.imageUrl,
    this.width = const DivFixedSize(
      value: ValueExpression(
        12,
      ),
    ),
  });

  // default value: const DivFixedSize(value: ValueExpression(12,),)
  final DivFixedSize height;

  final Expression<Uri> imageUrl;
  // default value: const DivFixedSize(value: ValueExpression(12,),)
  final DivFixedSize width;

  @override
  List<Object?> get props => [
        height,
        imageUrl,
        width,
      ];

  DivTabsTabTitleDelimiter copyWith({
    DivFixedSize? height,
    Expression<Uri>? imageUrl,
    DivFixedSize? width,
  }) =>
      DivTabsTabTitleDelimiter(
        height: height ?? this.height,
        imageUrl: imageUrl ?? this.imageUrl,
        width: width ?? this.width,
      );

  static DivTabsTabTitleDelimiter? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTabsTabTitleDelimiter(
        height: safeParseObj(
          DivFixedSize.fromJson(json['height']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              12,
            ),
          ),
        )!,
        imageUrl: safeParseUriExpr(json['image_url'])!,
        width: safeParseObj(
          DivFixedSize.fromJson(json['width']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              12,
            ),
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivTabsTabTitleDelimiter?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTabsTabTitleDelimiter(
        height: (await safeParseObjAsync(
          DivFixedSize.fromJson(json['height']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              12,
            ),
          ),
        ))!,
        imageUrl: (await safeParseUriExprAsync(json['image_url']))!,
        width: (await safeParseObjAsync(
          DivFixedSize.fromJson(json['width']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              12,
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
      await height.preload(context);
      await imageUrl.preload(context);
      await width.preload(context);
    } catch (e) {
      return;
    }
  }
}

class DivTabsItem extends Preloadable with EquatableMixin {
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

  DivTabsItem copyWith({
    Div? div,
    Expression<String>? title,
    DivAction? Function()? titleClickAction,
  }) =>
      DivTabsItem(
        div: div ?? this.div,
        title: title ?? this.title,
        titleClickAction: titleClickAction != null
            ? titleClickAction.call()
            : this.titleClickAction,
      );

  static DivTabsItem? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e) {
      return null;
    }
  }

  static Future<DivTabsItem?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTabsItem(
        div: (await safeParseObjAsync(
          Div.fromJson(json['div']),
        ))!,
        title: (await safeParseStrExprAsync(
          json['title']?.toString(),
        ))!,
        titleClickAction: await safeParseObjAsync(
          DivAction.fromJson(json['title_click_action']),
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
      await div.preload(context);
      await title.preload(context);
      await titleClickAction?.preload(context);
    } catch (e) {
      return;
    }
  }
}

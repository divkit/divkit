// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
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
import 'package:divkit/src/schema/div_corners_radius.dart';
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_font_weight.dart';
import 'package:divkit/src/schema/div_function.dart';
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
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Tabs. Height of the first tab is determined by its contents, and height of the remaining [depends on the platform](https://divkit.tech/docs/en/concepts/location#tabs).
class DivTabs with EquatableMixin implements DivBase {
  const DivTabs({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.animators,
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.disappearActions,
    this.dynamicHeight = const ValueExpression(false),
    this.extensions,
    this.focus,
    this.functions,
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

  /// Declaration of animators that change variable values over time.
  @override
  final Arr<DivAnimator>? animators;

  /// Element background. It can contain multiple layers.
  @override
  final Arr<DivBackground>? background;

  /// Element stroke.
  @override
  final DivBorder border;

  /// Merges cells in a column of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;

  /// Actions when an element disappears from the screen.
  @override
  final Arr<DivDisappearAction>? disappearActions;

  /// Updating height when changing the active element. In the browser, the value is always `true`.
  // default value: false
  final Expression<bool> dynamicHeight;

  /// Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](https://divkit.tech/docs/en/concepts/extensions).
  @override
  final Arr<DivExtension>? extensions;

  /// Parameters when focusing on an element or losing focus.
  @override
  final DivFocus? focus;

  /// User functions.
  @override
  final Arr<DivFunction>? functions;

  /// A separating line between tabs and contents.
  // default value: false
  final Expression<bool> hasSeparator;

  /// Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](https://divkit.tech/docs/en/concepts/layout).
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize(),)
  @override
  final DivSize height;

  /// Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
  @override
  final String? id;

  /// Tabs. Transition between tabs can be implemented using:
  /// • `div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;
  /// • `div-action://set_next_item?id=[&overflow={clamp\|ring}]` — go to the next tab inside an element, with the specified `id`;
  /// • `div-action://set_previous_item?id=[&overflow={clamp\|ring}]` — go to the previous tab inside an element, with the specified `id`.</p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:
  /// • `clamp` — transition will stop at the border element;
  /// • `ring` — go to the beginning or end, depending on the current element.</p><p>By default, `clamp`.
  // at least 1 elements
  final Arr<DivTabsItem> items;

  /// Provides data on the actual size of the element.
  @override
  final DivLayoutProvider? layoutProvider;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Internal margins from the element stroke.
  @override
  final DivEdgeInsets paddings;

  /// If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
  // default value: false
  final Expression<bool> restrictParentScroll;

  /// ID for the div object structure. Used to optimize block reuse. See [block reuse](https://divkit.tech/docs/en/concepts/reuse/reuse.md).
  @override
  final Expression<String>? reuseId;

  /// Merges cells in a string of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  /// List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
  @override
  final Arr<DivAction>? selectedActions;

  /// Ordinal number of the tab that will be opened by default.
  // constraint: number >= 0; default value: 0
  final Expression<int> selectedTab;

  /// Separator color.
  // default value: const Color(0x14000000)
  final Expression<Color> separatorColor;

  /// Indents from the separating line. Not used if `has_separator = false`.
  // default value: const DivEdgeInsets(bottom: ValueExpression(0,), left: ValueExpression(12,), right: ValueExpression(12,), top: ValueExpression(0,),)
  final DivEdgeInsets separatorPaddings;

  /// Switching tabs by scrolling through the contents.
  // default value: true
  final Expression<bool> switchTabsByContentSwipeEnabled;

  /// Design style of separators between tab titles.
  final DivTabsTabTitleDelimiter? tabTitleDelimiter;

  /// Design style of tab titles.
  final DivTabsTabTitleStyle tabTitleStyle;

  /// Indents in the tab name.
  // default value: const DivEdgeInsets(bottom: ValueExpression(8,), left: ValueExpression(12,), right: ValueExpression(12,), top: ValueExpression(0,),)
  final DivEdgeInsets titlePaddings;

  /// Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
  @override
  final Arr<DivTooltip>? tooltips;

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
  final Arr<DivTransitionTrigger>? transitionTriggers;

  /// Triggers for changing variables within an element.
  @override
  final Arr<DivTrigger>? variableTriggers;

  /// Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
  @override
  final Arr<DivVariable>? variables;

  /// Element visibility.
  // default value: DivVisibility.visible
  @override
  final Expression<DivVisibility> visibility;

  /// Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
  @override
  final DivVisibilityAction? visibilityAction;

  /// Actions when an element appears on the screen.
  @override
  final Arr<DivVisibilityAction>? visibilityActions;

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
        background,
        border,
        columnSpan,
        disappearActions,
        dynamicHeight,
        extensions,
        focus,
        functions,
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
    Arr<DivAnimator>? Function()? animators,
    Arr<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    Arr<DivDisappearAction>? Function()? disappearActions,
    Expression<bool>? dynamicHeight,
    Arr<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    Arr<DivFunction>? Function()? functions,
    Expression<bool>? hasSeparator,
    DivSize? height,
    String? Function()? id,
    Arr<DivTabsItem>? items,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    DivEdgeInsets? paddings,
    Expression<bool>? restrictParentScroll,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Arr<DivAction>? Function()? selectedActions,
    Expression<int>? selectedTab,
    Expression<Color>? separatorColor,
    DivEdgeInsets? separatorPaddings,
    Expression<bool>? switchTabsByContentSwipeEnabled,
    DivTabsTabTitleDelimiter? Function()? tabTitleDelimiter,
    DivTabsTabTitleStyle? tabTitleStyle,
    DivEdgeInsets? titlePaddings,
    Arr<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    Arr<DivTransitionTrigger>? Function()? transitionTriggers,
    Arr<DivTrigger>? Function()? variableTriggers,
    Arr<DivVariable>? Function()? variables,
    Expression<DivVisibility>? visibility,
    DivVisibilityAction? Function()? visibilityAction,
    Arr<DivVisibilityAction>? Function()? visibilityActions,
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
        animators: animators != null ? animators.call() : this.animators,
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        dynamicHeight: dynamicHeight ?? this.dynamicHeight,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        functions: functions != null ? functions.call() : this.functions,
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
        accessibility: reqProp<DivAccessibility>(
          safeParseObject(
            json['accessibility'],
            parse: DivAccessibility.fromJson,
            fallback: const DivAccessibility(),
          ),
          name: 'accessibility',
        ),
        alignmentHorizontal: safeParseStrEnumExpr(
          json['alignment_horizontal'],
          parse: DivAlignmentHorizontal.fromJson,
        ),
        alignmentVertical: safeParseStrEnumExpr(
          json['alignment_vertical'],
          parse: DivAlignmentVertical.fromJson,
        ),
        alpha: reqVProp<double>(
          safeParseDoubleExpr(
            json['alpha'],
            fallback: 1.0,
          ),
          name: 'alpha',
        ),
        animators: safeParseObjects(
          json['animators'],
          (v) => reqProp<DivAnimator>(
            safeParseObject(
              v,
              parse: DivAnimator.fromJson,
            ),
          ),
        ),
        background: safeParseObjects(
          json['background'],
          (v) => reqProp<DivBackground>(
            safeParseObject(
              v,
              parse: DivBackground.fromJson,
            ),
          ),
        ),
        border: reqProp<DivBorder>(
          safeParseObject(
            json['border'],
            parse: DivBorder.fromJson,
            fallback: const DivBorder(),
          ),
          name: 'border',
        ),
        columnSpan: safeParseIntExpr(
          json['column_span'],
        ),
        disappearActions: safeParseObjects(
          json['disappear_actions'],
          (v) => reqProp<DivDisappearAction>(
            safeParseObject(
              v,
              parse: DivDisappearAction.fromJson,
            ),
          ),
        ),
        dynamicHeight: reqVProp<bool>(
          safeParseBoolExpr(
            json['dynamic_height'],
            fallback: false,
          ),
          name: 'dynamic_height',
        ),
        extensions: safeParseObjects(
          json['extensions'],
          (v) => reqProp<DivExtension>(
            safeParseObject(
              v,
              parse: DivExtension.fromJson,
            ),
          ),
        ),
        focus: safeParseObject(
          json['focus'],
          parse: DivFocus.fromJson,
        ),
        functions: safeParseObjects(
          json['functions'],
          (v) => reqProp<DivFunction>(
            safeParseObject(
              v,
              parse: DivFunction.fromJson,
            ),
          ),
        ),
        hasSeparator: reqVProp<bool>(
          safeParseBoolExpr(
            json['has_separator'],
            fallback: false,
          ),
          name: 'has_separator',
        ),
        height: reqProp<DivSize>(
          safeParseObject(
            json['height'],
            parse: DivSize.fromJson,
            fallback: const DivSize.divWrapContentSize(
              DivWrapContentSize(),
            ),
          ),
          name: 'height',
        ),
        id: safeParseStr(
          json['id'],
        ),
        items: reqProp<Arr<DivTabsItem>>(
          safeParseObjects(
            json['items'],
            (v) => reqProp<DivTabsItem>(
              safeParseObject(
                v,
                parse: DivTabsItem.fromJson,
              ),
            ),
          ),
          name: 'items',
        ),
        layoutProvider: safeParseObject(
          json['layout_provider'],
          parse: DivLayoutProvider.fromJson,
        ),
        margins: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['margins'],
            parse: DivEdgeInsets.fromJson,
            fallback: const DivEdgeInsets(),
          ),
          name: 'margins',
        ),
        paddings: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['paddings'],
            parse: DivEdgeInsets.fromJson,
            fallback: const DivEdgeInsets(),
          ),
          name: 'paddings',
        ),
        restrictParentScroll: reqVProp<bool>(
          safeParseBoolExpr(
            json['restrict_parent_scroll'],
            fallback: false,
          ),
          name: 'restrict_parent_scroll',
        ),
        reuseId: safeParseStrExpr(
          json['reuse_id'],
        ),
        rowSpan: safeParseIntExpr(
          json['row_span'],
        ),
        selectedActions: safeParseObjects(
          json['selected_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        selectedTab: reqVProp<int>(
          safeParseIntExpr(
            json['selected_tab'],
            fallback: 0,
          ),
          name: 'selected_tab',
        ),
        separatorColor: reqVProp<Color>(
          safeParseColorExpr(
            json['separator_color'],
            fallback: const Color(0x14000000),
          ),
          name: 'separator_color',
        ),
        separatorPaddings: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['separator_paddings'],
            parse: DivEdgeInsets.fromJson,
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
          ),
          name: 'separator_paddings',
        ),
        switchTabsByContentSwipeEnabled: reqVProp<bool>(
          safeParseBoolExpr(
            json['switch_tabs_by_content_swipe_enabled'],
            fallback: true,
          ),
          name: 'switch_tabs_by_content_swipe_enabled',
        ),
        tabTitleDelimiter: safeParseObject(
          json['tab_title_delimiter'],
          parse: DivTabsTabTitleDelimiter.fromJson,
        ),
        tabTitleStyle: reqProp<DivTabsTabTitleStyle>(
          safeParseObject(
            json['tab_title_style'],
            parse: DivTabsTabTitleStyle.fromJson,
            fallback: const DivTabsTabTitleStyle(),
          ),
          name: 'tab_title_style',
        ),
        titlePaddings: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['title_paddings'],
            parse: DivEdgeInsets.fromJson,
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
          ),
          name: 'title_paddings',
        ),
        tooltips: safeParseObjects(
          json['tooltips'],
          (v) => reqProp<DivTooltip>(
            safeParseObject(
              v,
              parse: DivTooltip.fromJson,
            ),
          ),
        ),
        transform: reqProp<DivTransform>(
          safeParseObject(
            json['transform'],
            parse: DivTransform.fromJson,
            fallback: const DivTransform(),
          ),
          name: 'transform',
        ),
        transitionChange: safeParseObject(
          json['transition_change'],
          parse: DivChangeTransition.fromJson,
        ),
        transitionIn: safeParseObject(
          json['transition_in'],
          parse: DivAppearanceTransition.fromJson,
        ),
        transitionOut: safeParseObject(
          json['transition_out'],
          parse: DivAppearanceTransition.fromJson,
        ),
        transitionTriggers: safeParseObjects(
          json['transition_triggers'],
          (v) => reqProp<DivTransitionTrigger>(
            safeParseStrEnum(
              v,
              parse: DivTransitionTrigger.fromJson,
            ),
          ),
        ),
        variableTriggers: safeParseObjects(
          json['variable_triggers'],
          (v) => reqProp<DivTrigger>(
            safeParseObject(
              v,
              parse: DivTrigger.fromJson,
            ),
          ),
        ),
        variables: safeParseObjects(
          json['variables'],
          (v) => reqProp<DivVariable>(
            safeParseObject(
              v,
              parse: DivVariable.fromJson,
            ),
          ),
        ),
        visibility: reqVProp<DivVisibility>(
          safeParseStrEnumExpr(
            json['visibility'],
            parse: DivVisibility.fromJson,
            fallback: DivVisibility.visible,
          ),
          name: 'visibility',
        ),
        visibilityAction: safeParseObject(
          json['visibility_action'],
          parse: DivVisibilityAction.fromJson,
        ),
        visibilityActions: safeParseObjects(
          json['visibility_actions'],
          (v) => reqProp<DivVisibilityAction>(
            safeParseObject(
              v,
              parse: DivVisibilityAction.fromJson,
            ),
          ),
        ),
        width: reqProp<DivSize>(
          safeParseObject(
            json['width'],
            parse: DivSize.fromJson,
            fallback: const DivSize.divMatchParentSize(
              DivMatchParentSize(),
            ),
          ),
          name: 'width',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

/// Design style of tab titles.
class DivTabsTabTitleStyle with EquatableMixin {
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

  /// Background color of the active tab title.
  // default value: const Color(0xFFFFDC60)
  final Expression<Color> activeBackgroundColor;

  /// Active tab title style.
  final Expression<DivFontWeight>? activeFontWeight;

  /// Color of the active tab title text.
  // default value: const Color(0xCC000000)
  final Expression<Color> activeTextColor;

  /// Duration of active title change animation.
  // constraint: number >= 0; default value: 300
  final Expression<int> animationDuration;

  /// Active title change animation.
  // default value: DivTabsTabTitleStyleAnimationType.slide
  final Expression<DivTabsTabTitleStyleAnimationType> animationType;

  /// Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half of the smallest size). Not used if the `corners_radius` parameter is set.
  // constraint: number >= 0
  final Expression<int>? cornerRadius;

  /// Rounding radii of corners of multiple titles. Empty values are replaced by `corner_radius`.
  final DivCornersRadius? cornersRadius;

  /// Font family:
  /// • `text` — a standard text font;
  /// • `display` — a family of fonts with a large font size.
  final Expression<String>? fontFamily;

  /// Title font size.
  // constraint: number >= 0; default value: 12
  final Expression<int> fontSize;

  /// Units of title font size measurement.
  // default value: DivSizeUnit.sp
  final Expression<DivSizeUnit> fontSizeUnit;

  /// Style. Use `active_font_weight` and `inactive_font_weight` instead.
  // default value: DivFontWeight.regular
  final Expression<DivFontWeight> fontWeight;

  /// Background color of the inactive tab title.
  final Expression<Color>? inactiveBackgroundColor;

  /// Inactive tab title style.
  final Expression<DivFontWeight>? inactiveFontWeight;

  /// Color of the inactive tab title text.
  // default value: const Color(0x80000000)
  final Expression<Color> inactiveTextColor;

  /// Spacing between neighbouring tab titles.
  // constraint: number >= 0; default value: 0
  final Expression<int> itemSpacing;

  /// Spacing between title characters.
  // default value: 0
  final Expression<double> letterSpacing;

  /// Line spacing of the text.
  // constraint: number >= 0
  final Expression<int>? lineHeight;

  /// Indents around the tab title.
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
        activeBackgroundColor: reqVProp<Color>(
          safeParseColorExpr(
            json['active_background_color'],
            fallback: const Color(0xFFFFDC60),
          ),
          name: 'active_background_color',
        ),
        activeFontWeight: safeParseStrEnumExpr(
          json['active_font_weight'],
          parse: DivFontWeight.fromJson,
        ),
        activeTextColor: reqVProp<Color>(
          safeParseColorExpr(
            json['active_text_color'],
            fallback: const Color(0xCC000000),
          ),
          name: 'active_text_color',
        ),
        animationDuration: reqVProp<int>(
          safeParseIntExpr(
            json['animation_duration'],
            fallback: 300,
          ),
          name: 'animation_duration',
        ),
        animationType: reqVProp<DivTabsTabTitleStyleAnimationType>(
          safeParseStrEnumExpr(
            json['animation_type'],
            parse: DivTabsTabTitleStyleAnimationType.fromJson,
            fallback: DivTabsTabTitleStyleAnimationType.slide,
          ),
          name: 'animation_type',
        ),
        cornerRadius: safeParseIntExpr(
          json['corner_radius'],
        ),
        cornersRadius: safeParseObject(
          json['corners_radius'],
          parse: DivCornersRadius.fromJson,
        ),
        fontFamily: safeParseStrExpr(
          json['font_family'],
        ),
        fontSize: reqVProp<int>(
          safeParseIntExpr(
            json['font_size'],
            fallback: 12,
          ),
          name: 'font_size',
        ),
        fontSizeUnit: reqVProp<DivSizeUnit>(
          safeParseStrEnumExpr(
            json['font_size_unit'],
            parse: DivSizeUnit.fromJson,
            fallback: DivSizeUnit.sp,
          ),
          name: 'font_size_unit',
        ),
        fontWeight: reqVProp<DivFontWeight>(
          safeParseStrEnumExpr(
            json['font_weight'],
            parse: DivFontWeight.fromJson,
            fallback: DivFontWeight.regular,
          ),
          name: 'font_weight',
        ),
        inactiveBackgroundColor: safeParseColorExpr(
          json['inactive_background_color'],
        ),
        inactiveFontWeight: safeParseStrEnumExpr(
          json['inactive_font_weight'],
          parse: DivFontWeight.fromJson,
        ),
        inactiveTextColor: reqVProp<Color>(
          safeParseColorExpr(
            json['inactive_text_color'],
            fallback: const Color(0x80000000),
          ),
          name: 'inactive_text_color',
        ),
        itemSpacing: reqVProp<int>(
          safeParseIntExpr(
            json['item_spacing'],
            fallback: 0,
          ),
          name: 'item_spacing',
        ),
        letterSpacing: reqVProp<double>(
          safeParseDoubleExpr(
            json['letter_spacing'],
            fallback: 0,
          ),
          name: 'letter_spacing',
        ),
        lineHeight: safeParseIntExpr(
          json['line_height'],
        ),
        paddings: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['paddings'],
            parse: DivEdgeInsets.fromJson,
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
          ),
          name: 'paddings',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

enum DivTabsTabTitleStyleAnimationType {
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
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivTabsTabTitleStyleAnimationType: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }
}

/// Design style of separators between tab titles.
class DivTabsTabTitleDelimiter with EquatableMixin {
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

  /// Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](https://divkit.tech/docs/en/concepts/layout).
  // default value: const DivFixedSize(value: ValueExpression(12,),)
  final DivFixedSize height;

  /// Direct URL to an image.
  final Expression<Uri> imageUrl;

  /// Element width.
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
        height: reqProp<DivFixedSize>(
          safeParseObject(
            json['height'],
            parse: DivFixedSize.fromJson,
            fallback: const DivFixedSize(
              value: ValueExpression(
                12,
              ),
            ),
          ),
          name: 'height',
        ),
        imageUrl: reqVProp<Uri>(
          safeParseUriExpr(
            json['image_url'],
          ),
          name: 'image_url',
        ),
        width: reqProp<DivFixedSize>(
          safeParseObject(
            json['width'],
            parse: DivFixedSize.fromJson,
            fallback: const DivFixedSize(
              value: ValueExpression(
                12,
              ),
            ),
          ),
          name: 'width',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

/// Tab.
class DivTabsItem with EquatableMixin {
  const DivTabsItem({
    required this.div,
    required this.title,
    this.titleClickAction,
  });

  /// Tab contents.
  final Div div;

  /// Tab title.
  final Expression<String> title;

  /// Action when clicking on the active tab title.
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
        div: reqProp<Div>(
          safeParseObject(
            json['div'],
            parse: Div.fromJson,
          ),
          name: 'div',
        ),
        title: reqVProp<String>(
          safeParseStrExpr(
            json['title'],
          ),
          name: 'title',
        ),
        titleClickAction: safeParseObject(
          json['title_click_action'],
          parse: DivAction.fromJson,
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

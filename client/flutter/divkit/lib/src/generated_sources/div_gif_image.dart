// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_accessibility.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_alignment_horizontal.dart';
import 'package:divkit/src/generated_sources/div_alignment_vertical.dart';
import 'package:divkit/src/generated_sources/div_animation.dart';
import 'package:divkit/src/generated_sources/div_appearance_transition.dart';
import 'package:divkit/src/generated_sources/div_aspect.dart';
import 'package:divkit/src/generated_sources/div_background.dart';
import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/generated_sources/div_border.dart';
import 'package:divkit/src/generated_sources/div_change_transition.dart';
import 'package:divkit/src/generated_sources/div_disappear_action.dart';
import 'package:divkit/src/generated_sources/div_edge_insets.dart';
import 'package:divkit/src/generated_sources/div_extension.dart';
import 'package:divkit/src/generated_sources/div_focus.dart';
import 'package:divkit/src/generated_sources/div_image_scale.dart';
import 'package:divkit/src/generated_sources/div_match_parent_size.dart';
import 'package:divkit/src/generated_sources/div_size.dart';
import 'package:divkit/src/generated_sources/div_tooltip.dart';
import 'package:divkit/src/generated_sources/div_transform.dart';
import 'package:divkit/src/generated_sources/div_transition_trigger.dart';
import 'package:divkit/src/generated_sources/div_variable.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/generated_sources/div_visibility_action.dart';
import 'package:divkit/src/generated_sources/div_wrap_content_size.dart';

class DivGifImage with EquatableMixin implements DivBase {
  const DivGifImage({
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
    this.aspect,
    this.background,
    this.border = const DivBorder(),
    this.columnSpan,
    this.contentAlignmentHorizontal =
        const ValueExpression(DivAlignmentHorizontal.center),
    this.contentAlignmentVertical =
        const ValueExpression(DivAlignmentVertical.center),
    this.disappearActions,
    this.doubletapActions,
    this.extensions,
    this.focus,
    required this.gifUrl,
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
    this.id,
    this.longtapActions,
    this.margins = const DivEdgeInsets(),
    this.paddings = const DivEdgeInsets(),
    this.placeholderColor = const ValueExpression(Color(0x14000000)),
    this.preloadRequired = const ValueExpression(false),
    this.preview,
    this.rowSpan,
    this.scale = const ValueExpression(DivImageScale.fill),
    this.selectedActions,
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

  static const type = "gif";

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

  final DivAspect? aspect;

  @override
  final List<DivBackground>? background;

  @override
  final DivBorder border;
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;
  // default value: DivAlignmentHorizontal.center
  final Expression<DivAlignmentHorizontal> contentAlignmentHorizontal;
  // default value: DivAlignmentVertical.center
  final Expression<DivAlignmentVertical> contentAlignmentVertical;

  @override
  final List<DivDisappearAction>? disappearActions;

  final List<DivAction>? doubletapActions;

  @override
  final List<DivExtension>? extensions;

  @override
  final DivFocus? focus;

  final Expression<Uri> gifUrl;
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize())
  @override
  final DivSize height;

  @override
  final String? id;

  final List<DivAction>? longtapActions;

  @override
  final DivEdgeInsets margins;

  @override
  final DivEdgeInsets paddings;
  // default value: const Color(0x14000000)
  final Expression<Color> placeholderColor;
  // default value: false
  final Expression<bool> preloadRequired;

  final Expression<String>? preview;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;
  // default value: DivImageScale.fill
  final Expression<DivImageScale> scale;

  @override
  final List<DivAction>? selectedActions;

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
        action,
        actionAnimation,
        actions,
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        aspect,
        background,
        border,
        columnSpan,
        contentAlignmentHorizontal,
        contentAlignmentVertical,
        disappearActions,
        doubletapActions,
        extensions,
        focus,
        gifUrl,
        height,
        id,
        longtapActions,
        margins,
        paddings,
        placeholderColor,
        preloadRequired,
        preview,
        rowSpan,
        scale,
        selectedActions,
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

  DivGifImage copyWith({
    DivAccessibility? accessibility,
    DivAction? Function()? action,
    DivAnimation? actionAnimation,
    List<DivAction>? Function()? actions,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    DivAspect? Function()? aspect,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    Expression<DivAlignmentHorizontal>? contentAlignmentHorizontal,
    Expression<DivAlignmentVertical>? contentAlignmentVertical,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivAction>? Function()? doubletapActions,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    Expression<Uri>? gifUrl,
    DivSize? height,
    String? Function()? id,
    List<DivAction>? Function()? longtapActions,
    DivEdgeInsets? margins,
    DivEdgeInsets? paddings,
    Expression<Color>? placeholderColor,
    Expression<bool>? preloadRequired,
    Expression<String>? Function()? preview,
    Expression<int>? Function()? rowSpan,
    Expression<DivImageScale>? scale,
    List<DivAction>? Function()? selectedActions,
    List<DivTooltip>? Function()? tooltips,
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
      DivGifImage(
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
        aspect: aspect != null ? aspect.call() : this.aspect,
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        contentAlignmentHorizontal:
            contentAlignmentHorizontal ?? this.contentAlignmentHorizontal,
        contentAlignmentVertical:
            contentAlignmentVertical ?? this.contentAlignmentVertical,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        doubletapActions: doubletapActions != null
            ? doubletapActions.call()
            : this.doubletapActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        gifUrl: gifUrl ?? this.gifUrl,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        longtapActions: longtapActions != null
            ? longtapActions.call()
            : this.longtapActions,
        margins: margins ?? this.margins,
        paddings: paddings ?? this.paddings,
        placeholderColor: placeholderColor ?? this.placeholderColor,
        preloadRequired: preloadRequired ?? this.preloadRequired,
        preview: preview != null ? preview.call() : this.preview,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        scale: scale ?? this.scale,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
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

  static DivGifImage? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivGifImage(
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
      aspect: safeParseObj(
        DivAspect.fromJson(json['aspect']),
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
      contentAlignmentHorizontal: safeParseStrEnumExpr(
        json['content_alignment_horizontal'],
        parse: DivAlignmentHorizontal.fromJson,
        fallback: DivAlignmentHorizontal.center,
      )!,
      contentAlignmentVertical: safeParseStrEnumExpr(
        json['content_alignment_vertical'],
        parse: DivAlignmentVertical.fromJson,
        fallback: DivAlignmentVertical.center,
      )!,
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
      gifUrl: safeParseUriExpr(json['gif_url'])!,
      height: safeParseObj(
        DivSize.fromJson(json['height']),
        fallback: const DivSize.divWrapContentSize(DivWrapContentSize()),
      )!,
      id: safeParseStr(
        json['id']?.toString(),
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
      paddings: safeParseObj(
        DivEdgeInsets.fromJson(json['paddings']),
        fallback: const DivEdgeInsets(),
      )!,
      placeholderColor: safeParseColorExpr(
        json['placeholder_color'],
        fallback: const Color(0x14000000),
      )!,
      preloadRequired: safeParseBoolExpr(
        json['preload_required'],
        fallback: false,
      )!,
      preview: safeParseStrExpr(
        json['preview']?.toString(),
      ),
      rowSpan: safeParseIntExpr(
        json['row_span'],
      ),
      scale: safeParseStrEnumExpr(
        json['scale'],
        parse: DivImageScale.fromJson,
        fallback: DivImageScale.fill,
      )!,
      selectedActions: safeParseObj(
        safeListMap(
            json['selected_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
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

// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_accessibility.dart';
import 'div_action.dart';
import 'div_alignment_horizontal.dart';
import 'div_alignment_vertical.dart';
import 'div_animation.dart';
import 'div_appearance_transition.dart';
import 'div_aspect.dart';
import 'div_background.dart';
import 'div_base.dart';
import 'div_border.dart';
import 'div_change_transition.dart';
import 'div_disappear_action.dart';
import 'div_edge_insets.dart';
import 'div_extension.dart';
import 'div_focus.dart';
import 'div_image_scale.dart';
import 'div_match_parent_size.dart';
import 'div_size.dart';
import 'div_tooltip.dart';
import 'div_transform.dart';
import 'div_transition_trigger.dart';
import 'div_variable.dart';
import 'div_visibility.dart';
import 'div_visibility_action.dart';
import 'div_wrap_content_size.dart';

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
    this.placeholderColor = const ValueExpression(const Color(0x14000000)),
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

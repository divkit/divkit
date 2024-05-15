// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_accessibility.dart';
import 'div_action.dart';
import 'div_alignment_horizontal.dart';
import 'div_alignment_vertical.dart';
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
import 'div_match_parent_size.dart';
import 'div_size.dart';
import 'div_tooltip.dart';
import 'div_transform.dart';
import 'div_transition_trigger.dart';
import 'div_video_scale.dart';
import 'div_video_source.dart';
import 'div_visibility.dart';
import 'div_visibility_action.dart';
import 'div_wrap_content_size.dart';

class DivVideo with EquatableMixin implements DivBase {
  const DivVideo({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.aspect,
    this.autostart = const ValueExpression(false),
    this.background,
    this.border = const DivBorder(),
    this.bufferingActions,
    this.columnSpan,
    this.disappearActions,
    this.elapsedTimeVariable,
    this.endActions,
    this.extensions,
    this.fatalActions,
    this.focus,
    this.height = const DivSize(DivWrapContentSize()),
    this.id,
    this.margins = const DivEdgeInsets(),
    this.muted = const ValueExpression(false),
    this.paddings = const DivEdgeInsets(),
    this.pauseActions,
    this.playerSettingsPayload,
    this.preloadRequired = const ValueExpression(false),
    this.preview,
    this.repeatable = const ValueExpression(false),
    this.resumeActions,
    this.rowSpan,
    this.scale = const ValueExpression(DivVideoScale.fit),
    this.selectedActions,
    this.tooltips,
    this.transform = const DivTransform(),
    this.transitionChange,
    this.transitionIn,
    this.transitionOut,
    this.transitionTriggers,
    required this.videoSources,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize(DivMatchParentSize()),
  });

  static const type = "video";

  @override
  final DivAccessibility accessibility;

  @override
  final Expression<DivAlignmentHorizontal>? alignmentHorizontal;

  @override
  final Expression<DivAlignmentVertical>? alignmentVertical;
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  @override
  final Expression<double> alpha;

  final DivAspect? aspect;
  // default value: false
  final Expression<bool> autostart;

  @override
  final List<DivBackground>? background;

  @override
  final DivBorder border;

  final List<DivAction>? bufferingActions;
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;

  @override
  final List<DivDisappearAction>? disappearActions;

  final String? elapsedTimeVariable;

  final List<DivAction>? endActions;

  @override
  final List<DivExtension>? extensions;

  final List<DivAction>? fatalActions;

  @override
  final DivFocus? focus;
  // default value: const DivSize(DivWrapContentSize())
  @override
  final DivSize height;

  @override
  final String? id;

  @override
  final DivEdgeInsets margins;
  // default value: false
  final Expression<bool> muted;

  @override
  final DivEdgeInsets paddings;

  final List<DivAction>? pauseActions;

  final Map<String, dynamic>? playerSettingsPayload;
  // default value: false
  final Expression<bool> preloadRequired;

  final Expression<String>? preview;
  // default value: false
  final Expression<bool> repeatable;

  final List<DivAction>? resumeActions;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;
  // default value: DivVideoScale.fit
  final Expression<DivVideoScale> scale;

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
  // at least 1 elements
  final List<DivVideoSource> videoSources;
  // default value: DivVisibility.visible
  @override
  final Expression<DivVisibility> visibility;

  @override
  final DivVisibilityAction? visibilityAction;

  @override
  final List<DivVisibilityAction>? visibilityActions;
  // default value: const DivSize(DivMatchParentSize())
  @override
  final DivSize width;

  @override
  List<Object?> get props => [
        accessibility,
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        aspect,
        autostart,
        background,
        border,
        bufferingActions,
        columnSpan,
        disappearActions,
        elapsedTimeVariable,
        endActions,
        extensions,
        fatalActions,
        focus,
        height,
        id,
        margins,
        muted,
        paddings,
        pauseActions,
        playerSettingsPayload,
        preloadRequired,
        preview,
        repeatable,
        resumeActions,
        rowSpan,
        scale,
        selectedActions,
        tooltips,
        transform,
        transitionChange,
        transitionIn,
        transitionOut,
        transitionTriggers,
        videoSources,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  static DivVideo? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivVideo(
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
      aspect: safeParseObj(
        DivAspect.fromJson(json['aspect']),
      ),
      autostart: safeParseBoolExpr(
        json['autostart'],
        fallback: false,
      )!,
      background: safeParseObj(
        (json['background'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivBackground.fromJson(v),
              )!,
            )
            .toList(),
      ),
      border: safeParseObj(
        DivBorder.fromJson(json['border']),
        fallback: const DivBorder(),
      )!,
      bufferingActions: safeParseObj(
        (json['buffering_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      columnSpan: safeParseIntExpr(
        json['column_span'],
      ),
      disappearActions: safeParseObj(
        (json['disappear_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivDisappearAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      elapsedTimeVariable: safeParseStr(
        json['elapsed_time_variable']?.toString(),
      ),
      endActions: safeParseObj(
        (json['end_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      extensions: safeParseObj(
        (json['extensions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivExtension.fromJson(v),
              )!,
            )
            .toList(),
      ),
      fatalActions: safeParseObj(
        (json['fatal_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      focus: safeParseObj(
        DivFocus.fromJson(json['focus']),
      ),
      height: safeParseObj(
        DivSize.fromJson(json['height']),
        fallback: const DivSize(DivWrapContentSize()),
      )!,
      id: safeParseStr(
        json['id']?.toString(),
      ),
      margins: safeParseObj(
        DivEdgeInsets.fromJson(json['margins']),
        fallback: const DivEdgeInsets(),
      )!,
      muted: safeParseBoolExpr(
        json['muted'],
        fallback: false,
      )!,
      paddings: safeParseObj(
        DivEdgeInsets.fromJson(json['paddings']),
        fallback: const DivEdgeInsets(),
      )!,
      pauseActions: safeParseObj(
        (json['pause_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      playerSettingsPayload: safeParseMap(
        json,
      ),
      preloadRequired: safeParseBoolExpr(
        json['preload_required'],
        fallback: false,
      )!,
      preview: safeParseStrExpr(
        json['preview']?.toString(),
      ),
      repeatable: safeParseBoolExpr(
        json['repeatable'],
        fallback: false,
      )!,
      resumeActions: safeParseObj(
        (json['resume_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      rowSpan: safeParseIntExpr(
        json['row_span'],
      ),
      scale: safeParseStrEnumExpr(
        json['scale'],
        parse: DivVideoScale.fromJson,
        fallback: DivVideoScale.fit,
      )!,
      selectedActions: safeParseObj(
        (json['selected_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      tooltips: safeParseObj(
        (json['tooltips'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivTooltip.fromJson(v),
              )!,
            )
            .toList(),
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
        (json['transition_triggers'] as List<dynamic>?)
            ?.map(
              (v) => safeParseStrEnum(
                v,
                parse: DivTransitionTrigger.fromJson,
              )!,
            )
            .toList(),
      ),
      videoSources: safeParseObj(
        (json['video_sources'] as List<dynamic>)
            .map(
              (v) => safeParseObj(
                DivVideoSource.fromJson(v),
              )!,
            )
            .toList(),
      )!,
      visibility: safeParseStrEnumExpr(
        json['visibility'],
        parse: DivVisibility.fromJson,
        fallback: DivVisibility.visible,
      )!,
      visibilityAction: safeParseObj(
        DivVisibilityAction.fromJson(json['visibility_action']),
      ),
      visibilityActions: safeParseObj(
        (json['visibility_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivVisibilityAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      width: safeParseObj(
        DivSize.fromJson(json['width']),
        fallback: const DivSize(DivMatchParentSize()),
      )!,
    );
  }
}

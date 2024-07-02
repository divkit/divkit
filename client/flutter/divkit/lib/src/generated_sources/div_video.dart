// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_accessibility.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_alignment_horizontal.dart';
import 'package:divkit/src/generated_sources/div_alignment_vertical.dart';
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
import 'package:divkit/src/generated_sources/div_match_parent_size.dart';
import 'package:divkit/src/generated_sources/div_size.dart';
import 'package:divkit/src/generated_sources/div_tooltip.dart';
import 'package:divkit/src/generated_sources/div_transform.dart';
import 'package:divkit/src/generated_sources/div_transition_trigger.dart';
import 'package:divkit/src/generated_sources/div_variable.dart';
import 'package:divkit/src/generated_sources/div_video_scale.dart';
import 'package:divkit/src/generated_sources/div_video_source.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/generated_sources/div_visibility_action.dart';
import 'package:divkit/src/generated_sources/div_wrap_content_size.dart';

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
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
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
    this.variables,
    required this.videoSources,
    this.visibility = const ValueExpression(DivVisibility.visible),
    this.visibilityAction,
    this.visibilityActions,
    this.width = const DivSize.divMatchParentSize(DivMatchParentSize()),
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
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize())
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

  @override
  final List<DivVariable>? variables;
  // at least 1 elements
  final List<DivVideoSource> videoSources;
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
        variables,
        videoSources,
        visibility,
        visibilityAction,
        visibilityActions,
        width,
      ];

  DivVideo copyWith({
    DivAccessibility? accessibility,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    DivAspect? Function()? aspect,
    Expression<bool>? autostart,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    List<DivAction>? Function()? bufferingActions,
    Expression<int>? Function()? columnSpan,
    List<DivDisappearAction>? Function()? disappearActions,
    String? Function()? elapsedTimeVariable,
    List<DivAction>? Function()? endActions,
    List<DivExtension>? Function()? extensions,
    List<DivAction>? Function()? fatalActions,
    DivFocus? Function()? focus,
    DivSize? height,
    String? Function()? id,
    DivEdgeInsets? margins,
    Expression<bool>? muted,
    DivEdgeInsets? paddings,
    List<DivAction>? Function()? pauseActions,
    Map<String, dynamic>? Function()? playerSettingsPayload,
    Expression<bool>? preloadRequired,
    Expression<String>? Function()? preview,
    Expression<bool>? repeatable,
    List<DivAction>? Function()? resumeActions,
    Expression<int>? Function()? rowSpan,
    Expression<DivVideoScale>? scale,
    List<DivAction>? Function()? selectedActions,
    List<DivTooltip>? Function()? tooltips,
    DivTransform? transform,
    DivChangeTransition? Function()? transitionChange,
    DivAppearanceTransition? Function()? transitionIn,
    DivAppearanceTransition? Function()? transitionOut,
    List<DivTransitionTrigger>? Function()? transitionTriggers,
    List<DivVariable>? Function()? variables,
    List<DivVideoSource>? videoSources,
    Expression<DivVisibility>? visibility,
    DivVisibilityAction? Function()? visibilityAction,
    List<DivVisibilityAction>? Function()? visibilityActions,
    DivSize? width,
  }) =>
      DivVideo(
        accessibility: accessibility ?? this.accessibility,
        alignmentHorizontal: alignmentHorizontal != null
            ? alignmentHorizontal.call()
            : this.alignmentHorizontal,
        alignmentVertical: alignmentVertical != null
            ? alignmentVertical.call()
            : this.alignmentVertical,
        alpha: alpha ?? this.alpha,
        aspect: aspect != null ? aspect.call() : this.aspect,
        autostart: autostart ?? this.autostart,
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        bufferingActions: bufferingActions != null
            ? bufferingActions.call()
            : this.bufferingActions,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        elapsedTimeVariable: elapsedTimeVariable != null
            ? elapsedTimeVariable.call()
            : this.elapsedTimeVariable,
        endActions: endActions != null ? endActions.call() : this.endActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        fatalActions:
            fatalActions != null ? fatalActions.call() : this.fatalActions,
        focus: focus != null ? focus.call() : this.focus,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        margins: margins ?? this.margins,
        muted: muted ?? this.muted,
        paddings: paddings ?? this.paddings,
        pauseActions:
            pauseActions != null ? pauseActions.call() : this.pauseActions,
        playerSettingsPayload: playerSettingsPayload != null
            ? playerSettingsPayload.call()
            : this.playerSettingsPayload,
        preloadRequired: preloadRequired ?? this.preloadRequired,
        preview: preview != null ? preview.call() : this.preview,
        repeatable: repeatable ?? this.repeatable,
        resumeActions:
            resumeActions != null ? resumeActions.call() : this.resumeActions,
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
        videoSources: videoSources ?? this.videoSources,
        visibility: visibility ?? this.visibility,
        visibilityAction: visibilityAction != null
            ? visibilityAction.call()
            : this.visibilityAction,
        visibilityActions: visibilityActions != null
            ? visibilityActions.call()
            : this.visibilityActions,
        width: width ?? this.width,
      );

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
      bufferingActions: safeParseObj(
        safeListMap(
            json['buffering_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
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
      elapsedTimeVariable: safeParseStr(
        json['elapsed_time_variable']?.toString(),
      ),
      endActions: safeParseObj(
        safeListMap(
            json['end_actions'],
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
      fatalActions: safeParseObj(
        safeListMap(
            json['fatal_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      focus: safeParseObj(
        DivFocus.fromJson(json['focus']),
      ),
      height: safeParseObj(
        DivSize.fromJson(json['height']),
        fallback: const DivSize.divWrapContentSize(DivWrapContentSize()),
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
        safeListMap(
            json['pause_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
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
        safeListMap(
            json['resume_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
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
      videoSources: safeParseObj(
        safeListMap(
            json['video_sources'],
            (v) => safeParseObj(
                  DivVideoSource.fromJson(v),
                )!),
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

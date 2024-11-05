// Generated code. Do not modify.

import 'package:divkit/src/schema/div_accessibility.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_animation.dart';
import 'package:divkit/src/schema/div_animator.dart';
import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/schema/div_aspect.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_base.dart';
import 'package:divkit/src/schema/div_blend_mode.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/schema/div_disappear_action.dart';
import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_extension.dart';
import 'package:divkit/src/schema/div_fade_transition.dart';
import 'package:divkit/src/schema/div_filter.dart';
import 'package:divkit/src/schema/div_focus.dart';
import 'package:divkit/src/schema/div_function.dart';
import 'package:divkit/src/schema/div_image_scale.dart';
import 'package:divkit/src/schema/div_layout_provider.dart';
import 'package:divkit/src/schema/div_match_parent_size.dart';
import 'package:divkit/src/schema/div_size.dart';
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

/// Image.
class DivImage extends Resolvable with EquatableMixin implements DivBase {
  const DivImage({
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
    this.appearanceAnimation,
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
    this.filters,
    this.focus,
    this.functions,
    this.height = const DivSize.divWrapContentSize(
      DivWrapContentSize(),
    ),
    this.highPriorityPreviewShow = const ValueExpression(false),
    this.id,
    required this.imageUrl,
    this.layoutProvider,
    this.longtapActions,
    this.margins = const DivEdgeInsets(),
    this.paddings = const DivEdgeInsets(),
    this.placeholderColor = const ValueExpression(Color(0x14000000)),
    this.preloadRequired = const ValueExpression(false),
    this.preview,
    this.reuseId,
    this.rowSpan,
    this.scale = const ValueExpression(DivImageScale.fill),
    this.selectedActions,
    this.tintColor,
    this.tintMode = const ValueExpression(DivBlendMode.sourceIn),
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

  static const type = "image";

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

  /// Transparency animation when loading an image.
  final DivFadeTransition? appearanceAnimation;

  /// Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
  final DivAspect? aspect;

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

  /// Horizontal image alignment.
  // default value: DivAlignmentHorizontal.center
  final Expression<DivAlignmentHorizontal> contentAlignmentHorizontal;

  /// Vertical image alignment.
  // default value: DivAlignmentVertical.center
  final Expression<DivAlignmentVertical> contentAlignmentVertical;

  /// Actions when an element disappears from the screen.
  @override
  final List<DivDisappearAction>? disappearActions;

  /// Action when double-clicking on an element.
  final List<DivAction>? doubletapActions;

  /// Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](https://divkit.tech/docs/en/concepts/extensions).
  @override
  final List<DivExtension>? extensions;

  /// Image filters.
  final List<DivFilter>? filters;

  /// Parameters when focusing on an element or losing focus.
  @override
  final DivFocus? focus;

  /// User functions.
  @override
  final List<DivFunction>? functions;

  /// Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](https://divkit.tech/docs/en/concepts/layout).
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize(),)
  @override
  final DivSize height;

  /// It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
  // default value: false
  final Expression<bool> highPriorityPreviewShow;

  /// Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
  @override
  final String? id;

  /// Direct URL to an image.
  final Expression<Uri> imageUrl;

  /// Provides data on the actual size of the element.
  @override
  final DivLayoutProvider? layoutProvider;

  /// Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
  final List<DivAction>? longtapActions;

  /// External margins from the element stroke.
  @override
  final DivEdgeInsets margins;

  /// Internal margins from the element stroke.
  @override
  final DivEdgeInsets paddings;

  /// Placeholder background before the image is loaded.
  // default value: const Color(0x14000000)
  final Expression<Color> placeholderColor;

  /// Background image must be loaded before the display.
  // default value: false
  final Expression<bool> preloadRequired;

  /// Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
  final Expression<String>? preview;

  /// ID for the div object structure. Used to optimize block reuse. See [block reuse](https://divkit.tech/docs/en/concepts/reuse/reuse.md).
  @override
  final Expression<String>? reuseId;

  /// Merges cells in a string of the [grid](div-grid.md) element.
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;

  /// Image scaling:
  /// • `fit` places the entire image into the element (free space is filled with background);
  /// • `fill` scales the image to the element size and cuts off the excess.
  // default value: DivImageScale.fill
  final Expression<DivImageScale> scale;

  /// List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
  @override
  final List<DivAction>? selectedActions;

  /// New color of a contour image.
  final Expression<Color>? tintColor;

  /// Blend mode of the color specified in `tint_color`.
  // default value: DivBlendMode.sourceIn
  final Expression<DivBlendMode> tintMode;

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
        appearanceAnimation,
        aspect,
        background,
        border,
        columnSpan,
        contentAlignmentHorizontal,
        contentAlignmentVertical,
        disappearActions,
        doubletapActions,
        extensions,
        filters,
        focus,
        functions,
        height,
        highPriorityPreviewShow,
        id,
        imageUrl,
        layoutProvider,
        longtapActions,
        margins,
        paddings,
        placeholderColor,
        preloadRequired,
        preview,
        reuseId,
        rowSpan,
        scale,
        selectedActions,
        tintColor,
        tintMode,
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

  DivImage copyWith({
    DivAccessibility? accessibility,
    DivAction? Function()? action,
    DivAnimation? actionAnimation,
    List<DivAction>? Function()? actions,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivAnimator>? Function()? animators,
    DivFadeTransition? Function()? appearanceAnimation,
    DivAspect? Function()? aspect,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnSpan,
    Expression<DivAlignmentHorizontal>? contentAlignmentHorizontal,
    Expression<DivAlignmentVertical>? contentAlignmentVertical,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivAction>? Function()? doubletapActions,
    List<DivExtension>? Function()? extensions,
    List<DivFilter>? Function()? filters,
    DivFocus? Function()? focus,
    List<DivFunction>? Function()? functions,
    DivSize? height,
    Expression<bool>? highPriorityPreviewShow,
    String? Function()? id,
    Expression<Uri>? imageUrl,
    DivLayoutProvider? Function()? layoutProvider,
    List<DivAction>? Function()? longtapActions,
    DivEdgeInsets? margins,
    DivEdgeInsets? paddings,
    Expression<Color>? placeholderColor,
    Expression<bool>? preloadRequired,
    Expression<String>? Function()? preview,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Expression<DivImageScale>? scale,
    List<DivAction>? Function()? selectedActions,
    Expression<Color>? Function()? tintColor,
    Expression<DivBlendMode>? tintMode,
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
      DivImage(
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
        appearanceAnimation: appearanceAnimation != null
            ? appearanceAnimation.call()
            : this.appearanceAnimation,
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
        filters: filters != null ? filters.call() : this.filters,
        focus: focus != null ? focus.call() : this.focus,
        functions: functions != null ? functions.call() : this.functions,
        height: height ?? this.height,
        highPriorityPreviewShow:
            highPriorityPreviewShow ?? this.highPriorityPreviewShow,
        id: id != null ? id.call() : this.id,
        imageUrl: imageUrl ?? this.imageUrl,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        longtapActions: longtapActions != null
            ? longtapActions.call()
            : this.longtapActions,
        margins: margins ?? this.margins,
        paddings: paddings ?? this.paddings,
        placeholderColor: placeholderColor ?? this.placeholderColor,
        preloadRequired: preloadRequired ?? this.preloadRequired,
        preview: preview != null ? preview.call() : this.preview,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        scale: scale ?? this.scale,
        selectedActions: selectedActions != null
            ? selectedActions.call()
            : this.selectedActions,
        tintColor: tintColor != null ? tintColor.call() : this.tintColor,
        tintMode: tintMode ?? this.tintMode,
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

  static DivImage? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivImage(
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
        appearanceAnimation: safeParseObj(
          DivFadeTransition.fromJson(json['appearance_animation']),
        ),
        aspect: safeParseObj(
          DivAspect.fromJson(json['aspect']),
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
              DivFilter.fromJson(v),
            )!,
          ),
        ),
        focus: safeParseObj(
          DivFocus.fromJson(json['focus']),
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
        highPriorityPreviewShow: safeParseBoolExpr(
          json['high_priority_preview_show'],
          fallback: false,
        )!,
        id: safeParseStr(
          json['id']?.toString(),
        ),
        imageUrl: safeParseUriExpr(json['image_url'])!,
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
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
        reuseId: safeParseStrExpr(
          json['reuse_id']?.toString(),
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
            )!,
          ),
        ),
        tintColor: safeParseColorExpr(
          json['tint_color'],
        ),
        tintMode: safeParseStrEnumExpr(
          json['tint_mode'],
          parse: DivBlendMode.fromJson,
          fallback: DivBlendMode.sourceIn,
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

  @override
  DivImage resolve(DivVariableContext context) {
    accessibility.resolve(context);
    action?.resolve(context);
    actionAnimation.resolve(context);
    safeListResolve(actions, (v) => v.resolve(context));
    alignmentHorizontal?.resolve(context);
    alignmentVertical?.resolve(context);
    alpha.resolve(context);
    safeListResolve(animators, (v) => v.resolve(context));
    appearanceAnimation?.resolve(context);
    aspect?.resolve(context);
    safeListResolve(background, (v) => v.resolve(context));
    border.resolve(context);
    columnSpan?.resolve(context);
    contentAlignmentHorizontal.resolve(context);
    contentAlignmentVertical.resolve(context);
    safeListResolve(disappearActions, (v) => v.resolve(context));
    safeListResolve(doubletapActions, (v) => v.resolve(context));
    safeListResolve(extensions, (v) => v.resolve(context));
    safeListResolve(filters, (v) => v.resolve(context));
    focus?.resolve(context);
    safeListResolve(functions, (v) => v.resolve(context));
    height.resolve(context);
    highPriorityPreviewShow.resolve(context);
    imageUrl.resolve(context);
    layoutProvider?.resolve(context);
    safeListResolve(longtapActions, (v) => v.resolve(context));
    margins.resolve(context);
    paddings.resolve(context);
    placeholderColor.resolve(context);
    preloadRequired.resolve(context);
    preview?.resolve(context);
    reuseId?.resolve(context);
    rowSpan?.resolve(context);
    scale.resolve(context);
    safeListResolve(selectedActions, (v) => v.resolve(context));
    tintColor?.resolve(context);
    tintMode.resolve(context);
    safeListResolve(tooltips, (v) => v.resolve(context));
    transform.resolve(context);
    transitionChange?.resolve(context);
    transitionIn?.resolve(context);
    transitionOut?.resolve(context);
    safeListResolve(transitionTriggers, (v) => v.resolve(context));
    safeListResolve(variableTriggers, (v) => v.resolve(context));
    safeListResolve(variables, (v) => v.resolve(context));
    visibility.resolve(context);
    visibilityAction?.resolve(context);
    safeListResolve(visibilityActions, (v) => v.resolve(context));
    width.resolve(context);
    return this;
  }
}

// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div.dart';
import 'package:divkit/src/generated_sources/div_accessibility.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_alignment_horizontal.dart';
import 'package:divkit/src/generated_sources/div_alignment_vertical.dart';
import 'package:divkit/src/generated_sources/div_appearance_transition.dart';
import 'package:divkit/src/generated_sources/div_background.dart';
import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/generated_sources/div_border.dart';
import 'package:divkit/src/generated_sources/div_change_transition.dart';
import 'package:divkit/src/generated_sources/div_collection_item_builder.dart';
import 'package:divkit/src/generated_sources/div_disappear_action.dart';
import 'package:divkit/src/generated_sources/div_edge_insets.dart';
import 'package:divkit/src/generated_sources/div_extension.dart';
import 'package:divkit/src/generated_sources/div_focus.dart';
import 'package:divkit/src/generated_sources/div_layout_provider.dart';
import 'package:divkit/src/generated_sources/div_match_parent_size.dart';
import 'package:divkit/src/generated_sources/div_size.dart';
import 'package:divkit/src/generated_sources/div_tooltip.dart';
import 'package:divkit/src/generated_sources/div_transform.dart';
import 'package:divkit/src/generated_sources/div_transition_trigger.dart';
import 'package:divkit/src/generated_sources/div_variable.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/generated_sources/div_visibility_action.dart';
import 'package:divkit/src/generated_sources/div_wrap_content_size.dart';

class DivGallery with EquatableMixin implements DivBase {
  const DivGallery({
    this.accessibility = const DivAccessibility(),
    this.alignmentHorizontal,
    this.alignmentVertical,
    this.alpha = const ValueExpression(1.0),
    this.background,
    this.border = const DivBorder(),
    this.columnCount,
    this.columnSpan,
    this.crossContentAlignment =
        const ValueExpression(DivGalleryCrossContentAlignment.start),
    this.crossSpacing,
    this.defaultItem = const ValueExpression(0),
    this.disappearActions,
    this.extensions,
    this.focus,
    this.height = const DivSize.divWrapContentSize(DivWrapContentSize()),
    this.id,
    this.itemBuilder,
    this.itemSpacing = const ValueExpression(8),
    this.items,
    this.layoutProvider,
    this.margins = const DivEdgeInsets(),
    this.orientation = const ValueExpression(DivGalleryOrientation.horizontal),
    this.paddings = const DivEdgeInsets(),
    this.restrictParentScroll = const ValueExpression(false),
    this.reuseId,
    this.rowSpan,
    this.scrollMode = const ValueExpression(DivGalleryScrollMode.default_),
    this.scrollbar = const ValueExpression(DivGalleryScrollbar.none),
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

  static const type = "gallery";

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
  // constraint: number > 0
  final Expression<int>? columnCount;
  // constraint: number >= 0
  @override
  final Expression<int>? columnSpan;
  // default value: DivGalleryCrossContentAlignment.start
  final Expression<DivGalleryCrossContentAlignment> crossContentAlignment;
  // constraint: number >= 0
  final Expression<int>? crossSpacing;
  // constraint: number >= 0; default value: 0
  final Expression<int> defaultItem;

  @override
  final List<DivDisappearAction>? disappearActions;

  @override
  final List<DivExtension>? extensions;

  @override
  final DivFocus? focus;
  // default value: const DivSize.divWrapContentSize(DivWrapContentSize())
  @override
  final DivSize height;

  @override
  final String? id;

  final DivCollectionItemBuilder? itemBuilder;
  // constraint: number >= 0; default value: 8
  final Expression<int> itemSpacing;

  final List<Div>? items;

  @override
  final DivLayoutProvider? layoutProvider;

  @override
  final DivEdgeInsets margins;
  // default value: DivGalleryOrientation.horizontal
  final Expression<DivGalleryOrientation> orientation;

  @override
  final DivEdgeInsets paddings;
  // default value: false
  final Expression<bool> restrictParentScroll;

  @override
  final Expression<String>? reuseId;
  // constraint: number >= 0
  @override
  final Expression<int>? rowSpan;
  // default value: DivGalleryScrollMode.default_
  final Expression<DivGalleryScrollMode> scrollMode;
  // default value: DivGalleryScrollbar.none
  final Expression<DivGalleryScrollbar> scrollbar;

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
        alignmentHorizontal,
        alignmentVertical,
        alpha,
        background,
        border,
        columnCount,
        columnSpan,
        crossContentAlignment,
        crossSpacing,
        defaultItem,
        disappearActions,
        extensions,
        focus,
        height,
        id,
        itemBuilder,
        itemSpacing,
        items,
        layoutProvider,
        margins,
        orientation,
        paddings,
        restrictParentScroll,
        reuseId,
        rowSpan,
        scrollMode,
        scrollbar,
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

  DivGallery copyWith({
    DivAccessibility? accessibility,
    Expression<DivAlignmentHorizontal>? Function()? alignmentHorizontal,
    Expression<DivAlignmentVertical>? Function()? alignmentVertical,
    Expression<double>? alpha,
    List<DivBackground>? Function()? background,
    DivBorder? border,
    Expression<int>? Function()? columnCount,
    Expression<int>? Function()? columnSpan,
    Expression<DivGalleryCrossContentAlignment>? crossContentAlignment,
    Expression<int>? Function()? crossSpacing,
    Expression<int>? defaultItem,
    List<DivDisappearAction>? Function()? disappearActions,
    List<DivExtension>? Function()? extensions,
    DivFocus? Function()? focus,
    DivSize? height,
    String? Function()? id,
    DivCollectionItemBuilder? Function()? itemBuilder,
    Expression<int>? itemSpacing,
    List<Div>? Function()? items,
    DivLayoutProvider? Function()? layoutProvider,
    DivEdgeInsets? margins,
    Expression<DivGalleryOrientation>? orientation,
    DivEdgeInsets? paddings,
    Expression<bool>? restrictParentScroll,
    Expression<String>? Function()? reuseId,
    Expression<int>? Function()? rowSpan,
    Expression<DivGalleryScrollMode>? scrollMode,
    Expression<DivGalleryScrollbar>? scrollbar,
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
      DivGallery(
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
        columnCount:
            columnCount != null ? columnCount.call() : this.columnCount,
        columnSpan: columnSpan != null ? columnSpan.call() : this.columnSpan,
        crossContentAlignment:
            crossContentAlignment ?? this.crossContentAlignment,
        crossSpacing:
            crossSpacing != null ? crossSpacing.call() : this.crossSpacing,
        defaultItem: defaultItem ?? this.defaultItem,
        disappearActions: disappearActions != null
            ? disappearActions.call()
            : this.disappearActions,
        extensions: extensions != null ? extensions.call() : this.extensions,
        focus: focus != null ? focus.call() : this.focus,
        height: height ?? this.height,
        id: id != null ? id.call() : this.id,
        itemBuilder:
            itemBuilder != null ? itemBuilder.call() : this.itemBuilder,
        itemSpacing: itemSpacing ?? this.itemSpacing,
        items: items != null ? items.call() : this.items,
        layoutProvider: layoutProvider != null
            ? layoutProvider.call()
            : this.layoutProvider,
        margins: margins ?? this.margins,
        orientation: orientation ?? this.orientation,
        paddings: paddings ?? this.paddings,
        restrictParentScroll: restrictParentScroll ?? this.restrictParentScroll,
        reuseId: reuseId != null ? reuseId.call() : this.reuseId,
        rowSpan: rowSpan != null ? rowSpan.call() : this.rowSpan,
        scrollMode: scrollMode ?? this.scrollMode,
        scrollbar: scrollbar ?? this.scrollbar,
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

  static DivGallery? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivGallery(
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
        columnCount: safeParseIntExpr(
          json['column_count'],
        ),
        columnSpan: safeParseIntExpr(
          json['column_span'],
        ),
        crossContentAlignment: safeParseStrEnumExpr(
          json['cross_content_alignment'],
          parse: DivGalleryCrossContentAlignment.fromJson,
          fallback: DivGalleryCrossContentAlignment.start,
        )!,
        crossSpacing: safeParseIntExpr(
          json['cross_spacing'],
        ),
        defaultItem: safeParseIntExpr(
          json['default_item'],
          fallback: 0,
        )!,
        disappearActions: safeParseObj(
          safeListMap(
            json['disappear_actions'],
            (v) => safeParseObj(
              DivDisappearAction.fromJson(v),
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
        itemBuilder: safeParseObj(
          DivCollectionItemBuilder.fromJson(json['item_builder']),
        ),
        itemSpacing: safeParseIntExpr(
          json['item_spacing'],
          fallback: 8,
        )!,
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              Div.fromJson(v),
            )!,
          ),
        ),
        layoutProvider: safeParseObj(
          DivLayoutProvider.fromJson(json['layout_provider']),
        ),
        margins: safeParseObj(
          DivEdgeInsets.fromJson(json['margins']),
          fallback: const DivEdgeInsets(),
        )!,
        orientation: safeParseStrEnumExpr(
          json['orientation'],
          parse: DivGalleryOrientation.fromJson,
          fallback: DivGalleryOrientation.horizontal,
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
        scrollMode: safeParseStrEnumExpr(
          json['scroll_mode'],
          parse: DivGalleryScrollMode.fromJson,
          fallback: DivGalleryScrollMode.default_,
        )!,
        scrollbar: safeParseStrEnumExpr(
          json['scrollbar'],
          parse: DivGalleryScrollbar.fromJson,
          fallback: DivGalleryScrollbar.none,
        )!,
        selectedActions: safeParseObj(
          safeListMap(
            json['selected_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
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
          fallback: const DivSize.divMatchParentSize(DivMatchParentSize()),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}

enum DivGalleryCrossContentAlignment {
  start('start'),
  center('center'),
  end('end');

  final String value;

  const DivGalleryCrossContentAlignment(this.value);

  T map<T>({
    required T Function() start,
    required T Function() center,
    required T Function() end,
  }) {
    switch (this) {
      case DivGalleryCrossContentAlignment.start:
        return start();
      case DivGalleryCrossContentAlignment.center:
        return center();
      case DivGalleryCrossContentAlignment.end:
        return end();
    }
  }

  T maybeMap<T>({
    T Function()? start,
    T Function()? center,
    T Function()? end,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivGalleryCrossContentAlignment.start:
        return start?.call() ?? orElse();
      case DivGalleryCrossContentAlignment.center:
        return center?.call() ?? orElse();
      case DivGalleryCrossContentAlignment.end:
        return end?.call() ?? orElse();
    }
  }

  static DivGalleryCrossContentAlignment? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'start':
          return DivGalleryCrossContentAlignment.start;
        case 'center':
          return DivGalleryCrossContentAlignment.center;
        case 'end':
          return DivGalleryCrossContentAlignment.end;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

enum DivGalleryScrollMode {
  paging('paging'),
  default_('default');

  final String value;

  const DivGalleryScrollMode(this.value);

  T map<T>({
    required T Function() paging,
    required T Function() default_,
  }) {
    switch (this) {
      case DivGalleryScrollMode.paging:
        return paging();
      case DivGalleryScrollMode.default_:
        return default_();
    }
  }

  T maybeMap<T>({
    T Function()? paging,
    T Function()? default_,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivGalleryScrollMode.paging:
        return paging?.call() ?? orElse();
      case DivGalleryScrollMode.default_:
        return default_?.call() ?? orElse();
    }
  }

  static DivGalleryScrollMode? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'paging':
          return DivGalleryScrollMode.paging;
        case 'default':
          return DivGalleryScrollMode.default_;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

enum DivGalleryOrientation {
  horizontal('horizontal'),
  vertical('vertical');

  final String value;

  const DivGalleryOrientation(this.value);

  T map<T>({
    required T Function() horizontal,
    required T Function() vertical,
  }) {
    switch (this) {
      case DivGalleryOrientation.horizontal:
        return horizontal();
      case DivGalleryOrientation.vertical:
        return vertical();
    }
  }

  T maybeMap<T>({
    T Function()? horizontal,
    T Function()? vertical,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivGalleryOrientation.horizontal:
        return horizontal?.call() ?? orElse();
      case DivGalleryOrientation.vertical:
        return vertical?.call() ?? orElse();
    }
  }

  static DivGalleryOrientation? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'horizontal':
          return DivGalleryOrientation.horizontal;
        case 'vertical':
          return DivGalleryOrientation.vertical;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

enum DivGalleryScrollbar {
  none('none'),
  auto('auto');

  final String value;

  const DivGalleryScrollbar(this.value);

  T map<T>({
    required T Function() none,
    required T Function() auto,
  }) {
    switch (this) {
      case DivGalleryScrollbar.none:
        return none();
      case DivGalleryScrollbar.auto:
        return auto();
    }
  }

  T maybeMap<T>({
    T Function()? none,
    T Function()? auto,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivGalleryScrollbar.none:
        return none?.call() ?? orElse();
      case DivGalleryScrollbar.auto:
        return auto?.call() ?? orElse();
    }
  }

  static DivGalleryScrollbar? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivGalleryScrollbar.none;
        case 'auto':
          return DivGalleryScrollbar.auto;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

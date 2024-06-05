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
import 'div_collection_item_builder.dart';
import 'div_disappear_action.dart';
import 'div_edge_insets.dart';
import 'div_extension.dart';
import 'div_focus.dart';
import 'div_match_parent_size.dart';
import 'div_size.dart';
import 'div_tooltip.dart';
import 'div_transform.dart';
import 'div_transition_trigger.dart';
import 'div_variable.dart';
import 'div_visibility.dart';
import 'div_visibility_action.dart';
import 'div_wrap_content_size.dart';

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
    this.height = const DivSize(DivWrapContentSize()),
    this.id,
    this.itemBuilder,
    this.itemSpacing = const ValueExpression(8),
    this.items,
    this.margins = const DivEdgeInsets(),
    this.orientation = const ValueExpression(DivGalleryOrientation.horizontal),
    this.paddings = const DivEdgeInsets(),
    this.restrictParentScroll = const ValueExpression(false),
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
    this.width = const DivSize(DivMatchParentSize()),
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
  // default value: const DivSize(DivWrapContentSize())
  @override
  final DivSize height;

  @override
  final String? id;

  final DivCollectionItemBuilder? itemBuilder;
  // constraint: number >= 0; default value: 8
  final Expression<int> itemSpacing;

  final List<Div>? items;

  @override
  final DivEdgeInsets margins;
  // default value: DivGalleryOrientation.horizontal
  final Expression<DivGalleryOrientation> orientation;

  @override
  final DivEdgeInsets paddings;
  // default value: false
  final Expression<bool> restrictParentScroll;
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
  // default value: const DivSize(DivMatchParentSize())
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
        margins,
        orientation,
        paddings,
        restrictParentScroll,
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

  static DivGallery? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
        (json['disappear_actions'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivDisappearAction.fromJson(v),
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
      itemBuilder: safeParseObj(
        DivCollectionItemBuilder.fromJson(json['item_builder']),
      ),
      itemSpacing: safeParseIntExpr(
        json['item_spacing'],
        fallback: 8,
      )!,
      items: safeParseObj(
        (json['items'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                Div.fromJson(v),
              )!,
            )
            .toList(),
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
      variables: safeParseObj(
        (json['variables'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivVariable.fromJson(v),
              )!,
            )
            .toList(),
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
    switch (json) {
      case 'start':
        return DivGalleryCrossContentAlignment.start;
      case 'center':
        return DivGalleryCrossContentAlignment.center;
      case 'end':
        return DivGalleryCrossContentAlignment.end;
    }
    return null;
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
    switch (json) {
      case 'paging':
        return DivGalleryScrollMode.paging;
      case 'default':
        return DivGalleryScrollMode.default_;
    }
    return null;
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
    switch (json) {
      case 'horizontal':
        return DivGalleryOrientation.horizontal;
      case 'vertical':
        return DivGalleryOrientation.vertical;
    }
    return null;
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
    switch (json) {
      case 'none':
        return DivGalleryScrollbar.none;
      case 'auto':
        return DivGalleryScrollbar.auto;
    }
    return null;
  }
}

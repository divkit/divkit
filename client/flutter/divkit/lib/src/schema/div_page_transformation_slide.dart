// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Pages move without overlapping during pager scrolling.
class DivPageTransformationSlide extends Preloadable with EquatableMixin {
  const DivPageTransformationSlide({
    this.interpolator =
        const ValueExpression(DivAnimationInterpolator.easeInOut),
    this.nextPageAlpha = const ValueExpression(1.0),
    this.nextPageScale = const ValueExpression(1.0),
    this.previousPageAlpha = const ValueExpression(1.0),
    this.previousPageScale = const ValueExpression(1.0),
  });

  static const type = "slide";

  /// Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:
  /// • `linear` — cubic-bezier(0, 0, 1, 1)
  /// • `ease` — cubic-bezier(0.25, 0.1, 0.25, 1)
  /// • `ease_in` — cubic-bezier(0.42, 0, 1, 1)
  /// • `ease_out` — cubic-bezier(0, 0, 0.58, 1)
  /// • `ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)
  // default value: DivAnimationInterpolator.easeInOut
  final Expression<DivAnimationInterpolator> interpolator;

  /// Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  final Expression<double> nextPageAlpha;

  /// Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
  // constraint: number >= 0.0; default value: 1.0
  final Expression<double> nextPageScale;

  /// Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  final Expression<double> previousPageAlpha;

  /// Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
  // constraint: number >= 0.0; default value: 1.0
  final Expression<double> previousPageScale;

  @override
  List<Object?> get props => [
        interpolator,
        nextPageAlpha,
        nextPageScale,
        previousPageAlpha,
        previousPageScale,
      ];

  DivPageTransformationSlide copyWith({
    Expression<DivAnimationInterpolator>? interpolator,
    Expression<double>? nextPageAlpha,
    Expression<double>? nextPageScale,
    Expression<double>? previousPageAlpha,
    Expression<double>? previousPageScale,
  }) =>
      DivPageTransformationSlide(
        interpolator: interpolator ?? this.interpolator,
        nextPageAlpha: nextPageAlpha ?? this.nextPageAlpha,
        nextPageScale: nextPageScale ?? this.nextPageScale,
        previousPageAlpha: previousPageAlpha ?? this.previousPageAlpha,
        previousPageScale: previousPageScale ?? this.previousPageScale,
      );

  static DivPageTransformationSlide? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPageTransformationSlide(
        interpolator: safeParseStrEnumExpr(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
          fallback: DivAnimationInterpolator.easeInOut,
        )!,
        nextPageAlpha: safeParseDoubleExpr(
          json['next_page_alpha'],
          fallback: 1.0,
        )!,
        nextPageScale: safeParseDoubleExpr(
          json['next_page_scale'],
          fallback: 1.0,
        )!,
        previousPageAlpha: safeParseDoubleExpr(
          json['previous_page_alpha'],
          fallback: 1.0,
        )!,
        previousPageScale: safeParseDoubleExpr(
          json['previous_page_scale'],
          fallback: 1.0,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivPageTransformationSlide?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivPageTransformationSlide(
        interpolator: (await safeParseStrEnumExprAsync(
          json['interpolator'],
          parse: DivAnimationInterpolator.fromJson,
          fallback: DivAnimationInterpolator.easeInOut,
        ))!,
        nextPageAlpha: (await safeParseDoubleExprAsync(
          json['next_page_alpha'],
          fallback: 1.0,
        ))!,
        nextPageScale: (await safeParseDoubleExprAsync(
          json['next_page_scale'],
          fallback: 1.0,
        ))!,
        previousPageAlpha: (await safeParseDoubleExprAsync(
          json['previous_page_alpha'],
          fallback: 1.0,
        ))!,
        previousPageScale: (await safeParseDoubleExprAsync(
          json['previous_page_scale'],
          fallback: 1.0,
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
      await interpolator.preload(context);
      await nextPageAlpha.preload(context);
      await nextPageScale.preload(context);
      await previousPageAlpha.preload(context);
      await previousPageScale.preload(context);
    } catch (e) {
      return;
    }
  }
}

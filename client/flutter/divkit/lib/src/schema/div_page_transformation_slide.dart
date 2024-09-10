// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animation_interpolator.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// The pages are moving when the pager is flipping without overlaping each other.
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

  /// Tranformation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:
  /// • `linear` — cubic-bezier(0, 0, 1, 1);
  /// • `ease` — cubic-bezier(0.25, 0.1, 0.25, 1);
  /// • `ease_in` — cubic-bezier(0.42, 0, 1, 1);
  /// • `ease_out` — cubic-bezier(0, 0, 0.58, 1);
  /// • `ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).
  // default value: DivAnimationInterpolator.easeInOut
  final Expression<DivAnimationInterpolator> interpolator;

  /// Minimum alpha of the next page during pager scrolling in bounds [0, 1]. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  final Expression<double> nextPageAlpha;

  /// Scale of the next page during pager scrolling. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
  // constraint: number >= 0.0; default value: 1.0
  final Expression<double> nextPageScale;

  /// Minimum alpha of the previous page during pager scrolling in bounds [0, 1]. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  final Expression<double> previousPageAlpha;

  /// Scale of the previous page during pager scrolling. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
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

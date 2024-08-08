// Generated code. Do not modify.

import 'package:divkit/src/schema/div_alignment_horizontal.dart';
import 'package:divkit/src/schema/div_alignment_vertical.dart';
import 'package:divkit/src/schema/div_filter.dart';
import 'package:divkit/src/schema/div_image_scale.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivImageBackground extends Preloadable with EquatableMixin {
  const DivImageBackground({
    this.alpha = const ValueExpression(1.0),
    this.contentAlignmentHorizontal =
        const ValueExpression(DivAlignmentHorizontal.center),
    this.contentAlignmentVertical =
        const ValueExpression(DivAlignmentVertical.center),
    this.filters,
    required this.imageUrl,
    this.preloadRequired = const ValueExpression(false),
    this.scale = const ValueExpression(DivImageScale.fill),
  });

  static const type = "image";
  // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  final Expression<double> alpha;
  // default value: DivAlignmentHorizontal.center
  final Expression<DivAlignmentHorizontal> contentAlignmentHorizontal;
  // default value: DivAlignmentVertical.center
  final Expression<DivAlignmentVertical> contentAlignmentVertical;

  final List<DivFilter>? filters;

  final Expression<Uri> imageUrl;
  // default value: false
  final Expression<bool> preloadRequired;
  // default value: DivImageScale.fill
  final Expression<DivImageScale> scale;

  @override
  List<Object?> get props => [
        alpha,
        contentAlignmentHorizontal,
        contentAlignmentVertical,
        filters,
        imageUrl,
        preloadRequired,
        scale,
      ];

  DivImageBackground copyWith({
    Expression<double>? alpha,
    Expression<DivAlignmentHorizontal>? contentAlignmentHorizontal,
    Expression<DivAlignmentVertical>? contentAlignmentVertical,
    List<DivFilter>? Function()? filters,
    Expression<Uri>? imageUrl,
    Expression<bool>? preloadRequired,
    Expression<DivImageScale>? scale,
  }) =>
      DivImageBackground(
        alpha: alpha ?? this.alpha,
        contentAlignmentHorizontal:
            contentAlignmentHorizontal ?? this.contentAlignmentHorizontal,
        contentAlignmentVertical:
            contentAlignmentVertical ?? this.contentAlignmentVertical,
        filters: filters != null ? filters.call() : this.filters,
        imageUrl: imageUrl ?? this.imageUrl,
        preloadRequired: preloadRequired ?? this.preloadRequired,
        scale: scale ?? this.scale,
      );

  static DivImageBackground? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivImageBackground(
        alpha: safeParseDoubleExpr(
          json['alpha'],
          fallback: 1.0,
        )!,
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
        filters: safeParseObj(
          safeListMap(
            json['filters'],
            (v) => safeParseObj(
              DivFilter.fromJson(v),
            )!,
          ),
        ),
        imageUrl: safeParseUriExpr(json['image_url'])!,
        preloadRequired: safeParseBoolExpr(
          json['preload_required'],
          fallback: false,
        )!,
        scale: safeParseStrEnumExpr(
          json['scale'],
          parse: DivImageScale.fromJson,
          fallback: DivImageScale.fill,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivImageBackground?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivImageBackground(
        alpha: (await safeParseDoubleExprAsync(
          json['alpha'],
          fallback: 1.0,
        ))!,
        contentAlignmentHorizontal: (await safeParseStrEnumExprAsync(
          json['content_alignment_horizontal'],
          parse: DivAlignmentHorizontal.fromJson,
          fallback: DivAlignmentHorizontal.center,
        ))!,
        contentAlignmentVertical: (await safeParseStrEnumExprAsync(
          json['content_alignment_vertical'],
          parse: DivAlignmentVertical.fromJson,
          fallback: DivAlignmentVertical.center,
        ))!,
        filters: await safeParseObjAsync(
          await safeListMapAsync(
            json['filters'],
            (v) => safeParseObj(
              DivFilter.fromJson(v),
            )!,
          ),
        ),
        imageUrl: (await safeParseUriExprAsync(json['image_url']))!,
        preloadRequired: (await safeParseBoolExprAsync(
          json['preload_required'],
          fallback: false,
        ))!,
        scale: (await safeParseStrEnumExprAsync(
          json['scale'],
          parse: DivImageScale.fromJson,
          fallback: DivImageScale.fill,
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
      await alpha.preload(context);
      await contentAlignmentHorizontal.preload(context);
      await contentAlignmentVertical.preload(context);
      await safeFuturesWait(filters, (v) => v.preload(context));
      await imageUrl.preload(context);
      await preloadRequired.preload(context);
      await scale.preload(context);
    } catch (e) {
      return;
    }
  }
}

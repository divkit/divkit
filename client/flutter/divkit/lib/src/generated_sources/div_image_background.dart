// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_alignment_horizontal.dart';
import 'div_alignment_vertical.dart';
import 'div_filter.dart';
import 'div_image_scale.dart';

class DivImageBackground with EquatableMixin {
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

  static DivImageBackground? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
        (json['filters'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivFilter.fromJson(v),
              )!,
            )
            .toList(),
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
  }
}

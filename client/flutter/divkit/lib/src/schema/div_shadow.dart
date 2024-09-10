// Generated code. Do not modify.

import 'package:divkit/src/schema/div_point.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Element shadow.
class DivShadow extends Preloadable with EquatableMixin {
  const DivShadow({
    this.alpha = const ValueExpression(0.19),
    this.blur = const ValueExpression(2),
    this.color = const ValueExpression(Color(0xFF000000)),
    required this.offset,
  });

  /// Shadow transparency.
  // constraint: number >= 0.0 && number <= 1.0; default value: 0.19
  final Expression<double> alpha;

  /// Blur intensity.
  // constraint: number >= 0; default value: 2
  final Expression<int> blur;

  /// Shadow color.
  // default value: const Color(0xFF000000)
  final Expression<Color> color;

  /// Shadow offset.
  final DivPoint offset;

  @override
  List<Object?> get props => [
        alpha,
        blur,
        color,
        offset,
      ];

  DivShadow copyWith({
    Expression<double>? alpha,
    Expression<int>? blur,
    Expression<Color>? color,
    DivPoint? offset,
  }) =>
      DivShadow(
        alpha: alpha ?? this.alpha,
        blur: blur ?? this.blur,
        color: color ?? this.color,
        offset: offset ?? this.offset,
      );

  static DivShadow? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivShadow(
        alpha: safeParseDoubleExpr(
          json['alpha'],
          fallback: 0.19,
        )!,
        blur: safeParseIntExpr(
          json['blur'],
          fallback: 2,
        )!,
        color: safeParseColorExpr(
          json['color'],
          fallback: const Color(0xFF000000),
        )!,
        offset: safeParseObj(
          DivPoint.fromJson(json['offset']),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivShadow?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivShadow(
        alpha: (await safeParseDoubleExprAsync(
          json['alpha'],
          fallback: 0.19,
        ))!,
        blur: (await safeParseIntExprAsync(
          json['blur'],
          fallback: 2,
        ))!,
        color: (await safeParseColorExprAsync(
          json['color'],
          fallback: const Color(0xFF000000),
        ))!,
        offset: (await safeParseObjAsync(
          DivPoint.fromJson(json['offset']),
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
      await blur.preload(context);
      await color.preload(context);
      await offset.preload(context);
    } catch (e) {
      return;
    }
  }
}

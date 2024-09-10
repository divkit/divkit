// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
class DivAspect extends Preloadable with EquatableMixin {
  const DivAspect({
    required this.ratio,
  });

  /// `height = width / ratio`.
  // constraint: number > 0
  final Expression<double> ratio;

  @override
  List<Object?> get props => [
        ratio,
      ];

  DivAspect copyWith({
    Expression<double>? ratio,
  }) =>
      DivAspect(
        ratio: ratio ?? this.ratio,
      );

  static DivAspect? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivAspect(
        ratio: safeParseDoubleExpr(
          json['ratio'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivAspect?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivAspect(
        ratio: (await safeParseDoubleExprAsync(
          json['ratio'],
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
      await ratio.preload(context);
    } catch (e) {
      return;
    }
  }
}

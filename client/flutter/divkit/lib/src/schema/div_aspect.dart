// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
class DivAspect with EquatableMixin {
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
        ratio: reqVProp<double>(
          safeParseDoubleExpr(
            json['ratio'],
          ),
          name: 'ratio',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
class DivAspect extends Resolvable with EquatableMixin {
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

  @override
  DivAspect resolve(DivVariableContext context) {
    ratio.resolve(context);
    return this;
  }
}

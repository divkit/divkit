// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivAspect with EquatableMixin {
  const DivAspect({
    required this.ratio,
  });

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

  static DivAspect? fromJson(Map<String, dynamic>? json) {
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
}

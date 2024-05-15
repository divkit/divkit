// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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

  static DivAspect? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivAspect(
      ratio: safeParseDoubleExpr(
        json['ratio'],
      )!,
    );
  }
}

// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivMatchParentSize with EquatableMixin {
  const DivMatchParentSize({
    this.weight,
  });

  static const type = "match_parent";
  // constraint: number > 0
  final Expression<double>? weight;

  @override
  List<Object?> get props => [
        weight,
      ];

  DivMatchParentSize copyWith({
    Expression<double>? Function()? weight,
  }) =>
      DivMatchParentSize(
        weight: weight != null ? weight.call() : this.weight,
      );

  static DivMatchParentSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivMatchParentSize(
      weight: safeParseDoubleExpr(
        json['weight'],
      ),
    );
  }
}

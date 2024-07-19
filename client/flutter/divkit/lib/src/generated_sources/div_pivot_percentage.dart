// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivPivotPercentage with EquatableMixin {
  const DivPivotPercentage({
    required this.value,
  });

  static const type = "pivot-percentage";

  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivPivotPercentage copyWith({
    Expression<double>? value,
  }) =>
      DivPivotPercentage(
        value: value ?? this.value,
      );

  static DivPivotPercentage? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivPivotPercentage(
        value: safeParseDoubleExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}

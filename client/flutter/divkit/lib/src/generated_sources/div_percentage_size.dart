// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivPercentageSize with EquatableMixin {
  const DivPercentageSize({
    required this.value,
  });

  static const type = "percentage";
  // constraint: number > 0
  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivPercentageSize copyWith({
    Expression<double>? value,
  }) =>
      DivPercentageSize(
        value: value ?? this.value,
      );

  static DivPercentageSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivPercentageSize(
        value: safeParseDoubleExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}

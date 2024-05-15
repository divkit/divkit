// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class NumberValue with EquatableMixin {
  const NumberValue({
    required this.value,
  });

  static const type = "number";

  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static NumberValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return NumberValue(
      value: safeParseDoubleExpr(
        json['value'],
      )!,
    );
  }
}

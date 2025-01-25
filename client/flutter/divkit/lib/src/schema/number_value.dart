// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

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

  NumberValue copyWith({
    Expression<double>? value,
  }) =>
      NumberValue(
        value: value ?? this.value,
      );

  static NumberValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return NumberValue(
        value: reqVProp<double>(
          safeParseDoubleExpr(
            json['value'],
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

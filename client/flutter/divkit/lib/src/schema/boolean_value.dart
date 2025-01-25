// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class BooleanValue with EquatableMixin {
  const BooleanValue({
    required this.value,
  });

  static const type = "boolean";
  final Expression<bool> value;

  @override
  List<Object?> get props => [
        value,
      ];

  BooleanValue copyWith({
    Expression<bool>? value,
  }) =>
      BooleanValue(
        value: value ?? this.value,
      );

  static BooleanValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return BooleanValue(
        value: reqVProp<bool>(
          safeParseBoolExpr(
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

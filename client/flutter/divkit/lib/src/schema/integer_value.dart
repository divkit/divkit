// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class IntegerValue extends Resolvable with EquatableMixin {
  const IntegerValue({
    required this.value,
  });

  static const type = "integer";
  final Expression<int> value;

  @override
  List<Object?> get props => [
        value,
      ];

  IntegerValue copyWith({
    Expression<int>? value,
  }) =>
      IntegerValue(
        value: value ?? this.value,
      );

  static IntegerValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return IntegerValue(
        value: reqVProp<int>(
          safeParseIntExpr(
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

  @override
  IntegerValue resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}

// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
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
        value: safeParseIntExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  IntegerValue resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}

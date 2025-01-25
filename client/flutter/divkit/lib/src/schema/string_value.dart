// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class StringValue extends Resolvable with EquatableMixin {
  const StringValue({
    required this.value,
  });

  static const type = "string";
  final Expression<String> value;

  @override
  List<Object?> get props => [
        value,
      ];

  StringValue copyWith({
    Expression<String>? value,
  }) =>
      StringValue(
        value: value ?? this.value,
      );

  static StringValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return StringValue(
        value: safeParseStrExpr(
          json['value']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  StringValue resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
